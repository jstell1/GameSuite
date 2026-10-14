package gamesuite.boardgame.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import gamesuite.boardgame.model.*;

public class GameBoardFactoryImpl implements GameBoardFactory {

    @Override
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        CoordPairImpl[][] board = mapper.treeToValue(boardJson, CoordPairImpl[][].class);
        GameBoardImpl cBoard = new GameBoardImpl(board);
        BoardGameState game = mapper.treeToValue(gameJson, BoardGameState.class);
        GameBoardPanel gameBoard = new GameBoardPanel(cBoard, 600, listener);
        gameBoard.setGameState(game);
        return gameBoard;
    
    }
    
}
