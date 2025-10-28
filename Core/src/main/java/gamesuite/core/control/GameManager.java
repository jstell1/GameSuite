package gamesuite.core.control;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public interface GameManager {

    
    //public abstract void initGame();
    public void sendMove(Move move);
    public void sendMove(ObjectNode move);
    public boolean addPlayer(String player);
    public boolean isGameReady();
    public String getBoardString();
    public Player getWinner();
    public GameBoard getBoard();
    public boolean gameOver();
    public boolean initBoard();
    public int getTurn();
    public GameState getGameState();
    public GameState quitGame(int playerNum);
}
