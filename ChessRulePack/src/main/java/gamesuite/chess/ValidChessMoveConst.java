package gamesuite.chess;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Updatable;
import gamesuite.core.model.rules.Updater;

import java.util.Map;

public class ValidChessMoveConst extends Constraint {

    private BoardGameState gameState;
    private GameBoardImpl board;
    private Map<String, Updatable> updatees;

    public ValidChessMoveConst() {
        super("validChessMove");
    }

    public ValidChessMoveConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "validChessMove";
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

        CoordPairImpl start = this.board.getBoardPos(sX, sY);
        CoordPairImpl end = this.board.getBoardPos(eX, eY);

        if(end.getPiece() != null) {
            return false;
        }
        BoardGamePiece piece = start.getPiece();
        String team = piece.getTeam();
        String[] teams = this.gameState.getTeamNames();

        int turnFactor = -1;

        if(teams[1].equals(team)) {
            turnFactor = 1;
        }

        int[][] validMoves = piece.getValidMoves();

        for(int[] vect : validMoves) {
            int tmpX = sX + vect[0] * turnFactor;
            int tmpY = sY + vect[1] * turnFactor;

            if(!this.board.isValidPos(tmpX, tmpY)) {
                continue;
            }

            BoardGamePiece targ = this.board.getBoardPos(tmpX, tmpY).getPiece();

            if(targ != null) {
                continue;
            }

            if(tmpX == eX && tmpY == eY) {
                return true;
            }
        }

        return false;
    }
}
