package de.alphaomega.it.cmdhandlerapi;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BukkitCompleter implements TabCompleter {
	
	private final Map<String, Entry<Method, Object>> completer = new HashMap<>();
	
	public void addCompleter(String label, Method m, Object obj) {
		completer.put(label, new AbstractMap.SimpleEntry<>(m, obj));
	}
	
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		
		for (int i = args.length; i >= 0; -- i) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(label.toLowerCase());
			
			for (int x = 0; x < i; ++ x) {
				if (! args[x].equals("") && ! args[x].equals(" ")) {
					buffer.append(".").append(args[x].toLowerCase());
				}
			}
			
			String cmdLabel = buffer.toString();
			if (this.completer.containsKey(cmdLabel)) {
				Entry<Method, Object> entry = this.completer.get(cmdLabel);
				
				try {
					return (List<String>) (entry.getKey()).invoke(entry.getValue(), new CommandArgs(sender, command, label, args, cmdLabel.split("\\.").length - 1));
				} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException exc) {
					exc.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
}