package gamesuite.boardgame.test;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.ui.GameBoardPanel;

public class GUITestMain {
    
    public static void main(String args[]) {
          runTest();
    }

    public static void runTest() {
        SwingUtilities.invokeLater(() -> {
            CoordPairImpl[][] grid = new CoordPairImpl[8][8];

            for(int i = 0; i < grid.length; i++) {
                for(int j = 0; j < grid[i].length; j++) {
                    CoordPairImpl pos = new CoordPairImpl(i, j);
                    grid[i][j] = pos;
                }
            }
            //TestListener l = null;
            GameBoardImpl b = new GameBoardImpl(grid);
            TestListener l = new TestListener();
            GameBoardPanel p = new GameBoardPanel(b, 8, l);
            l.setPanel(p);
            l.pack();
            l.setVisible(true);

        });
    }
}
