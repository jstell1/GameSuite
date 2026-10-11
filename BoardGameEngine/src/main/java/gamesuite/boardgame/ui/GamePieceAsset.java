package gamesuite.boardgame.ui;

import java.awt.Color;
import javax.swing.JComponent;

public class GamePieceAsset extends JComponent {
    private Color color;
    private boolean isKing;
    private String name;
    private String team;
    private String type;
    
    public GamePieceAsset(Color color, String name, String team, String type) {
        this.color = color;
        this.name = name;
        this.team = team;
        this.type = type;
    }

    public String getName() { return this.name; }
    public String getTeam() { return this.team; }
    public Color getColor() { return this.color; }
    public boolean isKing() { return this.isKing; }
    public String getType() { return this.type; }

}
