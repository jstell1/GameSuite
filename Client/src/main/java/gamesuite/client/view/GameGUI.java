package gamesuite.client.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import gamesuite.client.App;
import gamesuite.client.control.UIListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

public class GameGUI {
    private JFrame window;
    private GameBoardPanel gameBoard;
    //private JLabel turnLabel;
    private JTextPane turnLabel;
    private JPanel centerPanel;
    private UIListener listener;

    public GameGUI(int startTurn, GameBoardPanel gameBoard) {
        this.window = new JFrame("GameSuite");
        this.window.setSize(800,800);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLayout(new BorderLayout());

        this.turnLabel = new JTextPane();//("Player " + startTurn + "'s turn");
        this.turnLabel.setText("Player " + startTurn + "'s turn");
        this.turnLabel.setEditable(false);
        this.window.add(this.turnLabel, BorderLayout.NORTH); 
        this.gameBoard = gameBoard;
        this.centerPanel = new JPanel();
        this.centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.centerPanel.add(gameBoard);
        this.window.add(this.centerPanel, BorderLayout.CENTER);
        this.window.setMinimumSize(new Dimension(800, 800));
        this.window.setResizable(true);
    }

    public GameGUI(UIListener listener) {
        this.listener = listener;
        this.window = new JFrame("GameSuite");
        this.window.setSize(800, 800);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        // --- Row 0: Create Game ---
        gridC.gridx = 0;
        gridC.gridy = 0;
        this.centerPanel.add(createGameBtn, gridC);

        gridC.gridx = 1;
        this.centerPanel.add(new JLabel("Name:"), gridC);

        gridC.gridx = 2;
        this.centerPanel.add(createName, gridC);

        // --- Row 1: Join Game (with Name field) ---
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

        // --- Row 2: Game ID field ---
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

    public void setGameOverLabel(String msg) {
        this.turnLabel.setText(msg);
    }
}
