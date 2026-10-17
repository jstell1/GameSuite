package gamesuite.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Updatable;

public class CheckEnPassantConst extends Constraint implements Updatable, ConstDependent {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private Set<BoardGamePiece> movedPawns;
    private Map<BoardGamePiece, List<BoardGamePiece>> missedPassant;
    private final String[] dependencies = {"noEmptyTarg"};
    private NoEmptyTargConst noEmpty;
    private Set<BoardGamePiece> passantDone;

    public CheckEnPassantConst() {
        super("checkEnPassant");
        this.movedPawns = new HashSet<>();
        this.missedPassant = new HashMap<>();
        this.passantDone = new HashSet<>();
    }

    public CheckEnPassantConst(GameState gameState, GameBoard board) {

        super(gameState, board);
        this.name = "checkEnPassant";
        this.movedPawns = new HashSet<>();
        this.missedPassant = new HashMap<>();
        this.passantDone = new HashSet<>();
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

    @Override
    public boolean checkMove(JsonNode move) {
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CoordPairImpl pos = this.board.getBoardPos(sX, sY);
        BoardGamePiece pawn = pos.getPiece();

        if(this.passantDone.contains(pawn)) {
            return false;
        }

        String team = pawn.getTeam();
        String[] teams = this.gameState.getTeamNames();   
        
        int turnFactor = -1;

        if(team.equals(teams[1])) {
            turnFactor = 1;
        }

        int x = eX + 1 * turnFactor;
        

        BoardGamePiece opp = null;
        
        opp = this.board.getBoardPos(x, eY).getPiece();
        if(opp == null || opp.getTeam().equals(team) || !opp.getType().equals("P")) {
            return false;
        }

        if(!this.missedPassant.containsKey(pawn) && this.movedPawns.contains(opp)) {
            List<BoardGamePiece> list = new ArrayList<>();
            list.add(opp);
            this.missedPassant.put(pawn, list);
            this.noEmpty.override();
            this.passantDone.add(pawn);
            return true;
        }
        
        if(this.missedPassant.get(pawn).contains(opp)) {
            return false;
        }

        if(this.movedPawns.contains(opp)) {
            this.missedPassant.get(pawn).add(opp);
            this.noEmpty.override();
            this.passantDone.add(pawn);
            return true;
        }


        return false;
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {


        int x = update.get(0).asInt();
        int y = update.get(1).asInt();

        BoardGamePiece piece = this.board.getBoardPos(x, y).getPiece();
        this.movedPawns.add(piece);
        
    }

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.noEmpty = (NoEmptyTargConst)dependencies.get("noEmptyTarg");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }

}
