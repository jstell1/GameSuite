package checkers.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//import gamesuite.client.view.CheckersCoordPair;
//import gamesuite.client.view.CheckersGameBoard;
//import gamesuite.client.view.CheckersGameState;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import checkers.model.*;

public class CheckersGameBoardFactory implements GameBoardFactory {

    @Override
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            CheckersCoordPair[][] board = mapper.treeToValue(boardJson, CheckersCoordPair[][].class);
            CheckersGameBoard cBoard = new CheckersGameBoard(board);
            CheckersGameState game = mapper.treeToValue(gameJson, CheckersGameState.class);
            CheckersGameBoardPanel gameBoard = new CheckersGameBoardPanel(cBoard, 600, listener);
            gameBoard.setGameState(game);
            return gameBoard;
        } catch (Exception e) {}

        return null;
    }
    
}
