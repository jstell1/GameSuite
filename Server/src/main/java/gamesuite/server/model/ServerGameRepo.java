package gamesuite.server.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.control.PluginLoader;

@Service
public class ServerGameRepo {
    private final Map<String, GameManager> games = new ConcurrentHashMap<>();
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> gameUserMap = new ConcurrentHashMap<>();
    private PluginLoader loader;
    private final Map<String, Map<String, Integer>> activeList = new ConcurrentHashMap<>();

    public ServerGameRepo() {
        try {
       
            this.loader = new PluginLoader("../plugins/");
        
            this.loader.loadAll();
            this.loader.watchForChanges();

            Set<String> list = this.loader.listAvailableGames();
            for(String name : list) {
                Map<String, Integer> games = new ConcurrentHashMap<>();
                this.activeList.put(name, games);   
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    public String createGame(String game, String p1, String sessionId) {
        
        GameManagerFactory gmFact = loader.createGameManager(game);
        GameManager gm = gmFact.createGame(p1);
        
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        synchronized(gm) {
            Map<String, Integer> users = new HashMap<>();
            users.put(sessionId, 1);
            this.gameUserMap.put(gameId, users);
            this.userSessions.put(sessionId, gameId);
            synchronized(this.activeList) {
                this.activeList.get(gm.getName()).put(gameId, 1);
            }
            System.out.println("numGames: " + this.games.size());
            System.out.println("numSessions: " + this.userSessions.size());
            System.out.println("PlayerNumMap: " + this.gameUserMap.get(gameId).size());
        }
        return gameId;
    }

    public boolean hasUserSession(String id) {
        return this.userSessions.containsKey(id);
    }

    public String getUserSessionGame(String id) {
        return this.userSessions.get(id);
    }

    public Map<String, Integer> getGameUserMap(String gameId) {
        return this.gameUserMap.get(gameId);
    }

    public void addWebSocketToGame(String gameId, String sessionId) {
        GameManager gm = games.get(gameId);

        synchronized(gm) {
            if(!this.gameUserMap.containsKey(gameId)) {
                Map<String, Integer> sessionList = new HashMap<>();
                sessionList.put(sessionId, 1);
                this.gameUserMap.put(gameId, sessionList);
                this.userSessions.put(sessionId, gameId);
            } else {
                this.gameUserMap.get(gameId).put(sessionId, 2);
                this.userSessions.put(sessionId, gameId);
            }
        }

        System.out.println("numGames: " + this.games.size());
        System.out.println("numSessions: " + this.userSessions.size());
        System.out.println("PlayerNumMap: " + this.gameUserMap.get(gameId).size());
    }

    public Set<String> getGameUsers(String gameId) {
        return this.gameUserMap.get(gameId).keySet();
    }

    public int getNumGames() { return this.games.size(); }

    //public void setUserNum(String sessionId, int num) {
        //this.userPlayerNumMap.put(sessionId, num);
    //}

    public JsonNode joinGame(String player, String gameId) {
        
        GameManager gm = this.games.get(gameId);
        JsonNode node = null;
        synchronized(gm) {
            node = gm.joinGame(player);
            synchronized(this.activeList) {
                this.activeList.get(gm.getName()).put(gameId, 2);
            }
            //GameState game = gm.getGameState();
            // Player p = game.getPlayer(2);
            // if(p == null) {

            //     boolean added = gm.addPlayer(player);
            //     if(added) {
            //         gm.initBoard();
            //         return gm.getBoard();
            //     }
            // }
        }
        return node;
    }

    public JsonNode getGameView(String gameId) {
        return this.games.get(gameId).getGameStateJson();
    }

    public boolean containsGame(String gameId) {
        return this.games.containsKey(gameId);
    }

    public GameManager getGM(String id) { 
        return this.games.get(id);
    }

    public void setGame(String gameId, GameManager gm) {
        this.games.put(gameId, gm);
    }

    public boolean rightPlayer(String gameId, String sessionId) {
        if(!containsGame(gameId) || !this.gameUserMap.get(gameId).containsKey(sessionId))
            return false;
        GameManager gm = this.games.get(gameId);
        synchronized(gm) {

            Map<String, Integer> sessionList = this.gameUserMap.get(gameId);
    
            if(sessionList == null || !sessionList.containsKey(sessionId))
                return false;
    
            int turn = this.games.get(gameId).getTurn();
            if(this.gameUserMap.get(gameId).get(sessionId).intValue() != turn)
                return false;
            return true;
        }
    }

    

    public GameManager removePlayer(String sessionId) {
        String gameId = null;
        GameManager gm = null;
        //GameState game = null;

        try {
            gameId = this.userSessions.get(sessionId);
            gm = this.games.get(gameId);
            synchronized(gm) {
                if(gm.getWinner() == null && gm.getNumPlayers() > 1) {
                    
                    Map<String, Integer> playerNums = this.gameUserMap.get(gameId);
                    int playerNum = playerNums.get(sessionId).intValue();
                    gm.quitGame(playerNum);
                    this.userSessions.remove(sessionId);
                    this.gameUserMap.get(gameId).remove(sessionId);
                    
                } else {
                    this.gameUserMap.remove(gameId);
                    this.games.remove(gameId);
                    this.userSessions.remove(sessionId);
                }
                synchronized(this.activeList) {
                    this.activeList.get(gm.getName()).remove(gameId);
                }
                System.out.println("numGames: " + this.games.size());
                System.out.println("numSessions: " + this.userSessions.size());
                System.out.println("PlayerNumMap: " + this.gameUserMap.size());
            }
        } catch (Exception e) {}
        
        return gm;
    }

    public String[] getActiveGames(String game) {
        
        synchronized(this.activeList) {
            Map<String, Integer> l = this.activeList.get(game);
            List<String> list = new ArrayList<>();
            for(String gameId : l.keySet()) {
                if(l.get(gameId) < 2) {
                    list.add(gameId);
                }
            }

            //String[] list = l.keySet().toArray(new String[0]);
            return list.toArray(new String[list.size()]);
        }
    }

    public List<String> getGamesList() {
        String[] gamesList = this.loader.listAvailableGames().toArray(new String[0]);
        List<String> list = Arrays.asList(gamesList);
        return list;
    }
}