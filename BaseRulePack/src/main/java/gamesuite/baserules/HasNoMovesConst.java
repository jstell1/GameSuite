package gamesuite.baserules;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Result;

public class HasNoMovesConst extends Constraint {

    //private FurtherAttacksConst;
    private CheckersGameState gameState;
    private CheckersGameBoard board;
    //private String[] names;
    private Constraint inBounds;
    private final Constraint furtherAttacks = new ValidAttackConst();
    private final Constraint furtherMoves = new ValidMoveConst();

    public HasNoMovesConst() {
        super("hasMoves");
        this.inBounds = new InBoundsConst();
    }

    public HasNoMovesConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "hasMoves";
        this.inBounds = new InBoundsConst();
    }

    @Override
    public boolean checkMove(JsonNode move) {
        
        String[] teams = this.gameState.getTeamNames();
        int turn = this.gameState.getTurn();
        String team = teams[turn - 1];

        if(hasValidMoves(team, move)) {
            return false;
        }

        return true;
    }

    private boolean hasValidMoves(String team, JsonNode move) {
        for(int i = 0; i < this.board.getSideLength(); i++) {

            if(rowHasValidMoves(i, 0, team, move)) {
               return true;
            }
        }
        return false;
    }

     private boolean rowHasValidMoves(int row, int start, String team, JsonNode move) {
        String[] teamNames = this.gameState.getTeamNames();
        int fact = -1;
        if(team.equals(teamNames[1])) {
            fact = 1;
        }
        for(int j = start; j < this.board.getSideLength(); j++) {
            CheckersCoordPair pos = this.board.getBoardPos(row, j);

            //this check is weird. There should be a thrown exception probably
            if(pos == null)
                return false;
            CheckersGamePiece piece = pos.getPiece();

            if(piece == null) {
                break;
            }

            if(!piece.getTeam().equals(team)) {
                break;
            }

            CheckersMove cMove = new CheckersMove();
            cMove.setStartX(pos.getX());
            cMove.setStartY(pos.getY());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.valueToTree(cMove);
            
            if(this.furtherMoves.checkMove(node)) {
                return false;
            }

            if(this.furtherAttacks.checkMove(node)) {
                return false;
            }
            

                                    //if(piece != null && piece.getName().equals(name)) {



                                        // int[][] validMoves = piece.getValidMoves();

                                        // for(int[] vect : validMoves) {
                                        //     //CheckersCoordPair validDiff
                                        //     int x = pos.getX() + vect[0] * fact;
                                        //     int y = pos.getY() + vect[1] * fact;
                                        //    // if(inBounds(new CheckersCoordPair(x, y))) {
                                        //         CheckersCoordPair end = this.board.getBoardPos(x, y);
                                        //         if(end == null)
                                        //             end = new CheckersCoordPair(x, y);
                            
                                        //         CheckersMove move = new CheckersMove(pos.getX(), pos.getY(), end.getX(), end.getY());
                                        //         if(isValidMove(move, piece.getName()))
                                        //             return true;
                                        //     //}
                                        // }
                                        //if(hasFurtherJumps(pos))
                                        //    return true;
                                // }
        }
        return false;
    }


















    private boolean hasFurtherJumps(CheckersCoordPair pos) {
        if(pos == null || pos.getPiece() == null)
            return false;
        CheckersGamePiece piece = pos.getPiece();
        String team = piece.getTeam();
        int startX = pos.getX();
        int startY = pos.getY();

        if(!this.board.isValidPos(startX, startY) || piece == null)
            return false;

        int[][] validJumps = piece.getValidJumps();
        int x;
        int y;
        int jumpX;
        int jumpY;
        String[] teams = this.gameState.getTeamNames();

        for(int i = 0; i < validJumps.length; i++) {
            
            if(teams.equals(teams[0])) {
                x = startX + validJumps[i][0] * -1;
                y = startY + validJumps[i][1] * -1;
            } else {
                x = startX + validJumps[i][0];
                y = startY + validJumps[i][1];
            }

            if(isValidPos(this.board.getBoardPos(x, y))) {
                jumpX = (startX + x) >> 1;
                jumpY = (startY + y) >> 1;

                CheckersCoordPair jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CheckersCoordPair end = this.board.getBoardPos(x, y);
                CheckersGamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                CheckersGamePiece jumpPiece = jumpPos.getPiece();

                if(jumpPiece != null && !team.equals(jumpPiece.getTeam()) && endPiece == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidPos(CheckersCoordPair pos) {
        if(pos == null)
            return false;
        int length = this.board.getSideLength();
        int x = pos.getX();
        int y = pos.getY();
        
        if(x >= 0 && x < length && y >= 0 && y < length) 
            if(x % 2 == 0 && y % 2 != 0)
                return true;
            else if(x % 2 != 0 && y % 2 == 0)
                return true;
        return false;
    }

    private boolean isValidMove(CheckersMove move, String pName) {
        if(move == null)
            return false;

        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CheckersCoordPair start = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return false; 
        if(start.getPiece() == null || !start.getPiece().getName().equals(pName) || end.getPiece() != null) 
            return false;

        String name = start.getPiece().getName();
        int turnFactor = -1;
        if(name.equals("R"))
            turnFactor = 1;

        int[][] validMoves = start.getPiece().getValidMoves();
        for(int[] pair : validMoves) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return true;
        }
        return false;
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
    
}
