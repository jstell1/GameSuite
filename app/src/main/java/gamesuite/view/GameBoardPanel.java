package gamesuite.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import gamesuite.model.data.GameBoardView;
import gamesuite.model.data.Move;

public class GameBoardPanel extends JPanel {

    private GameBoardView board;
    private int size;
    private CoordPairPanel sourceSquare = null;
    public GameBoardPanel(GameBoardView board, int size) {
        super();
        this.board = board;
        this.size = size;
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                CoordPairPanel pos = new CoordPairPanel(board.getPos(i, j));
                pos.setPiece(board.getPos(i, j).getPieceView());
                if ((i + j) % 2 == 0) {
                    pos.setBackground(Color.LIGHT_GRAY);
                } else {
                    pos.setBackground(Color.DARK_GRAY);
                }
                pos.addMouseListener(squareClickListener);
                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                this.add(pos);
            }
        }
    }

    private final MouseAdapter squareClickListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            CoordPairPanel clicked = (CoordPairPanel) e.getSource();
            if (clicked.getPiece() != null) {
                sourceSquare = clicked;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (sourceSquare == null) return;

            CoordPairPanel targetSquare = (CoordPairPanel) e.getSource();

            Move move = new Move(sourceSquare.getRow(), sourceSquare.getCol(), targetSquare.getRow(), targetSquare.getCol());

            
                targetSquare.setPiece(sourceSquare.getCoordPairView().getPieceView());
                sourceSquare.removePiece();
            

            sourceSquare = null;
        }
    };


}
