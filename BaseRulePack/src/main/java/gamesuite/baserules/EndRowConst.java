package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class EndRowConst extends Constraint {

    private CheckersGameBoard board;
    private CheckersGameState gameState;

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
        this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

    @Override
    public boolean checkMove(JsonNode move) {
        
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(x, y);

         CheckersGamePiece piece = pos.getPiece();
        String[] names = this.gameState.getTeamNames();
        boolean check = piece.getTeam().equals(names[0]) && pos.getX() == this.board.getSideLength() - 1;
        check = check || piece.getTeam().equals(names[1]) && pos.getX() == 0; 

        return check;
    }
    
}
