package gamesuite.server.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;
import java.util.HashMap;

@Service
public class ServerGameRepo {
    public final Map<String, GameManager> games = new ConcurrentHashMap<>();
    public final Set<String> userSessions = new HashSet<>();
    public final Map<String, Map<String, Integer>> gameUserMap = new ConcurrentHashMap<>();

    public String createGame(Player p1, GameBoard board, String sessionId) {
        GameManager gm = new GameManager(board, p1);
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        Map<String, Integer> users = new HashMap<>();
        users.put(sessionId, 1);
        this.gameUserMap.put(gameId, users);
        this.userSessions.add(sessionId);
        return gameId;
    }

    public Map<String, Integer> getUserSessions(String gameId) {
        return this.gameUserMap.get(gameId);
    }

    public void addWebSocketToGame(String gameId, String sessionId) {
        if(!this.gameUserMap.containsKey(gameId)) {
            Map<String, Integer> sessionList = new HashMap<>();
            sessionList.put(sessionId, 1);
            this.gameUserMap.put(gameId, sessionList);
            this.userSessions.add(sessionId);
        } else {
            this.gameUserMap.get(gameId).put(sessionId, 2);
            this.userSessions.add(sessionId);
        }
    }

    //public void setUserNum(String sessionId, int num) {
        //this.userPlayerNumMap.put(sessionId, num);
    //}

    public GameBoard joinGame(Player player, String gameId) {
        GameManager gm = this.games.get(gameId);
        boolean added = gm.addPlayer(player);
        if(added) {
            gm.initBoard();
            return gm.getBoard();
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

        Map<String, Integer> sessionList = this.gameUserMap.get(gameId);

        if(sessionList == null || !sessionList.containsKey(sessionId))
            return false;

        int turn = this.games.get(gameId).getTurn();
        if(this.gameUserMap.get(gameId).get(sessionId).intValue() != turn)
            return false;
            
        return true;
    }
}