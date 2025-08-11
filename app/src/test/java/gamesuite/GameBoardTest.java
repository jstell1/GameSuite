package gamesuite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gamesuite.Core.Model.CoordPair;
import gamesuite.Core.Model.GameBoard;
import gamesuite.Core.Model.GamePiece;
import gamesuite.Core.View.CoordPairView;
import gamesuite.Core.View.GamePieceView;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class GameBoardTest {

    GameBoard board;

    @BeforeEach
    void setup() {
        this.board = new GameBoard(8);
    }

    @Test
    void testIsValidPos() {
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(4, 6));
        assertFalse(this.board.isValidPos(-1, 9));
        assertFalse(this.board.isValidPos(3, -4));
        CoordPair[][] board = {
            { new CoordPair(0, 0), null },
            { null, null}
        };
        this.board = new GameBoard(board);
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(0, 1));
        assertFalse(this.board.isValidPos(0, 5));
    }

    @Test
    void testGetBoardPos() {
        CoordPair[][] board = new CoordPair[8][8];
        
        board[0][0] = new CoordPair(0, 0);
        board[0][7] = new CoordPair(0, 7);
        board[7][0] = new CoordPair(7, 0);
        board[7][7] = new CoordPair(7, 7);
        
        this.board = new GameBoard(board);

        assertEquals(board[0][0], this.board.getBoardPos(0, 0));
        assertEquals(board[0][7], this.board.getBoardPos(0, 7));
        assertEquals(board[7][0], this.board.getBoardPos(7, 0));
        assertEquals(board[7][7], this.board.getBoardPos(7, 7));
        assertNull(this.board.getBoardPos(0, 1));
        assertNull(this.board.getBoardPos(4, 6));
        assertNull(this.board.getBoardPos(-3, -9));
        assertNull(this.board.getBoardPos(-4, 6));
        assertNull(this.board.getBoardPos(8, 8));
    }

    @Test
    void testNotNullBoard() {
        for(int i = 0; i < this.board.getSideLength(); i++) 
            for(int j = 0; j < this.board.getSideLength(); j++) 
                assertNotNull(board.getBoardPos(i, j));
    }

    @Test
    void testSetBoardPos() {
        GamePiece piece = new GamePiece("R", "C", 1);
        CoordPair[][] board = {
            {new CoordPair(0, 0), new CoordPair(0, 1) },
            {new CoordPair(1, 0), null } 
        };
        this.board = new GameBoard(board);

        this.board.setBoardPos(0, 0, piece);
        assertEquals(piece, this.board.getBoardPos(0, 0).getPiece());
        this.board.setBoardPos(1, 1, piece);
        assertNull(this.board.getBoardPos(1, 1));
        this.board.setBoardPos(-1, -1, null);
    }

    @Test
    void testGetPos() {
        CoordPair pos = new CoordPair(0, 0);
        GamePiece piece = new GamePiece("B", "K", 1);
        this.board.setBoardPos(0, 0, piece);
        CoordPairView posView = this.board.getPos(0, 0);
        assertEquals(pos.getX(), posView.getX());
        assertEquals(pos.getY(), posView.getY());
        GamePieceView pieceView = posView.getPieceView();
        assertEquals(pieceView.getName(), piece.getName());
        assertEquals(pieceView.getType(), piece.getType());
        assertEquals(pieceView.getVal(), piece.getVal());
    }
}
