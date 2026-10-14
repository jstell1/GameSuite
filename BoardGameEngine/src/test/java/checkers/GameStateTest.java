package checkers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGamePlayer;

public class GameStateTest {

    GameBoardImpl board;
    BoardGamePlayer p1, p2;
    BoardGameState game;

    @BeforeEach
    void setup() {
        this.p1 = new BoardGamePlayer("Bob", 0);
        this.p2 = new BoardGamePlayer("Joe", 0);
    }

    CoordPairImpl[][] getBoardArr(int size, CoordPairImpl[] set) {
        CoordPairImpl[][] arr = new CoordPairImpl[size][size];

        for(CoordPairImpl pos : set) 
            arr[pos.getX()][pos.getY()] = pos;
        return arr;
    }

    @Test
    void testIsValidPos() {
        CoordPairImpl[] ar = {
            new CoordPairImpl(0, 0),
            new CoordPairImpl(1, 1),
            new CoordPairImpl(2, 2),
        };
        CoordPairImpl[][] arr = getBoardArr(3, ar);
        this.board = new GameBoardImpl(arr);
        this.game = new BoardGameState(this.p1, this.p2);

        assertTrue(this.board.isValidPos(0, 0));
        assertTrue(this.board.isValidPos(1, 0));
        assertFalse(this.board.isValidPos(-1, 2));
        assertFalse(this.board.isValidPos(-2, 9));
    }

    @Test
    void testGetBoardPos() {
        CoordPairImpl[] arr = new CoordPairImpl[3];
        arr[0] = new CoordPairImpl(0, 0);
        arr[1] = new CoordPairImpl(1, 1);
        arr[2] = new CoordPairImpl(2, 2);
        CoordPairImpl[][] board = getBoardArr(3, arr);
        this.board = new GameBoardImpl(board);
        this.game = new BoardGameState(this.p1, this.p2);

        assertEquals(arr[0], this.board.getBoardPos(0, 0));
        assertNotEquals(arr[1], this.board.getBoardPos(0, 0));
        assertNull(this.board.getBoardPos(0, 1));
        assertNull(this.board.getBoardPos(-1, 4));
        assertNull(this.board.getBoardPos(9, 1));
    }

    @Test
    void testAddPlayerJumps() {
        CoordPairImpl[] arr = new CoordPairImpl[3];
        arr[0] = new CoordPairImpl(0, 0);
        arr[1] = new CoordPairImpl(1, 1);
        arr[2] = new CoordPairImpl(2, 2);
        BoardGamePiece p1 = new BoardGamePiece("B", "C", 1);
        BoardGamePiece p2 = new BoardGamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CoordPairImpl[][] board = getBoardArr(3, arr);
        this.board = new GameBoardImpl(board);
        this.game = new BoardGameState(this.p1, this.p2);
        assertTrue(this.game.getJumps(1).isEmpty());
        this.game.addPlayerJumps(this.board.getBoardPos(0, 0), 1);
        Set<CoordPairImpl> jumps = this.game.getJumps(1);
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
        CoordPairImpl[] arr = new CoordPairImpl[3];
        arr[0] = new CoordPairImpl(0, 0);
        arr[1] = new CoordPairImpl(1, 1);
        arr[2] = new CoordPairImpl(2, 2);
        BoardGamePiece p1 = new BoardGamePiece("B", "C", 1);
        BoardGamePiece p2 = new BoardGamePiece("R", "C", 1);
        arr[0].setPiece(p1);
        arr[1].setPiece(p2);
        CoordPairImpl[][] board = getBoardArr(3, arr);
        this.board = new GameBoardImpl(board);
        this.game = new BoardGameState(this.p1, this.p2);
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
