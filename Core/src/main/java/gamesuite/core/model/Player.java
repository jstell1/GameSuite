package gamesuite.core.model;

import gamesuite.core.view.PlayerView;

public class Player implements PlayerView {
    private int points;
    private String name;

    public Player() {}

    public Player(String name, int points) { 
        this.name = name;
        this.points = points; 
    }

    public int addPoints(int num) {
        points += num;
        return points;
    }

    public void setPoints(int num) {
        if(this.points == 0)
            this.points = num;
    }

    public void setName(String name) {
        if(this.name == null) 
            this.name = name;
    }

    @Override
    public int getPoints() { return this.points; }

    @Override
    public String getName() { return this.name; }

    public Player copy() { return new Player(this.name, this.points); }

    public static PlayerView getPlayerView(Player player) { 
        return player; 
    }
}