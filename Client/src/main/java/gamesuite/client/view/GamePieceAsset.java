package gamesuite.client.view;

import java.awt.Color;
import javax.swing.JComponent;

public class GamePieceAsset extends JComponent {
    private Color color;
    private boolean isKing;
    
    public GamePieceAsset(Color color,boolean isKing) {
        this.color = color;
        this.isKing = isKing;
    }

    public Color getColor() { return this.color; }
    public boolean isKing() { return this.isKing; }
}
