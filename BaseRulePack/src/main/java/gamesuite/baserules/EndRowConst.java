package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class EndRowConst extends Constraint {

    private GameBoardImpl board;
    private BoardGameState gameState;

    public EndRowConst() {
        super("endRow");
    }

    public EndRowConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        this.name = "endRow";
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
        
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(x, y);

         BoardGamePiece piece = pos.getPiece();
        String[] names = this.gameState.getTeamNames();
        boolean check = piece.getTeam().equals(names[0]) && pos.getX() == this.board.getSideLength() - 1;
        check = check || piece.getTeam().equals(names[1]) && pos.getX() == 0; 

        return check;
    }
    
}
