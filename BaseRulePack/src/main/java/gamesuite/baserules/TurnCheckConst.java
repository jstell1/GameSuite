package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class TurnCheckConst extends Constraint {

    private GameBoardImpl board;
    private BoardGameState gameState;

    public TurnCheckConst() {
        super("turnCheck");
    }

    public TurnCheckConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "turnCheck";
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
        
        int turn = this.gameState.getTurn();
        String[] teams = this.gameState.getTeamNames();
        String curr = teams[turn - 1];
        int x = move.get("startX").asInt();
        int y = move.get("startY").asInt();
        if(!this.board.isValidPos(x,y)) {
            return false;
        }
        CoordPairImpl pos = this.board.getBoardPos(x, y);
        BoardGamePiece piece = pos.getPiece();

        if(piece == null) {
            return false;    
        }
        
        String team = piece.getTeam();

        if(team.equals(curr)) {
            return true;
        }
        
        return false;
    }
    
}
