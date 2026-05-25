package checkers.model.Rules;

import gamesuite.core.model.GameState;

public abstract class Effect {
    protected String name;
    protected GameState gameState;

    public Effect(String name, GameState gameState) {
        this.name = name; this.gameState = gameState;
    }
}
