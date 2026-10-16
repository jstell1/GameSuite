package gamesuite.chess;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class ChessIncrTurnEffect extends Effect{

    private BoardGameState gameState;
    private GameBoardImpl board;

    public ChessIncrTurnEffect() {
        super("chessIncrTurn");
    }

    public ChessIncrTurnEffect(String name, GameState gameState) {
        super(name, gameState);
        this.name = "chessIncrTurn";
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
        this.gameState.setTurn(this.gameState.getTurn() % 2 + 1);
        this.gameState.flipTurnFactor();
    }
    
}
