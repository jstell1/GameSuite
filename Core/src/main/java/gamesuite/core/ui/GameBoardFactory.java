package gamesuite.core.ui;

import com.fasterxml.jackson.databind.JsonNode;

public interface GameBoardFactory {
    
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener);
}
