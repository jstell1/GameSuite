package checkers.model.Rules;

import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public class TurnCheckConst extends Constraint {
    
    public TurnCheckConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "turnCheck";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        int gameTurn = this.gameState.getTurn();
        CheckersPlayer currTurn = 
                (CheckersPlayer)this.gameState.getPlayer(gameTurn);
        CheckersPlayer inPlayer = 
                this.gameState.getPlayerById(move.getPlayerId());

        if(currTurn == inPlayer) {
            return new Result(true);
        }
        return new Result(false);
        
    }
    
}
