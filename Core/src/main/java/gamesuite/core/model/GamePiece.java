package gamesuite.core.model;

import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ObjectNode;
public interface GamePiece {



    public GamePiece copy();

    public String getName();

    public String getType();

    public int getVal();
    
    public String toString();

    public int[][] getValidMoves();

    public int[][] getValidJumps();

    public boolean isKing();
    //public ObjectNode getObjectNode();
}
