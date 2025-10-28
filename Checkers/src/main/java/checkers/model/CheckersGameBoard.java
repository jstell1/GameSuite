package checkers.model;

import gamesuite.core.model.GameBoard;

public class CheckersGameBoard {
    private CheckersCoordPair[][] board;
    private int sideLength;
    private int size;

    public CheckersGameBoard(int sideLength) {
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

    public CheckersCoordPair[][] getBoard() { return this.board; }

    public CheckersGameBoard copy() {
        CheckersGameBoard board = new CheckersGameBoard(this.sideLength);
        for(CheckersCoordPair[] row: this.board)
            for(CheckersCoordPair pos : row) 
                if(pos.getPiece() != null)
                    board.setBoardPos(pos.getX(), pos.getY(), pos.getPiece());
                else    
                    board.setBoardPos(pos.getX(), pos.getY(), null);      
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
                        str += piece.getName() + piece.getType() + "|";
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
