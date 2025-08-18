package gamesuite.core.model;

public class CoordPair{
    private int x;
    private int y;
    private GamePiece piece;

    public CoordPair() {
        x = -1; y = -1;
    }

    public CoordPair(int x, int y) {
        this.x = x;
        this.y = y;
        this.piece = null;
    }

    public void setX(int x) {
        if(this.x == -1)
            this.x = x;
    }

    public void setY(int y) {
        if(this.y == -1)
            this.y = y;
    }

    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public GamePiece getPiece() { return this.piece; }

    public void setPiece(GamePiece piece) { this.piece = piece; } 

    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof CoordPair)) return false;
        CoordPair other = (CoordPair) obj;
        return this.x == other.x && this.y == other.y;
    }

    public CoordPair copy() { 
        CoordPair pos = new CoordPair(x, y); 
        if(this.piece != null)
            pos.setPiece(this.piece.copy());
        return pos;
    }

    public static CoordPair toCoordPair(int[] arr) {
        if(arr.length == 2) 
            return new CoordPair(arr[0], arr[1]);
        return null;
    }

    public static int[] toArray(CoordPair pos) {
        if(pos != null) {
            int[] arr = new int[2];
            arr[0] = pos.getX();
            arr[1] = pos.getY();
        }
        return null;
    }
}
