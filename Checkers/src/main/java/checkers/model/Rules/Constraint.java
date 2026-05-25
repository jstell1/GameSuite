package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;

public abstract class Constraint {
    protected String name;
    protected CheckersGameState gameState;
    protected CheckersGameBoard board;
    protected int turnFactor;

    public Constraint(CheckersGameState gameState, CheckersGameBoard board) {
        this.turnFactor = -1;
        this.gameState = gameState;
        this.board = board;
    }

    public abstract Result checkMove(CheckersMove move);

    protected boolean inBounds(CheckersCoordPair pos) {
        int x = pos.getX();
        int y = pos.getY();
        int length = this.board.getSideLength();

         if(x >= 0 && x < length && y >= 0 && y < length) 
            if(x % 2 == 0 && y % 2 != 0)
                return true;
            else if(x % 2 != 0 && y % 2 == 0)
                return true;
        return false;        

    }
}
