package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class FurtherAttacksConst extends Constraint {

    private BoardGameState gameState;
    private GameBoardImpl board;
    
    public FurtherAttacksConst() {
        super("furtherAttacks");
    }

    public FurtherAttacksConst(BoardGameState gameState, GameBoardImpl board) {
        super(gameState, board);
        this.name = "furtherAttacks";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        
        int tmpx = move.get("endX").asInt();
        int tmpy = move.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(tmpx, tmpy);
        if(pos == null || pos.getPiece() == null)
            return false;
        BoardGamePiece piece = pos.getPiece();
        String team = piece.getTeam();
        int startX = pos.getX();
        int startY = pos.getY();

        if(!this.board.isValidPos(startX, startY) || piece == null)
            return false;

        int[][] validJumps = piece.getValidAttacks();
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
            
            if(this.board.isValidPos(x, y)) {
                jumpX = (startX + x) >> 1;
                jumpY = (startY + y) >> 1;

                CoordPairImpl jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CoordPairImpl end = this.board.getBoardPos(x, y);
                BoardGamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                BoardGamePiece jumpPiece = jumpPos.getPiece();

                if(jumpPiece != null && !team.equals(jumpPiece.getTeam()) && endPiece == null) {
                    return true;
                }
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
        this.gameState = (BoardGameState) game;
        
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }
}
