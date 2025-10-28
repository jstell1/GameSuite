package checkers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import checkers.model.CheckersCoordPair;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GamePiece;

public class CheckersCoordPairView implements CoordPair {
    
    CheckersCoordPair chp;

    public CheckersCoordPairView(CheckersCoordPair chp) { this.chp = chp; }

    public int getX() { return this.chp.getX(); }

    public int getY() { return this.chp.getY(); }

    public GamePiece getPiece() { return new CheckersGamePieceView(this.chp.getPiece()); }

    public boolean equals(Object obj) { return this.chp.equals(obj); }

    public CoordPair copy() { return new CheckersCoordPairView(this.chp.copy()); }

    public CoordPair toCoordPair(int[] arr) { return new CheckersCoordPairView(CheckersCoordPair.toCoordPair(arr)); }

    public int[] toArray(CoordPair pos) { return null; }
}
