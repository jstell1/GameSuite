package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;

public class EmptyTargConst extends Constraint {

    public EmptyTargConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "emptyTarg";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        CheckersCoordPair pos = this.board.getBoardPos(move.getEndX(), move.getEndY());
        if(pos.getPiece() != null)
            return new Result(false);
        return new Result(true); 
        
    }
    
}
