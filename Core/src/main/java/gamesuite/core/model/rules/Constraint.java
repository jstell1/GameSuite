package gamesuite.core.model.rules;

import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;

public abstract class Constraint {
    protected String name;
    protected GameState mainGameState;
    protected GameBoard mainBoard;
    protected int turnFactor;
    protected int[][] vector;

    public Constraint(String name) {
        this.turnFactor = -1;
        this.mainGameState = null;
        this.mainBoard = null;
        this.name = name;
    }
    
    public Constraint(GameState gameState, GameBoard board) {
        this.turnFactor = -1;
        this.mainGameState = gameState;
        this.mainBoard = board;
    }
    
   //j;kj;lkj;lkj
    public void setVector(int[][] vector) { this.vector = vector; }

    public String getName() { return this.name; }

    public abstract void setGameState(JsonNode game);

    public abstract void setBoard(JsonNode board);

    public abstract void setGameState(GameState game);

    public abstract void setBoard(GameBoard board);
    
    public abstract boolean checkMove(JsonNode move);

    //protected abstract boolean inBounds(JsonNode pos);
      /* 
        int x = pos.getX();
        int y = pos.getY();
        int length = this.board.getSideLength();

         if(x >= 0 && x < length && y >= 0 && y < length) 
            if(x % 2 == 0 && y % 2 != 0)
                return true;
            else if(x % 2 != 0 && y % 2 == 0)
                return true;
        return false;        
        */
    
}
