package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.control.BoardGameManager;
import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class PromoteEffect extends Effect {

    private CheckersGameBoard board;
    private CheckersGameState gameState;
    private BoardGameManager gm;
    private String promoteName;

    public PromoteEffect() {
        super("promote");
    }

    public PromoteEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
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
    public void updateState(JsonNode move) {
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(x, y);
        CheckersGamePiece piece = pos.getPiece(); 
        String type = piece.getType();
        CheckersGamePiece newPiece = this.gm.buildPiece(this.promoteName, type);
        pos.setPiece(newPiece);
    }

    @Override 
    public void setExtraParams(JsonNode extras) {
        this.promoteName = extras.get("piece")
                                .get("name").asText();
    }
    

    @Override 
    public void setGameManager(GameManager gm) {
        this.gm = (BoardGameManager) gm;
    }
}
