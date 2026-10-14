package gamesuite.staticUI;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public GameBoardUI createGameBoard(JsonNode boardJson, JsonNode gameJson, UIListener listener) throws JsonProcessingException {
        
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
        BoardGameState game = mapper.treeToValue(gameJson, BoardGameState.class);
        String teamNames[] = mapper.treeToValue(gameJson.get("teamNames"), String[].class);
        StaticGameBoard b = new StaticGameBoard(listener, pBoard, teamNames, game);
        //b.setTeamNames(teamNames);
        //b.setBoard();
        return b;
    }

}
