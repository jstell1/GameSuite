package gamesuite.core.model;


public interface Move {

    public int getStartX();

    public int getStartY();

    public int getEndX();

    public int getEndY();

    public void setPlayerId(String id);
    
    public String getPlayerId();



}
