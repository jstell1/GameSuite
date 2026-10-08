package gamesuite.boardgame.test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.ui.CheckersGameBoardPanel;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

public class TestListener extends JFrame implements UIListener{
    CheckersGameBoardPanel panel;
    JPanel centerPanel;
      public TestListener() {
        
        //this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      

        this.setSize(800, 800);
        //GridBagLayout gridbag = new GridBagLayout();
        //t//his.setLayout(gridbag);
        
        //JPanel centerPanel = new JPanel();
        //centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        //centerPanel.add(gameBoard);
        
        // Constraints for the game board
        //GridBagConstraints boardConstraints = new GridBagConstraints();
        //boardConstraints.gridx = 0;
        //boardConstraints.gridy = 1;
       // boardConstraints.weightx = 1.0; 
        //boardConstraints.weighty = 1.0; 
        //boardConstraints.fill = GridBagConstraints.BOTH; 
        //boardConstraints.insets = new Insets(0, 5, 5, 5); 
        //this.add(centerPanel, boardConstraints);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setMinimumSize(new Dimension(800, 800));
        this.setResizable(true);
    }

    public void setPanel(CheckersGameBoardPanel p) {
        this.panel = p;
        this.add(p);
        
    }

     @Override
    public void createGame(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public void disabledBoard() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'disabledBoard'");
    }

    @Override
    public void enableBoard() {
        // TODO Auto-generated method stub
       // throw new UnsupportedOperationException("Unimplemented method 'enableBoard'");
    }

    @Override
    public boolean getIsBoardEnabled() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'getIsBoardEnabled'");
        return true;
    }

    @Override
    public void initActiveList(String arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initActiveList'");
    }

    @Override
    public boolean isPlayerTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPlayerTurn'");
    }

    @Override
    public void joinGame(String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }

    @Override
    public void quitGame(boolean arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'quitGame'");
    }

    @Override
    public void refreshActiveList(String arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshActiveList'");
    }

    @Override
    public void refreshGamesList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshGamesList'");
    }

    @Override
    public void sendMove(JsonNode arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'sendMove'");
    }

    @Override
    public void sendYellowedPanel(JsonNode arg0) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'sendYellowedPanel'");
    }

}
