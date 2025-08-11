package gamesuite.Client.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import gamesuite.Core.View.GamePieceView;

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
