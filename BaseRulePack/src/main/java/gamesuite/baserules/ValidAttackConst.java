package gamesuite.baserules;


import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class ValidAttackConst extends Constraint {

    private BoardGameState gameState;
    private GameBoardImpl board;

    public ValidAttackConst(BoardGameState gameState, GameBoardImpl board) {
        super(gameState, board);
        this.name = "validAttack";
    }

    public ValidAttackConst() {
        super("validAttack");
    }

    @Override
    public boolean checkMove(JsonNode move) {
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();
        
        if(!this.board.isValidPos(sX, sY) || !this.board.isValidPos(eX, eY)) {
            return false;
        }
        
        int turnFactor = this.gameState.getTurnFactor();
        String[] teamNames = this.gameState.getTeamNames();
        int turn = this.gameState.getTurn();
        CoordPairImpl start = this.board.getBoardPos(sX, sY);
        CoordPairImpl end = this.board.getBoardPos(eX, eY);

        BoardGamePiece piece = start.getPiece();
         String team = piece.getTeam();

        if(!teamNames[turn - 1].equals(team)) {
            return false;
        }
        
        
        boolean checkJump = false;
        int[][] validJumps = piece.getValidJumps();
        for(int[] pair : validJumps) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;
            
            if(end.getX() == x && end.getY() == y) {
                checkJump = true;
                break;
            }
            
        }

        if(!checkJump) {
            return false;
        }        
        
        int midX = (eX + sX) >> 1;
        int midY = (eY + sY) >> 1;

        CoordPairImpl attackCoord = this.board.getBoardPos(midX, midY);
        BoardGamePiece p = attackCoord.getPiece();
        if(p != null && !p.getTeam().equals(team)) {
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
