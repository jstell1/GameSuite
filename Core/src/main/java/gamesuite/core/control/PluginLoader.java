package gamesuite.core.control;

import gamesuite.core.control.GameManager;
import gamesuite.core.ui.GameBoardFactory;

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
    private File uiPluginDir;
    private final Map<String, Class<? extends GameManagerFactory>> gameClasses = new ConcurrentHashMap<>();
    private final Map<String, URLClassLoader> classLoaders = new ConcurrentHashMap<>();
    private Map<String, Class<? extends GameBoardFactory>> gameBoards;
    private Map<String, URLClassLoader> gameBoardClassLoaders;
    File[] jars;
    File[] uiJars;

    public PluginLoader(String pluginDirPath) {
        this.pluginDir = new File(pluginDirPath);
        if (!this.pluginDir.exists()) this.pluginDir.mkdirs();
    }

    public void setUIPluginLoader(String uiPluginDirPath) {
        this.uiPluginDir = new File(uiPluginDirPath);
        if(!this.uiPluginDir.exists()) this.uiPluginDir.mkdir();
    }

    public boolean loadGameBoards() {
        boolean check = true;
        if(this.uiPluginDir == null) {
            check = false;
            System.out.println("PROBLEM!!!!");
            return check;
        }


        this.gameBoards = new ConcurrentHashMap<>();
        this.gameBoardClassLoaders = new ConcurrentHashMap<>();
        //String p = this.path + "/ui";
        this.uiJars = uiPluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        for(File jar : this.uiJars) {
            try {
                loadGameUIs(jar);
            } catch (Exception e) { check = false; }
        }
        return check;
    }

    private void loadGameUIs(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        String gameName = jarFile.getName();

        gameName = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

        ServiceLoader<GameBoardFactory> serviceLoader = ServiceLoader.load(GameBoardFactory.class, classLoader);
        Iterator<GameBoardFactory> iterator = serviceLoader.iterator();

        if (!iterator.hasNext()) {
            System.err.println("No GameBoardUI found in " + jarFile.getName());
            return;
        }

        GameBoardFactory temp = iterator.next();
        Class<? extends GameBoardFactory> clazz = (Class<? extends GameBoardFactory>) temp.getClass();

        this.gameBoards.put(gameName, clazz);
        this.gameBoardClassLoaders.put(gameName, classLoader);

        System.out.println("Registered game plugin: " + gameName);
    }

    public void loadAll() throws Exception {
        this.jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (this.jars == null) return;
        for (File jar : this.jars) {


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

        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

        ServiceLoader<GameManagerFactory> serviceLoader = ServiceLoader.load(GameManagerFactory.class, classLoader);
        Iterator<GameManagerFactory> iterator = serviceLoader.iterator();

        if (!iterator.hasNext()) {
            System.err.println("No GameManager found in " + jarFile.getName());
            return;
        }

        GameManagerFactory temp = iterator.next();
        Class<? extends GameManagerFactory> clazz = (Class<? extends GameManagerFactory>) temp.getClass();

        gameClasses.put(gameName, clazz);
        classLoaders.put(gameName, classLoader);

        System.out.println("Registered game plugin: " + gameName);
    }

    public Set<String> listAvailableGames() {
        return Collections.unmodifiableSet(gameClasses.keySet());
    }

    public GameBoardFactory createBoardFactory(String gameName) {
        Class<? extends GameBoardFactory> clazz = this.gameBoards.get(gameName);
        if (clazz == null) throw new IllegalArgumentException("Game not found: " + gameName);
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate " + gameName, e);
        }
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

