package gamesuite.view;

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

import gamesuite.control.UIListener;
import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GameBoardView;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;

public class GameBoardPanel extends JPanel {

    private GameBoardView board;
    private int size;
    private List<CoordPairView> changed;
    private CoordPairPanel[][] boardPanel;
    private UIListener listener;
    private Set<CoordPairPanel> yellowed;

    public GameBoardPanel(GameBoardView board, int size, UIListener listener) {
        super();
        //this.setOpaque(true);
        this.boardPanel = new CoordPairPanel[size][size];
        this.yellowed = new HashSet<>();
        this.changed = null;
        this.board = board;
        this.size = size;
        this.listener = listener;
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                CoordPairPanel pos = null;
                if ((i + j) % 2 == 0) {
                    pos = new CoordPairPanel(board.getPos(i, j), this.listener, Color.LIGHT_GRAY);
                } else {
                    pos = new CoordPairPanel(board.getPos(i, j), this.listener, Color.DARK_GRAY);
                }

                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                boardPanel[i][j] = pos;
                this.add(pos);
            }
        }
    }

    public void removeYellowed(int x, int y) {
            this.yellowed.remove(this.boardPanel[x][y]);
    }
    
    public void updateBoard(List<CoordPairView> changes) {
        this.changed = changes;
        if(this.changed != null) {
            CoordPairPanel[] updateList = new CoordPairPanel[this.changed.size()];
            for(int i = 0; i < this.changed.size(); i++) {
                updateList[i] = this.boardPanel[this.changed.get(i).getX()][this.changed.get(i).getY()];
                updateList[i].setPiece(this.changed.get(i).getPieceView());
            }
            this.changed = null;        
        }

        for(CoordPairPanel pos : this.yellowed) {
            pos.resetBackground();
        }

        this.yellowed.clear();
        setEnabled(true);
    }
    
    public void addYellowedPanel(CoordPairPanel pos) {
        this.yellowed.add(pos);
    }

    public CoordPairPanel getBoardPos(int x, int y) {
        return this.boardPanel[x][y];
    }
}


