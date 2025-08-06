package gamesuite.model.data;

import java.util.Arrays;

public class GamePiece implements GamePieceView {
    private String name;
    private String type;
    private int val;
    private final int[][] validMoves = {{-1, -1}, {-1, 1}};
    private final int[][] validJumps = {{-2, -2}, {-2, 2}};
    private final int[][] validKingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private final int[][] validKingJumps = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

    public GamePiece(String name, String type, int val) {
        this.name = name;
        this.type = type;
        this.val = val;
    }

    public GamePiece copy() { return new GamePiece(this.name, this.type, this.val); }

    @Override
    public String getName() { return this.name; }

    @Override
    public String getType() { return this.type; }

    @Override
    public int getVal() { return this.val; }
    
    @Override
    public String toString() {
        return this.name + this.type;
    }

    public void kingPiece() {
        if(this.type == "C") 
            this.type = "K";
    }

    @Override
    public int[][] getValidMoves() { 
        if(this.type == "C")
            return Arrays.copyOf(this.validMoves, this.validMoves.length);
        else 
            return Arrays.copyOf(this.validKingMoves, this.validKingMoves.length);
    }

    @Override
    public int[][] getValidJumps() {
        if(this.type == "C") 
            return Arrays.copyOf(this.validJumps, this.validJumps.length);
        else 
            return Arrays.copyOf(this.validKingJumps, this.validKingJumps.length);
    }

    @Override
    public boolean isKing() { return this.type == "K"; }

    public GamePieceView getPieceView() { return this; }
}
