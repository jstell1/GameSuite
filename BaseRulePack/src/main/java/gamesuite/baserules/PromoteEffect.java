package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.control.BoardGameManager;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class PromoteEffect extends Effect {

    private GameBoardImpl board;
    private BoardGameState gameState;
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
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public void updateState(JsonNode move) {
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(x, y);
        BoardGamePiece piece = pos.getPiece(); 
        String team = piece.getTeam();
        BoardGamePiece newPiece = this.gm.buildPiece(this.promoteName, team);
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
