package checkers.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.model.Move;

public class CheckersMove implements Move {
    int startX, startY;
    int endX, endY;

    public CheckersMove() {
        this.startX = -1; this.startY = -1;
        this.endX = -1; this.endY = -1;
    }

    public CheckersMove(int startX, int startY, int endX, int endY) {
        this.startX = startX; this.startY = startY;
        this.endX = endX; this.endY = endY;
    }

    public void setStartX(int x) {
        if( x >= 0 && this.startX == -1) 
            this.startX = x;
    }

    public void setStartY(int y) {
        if( y >= 0 && this.startY == -1) 
            this.startY = y;
    }

    public void setEndX(int x) {
        if( x >= 0 && this.endX == -1) 
            this.endX = x;
    }

    public void setEndY(int y) {
        if( y >= 0 && this.endY == -1)  
            this.endY = y;
    }

    public int getStartX() { return this.startX; }

    public int getStartY() { return this.startY; }

    public int getEndX() { return this.endX; }

    public int getEndY() { return this.endY; }

    // @Override
    // public void setJsonNode(JsonNode node) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'setJsonNode'");
    // }

    // @Override
    // public JsonNode getJsonNode() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode node = mapper.valueToTree(this);
    //     return node;
    // } 
}
