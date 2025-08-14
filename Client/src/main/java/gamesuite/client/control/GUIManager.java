package gamesuite.client.control;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.util.List;
import javax.swing.SwingUtilities;

import gamesuite.client.view.CoordPairPanel;
import gamesuite.client.view.GameBoardPanel;
import gamesuite.client.view.GameGUI;
import gamesuite.client.view.GameUI;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.Move;
import gamesuite.core.view.CoordPairView;
import gamesuite.core.view.GameStateView;
import gamesuite.core.view.PlayerView;

public class GUIManager implements GameUI, UIListener {
    private GameStateView gameView;
    private GameManager gm;
    private GameGUI gui;
    private GameBoardPanel gameBoard;
    private int tmpX, tmpY;

    public GUIManager(GameManager gm) {
        this.gm = gm;
        this.tmpX = -1; 
        this.tmpY = -1;
        this.gameView = gm.getGameStateView();
        this.gameBoard = new GameBoardPanel(this.gameView.getBoardView(), 600, this);
        this.gui = new GameGUI(this.gameView.getTurn(), this.gameBoard);
    }

    @Override
    public boolean getIsBoardEnabled() {
        return this.gameBoard.isEnabled();
    }

    @Override
    public void sendYellowedPanel(CoordPairPanel pos) {
        this.gameBoard.addYellowedPanel(pos);
    }

    @Override
    public void runGame() {
        this.gui.activate();
    }

    @Override
    public void sendChange(int x, int y) {
        
        if(this.tmpX == -1 && this.tmpY == -1) {
            this.tmpX = x;
            this.tmpY = y;
        } else if(x == this.tmpX && y == this.tmpY) {
            this.tmpX = -1;
            this.tmpY = -1;
            this.gameBoard.removeYellowed(x, y);
        } else {
            this.gameBoard.setEnabled(false);
            new Thread(() -> {
                this.gm.sendMove(new Move(this.tmpX, this.tmpY, x, y));
                SwingUtilities.invokeLater(() -> {
                    if(this.gameView != null) {
                        List<CoordPairView> changed = this.gameView.getChangedPos();
                        this.gameBoard.updateBoard(changed);
                    }
                    this.tmpX = -1;
                    this.tmpY = -1;

                    if(!this.gameView.isGameOver()) {
                        this.gameBoard.setEnabled(true);
                        this.gui.setTurnLabel(this.gameView.getTurn());
                    } else {
                        PlayerView winner = this.gameView.getWinnerView();
                        this.gui.setGameOverLabel(winner.getName() + " is the winner");
                    }
                });
            }).start();
        }
    }
}
