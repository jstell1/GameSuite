package checkers;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import checkers.control.GameStateManager;
import checkers.control.RulesValidator;
import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;

public class RulesValidatorTest {
    CheckersPlayer p1, p2;
    CheckersGamePiece piece, piece2;
    CheckersGameBoard board;

    @BeforeEach
    void setUp() {
        this.p1 = new CheckersPlayer("Bob", 0);
        this.p2 = new CheckersPlayer("Sue", 0);
        this.piece = new CheckersGamePiece("R", "C", 1);
        this.piece2 = new CheckersGamePiece("B", "C", 1);
        this.board = new CheckersGameBoard(8);
    }

    @Test
    void testIsValidPos() {
        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        assertFalse(v.isValidPos(new CheckersCoordPair(0, 0)));
        assertFalse(v.isValidPos(new CheckersCoordPair(-1, -2)));
        assertFalse(v.isValidPos(new CheckersCoordPair(-1, -3)));
        assertFalse(v.isValidPos(new CheckersCoordPair(1, 1)));
        assertTrue(v.isValidPos(new CheckersCoordPair(0, 1)));
        assertTrue(v.isValidPos(new CheckersCoordPair(1, 0)));
    }

    @Test
    void testIsKingable() {
        
        this.board.setBoardPos(0, 1, this.piece);
        this.board.setBoardPos(0, 3, this.piece2);
        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator validator = new RulesValidator(game, this.board);
        CheckersCoordPair pos = this.board.getBoardPos(0, 1);
        CheckersCoordPair pos2 = this.board.getBoardPos(0, 3);
        CheckersGamePiece piece3 = new CheckersGamePiece("B", "C", 1);
        CheckersGamePiece piece4 = new CheckersGamePiece("B", "K", 1);
        CheckersGamePiece piece5 = new CheckersGamePiece("R", "K", 1);
        CheckersGamePiece piece6 = new CheckersGamePiece("R", "C", 1);
        this.board.setBoardPos(7, 0, piece3);
        this.board.setBoardPos(7, 2, piece4);
        this.board.setBoardPos(0, 5, piece5);
        this.board.setBoardPos(7, 4, piece6);
        CheckersCoordPair pos3 = this.board.getBoardPos(7, 0);
        CheckersCoordPair pos4 = this.board.getBoardPos(7, 2);
        CheckersCoordPair pos5 = this.board.getBoardPos(0, 5);
        CheckersCoordPair pos6 = this.board.getBoardPos(7, 4);

        assertTrue(validator.isKingable(pos));
        assertFalse(validator.isKingable(pos2));
        assertTrue(validator.isKingable(pos3));
        assertFalse(validator.isKingable(pos4));
        assertFalse(validator.isKingable(pos5));
        assertFalse(validator.isKingable(pos6));
        
        CheckersGamePiece piece7 = new CheckersGamePiece("R", "C", 1);
        CheckersCoordPair pos7 = new CheckersCoordPair(-1, -2);
        pos7.setPiece(piece7);
        assertFalse(validator.isKingable(pos7));
        pos7 = this.board.getBoardPos(0, 0);
        pos7.setPiece(piece7);
        assertFalse(validator.isKingable(pos7));
    }

    @Test
    void testIsValidMove() {
        this.board.setBoardPos(0, 1, this.piece2);
        this.board.setBoardPos(1, 2, this.piece);

        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        CheckersMove move = new CheckersMove(0, 1, 1, 0);
        assertTrue(v.isValidMove(move));
        move = new CheckersMove(0, 1, 1, 2);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(1, 2, 0, 3);
        game.setTurn(2);
        assertTrue(v.isValidMove(move));
        game.setTurn(1);
        move = new CheckersMove(0, 3, 1, 4);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(-3, -5, 7, 9);
        assertFalse(v.isValidMove(move));
        this.board.setBoardPos(1, 2, null);
        this.board.setBoardPos(0, 3, this.piece);
        this.piece.kingPiece();
        game.setTurn(2);
        move = new CheckersMove(0, 3, 1, 2);
        assertTrue(v.isValidMove(move));
        move = new CheckersMove(0, 3, -1, -1);
        assertFalse(v.isValidMove(move));
        this.board.setBoardPos(1, 2, this.piece);
        this.board.setBoardPos(0, 3, null);
        move = new CheckersMove(1, 2, 0, 2);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(1, 2, 2, 3);
        assertTrue(v.isValidMove(move));
        move = new CheckersMove(0, 1, 1, 1);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(0, 1, 2, 3);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(0, 1, 0, 0);
        assertFalse(v.isValidMove(move));
        CheckersGamePiece piece3 = new CheckersGamePiece("B", "K", 1);
        this.board.setBoardPos(5, 3, piece3);
        move = new CheckersMove(5, 3, 4, 4);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(5, 3, 6, 4);
        assertFalse(v.isValidMove(move));
        move = new CheckersMove(5, 3, 4, 3);
        assertFalse(v.isValidMove(move));
        this.board.setBoardPos(5, 3, null);
        this.board.setBoardPos(1, 0, piece3);
        move = new CheckersMove(1, 0, 0, 1);
        assertFalse(v.isValidMove(move));
    }

