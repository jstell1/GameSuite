package gamesuite.core.model.rules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;

public abstract class Effect {
    protected String name;
    protected GameState gameStateOther;
    protected JsonNode extraParams;
    protected GameManager topGm;

     public Effect(String name) {
        //this.turnFactor = -1;
        this.gameStateOther = null;
        //this.board = null;
        this.name = name;
    }

    public Effect(String name, GameState gameState) {
        this.name = name; this.gameStateOther = gameState;
    }

    
    public void setGameManager(GameManager gm) {
        this.topGm = gm;
    }

    public void setExtraParams(JsonNode extra) { this.extraParams = extra; }

    public String getName() { return this.name; }

    public abstract void setGameState(JsonNode game);

    public abstract void setBoard(JsonNode board);

    public abstract void setGameState(GameState game);

    public abstract void setBoard(GameBoard board);

    public abstract void updateState(JsonNode move);

}
