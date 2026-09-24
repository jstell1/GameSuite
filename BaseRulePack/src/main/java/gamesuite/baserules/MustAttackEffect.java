package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;

public class MustAttackEffect extends Effect {

    private CheckersGameBoard board;
    private CheckersGameState gameState;

    public MustAttackEffect() {
        super("mustAttack");
    }

    public MustAttackEffect(String name, GameState gameState) {
        super(name, gameState);
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
        this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

    @Override
    public void updateState(JsonNode move) {
        
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(x, y);
        this.gameState.setFurtherJumps(pos);


    }

   
}
