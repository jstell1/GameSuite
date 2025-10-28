package checkers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import checkers.control.GameStateManager;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersPlayer;

public class GameStateManagerTest {

    CheckersPlayer player1;
    CheckersPlayer player2;
    
    @BeforeEach
    void setup() {
        this.player1 = new CheckersPlayer("Jim BoB", 0);
        this.player2 = new CheckersPlayer("Sue Bob", 0);
    }

    @Test
    void testIncrTurn() {
        CheckersGameBoard board = new CheckersGameBoard(8);
        CheckersGameState game = new CheckersGameState(player1, player2);
        GameStateManager stateMang = new GameStateManager(game, board);
        this.player1.addPoints(5);
        this.player2.addPoints(7);
        stateMang.incrTurn(true, false);
        int turn = stateMang.getTurn();
        assertEquals(1, turn);
        assertTrue(stateMang.getWinner() == player1);
        game = new CheckersGameState(player1, player2);
        stateMang = new GameStateManager(game, board);
        stateMang.incrTurn(false, true);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player2);
        game = new CheckersGameState(player1, player2);
        stateMang = new GameStateManager(game, board);
        stateMang.incrTurn(true, true);
        assertEquals(2, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == null);
        game = new CheckersGameState(player1, player2);
        stateMang = new GameStateManager(game, board);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player2);
        game = new CheckersGameState(player1, player2);
        stateMang = new GameStateManager(game, board);
        player1.addPoints(3);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player1);
         game = new CheckersGameState(player1, player2);
        stateMang = new GameStateManager(game, board);
        player2.addPoints(1);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == null);
        assertTrue(stateMang.getDraw());
    }

    @Test
    void testKingPiece() {
        CheckersGameBoard board = new CheckersGameBoard(8);
        CheckersGameState game = new CheckersGameState(player1, player2);
        GameStateManager stateMang = new GameStateManager(game, board);
        CheckersGamePiece piece = new CheckersGamePiece("R", "C", 1);
        board.setBoardPos(0, 1, piece);
        stateMang.kingPiece(board.getBoardPos(0, 1));
        assertTrue(piece.getType() == "K");
        CheckersGamePiece piece2 = new CheckersGamePiece("R", "C", 1);
        CheckersGamePiece piece3 = new CheckersGamePiece("B", "C", 1);
        board.setBoardPos(7, 0, piece2);
        board.setBoardPos(7, 2, piece3);
        stateMang.kingPiece(board.getBoardPos(7, 0));
        assertFalse(piece2.getType() == "K");
        stateMang.kingPiece(board.getBoardPos(7, 2));
        assertTrue(piece3.getType() == "K");
        stateMang.kingPiece(board.getBoardPos(0, 7));
    }

    
}
