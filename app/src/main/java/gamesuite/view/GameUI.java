package gamesuite.view;

import gamesuite.model.data.Move;

public interface GameUI {
    public void displayBoard();
    public Move getPlayerMove(int playerNum);
}
