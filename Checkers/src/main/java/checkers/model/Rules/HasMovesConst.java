package checkers.model.Rules;

import java.util.Set;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;

public class HasMovesConst extends Constraint {

    private FurtherAttacksConst

    public HasMovesConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "allCaptured";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        
        String[] names = this.gameState.getPieceNames();

    }

    private boolean hasValidMoves(String name) {
        for(int i = 0; i < this.board.getSideLength(); i++) {
            if(i % 2 == 0 && rowHasValidMoves(i, 1, name)) {
               return true;
            } else if(rowHasValidMoves(i, 0, name)) {
                return true;
            }
        }
        return false;
    }

     private boolean rowHasValidMoves(int row, int start, String name) {
        String[] pieceNames = this.gameState.getPieceNames();
        int fact = -1;
        if(name.equals(pieceNames[1]))
            fact = 1;
        for(int j = start; j < this.board.getSideLength(); j += 2) {
            CheckersCoordPair pos = this.board.getBoardPos(row, j);
            if(pos == null)
                return false;
            CheckersGamePiece piece = pos.getPiece();
            if(piece != null && piece.getName().equals(name)) {
                int[][] validMoves = piece.getValidMoves();

                for(int[] vect : validMoves) {
                    //CheckersCoordPair validDiff
                    int x = pos.getX() + vect[0] * fact;
                    int y = pos.getY() + vect[1] * fact;
                    if(inBounds(new CheckersCoordPair(x, y))) {
                        CheckersCoordPair end = this.board.getBoardPos(x, y);
                        if(end == null)
                            end = new CheckersCoordPair(x, y);
    
                        CheckersMove move = new CheckersMove(pos.getX(), pos.getY(), end.getX(), end.getY());
                        if(isValidMove(move, piece.getName()))
                            return true;
                    }
                }
                if(hasFurtherJumps(pos))
                    return true;
            }
        }
        return false;
    }

    private boolean isValidMove(CheckersMove move, String pName) {
        if(move == null)
            return false;

        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CheckersCoordPair start = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return false; 
        if(start.getPiece() == null || !start.getPiece().getName().equals(pName) || end.getPiece() != null) 
            return false;

        String name = start.getPiece().getName();
        int turnFactor = -1;
        if(name.equals("R"))
            turnFactor = 1;

        int[][] validMoves = start.getPiece().getValidMoves();
        for(int[] pair : validMoves) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return true;
        }
        return false;
    }
    
}
