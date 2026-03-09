package gamesuite.client.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.fasterxml.jackson.databind.JsonNode;

//import gamesuite.core.model.GameState;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
    private GameBoardUI gameBoard;
    private int tmpX, tmpY;
    private int playerTurn;
    private JList<String> gamesList;
    private JScrollPane listpane;
    private JButton selectBtn;
    private JLabel nameLbl;
    private JTextField name;
    private JButton createGameBtn;
    private String game;
    private JButton refreshButton;

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

        //this.cmg = cmg;
        this.listener = listener;
        this.window = new JFrame("GameSuite");
        this.window.setSize(800, 800);
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.turnLabel = new JTextPane();

        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GameGUI.this.listener.quitGame(true);
                window.dispose();
                System.exit(0);
            }
        });
        this.window.setLayout(new BorderLayout());
        this.gamesList = new JList<>();
        this.listpane = new JScrollPane(this.gamesList);
        this.listpane.setPreferredSize(new Dimension(200,400));
        this.centerPanel = new JPanel();
        this.centerPanel.add(this.listpane);

        this.centerPanel.setPreferredSize(new Dimension(600, 600));

        this.name = new JTextField();
        this.name.setPreferredSize(new Dimension(120, 30));
        this.nameLbl = new JLabel("Name");
        this.selectBtn = new JButton("Join Game");
        this.selectBtn.setPreferredSize(new Dimension(120, 30));
        
        this.selectBtn.addActionListener(a -> {
            String val = this.gamesList.getSelectedValue();
            System.out.println(this.name.getText());
            if(val != null && !this.name.getText().equals("")) {
                System.out.println("joining");
                 this.listener.joinGame(game, this.name.getText(), val);
                //this.listener.initActiveList(val);
                //this.cmg.getActiveGames(val);
            }
        });
        this.createGameBtn = new JButton("Create Game");
        this.createGameBtn.setPreferredSize(new Dimension(120, 30));
        this.createGameBtn.addActionListener(e -> {
             if(!this.name.getText().equals("")) {
                 this.listener.createGame(game, name.getText());  
             } 
         });
        
        JPanel btnPanel = new JPanel(new GridLayout(6, 1));
        this.refreshButton = new JButton("Refresh List");
        
        this.refreshButton.addActionListener(e -> {
            this.listener.refreshActiveList(this.game);
        });

        btnPanel.add(this.nameLbl);
        btnPanel.add(this.name);
        btnPanel.add(this.createGameBtn);
        btnPanel.add(this.selectBtn);
        btnPanel.add(this.refreshButton);
        this.centerPanel.add(btnPanel);
        this.turnLabel.setEditable(false);
        this.centerPanel.add(this.turnLabel);
        //this.btnPanel = new JPanel();
        this.window.add(this.centerPanel, BorderLayout.CENTER);
        //this.btnPanel.add(this.selectBtn);
        //this.window.add(this.btnPanel, BorderLayout.SOUTH);
        this.window.setMinimumSize(new Dimension(800, 500));
        this.window.setResizable(true);

    }

    public void setGame(String game) { this.game = game; }

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

    public void setActiveGamesList(String[] list) {
        this.gamesList.setListData(list);
    }
}
