package checkers.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

public class CheckersGameBoardFactory implements GameBoardFactory {

    @Override
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            CheckersCoordPair[][] board = mapper.treeToValue(boardJson, CheckersCoordPair[][].class);
            CheckersGameBoard cBoard = new CheckersGameBoard(board);
            CheckersGameState game = mapper.treeToValue(gameJson, CheckersGameState.class);
            CheckersGameBoardPanel gameBoard = new CheckersGameBoardPanel(cBoard, 600, listener);
            return gameBoard;
        } catch (Exception e) {}

        return null;
    }
    
}
