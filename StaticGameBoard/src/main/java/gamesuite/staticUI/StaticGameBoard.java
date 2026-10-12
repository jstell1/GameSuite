package gamesuite.staticUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.fasterxml.jackson.databind.JsonNode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

public class StaticGameBoard extends GameBoardUI {

    private final int xBoard = 0;
    private final int yBoard = 0;
    private final int dotSize = 4;
    private final int pieceSize = 80;
    private final int offs = 4;
    private final int squareLen = 82;
    private final int boardSize = 8;
    private final int pieceXOffs = 4;
    private final int pieceYOffs = 6;
    private final int leftX = xBoard + 55;
    private final int topY = yBoard + 53;
    private final int rightX = leftX + squareLen;
    private final int xDist = rightX - leftX;
    private final int rightXBound = leftX + xDist*boardSize;
    private final int bottomY = topY + squareLen;
    private final int yDist = bottomY - topY;
    private final int bottomYBound = topY + yDist*boardSize;
    private final int midX = (leftX + rightX) >> 1;
    private final int midY = (topY + bottomY) >> 1;
    private final int pieceLeftX = leftX+pieceXOffs;
    private final int pieceTopY = topY+pieceYOffs;
    private final int yellowX = leftX + offs;
    private final int yellowY = topY + offs;
    private final int yellowXDist = xDist - offs;
    private final int yellowYDist = yDist - offs;
    
    private ImageIcon redPieceIcon;
    private ImageIcon blackPieceIcon;
    private ImageIcon boardIcon;
    private boolean[][] yellows;
    private boolean[][] pieces;
    private int tmpX;
    private int tmpY;

    public StaticGameBoard() {
        URL redPieceURL = getClass().getClassLoader().getResource("images/red.png");
        URL blackPieceURL = getClass().getClassLoader().getResource("images/black.png");
        URL boardURL = getClass().getClassLoader().getResource("images/board.png");
        this.redPieceIcon = new ImageIcon(redPieceURL);
        this.blackPieceIcon = new ImageIcon(blackPieceURL);
        this.boardIcon = new ImageIcon(boardURL);
        this.setPreferredSize(new Dimension(800,800));
        this.setMaximumSize(new Dimension(800,800));
        this.setMinimumSize(new Dimension(800,800));
        yellows = new boolean[boardSize][boardSize];
        pieces = new boolean[boardSize][boardSize];
        tmpX = -1;
        tmpY = -1;
        for(int i = 0; i < boardSize; i++) {
            pieces[0][i] = true;
        }

        this.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                
                System.out.println("Mouse coords: (" + x + "," + y + ")");

                if(x > leftX && x < rightXBound && y > topY && y < bottomYBound) {
                    int xCoord = (x - leftX) / xDist;
                    int yCoord = (y - topY) / yDist;

                    System.out.println("(" + xCoord + "," + yCoord + ")");

                    if(yellows[yCoord][xCoord]) {
                        yellows[yCoord][xCoord] = false;
                    } else {
                        yellows[yCoord][xCoord] = true;
                    }

                    if(tmpX == -1 && tmpY == -1) {
                        tmpX = xCoord; 
                        tmpY = yCoord;
                    } else if(x == tmpX && y == tmpY) {
                        tmpX = -1;
                        tmpY = -1;
                    } else {

                        if(pieces[tmpY][tmpX] && !pieces[yCoord][xCoord]) {
                            pieces[tmpY][tmpX] = false;
                            pieces[yCoord][xCoord] = true;
                        }
                        yellows[yCoord][xCoord] = false;
                        yellows[tmpY][tmpX] = false;
                        tmpX = -1;
                        tmpY = -1;
                    }

                }
                repaint();
            }
        });
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D g2 = (Graphics2D)g;
        g.drawImage(boardIcon.getImage(), xBoard, yBoard, this);

        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(yellows[i][j]) {
                    g.setColor(new Color(170,180,0,90));
                    g.fillRect(yellowX+xDist*j, yellowY+yDist*i, yellowXDist, yellowYDist);
                    g.drawRect(yellowX+xDist*j, yellowY+yDist*i, yellowXDist, yellowYDist);
                }
                if(pieces[i][j]) {
                    setPiece(g, pieceLeftX + xDist*j, pieceTopY+yDist*i, pieceSize);
                }
            }
        }
        
      
     
      
        g.drawImage(redPieceIcon.getImage(), pieceLeftX, pieceTopY, pieceSize, pieceSize, this);

        setDot(g, leftX, topY, dotSize);
        setDot(g, rightX, topY, dotSize);
        setDot(g, rightX, bottomY, dotSize);
        setDot(g, leftX, bottomY, dotSize);
        setDot(g, midX, midY, dotSize);
        setDot(g, rightXBound, topY, dotSize);
        setDot(g, rightXBound, bottomYBound, dotSize);



    }

    public void setPiece(Graphics g, int pieceX, int pieceY, int pieceSize) {
           g.drawImage(redPieceIcon.getImage(), pieceX, pieceY, pieceSize, pieceSize, this);

    }

    public void setDot(Graphics g, int x, int y, int size) {
          g.setColor(Color.YELLOW);
        g.fillOval(x,y,size,size);
        g.drawOval(x,y,size,size);
       
    }

    @Override
    public void addYellowedPanel(JsonNode arg0) {
      
    }

    @Override
    public int getTurn() {
        return 1;
    }

    @Override
    public String getWinner() {
       return null;
    }

    @Override
    public boolean isGameOver() {
    return false;
    }

    @Override
    public void removeYellowed(int arg0, int arg1) {
       
    }

    @Override
    public void setGameState(JsonNode arg0) {
        
    }

    @Override
    public void setListener(UIListener arg0) {
    
    }

    @Override
    public void update(JsonNode arg0) {
        
    }
}
