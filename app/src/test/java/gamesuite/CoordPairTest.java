package gamesuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import gamesuite.model.data.CoordPair;
import gamesuite.model.data.GamePiece;

public class CoordPairTest {
    
    @Test
    void testEquals() {
        CoordPair pos = new CoordPair(1, 2);
        CoordPair pos2 = new CoordPair(1, 2);
        assertTrue(pos.equals(pos2));
        assertEquals(pos, pos2);
        GamePiece piece = new GamePiece(null, null, 0);
        assertNotEquals(piece, pos2);
        pos2 = new CoordPair(1, 3);
        assertNotEquals(pos2, pos);
    }

    @Test
    void testPieceRef() {
        CoordPair pos = new CoordPair(0, 0);
        GamePiece piece = new GamePiece("bob", "C", 1);
        pos.setPiece(piece);
        assertEquals(pos.getPiece(), piece);
    }
}
