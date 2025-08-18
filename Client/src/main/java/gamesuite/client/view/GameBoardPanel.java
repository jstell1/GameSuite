package gamesuite.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gamesuite.client.control.UIListener;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.Move;

public class GameBoardPanel extends JPanel {

    private GameBoard board;
    private int size;
    private List<CoordPair> changed;
    private CoordPairPanel[][] boardPanel;
    private UIListener listener;
    private Set<CoordPairPanel> yellowed;

    public GameBoardPanel(GameBoard board, int size, UIListener listener) {
        super();
        this.listener = listener;
        this.boardPanel = new CoordPairPanel[size][size];
        this.yellowed = new HashSet<>();
        this.changed = null;
        this.board = board;
        this.size = size;
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                CoordPairPanel pos = null;
                if ((i + j) % 2 == 0) {
                    pos = new CoordPairPanel(board.getBoardPos(i, j), this.listener, Color.LIGHT_GRAY);
                } else {
                    pos = new CoordPairPanel(board.getBoardPos(i, j), this.listener, Color.DARK_GRAY);
                }

                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                boardPanel[i][j] = pos;
                this.add(pos);
            }
        }
    }

    public void setListener(UIListener listener) {
        if(this.listener == null)
            this.listener = listener;
    }

    public void removeYellowed(int x, int y) {
            this.yellowed.remove(this.boardPanel[x][y]);
    }
    
    public void updateBoard(List<CoordPair> changes) {
        this.changed = changes;
        if(this.changed != null) {
            CoordPairPanel[] updateList = new CoordPairPanel[this.changed.size()];
            for(int i = 0; i < this.changed.size(); i++) {
                updateList[i] = this.boardPanel[this.changed.get(i).getX()][this.changed.get(i).getY()];
                updateList[i].setPiece(this.changed.get(i).getPiece());
            }
            this.changed = null;        
        }

        for(CoordPairPanel pos : this.yellowed) {
            pos.resetBackground();
        }

        this.yellowed.clear();
    }
    
    public void addYellowedPanel(CoordPairPanel pos) {
        this.yellowed.add(pos);
    }

    public CoordPairPanel getBoardPos(int x, int y) {
        return this.boardPanel[x][y];
    }
}


