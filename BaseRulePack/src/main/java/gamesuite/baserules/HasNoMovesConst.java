package gamesuite.baserules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;

public class HasNoMovesConst extends Constraint implements ConstDependent {

    private CheckersGameState gameState;
    private CheckersGameBoard board;
    private Constraint inBounds;
    private Constraint furtherAttacks;
    private Constraint furtherMoves; 
    private static final String[] dependencies = {"furtherAttacks", "furtherMoves"};
    public HasNoMovesConst() {
        super("hasNoMoves");
        this.inBounds = new InBoundsConst();
    }

    public HasNoMovesConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "hasNoMoves";
        this.inBounds = new InBoundsConst();
    }

    @Override
    public boolean checkMove(JsonNode move) {
        
        String[] teams = this.gameState.getTeamNames();
        int turn = this.gameState.getTurn();
        String team = teams[(turn % 2 + 1) - 1];

        if(hasValidMoves(team, move)) {
            return false;
        }

        return true;
    }

    private boolean hasValidMoves(String team, JsonNode move) {
        for(int i = 0; i < this.board.getSideLength(); i++) {

            if(rowHasValidMoves(i, 0, team, move)) {
               return true;
            }
        }
        return false;
    }

     private boolean rowHasValidMoves(int row, int start, String team, JsonNode move) {
        String[] teamNames = this.gameState.getTeamNames();
        int fact = -1;
        if(team.equals(teamNames[1])) {
            fact = 1;
        }
        for(int j = start; j < this.board.getSideLength(); j++) {
            CheckersCoordPair pos = this.board.getBoardPos(row, j);
            CheckersGamePiece piece = pos.getPiece();

            if(piece == null) {
                continue;
            }

            if(!piece.getTeam().equals(team)) {
                continue;
            }

            CheckersMove cMove = new CheckersMove();
            cMove.setEndX(pos.getX());
            cMove.setEndY(pos.getY());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.valueToTree(cMove);
            
            if(this.furtherMoves.checkMove(node)) {
                return true;
            }

            if(this.furtherAttacks.checkMove(node)) {
                return true;
            }
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

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.furtherAttacks = dependencies.get("furtherAttacks");
        this.furtherMoves = dependencies.get("furtherMoves");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(dependencies, dependencies.length));
    }
    
}
