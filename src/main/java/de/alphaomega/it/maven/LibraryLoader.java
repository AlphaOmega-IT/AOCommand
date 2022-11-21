package de.alphaomega.it.maven;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import de.alphaomega.it.AOCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Objects;

public class LibraryLoader {

    private final JavaPlugin plugin;
    private final URLClassLoaderAccess URL_INJECTOR;

    public LibraryLoader(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.URL_INJECTOR = URLClassLoaderAccess.create((URLClassLoader) plugin.getClass().getClassLoader());
    }

    public void load(final Dependency dependency) {
        this.plugin.getLogger().info(String.format("Loading dependency %s:%s:%s from maven", dependency.groupId(), dependency.artifactId(), dependency.version()));
        String name = dependency.artifactId() + "-" + dependency.version();

        File saveLocation = new File(this.plugin.getDataFolder() + "/libs/", name + ".jar");
        if (!saveLocation.exists()) {
            try {
                this.plugin.getLogger().info("Dependency '" + name + "' is not already in the libraries folder. Attempting to download...");
                URL url = dependency.getUrl();

                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            this.plugin.getLogger().info("Dependency '" + name + "' successfully downloaded.");
        }

        if (!saveLocation.exists()) {
            throw new RuntimeException("Unable to download dependency: " + dependency);
        }

        try {
            URL_INJECTOR.addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation, e);
        }

        this.plugin.getLogger().info("Loaded dependency '" + name + "' successfully.");
    }

    public record Dependency(String groupId, String artifactId, String version) {
            public Dependency(String groupId, String artifactId, String version) {
                this.groupId = Objects.requireNonNull(groupId, "groupId");
                this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
                this.version = Objects.requireNonNull(version, "version");
            }

            public URL getUrl() throws MalformedURLException {
                String repo = "https://repo1.maven.org/maven2/";
                repo += "%s/%s/%s/%s-%s.jar";

                String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
                return new URL(url);
            }

            @Override
            public boolean equals(Object o) {
                if (o == this) return true;
                if (!(o instanceof final Dependency other)) return false;
                return this.groupId().equals(other.groupId()) &&
                        this.artifactId().equals(other.artifactId()) &&
                        this.version().equals(other.version());
            }
        }
}
