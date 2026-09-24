package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Result;

public class FurtherAttacksConst extends Constraint {

    private CheckersGameState gameState;
    private CheckersGameBoard board;
    
    public FurtherAttacksConst() {
        super("furtherAttacks");
    }

    public FurtherAttacksConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "furtherAttacks";
    }

    @Override
    public boolean checkMove(JsonNode move) {

       // this.gameState.setFurtherJumps(null);
        
        int tmpx = move.get("startX").asInt();
        int tmpy = move.get("startY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(tmpx, tmpy);
        if(pos == null || pos.getPiece() == null)
            return false;
        CheckersGamePiece piece = pos.getPiece();
        String team = piece.getTeam();
        int startX = pos.getX();
        int startY = pos.getY();

        if(!this.board.isValidPos(startX, startY) || piece == null)
            return false;

        int[][] validJumps = piece.getValidJumps();
        int x;
        int y;
        int jumpX;
        int jumpY;
        String[] teamNames = this.gameState.getTeamNames();
        for(int i = 0; i < validJumps.length; i++) {
            
            if(team.equals(teamNames[0])) {
                x = startX + validJumps[i][0] * -1;
                y = startY + validJumps[i][1] * -1;
            } else {
                x = startX + validJumps[i][0];
                y = startY + validJumps[i][1];
            }
            
            if(this.board.isValidPos(x, y)) {//inBounds(this.board.getBoardPos(x, y))) {
                jumpX = (startX + x) >> 1;
                jumpY = (startY + y) >> 1;

                CheckersCoordPair jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CheckersCoordPair end = this.board.getBoardPos(x, y);
                CheckersGamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                CheckersGamePiece jumpPiece = jumpPos.getPiece();

                if(jumpPiece != null && !team.equals(jumpPiece.getTeam()) && endPiece == null) {
                    return true;
                }
            }
        }
        return false;
        // if(this.gameState.getFurtherJumps() != null) {
        //     return new Result(true);
        // }
        // return new Result(false);
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
