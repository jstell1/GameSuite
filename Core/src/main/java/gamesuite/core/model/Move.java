package gamesuite.core.model;

public class Move {
    int startX, startY;
    int endX, endY;

    public Move() {}

    public Move(int startX, int startY, int endX, int endY) {
        this.startX = startX; this.startY = startY;
        this.endX = endX; this.endY = endY;
    }

    public void setStartX(int x) {
        if( x >= 0) 
            this.startX = x;
    }

    public void setStartY(int y) {
        if( y >= 0) 
            this.startY = y;
    }

    public void setEndX(int x) {
        if( x >= 0) 
            this.endX = x;
    }

    public void setEndY(int y) {
        if( y >= 0) 
            this.endY = y;
    }

    public int getStartX() { return this.startX; }

    public int getStartY() { return this.startY; }

    public int getEndX() { return this.endX; }

    public int getEndY() { return this.endY; } 
}
