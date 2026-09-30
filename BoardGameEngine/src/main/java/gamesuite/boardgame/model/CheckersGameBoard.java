package gamesuite.boardgame.model;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.model.GameBoard;


public class CheckersGameBoard implements GameBoard {
    private CheckersCoordPair[][] board;
    private int sideLength;
    private int size;

    public CheckersGameBoard(int sideLength) {
        if(sideLength < 1) {
            throw new IllegalArgumentException("sidelengths cannot be less than 1");
        }
        this.sideLength = sideLength;
        this.board = new CheckersCoordPair[sideLength][sideLength];
        this.size = sideLength * sideLength;
        for(int i = 0; i < this.sideLength; i++)
            for(int j = 0; j < this.sideLength; j++) 
                this.board[i][j] = new CheckersCoordPair(i, j);
    }

    public CheckersGameBoard(CheckersCoordPair[][] board) {
        this.board = board;
        this. sideLength = board.length;
        this.size = board.length * board.length;
    }
    

    public CheckersCoordPair[][] getBoard() { 
        CheckersCoordPair[][] b = new CheckersCoordPair[this.board.length][];
        for(int i = 0; i < b.length; i++) {
            b[i] = Arrays.copyOf(this.board[i], this.board[i].length);
        }
        return b; 
    }

    public CheckersGameBoard copy() {
        CheckersGameBoard board = new CheckersGameBoard(this.sideLength);
        for(CheckersCoordPair[] row: this.board)
            for(CheckersCoordPair pos : row) 
                board.setBoardPos(pos.getX(), pos.getY(), pos.getPiece());     
        return board;
    }

    public int getSize() { return this.size; }

    public int getSideLength() { return this.sideLength; }

    public CheckersCoordPair getBoardPos(int x, int y) {
        if(isValidPos(x, y))
            return board[x][y];
        return null;
    }

    public void setBoardPos(int x, int y, CheckersGamePiece piece) {
        if(isValidPos(x, y) && board[x][y] != null) 
            board[x][y].setPiece(piece);
    }

    public boolean isValidPos(int x, int y) {
        if(x < 0 || x >= sideLength || y < 0 || y >= sideLength) 
            return false;
        return true;
    }

    public String toString() {

        String str = "";
        str += "_";
        for(int i = 0; i < sideLength; i++) {
            str += "___";
        }
        str += "\n";
        for(int i = 0; i < sideLength; i++) {
            str += "|"; 
            for(int j = 0; j < sideLength; j++) {
                if(board[i][j] == null) {
                    str += "  |";
                } else {
                    CheckersGamePiece piece = board[i][j].getPiece();

                    if(piece == null)
                         str += "  |";
                    else
                        str += piece.getName() + "|";
                }
            }
            str += "\n";
            for(int j = 0; j < sideLength; j++) 
                str += "___";
            str += "\n";
        }
        str += "\n";
        return str;
    }

    // @Override
    // public JsonNode getJsonNode() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode node = mapper.valueToTree(this);
    //     return node; 
    // }
}
