package gamesuite.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;

public class GameStateTest {

    GameBoard board;
    Player p1, p2;
    GameState game;

    @BeforeEach
    void setup() {
        this.p1 = new Player("Bob", 0);
        this.p2 = new Player("Joe", 0);
    }

    CoordPair[][] getBoardArr(int size, CoordPair[] set) {
        CoordPair[][] arr = new CoordPair[size][size];

        for(CoordPair pos : set) 
            arr[pos.getX()][pos.getY()] = pos;
        return arr;
    }

    @Test
    void testIsValidPos() {
        CoordPair[] ar = {
            new CoordPair(0, 0),
            new CoordPair(1, 1),
            new CoordPair(2, 2),
        };
        CoordPair[][] arr = getBoardArr(3, ar);
        this.board = new GameBoard(arr);
        this.game = new GameState(this.p1, this.p2);

        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(1, 0));
        assertFalse(this.board.isValidPos(-1, 2));
        assertFalse(this.board.isValidPos(-2, 9));
    }

    @Test
    void testGetBoardPos() {
        CoordPair[] arr = new CoordPair[3];
        arr[0] = new CoordPair(0, 0);
        arr[1] = new CoordPair(1, 1);
        arr[2] = new CoordPair(2, 2);
        CoordPair[][] board = getBoardArr(3, arr);
        this.board = new GameBoard(board);
        this.game = new GameState(this.p1, this.p2);

        assertEquals(arr[0], this.board.getBoardPos(0, 0));
        assertNotEquals(arr[1], this.board.getBoardPos(0, 0));
        assertNull(this.board.getBoardPos(0, 1));
        assertNull(this.board.getBoardPos(-1, 4));
        assertNull(this.board.getBoardPos(9, 1));
    }

    @Test
    void testAddPlayerJumps() {
        CoordPair[] arr = new CoordPair[3];
        arr[0] = new CoordPair(0, 0);
        arr[1] = new CoordPair(1, 1);
        arr[2] = new CoordPair(2, 2);
        GamePiece p1 = new GamePiece("B", "C", 1);
        GamePiece p2 = new GamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CoordPair[][] board = getBoardArr(3, arr);
        this.board = new GameBoard(board);
        this.game = new GameState(this.p1, this.p2);
        assertTrue(this.game.getJumps(1).isEmpty());
        this.game.addPlayerJumps(this.board.getBoardPos(0, 0), 1);
        Set<CoordPair> jumps = this.game.getJumps(1);
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
        CoordPair[] arr = new CoordPair[3];
        arr[0] = new CoordPair(0, 0);
        arr[1] = new CoordPair(1, 1);
        arr[2] = new CoordPair(2, 2);
        GamePiece p1 = new GamePiece("B", "C", 1);
        GamePiece p2 = new GamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CoordPair[][] board = getBoardArr(3, arr);
        this.board = new GameBoard(board);
        this.game = new GameState(this.p1, this.p2);
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
