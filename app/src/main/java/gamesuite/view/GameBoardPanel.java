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

import gamesuite.control.MoveListener;
import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GameBoardView;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;

public class GameBoardPanel extends JPanel implements CoordPairPanelObserver {

    private GameBoardView board;
    private int size;
    private List<CoordPairView> changed;
    private CoordPairPanel[][] boardPanel;
    private int tmpX;
    private int tmpY;
    private MoveListener listener;
    private Set<CoordPairPanel> yellowed;

    public GameBoardPanel(GameBoardView board, int size, MoveListener listener) {
        super();
        //this.setOpaque(true);
        this.boardPanel = new CoordPairPanel[size][size];
        this.yellowed = new HashSet<>();
        this.changed = null;
        this.board = board;
        this.size = size;
        this.tmpX = -1;
        this.tmpY = -1;
        this.listener = listener;
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                CoordPairPanel pos = null;
                if ((i + j) % 2 == 0) {
                    pos = new CoordPairPanel(board.getPos(i, j), this, Color.LIGHT_GRAY);
                } else {
                    pos = new CoordPairPanel(board.getPos(i, j), this, Color.DARK_GRAY);
                }

                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                boardPanel[i][j] = pos;
                this.add(pos);
            }
        }
    }

    @Override
    public CoordPairView sendChange(int x, int y) {
        if(this.tmpX == -1 && this.tmpY == -1) {
            this.tmpX = x;
            this.tmpY = y;
        } else if(x == this.tmpX && y == this.tmpY) {
            this.tmpX = -1;
            this.tmpY = -1;
            this.yellowed.remove(this.boardPanel[x][y]);
        } else {
            
            setEnabled(false);
            new Thread(() -> {
                this.listener.sendMove(new Move(this.tmpX, this.tmpY, x, y));
            }).start();
            
        }
        return null;
    }

    public void sendChanges(List<CoordPairView> changes) {
        this.changed = changes;
         if(this.changed != null) {
            updateBoard();
        }
        this.tmpX = -1;
        this.tmpY = -1;
        for(CoordPairPanel pos : this.yellowed) {
            pos.resetBackground();
        }
        this.yellowed.clear();
        setEnabled(true);
    }

    private void updateBoard() {
        CoordPairPanel[] updateList = new CoordPairPanel[this.changed.size()];
        for(int i = 0; i < this.changed.size(); i++) {
            updateList[i] = this.boardPanel[this.changed.get(i).getX()][this.changed.get(i).getY()];
            updateList[i].setPiece(this.changed.get(i).getPieceView());
        }
        repaint();
        this.changed = null;
    }

    @Override
    public boolean getIsBoardEnabled() {
        return isEnabled();
    }

    @Override
    public void sendYellowedPanel(CoordPairPanel pos) {
        this.yellowed.add(pos);
    }
}


