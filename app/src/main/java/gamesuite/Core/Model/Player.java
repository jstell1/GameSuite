package gamesuite.Core.Model;

import gamesuite.Core.View.PlayerView;

public class Player implements PlayerView {
    private int points;
    private String name;

    public Player(String name, int points) { 
        this.name = name;
        this.points = points; 
    }

    public int addPoints(int num) {
        points += num;
        return points;
    }

    @Override
    public int getPoints() { return this.points; }

    @Override
    public String getName() { return this.name; }

    public Player copy() { return new Player(this.name, this.points); }

    public PlayerView getPlayerView() { return this; }
}