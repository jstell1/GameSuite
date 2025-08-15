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

    public String createGame(Player p1, GameBoard board) {
        GameManager gm = new GameManager(board, p1);
        String gameId = UUID.randomUUID().toString();
        this.games.put(gameId, gm);
        return gameId;
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
        return null;//this.games.get(gameId).getGameState();
    }

    public boolean contains(String gameId) {
        return this.games.containsKey(gameId);
    }
}