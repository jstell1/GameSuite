package gamesuite.chess;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Updatable;

public class FirstMoveConst extends Constraint implements Updatable {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private Set<BoardGamePiece> madeFirstMove;

    public FirstMoveConst() {
        super("firstMove");
        this.madeFirstMove = new HashSet<>();
    }

    public FirstMoveConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "firstMove";
        this.madeFirstMove = new HashSet<>();
    }

    @Override
    public boolean checkMove(JsonNode move) {
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        if(!this.board.isValidPos(eX, eY) || !this.board.isValidPos(sX, sY)) {
            return false;
        }

        BoardGamePiece start = this.board.getBoardPos(sX, sY).getPiece();

        if(start == null) {
            return false;
        }

        int turnFactor = this.gameState.getTurnFactor();
        
        int tmpX = sX - 2 * turnFactor;
        
        if(tmpX != eX || eY != sY) {
            return true;
        }
        
        if(this.madeFirstMove.contains(start)) {
            return false;
        }
        
        tmpX = eX + turnFactor;
        BoardGamePiece inFront = this.board.getBoardPos(tmpX, sY).getPiece();

        if(inFront != null) {
            return false;
        }

        this.madeFirstMove.add(start);
        return true;
    }

    @Override
    public void setBoard(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public void setGameState(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setGameState(GameState game) {
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {
        int x = update.get(0).asInt();
        int y = update.get(1).asInt();

        BoardGamePiece piece = this.board.getBoardPos(x,y).getPiece();

        this.madeFirstMove.add(piece);

    }
}
