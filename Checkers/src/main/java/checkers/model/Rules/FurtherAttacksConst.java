package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import gamesuite.core.model.Move;

public class FurtherAttacksConst extends Constraint {

    

    public FurtherAttacksConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "furtherAttacks";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        CheckersCoordPair 
           if(pos == null || pos.getPiece() == null)
            return false;
        CheckersGamePiece piece = pos.getPiece();
        String name = piece.getName();
        int startX = pos.getX();
        int startY = pos.getY();

        if(!this.board.isValidPos(startX, startY) || piece == null)
            return false;

        int[][] validJumps = piece.getValidJumps();
        int x;
        int y;
        int jumpX;
        int jumpY;

        for(int i = 0; i < validJumps.length; i++) {
            
            if(name.equals(this.pieceNames[0])) {
                x = startX + validJumps[i][0] * -1;
                y = startY + validJumps[i][1] * -1;
            } else {
                x = startX + validJumps[i][0];
                y = startY + validJumps[i][1];
            }

            if(inBounds(this.board.getBoardPos(x, y))) {
                jumpX = (startX + x) >> 1;
                jumpY = (startY + y) >> 1;

                CheckersCoordPair jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CheckersCoordPair end = this.board.getBoardPos(x, y);
                CheckersGamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                CheckersGamePiece jumpPiece = jumpPos.getPiece();

                if(jumpPiece != null && !name.equals(jumpPiece.getName()) && endPiece == null) {
                    return true;
                }
            }
        }
        return false;
        // if(this.gameState.getFurtherJumps() != null) {
        //     return new Result(true);
        // }
        // return new Result(false);
    }
}
