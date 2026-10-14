package gamesuite.boardgame.model;

import gamesuite.core.model.Move;

public class BoardGameMove implements Move {
    int startX, startY;
    int endX, endY;
    String playerId;
    String action;

    public BoardGameMove() {
        this.startX = -1; this.startY = -1;
        this.endX = -1; this.endY = -1;
    }

    public BoardGameMove(int startX, int startY, int endX, int endY) {
        this.startX = startX; this.startY = startY;
        this.endX = endX; this.endY = endY;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() { return this.action; }

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

    public void setPlayerId(String id) { this.playerId = id; }

    public String getPlayerId() { return this.playerId; }

    public int getStartX() { return this.startX; }

    public int getStartY() { return this.startY; }

    public int getEndX() { return this.endX; }

    public int getEndY() { return this.endY; }

   
}
