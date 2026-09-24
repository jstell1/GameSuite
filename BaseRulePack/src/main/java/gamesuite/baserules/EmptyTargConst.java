package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Result;

public class EmptyTargConst extends Constraint {

    private CheckersGameState game;
    private CheckersGameBoard board;

    public EmptyTargConst() {
        super("emptyTarg");
    }

    public EmptyTargConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "emptyTarg";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(x, y);
        if(pos.getPiece() != null)
            return false;
        return true; 
        
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
        this.game = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }
    
}
