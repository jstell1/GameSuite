package gamesuite.core.model.rules;

import gamesuite.core.model.GameState;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;

public abstract class Constraint {
    protected String name;
    private GameState mainGameState;
    private GameBoard mainBoard;
    //protected int turnFactor;
    private int[][] vector;
    protected JsonNode extraParams;
    protected GameManager topGm;

    public Constraint(String name) {
       // this.turnFactor = -1;
        this.mainGameState = null;
        this.mainBoard = null;
        this.name = name;
    }
    
    public Constraint(GameState gameState, GameBoard board) {
      //  this.turnFactor = -1;
        this.mainGameState = gameState;
        this.mainBoard = board;
    }

      public void setGameManager(GameManager gm) {
        this.topGm = gm;
    }

     public void setExtraParams(JsonNode extra) { this.extraParams = extra; }
    
   //j;kj;lkj;lkj
    public void setVector(int[][] vector) { this.vector = vector; }

    public String getName() { return this.name; }

    public abstract void setGameState(JsonNode game);

    public abstract void setBoard(JsonNode board);

    public abstract void setGameState(GameState game);

    public abstract void setBoard(GameBoard board);
    
    public abstract boolean checkMove(JsonNode move);

  
    
}
