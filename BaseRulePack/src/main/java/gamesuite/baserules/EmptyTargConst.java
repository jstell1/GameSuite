package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class EmptyTargConst extends Constraint {

    private BoardGameState game;
    private GameBoardImpl board;

    public EmptyTargConst() {
        super("emptyTarg");
    }

    public EmptyTargConst(BoardGameState gameState, GameBoardImpl board) {
        super(gameState, board);
        this.name = "emptyTarg";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(x, y);
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
        this.game = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }
    
}
