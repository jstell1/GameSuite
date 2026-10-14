package gamesuite.boardgame.model;

import gamesuite.core.model.CoordPair;

public class CoordPairImpl implements CoordPair {
    private int x;
    private int y;
    private BoardGamePiece piece;

    public CoordPairImpl() {
        x = -1; y = -1;
    }

    public CoordPairImpl(int x, int y) {
        if(x < 0 || y < 0) {
            throw new IllegalArgumentException("coords are always positive in arrays");
        }
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    private CoordPairImpl(int x, int y, BoardGamePiece piece) {
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

    public BoardGamePiece getPiece() { return this.piece; }

    public void setPiece(BoardGamePiece piece) { this.piece = piece; } 

    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof CoordPairImpl)) return false;
        CoordPairImpl other = (CoordPairImpl) obj;
        return this.x == other.x && this.y == other.y;
    }

    public CoordPairImpl copy() { 
        CoordPairImpl pos = new CoordPairImpl(x, y); 
        
            pos.setPiece(this.piece);
        
        return pos;
    }

    public CoordPairImpl toCoordPair(int[] arr) {
        if(arr.length != 2)
            throw new IllegalArgumentException("must have 2 coord values"); 
        return new CoordPairImpl(arr[0], arr[1]);
    }

    public static int[] toArray(CoordPairImpl pos) {
        if(pos != null) {
            int[] arr = new int[2];
            arr[0] = pos.getX();
            arr[1] = pos.getY();
        }
        return null;
    }

}
