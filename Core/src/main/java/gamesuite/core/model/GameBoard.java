package gamesuite.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface GameBoard {

    public CoordPair[][] getBoard();

    public GameBoard copy();

    public int getSize();

    public int getSideLength();

    public CoordPair getBoardPos(int x, int y);
    public boolean isValidPos(int x, int y);

    public String toString();
    public JsonNode getJsonNode();
}
