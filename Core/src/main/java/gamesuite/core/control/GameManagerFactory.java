package gamesuite.core.control;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class GameManagerFactory {
    public abstract GameManager createGame(String playerName, String playerId, JsonNode gameDef);
    public abstract boolean isMultiGame();
}
