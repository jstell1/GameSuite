package gamesuite.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;

import gamesuite.model.data.GameBoardView;

public class GameBoardPanel extends JPanel {

    private GameBoardView board;
    private int size;
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
                if ((i + j) % 2 == 0) {
                    pos.setBackground(Color.LIGHT_GRAY);
                } else {
                    pos.setBackground(Color.DARK_GRAY);
                }
                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                this.add(pos);
            }
        }
    }


}
