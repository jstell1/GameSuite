package gamesuite.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import gamesuite.model.data.GamePieceView;

public class GamePieceAsset extends JComponent {
    private Color color;
    private boolean isKing;
    
    public GamePieceAsset(Color color,boolean isKing) {
        this.color = color;
        this.isKing = isKing;
    }

    public void draw(Graphics2D g2d, int panelWidth, int panelHeight) {
        int diameter = Math.min(panelWidth, panelHeight) - 10;
        int x = (panelWidth - diameter) / 2;
        int y = (panelHeight - diameter) / 2;

        g2d.setColor(this.color);
        g2d.fillOval(x, y, diameter, diameter);

        g2d.setColor(this.color.darker());
        g2d.drawOval(x, y, diameter, diameter);
      
        if (isKing) {
            g2d.setColor(Color.YELLOW); 
            g2d.setStroke(new BasicStroke(2f));
            int inset = diameter / 4;
            g2d.drawOval(x + inset, y + inset, diameter - 2 * inset, diameter - 2 * inset);
        }
    }
}
