package gamesuite.server;

import com.fasterxml.jackson.databind.JsonNode;
import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;

import java.io.IOException;

public class MockGameFactory extends GameManagerFactory {
    @Override
    public GameManager createGame(String playerName, String playerId, JsonNode gameDef) throws IOException {
        return null;
    }

    @Override
    public boolean isMultiGame() {
        return false;
    }
}
