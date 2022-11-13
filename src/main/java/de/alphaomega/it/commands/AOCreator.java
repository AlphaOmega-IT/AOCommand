package de.alphaomega.it.commands;

import de.alphaomega.it.AOCommand;
import de.alphaomega.it.cmdHandler.Command;
import de.alphaomega.it.cmdHandler.CommandArgs;
import de.alphaomega.it.cmdHandler.Completer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public record AOCreator(AOCommand aoCommand) {

    //initialize the command using the @Command annotation
    //if you leave a value open, it will take the default value from the interface
    //there are few variables that you can change: name, aliases, permission, inGameOnly, noPermsDE (German no perms message), noPermsEN (English no perms message)
    //the parameter of the command method has to be CommandArgs, it will give you information about the arguments, the player and the command itself.

    @Command(
            name = "aocreator",
            aliases = {"aocreator"},
            permission = "aocommand.*"
    )
    public void onCommand(final CommandArgs arg) {
        final Player p = arg.getPlayer();
        final String[] args = arg.getArgs();

        //do stuff
        if (args.length == 1 && args[0].equalsIgnoreCase("info"))
            p.sendMessage(MiniMessage.miniMessage().deserialize("API from: <rainbow>https://github.com/AlphaOmega-IT</rainbow>"));
    }

    //initialize the tabCompleter using the @Completer annotation
    //if you leave a value open, it will take the default value from the interface
    //there are few variables that you can change: name, aliases
    //the parameter of the command method has to be CommandArgs, it will give you information about the arguments, the player and the command itself.

    @Completer(
            name = "aocreator",
            aliases = {"aocreator"}
    )
    public List<String> onTabComplete(final CommandArgs arg) {
        final String[] args = arg.getArgs();
        if (args.length == 1)
            return new ArrayList<>(List.of("info"));
        return new ArrayList<>();
    }
}
