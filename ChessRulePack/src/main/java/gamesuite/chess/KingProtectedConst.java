package gamesuite.chess;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.boardgame.model.BoardGameMove;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Updatable;

public class KingProtectedConst extends Constraint implements ConstDependent, Updatable {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private CoordPairImpl p1King;
    private CoordPairImpl p2King;
    private int[][] attackVectors;
    private Constraint validateAttack;
    private final String[] dependencies = {"validateChessAttack"};
    

    public KingProtectedConst() {
        super("kingProtected");
    }

    public KingProtectedConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "kingProtected";
    }

    @Override
    public boolean checkMove(JsonNode move) {
        
        int turn = this.gameState.getTurn();

        boolean inCheck = false;
        if(turn == 1) {
            inCheck = checkKing(p1King);
        } else {
            inCheck = checkKing(p2King);
        }

        if(!inCheck) {
            return true;
        }

        boolean safe = verifyMove(move);

        return safe;
    }

    protected boolean checkKing(CoordPairImpl king) {
        int kX = king.getX(); int kY = king.getY();
        for(int[] vect : this.attackVectors) {
             int xDirect = vect[0]; int yDirect = vect[1];
             int sX = kX + xDirect;
             int sY = kY + yDirect;
             int rise = kY - sY;
             int run = kX - sX;

             if(!this.board.isValidPos(sX, sY)) {
                continue;
             }
             
             BoardGameMove move = new BoardGameMove(sX, sY, kX, kY);
             ObjectMapper mapper = new ObjectMapper();
             JsonNode moveNode = mapper.valueToTree(move);

             boolean exact = false;
             if(xDirect != 1 || xDirect != -1) {
                exact = true;
             }
             if(yDirect != 1 || yDirect != -1) {
                exact = true;
             }

             if(exact) {
                boolean valid = this.validateAttack.checkMove(moveNode);

                if(valid) {
                    return true;
                }
             } else {
                BoardGamePiece piece = null;
                while(this.board.isValidPos(sX, sY) && piece == null) {
                    piece = this.board.getBoardPos(sX, sY).getPiece();

                    if(piece != null) {
                        boolean valid = this.validateAttack.checkMove(moveNode);
                        if(valid) {
                            return true;
                        }
                    }

                    if(rise == 0) {
                        sX += xDirect;
                    } else if(run == 0) {
                        sY += yDirect;
                    } else {
                        sX += xDirect;
                        sY += yDirect;
                    }

                }
             }
        }

        return false;
    }

    protected boolean verifyMove(JsonNode move) {



        return false;
    }

    @Override 
    public void setExtraParams(JsonNode extraParams) {
        JsonNode king1Node = extraParams.get("king1");
        JsonNode king2Node = extraParams.get("king2");
        JsonNode attackVectors = extraParams.get("attackVectors");
        this.attackVectors = new int[attackVectors.size()][];

        int i = 0;
        for(JsonNode node : attackVectors) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();

            int[] vect = new int[2];
            vect[0] = x;
            vect[1] = y;

            this.attackVectors[i] = vect;
            i++;
        }
        int x1 = king1Node.get("x").asInt();
        int y1 = king1Node.get("y").asInt();
        int x2 = king2Node.get("x").asInt();
        int y2 = king2Node.get("y").asInt();

        this.p1King = this.board.getBoardPos(x1, y1);
        this.p2King = this.board.getBoardPos(x2, y2);
     }

    @Override
    public void setBoard(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setBoard(GameBoard arg0) {
        this.board = (GameBoardImpl) arg0;
    }

    @Override
    public void setGameState(JsonNode arg0) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setGameState(GameState arg0) {
        this.gameState = (BoardGameState) arg0;
    }

    @Override
    public void addDependencies(Map<String, Constraint> arg0) {
        this.validateAttack = arg0.get("validateChessAttack");
    }

    @Override
    public List<String> getDependencyList() {
        List<String> list = Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
        return list;
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        int num = update.get("king").asInt();
        int[] vect = mapper.treeToValue(update.get("vect"), int[].class);

        if(num == 1) {
            this.p1King = this.board.getBoardPos(vect[0], vect[1]);
        } else {
            this.p2King = this.board.getBoardPos(vect[0], vect[1]);
        }
    }

    
    
}
