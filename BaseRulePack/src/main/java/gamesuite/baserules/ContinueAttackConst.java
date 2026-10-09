package gamesuite.baserules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;

public class ContinueAttackConst extends Constraint implements ConstDependent {

    private CheckersGameBoard board;
    private CheckersGameState gameState;
    private Constraint validAttack;
    private Constraint furtherAttacks;
    static final String[] dependencies = {"furtherAttacks", "validAttack"};   

    public ContinueAttackConst() {
        super("continueAttack");
    }

    public ContinueAttackConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "continueAttack";
    }

    @Override
    public boolean checkMove(JsonNode arg0) {
        if(!this.validAttack.checkMove(arg0)) {
            return false;
        }

        return this.furtherAttacks.checkMove(arg0);
    }

    @Override
    public void setBoard(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setBoard(GameBoard arg0) {
        // TODO Auto-generated method stub
        this.board = (CheckersGameBoard) arg0;
    }

    @Override
    public void setGameState(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setGameState(GameState arg0) {
        // TODO Auto-generated method stub
        this.gameState = (CheckersGameState) arg0;
    }

    @Override
    public void addDependencies(Map<String, Constraint> arg0) {
        
        this.validAttack = arg0.get("validAttack");
        this.furtherAttacks = arg0.get("furtherAttacks");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(dependencies);
    }
    
}
