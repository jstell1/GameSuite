package gamesuite.boardgame.test;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.ui.CheckersGameBoardPanel;

public class GUITestMain {
    
    public static void main(String args[]) {
          runTest();
    }

    public static void runTest() {
        SwingUtilities.invokeLater(() -> {
            CheckersCoordPair[][] grid = new CheckersCoordPair[8][8];

            for(int i = 0; i < grid.length; i++) {
                for(int j = 0; j < grid[i].length; j++) {
                    CheckersCoordPair pos = new CheckersCoordPair(i, j);
                    grid[i][j] = pos;
                }
            }
            //TestListener l = null;
            CheckersGameBoard b = new CheckersGameBoard(grid);
            TestListener l = new TestListener();
            CheckersGameBoardPanel p = new CheckersGameBoardPanel(b, 8, l);
            l.setPanel(p);
            l.pack();
            l.setVisible(true);

        });
    }
}
