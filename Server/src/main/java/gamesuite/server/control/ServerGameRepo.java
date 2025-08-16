package gamesuite.server.control;

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
    private final Map<String, GameManager> games = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> userGameMap = new ConcurrentHashMap<>();

    public String createGame(Player p1, GameBoard board) {
        GameManager gm = new GameManager(board, p1);
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        return gameId;
    }

    public String setUserToGame(String gameId, int num) {
        String userId = UUID.randomUUID().toString();
        Map<String, Integer> playerNumMap = new ConcurrentHashMap<>();
        playerNumMap.put(gameId, num);
        this.userGameMap.put(userId, playerNumMap);
        return userId;
        
    }

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

    public boolean rightPlayer(String gameId, String userId) {
        if(!containsGame(gameId) || !this.userGameMap.containsKey(userId))
            return false;
            
        Map<String, Integer> game = this.userGameMap.get(userId);
        int currTurn = this.games.get(gameId).getTurn();
        if(!game.get(gameId).equals(currTurn))
            return false;
        return true;
    }
}