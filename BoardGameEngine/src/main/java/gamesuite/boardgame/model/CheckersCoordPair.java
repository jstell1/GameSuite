package gamesuite.boardgame.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GamePiece;

public class CheckersCoordPair implements CoordPair {
    private int x;
    private int y;
    private CheckersGamePiece piece;

    public CheckersCoordPair() {
        x = -1; y = -1;
    }

    public CheckersCoordPair(int x, int y) {
        if(x < 0 || y < 0) {
            throw new IllegalArgumentException("coords are always positive in arrays");
        }
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    private CheckersCoordPair(int x, int y, CheckersGamePiece piece) {
        if(x < 0 || y < 0) {
            throw new IllegalArgumentException("coords are always positive in arrays");
        }
        this.x = x;
        this.y = y;
        this.piece = piece;
    }

    public void setX(int x) {
        if(this.x != -1)
            throw new IllegalStateException("coords are immutable once set");
        if(x < 0)
            throw new IllegalArgumentException("coords are always positive in arrays");
        this.x = x;
    }

    public void setY(int y) {
        if(this.y != -1)
            throw new IllegalStateException("coords are immutable once set");
        if(y < 0)
            throw new IllegalArgumentException("coords are always positive in arrays");
        this.y = y;
    }

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public CheckersGamePiece getPiece() { return this.piece; }

    public void setPiece(CheckersGamePiece piece) { this.piece = piece; } 

    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof CheckersCoordPair)) return false;
        CheckersCoordPair other = (CheckersCoordPair) obj;
        return this.x == other.x && this.y == other.y;
    }

    public CheckersCoordPair copy() { 
        CheckersCoordPair pos = new CheckersCoordPair(x, y); 
        //if(this.piece != null) {
            pos.setPiece(this.piece);
        //}
        return pos;
    }

    public CheckersCoordPair toCoordPair(int[] arr) {
        if(arr.length != 2)
            throw new IllegalArgumentException("must have 2 coord values"); 
        return new CheckersCoordPair(arr[0], arr[1]);
    }

    public static int[] toArray(CheckersCoordPair pos) {
        if(pos != null) {
            int[] arr = new int[2];
            arr[0] = pos.getX();
            arr[1] = pos.getY();
        }
        return null;
    }

  

    // @Override
    // public int[] toArray(CoordPair pos) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'toArray'");
    // }

    // @Override
    // public ObjectNode getCoordPairJson() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getCoordPairJson'");
    // }

}
