package checkers;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.test.TestListener;
import gamesuite.boardgame.ui.CheckersCoordPairPanel;
import gamesuite.boardgame.ui.CheckersGameBoardPanel;
import gamesuite.core.ui.UIListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;


public class BoardUITest {
    
    TestListener l;
    @Test
    void runUI() {
        //SwingUtilities.invokeAndWait(null);
        
        CountDownLatch closed = new CountDownLatch(1);
        try {
            SwingUtilities.invokeAndWait(() -> {
                CheckersCoordPair[][] grid = new CheckersCoordPair[8][8];
   
                for(int i = 0; i < grid.length; i++) {
                    for(int j = 0; j < grid[i].length; j++) {
                        CheckersCoordPair pos = new CheckersCoordPair(i, j);
                        grid[i][j] = pos;
                    }
                }
                //TestListener l = null;
                CheckersGameBoard b = new CheckersGameBoard(grid);
                this.l = new TestListener();
                CheckersGameBoardPanel p = new CheckersGameBoardPanel(b, 8, l);
                this.l.setPanel(p);
   
                this.l.addWindowListener(new WindowAdapter() {
                    @Override public void windowClosed(WindowEvent e) {
                        closed.countDown();
                    }
                });
                this.l.pack();
                this.l.setVisible(true);

            });
        } catch (InvocationTargetException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //this.add(p);
        //p.setVisible(true);
        try {
            closed.await();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

   
}
