package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;

public class ValidAttackConst extends Constraint {

    public ValidAttackConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "validAttack";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CheckersCoordPair start = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return new Result(false);

        CheckersGamePiece piece = start.getPiece();
        String[] pieceNames = this.gameState.getPieceNames();
        String name = piece.getName();

        int turnFactor = -1;
        if(name.equals(pieceNames[1]))
            turnFactor = 1;

        int[][] validJumps = piece.getValidJumps();
        for(int[] pair : validJumps) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return new Result(true);
        }
        return new Result(false);
    }

}
