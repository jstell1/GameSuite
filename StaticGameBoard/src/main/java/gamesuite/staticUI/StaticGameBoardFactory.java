package gamesuite.staticUI;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import gamesuite.staticUI.model.Piece;

public class StaticGameBoardFactory implements GameBoardFactory {

    @Override
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        CoordPairImpl[][] board = mapper.treeToValue(boardJson, CoordPairImpl[][].class);

        Piece[][] pBoard = new Piece[board.length][board[0].length];

        for(int i = 0; i < pBoard.length; i++) {
            for(int j = 0; j < pBoard[0].length; j++) {
                CoordPairImpl pos = board[i][j];
                BoardGamePiece piece = pos.getPiece();

                if(piece == null) {
                    pBoard[i][j] = null;
                } else {
                    Piece newPiece = new Piece(
                                piece.getName(),
                                piece.getTeam(),
                                piece.getType(),
                                piece.getVal()
                            );
                    pBoard[i][j] = newPiece;
                }
            }
        }



        Map<String,ImageIcon> map1 = new HashMap<>();
        Map<String,ImageIcon> map2 = new HashMap<>();

        
        BoardGameState game = mapper.treeToValue(gameJson, BoardGameState.class);
        String gameName = game.getGameName();
        String tmp = "boardDef/" + gameName + "Board.json";
        JsonNode file = mapper.readTree(getClass().getClassLoader().getResourceAsStream(tmp));
        URL boardURL = getClass().getClassLoader().getResource(file.get("board").asText());
        ImageIcon boardIcon = new ImageIcon(boardURL);

        JsonNode team1 = file.get("team1");
        JsonNode team2 = file.get("team2");

        for(JsonNode piece : team1) {
            String name = piece.get("name").asText();
            URL path = getClass().getClassLoader().getResource(piece.get("path").asText());
            ImageIcon ico = new ImageIcon(path);
            map1.put(name, ico);
        }
        for(JsonNode piece : team2) {
            String name = piece.get("name").asText();
            URL path = getClass().getClassLoader().getResource(piece.get("path").asText());
            ImageIcon ico = new ImageIcon(path);
            map2.put(name, ico);
        }


        String teamNames[] = mapper.treeToValue(gameJson.get("teamNames"), String[].class);
        StaticGameBoard b = new StaticGameBoard(listener, pBoard, teamNames, game, map1, map2, boardIcon);
        //b.setTeamNames(teamNames);
        //b.setBoard();
        return b;
    }

}
