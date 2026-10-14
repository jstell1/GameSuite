package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class NotFurtherAttackConst extends Constraint {

    private GameBoardImpl board;
    private BoardGameState gameState;

    public NotFurtherAttackConst() {
        super("notFurtherAttack");
    }

    public NotFurtherAttackConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        this.name = "notFurtherAttack";
        //TODO Auto-generated constructor stub
    }

    @Override
    public void setGameState(JsonNode game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setBoard(JsonNode board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setGameState(GameState game) {
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public boolean checkMove(JsonNode move) {
        CoordPairImpl currAttacks = this.gameState.getFurtherJumps();

        if(currAttacks == null) {
            return true;
        }

        int x = move.get("startX").asInt();
        int y = move.get("startY").asInt();

        CoordPairImpl pos = this.board.getBoardPos(x, y);

        if(pos == currAttacks) {
            return true;
        }
        return false;
    }
    
}
