package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;

public class AttackVector extends Constraint {

    public AttackVector(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "attackVector";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        CheckersCoordPair pos = this.board.getBoardPos(move.getStartX(), move.getStartY());
        CheckersCoordPair end = this.board.getBoardPos(move.getEndX(), move.getEndY());
        CheckersGamePiece p = pos.getPiece();
        int[][] attacks = p.getValidJumps();
        int[][] attackVectors = p.getAttackVectors();
        int idx = 0;
        int[] attack = null;
        String[] pieceNames = this.gameState.getPieceNames();
        String name = p.getName();
        this.turnFactor = -1;

        if(name.equals(pieceNames[1]))
            this.turnFactor = 1;

        for(int[] vector : attacks) {
            int x = move.getEndX() + vector[0] * this.turnFactor;
            int y = move.getEndY() + vector[1] * this.turnFactor;
            CheckersCoordPair tmp = this.board.getBoardPos(x, y);
            if(tmp == end) {
                attack = attackVectors[idx];
                break;
            }
            idx++;
        }

        CheckersCoordPair targ = this.board.getBoardPos(attack[0], attack[1]);
        if(targ.getPiece() == null) 
            return new Result(false);

        if(targ.getPiece().getName().equals(name))
            return new Result(false);

        return new Result(true);
        
    }
    
}
