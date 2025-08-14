package gamesuite.core.model;

import gamesuite.core.view.CoordPairView;
import gamesuite.core.view.GameBoardView;

public class GameBoard implements GameBoardView {
    private CoordPair[][] board;
    private int sideLength;
    private int size;

    public GameBoard(int sideLength) {
        this.sideLength = sideLength;
        this.board = new CoordPair[sideLength][sideLength];
        this.size = sideLength * sideLength;
        for(int i = 0; i < this.sideLength; i++)
            for(int j = 0; j < this.sideLength; j++) 
                this.board[i][j] = new CoordPair(i, j);
    }

    public GameBoard(CoordPair[][] board) {
        this.board = board;
        this. sideLength = board.length;
        this.size = board.length * board.length;
    }

    public GameBoard copy() {
        GameBoard board = new GameBoard(this.sideLength);
        for(CoordPair[] row: this.board)
            for(CoordPair pos : row) 
                if(pos.getPiece() != null)
                    board.setBoardPos(pos.getX(), pos.getY(), pos.getPiece().copy());
                else    
                    board.setBoardPos(pos.getX(), pos.getY(), null);      
        return board;
    }

    @Override
    public int getSize() { return this.size; }

    @Override
    public int getSideLength() { return this.sideLength; }

    public CoordPair getBoardPos(int x, int y) {
        if(isValidPos(x, y))
            return board[x][y];
        return null;
    }

    public void setBoardPos(int x, int y, GamePiece piece) {
        if(isValidPos(x, y) && board[x][y] != null) 
            board[x][y].setPiece(piece);
    }

    @Override
    public CoordPairView getPos(int x, int y) { return getBoardPos(x, y); }

    public boolean isValidPos(int x, int y) {
        if(x < 0 || x >= sideLength || y < 0 || y >= sideLength) 
            return false;
        return true;
    }

    @Override
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
                    GamePiece piece = board[i][j].getPiece();

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

    //public GameBoardView getGameBoardView() { return this; }
}
