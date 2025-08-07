package gamesuite.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GameStateView;

public class CoordPairPanel extends JPanel {
    private CoordPairView pos;
    public CoordPairPanel(CoordPairView pos) {
        super();
        this.pos = pos;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (hasPiece()) {
            int diameter = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            if(this.pos.getPieceView().getName() == "B")
                g2d.setColor(Color.BLACK);
            else
                g2d.setColor(Color.RED);
                
            g2d.fillOval(x, y, diameter, diameter);
        }
    }

    public boolean hasPiece() {
        if(this.pos.getPieceView() != null)
            return true;
        return false;
    }

}
