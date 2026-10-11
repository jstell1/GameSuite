package gamesuite.core.model;


public interface CoordPair{
    

    public int getX();

    public int getY();

    public GamePiece getPiece();
    public boolean equals(Object obj);

    public CoordPair copy();

    public CoordPair toCoordPair(int[] arr);

}
