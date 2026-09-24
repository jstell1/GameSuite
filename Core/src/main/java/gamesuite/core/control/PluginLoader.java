package gamesuite.core.control;

//import gamesuite.boardgame.model.Rules.Constraint;
//import gamesuite.boardgame.model.Rules.Rules.Effect;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PluginLoader {
    private final File pluginDir;
    private File uiPluginDir;
    private final Map<String, Class<? extends GameManagerFactory>> gameClasses = new ConcurrentHashMap<>();
    private final Map<String, URLClassLoader> classLoaders = new ConcurrentHashMap<>();
    private Map<String, Class<? extends GameBoardFactory>> gameBoards;
    private Map<String, URLClassLoader> gameBoardClassLoaders;
    File[] jars;
    File[] uiJars;
    File[] rulePacks;

    //this is for multi-game game engine jars that can have multiple game defs and rulePacks
    private final Map<String, ArrayList<String>> games = new HashMap<>();
    //private final ArrayList<String> games = new ArrayList<>(); 
    private final String gameDefsPath = "gameDefinitions";
    private final String rulesPath = "rules";
    private final Map<String, JsonNode> gameDefs = new HashMap<>();

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

     private void loadPlugin(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        String gameName = jarFile.getName();

        gameName = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = 
            new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

        ServiceLoader<GameManagerFactory> serviceLoader = 
            ServiceLoader.load(GameManagerFactory.class, classLoader);
        Iterator<GameManagerFactory> iterator = serviceLoader.iterator();

        if (!iterator.hasNext()) {
            System.err.println("No GameManager found in " + jarFile.getName());
            return;
        }

        GameManagerFactory temp = iterator.next();
        Class<? extends GameManagerFactory> clazz = (Class<? extends GameManagerFactory>) temp.getClass();

        gameClasses.put(gameName, clazz);
        classLoaders.put(gameName, classLoader);
        ArrayList<String> tmp = null;

        if(temp.isMultiGame()) {  
            File gamesDirect = new File(this.pluginDir + "/" + gameDefsPath + "/" + gameName);
            File[] gameDefs = gamesDirect.listFiles((dir, name) -> name.endsWith(".json"));

            tmp = new ArrayList<>();
            for(File file : gameDefs) {
                String game = file.getName().replaceFirst("\\.json$", "");
                tmp.add(game);
                ObjectMapper mapper = new ObjectMapper();
                this.gameDefs.put(game, mapper.readTree(file));
            }
        } //else {
           // this.games.add(gameName);
           this.games.put(gameName, tmp);
        //}

        System.out.println("Registered game plugin: " + gameName);
    }

    public Map<String, Constraint> loadConstraints(String packName) throws Exception {

        File jarFile = new File(this.pluginDir + "/rules/" + packName);
        URL jarUrl = jarFile.toURI().toURL();
        String pack = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = 
            new URLClassLoader(new URL[]{jarUrl}, this.classLoaders.get("BoardGameEngine"));//this.getClass().getClassLoader());

        ServiceLoader<Constraint> serviceLoader = 
            ServiceLoader.load( Constraint.class, classLoader);
        Iterator<Constraint> iterator = serviceLoader.iterator();

        Map<String, Constraint> ruleMap = new HashMap<>();

        while (iterator.hasNext()) {
            Constraint temp = iterator.next();
            //Class<? extends Constraint> clazz = (Class<? extends Constraint>) temp.getClass();
            String name = temp.getName();
            ruleMap.put(name, temp);
        }

        return ruleMap;
    }

    public Map<String, Effect> loadEffects(String packName) throws Exception {

        File jarFile = new File(this.pluginDir + "/rules/" + packName);
        URL jarUrl = jarFile.toURI().toURL();
        String pack = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = 
            new URLClassLoader(new URL[]{jarUrl}, this.classLoaders.get("BoardGameEngine"));//this.getClass().getClassLoader());

        ServiceLoader<Effect> serviceLoader = 
            ServiceLoader.load( Effect.class, classLoader);
        Iterator<Effect> iterator = serviceLoader.iterator();

        Map<String, Effect> ruleMap = new HashMap<>();

        while (iterator.hasNext()) {
            Effect temp = iterator.next();
            //Class<? extends Constraint> clazz = (Class<? extends Constraint>) temp.getClass();
            String name = temp.getName();
            ruleMap.put(name, temp);
        }

        return ruleMap;
    }

    public void loadAllRules() throws Exception {
        File path = new File(this.pluginDir + "/rules"); 
        this.rulePacks = path.listFiles((dir, name) -> name.endsWith(".jar"));

        //if(this.rulePacks == null) return;

        // for(File pack : this.rulePacks) {

        // }
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

   

    public Map<String, ArrayList<String>> listAvailableGames() {
        return this.games;
        //return Collections.unmodifiableSet(gameClasses.keySet());
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

    public boolean isMultiGame(String game) {
         return !this.games.get(game).isEmpty();
    }

    public JsonNode getGameDef(String game) {
        return this.gameDefs.get(game);
    }

    public Constraint getGameConstraint(String name) {
        //Map<String, Constraint> constraints = new HashMap<>();
        JsonNode gameDef = this.gameDefs.get(name);
        
        return null;
    }

    public Effect getGameEffect(String name) {
        
        

        return null;
    }
}

