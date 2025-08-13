package gamesuite.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gamesuite.core.control.GameStateManager;
import gamesuite.core.control.RulesValidator;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public class RulesValidatorTest {
    Player p1, p2;
    GamePiece piece, piece2;
    GameBoard board;

    @BeforeEach
    void setUp() {
        this.p1 = new Player("Bob", 0);
        this.p2 = new Player("Sue", 0);
        this.piece = new GamePiece("R", "C", 1);
        this.piece2 = new GamePiece("B", "C", 1);
        this.board = new GameBoard(8);
    }

    @Test
    void testIsValidPos() {
        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        assertFalse(v.isValidPos(new CoordPair(0, 0)));
        assertFalse(v.isValidPos(new CoordPair(-1, -2)));
        assertFalse(v.isValidPos(new CoordPair(-1, -3)));
        assertFalse(v.isValidPos(new CoordPair(1, 1)));
        assertTrue(v.isValidPos(new CoordPair(0, 1)));
        assertTrue(v.isValidPos(new CoordPair(1, 0)));
    }

    @Test
    void testIsKingable() {
        
        this.board.setBoardPos(0, 1, this.piece);
        this.board.setBoardPos(0, 3, this.piece2);
        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator validator = new RulesValidator(game);
        CoordPair pos = game.getBoardPos(0, 1);
        CoordPair pos2 = game.getBoardPos(0, 3);
        GamePiece piece3 = new GamePiece("B", "C", 1);
        GamePiece piece4 = new GamePiece("B", "K", 1);
        GamePiece piece5 = new GamePiece("R", "K", 1);
        GamePiece piece6 = new GamePiece("R", "C", 1);
        game.setBoardPos(7, 0, piece3);
        game.setBoardPos(7, 2, piece4);
        game.setBoardPos(0, 5, piece5);
        game.setBoardPos(7, 4, piece6);
        CoordPair pos3 = game.getBoardPos(7, 0);
        CoordPair pos4 = game.getBoardPos(7, 2);
        CoordPair pos5 = game.getBoardPos(0, 5);
        CoordPair pos6 = game.getBoardPos(7, 4);

        assertTrue(validator.isKingable(pos));
        assertFalse(validator.isKingable(pos2));
        assertTrue(validator.isKingable(pos3));
        assertFalse(validator.isKingable(pos4));
        assertFalse(validator.isKingable(pos5));
        assertFalse(validator.isKingable(pos6));
        
        GamePiece piece7 = new GamePiece("R", "C", 1);
        CoordPair pos7 = new CoordPair(-1, -2);
        pos7.setPiece(piece7);
        assertFalse(validator.isKingable(pos7));
        pos7 = game.getBoardPos(0, 0);
        pos7.setPiece(piece7);
        assertFalse(validator.isKingable(pos7));
    }

    @Test
    void testIsValidMove() {
        this.board.setBoardPos(0, 1, this.piece2);
        this.board.setBoardPos(1, 2, this.piece);

        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        Move move = new Move(0, 1, 1, 0);
        assertTrue(v.isValidMove(move));
        move = new Move(0, 1, 1, 2);
        assertFalse(v.isValidMove(move));
        move = new Move(1, 2, 0, 3);
        game.setTurn(2);
        assertTrue(v.isValidMove(move));
        game.setTurn(1);
        move = new Move(0, 3, 1, 4);
        assertFalse(v.isValidMove(move));
        move = new Move(-3, -5, 7, 9);
        assertFalse(v.isValidMove(move));
        this.board.setBoardPos(1, 2, null);
        this.board.setBoardPos(0, 3, this.piece);
        this.piece.kingPiece();
        game.setTurn(2);
        move = new Move(0, 3, 1, 2);
        assertTrue(v.isValidMove(move));
        move = new Move(0, 3, -1, -1);
        assertFalse(v.isValidMove(move));
        this.board.setBoardPos(1, 2, this.piece);
        this.board.setBoardPos(0, 3, null);
        move = new Move(1, 2, 0, 2);
        assertFalse(v.isValidMove(move));
        move = new Move(1, 2, 2, 3);
        assertTrue(v.isValidMove(move));
        move = new Move(0, 1, 1, 1);
        assertFalse(v.isValidMove(move));
        move = new Move(0, 1, 2, 3);
        assertFalse(v.isValidMove(move));
        move = new Move(0, 1, 0, 0);
        assertFalse(v.isValidMove(move));
        GamePiece piece3 = new GamePiece("B", "K", 1);
        game.setBoardPos(5, 3, piece3);
        move = new Move(5, 3, 4, 4);
        assertFalse(v.isValidMove(move));
        move = new Move(5, 3, 6, 4);
        assertFalse(v.isValidMove(move));
        move = new Move(5, 3, 4, 3);
        assertFalse(v.isValidMove(move));
        game.setBoardPos(5, 3, null);
        game.setBoardPos(1, 0, piece3);
        move = new Move(1, 0, 0, 1);
        assertFalse(v.isValidMove(move));
    }

    @Test
    void testIsValidMove2() {
        this.board.setBoardPos(0, 1, this.piece2);
        this.board.setBoardPos(1, 2, this.piece);

        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        String name = this.piece2.getName();
        Move[] moves = {
            new Move(0,1,1,0),
            new Move(0,1,1,2),
            new Move(0,1,-1,0),
            new Move(0,1,0,0),
            new Move(0,1,1,1),
            new Move(0,1,2,3),
            new Move(1,2,0,3),
            new Move(1,2,2,3),
            new Move(1,2,2,2),
            new Move(1,2,0,2),
            new Move(-5,-2,-8,9),
            new Move(9, 4, 2, 3),
            new Move(5,6,6,7),
            new Move(5,5,6,6),
        };

        assertTrue(v.isValidMove(moves[0], name));

        for(int i = 1; i < moves.length; i++) {
            assertFalse(v.isValidMove(moves[i], name));
        }
        game.setTurn(2);
        name = this.piece.getName();
        assertTrue(v.isValidMove(moves[6], name));
        assertTrue(v.isValidMove(moves[6], name));

        for(int i = 7; i < moves.length; i++) {
            assertFalse(v.isValidMove(moves[i], name));
        }

        this.piece.kingPiece();
        assertTrue(v.isValidMove(moves[6], name));
        assertTrue(v.isValidMove(moves[7], name));
    }

    @Test
    void testIsValidJump() {
        this.board.setBoardPos(1, 2, this.piece2);
        this.board.setBoardPos(2, 3, this.piece);
        GamePiece piece3 = new GamePiece("B", "K", 1);
        GamePiece piece4 = new GamePiece("B", "C", 1);
        GamePiece piece5 = new GamePiece("R", "C", 1);
        this.board.setBoardPos(3, 2, piece3);
        this.board.setBoardPos(4, 5, piece4);
        this.board.setBoardPos(3, 6, piece5);
        this.piece.kingPiece();
        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        String name = this.piece2.getName();

        Move[] moves = {
            new Move(1,2,3,4),
            new Move(2,3,0,1),
            new Move(2,3,4,1),
            new Move(2, 3, 0, 5),
            new Move(4,5,2,7),
            new Move(3,6,5,4),
            new Move(-1, 9, 3, 4),
            new Move(4, 4, 9, 9),
            new Move(1,2,2,1),
            new Move(2,3,2,1)
        };

        assertTrue(v.isValidJump(moves[0]));
        game.setTurn(2);
        assertTrue(v.isValidJump(moves[1]));
        assertTrue(v.isValidJump(moves[2]));
        
        for(int i = 3; i < moves.length; i++) {
            assertFalse(v.isValidJump(moves[i]));
        }

        game.setTurn(1);
        for(int i = 3; i < moves.length; i++) {
            assertFalse(v.isValidJump(moves[i]));
        }
    }

    @Test
    void testHasValidMoves() {
        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        GameStateManager stateManager = new GameStateManager(game);
        stateManager.initBoard();

        assertTrue(v.hasValidMoves("B"));
        assertTrue(v.hasValidMoves("R"));
        for(int i = 0; i < game.getBoardSize(); i++) {
            GamePiece pi = new GamePiece("B", "C", 1);
            GamePiece pi2 = new GamePiece("R", "C", 1);
            game.setBoardPos(3, i, pi);
            game.setBoardPos(4, i, pi2);
        }

        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
        game.setBoardPos(1, 2, null);

        assertTrue(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
        GamePiece pi = new GamePiece("R", "C", 1);
        game.setBoardPos(2, 3, null);
        game.setBoardPos(1, 2, pi);
        assertTrue(v.hasValidMoves("R"));
        assertTrue(v.hasValidMoves("B"));
        this.board = new GameBoard(8);
        game = new GameState(this.board, this.p1, this.p2);
        v = new RulesValidator(game);
        stateManager = new GameStateManager(game);
        GamePiece piece3 = new GamePiece("B", "C", 1);
        GamePiece piece4 = new GamePiece("B", "C", 1);
        game.setBoardPos(5,0,piece2);
        game.setBoardPos(7, 2, piece3);
        game.setBoardPos(6,1,piece4);
        game.setBoardPos(7, 0, piece);
        assertFalse(v.hasValidMoves("B"));
        assertTrue(v.hasValidMoves("R"));
        GamePiece piece5 = new GamePiece("B", "C", 1);
        game.setBoardPos(6, 3, new GamePiece("B", "C", 1));
        game.setBoardPos(7, 4, new GamePiece("B", "C", 1));
        game.setBoardPos(5, 2, piece5);
        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));

        game = new GameState(this.board, this.p1, this.p2);
        v = new RulesValidator(game);
        stateManager = new GameStateManager(game);

        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
    }

    @Test
    void testHasFurtherJumps() {
        GameState game = new GameState(this.board, this.p1, this.p2);
        RulesValidator v = new RulesValidator(game);
        game.setBoardPos(5, 4, piece);
        game.setBoardPos(4,3, piece2);
        assertTrue(v.hasFurtherJumps(game.getBoardPos(5, 4)));
        assertTrue(v.hasFurtherJumps(game.getBoardPos(4, 3)));
        game.setBoardPos(5, 4, null);
        game.setBoardPos(3, 2, piece);
        piece.kingPiece();
        assertTrue(v.hasFurtherJumps(game.getBoardPos(3, 2)));
        assertFalse(v.hasFurtherJumps(game.getBoardPos(4, 3)));
        game.setBoardPos(4, 3, null);
        game.setBoardPos(5, 4, piece2);
        assertFalse(v.hasFurtherJumps(game.getBoardPos(5, 4)));
        assertFalse(v.hasFurtherJumps(game.getBoardPos(3, 2)));
        assertFalse(v.hasFurtherJumps(game.getBoardPos(0, 0)));
        assertFalse(v.hasFurtherJumps(game.getBoardPos(0, 1)));
        GamePiece pi1 = new GamePiece("B", "K", 1);
        game.setBoardPos(7, 2, pi1);
        GamePiece pi3 = new GamePiece("R", "C", 1);
        game.setBoardPos(6, 1, pi3);
        game.addJustKinged(game.getBoardPos(7, 2));
        //not the right place for this test??
        //assertNotEquals(game.getFurtherJumps(), game.getBoardPos(7, 2));
        //assertTrue(game.getJumps(1).contains(game.getBoardPos(7, 2)));
    }
    
}
