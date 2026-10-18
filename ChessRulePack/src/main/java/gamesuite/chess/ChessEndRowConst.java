package gamesuite.chess;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Updatable;
import gamesuite.core.model.rules.Updater;

public class ChessEndRowConst extends Constraint implements Updatable {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private boolean mustChoosePromote;
    
    public ChessEndRowConst() {
        super("chessEndRow");
        this.mustChoosePromote = false;
    }

    public ChessEndRowConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "chessEndRow";
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

        if(move.has("promote")) {
            this.mustChoosePromote = false;
            return true;
        }

        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(eX, eY);
        BoardGamePiece piece = pos.getPiece();

        if(piece == null) {
            return false;
        }
        String team = piece.getTeam();

        if(!piece.getType().equals("P")) {
            return false;
        }

        String[] teams = this.gameState.getTeamNames();

        if(eX != 0 && eX != 7) {
            return false;
        }

        if((eX == 0 && !team.equals(teams[1])) || (eX == 7 && !team.equals(teams[0]))) {
            return false;
        } 

        if(!this.mustChoosePromote) {

            this.mustChoosePromote = true;

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode choices = mapper.createObjectNode();

            String[] ch = {"Q", "R", "B", "N"};
            ArrayNode arr = mapper.createArrayNode();

            for(String c : ch) {
                arr.add(c);
            }
            //choices.set("choices",arr);
            this.topGm.sendPromotionRequest(move, arr);

        }

        return false;
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {
    
        this.mustChoosePromote = false;
    }
    
}
