package checkers;

import org.junit.jupiter.api.Test;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class GameBoardTest {

    CheckersGameBoard board;

    @BeforeEach
    void setup() {
        this.board = new CheckersGameBoard(8);
    }

    @Test
    void testIsValidPos() {
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(4, 6));
        assertFalse(this.board.isValidPos(-1, 9));
        assertFalse(this.board.isValidPos(3, -4));
        CheckersCoordPair[][] board = {
            { new CheckersCoordPair(0, 0), null },
            { null, null}
        };
        this.board = new CheckersGameBoard(board);
        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(0, 1));
        assertFalse(this.board.isValidPos(0, 5));
    }

    @Test
    void testGetBoardPos() {
        CheckersCoordPair[][] board = new CheckersCoordPair[8][8];
        
        board[0][0] = new CheckersCoordPair(0, 0);
        board[0][7] = new CheckersCoordPair(0, 7);
        board[7][0] = new CheckersCoordPair(7, 0);
        board[7][7] = new CheckersCoordPair(7, 7);
        
        this.board = new CheckersGameBoard(board);

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
        CheckersGamePiece piece = new CheckersGamePiece("R", "C", 1);
        CheckersCoordPair[][] board = {
            {new CheckersCoordPair(0, 0), new CheckersCoordPair(0, 1) },
            {new CheckersCoordPair(1, 0), null } 
        };
        this.board = new CheckersGameBoard(board);

        this.board.setBoardPos(0, 0, piece);
        assertEquals(piece, this.board.getBoardPos(0, 0).getPiece());
        this.board.setBoardPos(1, 1, piece);
        assertNull(this.board.getBoardPos(1, 1));
        this.board.setBoardPos(-1, -1, null);
    }

    @Test
    void testGetPos() {
        CheckersCoordPair pos = new CheckersCoordPair(0, 0);
        CheckersGamePiece piece = new CheckersGamePiece("B", "K", 1);
        this.board.setBoardPos(0, 0, piece);
        CheckersCoordPair posView = this.board.getBoardPos(0, 0);
        assertEquals(pos.getX(), posView.getX());
        assertEquals(pos.getY(), posView.getY());
        CheckersGamePiece pieceView = posView.getPiece();
        assertEquals(pieceView.getName(), piece.getName());
        assertEquals(pieceView.getType(), piece.getType());
        assertEquals(pieceView.getVal(), piece.getVal());
    }
}
