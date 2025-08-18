package gamesuite.core.control;

import gamesuite.core.model.Move;
import gamesuite.core.network.GameCreatedResponse;

public interface GameManager {
    public void sendMove(Move move);
    public GameCreatedResponse createGame(String name);
    public GameCreatedResponse joinGame(String name, String gameId);
}
