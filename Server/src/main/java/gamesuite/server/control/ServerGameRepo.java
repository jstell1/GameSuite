package gamesuite.server.control;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;

@Service
public class ServerGameRepo {
    public final Map<String, GameManager> games = new ConcurrentHashMap<>();
    public final Map<String, String> userSessions = new ConcurrentHashMap<>();
    public final Map<String, Map<String, Integer>> gameUserMap = new ConcurrentHashMap<>();

    public String createGame(Player p1, GameBoard board, String sessionId) {
        GameManager gm = new GameManager(board, p1);

        
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        synchronized(gm) {
            Map<String, Integer> users = new HashMap<>();
            users.put(sessionId, 1);
            this.gameUserMap.put(gameId, users);
            this.userSessions.put(sessionId, gameId);
            System.out.println("numGames: " + this.games.size());
            System.out.println("numSessions: " + this.userSessions.size());
            System.out.println("PlayerNumMap: " + this.gameUserMap.get(gameId).size());
        }
        return gameId;
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

    //public void setUserNum(String sessionId, int num) {
        //this.userPlayerNumMap.put(sessionId, num);
    //}

    public GameBoard joinGame(Player player, String gameId) {
        GameManager gm = this.games.get(gameId);
        synchronized(gm) {
            if(gm.getGameState().getPlayer(2) == null) {

                boolean added = gm.addPlayer(player);
                if(added) {
                    gm.initBoard();
                    return gm.getBoard();
                }
            }
        }
        return null;
    }

    public GameState getGameView(String gameId) {
        return this.games.get(gameId).getGameState();
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

    public GameState removePlayer(String sessionId) {
        String gameId = null;
        GameManager gm = null;
        GameState game = null;

        try {
            gameId = this.userSessions.get(sessionId);
            gm = this.games.get(gameId);
            synchronized(gm) {
                if(gm.getGameState().getWinner() == null) {
                    
                    Map<String, Integer> playerNums = this.gameUserMap.get(gameId);
                    int playerNum = playerNums.get(sessionId).intValue();
                    game = gm.quitGame(playerNum);
                    this.userSessions.remove(sessionId);
                    this.gameUserMap.get(gameId).remove(sessionId);
                    
                } else {
                    this.gameUserMap.remove(gameId);
                    this.games.remove(gameId);
                    this.userSessions.remove(sessionId);
                }
                System.out.println("numGames: " + this.games.size());
                System.out.println("numSessions: " + this.userSessions.size());
                System.out.println("PlayerNumMap: " + this.gameUserMap.size());
            }
        } catch (Exception e) {}
        
        return game;
    }
}