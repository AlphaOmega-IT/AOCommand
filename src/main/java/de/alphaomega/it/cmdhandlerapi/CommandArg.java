package de.alphaomega.it.cmdhandlerapi;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
public class CommandArg {
	
	private final CommandSender sender;
	private final Command command;
	private final String label;
	private final String[] args;
	
	public CommandArg(final CommandSender sender, final Command command, final String label, String[] args, final int subCommand) {
		String[] modArgs = new String[args.length - subCommand];
		
		if (args.length - subCommand >= 0) System.arraycopy(args, subCommand, modArgs, 0, args.length - subCommand);
		
		StringBuilder buffer = new StringBuilder();
		buffer.append(label);
		
		for (int x = 0; x < subCommand; ++ x) {
			buffer.append(".").append(args[x]);
		}
		
		String cmdLabel = buffer.toString();
		this.sender = sender;
		this.command = command;
		this.label = cmdLabel;
		this.args = modArgs;
	}
	
	public Player getPlayer() {
		if (sender instanceof Player) {
			return (Player) sender;
		}
		return null;
	}
	
}