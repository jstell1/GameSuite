package gamesuite.model.data;

public interface GamePieceView {
    public String getName();
    public String getType();
    public int getVal();
    public boolean isKing();
    public int[][] getValidMoves();
    public int[][] getValidJumps();
}
