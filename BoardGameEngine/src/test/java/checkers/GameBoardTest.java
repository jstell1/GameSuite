package checkers;

import org.junit.jupiter.api.Test;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class GameBoardTest {

    GameBoardImpl board;

    @BeforeEach
    void setup() {
        this.board = new GameBoardImpl(8);
    }

    @Test
    void testIsValidPos() {
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(4, 6));
        assertFalse(this.board.isValidPos(-1, 9));
        assertFalse(this.board.isValidPos(3, -4));
        CoordPairImpl[][] board = {
            { new CoordPairImpl(0, 0), null },
            { null, null}
        };
        this.board = new GameBoardImpl(board);
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(0, 1));
        assertFalse(this.board.isValidPos(0, 5));
    }

    @Test
    void testGetBoardPos() {
        CoordPairImpl[][] board = new CoordPairImpl[8][8];
        
        board[0][0] = new CoordPairImpl(0, 0);
        board[0][7] = new CoordPairImpl(0, 7);
        board[7][0] = new CoordPairImpl(7, 0);
        board[7][7] = new CoordPairImpl(7, 7);
        
        this.board = new GameBoardImpl(board);

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
        BoardGamePiece piece = new BoardGamePiece("R", "C", 1);
        CoordPairImpl[][] board = {
            {new CoordPairImpl(0, 0), new CoordPairImpl(0, 1) },
            {new CoordPairImpl(1, 0), null } 
        };
        this.board = new GameBoardImpl(board);

        this.board.setBoardPos(0, 0, piece);
        assertEquals(piece, this.board.getBoardPos(0, 0).getPiece());
        this.board.setBoardPos(1, 1, piece);
        assertNull(this.board.getBoardPos(1, 1));
        this.board.setBoardPos(-1, -1, null);
    }

    @Test
    void testGetPos() {
        CoordPairImpl pos = new CoordPairImpl(0, 0);
        BoardGamePiece piece = new BoardGamePiece("B", "K", 1);
        this.board.setBoardPos(0, 0, piece);
        CoordPairImpl posView = this.board.getBoardPos(0, 0);
        assertEquals(pos.getX(), posView.getX());
        assertEquals(pos.getY(), posView.getY());
        BoardGamePiece pieceView = posView.getPiece();
        assertEquals(pieceView.getName(), piece.getName());
        assertEquals(pieceView.getType(), piece.getType());
        assertEquals(pieceView.getVal(), piece.getVal());
    }
}
