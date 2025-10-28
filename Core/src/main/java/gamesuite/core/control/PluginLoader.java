package gamesuite.core.control;

import gamesuite.core.control.GameManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginLoader {
    private final File pluginDir;
    private final Map<String, Class<? extends GameManagerFactory>> gameClasses = new ConcurrentHashMap<>();
    private final Map<String, URLClassLoader> classLoaders = new ConcurrentHashMap<>();

    public PluginLoader(String pluginDirPath) {
        this.pluginDir = new File(pluginDirPath);
        if (!pluginDir.exists()) pluginDir.mkdirs();
    }

    public void loadAll() throws Exception {
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) return;
        for (File jar : jars) {


            loadPlugin(jar);
        }
    }

    public void watchForChanges() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = pluginDir.toPath();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path filename = (Path) event.context();
                        if (filename.toString().endsWith(".jar")) {
                            File jarFile = new File(pluginDir, filename.toString());
                            try {
                                loadPlugin(jarFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    key.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "PluginWatcher").start();
    }

    private void loadPlugin(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        String gameName = jarFile.getName();

        gameName = jarFile.getName().replaceFirst("\\.jar$", "");

         //gameName.replaceFirst(".jar", "");
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

        // Option 1: Use ServiceLoader
        ServiceLoader<GameManagerFactory> serviceLoader = ServiceLoader.load(GameManagerFactory.class, classLoader);
        Iterator<GameManagerFactory> iterator = serviceLoader.iterator();

        if (!iterator.hasNext()) {
            System.err.println("No GameManager found in " + jarFile.getName());
            return;
        }

        GameManagerFactory temp = iterator.next();
        Class<? extends GameManagerFactory> clazz = (Class<? extends GameManagerFactory>) temp.getClass();

        //String gameName = temp.getGameName();
        gameClasses.put(gameName, clazz);
        classLoaders.put(gameName, classLoader);

        System.out.println("Registered game plugin: " + gameName);
    }

    public Set<String> listAvailableGames() {
        return Collections.unmodifiableSet(gameClasses.keySet());
    }

    public GameManagerFactory createGameManager(String gameName) {
        Class<? extends GameManagerFactory> clazz = gameClasses.get(gameName);
        if (clazz == null) throw new IllegalArgumentException("Game not found: " + gameName);
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + gameName, e);
        }
    }
}

