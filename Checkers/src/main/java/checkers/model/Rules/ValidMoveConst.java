package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import gamesuite.core.model.Move;

public class ValidMoveConst extends Constraint {

    public ValidMoveConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "validMove";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        CheckersCoordPair pos = this.board.getBoardPos(move.getStartX(), move.getStartY());
        CheckersCoordPair end = this.board.getBoardPos(move.getEndX(), move.getEndY());
        CheckersGamePiece p = pos.getPiece();
        int[][] moves = p.getValidMoves();
        String name = p.getName();
        String[] pieceNames = this.gameState.getPieceNames();
        this.turnFactor = -1;
        
        if(name.equals(pieceNames[1]));
            turnFactor = 1;
        
        for(int[] pair : moves) {
            int x = pos.getX() + pair[0] * turnFactor;
            int y = pos.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return new Result(true);
        }
        return new Result(false);
    }
    
}
