package gamesuite.boardgame.model;

import java.util.Arrays;


import gamesuite.core.model.GameBoard;


public class GameBoardImpl implements GameBoard {
    private CoordPairImpl[][] board;
    private int sideLength;
    private int size;

    public GameBoardImpl(int sideLength) {
        if(sideLength < 1) {
            throw new IllegalArgumentException("sidelengths cannot be less than 1");
        }
        this.sideLength = sideLength;
        this.board = new CoordPairImpl[sideLength][sideLength];
        this.size = sideLength * sideLength;
        for(int i = 0; i < this.sideLength; i++)
            for(int j = 0; j < this.sideLength; j++) 
                this.board[i][j] = new CoordPairImpl(i, j);
    }

    public GameBoardImpl(CoordPairImpl[][] board) {
        this.board = board;
        this. sideLength = board.length;
        this.size = board.length * board.length;
    }
    

    public CoordPairImpl[][] getBoard() { 
        CoordPairImpl[][] b = new CoordPairImpl[this.board.length][];
        for(int i = 0; i < b.length; i++) {
            b[i] = Arrays.copyOf(this.board[i], this.board[i].length);
        }
        return b; 
    }

    public GameBoardImpl copy() {
        GameBoardImpl board = new GameBoardImpl(this.sideLength);
        for(CoordPairImpl[] row: this.board)
            for(CoordPairImpl pos : row) 
                board.setBoardPos(pos.getX(), pos.getY(), pos.getPiece());     
        return board;
    }

    public int getSize() { return this.size; }

    public int getSideLength() { return this.sideLength; }

    public CoordPairImpl getBoardPos(int x, int y) {

            return board[x][y];

    }

    public void setBoardPos(int x, int y, BoardGamePiece piece) {
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
                    BoardGamePiece piece = board[i][j].getPiece();

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

}
