package gamesuite.client.control;

import java.util.List;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//import gamesuite.core.ui.CoordPairPanel;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.ui.UIListener;
import gamesuite.client.view.GameGUI;
import gamesuite.client.view.GameUI;

public class GUIManager implements GameUI, UIListener {
    
    private ClientManager gm;
    private GameGUI gui;
    public int tmpX;
    public int tmpY;

    public GUIManager() {//GameBoardUI gameBoard) {
        //this.gameBoard = gameBoard;
        this.tmpX = -1;
        this.tmpY = -1;
    }

    public GUIManager(ClientManager gm, GameState game) {
        this.gm = gm;
        this.tmpX = -1; 
        this.tmpY = -1;
        //this.gameView = game;
    }

    public void setGamManager(ClientManager gm) {
        if(this.gm == null)
            this.gm = gm;
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
                this.gui.setGameOverLabel("Give to player 2, GameId: " + gameId);
            });
        }
    }

    public void resetGUI() {
          //  this.tmpX = -1; this.tmpY = -1;
         GameGUI old = this.gui;
        SwingUtilities.invokeLater(() -> {
            old.disableGUI();
            this.gui = new GameGUI(this);
            old.closeWindow();
            this.gui.activate();
        });
    }

    @Override
    public void createGame(String name) {
     //   this.playerTurn = 1;
        new Thread(() -> {
            try {
                this.gm.createGame(name);
            } catch (Exception e) {
            }
        }).start();
    }

    @Override
    public void joinGame(String name, String gameId) {
        new Thread(() -> {
            String id = this.gm.joinGame(name, gameId);
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

}
