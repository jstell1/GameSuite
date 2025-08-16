package gamesuite.core.model;

public class Player {
    private int points;
    private String name;
    private String userId;

    public Player() {}

    public Player(String name, int points) { 
        this.name = name;
        this.points = points; 
        this.userId = null;
    }

    public boolean setUserId(String id) {
        if(this.userId == null) {
            this.userId = id;
            return true;
        }
        return false;
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

    public int getPoints() { return this.points; }

    public String getName() { return this.name; }

    public Player copy() { return new Player(this.name, this.points); }
}