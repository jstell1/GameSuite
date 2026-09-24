package gamesuite.baserules;

import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.rules.Constraint;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;

public class ValidMoveConst extends Constraint {

    private CheckersGameState gameState;
    private CheckersGameBoard board;

    public ValidMoveConst(CheckersGameState gameState, CheckersGameBoard board) {
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
        
        CheckersCoordPair pos = this.board.getBoardPos(startX, startY);
        //CheckersCoordPair end = this.board.getBoardPos(endX, endY);
        CheckersGamePiece p = pos.getPiece();

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
        this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

   
    
}