    @Test
    void testIsValidMove2() {
        this.board.setBoardPos(0, 1, this.piece2);
        this.board.setBoardPos(1, 2, this.piece);

        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        String name = this.piece2.getName();
        CheckersMove[] moves = {
            new CheckersMove(0,1,1,0),
            new CheckersMove(0,1,1,2),
            new CheckersMove(0,1,-1,0),
            new CheckersMove(0,1,0,0),
            new CheckersMove(0,1,1,1),
            new CheckersMove(0,1,2,3),
            new CheckersMove(1,2,0,3),
            new CheckersMove(1,2,2,3),
            new CheckersMove(1,2,2,2),
            new CheckersMove(1,2,0,2),
            new CheckersMove(-5,-2,-8,9),
            new CheckersMove(9, 4, 2, 3),
            new CheckersMove(5,6,6,7),
            new CheckersMove(5,5,6,6),
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
        CheckersGamePiece piece3 = new CheckersGamePiece("B", "K", 1);
        CheckersGamePiece piece4 = new CheckersGamePiece("B", "C", 1);
        CheckersGamePiece piece5 = new CheckersGamePiece("R", "C", 1);
        this.board.setBoardPos(3, 2, piece3);
        this.board.setBoardPos(4, 5, piece4);
        this.board.setBoardPos(3, 6, piece5);
        this.piece.kingPiece();
        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        String name = this.piece2.getName();

        CheckersMove[] moves = {
            new CheckersMove(1,2,3,4),
            new CheckersMove(2,3,0,1),
            new CheckersMove(2,3,4,1),
            new CheckersMove(2, 3, 0, 5),
            new CheckersMove(4,5,2,7),
            new CheckersMove(3,6,5,4),
            new CheckersMove(-1, 9, 3, 4),
            new CheckersMove(4, 4, 9, 9),
            new CheckersMove(1,2,2,1),
            new CheckersMove(2,3,2,1)
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
        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        GameStateManager stateManager = new GameStateManager(game, this.board);
        stateManager.initBoard();

        assertTrue(v.hasValidMoves("B"));
        assertTrue(v.hasValidMoves("R"));
        for(int i = 0; i < this.board.getSideLength(); i++) {
            CheckersGamePiece pi = new CheckersGamePiece("B", "C", 1);
            CheckersGamePiece pi2 = new CheckersGamePiece("R", "C", 1);
            this.board.setBoardPos(3, i, pi);
            this.board.setBoardPos(4, i, pi2);
        }

        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
        this.board.setBoardPos(1, 2, null);

        assertTrue(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
        CheckersGamePiece pi = new CheckersGamePiece("R", "C", 1);
        this.board.setBoardPos(2, 3, null);
        this.board.setBoardPos(1, 2, pi);
        assertTrue(v.hasValidMoves("R"));
        assertTrue(v.hasValidMoves("B"));
        this.board = new CheckersGameBoard(8);
        game = new CheckersGameState(this.p1, this.p2);
        v = new RulesValidator(game, this.board);
        stateManager = new GameStateManager(game, this.board);
        CheckersGamePiece piece3 = new CheckersGamePiece("B", "C", 1);
        CheckersGamePiece piece4 = new CheckersGamePiece("B", "C", 1);
        this.board.setBoardPos(5,0,piece2);
        this.board.setBoardPos(7, 2, piece3);
        this.board.setBoardPos(6,1,piece4);
        this.board.setBoardPos(7, 0, piece);
        assertFalse(v.hasValidMoves("B"));
        assertTrue(v.hasValidMoves("R"));
        CheckersGamePiece piece5 = new CheckersGamePiece("B", "C", 1);
        this.board.setBoardPos(6, 3, new CheckersGamePiece("B", "C", 1));
        this.board.setBoardPos(7, 4, new CheckersGamePiece("B", "C", 1));
        this.board.setBoardPos(5, 2, piece5);
        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));

        game = new CheckersGameState(this.p1, this.p2);
        v = new RulesValidator(game, this.board);
        stateManager = new GameStateManager(game, this.board);

        assertFalse(v.hasValidMoves("B"));
        assertFalse(v.hasValidMoves("R"));
    }

    @Test
    void testHasFurtherJumps() {
        CheckersGameState game = new CheckersGameState(this.p1, this.p2);
        RulesValidator v = new RulesValidator(game, this.board);
        this.board.setBoardPos(5, 4, piece);
        this.board.setBoardPos(4,3, piece2);
        assertTrue(v.hasFurtherJumps(this.board.getBoardPos(5, 4)));
        assertTrue(v.hasFurtherJumps(this.board.getBoardPos(4, 3)));
        this.board.setBoardPos(5, 4, null);
        this.board.setBoardPos(3, 2, piece);
        piece.kingPiece();
        assertTrue(v.hasFurtherJumps(this.board.getBoardPos(3, 2)));
        assertFalse(v.hasFurtherJumps(this.board.getBoardPos(4, 3)));
        this.board.setBoardPos(4, 3, null);
        this.board.setBoardPos(5, 4, piece2);
        assertFalse(v.hasFurtherJumps(this.board.getBoardPos(5, 4)));
        assertFalse(v.hasFurtherJumps(this.board.getBoardPos(3, 2)));
        assertFalse(v.hasFurtherJumps(this.board.getBoardPos(0, 0)));
        assertFalse(v.hasFurtherJumps(this.board.getBoardPos(0, 1)));
        CheckersGamePiece pi1 = new CheckersGamePiece("B", "K", 1);
        this.board.setBoardPos(7, 2, pi1);
        CheckersGamePiece pi3 = new CheckersGamePiece("R", "C", 1);
        this.board.setBoardPos(6, 1, pi3);
        game.addJustKinged(this.board.getBoardPos(7, 2));
        //not the right place for this test??
        //assertNotEquals(game.getFurtherJumps(), game.getBoardPos(7, 2));
        //assertTrue(game.getJumps(1).contains(game.getBoardPos(7, 2)));
    }
    
}
