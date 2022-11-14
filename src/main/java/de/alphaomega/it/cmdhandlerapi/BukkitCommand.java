package de.alphaomega.it.cmdhandlerapi;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BukkitCommand extends Command {

	private final Plugin owningPlugin;
	private final CommandExecutor executor;
	protected BukkitCompleter completer;

	protected BukkitCommand(String label, CommandExecutor executor, Plugin owner) {
		super(label);
		this.executor = executor;
		this.owningPlugin = owner;
		this.usageMessage = "";
	}

	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
		boolean success;
		if (!this.owningPlugin.isEnabled()) {
			return false;
		} else if (!this.testPermission(sender)) {
			return true;
		} else {
			try {
				success = this.executor.onCommand(sender, this, commandLabel, args);
			} catch (Throwable exc) {
				return false;
			}

			if (!success && this.usageMessage.length() > 0) {
				String[] lines = this.usageMessage.replace("<command>", commandLabel).split("\n");

				for (String line : lines) {
					sender.sendMessage(Component.text(line));
				}
			}

			return success;
		}
	}

	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws CommandException, IllegalArgumentException {
		List<String> completions = null;

		try {
			if (this.completer != null) {
				completions = this.completer.onTabComplete(sender, this, alias, args);
			}

			if (completions == null && this.executor instanceof TabCompleter) {
				completions = ((TabCompleter) this.executor).onTabComplete(sender, this, alias, args);
			}
		} catch (Throwable var11) {
			StringBuilder message = new StringBuilder();
			message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');

			for (String arg : args) {
				message.append(arg).append(' ');
			}

			message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.owningPlugin.getDescription().getFullName());
			throw new CommandException(message.toString(), var11);
		}

		return completions == null ? super.tabComplete(sender, alias, args) : completions;
	}
}