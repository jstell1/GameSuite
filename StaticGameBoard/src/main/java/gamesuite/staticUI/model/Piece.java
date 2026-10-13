package gamesuite.staticUI.model;


public class Piece {
    private String name;
    private String team;
    private String type;
    private int val;

    public Piece(String name, String team, String type, int val) {
        this.name = name; this.team = team;
        this.type = type; this.val = val;
    }

    public String getName() { return this.name; }
    public String getTeam() { return this.team; }
    public String getType() { return this.type; }
    public int getVal() { return this.val; }

}
