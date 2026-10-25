package gamesuite.chess;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
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
import gamesuite.core.model.rules.Effect;
import gamesuite.core.model.rules.Updatable;
import gamesuite.core.model.rules.Updater;

public class ChessMakeMoveEffect extends Effect implements Updater {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private List<Updatable> updatees;

    public ChessMakeMoveEffect() {
        super("chessMakeMove");
    }

    public ChessMakeMoveEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "chessMakeMove";
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

        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CoordPairImpl start = this.board.getBoardPos(sX, sY);
        CoordPairImpl end = this.board.getBoardPos(eX, eY);
        BoardGamePiece piece = start.getPiece();

        end.setPiece(piece);
        start.setPiece(null);      
        this.gameState.addChangedPos(start);
        this.gameState.addChangedPos(end);  

        String type = piece.getType();
        String team = piece.getTeam();
        String[] teams = this.gameState.getTeamNames();
        int turn = 1;
        
        if(teams[1].equals(team)) {
            turn = 2;
        }

        int[] vect = new int[2];
        vect[0] = eX; vect[1] = eY;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode vectNode = mapper.valueToTree(vect);
        
        try {
            if(type.equals("K")) {
                Updatable updatee = this.updatees.get(1);
                
                ObjectNode node = mapper.createObjectNode();
                node.put("king", turn);
                node.set("vect", vectNode);
                
                updatee.update(node);
            }

            if(type.equals("K") || type.equals("R")) {

                Updatable updatee = this.updatees.get(0);
                
                    updatee.update(vectNode);
                

            }

            if(type.equals("P")) {
                Updatable updatee = this.updatees.get(3);
                updatee.update(vectNode);
            }

            Updatable updatee = this.updatees.get(2);
            updatee.update(move);
                
                

        } catch (JsonProcessingException | IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalArgumentException("error adding elements to ObjectNode");
        }
            
            

        

    }

    @Override
    public void addUpdatables(List<Updatable> updatees) {
        this.updatees = updatees;
    }
    
}
