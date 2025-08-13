package gamesuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gamesuite.core.control.GameStateManager;
import gamesuite.core.control.MoveController;
import gamesuite.core.control.RulesValidator;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.view.CoordPairView;

public class MoveControllerTest {
    
    Player p1;
    Player p2;
    GameBoard board;
    GameState game;
    GameStateManager stateMang;
    RulesValidator validator;
    MoveController movCont;

    @BeforeEach
    void setUp() {
        this.p1 = new Player("Frodo", 0);
        this.p2 = new Player("Sam", 0);
        this.board = new GameBoard(8);
        this.game = new GameState(board, p1, p2);
        this.stateMang = new GameStateManager(game);
        this.validator = new RulesValidator(game);
        this.movCont = new MoveController(validator, stateMang);
    }
    
    @Test
    void testCheckMove() {
        

        GamePiece[] pieces = {
            new GamePiece("B", "C", 1),
            new GamePiece("B", "C", 1),
            new GamePiece("B", "C", 1),
            new GamePiece("B", "C", 1),
            new GamePiece("B", "C", 1),
            new GamePiece("R", "C", 1),//5
            new GamePiece("R", "C", 1),
            new GamePiece("R", "C", 1),
            new GamePiece("R", "C", 1),
            new GamePiece("R", "C", 1),
        };

        this.game.setTurn(2);
        this.game.addPlayerJumps(4, 1, 2);
        this.board.setBoardPos(3, 0, pieces[0]);
        this.board.setBoardPos(3, 2, pieces[1]);
        this.board.setBoardPos(4, 1, pieces[5]);
        this.board.setBoardPos(6, 3, pieces[6]);
        Move move = new Move(6, 3, 5, 4);
        assertFalse(this.movCont.checkMove(move));
        this.board.setBoardPos(5, 4, pieces[2]);
        this.game.addPlayerJumps(6, 3, 2);
        move = new Move(4, 1, 2,3);
        assertTrue(this.movCont.checkMove(move));
        move = new Move(6,3,4,3);
        assertFalse(this.movCont.checkMove(move));
        move = new Move(6,3,4,5);
        assertTrue(this.movCont.checkMove(move));
        game.setFurtherJumps(board.getBoardPos(4, 1));
        assertFalse(this.movCont.checkMove(move));
        this.game.setFurtherJumps(null);
        move = new Move(6, 3, 5, 2);
        assertFalse(this.movCont.checkMove(move));
        this.game.removePlayerJumps(6, 3, 2);
        this.game.removePlayerJumps(4, 1, 2);
        move = new Move(6, 3, 5, 2);
        assertTrue(this.movCont.checkMove(move));
        move = new Move(6, 3, 5, 3);
        assertFalse(this.movCont.checkMove(move));

        this.board = new GameBoard(8);
        this.game = new GameState(this.board, this.p1, this.p2);
        this.stateMang = new GameStateManager(this.game);
        this.validator = new RulesValidator(this.game);
        this.movCont = new MoveController(this.validator, this.stateMang);
        this.game.setBoardPos(3, 2, pieces[0]);
        this.game.setBoardPos(4, 1, pieces[5]);
        this.game.setBoardPos(6, 1, pieces[6]);
        this.game.addPlayerJumps(4, 1, 2);
        move = new Move(6, 1, 5, 2);
        assertFalse(this.movCont.checkMove(move));
    }

    @Test
    void testMakeMove() {
        CoordPair pos = this.board.getBoardPos(5, 4);
        GamePiece piece = new GamePiece("B", "C", 1);
        pos.setPiece(piece);
        CoordPair pos2 = this.board.getBoardPos(6, 5);
        GamePiece piece2 = new GamePiece("R", "C", 1);
        pos2.setPiece(piece2);
        CoordPair pos3 = this.board.getBoardPos(3, 2);
        GamePiece piece3 = new GamePiece("R", "C", 1);
        pos3.setPiece(piece3);
        Move move = new Move(5, 4, 7, 6);
        Move move2 = new Move(3, 2, 2, 3);
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
        GamePiece[] pieces = {
            new GamePiece("B", "C", 1),
            new GamePiece("B", "C", 1),
            new GamePiece("R", "C", 1),
            new GamePiece("R", "C", 1),
            new GamePiece("R","C",1)
        };

        this.game.setBoardPos(3,2, pieces[0]);
        this.game.setBoardPos(2,5, pieces[1]);
        this.game.setBoardPos(5,2, pieces[2]);
        this.game.setBoardPos(4,5, pieces[3]);

        CoordPair[] positions = {
            this.game.getBoardPos(3,2),
            this.game.getBoardPos(2,5),
            this.game.getBoardPos(5,2),
            this.game.getBoardPos(4,5),
            this.game.getBoardPos(4,3),
            this.game.getBoardPos(3, 4),
        };

        Move[] moves = {
            new Move(3, 2, 4, 3),
            new Move(5, 2, 3, 4),
            new Move(3, 4, 1, 6),
            new Move(4, 5, 3, 4),
            new Move(2, 5, 4, 3),
            new Move(4, 3, 6, 1),
            new Move(2, 5, 3, 4),
            new Move(5,2,4,3)
        };

        List<CoordPairView> changed = this.movCont.makeMove(moves[0]);
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
        CoordPair[] positions2 = {
            this.game.getBoardPos(3,2),
            this.game.getBoardPos(2,5),
            this.game.getBoardPos(5,2),
            this.game.getBoardPos(4,5),
            this.game.getBoardPos(4,3),
            this.game.getBoardPos(3, 4),
        };
        this.game.setBoardPos(3,2, pieces[0]);
        this.game.setBoardPos(2,5, pieces[1]);
        this.game.setBoardPos(5,2, pieces[2]);
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
        this.game.setBoardPos(1, 0, pieces[0]);
        this.game.setBoardPos(2, 1, pieces[2]);
        this.game.setBoardPos(4, 3, pieces[3]);
        this.game.addPlayerJumps(1, 0, 1);
        this.game.setTurn(2);
        Move move = new Move(4,3,3,2);
        changed = this.movCont.makeMove(move);
        this.movCont.updateState(changed);
        assertEquals(this.p2, this.game.getWinner());

        setUp();
        this.game.setBoardPos(0, 7, pieces[0]);
        this.game.setBoardPos(3, 6, pieces[1]);
        this.game.setBoardPos(1, 6, pieces[2]);
        this.game.setBoardPos(4, 5, pieces[3]);
        this.game.setBoardPos(4, 7, pieces[4]);
        GamePiece piece = new GamePiece("B", "K", 1);
        this.game.setBoardPos(5, 4, piece);
        this.game.addPlayerJumps(0, 7, 1);
        this.game.addPlayerJumps(3, 6, 1);
        this.game.addPlayerJumps(4, 5, 2);
        this.game.addPlayerJumps(4, 7, 2);
        this.game.setTurn(2);
        move = new Move(4, 7, 2, 5);
        changed = this.movCont.makeMove(move);
        this.movCont.updateState(changed);
        assertEquals(1, this.game.getJumps(1).size());
        assertFalse(this.game.getJumps(1).contains(this.game.getBoardPos(0, 7)));
        assertTrue(this.game.getJumps(1).contains(this.game.getBoardPos(5, 4)));
    }
}
