package gamesuite.client.view;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import gamesuite.client.control.ClientManager;

import java.awt.event.WindowListener;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGUI {
    private JFrame window;
    private ClientManager cmg;
    private JList<String> gamesList;
    private JScrollPane listpane;
    private JPanel centerPanel;
    private JButton selectBtn;

    public MainGUI(ClientManager cmg) {
        this.cmg = cmg;
        this.window = new JFrame("GameSuite");
        this.window.setSize(800, 800);
        this.window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainGUI.this.cmg.quitGame(true);
                window.dispose();
                System.exit(0);
            }
        });
        this.window.setLayout(new BorderLayout());
        this.gamesList = new JList<>();
        this.listpane = new JScrollPane(this.gamesList);
        this.listpane.setPreferredSize(new Dimension(200,700));
        this.centerPanel = new JPanel();
        this.centerPanel.add(this.listpane);

        this.centerPanel.setPreferredSize(new Dimension(200, 500));
        this.window.add(this.centerPanel);
        this.selectBtn = new JButton("Select Game");

        this.selectBtn.addActionListener(a -> {
            String val = this.gamesList.getSelectedValue();
            if(val != null) {
                new Thread(() -> {
                    this.cmg.getActiveGames(val);
                });
            }
        });
        this.window.setMinimumSize(new Dimension(800, 500));
        this.window.setResizable(true);
    }

    public void setGamesList(String[] gamesList) {
        SwingUtilities.invokeLater(() -> {
            this.gamesList.setListData(gamesList);
        });
    }

    public void activate() {
        SwingUtilities.invokeLater(() -> {
            this.window.pack();
            this.window.setVisible(true);    
        });
    }
}
