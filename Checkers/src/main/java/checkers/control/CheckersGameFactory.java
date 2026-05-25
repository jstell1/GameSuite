package checkers.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersPlayer;
import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;

public class CheckersGameFactory extends GameManagerFactory {

    private static final boolean multiGame = true;

    @Override
    public GameManager createGame(String playerName, String playerId, JsonNode gameDef) {
        
        
        ObjectMapper mapper = new ObjectMapper();
        int sideLength = gameDef.get("board")
                                .get("dimensions")
                                .get("height").asInt();

        CheckersGameBoard board = new CheckersGameBoard(sideLength);

        JsonNode initVector = gameDef
                            .get("board")
                            .get("initState");

        for(JsonNode node : initVector) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            int pNum = node.get("piece").get("player").asInt();
            CheckersGamePiece piece;

            if(pNum == 1) {

                piece = new CheckersGamePiece("B", "C", 1);
            } else {
                piece = new CheckersGamePiece("R", "C", 1);
            }

            board.setBoardPos(x, y, piece);
        }

        CheckersPlayer player = new CheckersPlayer(playerName, 0);
        player.setUserId(playerId);
        CheckersGameManager gm = new CheckersGameManager(board, player);

        return gm;
    }

    @Override
    public boolean isMultiGame() {
        return multiGame;
    }
    
}
