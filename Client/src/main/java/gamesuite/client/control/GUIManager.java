package gamesuite.client.control;

import java.util.List;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import gamesuite.client.view.GameGUI;
import gamesuite.client.view.GameUI;
import gamesuite.client.view.MainGUI;

public class GUIManager implements GameUI, UIListener {
    
    private ClientManager gm;
    private GameGUI gui;
    private MainGUI main;
    public int tmpX;
    public int tmpY;

    public GUIManager() {//GameBoardUI gameBoard) {
        //this.gameBoard = gameBoard;
        this.tmpX = -1;
        this.tmpY = -1;
    }

    // public GUIManager(ClientManager gm, GameState game) {
    //     this.gm = gm;
    //     this.tmpX = -1; 
    //     this.tmpY = -1;
    //     //this.gameView = game;
    // }

    public void setGamManager(ClientManager gm) {
        if(this.gm == null)
            this.gm = gm;
    } 
    public void setMainGUI(MainGUI gui) {
        this.main = gui;
    }

    public void setGameState(JsonNode game) {
        SwingUtilities.invokeLater(() -> {
            this.gui.setGameState(game);
        });

            //this.gameView = game;
    }

    public void setPlayerTurn(int playerTurn) {
        this.gui.setPlayerTurn(playerTurn);
    }

    public void setGameGUI(GameGUI gui) {
            this.gui = gui;
    }

    public void setBoard(GameBoardUI boardPanel) {
        //if(this.gameBoard == null)
            //this.gameBoard = boardPanel;
    }

    @Override
    public boolean getIsBoardEnabled() {
        return this.gui.isGUIEnabled();
    }

    @Override
    public void sendYellowedPanel(JsonNode pos) {
        //this.gameBoard.addYellowedPanel(pos);
    }

    @Override
    public void runGame() {
        this.gui.activate();
    }

    public int getPlayerTurn() {
        return this.gui.getPlayerTurn();
    }

    @Override
    public boolean isPlayerTurn() { 
        return false;//this.playerTurn == this.gameBoard.getTurn(); 
    }

    public void update(JsonNode gameState) {
        SwingUtilities.invokeLater(() -> {
            this.gui.update(gameState);
           // if(this.gameView != null) {
                //List<CoordPair> changed = this.gameView.getChangedPos();
                //this.gameBoard.updateBoard(changed);
            //    this.gameBoard.update();
           // }
           // this.tmpX = -1;
            //this.tmpY = -1;

           // if(!this.gameView.isGameOver()) {
            //    if(isPlayerTurn())
            //        this.gui.enableGUI();
            //    this.gui.setTurnLabel(this.gameView.getTurn());
          //  } else {
           //     Player winner = this.gameView.getWinner();
           //     this.gui.setGameOverLabel(winner.getName() + " is the winner");
          //  }
        });
    }
 
    @Override
    public void sendMove(JsonNode move) {
  
        SwingUtilities.invokeLater(() -> {
            this.gui.disableGUI();
            new Thread(() -> {
                this.gm.sendMove(move);
            }).start();     
        });
    }

    @Override
    public void initActiveList(String game) {
        SwingUtilities.invokeLater(() -> {
            this.main.closeWindow();
            this.gui.setGame(game);
            this.gui.activate();
            new Thread(() -> {
                this.gm.getActiveGames(game);
            }).start();
        });
    }

    @Override
    public void refreshActiveList(String game) {
        new Thread(() -> {
           // if(this.gui.getGroup(game) == null)
                this.gm.getActiveGames(game);//, null);
            //else
              //  this.gm.getActiveGames(game, this.gui.getGroup(game));
        }).start();
    }

    @Override
    public void refreshGamesList() {
        new Thread(() -> {
            this.gm.getAvailableGames();
        }).start();
    }

    public void initGame(GameBoardUI boardUI) {
        
        SwingUtilities.invokeLater(()->{

            GameGUI old = this.gui;
            int turn = old.getPlayerTurn();
            old.disableGUI();
            old.closeWindow();
            //this.gui.initGame(board, game, boardUI);
            //GameBoard gameBoard = new GameBoard(board);
            //setGameState(game);
            //GameBoardPanel panel = new GameBoardPanel(gameBoard, 600, this);
            //setBoard(panel);
            this.gui = new GameGUI(boardUI, this);
            this.gui.setPlayerTurn(turn);
            if(!this.gui.isPlayerTurn())
                this.gui.disableGUI();
    
            runGame();
        });
    }

    public void setGameId(String gameId) {
        if(gameId != null) {
           // this.playerTurn = 1;
            SwingUtilities.invokeLater(() -> {
                this.gui.disableGUI();
                this.gui.setGameOverLabel("GameId: " + gameId);
            });
        }
    }

    public void resetGUI() {
          //  this.tmpX = -1; this.tmpY = -1;
         GameGUI old = this.gui;
        SwingUtilities.invokeLater(() -> {
            old.disableGUI();
            this.gui = new GameGUI(this);
            this.main = new MainGUI(gm, this);
            this.gm.setMainGUI(this.main);
            old.closeWindow();
            this.main.activate();
            //this.gui.activate();
        });
    }

    @Override
    public void createGame(String game, String name) {
     //   this.playerTurn = 1;
        new Thread(() -> {
            try {
                this.gm.createGame(game, name);
            } catch (Exception e) {
            }
        }).start();
    }

    @Override
    public void joinGame(String game, String name, String gameId) {
        new Thread(() -> {
            String id = this.gm.joinGame(game, name, gameId);
            if(id != null) {
               // this.playerTurn = 2;
                SwingUtilities.invokeLater(() -> {
                    this.gui.disableGUI();
                    this.gui.setGameOverLabel(id);
                });
            }
        }).start();
    }

    @Override
    public void quitGame(boolean hardQuit) {
        SwingUtilities.invokeLater(() -> {
            this.gui.disableGUI();
            new Thread(() -> {
                try {
                    
                    this.gm.quitGame(hardQuit);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();
        });
    }

    @Override
    public void disabledBoard() {
        
        SwingUtilities.invokeLater(() -> {
            this.gui.disableGUI();
        });
    }

    @Override
    public void enableBoard() {
        SwingUtilities.invokeLater(() -> {
            this.gui.enableGUI();
        });
    }

    public void setActiveGamesList(String[] list) {
        SwingUtilities.invokeLater(() -> {
            this.gui.setActiveGamesList(list);
        });
    }

}
