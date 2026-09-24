package gamesuite.server.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.control.PluginLoader;

@Service
public class ServerGameRepo {

    //<gameId, gm> actual list of running games stored by gameId
    private final Map<String, GameManager> games = new ConcurrentHashMap<>();

    //<userSessions, gameId> maps given userSession to the associated game GameManager instance
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    //private final Map<String, Map<String, Integer>> gameUserMap = new ConcurrentHashMap<>();

    //may be a redundant field. stores map of gameId to a list of userId's associated with it
    //private final Map<String, List<String>> gameUserMap = new ConcurrentHashMap<>();
    private PluginLoader loader;

    //not really sure what this is
    //private final Map<String, Map<String, Integer>> activeList = new ConcurrentHashMap<>();

    // <gameId, name> tells which game corresponds to the gameId, mostly for multi-game plugins
    private final Map<String, String> gamePluginMap = new ConcurrentHashMap<>();

    //<gameName, List<gameId>>
    private final Map<String, List<String>> joinable = new ConcurrentHashMap<>();
    public ServerGameRepo() {
        try {
       
            this.loader = new PluginLoader("../plugins/");
        
            this.loader.loadAll();
            this.loader.loadAllRules();
            this.loader.watchForChanges();

            /* 
            Map<String, ArrayList<String>> list = this.loader.listAvailableGames();
            
            for(String name : list.keySet()) {
                Map<String, Integer> games = new ConcurrentHashMap<>();
                ArrayList<String> tmp = list.get(name);
                if(tmp.size() > 0) {
                    for(String n : tmp) {
                        games = new ConcurrentHashMap<>();
                        this.activeList.put(n, games);
                    }
                } else {
                    this.activeList.put(name, games);   
                }
            }
                */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    public String createGame(String game, String group, String p1, String sessionId) {
        
        try {

            GameManagerFactory gmFact = loader.createGameManager(group);
            gmFact.setRulesLoader(loader);
            GameManager gm;
            boolean multi = loader.isMultiGame(group); 
    
            
            if(multi) {
                gm = gmFact.createGame(p1, sessionId, loader.getGameDef(game));
            } else {
                gm = gmFact.createGame(p1, sessionId, null);
            }
            
            String gameId = UUID.randomUUID().toString();
            this.games.put(gameId, gm);
    
            
            synchronized(gm) {
                // Map<String, Integer> users = new HashMap<>();
                //users.put(sessionId, 1);
                //this.gameUserMap.put(gameId, users);
                //List<String> l = new ArrayList<>();
                //l.add(sessionId);
                //this.gameUserMap.put(gameId, l);
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
            
                
               
               // synchronized(this.activeList) {
    
                //    if(multi) {
               //         this.activeList.get(game).put(gameId, 1);
                //    } else {
                //        this.activeList.get(gm.getName()).put(gameId, 1);
    
                //    }
              //  }
                System.out.println("numGames: " + this.games.size());
                System.out.println("numSessions: " + this.userSessions.size());
                //System.out.println("PlayerNumMap: " + this.gameUserMap.get(gameId).size());
            }
            return gameId;
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasUserSession(String id) {
        return this.userSessions.containsKey(id);
    }

    public String getUserSessionGame(String id) {
        return this.userSessions.get(id);
    }

    //public List<String> getGameUserMap(String gameId) {
   //     return this.gameUserMap.get(gameId);
   // }

    public List<String> getGameSessions(String gameId) {

        GameManager gm = this.games.get(gameId);
        List<String> ret = gm.getUserIdList();
        return ret;
    }

    public void addWebSocketToGame(String gameId, String sessionId) {
        GameManager gm = this.games.get(gameId);

        synchronized(gm) {
            //if(!this.gameUserMap.containsKey(gameId)) {
               // Map<String, Integer> sessionList = new HashMap<>();
                //sessionList.put(sessionId, 1);
                //this.gameUserMap.put(gameId, sessionList);
                //this.userSessions.put(sessionId, gameId);

             //   List<String> sessionList = new ArrayList<>();
            //    sessionList.add(sessionId);
          //      this.gameUserMap.put(gameId, sessionList);
          //  } else {
                //this.gameUserMap.get(gameId).put(sessionId, 2);
          //      this.gameUserMap.get(gameId).add(sessionId);
                 ///this.userSessions.put(sessionId, gameId);
        //    }
            this.userSessions.put(sessionId, gameId);
        }

        System.out.println("numGames: " + this.games.size());
        System.out.println("numSessions: " + this.userSessions.size());
      //  System.out.println("PlayerNumMap: " + this.gameUserMap.get(gameId).size());
    }

    public Set<String> getGameUsers(String gameId) {
       
        GameManager gm = this.games.get(gameId);
        List<String> users = gm.getUserIdList(); 
       return new HashSet<>(users);//this.gameUserMap.get(gameId));
    }

    public int getNumGames() { return this.games.size(); }

    //public void setUserNum(String sessionId, int num) {
        //this.userPlayerNumMap.put(sessionId, num);
    //}

    public JsonNode joinGame(String player, String playerId, String gameId) {
        
        GameManager gm = this.games.get(gameId);
        JsonNode node = null;
        synchronized(gm) {
            node = gm.joinGame(player, playerId);

            String gameName = this.gamePluginMap.get(gameId);
    
            this.joinable.get(gameName).remove(gameId);
            /* 
            synchronized(this.activeList) {

                if(!this.loader.isMultiGame(gm.getName())) {
                    this.activeList.get(gm.getName()).put(gameId, 2);
                } else {

                    this.activeList.get("Checkers").put(gameId, 2);
                   // String name = this.loader.get
                }
            }
                */

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
        if(!containsGame(gameId)) //|| !this.gameUserMap.get(gameId).containsKey(sessionId))
            return false;
        GameManager gm = this.games.get(gameId);
        synchronized(gm) {
            if(!gm.checkPlayerSession(sessionId)) {
                return false;
            }
            //Map<String, Integer> sessionList = this.gameUserMap.get(gameId);
    
            //if(sessionList == null || !sessionList.containsKey(sessionId))
            //    return false;
    

            //this is a game responsibility!!!!! Shouldn't be here!!
           // int turn = this.games.get(gameId).getTurn();
            //if(this.gameUserMap.get(gameId).get(sessionId).intValue() != turn)
            //    return false;
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
                    
                    //Map<String, Integer> playerNums = this.gameUserMap.get(gameId);
                   // int playerNum = playerNums.get(sessionId).intValue();
                   
                    gm.quitGame(sessionId);
                    this.userSessions.remove(sessionId);
                    //this.gameUserMap.get(gameId).remove(sessionId);
                   // this.gameUserMap.get(gameId).remove(sessionId);
                    
                } else {
                   // this.gameUserMap.remove(gameId);
                    this.games.remove(gameId);
                    this.userSessions.remove(sessionId);
                }
               // synchronized(this.activeList) {
               //     this.activeList.get(gm.getName()).remove(gameId);
               // }
                System.out.println("numGames: " + this.games.size());
                System.out.println("numSessions: " + this.userSessions.size());
               // System.out.println("PlayerNumMap: " + this.gameUserMap.size());
            }
        } catch (Exception e) {}
        
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
        



        // synchronized(this.activeList) {
        //     Map<String, Integer> l = this.activeList.get(game);
        //     List<String> list = new ArrayList<>();
        //     if(l != null) {
        //         for(String gameId : l.keySet()) {
        //             if(l.get(gameId) < 2) {
        //                 list.add(gameId);
        //             }
        //         }
        //     }

        //     //String[] list = l.keySet().toArray(new String[0]);
        //     return list.toArray(new String[list.size()]);
        //}
    }

    public Map<String, ArrayList<String>> getGamesList() {
        Map<String, ArrayList<String>> gamesList = this.loader.listAvailableGames();
        //List<String> list = Arrays.asList(gamesList);
        return gamesList;
    }
}