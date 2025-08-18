package gamesuite.server.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import gamesuite.core.control.CoreGameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;

@Service
public class ServerGameRepo {
    public final Map<String, CoreGameManager> games = new ConcurrentHashMap<>();
    public final Map<String, Integer> userPlayerNumMap = new ConcurrentHashMap<>();
    public final Map<String, List<String>> gameUserMap = new ConcurrentHashMap<>();

    public String createGame(Player p1, GameBoard board) {
        CoreGameManager gm = new CoreGameManager(board, p1);
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        return gameId;
    }

    public List<String> getUserSessions(String gameId) {
        List<String> list = this.gameUserMap.get(gameId);
        return list;
    }

    public void addWebSocketToGame(String gameId, String sessionId) {
        if(!this.gameUserMap.containsKey(gameId)) {
            List<String> sessionList = new ArrayList<>();
            sessionList.add(sessionId);
            this.gameUserMap.put(gameId, sessionList);
        } else {
            this.gameUserMap.get(gameId).add(sessionId);
        }
    }

    public void setUserNum(String sessionId, int num) {
        this.userPlayerNumMap.put(sessionId, num);
    }

    public GameBoard joinGame(Player player, String gameId) {
        CoreGameManager gm = this.games.get(gameId);
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

    public CoreGameManager getGM(String id) { 
        return this.games.get(id);
    }

    public boolean rightPlayer(String gameId, String sessionId) {
        if(!containsGame(gameId) || !this.userPlayerNumMap.containsKey(sessionId))
            return false;

        List<String> sessionList = this.gameUserMap.get(gameId);

        if(sessionList == null || !sessionList.contains(sessionId))
            return false;

        int turn = this.games.get(gameId).getTurn();
        if(this.userPlayerNumMap.get(sessionId) != turn)
            return false;
            
        return true;
    }
}