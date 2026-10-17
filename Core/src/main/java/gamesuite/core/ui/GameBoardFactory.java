package gamesuite.core.ui;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface GameBoardFactory {
    
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) throws JsonProcessingException, IOException;
}
