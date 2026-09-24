package gamesuite.baserules;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

//import gamesuite.core.model.Move;
import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.boardgame.model.CheckersPlayer;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Result;

public class HasNoAttacksConst extends Constraint {

    CheckersGameBoard board;
    CheckersGameState gameState;

    public HasNoAttacksConst() {
        super("hasNoAttacks");
    }

    public HasNoAttacksConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "hasNoAttacks";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        String id = move.get("playerId").asText();
        CheckersPlayer p = this.gameState.getPlayerById(id);
        int playerNum = p.getTurn();
        Set<CheckersCoordPair> tmp;
        tmp = this.gameState.getJumps(playerNum);
        
        if(tmp == null) {
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
       this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }
    
}
