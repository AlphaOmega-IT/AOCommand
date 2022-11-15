package de.alphaomega.it;

import de.alphaomega.it.cmdhandlerapi.AOCFramework;
import de.alphaomega.it.commands.AOCreator;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class AOCommand {

    private final JavaPlugin pl;
    private final AOCFramework AOCFramework;

    public AOCommand(final JavaPlugin pl) {
        this.pl = pl;
        this.AOCFramework = new AOCFramework(pl);

        registerCommands();
    }

    private void registerCommands() {
        //How you register for example a command.
        this.AOCFramework.registerCommands(new AOCreator(this));
    }

    public void registerCommand(final Object commandClazz) {
        this.AOCFramework.registerCommands(commandClazz);
    }
}
