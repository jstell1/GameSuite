package gamesuite.chess;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class CheckmateEffect extends Effect {

    private BoardGameState gameState;
    private GameBoardImpl board;

    public CheckmateEffect() {
        super("checkmate");
    }

    public CheckmateEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "checkmate";
    }

    @Override
    public void setGameState(JsonNode game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setBoard(JsonNode board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setGameState(GameState game) {
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public void updateState(JsonNode move) {
        
    }
    
}
