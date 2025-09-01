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
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.GameCreatedResponse;

public class GUIManager implements GameUI, UIListener {
    private GameState gameView;
    private ClientManager gm;
    private GameGUI gui;
    private GameBoardPanel gameBoard;
    private int tmpX, tmpY;
    private int playerTurn;

    public GUIManager() {
        this.tmpX = -1;
        this.tmpY = -1;
    }

    public GUIManager(ClientManager gm, GameState game) {
        this.gm = gm;
        this.tmpX = -1; 
        this.tmpY = -1;
        this.gameView = game;
    }

    public void setGamManager(ClientManager gm) {
        if(this.gm == null)
            this.gm = gm;
    } 

    public void setGameState(GameState game) {
            this.gameView = game;
    }

    public void setGameGUI(GameGUI gui) {
            this.gui = gui;
    }

    public void setBoard(GameBoardPanel boardPanel) {
        if(this.gameBoard == null)
            this.gameBoard = boardPanel;
    }

    @Override
    public boolean getIsBoardEnabled() {
        return this.gui.isGUIEnabled();
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
    public boolean isPlayerTurn() { 
        return this.playerTurn == this.gameView.getTurn(); 
    }

    public void update() {
        SwingUtilities.invokeLater(() -> {
            if(this.gameView != null) {
                List<CoordPair> changed = this.gameView.getChangedPos();
                this.gameBoard.updateBoard(changed);
            }
            this.tmpX = -1;
            this.tmpY = -1;

            if(!this.gameView.isGameOver()) {
                if(isPlayerTurn())
                    this.gui.enableGUI();
                this.gui.setTurnLabel(this.gameView.getTurn());
            } else {
                Player winner = this.gameView.getWinner();
                this.gui.setGameOverLabel(winner.getName() + " is the winner");
            }
        });
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
            this.gui.disableGUI();
            //this.gameBoard.setEnabled(false);
            new Thread(() -> {
                this.gm.sendMove(new Move(this.tmpX, this.tmpY, x, y));
            }).start();

        }
    }

    public void initGame(CoordPair[][] board, GameState game) {
        GameGUI old = this.gui;
        SwingUtilities.invokeLater(() -> {
            old.disableGUI();
        });
        GameBoard gameBoard = new GameBoard(board);
        setGameState(game);
        GameBoardPanel panel = new GameBoardPanel(gameBoard, 600, this);
        setBoard(panel);
        this.gui = new GameGUI(game.getTurn(), panel);
        old.closeWindow();
        if(!isPlayerTurn())
            this.gui.disableGUI();
        runGame();
    }

    @Override
    public void createGame(String name) {
        new Thread(() -> {
            GameCreatedResponse resp = this.gm.createGame(name);
            if(resp != null) {
                this.playerTurn = 1;
                SwingUtilities.invokeLater(() -> {
                    this.gui.disableGUI();
                    this.gui.setGameOverLabel("Give to player 2, GameId: " + resp.getGameId());
                });
            }
        }).start();
    }

    @Override
    public GameCreatedResponse joinGame(String name, String gameId) {
        new Thread(() -> {
            GameCreatedResponse resp = this.gm.joinGame(name, gameId);
            if(resp != null) {
                this.playerTurn = 2;
                SwingUtilities.invokeLater(() -> {
                    this.gui.disableGUI();
                    this.gui.setGameOverLabel(resp.getGameId());
                });
            }
        }).start();

        return null;
    }
}
