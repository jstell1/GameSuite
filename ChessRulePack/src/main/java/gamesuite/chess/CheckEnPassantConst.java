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
    private final String[] dependencies = {"noEmptyTarg"};
    private NoEmptyTargConst noEmpty;
    private CoordPairImpl tracked;

    public CheckEnPassantConst() {
        super("checkEnPassant");
    }

    public CheckEnPassantConst(GameState gameState, GameBoard board) {

        super(gameState, board);
        this.name = "checkEnPassant";
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

        CoordPairImpl pos = this.board.getBoardPos(sX,sY);
        BoardGamePiece attacker = pos.getPiece();
        CoordPairImpl targSquare = this.board.getBoardPos(eX,eY);

        if(targSquare != this.tracked) {
            return false;
        }
        String team = attacker.getTeam();
        String[] teams = this.gameState.getTeamNames();

        int turnFactor = -1;

        if(team.equals(teams[1])) {
            turnFactor = 1;
        }

        CoordPairImpl attacked = this.board.getBoardPos(eX + turnFactor, eY);

        if(attacked.getPiece() != null && attacked.getPiece().getTeam().equals(attacker.getTeam())) {
            return false;
        }

        if(targSquare == this.tracked) {
            this.tracked = null;
            this.noEmpty.override();
            attacked.setPiece(null);
            return true;
        }
        return false;
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {
        this.tracked = null;

        int sX = update.get("startX").asInt();
        int sY = update.get("startY").asInt();
        int eX = update.get("endX").asInt();
        int eY = update.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(eX, eY);
        BoardGamePiece piece = pos.getPiece();

        if(piece.getType().equals("P")) {

            String team = piece.getTeam();
            String[] teams = this.gameState.getTeamNames();
            int turnFactor = -1;
            if(team.equals(teams[1])) {
                turnFactor = 1;
            }

            if(sY == eY && eX == (sX - 2*turnFactor)) {

                this.tracked = this.board.getBoardPos(eX + turnFactor, eY);
            }

        }
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
