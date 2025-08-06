package gamesuite.model.data;

public interface GameBoardView {
    public CoordPairView getPos(int x, int y);
    public String toString();
    public int getSideLength();
    public int getSize();
}
