package checkers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGamePiece;

public class CoordPairTest {
    
    @Test
    void testEquals() {
        CheckersCoordPair pos = new CheckersCoordPair(1, 2);
        CheckersCoordPair pos2 = new CheckersCoordPair(1, 2);
        assertTrue(pos.equals(pos2));
        assertEquals(pos, pos2);
        CheckersGamePiece piece = new CheckersGamePiece(null, null, 0);
        assertNotEquals(piece, pos2);
        pos2 = new CheckersCoordPair(1, 3);
        assertNotEquals(pos2, pos);
    }

    @Test
    void testPieceRef() {
        CheckersCoordPair pos = new CheckersCoordPair(0, 0);
        CheckersGamePiece piece = new CheckersGamePiece("bob", "C", 1);
        pos.setPiece(piece);
        assertEquals(pos.getPiece(), piece);
    }
}
