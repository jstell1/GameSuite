package checkers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.BoardGamePiece;

public class CoordPairTest {
    
    @Test
    void testEquals() {
        CoordPairImpl pos = new CoordPairImpl(1, 2);
        CoordPairImpl pos2 = new CoordPairImpl(1, 2);
        assertTrue(pos.equals(pos2));
        assertEquals(pos, pos2);
        BoardGamePiece piece = new BoardGamePiece(null, null, 0);
        assertNotEquals(piece, pos2);
        pos2 = new CoordPairImpl(1, 3);
        assertNotEquals(pos2, pos);
    }

    @Test
    void testPieceRef() {
        CoordPairImpl pos = new CoordPairImpl(0, 0);
        BoardGamePiece piece = new BoardGamePiece("bob", "C", 1);
        pos.setPiece(piece);
        assertEquals(pos.getPiece(), piece);
    }
}
