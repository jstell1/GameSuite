package gamesuite.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GamePieceView;
public class CoordPairPanel extends JPanel {

    private GamePieceAsset piece;
    private CoordPairView pos;
    private int row;
    private int col;

    public CoordPairPanel(CoordPairView pos) {
        super();
        this.pos = pos;
        this.row = pos.getX();
        this.col = pos.getY();
    }

    public int getRow() { return this.row; }

    public int getCol() { return this.col; }

    public CoordPairView getCoordPairView() { return this.pos; }

    public void setPiece(GamePieceView piece) {
        if(piece == null)
            return;

        if(this.pos.getPieceView().getName() == "B") 
            this.piece = new GamePieceAsset(Color.BLACK,this.pos.getPieceView().isKing());
        else
            this.piece = new GamePieceAsset(Color.RED,this.pos.getPieceView().isKing());
        repaint();
    }

    public void removePiece() {
        this.piece = null;
        repaint();
    }

    public GamePieceAsset getPiece() {
        return piece;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.piece != null) 
            this.piece.draw(g2d, getWidth(), getHeight());
    }
    

}
