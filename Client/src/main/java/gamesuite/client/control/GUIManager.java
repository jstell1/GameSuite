package gamesuite.client.control;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public GUIManager() {
        this.tmpX = -1;
        this.tmpY = -1;
    }


    public void setGamManager(ClientManager gm) {
        if(this.gm != null)
            throw new IllegalStateException("cannot be changed once set");
        this.gm = gm;
    } 
    public void setMainGUI(MainGUI gui) {
        this.main = gui;
    }

    public void setGameState(JsonNode game) {
        SwingUtilities.invokeLater(() -> {
            this.gui.setGameState(game);
        });

    }

    public void setErrorMsg(String error) {
        SwingUtilities.invokeLater(() -> {
            
            this.main.setErrorMsg(error);
        });
    }

    public void setPlayerTurn(int playerTurn) {
        this.gui.setPlayerTurn(playerTurn);
    }

    public void setGameGUI(GameGUI gui) {
            this.gui = gui;
    }

    public void setBoard(GameBoardUI boardPanel) {
    }

    @Override
    public boolean getIsBoardEnabled() {
        return this.gui.isGUIEnabled();
    }

    @Override
    public void sendYellowedPanel(JsonNode pos) {
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
        return false;
    }

    public void update(JsonNode gameState) {
        SwingUtilities.invokeLater(() -> {
            this.gui.update(gameState);
      
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
            Map<String, String> tmp = this.main.getGamesMap();
            this.main.closeWindow();
            this.gui.setGame(game);
            this.gui.setGamesMap(tmp);
            this.gui.activate();
            new Thread(() -> {
                this.gm.getActiveGames(game);
            }).start();
        });
    }

    @Override
    public void refreshActiveList(String game) {
        new Thread(() -> {
                this.gm.getActiveGames(game);
     
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
            this.gui = new GameGUI(boardUI, this);
            this.gui.setPlayerTurn(turn);
            System.out.println(this.gui.getPlayerTurn());
            System.out.println(boardUI.getTurn());
            if(!this.gui.isPlayerTurn())
                this.gui.disableGUI();
    
            runGame();
        });
    }

    public void setGameId(String gameId) {
        if(gameId != null) {
            SwingUtilities.invokeLater(() -> {
                this.gui.disableGUI();
                this.gui.setGameOverLabel("GameId: " + gameId);
            });
        }
    }

    public void resetGUI() {
         GameGUI old = this.gui;
        SwingUtilities.invokeLater(() -> {
            old.disableGUI();
            this.gui = new GameGUI(this);
            this.main = new MainGUI(gm, this);
            this.gm.setMainGUI(this.main);
            old.closeWindow();
            this.main.activate();
        });
    }

    @Override
    public void createGame(String game, String group, String name) {
        new Thread(() -> {
            
            this.gm.createGame(game, group, name);
           
        }).start();
    }

    @Override
    public void joinGame(String game, String name, String gameId) {
        new Thread(() -> {
            String id;
            try {
                id = this.gm.joinGame(game, name, gameId);
                if(id != null) {
                    SwingUtilities.invokeLater(() -> {
                        this.gui.disableGUI();
                        this.gui.setGameOverLabel(id);
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("issue joining game");
            }
        }).start();
    }

    @Override
    public void quitGame(boolean hardQuit) {
        SwingUtilities.invokeLater(() -> {
            this.gui.disableGUI();
            new Thread(() -> {
                this.gm.quitGame(hardQuit);
               
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


    public void setPromotionChoices(String[] list) {
        SwingUtilities.invokeLater(() -> {
            this.gui.setPromotionChoices(list);
        });
    }


    @Override
    public void sendPromoteChoice(String choice) {
        new Thread(() -> {
            this.gm.sendPromoteChoice(choice);
        }).start();
    }

}
