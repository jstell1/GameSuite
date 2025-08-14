package gamesuite.server.control;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.Player;
import gamesuite.core.view.GameStateView;

@Service
public class ServerGameManager {
    private final Map<String, GameManager> games = new ConcurrentHashMap<>();

    public String createGame(Player p1) {
        GameManager gm = new GameManager(p1);
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, gm);
        return gameId;
    }

    public GameStateView getGameView(String gameId) {
        return games.get(gameId).getGameStateView();
    }

    
}