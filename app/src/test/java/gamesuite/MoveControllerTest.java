package gamesuite;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gamesuite.model.data.GameBoard;
import gamesuite.model.data.GamePiece;
import gamesuite.model.data.GameState;
import gamesuite.model.data.Move;
import gamesuite.model.data.Player;
import gamesuite.model.logic.GameStateManager;
import gamesuite.model.logic.MoveController;
import gamesuite.model.logic.RulesValidator;

public class MoveControllerTest {
    
    Player p1 = new Player("Bob", 0);
    Player p2 = new Player("Sara", 0);
    
    @Test
    void testCheckMove() {
        GameBoard board = new GameBoard(8);
        GameState game = new GameState(board, null, null);
        GameStateManager stateMang = new GameStateManager(game);
        RulesValidator validator = new RulesValidator(game);
        MoveController movCont = new MoveController(validator, stateMang);

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

        game.setTurn(2);
        game.addPlayerJumps(4, 1, 2);
        board.setBoardPos(3, 0, pieces[0]);
        board.setBoardPos(3, 2, pieces[1]);
        board.setBoardPos(4, 1, pieces[5]);
        board.setBoardPos(6, 3, pieces[6]);
        Move move = new Move(6, 3, 5, 4);
        assertFalse(movCont.checkMove(move));
        board.setBoardPos(5, 4, pieces[2]);
        game.addPlayerJumps(6, 3, 2);
        move = new Move(4, 1, 2,3);
        assertTrue(movCont.checkMove(move));
        move = new Move(6,3,4,3);
        assertFalse(movCont.checkMove(move));
        move = new Move(6,3,4,5);
        assertTrue(movCont.checkMove(move));
        game.setFurtherJumps(board.getBoardPos(4, 1));
        assertFalse(movCont.checkMove(move));
        move = new Move(4, 1, 2, 3);
        assertTrue(movCont.checkMove(move));
        game.setFurtherJumps(null);
        move = new Move(6, 3, 5, 2);
        assertFalse(movCont.checkMove(move));
        game.removePlayerJumps(6, 3, 2);
        game.removePlayerJumps(4, 1, 2);
        move = new Move(6, 3, 5, 2);
        assertTrue(movCont.checkMove(move));
        move = new Move(6, 3, 5, 3);
        assertFalse(movCont.checkMove(move));

        board = new GameBoard(8);
        game = new GameState(board, null, null);
        stateMang = new GameStateManager(game);
        validator = new RulesValidator(game);
        movCont = new MoveController(validator, stateMang);
        game.setBoardPos(3, 2, pieces[0]);
        game.setBoardPos(4, 1, pieces[5]);
        game.setBoardPos(6, 1, pieces[6]);
        game.addPlayerJumps(4, 1, 2);
        move = new Move(6, 1, 5, 2);
        assertFalse(movCont.checkMove(move));
    }

    @Test
    void testMakeMove() {
        
    }

    @Test
    void testUpdateState() {
        
    }
}
