package checkers.model.Rules;

import java.util.Set;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;
import gamesuite.core.model.Move;

public class MustAttackConst extends Constraint {

    public MustAttackConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "mustAttack";
    }

    @Override
    public Result checkMove(CheckersMove move) {
        CheckersPlayer p = this.gameState.getPlayerById(move.getPlayerId());
        int playerNum = p.getTurn();
        Set<CheckersCoordPair> tmp;
        tmp = this.gameState.getJumps(playerNum);
        return new Result(tmp != null);
    }
    
}
