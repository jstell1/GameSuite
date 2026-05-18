package checkers.control;

import checkers.model.CheckersGameBoard;
import checkers.model.CheckersPlayer;
import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;

public class CheckersGameFactory extends GameManagerFactory {

    private static final boolean multiGame = true;

    @Override
    public GameManager createGame(String playerName) {
        
        CheckersGameBoard board = new CheckersGameBoard(8);
        CheckersPlayer player = new CheckersPlayer(playerName, 0);
        CheckersGameManager gm = new CheckersGameManager(board, player);

        return gm;
    }

    @Override
    public boolean isMultiGame() {
        return multiGame;
    }
    
}
