package gamesuite.core.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface CoordPair{
    

    public int getX();

    public int getY();

    public GamePiece getPiece();
    public boolean equals(Object obj);

    public CoordPair copy();

    public CoordPair toCoordPair(int[] arr);

    //public int[] toArray(CoordPair pos);

   //public ObjectNode getCoordPairJson();
}
