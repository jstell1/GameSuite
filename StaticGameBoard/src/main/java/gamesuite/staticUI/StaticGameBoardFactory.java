package gamesuite.staticUI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

public class StaticGameBoardFactory implements GameBoardFactory {

    @Override
    public GameBoardUI createGameBoard(JsonNode arg0, JsonNode arg1, UIListener arg2) throws JsonProcessingException {
        return new StaticGameBoard();
    }

}
