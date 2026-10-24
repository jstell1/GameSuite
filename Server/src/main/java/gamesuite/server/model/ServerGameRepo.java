package gamesuite.server.model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.utils.PluginLoader;
import gamesuite.core.model.PromoteRequester;

@Service
public class ServerGameRepo implements PromoteRequester {

    //<gameId, gm> actual list of running games stored by gameId
    private final Map<String, GameManager> games = new ConcurrentHashMap<>();
    private final Object gamesLock = new Object();
    //<userSessions, gameId> maps given userSession to the associated game GameManager instance
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    private PluginLoader loader;

    // <gameId, name> tells which game corresponds to the gameId, mostly for multi-game plugins
    private final Map<String, String> gamePluginMap = new ConcurrentHashMap<>();

    //<gameName, List<gameId>>
    private final Map<String, List<String>> joinable = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(ServerGameRepo.class);

    private PromoteRequester requester;
    
    public ServerGameRepo() {
      
    
        this.loader = new PluginLoader("../plugins/");
    
      //  try {
            this.loader.loadAll();
       // } catch (IOException e) {
        //    throw new RuntimeException("issue loading plugins");
      //  }
        this.loader.loadAllRules();
        //this.loader.watchForChanges();

    }

    public void setPromoteRequester(PromoteRequester requester) {
        this.requester = requester;
    }

    public void closeGame(String gameId) {
        this.games.remove(gameId);
        String name = this.gamePluginMap.get(gameId);
        List<String> games = this.joinable.get(name);
        games.remove(gameId);
        this.gamePluginMap.remove(gameId);
        log.info("numGames={} numSessions={} numPluginsMapped={} numJoinable in {}={}", this.games.size(), this.userSessions.size(), this.gamePluginMap.size(), name, this.joinable.get(name).size());
    }

    public Object getGamesLock() { return this.gamesLock; } 
    
    public String createGame(String game, String group, String p1, String sessionId) throws 
                                                                                        InstantiationException, IllegalAccessException, 
                                                                                        InvocationTargetException, NoSuchMethodException, 
                                                                                        IOException, IllegalArgumentException
    {
        
        GameManagerFactory gmFact = this.loader.createGameManager(group);
        gmFact.setRulesLoader(this.loader);
        GameManager gm;
        boolean multi = this.loader.isMultiGame(group); 

        
        if(multi) {
            
            gm = gmFact.createGame(p1, sessionId, this.loader.getGameDef(game));
        } else {
            gm = gmFact.createGame(p1, sessionId, null);
        }
        gm.setPromoteRequester(this);
        String gameId = UUID.randomUUID().toString();
        gm.setGameId(gameId);
        this.games.put(gameId, gm);

        
        synchronized(gm) {
            this.userSessions.put(sessionId, gameId);

            String joinName;

            if(multi) {
                joinName = game;
            } else {
                joinName = group;
            }
            
            this.gamePluginMap.put(gameId, joinName);

            if(!this.joinable.containsKey(joinName)) {
                List<String> idList = new ArrayList<>();
                this.joinable.put(joinName, idList);
            }
            this.joinable.get(joinName).add(gameId);
        
            log.info("numGames={} numSessions={} numPluginsMapped={} numJoinable in {}={}", this.games.size(), this.userSessions.size(), this.gamePluginMap.size(), joinName, this.joinable.get(joinName).size());
            }
            return gameId;
    }

    public boolean hasUserSession(String id) {
        return this.userSessions.containsKey(id);
    }

    public String getUserSessionGame(String id) {
        return this.userSessions.get(id);
    }

    public List<String> getGameSessions(String gameId) {

        GameManager gm = this.games.get(gameId);
        List<String> ret = gm.getUserIdList();
        return ret;
    }

    public void addWebSocketToGame(String gameId, String sessionId) {
        GameManager gm = this.games.get(gameId);

        synchronized(gm) {
            this.userSessions.put(sessionId, gameId);
        }

        log.info("Added webSock to game: gameId={} sessionId={}", gameId, sessionId);
    }

    public Set<String> getGameUsers(String gameId) {
       
        GameManager gm = this.games.get(gameId);
        List<String> users = gm.getUserIdList(); 
       return new HashSet<>(users);
    }

    public int getNumGames() { return this.games.size(); }

    public JsonNode joinGame(String player, String playerId, String gameId) {
        
        GameManager gm = this.games.get(gameId);
        JsonNode node = null;
        synchronized(gm) {
            node = gm.joinGame(player, playerId);

            String gameName = this.gamePluginMap.get(gameId);
            
            this.joinable.get(gameName).remove(gameId);
            log.info("numGames={} numSessions={} numPluginsMapped={} numJoinable in {}={}", this.games.size(), this.userSessions.size(), this.gamePluginMap.size(), gameName, this.joinable.get(gameName).size());
        }
        return node;
    }

    public JsonNode getGameView(String gameId) {
        return this.games.get(gameId).getGameStateJson();
    }

    public boolean containsGame(String gameId) {
        return this.games.containsKey(gameId);
    }

    public boolean validGame(String group, String game) {

        if(this.loader.getGameDef(game) == null) {
            return false;
        }
        return true;

    }

    public GameManager getGM(String id) { 
        return this.games.get(id);
    }

    public void setGame(String gameId, GameManager gm) {
        this.games.put(gameId, gm);
    }

    public boolean rightPlayer(String gameId, String sessionId) throws Exception {
        if(!containsGame(gameId)) 
            return false;
        GameManager gm = this.games.get(gameId);
        synchronized(gm) {
            if(!gm.checkPlayerSession(sessionId)) {
                return false;
            }
            return true;
        }
    }

    

    public GameManager removePlayer(String sessionId) throws Exception {
         String gameId = null;
         GameManager gm = null;
             gameId = this.userSessions.get(sessionId);
             String name = this.gamePluginMap.get(gameId);
             gm = this.games.get(gameId);
             synchronized(gm) {
                   if(gm.getNumMappedPlayers() > 1) {
                    this.joinable.get(name).remove(gameId);
                   }
                    gm.quitGame(sessionId);
                    this.userSessions.remove(sessionId);

                 log.info("numGames={} numSessions={} numPluginsMapped={} numJoinable in {}={}", this.games.size(), this.userSessions.size(), this.gamePluginMap.size(), name, this.joinable.get(name).size());
            }
        
        return gm;
    }

    public String[] getJoinableGames(String game) {
        

        synchronized(this.joinable) {
            if(this.joinable.containsKey(game)) {
                return this.joinable.get(game).toArray(new String[0]);
            } else {
                return new String[0];
            }
        }
        
    }

    public Map<String, ArrayList<String>> getGamesList() {
        Map<String, ArrayList<String>> gamesList = this.loader.listAvailableGames();
        return gamesList;
    }

    @Override
    public void requestPromoteChoice(JsonNode choices) {
        this.requester.requestPromoteChoice(choices);
    }

    @Override
    public void sendUpdate(JsonNode update) throws IOException {
        this.requester.sendUpdate(update);
    }
}