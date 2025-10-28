package checkers.view;

import java.util.Arrays;

import checkers.model.CheckersGamePiece;
import gamesuite.core.model.GamePiece;

public class CheckersGamePieceView implements GamePiece {

    CheckersGamePiece piece;
    
    public CheckersGamePieceView(CheckersGamePiece piece) { this.piece = piece; } 

    public GamePiece copy() { return new CheckersGamePieceView(this.piece.copy()); }

    public String getName() { return this.piece.getName(); }

    public String getType() { return this.piece.getType(); }

    public int getVal() { return this.piece.getVal(); }
    
    public String toString() { return this.piece.toString(); }

    public int[][] getValidMoves() { return this.piece.getValidMoves(); }

    public int[][] getValidJumps() { return this.piece.getValidJumps(); }

    public boolean isKing() { return this.piece.isKing(); }
}
