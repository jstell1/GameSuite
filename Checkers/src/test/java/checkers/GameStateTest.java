package checkers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersPlayer;

public class GameStateTest {

    CheckersGameBoard board;
    CheckersPlayer p1, p2;
    CheckersGameState game;

    @BeforeEach
    void setup() {
        this.p1 = new CheckersPlayer("Bob", 0);
        this.p2 = new CheckersPlayer("Joe", 0);
    }

    CheckersCoordPair[][] getBoardArr(int size, CheckersCoordPair[] set) {
        CheckersCoordPair[][] arr = new CheckersCoordPair[size][size];

        for(CheckersCoordPair pos : set) 
            arr[pos.getX()][pos.getY()] = pos;
        return arr;
    }

    @Test
    void testIsValidPos() {
        CheckersCoordPair[] ar = {
            new CheckersCoordPair(0, 0),
            new CheckersCoordPair(1, 1),
            new CheckersCoordPair(2, 2),
        };
        CheckersCoordPair[][] arr = getBoardArr(3, ar);
        this.board = new CheckersGameBoard(arr);
        this.game = new CheckersGameState(this.p1, this.p2);

        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(1, 0));
        assertFalse(this.board.isValidPos(-1, 2));
        assertFalse(this.board.isValidPos(-2, 9));
    }

    @Test
    void testGetBoardPos() {
        CheckersCoordPair[] arr = new CheckersCoordPair[3];
        arr[0] = new CheckersCoordPair(0, 0);
        arr[1] = new CheckersCoordPair(1, 1);
        arr[2] = new CheckersCoordPair(2, 2);
        CheckersCoordPair[][] board = getBoardArr(3, arr);
        this.board = new CheckersGameBoard(board);
        this.game = new CheckersGameState(this.p1, this.p2);

        assertEquals(arr[0], this.board.getBoardPos(0, 0));
        assertNotEquals(arr[1], this.board.getBoardPos(0, 0));
        assertNull(this.board.getBoardPos(0, 1));
        assertNull(this.board.getBoardPos(-1, 4));
        assertNull(this.board.getBoardPos(9, 1));
    }

    @Test
    void testAddPlayerJumps() {
        CheckersCoordPair[] arr = new CheckersCoordPair[3];
        arr[0] = new CheckersCoordPair(0, 0);
        arr[1] = new CheckersCoordPair(1, 1);
        arr[2] = new CheckersCoordPair(2, 2);
        CheckersGamePiece p1 = new CheckersGamePiece("B", "C", 1);
        CheckersGamePiece p2 = new CheckersGamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CheckersCoordPair[][] board = getBoardArr(3, arr);
        this.board = new CheckersGameBoard(board);
        this.game = new CheckersGameState(this.p1, this.p2);
        assertTrue(this.game.getJumps(1).isEmpty());
        this.game.addPlayerJumps(this.board.getBoardPos(0, 0), 1);
        Set<CheckersCoordPair> jumps = this.game.getJumps(1);
        assertTrue(jumps.contains(board[0][0]));
        this.game.addPlayerJumps(this.board.getBoardPos(1, 0), 1);
        this.game.addPlayerJumps(this.board.getBoardPos(1, 0), 1);
        assertEquals(this.game.getJumps(1).size(), 1);
        assertFalse(this.game.getJumps(1).contains(board[1][0]));
        this.game.addPlayerJumps(this.board.getBoardPos(1, 1), 2);
        assertTrue(this.game.getJumps(2).contains(board[1][1]));
        this.game.addPlayerJumps(this.board.getBoardPos(-1, -5), 1);
        assertEquals(this.game.getJumps(1).size(), 1);
        assertTrue(jumps.contains(board[0][0]));
    }

    @Test
    void testRemovePlayerJumps() {
        CheckersCoordPair[] arr = new CheckersCoordPair[3];
        arr[0] = new CheckersCoordPair(0, 0);
        arr[1] = new CheckersCoordPair(1, 1);
        arr[2] = new CheckersCoordPair(2, 2);
        CheckersGamePiece p1 = new CheckersGamePiece("B", "C", 1);
        CheckersGamePiece p2 = new CheckersGamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CheckersCoordPair[][] board = getBoardArr(3, arr);
        this.board = new CheckersGameBoard(board);
        this.game = new CheckersGameState(this.p1, this.p2);
        this.game.addPlayerJumps(this.board.getBoardPos(0, 0), 1);
        assertTrue(this.game.getJumps(1).contains(board[0][0]));
        this.game.removePlayerJumps(this.board.getBoardPos(1, 0), 1);
        assertTrue(this.game.getJumps(1).contains(board[0][0]));
        this.game.removePlayerJumps(this.board.getBoardPos(-3, 9), 1);
        assertTrue(this.game.getJumps(1).contains(board[0][0]));
        assertEquals(this.game.getJumps(1).size(), 1);
        this.game.removePlayerJumps(this.board.getBoardPos(0, 0), 1);
        assertTrue(this.game.getJumps(1).isEmpty());
    }
    
}
