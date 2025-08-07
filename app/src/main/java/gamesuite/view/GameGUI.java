package gamesuite.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import gamesuite.control.MoveListener;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameGUI implements GameUI {
    private JFrame window;
    private GameStateView gameView;
    private GameBoardPanel gameBoard;
    private JLabel turnLabel;
    private int turn;
    private MoveListener moveListener;

    public GameGUI(GameStateView gameView, MoveListener moveListener) {
        this.moveListener = moveListener;
        this.gameView = gameView;
        this.turn = gameView.getTurn();
        this.window = new JFrame("GameSuite");
        this.window.setSize(800,800);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLayout(new BorderLayout());

        this.turnLabel = new JLabel("Player " + this.turn + "'s turn");
        this.window.add(this.turnLabel, BorderLayout.NORTH); 
        this.gameBoard = new GameBoardPanel(this.gameView.getBoardView(), 600);
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.add(gameBoard);
        this.window.add(centerPanel, BorderLayout.CENTER);
        this.window.setMinimumSize(new Dimension(800, 800));
        this.window.setResizable(true);
    }

    @Override
    public void runGame() {
        this.window.pack();
        this.window.setVisible(true);
    }

}
