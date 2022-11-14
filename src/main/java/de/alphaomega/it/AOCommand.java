package de.alphaomega.it;

import de.alphaomega.it.cmdHandler.CommandFramework;
import de.alphaomega.it.commands.AOCreator;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class AOCommand {

    private final JavaPlugin pl;
    private final CommandFramework commandFramework;

    public AOCommand(final JavaPlugin pl) {
        this.pl = pl;
        this.commandFramework = new CommandFramework(pl);

        registerCommands();
    }

    private void registerCommands() {
        //How you register for example a command.
        this.commandFramework.registerCommands(new AOCreator(this));
    }

    public void registerCommand(final Object commandClazz) {
        this.commandFramework.registerCommands(commandClazz);
    }
}
