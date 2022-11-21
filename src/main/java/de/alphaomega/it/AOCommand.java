package de.alphaomega.it;

import de.alphaomega.it.cmdhandlerapi.AOCFramework;
import de.alphaomega.it.commands.AOCreator;
import de.alphaomega.it.maven.LibraryLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class AOCommand {

    private final JavaPlugin plugin;
    private final AOCFramework AOCFramework;
    private final LibraryLoader loader;

    public AOCommand(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.loader = new LibraryLoader(plugin);
        this.AOCFramework = new AOCFramework(plugin);

        registerCommands();
    }

    private void registerCommands() {
        //How you register for example a command.
        this.AOCFramework.registerCommands(new AOCreator());
    }

    public void registerCommand(final Object commandClazz) {
        this.AOCFramework.registerCommands(commandClazz);
    }
}
