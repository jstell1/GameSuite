package gamesuite.core.control;

import java.util.HashSet;
import java.util.Set;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;

public class RulesValidator {
    private HashSet<CoordPair> validMoves;
    private HashSet<CoordPair> validJumps;
    private HashSet<CoordPair> validKingMoves;
    private HashSet<CoordPair> validKingJumps;
    private GameState game;
    private GameBoard board;
    private final String[] pieceNames = {"B", "R"};

    public RulesValidator(GameState game, GameBoard board) {
        this.game = game;
        this.board = board;
        this.validMoves = new HashSet<>();
        this.validJumps = new HashSet<>();
        this.validKingMoves = new HashSet<>();
        this.validKingJumps = new HashSet<>();

        CoordPair pos = new CoordPair(-1, -1);
        this.validMoves.add(pos);
        this.validKingMoves.add(pos);
        pos = new CoordPair(-1, 1);
        this.validMoves.add(pos);
        this.validKingMoves.add(pos);
        pos = new CoordPair(1, -1);
        this.validKingMoves.add(pos);
        pos = new CoordPair(1, 1);
        this.validKingMoves.add(pos);

        pos = new CoordPair(-2, -2);
        this.validJumps.add(pos);
        this.validKingJumps.add(pos);
        pos = new CoordPair(-2, 2);
        this.validJumps.add(pos);
        this.validKingJumps.add(pos);
        pos = new CoordPair(2, -2);
        this.validKingJumps.add(pos);
        pos = new CoordPair(2, 2);
        this.validKingJumps.add(pos);
    }

    public boolean isKingable(CoordPair end) {
        boolean check = end == null || end.getPiece() == null;
        check = check || !isValidPos(end) || end.getPiece().getType() == "K";  
        if(check)
            return false;

        String name = end.getPiece().getName();
        if(name.equals(this.pieceNames[0]) && end.getX() == this.board.getSideLength() - 1) 
            return true;
        else if(name.equals(this.pieceNames[1]) && end.getX() == 0)
            return true;
        return false;
    }

    private boolean isValidJumpedPiece(GamePiece piece) {
        if(piece == null)
            return false;
        int turn = this.game.getTurn();
        String name = this.pieceNames[turn - 1];
        if(!piece.getName().equals(name))
            return true;
        return false;
    }

    public boolean isValidPos(CoordPair pos) {
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

    public boolean isTurnPiece(GamePiece piece) {
        int turn = this.game.getTurn();
        String name = this.pieceNames[turn - 1];

        if(piece != null && piece.getName().equals(name))
            return true;
        return false;
    }

    public boolean isValidMove(Move move) {
        if(move == null)
            return false;
        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CoordPair start = this.board.getBoardPos(sX, sY);
        CoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return false; 
        if(!isTurnPiece(start.getPiece()) || end.getPiece() != null) 
            return false;

        String name = start.getPiece().getName();
        int turnFactor = -1;
        if(name.equals(this.pieceNames[1]))
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

    public boolean hasJumps(int playerNum) {
        return !this.game.getJumps(playerNum).isEmpty();
    }

    public boolean isPlayerJump(int playerNum, CoordPair pos) {
        return this.game.getJumps(playerNum).contains(pos);
    }

     public boolean isValidMove(Move move, String pName) {
        if(move == null)
            return false;

        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CoordPair start = this.board.getBoardPos(sX, sY);
        CoordPair end = this.board.getBoardPos(eX, eY);
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

    public boolean isValidJump(Move move) {
        if(move == null)
            return false;

        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CoordPair start = this.board.getBoardPos(sX, sY);
        CoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return false;

        int jumpedX = (start.getX() + end.getX()) >> 1;
        int jumpedY = (start.getY() + end.getY()) >> 1;
        CoordPair pos = this.board.getBoardPos(jumpedX, jumpedY);
        GamePiece jumpedPiece = pos.getPiece();
        GamePiece piece = start.getPiece();
        GamePiece endPiece = end.getPiece();
        if(!isTurnPiece(piece) ||  endPiece != null || !isValidJumpedPiece(jumpedPiece)) 
            return false;

        String name = piece.getName();
        int turnFactor = -1;
        if(name.equals("R"))
            turnFactor = 1;
        int[][] validJumps = piece.getValidJumps();
        for(int[] pair : validJumps) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return true;
        }
        return false;
    }

    public boolean hasValidMoves(String name) {
        for(int i = 0; i < this.board.getSideLength(); i++) {
            if(i % 2 == 0 && rowHasValidMoves(i, 1, name)) {
               return true;
            } else if(rowHasValidMoves(i, 0, name)) {
                return true;
            }
        }
        return false;
    }

    private Set<CoordPair> getValidMoves(GamePiece piece) {
        if(piece == null)
            return null;
        if(piece.getType().equals("K"))
            return this.validKingMoves;
        return this.validMoves;
    }

    private boolean rowHasValidMoves(int row, int start, String name) {
        int fact = -1;
        if(name.equals("R"))
            fact = 1;
        for(int j = start; j < this.board.getSideLength(); j += 2) {
            CoordPair pos = this.board.getBoardPos(row, j);
            if(pos == null)
                return false;
            GamePiece piece = pos.getPiece();
            if(piece != null && piece.getName().equals(name)) {
                Set<CoordPair> validMoves = getValidMoves(piece);

                for(CoordPair validDiff : validMoves) {
                    int x = pos.getX() + validDiff.getX() * fact;
                    int y = pos.getY() + validDiff.getY() * fact;
                    if(isValidPos(new CoordPair(x, y))) {
                        CoordPair end = this.board.getBoardPos(x, y);
                        if(end == null)
                            end = new CoordPair(x, y);
    
                        Move move = new Move(pos.getX(), pos.getY(), end.getX(), end.getY());
                        if(isValidMove(move, piece.getName()))
                            return true;
                    }
                }
                if(hasFurtherJumps(pos))
                    return true;
            }
        }
        return false;
    }

    public boolean hasFurtherJumps(CoordPair pos) {
        if(pos == null || pos.getPiece() == null)
            return false;
        GamePiece piece = pos.getPiece();
        String name = piece.getName();
        int startX = pos.getX();
        int startY = pos.getY();

        if(!this.board.isValidPos(startX, startY) || piece == null)
            return false;

        int[][] validJumps = piece.getValidJumps();
        int x;
        int y;
        int jumpX;
        int jumpY;

        for(int i = 0; i < validJumps.length; i++) {
            
            if(name.equals(this.pieceNames[0])) {
                x = startX + validJumps[i][0] * -1;
                y = startY + validJumps[i][1] * -1;
            } else {
                x = startX + validJumps[i][0];
                y = startY + validJumps[i][1];
            }

            if(isValidPos(this.board.getBoardPos(x, y))) {
                jumpX = (startX + x) >> 1;
                jumpY = (startY + y) >> 1;

                CoordPair jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CoordPair end = this.board.getBoardPos(x, y);
                GamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                GamePiece jumpPiece = jumpPos.getPiece();

                if(jumpPiece != null && !name.equals(jumpPiece.getName()) && endPiece == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean playersReady() {
        if(this.game.getPlayers().length == 2)
            return true;
        return false;
    }

    
}
