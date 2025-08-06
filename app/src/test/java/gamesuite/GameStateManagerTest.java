package gamesuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gamesuite.model.data.GameBoard;
import gamesuite.model.data.GamePiece;
import gamesuite.model.data.GameState;
import gamesuite.model.data.Player;
import gamesuite.model.logic.GameStateManager;

public class GameStateManagerTest {

    Player player1;
    Player player2;
    
    @BeforeEach
    void setup() {
        this.player1 = new Player("Jim BoB", 0);
        this.player2 = new Player("Sue Bob", 0);
    }

    @Test
    void testIncrTurn() {
        GameBoard board = new GameBoard(8);
        GameState game = new GameState(board, player1, player2);
        GameStateManager stateMang = new GameStateManager(game);
        this.player1.addPoints(5);
        this.player2.addPoints(7);
        stateMang.incrTurn(true, false);
        int turn = stateMang.getTurn();
        assertEquals(1, turn);
        assertTrue(stateMang.getWinner() == player1);
        game = new GameState(board, player1, player2);
        stateMang = new GameStateManager(game);
        stateMang.incrTurn(false, true);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player2);
        game = new GameState(board, player1, player2);
        stateMang = new GameStateManager(game);
        stateMang.incrTurn(true, true);
        assertEquals(2, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == null);
        game = new GameState(board, player1, player2);
        stateMang = new GameStateManager(game);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player2);
        game = new GameState(board, player1, player2);
        stateMang = new GameStateManager(game);
        player1.addPoints(3);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == player1);
         game = new GameState(board, player1, player2);
        stateMang = new GameStateManager(game);
        player2.addPoints(1);
        stateMang.incrTurn(false, false);
        assertEquals(1, stateMang.getTurn());
        assertTrue(stateMang.getWinner() == null);
        assertTrue(stateMang.getDraw());
    }

    @Test
    void testKingPiece() {
        GameBoard board = new GameBoard(8);
        GameState game = new GameState(board, player1, player2);
        GameStateManager stateMang = new GameStateManager(game);
        GamePiece piece = new GamePiece("R", "C", 1);
        game.setBoardPos(0, 1, piece);
        stateMang.kingPiece(game.getBoardPos(0, 1));
        assertTrue(piece.getType() == "K");
        GamePiece piece2 = new GamePiece("R", "C", 1);
        GamePiece piece3 = new GamePiece("B", "C", 1);
        game.setBoardPos(7, 0, piece2);
        game.setBoardPos(7, 2, piece3);
        stateMang.kingPiece(game.getBoardPos(7, 0));
        assertFalse(piece2.getType() == "K");
        stateMang.kingPiece(game.getBoardPos(7, 2));
        assertTrue(piece3.getType() == "K");
        stateMang.kingPiece(game.getBoardPos(0, 7));
    }

    
}
