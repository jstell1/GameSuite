package gamesuite.core.utils;

import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;
import gamesuite.core.ui.GameBoardFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(PluginLoader.class);

    //this is for multi-game game engine jars that can have multiple game defs and rulePacks
    private final Map<String, ArrayList<String>> games = new HashMap<>();
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

    public boolean loadGameBoards() throws IOException {
        boolean check = true;
        if(this.uiPluginDir == null) {
           throw new IOException();
        }


        this.gameBoards = new ConcurrentHashMap<>();
        this.gameBoardClassLoaders = new ConcurrentHashMap<>();
        this.uiJars = uiPluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        for(File jar : this.uiJars) {
                loadGameUIs(jar);
        }
        return check;
    }
    

    private void loadGameUIs(File jarFile) {
        URL jarUrl = null;
        String gameName = jarFile.getName();
        try {
            jarUrl = jarFile.toURI().toURL();


            gameName = jarFile.getName().replaceFirst("\\.jar$", "");

            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, resolveParent(jarFile));//this.getClass().getClassLoader());

            ServiceLoader<GameBoardFactory> serviceLoader = ServiceLoader.load(GameBoardFactory.class, classLoader);
            Iterator<GameBoardFactory> iterator = serviceLoader.iterator();

            if (!iterator.hasNext()) {
               // System.err.println("No GameBoardUI found in " + jarFile.getName());
                log.warn("Missing GameBoardUI in jar: gameName={}", gameName);
                return;
            }

            GameBoardFactory temp = iterator.next();
            Class<? extends GameBoardFactory> clazz = (Class<? extends GameBoardFactory>) temp.getClass();

            this.gameBoards.put(gameName, clazz);
            this.gameBoardClassLoaders.put(gameName, classLoader);

            log.debug("Registered game plugin: gameName={}",gameName);
        } catch (IOException e) {
            log.error("Failed to load ui plugin: fileName={}", gameName, e);
        }

    }

    public void loadAll() {
        this.jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (this.jars == null) {
            throw new IllegalStateException("this should not be null");
        }
        for (File jar : this.jars) {
            loadPlugin(jar);
        }
    }

     private void loadPlugin(File jarFile) {

         String gameName = jarFile.getName();

         gameName = jarFile.getName().replaceFirst("\\.jar$", "");
         try{
             URL jarUrl = jarFile.toURI().toURL();


             URLClassLoader classLoader =
                     new URLClassLoader(new URL[]{jarUrl}, this.getClass().getClassLoader());

             ServiceLoader<GameManagerFactory> serviceLoader =
                     ServiceLoader.load(GameManagerFactory.class, classLoader);
             Iterator<GameManagerFactory> iterator = serviceLoader.iterator();

             if (!iterator.hasNext()) {
                 log.warn("No GameManager found: jarFile={}", jarFile.getName());
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
                     ObjectMapper mapper = new ObjectMapper();
                     JsonNode schema = mapper.readTree(file);
                     if(JsonSchemaValidator.isValidGameSchema(schema)) {
                         this.gameDefs.put(game, schema);
                         tmp.add(game);
                     } else {
                         log.warn("Schema failed validation for multi-game: game={} gameGroup={}", game, gameName);
                     }
                 }
             }
             this.games.put(gameName, tmp);

            log.debug("registered plugin: gameName={}", gameName);
         } catch(IOException e) {
             log.error("Failed to laod game plugin: gameName={}", gameName, e);
         }

    }

    public Map<String, Constraint> loadConstraints(String packName) throws IOException {

        File jarFile = new File(this.pluginDir + "/rules/" + packName);
        URL jarUrl = jarFile.toURI().toURL();
        String pack = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = 
            new URLClassLoader(new URL[]{jarUrl}, resolveParent(jarFile));

        ServiceLoader<Constraint> serviceLoader = 
            ServiceLoader.load( Constraint.class, classLoader);
        Iterator<Constraint> iterator = serviceLoader.iterator();

        Map<String, Constraint> ruleMap = new HashMap<>();

        while (iterator.hasNext()) {
            Constraint temp = iterator.next();
            String name = temp.getName();
            ruleMap.put(name, temp);
        }

        return ruleMap;
    }

    public Map<String, Effect> loadEffects(String packName) throws IOException {

        File jarFile = new File(this.pluginDir + "/rules/" + packName);
        URL jarUrl = jarFile.toURI().toURL();
        String pack = jarFile.getName().replaceFirst("\\.jar$", "");

        URLClassLoader classLoader = 
            new URLClassLoader(new URL[]{jarUrl}, resolveParent(jarFile));//this.classLoaders.get("BoardGameEngine"));

        ServiceLoader<Effect> serviceLoader = 
            ServiceLoader.load( Effect.class, classLoader);
        Iterator<Effect> iterator = serviceLoader.iterator();

        Map<String, Effect> ruleMap = new HashMap<>();

        while (iterator.hasNext()) {
            Effect temp = iterator.next();
            String name = temp.getName();
            ruleMap.put(name, temp);
        }

        return ruleMap;
    }

    public void loadAllRules() {
        File path = new File(this.pluginDir + "/rules"); 
        this.rulePacks = path.listFiles((dir, name) -> name.endsWith(".jar"));

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
    }

    public GameBoardFactory createBoardFactory(String gameName) throws InstantiationException, IllegalAccessException,
                                                                         InvocationTargetException, NoSuchMethodException, 
                                                                         SecurityException, IllegalArgumentException
    {
        log.debug("In createBoardFactory func: gameName={}", gameName);

        
        Class<? extends GameBoardFactory> clazz = this.gameBoards.get(gameName);
        if (clazz == null) {
          throw new IllegalArgumentException("Game not found: " + gameName);
        } 
  
            return clazz.getDeclaredConstructor().newInstance();
 
    }

    public GameManagerFactory createGameManager(String gameName) throws NoSuchMethodException, SecurityException,
                                                                        InstantiationException, InvocationTargetException,
                                                                        IllegalAccessException, IllegalArgumentException
{
        Class<? extends GameManagerFactory> clazz = gameClasses.get(gameName);
        if (clazz == null) throw new IllegalArgumentException("Game not found: " + gameName);
   
            return clazz.getDeclaredConstructor().newInstance();
     
    }

    public boolean isMultiGame(String game) {
         return !this.games.get(game).isEmpty();
    }

    public JsonNode getGameDef(String game) {
        JsonNode g = this.gameDefs.get(game);
        return g;
    }

    public Constraint getGameConstraint(String name) {
        JsonNode gameDef = this.gameDefs.get(name);
        
        return null;
    }

    public Effect getGameEffect(String name) {
        
        

        return null;
    }

    private ClassLoader resolveParent(File jarFile) throws IOException {
        try (JarFile jf = new java.util.jar.JarFile(jarFile)) {
            Manifest mf = jf.getManifest();
            String dep = (mf == null) ? null : mf.getMainAttributes().getValue("Plugin-Dependency");
            if (dep != null && !dep.isBlank()) {
                URLClassLoader parent = this.classLoaders.get(dep.trim());
                if (parent == null) {
                    throw new IllegalStateException(jarFile.getName() + " requires plugin '" + dep
                            + "' but it isn't loaded");
                }
                return parent;
            }
        }
        return this.getClass().getClassLoader();
    }
}

