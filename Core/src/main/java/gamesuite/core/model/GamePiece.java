package gamesuite.core.model;

import java.util.Arrays;

public class GamePiece {
    private String name;
    private String type;
    private int val;
    private boolean king;
    private final int[][] validMoves = {{-1, -1}, {-1, 1}};
    private final int[][] validJumps = {{-2, -2}, {-2, 2}};
    private final int[][] validKingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private final int[][] validKingJumps = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

    public GamePiece() {}

    public GamePiece(String name, String type, int val) {
        this.name = name;
        this.type = type;
        this.val = val;
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    public void setName(String name) {
        if(this.name == null)
            this.name = name;
    }

    public void setType(String type) {
        if(this.type == null) 
            this.type = type;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public GamePiece copy() { return new GamePiece(this.name, this.type, this.val); }

    public String getName() { return this.name; }

    public String getType() { return this.type; }

    public int getVal() { return this.val; }
    
    public String toString() {
        return this.name + this.type;
    }

    public void kingPiece() {
        if(this.type == "C") 
            this.type = "K";
    }

    public int[][] getValidMoves() { 
        if(this.type == "C")
            return Arrays.copyOf(this.validMoves, this.validMoves.length);
        else 
            return Arrays.copyOf(this.validKingMoves, this.validKingMoves.length);
    }

    public int[][] getValidJumps() {
        if(this.type == "C") 
            return Arrays.copyOf(this.validJumps, this.validJumps.length);
        else 
            return Arrays.copyOf(this.validKingJumps, this.validKingJumps.length);
    }

    public boolean isKing() { return this.type == "K"; }
}
