package gamesuite.core.model;


public interface Player {

    public int getPoints();

    public String getUserId();

    public String getName();

    public Player copy();
}