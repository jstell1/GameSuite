package checkers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import checkers.control.GameStateManager;
import checkers.control.MoveController;
import checkers.
control.RulesValidator;
import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;

public class MoveControllerTest {
    
    CheckersPlayer p1;
    CheckersPlayer p2;
    CheckersGameBoard board;
    CheckersGameState game;
    GameStateManager stateMang;
    RulesValidator validator;
    MoveController movCont;

    @BeforeEach
    void setUp() {
        this.p1 = new CheckersPlayer("Frodo", 0);
        this.p2 = new CheckersPlayer("Sam", 0);
        this.board = new CheckersGameBoard(8);
        this.game = new CheckersGameState(p1, p2);
        this.stateMang = new GameStateManager(game, board);
        this.validator = new RulesValidator(game, board);
        this.movCont = new MoveController(validator, stateMang);
    }
    
    @Test
    void testCheckMove() {
        

        CheckersGamePiece[] pieces = {
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("R", "C", 1),//5
            new CheckersGamePiece("R", "C", 1),
            new CheckersGamePiece("R", "C", 1),
            new CheckersGamePiece("R", "C", 1),
            new CheckersGamePiece("R", "C", 1),
        };

        this.game.setTurn(2);
        this.game.addPlayerJumps(this.board.getBoardPos(4, 1), 2);
        this.board.setBoardPos(3, 0, pieces[0]);
        this.board.setBoardPos(3, 2, pieces[1]);
        this.board.setBoardPos(4, 1, pieces[5]);
        this.board.setBoardPos(6, 3, pieces[6]);
        CheckersMove move = new CheckersMove(6, 3, 5, 4);
        assertFalse(this.movCont.checkMove(move));
        this.board.setBoardPos(5, 4, pieces[2]);
        this.game.addPlayerJumps(this.board.getBoardPos(6, 3), 2);
        move = new CheckersMove(4, 1, 2,3);
        assertTrue(this.movCont.checkMove(move));
        move = new CheckersMove(6,3,4,3);
        assertFalse(this.movCont.checkMove(move));
        move = new CheckersMove(6,3,4,5);
        assertTrue(this.movCont.checkMove(move));
        game.setFurtherJumps(board.getBoardPos(4, 1));
        assertFalse(this.movCont.checkMove(move));
        this.game.setFurtherJumps(null);
        move = new CheckersMove(6, 3, 5, 2);
        assertFalse(this.movCont.checkMove(move));
        this.game.removePlayerJumps(this.board.getBoardPos(6, 3), 2);
        this.game.removePlayerJumps(this.board.getBoardPos(4, 1), 2);
        move = new CheckersMove(6, 3, 5, 2);
        assertTrue(this.movCont.checkMove(move));
        move = new CheckersMove(6, 3, 5, 3);
        assertFalse(this.movCont.checkMove(move));

        this.board = new CheckersGameBoard(8);
        this.game = new CheckersGameState(this.p1, this.p2);
        this.stateMang = new GameStateManager(this.game, this.board);
        this.validator = new RulesValidator(this.game, this.board);
        this.movCont = new MoveController(this.validator, this.stateMang);
        this.board.setBoardPos(3, 2, pieces[0]);
        this.board.setBoardPos(4, 1, pieces[5]);
        this.board.setBoardPos(6, 1, pieces[6]);
        this.game.addPlayerJumps(this.board.getBoardPos(4, 1), 2);
        move = new CheckersMove(6, 1, 5, 2);
        assertFalse(this.movCont.checkMove(move));
    }

    @Test
    void testMakeMove() {
        CheckersCoordPair pos = this.board.getBoardPos(5, 4);
        CheckersGamePiece piece = new CheckersGamePiece("B", "C", 1);
        pos.setPiece(piece);
        CheckersCoordPair pos2 = this.board.getBoardPos(6, 5);
        CheckersGamePiece piece2 = new CheckersGamePiece("R", "C", 1);
        pos2.setPiece(piece2);
        CheckersCoordPair pos3 = this.board.getBoardPos(3, 2);
        CheckersGamePiece piece3 = new CheckersGamePiece("R", "C", 1);
        pos3.setPiece(piece3);
        CheckersMove move = new CheckersMove(5, 4, 7, 6);
        CheckersMove move2 = new CheckersMove(3, 2, 2, 3);
        this.movCont.makeMove(move);
        this.stateMang.incrTurn(true, true);
        this.movCont.makeMove(move2);
        assertEquals(piece, this.stateMang.getBoardPos(7, 6).getPiece());
        assertEquals(piece3, this.stateMang.getBoardPos(2, 3).getPiece());
        assertNull(this.stateMang.getBoardPos(3, 2).getPiece());
        assertNull(this.stateMang.getBoardPos(5, 4).getPiece());
        assertNull(this.stateMang.getBoardPos(6, 5).getPiece());

    }

