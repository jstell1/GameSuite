package gamesuite.chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class CheckCastleConst extends Constraint implements Updatable, ConstDependent{

    private GameBoardImpl board;
    private BoardGameState gameState;
    private Set<BoardGamePiece> hasMoved;
    private int[][] attackVectors;
    private boolean p1KingMoved;
    private boolean p2KingMoved;
    private Constraint validateAttack;
    private final String[] dependencies = {"validChessAttack"};

    public CheckCastleConst() {
        super("checkCastle");
        this.hasMoved = new HashSet<>();
        this.p1KingMoved = false;
        this.p2KingMoved = false;
    }

    public CheckCastleConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "checkCastle";
        this.hasMoved = new HashSet<>();
        this.p1KingMoved = false;
        this.p2KingMoved = false;
    }

    @Override
    public boolean checkMove(JsonNode move) {

        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CoordPairImpl start = this.board.getBoardPos(sX, sY);
        BoardGamePiece king = start.getPiece();
        BoardGamePiece end = this.board.getBoardPos(eX, eY).getPiece();

        int tmpY = sY - 2;
        int tmpY2 = sY + 2;

        if(sX != eX && eY != tmpY || eY != tmpY2) {
            return false;
        }

        if(this.hasMoved.contains(king)) {
            return false;
        }

        if(end != null) {
            return false;
        }

        String team = king.getTeam();
        int row = 0;
        String[] teams = this.gameState.getTeamNames();

        if(team.equals(teams[1])) {
            row = 7;
        }
        
        int rookY = 0;
        if(sY == tmpY2){
            tmpY = tmpY2;
            rookY = 7;
        }

        CoordPairImpl rookPos = this.board.getBoardPos(row, rookY);
        BoardGamePiece rook = rookPos.getPiece();
        if(!checkRook(rook,team) || this.hasMoved.contains(rook)) {
            return false;
        }


        //check pieces on back row
        List<CoordPairImpl> backRow = new ArrayList<>();

        if(rookY == 0) {
            backRow.add(this.board.getBoardPos(row, 1));
            backRow.add(this.board.getBoardPos(row, 2));

        } else {
            backRow.add(this.board.getBoardPos(row, 6));
            backRow.add(this.board.getBoardPos(row, 5));
            backRow.add(this.board.getBoardPos(row, 4));
        }

        for(CoordPairImpl pos : backRow) {
            if(pos.getPiece() != null) {
                return false;
            }
        }

        backRow.add(start);
        backRow.add(rookPos);

        int size = this.board.getSideLength();
        //check attacks on back row
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {

                for(CoordPairImpl pos : backRow) {
                    BoardGameMove m = new BoardGameMove(i, j, pos.getX(), pos.getY());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.valueToTree(m);
                    boolean validAttack = this.validateAttack.checkMove(node);

                    if(validAttack) {
                        return false;
                    }
                }

            }
        }

        return true;
    }

    private boolean checkRook(BoardGamePiece rook, String team) {
        return rook == null || rook.getType() != "R" || !rook.getTeam().equals(team);
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
    public void setExtraParams(JsonNode extraParams) {

        this.attackVectors = new int[extraParams.size()][];
        int i = 0;
        for(JsonNode node : extraParams) {
            int[] vect = new int[2];
            vect[0] = node.get("x").asInt();
            vect[1] = node.get("y").asInt();

            this.attackVectors[i] = vect;
            i++;
        }
    }

    @Override
    public void update(JsonNode update) throws IllegalArgumentException, JsonProcessingException {
       
        int x = update.get(0).asInt();
        int y = update.get(1).asInt();

        BoardGamePiece piece = this.board.getBoardPos(x, y).getPiece();
        this.hasMoved.add(piece);

    }

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.validateAttack = dependencies.get("validChessAttack");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }
}
