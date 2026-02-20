package gamesuite.core.ui;

import javax.swing.JPanel;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.model.Player;

public abstract class GameBoardUI extends JPanel {
     public abstract void setListener(UIListener listener);

    public abstract void removeYellowed(int x, int y);
    
    //public abstract void updateBoard(JsonNode changesNode);

    public abstract void setGameState(JsonNode gameState);

    public abstract int getTurn();
    
    public abstract void addYellowedPanel(JsonNode pos);

    public abstract void update(JsonNode gameState);

    public abstract boolean isGameOver();

    public abstract String getWinner();
    

    //public CoordPairPanel getBoardPos(int x, int y);
}
