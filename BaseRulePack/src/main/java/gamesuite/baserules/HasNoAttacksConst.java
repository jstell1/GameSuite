package gamesuite.baserules;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGamePlayer;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class HasNoAttacksConst extends Constraint {

    GameBoardImpl board;
    BoardGameState gameState;

    public HasNoAttacksConst() {
        super("hasNoAttacks");
    }

    public HasNoAttacksConst(BoardGameState gameState, GameBoardImpl board) {
        super(gameState, board);
        this.name = "hasNoAttacks";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        String id = move.get("playerId").asText();
        BoardGamePlayer p = this.gameState.getPlayerById(id);
        int playerNum = p.getTurn();
        Set<CoordPairImpl> tmp;
        tmp = this.gameState.getJumps(playerNum);
        
        if(tmp.size() == 0) {
            return true;
        } 
        return false;
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
    
}
