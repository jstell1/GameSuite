package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class InBoundsConst extends Constraint {

    CheckersGameBoard board;
    CheckersGameState gameState;

    public InBoundsConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        this.name = "inBounds";
    }

    public InBoundsConst() {
        super("inBounds");
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
    public boolean checkMove(JsonNode move) {
        
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);

        boolean check = isValidPos(pos);
        check = isValidPos(end);
        
        return check;
    }

      private boolean isValidPos(CheckersCoordPair pos) {
        if(pos == null)
            return false;
        int length = this.board.getSideLength();
        int x = pos.getX();
        int y = pos.getY();
        
        if(x >= 0 && x < length && y >= 0 && y < length) 
            if(x % 2 == 0 && y % 2 != 0)
                return true;
            else if(x % 2 != 0 && y % 2 == 0)
                return true;
        return false;
    }
    
}
