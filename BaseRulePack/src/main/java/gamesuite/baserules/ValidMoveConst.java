package gamesuite.baserules;

import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;

public class ValidMoveConst extends Constraint {

    private BoardGameState gameState;
    private GameBoardImpl board;

    public ValidMoveConst(BoardGameState gameState, GameBoardImpl board) {
        super(gameState, board);
        this.name = "validMove";
    }

    public ValidMoveConst() {
        super("validMove");
    }

    @Override
    public boolean checkMove(JsonNode move) {

        int startX = move.get("startX").asInt();
        int startY = move.get("startY").asInt();
        int endY = move.get("endY").asInt();
        int endX = move.get("endX").asInt();


          if(!this.board.isValidPos(startX, startY) || !this.board.isValidPos(endX, endY)) {
            return false;
        }
        
        CoordPairImpl pos = this.board.getBoardPos(startX, startY);
        BoardGamePiece p = pos.getPiece();

        if(p == null) {
            return false;
        }
        
        int[][] moves = p.getValidMoves();
        String team = p.getTeam();
        String[] teamNames = this.gameState.getTeamNames();
        int turnFactor = this.gameState.getTurnFactor();
        int turn = this.gameState.getTurn();

        if(!teamNames[turn - 1].equals(team)) {
            return false;
        }
        
        for(int[] pair : moves) {
            int x = startX + pair[0] * turnFactor;
            int y = startY + pair[1] * turnFactor;

            if(endX == x && endY == y)
                return true;
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
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

   
    
}
