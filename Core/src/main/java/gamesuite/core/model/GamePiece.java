package gamesuite.core.model;

import java.util.List;

import gamesuite.core.model.rules.Action;
public interface GamePiece {



    public GamePiece copy();

    public String getName();

    public String getType();

    public int getVal();
    
    public String toString();

    public int[][] getValidMoves();

    public int[][] getValidJumps();

    public List<Action> getAttackRules();

    public List<Action> getMoveRules();

    public boolean isKing();
}
