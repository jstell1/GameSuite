package checkers.view;

import com.fasterxml.jackson.databind.node.ObjectNode;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.CoordPair;

public class CheckersGameBoardView implements GameBoard {

    CheckersGameBoard board;

    public CheckersGameBoardView(CheckersGameBoard board) { this.board = board; }

    public CoordPair[][] getBoard() { 
        CheckersCoordPair[][] bord = this.board.getBoard();
        CheckersCoordPairView[][] ret = new CheckersCoordPairView[board.getSideLength()][board.getSideLength()];
        for(CheckersCoordPair[] row : bord)
            for(CheckersCoordPair pos : row)
                ret[pos.getX()][pos.getY()] = new CheckersCoordPairView(pos);
        return ret;
     }

    public GameBoard copy() { return new CheckersGameBoardView(this.board.copy()); }

    public int getSize() { return this.board.getSize(); }

    public int getSideLength() { return this.board.getSideLength(); }

    public CoordPair getBoardPos(int x, int y) { return new CheckersCoordPairView(this.board.getBoardPos(x, y)); }
    public boolean isValidPos(int x, int y) { return this.board.isValidPos(x, y); }

    public String toString() { return this.board.toString(); }

    @Override
    public ObjectNode getObjectNode() {
         CheckersCoordPair[][] bord = this.board.getBoard();
        CheckersCoordPairView[][] ret = new CheckersCoordPairView[board.getSideLength()][board.getSideLength()];
        for(CheckersCoordPair[] row : bord)
            for(CheckersCoordPair pos : row)
                ret[pos.getX()][pos.getY()] = new CheckersCoordPairView(pos);
        return null;
    }
}
