package gamesuite.client.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import gamesuite.client.control.UIListener;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GamePiece;
public class CoordPairPanel extends JPanel {

    private GamePieceAsset piece;
    private CoordPair pos;
    private int row;
    private int col;
    private UIListener listener;
    private Color baseColor;

    public CoordPairPanel(CoordPair pos, UIListener listener, Color baseColor) {
        super();
        this.pos = pos;
        this.row = pos.getX();
        this.col = pos.getY();
        this.listener = listener;
        this.baseColor = baseColor;
        setBackground(baseColor);
        setPiece();
        setLayout(new FlowLayout());

        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(listener.getIsBoardEnabled()) {
                    Color currCol = getBackground();
                    if(currCol != Color.YELLOW) {
                        setBackground(Color.YELLOW);
                        repaint();
                        listener.sendYellowedPanel(getSelf());
                        listener.sendChange(row, col);
                    } else {
                        setBackground(baseColor);
                        repaint();
                    }
                }
            }
        });
        
    }

    public void resetBackground() {
        setBackground(this.baseColor);
    }
    
    private CoordPairPanel getSelf() { return this; }

    public int getRow() { return this.row; }

    public int getCol() { return this.col; }

    public CoordPair getCoordPair() { return this.pos; }

    private void setPiece() {
        if(this.pos.getPiece() == null)
            this.piece = null;
        else if(this.pos.getPiece().getName().equals("B")) {
            this.piece = new GamePieceAsset(Color.BLACK,this.pos.getPiece().isKing());
            //add(this.piece);
        } else {
            this.piece = new GamePieceAsset(Color.RED,this.pos.getPiece().isKing());
            //add(this.piece);
        }
        repaint();
    }

    public void setPiece(GamePiece piece) {
        if(piece == null) {
            this.piece = null;
        }
            //remove(this.piece);
        else if(this.pos.getPiece().getName().equals("B")) {
            //remove(this.piece);
            this.piece = new GamePieceAsset(Color.BLACK,this.pos.getPiece().isKing());
            //add(this.piece);
        } else {
            //remove(this.piece);
            this.piece = new GamePieceAsset(Color.RED,this.pos.getPiece().isKing());
            //add(this.piece);
        }
        repaint();
    }

    public GamePieceAsset getPiece() {
        return piece;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(this.piece == null) {
            super.paintComponent(g);
        } else {

            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
            int diameter = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
    
            g2d.setColor(this.piece.getColor());
            g2d.fillOval(x, y, diameter, diameter);
            g2d.drawOval(x, y, diameter, diameter);
    
            if (this.piece.isKing()) {
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(2f));
                int inset = diameter / 4;
                g2d.drawOval(x + inset, y + inset, diameter - 2 * inset, diameter - 2 * inset);
            }
    
            g2d.dispose();
        }
    }

    public Color getBaseColor() { return this.baseColor; }
}
