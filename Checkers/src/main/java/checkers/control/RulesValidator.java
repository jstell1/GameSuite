package checkers.control;

import java.util.HashSet;
import java.util.Set;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;

public class RulesValidator {
    private HashSet<CheckersCoordPair> validMoves;
    private HashSet<CheckersCoordPair> validJumps;
    private HashSet<CheckersCoordPair> validKingMoves;
    private HashSet<CheckersCoordPair> validKingJumps;
    private CheckersGameState game;
    private CheckersGameBoard board;
    private final String[] pieceNames = {"B", "R"};

    public RulesValidator(CheckersGameState game, CheckersGameBoard board) {
        this.game = game;
        this.board = board;
        this.validMoves = new HashSet<>();
        this.validJumps = new HashSet<>();
        this.validKingMoves = new HashSet<>();
        this.validKingJumps = new HashSet<>();

        CheckersCoordPair pos = new CheckersCoordPair(-1, -1);
        this.validMoves.add(pos);
        this.validKingMoves.add(pos);
        pos = new CheckersCoordPair(-1, 1);
        this.validMoves.add(pos);
        this.validKingMoves.add(pos);
        pos = new CheckersCoordPair(1, -1);
        this.validKingMoves.add(pos);
        pos = new CheckersCoordPair(1, 1);
        this.validKingMoves.add(pos);

        pos = new CheckersCoordPair(-2, -2);
        this.validJumps.add(pos);
        this.validKingJumps.add(pos);
        pos = new CheckersCoordPair(-2, 2);
        this.validJumps.add(pos);
        this.validKingJumps.add(pos);
        pos = new CheckersCoordPair(2, -2);
        this.validKingJumps.add(pos);
        pos = new CheckersCoordPair(2, 2);
        this.validKingJumps.add(pos);
    }

    public boolean isKingable(CheckersCoordPair end) {
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

    private boolean isValidJumpedPiece(CheckersGamePiece piece) {
        if(piece == null)
            return false;
        int turn = this.game.getTurn();
        String name = this.pieceNames[turn - 1];
        if(!piece.getName().equals(name))
            return true;
        return false;
    }

    public boolean isValidPos(CheckersCoordPair pos) {
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

    public boolean isTurnPiece(CheckersGamePiece piece) {
        int turn = this.game.getTurn();
        String name = this.pieceNames[turn - 1];

        if(piece != null && piece.getName().equals(name))
            return true;
        return false;
    }

    public boolean isValidMove(CheckersMove move) {
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

    public boolean isPlayerJump(int playerNum, CheckersCoordPair pos) {
        return this.game.getJumps(playerNum).contains(pos);
    }

     public boolean isValidMove(CheckersMove move, String pName) {
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

    public boolean isValidJump(CheckersMove move) {
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

        int jumpedX = (start.getX() + end.getX()) >> 1;
        int jumpedY = (start.getY() + end.getY()) >> 1;
        CheckersCoordPair pos = this.board.getBoardPos(jumpedX, jumpedY);
        CheckersGamePiece jumpedPiece = pos.getPiece();
        CheckersGamePiece piece = start.getPiece();
        CheckersGamePiece endPiece = end.getPiece();
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

    private Set<CheckersCoordPair> getValidMoves(CheckersGamePiece piece) {
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
            CheckersCoordPair pos = this.board.getBoardPos(row, j);
            if(pos == null)
                return false;
            CheckersGamePiece piece = pos.getPiece();
            if(piece != null && piece.getName().equals(name)) {
                Set<CheckersCoordPair> validMoves = getValidMoves(piece);

                for(CheckersCoordPair validDiff : validMoves) {
                    int x = pos.getX() + validDiff.getX() * fact;
                    int y = pos.getY() + validDiff.getY() * fact;
                    if(isValidPos(new CheckersCoordPair(x, y))) {
                        CheckersCoordPair end = this.board.getBoardPos(x, y);
                        if(end == null)
                            end = new CheckersCoordPair(x, y);
    
                        CheckersMove move = new CheckersMove(pos.getX(), pos.getY(), end.getX(), end.getY());
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

    public boolean hasFurtherJumps(CheckersCoordPair pos) {
        if(pos == null || pos.getPiece() == null)
            return false;
        CheckersGamePiece piece = pos.getPiece();
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

                CheckersCoordPair jumpPos = this.board.getBoardPos(jumpX, jumpY);
                CheckersCoordPair end = this.board.getBoardPos(x, y);
                CheckersGamePiece endPiece = null;
                if(end != null)
                    endPiece = end.getPiece();
                CheckersGamePiece jumpPiece = jumpPos.getPiece();

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
