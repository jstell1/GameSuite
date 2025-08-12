package gamesuite.Client.View;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class GameGUI {
    private JFrame window;
    private GameBoardPanel gameBoard;
    private JLabel turnLabel;

    public GameGUI(int startTurn, GameBoardPanel gameBoard) {
        this.window = new JFrame("GameSuite");
        this.window.setSize(800,800);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLayout(new BorderLayout());

        this.turnLabel = new JLabel("Player " + startTurn + "'s turn");
        this.window.add(this.turnLabel, BorderLayout.NORTH); 
        this.gameBoard = gameBoard;
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.add(gameBoard);
        this.window.add(centerPanel, BorderLayout.CENTER);
        this.window.setMinimumSize(new Dimension(800, 800));
        this.window.setResizable(true);
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
