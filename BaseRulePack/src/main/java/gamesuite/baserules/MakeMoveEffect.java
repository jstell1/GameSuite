package gamesuite.baserules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;

public class MakeMoveEffect extends Effect implements ConstDependent {

    private CheckersGameBoard board;
    private CheckersGameState gameState;
    private Constraint validAttack;
    static final String[] dependencies = {"validAttack"};

    public MakeMoveEffect() {
        super("makeMove");
    }

    public MakeMoveEffect(String name, GameState gameState) {
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

       // if(this.validAttack.checkMove(move)) {
        //    this.gameState.setLastJumped(true);
       // }

        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CheckersCoordPair pos = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);

        CheckersGamePiece piece = pos.getPiece();
        end.setPiece(piece);
        pos.setPiece(null);

        if(this.gameState.getChangedPos() == null) {
            this.gameState.resetChangedPos();
        }
        
        this.gameState.addChangedPos(pos);
        this.gameState.addChangedPos(end);
        this.gameState.setFurtherJumps(null);
        //((ObjectNode) move).put("startX", eX);
        //((ObjectNode) move).put("startY", eY);
    }

    @Override
    public void addDependencies(Map<String, Constraint> arg0) {
        this.validAttack = arg0.get("validAttack");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(dependencies);
    }
    
}
