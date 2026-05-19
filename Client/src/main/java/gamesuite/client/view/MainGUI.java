package gamesuite.client.view;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import gamesuite.client.control.ClientManager;
import gamesuite.core.ui.UIListener;

import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGUI {
    private JFrame window;
    private ClientManager cmg;
    private JList<String> gamesList;
    private JScrollPane listpane;
    private JPanel centerPanel;
    private JPanel btnPanel;
    private JButton selectBtn;
    private JButton refreshBtn;
    private UIListener listener;
    private Map<String, String> gamesMap; 

    public MainGUI(ClientManager cmg, UIListener listener) {
        this.cmg = cmg;
        this.listener = listener;
        this.gamesMap = new HashMap<>();
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
        this.listpane.setPreferredSize(new Dimension(200,400));
        this.centerPanel = new JPanel();
        this.centerPanel.add(this.listpane);

        this.centerPanel.setPreferredSize(new Dimension(600, 600));
        this.selectBtn = new JButton("Select Game");
        this.selectBtn.setPreferredSize(new Dimension(120, 30));
        
        this.selectBtn.addActionListener(a -> {
            String val = this.gamesList.getSelectedValue();
            //if(val != null && this.gamesMap.get(val) == null) {
                this.listener.initActiveList(val);//, null);
                //this.cmg.getActiveGames(val);
            //} else if (val != null) {
               // this.listener.initActiveList(val, this.gamesMap.get(val));
           // }
        });

        JPanel btnPanel = new JPanel(new GridLayout(2, 1));
        btnPanel.add(this.selectBtn);
        this.refreshBtn = new JButton("Refresh List");
        this.refreshBtn.setPreferredSize(new Dimension(120, 30));

        this.refreshBtn.addActionListener(e -> {
            this.listener.refreshGamesList();
        });
        btnPanel.add(this.refreshBtn);
        this.centerPanel.add(btnPanel);
        //this.btnPanel = new JPanel();
        this.window.add(this.centerPanel, BorderLayout.CENTER);
        //this.btnPanel.add(this.selectBtn);
        //this.window.add(this.btnPanel, BorderLayout.SOUTH);
        this.window.setMinimumSize(new Dimension(800, 500));
        this.window.setResizable(true);
    }

    public void setGamesList(Map<String, ArrayList<String>> gamesList) {
        SwingUtilities.invokeLater(() -> {
            ArrayList<String> tmp = new ArrayList<>();
            for(String game : gamesList.keySet()) {
                if(gamesList.get(game) == null) {
                    tmp.add(game);
                    this.gamesMap.put(game, null);
                } else {
                    ArrayList<String> tmp2 = gamesList.get(game);

                    for(String name : tmp2) {
                        tmp.add(name);
                        this.gamesMap.put(name, game);
                    }
                }
            }
            this.gamesList.setListData(tmp.toArray(new String[0]));
        });
    }

    public void hideGUI() {
        this.window.setEnabled(false);
        this.window.setVisible(false);
    }

    public void showGUI() {
        this.window.setEnabled(true);
        this.window.setVisible(true);
    }

    public void closeWindow() {
        this.window.dispose();
    }

    public void activate() {
        SwingUtilities.invokeLater(() -> {
            this.window.pack();
            this.window.setVisible(true);    
            new Thread(() -> {
                List<String> games = this.cmg.getAvailableGames();
            }).start();
        });
    }
}
