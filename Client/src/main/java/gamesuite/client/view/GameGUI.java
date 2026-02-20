package gamesuite.client.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;
//import checkers.ui.GameBoardPanel;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameGUI {
    private JFrame window;
    //private JLabel turnLabel;
    private JTextPane turnLabel;
    private JPanel centerPanel;
    private UIListener listener;
    private JButton quitButton;
    private GameState gameView;
    private GameBoardUI gameBoard;
    private int tmpX, tmpY;
    private int playerTurn;

    //Gameboard ready
    public GameGUI(GameBoardUI gameBoard, UIListener listener) {
        int startTurn = gameBoard.getTurn();
        this.listener = listener;
        this.window = new JFrame("GameSuite");

        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listener.quitGame(true);
                window.dispose();
                System.exit(0);
            }
        });

        this.window.setSize(800, 800);
        GridBagLayout gridbag = new GridBagLayout();
        this.window.setLayout(gridbag);

        this.turnLabel = new JTextPane();
        this.turnLabel.setText("Player " + startTurn + "'s turn");
        this.turnLabel.setEditable(false);
        
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 2; 
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1.0;
        labelConstraints.weighty = 0.0; 
        labelConstraints.insets = new Insets(5, 5, 5, 5); 
        this.window.add(this.turnLabel, labelConstraints);

        this.gameBoard = gameBoard;
        this.centerPanel = new JPanel();
        this.centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.centerPanel.add(gameBoard);
        
        // Constraints for the game board
        GridBagConstraints boardConstraints = new GridBagConstraints();
        boardConstraints.gridx = 0;
        boardConstraints.gridy = 1;
        boardConstraints.weightx = 1.0; 
        boardConstraints.weighty = 1.0; 
        boardConstraints.fill = GridBagConstraints.BOTH; 
        boardConstraints.insets = new Insets(0, 5, 5, 5); 
        this.window.add(this.centerPanel, boardConstraints);

        this.quitButton = new JButton("Quit");

        this.quitButton.addActionListener(a -> {
            listener.quitGame(false);
        });
        
        // Constraints for the quit button on the right
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1; 
        buttonConstraints.gridy = 1; 
        buttonConstraints.weightx = 0.0;
        buttonConstraints.weighty = 0.0; 
        buttonConstraints.anchor = GridBagConstraints.NORTH; 
        buttonConstraints.insets = new Insets(5, 0, 5, 5); 
        this.window.add(this.quitButton, buttonConstraints);

        this.window.setMinimumSize(new Dimension(800, 800));
        this.window.setResizable(true);
    }

    //main page
    public GameGUI(UIListener listener) {
        this.listener = listener;
        this.window = new JFrame("GameSuite");
        this.window.setSize(800, 800);
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listener.quitGame(true);
                window.dispose();
                System.exit(0);
            }
        });

        this.window.setLayout(new BorderLayout());
        //this.turnLabel = new JLabel("Create new game or join game");
        this.turnLabel = new JTextPane();
        this.turnLabel.setText("Create new game or join game");
        this.turnLabel.setEditable(false);
        this.window.add(this.turnLabel, BorderLayout.NORTH); 

        this.centerPanel = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        this.centerPanel.setLayout(gridbag);
        GridBagConstraints gridC = new GridBagConstraints();

        gridC.insets = new Insets(5, 5, 5, 5);
        gridC.fill = GridBagConstraints.HORIZONTAL;

        JButton createGameBtn = new JButton("Create Game");
        JButton joinGameBtn = new JButton("Join Game");
        JTextField createName = new JTextField(20);
        JTextField joinName = new JTextField(20);
        JTextField joinGameId = new JTextField(20);
        
        createGameBtn.addActionListener(e -> {
            if(createName.getText() != null) {
                this.listener.createGame(createName.getText());  
            } 
        });

        joinGameBtn.addActionListener(e -> {
            if(joinName.getText() != null && joinGameId != null) {
                this.listener.joinGame(joinName.getText(), joinGameId.getText());
            }
        });
        // Row 0: Create Game
        gridC.gridx = 0;
        gridC.gridy = 0;
        this.centerPanel.add(createGameBtn, gridC);

        gridC.gridx = 1;
        this.centerPanel.add(new JLabel("Name:"), gridC);

        gridC.gridx = 2;
        this.centerPanel.add(createName, gridC);

        // Row 1: Join Game (with Name field)
        gridC.gridx = 0;
        gridC.gridy = 1;
        gridC.gridheight = 2; // spans 2 rows vertically
        gridC.anchor = GridBagConstraints.CENTER;
        this.centerPanel.add(joinGameBtn, gridC);

        gridC.gridheight = 1; // reset for others

        // Row 1 label + text field
        gridC.gridx = 1;
        gridC.gridy = 1;
        this.centerPanel.add(new JLabel("Name:"), gridC);

        gridC.gridx = 2;
        this.centerPanel.add(joinName, gridC);

        // Row 2: Game ID field
        gridC.gridx = 1;
        gridC.gridy = 2;
        this.centerPanel.add(new JLabel("GameID:"), gridC);

        gridC.gridx = 2;
        this.centerPanel.add(joinGameId, gridC);

        // finalize
        this.window.add(this.centerPanel, BorderLayout.CENTER);
        this.window.setMinimumSize(new Dimension(800, 800));
        this.window.setResizable(true);

    }

    public void closeWindow() {
        this.window.dispose();
    }

    public boolean isGUIEnabled() { return this.centerPanel.isEnabled(); }

    public void disableGUI() { 
        this.centerPanel.setEnabled(false);
        //this.gameBoard.setEnabled(false);
    }

    public void enableGUI() {
        this.centerPanel.setEnabled(true);
        //this.gameBoard.setEnabled(true);
    }

    public void activate() {
        this.window.pack();
        this.window.setVisible(true);
    }

    public void setTurnLabel(int num) {
        this.turnLabel.setText("Player " + num + "'s turn");
    }

    public int getPlayerTurn() {
        return this.playerTurn;
    }

    public void setGameOverLabel(String msg) {
        this.turnLabel.setText(msg);
    }

    public void update(JsonNode gameState) {
       // if(this.gameView != null) {
                //List<CoordPair> changed = this.gameView.getChangedPos();
                this.gameBoard.update(gameState);
                //this.gameBoard.update();
         //   }
           // this.tmpX = -1;
            //this.tmpY = -1;
 
            if(!this.gameBoard.isGameOver()) {
                if(this.playerTurn == this.gameBoard.getTurn())
                    enableGUI();
                setTurnLabel(this.gameBoard.getTurn());
            } else {
                String winner = this.gameBoard.getWinner();
                setGameOverLabel(winner + " is the winner");
            }
           // return null;
        //this.gameBoard.updateBoard(gameState);
    }

    public void setGameState(JsonNode game) {
       
    }

    public void initGame(JsonNode board, JsonNode game, GameBoardUI boardUI) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initGame'");
    }

    public boolean isPlayerTurn() {
        return this.playerTurn == this.gameBoard.getTurn();
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public void removeYellowed(int x, int y) {
        this.gameBoard.removeYellowed(x, y);
    }
}
