package gamesuite.chess;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class EnPassantEffect extends Effect {

    private GameBoardImpl board;
    private BoardGameState gameSate;

    public EnPassantEffect() {
        super("enPassant");
    }

    public EnPassantEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "enPassant";
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
        this.gameSate = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public void updateState(JsonNode move) {
        
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CoordPairImpl targ = null;

        if(eX < sX) {
            targ = this.board.getBoardPos(eX + 1, eY);
        } else {
            targ = this.board.getBoardPos(eX - 1, eY);
        }

        targ.setPiece(null);
        this.gameSate.addChangedPos(targ);

    }
    
}
