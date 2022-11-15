package de.alphaomega.it.cmdhandlerapi;

import lombok.NonNull;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;


public class AOCFramework implements CommandExecutor {

    private final Map<String, Entry<Method, Object>> commandMap = new HashMap<>();
    private final JavaPlugin pl;
    private CommandMap map;

    public AOCFramework(final JavaPlugin pl) {
        if (pl.getServer().getPluginManager() instanceof SimplePluginManager manager) {
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                this.map = (CommandMap) field.get(manager);
            } catch (SecurityException | IllegalAccessException | NoSuchFieldException | IllegalArgumentException exc) {
                exc.printStackTrace();
            }
        }
        this.pl = pl;
        registerHelp(pl);
    }

    public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command cmd, final @NonNull String label, final @NonNull String[] args) {
        return this.handleCommand(sender, cmd, label, args);
    }

    public boolean handleCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        for (int i = args.length; i >= 0; --i) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());

            for (int x = 0; x < i; ++x) {
                buffer.append(".").append(args[x].toLowerCase());
            }

            String cmdLabel = buffer.toString();
            if (this.commandMap.containsKey(cmdLabel)) {
                Method method = (Method) ((Entry<?, ?>) this.commandMap.get(cmdLabel)).getKey();
                Object methodObject = this.commandMap.get(cmdLabel).getValue();
                ICommand command = method.getAnnotation(ICommand.class);
                if (sender instanceof Player p) {
                    if (!command.permission().equals("") && !sender.hasPermission(command.permission()) && !sender.isOp()) {
                        if (p.locale().toString().equals("de_DE")) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(command.noPermsDE()));
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(command.noPermsEN()));
                        }
                        return true;
                    }
                    try {
                        method.invoke(methodObject, new CommandArg(sender, cmd, label, args, cmdLabel.split("\\.").length - 1));
                    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException exc) {
                        exc.printStackTrace();
                    }
                    return true;
                } else if (sender instanceof ConsoleCommandSender console) {
                    if (command.inGameOnly()) {
                        console.sendMessage("This command is only performable in game");
                        return true;
                    }
                    try {
                        method.invoke(methodObject, new CommandArg(console, cmd, label, args, cmdLabel.split("\\.").length - 1));
                    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException exc) {
                        exc.printStackTrace();
                    }
                    return true;
                }
            }
        }

        this.defaultCommand(new CommandArg(sender, cmd, label, args, 0));
        return true;
    }

    public void registerCommands(final @NotNull Object obj) {
        Method[] methods = obj.getClass().getMethods();

        for (Method m : methods) {
            String[] strings;
            int length;
            String alias;
            if (m.getAnnotation(ICommand.class) != null) {
                ICommand command = m.getAnnotation(ICommand.class);
                if (m.getParameterTypes().length <= 1 && m.getParameterTypes()[0] == CommandArg.class) {
                    this.registerCommand(command, command.name(), m, obj);
                    strings = command.aliases();
                    length = strings.length;

                    for (int i = 0; i < length; ++i) {
                        alias = strings[i];
                        this.registerCommand(command, alias, m, obj);
                    }
                } else {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                }
            } else if (m.getAnnotation(Completer.class) != null) {
                Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == CommandArg.class) {
                    if (m.getReturnType() != List.class) {
                        System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    } else {
                        this.registerCompleter(comp.name(), m, obj);
                        strings = comp.aliases();
                        length = strings.length;

                        for (int i = 0; i < length; ++i) {
                            alias = strings[i];
                            this.registerCompleter(alias, m, obj);
                        }
                    }
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                }
            }
        }

    }

    public void registerHelp(final JavaPlugin pl) {
        Set<HelpTopic> help = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());

        for (String s : this.commandMap.keySet()) {
            if (!s.contains(".")) {
                Command cmd = this.map.getCommand(s);
                if (cmd != null) {
                    HelpTopic topic = new GenericCommandHelpTopic(cmd);
                    help.add(topic);
                }
            }
        }

        IndexHelpTopic topic = new IndexHelpTopic(pl.getName(), "All commands for " + pl.getName(), null, help, "Below is a list of all " + pl.getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

    public void registerCommand(final @NotNull ICommand command, final @NotNull String label, final @NotNull Method m, final @NotNull Object obj) {
        this.commandMap.put(label.toLowerCase(), new SimpleEntry<>(m, obj));
        this.commandMap.put(pl.getName() + ':' + label.toLowerCase(), new SimpleEntry<>(m, obj));
        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (this.map.getCommand(cmdLabel) == null) {
            Command cmd = new BCommand(cmdLabel, this, pl);
            this.map.register(pl.getName(), cmd);
        }

        if (!command.description().equalsIgnoreCase("") && cmdLabel.equals(label)) {
            this.map.getCommand(cmdLabel).setDescription(command.description());
        }

        if (!command.usage().equalsIgnoreCase("") && cmdLabel.equals(label)) {
            this.map.getCommand(cmdLabel).setUsage(command.usage());
        }

    }

    public void registerCompleter(final @NotNull String label, final @NotNull Method m, final @NotNull Object obj) {
        String cmdLabel = label.split("\\.")[0].toLowerCase();
        BCommand bCommand;
        if (this.map.getCommand(cmdLabel) == null) {
            bCommand = new BCommand(cmdLabel, this, pl);
            this.map.register(pl.getName(), bCommand);
        }

        if (this.map.getCommand(cmdLabel) instanceof BCommand) {
            bCommand = (BCommand) this.map.getCommand(cmdLabel);
            if (bCommand.completer == null) {
                bCommand.completer = new BCompleter();
            }

            bCommand.completer.addCompleter(label, m, obj);
        } else if (this.map.getCommand(cmdLabel) instanceof PluginCommand) {
            try {
                Object command = this.map.getCommand(cmdLabel);
                Field field = command.getClass().getDeclaredField("completer");
                field.setAccessible(true);
                BCompleter completer;
                if (field.get(command) == null) {
                    completer = new BCompleter();
                    completer.addCompleter(label, m, obj);
                    field.set(command, completer);
                } else if (field.get(command) instanceof BCompleter) {
                    completer = (BCompleter) field.get(command);
                    completer.addCompleter(label, m, obj);
                } else {
                    System.out.println("Unable to register tab completer " + m.getName() + ". A tab completer is already registered for that command!");
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

    }

    private void defaultCommand(final @NotNull CommandArg args) {
        args.getSender().sendMessage(args.getLabel() + " is not handled! Oh noes!");
    }

}