    @Test
    void testUpdateState() {
        CheckersGamePiece[] pieces = {
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("B", "C", 1),
            new CheckersGamePiece("R", "C", 1),
            new CheckersGamePiece("R", "C", 1),
            new CheckersGamePiece("R","C",1)
        };

        this.board.setBoardPos(3,2, pieces[0]);
        this.board.setBoardPos(2,5, pieces[1]);
        this.board.setBoardPos(5,2, pieces[2]);
        this.board.setBoardPos(4,5, pieces[3]);

        CheckersCoordPair[] positions = {
            this.board.getBoardPos(3,2),
            this.board.getBoardPos(2,5),
            this.board.getBoardPos(5,2),
            this.board.getBoardPos(4,5),
            this.board.getBoardPos(4,3),
            this.board.getBoardPos(3, 4),
        };

        CheckersMove[] moves = {
            new CheckersMove(3, 2, 4, 3),
            new CheckersMove(5, 2, 3, 4),
            new CheckersMove(3, 4, 1, 6),
            new CheckersMove(4, 5, 3, 4),
            new CheckersMove(2, 5, 4, 3),
            new CheckersMove(4, 3, 6, 1),
            new CheckersMove(2, 5, 3, 4),
            new CheckersMove(5,2,4,3)
        };

        List<CheckersCoordPair> changed = this.movCont.makeMove(moves[0]);
        this.movCont.updateState(changed);
        assertEquals(2, this.stateMang.getTurn());
        assertTrue(this.game.getJumps(2).contains(positions[2]));
        changed = this.movCont.makeMove(moves[1]);
        this.movCont.updateState(changed);
        assertEquals(2, this.stateMang.getTurn());
        assertEquals(positions[5], this.stateMang.getFurtherJumps());
        changed = this.movCont.makeMove(moves[2]);
        this.movCont.updateState(changed);
        assertEquals(this.stateMang.getWinner(), this.p2);

        setUp();
        CheckersCoordPair[] positions2 = {
            this.board.getBoardPos(3,2),
            this.board.getBoardPos(2,5),
            this.board.getBoardPos(5,2),
            this.board.getBoardPos(4,5),
            this.board.getBoardPos(4,3),
            this.board.getBoardPos(3, 4),
        };
        this.board.setBoardPos(3,2, pieces[0]);
        this.board.setBoardPos(2,5, pieces[1]);
        this.board.setBoardPos(5,2, pieces[2]);
        changed = this.movCont.makeMove(moves[6]);
        this.movCont.updateState(changed);
        assertEquals(2, this.stateMang.getTurn());
        assertNull(this.game.getFurtherJumps());
        assertEquals(0, this.game.getJumps(1).size());
        assertEquals(0, this.game.getJumps(2).size());
        changed = this.movCont.makeMove(moves[7]);
        this.movCont.updateState(changed);
        assertTrue(this.game.getJumps(1).contains(positions2[0]));
        assertTrue(this.game.getJumps(1).contains(positions2[5]));

        setUp();
        this.board.setBoardPos(1, 0, pieces[0]);
        this.board.setBoardPos(2, 1, pieces[2]);
        this.board.setBoardPos(4, 3, pieces[3]);
        this.game.addPlayerJumps(this.board.getBoardPos(1, 0), 1);
        this.game.setTurn(2);
        CheckersMove move = new CheckersMove(4,3,3,2);
        changed = this.movCont.makeMove(move);
        this.movCont.updateState(changed);
        assertEquals(this.p2, this.game.getWinner());

        setUp();
        this.board.setBoardPos(0, 7, pieces[0]);
        this.board.setBoardPos(3, 6, pieces[1]);
        this.board.setBoardPos(1, 6, pieces[2]);
        this.board.setBoardPos(4, 5, pieces[3]);
        this.board.setBoardPos(4, 7, pieces[4]);
        CheckersGamePiece piece = new CheckersGamePiece("B", "K", 1);
        this.board.setBoardPos(5, 4, piece);
        this.game.addPlayerJumps(this.board.getBoardPos(0, 7), 1);
        this.game.addPlayerJumps(this.board.getBoardPos(3, 6), 1);
        this.game.addPlayerJumps(this.board.getBoardPos(4, 5), 2);
        this.game.addPlayerJumps(this.board.getBoardPos(4, 7), 2);
        this.game.setTurn(2);
        move = new CheckersMove(4, 7, 2, 5);
        changed = this.movCont.makeMove(move);
        this.movCont.updateState(changed);
        assertEquals(1, this.game.getJumps(1).size());
        assertFalse(this.game.getJumps(1).contains(this.board.getBoardPos(0, 7)));
        assertTrue(this.game.getJumps(1).contains(this.board.getBoardPos(5, 4)));
    }
}
