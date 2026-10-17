package gamesuite.staticUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;
import java.util.Map;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGameMove;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import gamesuite.staticUI.model.Piece;

public class StaticGameBoard extends GameBoardUI {

    //top left corner of png
    private final int xBoard = 0;
    private final int yBoard = 0;

    //size of test dots
    private final int dotSize = 4;
    private final int pieceSize = 70;
    //offset for the line width
    private final int offs = 4;
    private final int squareLen = 82;
    private final int boardSize = 8;
    private final int pieceXOffs = 3;
    private final int pieceYOffs = 5;

    //top left corner of actually board outline more or less centered in the line width
    private final int leftX = xBoard + 58;
    private final int topY = yBoard + 55;
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

    //placement of yellow square
    private final int yellowX = leftX + offs;
    private final int yellowY = topY + offs;
    private final int yellowXDist = xDist - offs;
    private final int yellowYDist = yDist - offs;
    
    private ImageIcon redPieceIcon;
    private ImageIcon blackPieceIcon;
    private ImageIcon redKingIcon;
    private ImageIcon blackKingIcon;
    private ImageIcon boardIcon;
    private boolean[][] yellows;
    private boolean[][] pieces;
    private int tmpX;
    private int tmpY;
    private int tmpX2;
    private int tmpY2;
    private String[] teamNames;

    private Piece[][] board;
    private UIListener listener;
    private BoardGameState game;
    private Map<String, ImageIcon> pieceMap1;
    private Map<String, ImageIcon> pieceMap2;


    public StaticGameBoard(UIListener listener, Piece[][] board, String[] teamNames, BoardGameState game, Map<String, ImageIcon> pieceMap1, Map<String, ImageIcon> pieceMap2, ImageIcon boardIcon) {
        this.pieceMap1 = pieceMap1;
        this.pieceMap2 = pieceMap2;
        this.teamNames = teamNames;
        this.game = game;
        this.boardIcon = boardIcon;
       // URL redPieceURL = getClass().getClassLoader().getResource("images/red.png");
       // URL blackPieceURL = getClass().getClassLoader().getResource("images/black.png");
       // URL boardURL = getClass().getClassLoader().getResource("images/board.png");
      //  URL redKingURL = getClass().getClassLoader().getResource("images/red_king.png");
       // URL blackKingURL = getClass().getClassLoader().getResource("images/black_king.png");
        //this.redPieceIcon = new ImageIcon(redPieceURL);
       // this.blackPieceIcon = new ImageIcon(blackPieceURL);
       // this.redKingIcon = new ImageIcon(redKingURL);
      //  this.blackKingIcon = new ImageIcon(blackKingURL);
       // this.boardIcon = new ImageIcon(boardURL);
        this.setPreferredSize(new Dimension(800,800));
        this.setMaximumSize(new Dimension(800,800));
        this.setMinimumSize(new Dimension(800,800));
        yellows = new boolean[boardSize][boardSize];
        pieces = new boolean[boardSize][boardSize];
        this.board = board;
        this.listener = listener;
        tmpX = -1;
        tmpY = -1;
        this.tmpX2 = -1;
        this.tmpY2 = -1;
        this.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                
                System.out.println("Mouse coords: (" + x + "," + y + ")");

                if(x > leftX && x < rightXBound && y > topY && y < bottomYBound && StaticGameBoard.this.listener.getIsBoardEnabled()) {
                    int xCoord = (x - leftX) / xDist;
                    int yCoord = (y - topY) / yDist;

                    System.out.println("(" + xCoord + "," + yCoord + ")");

                    if(StaticGameBoard.this.yellows[yCoord][xCoord]) {
                        StaticGameBoard.this.yellows[yCoord][xCoord] = false;
                    } else {
                        StaticGameBoard.this.yellows[yCoord][xCoord] = true;
                    }

                    if(StaticGameBoard.this.tmpX == -1 && StaticGameBoard.this.tmpY == -1) {
                        StaticGameBoard.this.tmpX = yCoord; 
                        StaticGameBoard.this.tmpY = xCoord;
                    } else if(yCoord == StaticGameBoard.this.tmpX && xCoord == StaticGameBoard.this.tmpY) {
                        StaticGameBoard.this.tmpX = -1;
                        StaticGameBoard.this.tmpY = -1;
                    } else {
                        StaticGameBoard.this.tmpX2 = yCoord;
                        StaticGameBoard.this.tmpY2 = xCoord;

                        StaticGameBoard.this.listener.disabledBoard();
                          BoardGameMove move = new BoardGameMove(
                                StaticGameBoard.this.tmpX, 
                                StaticGameBoard.this.tmpY, 
                                StaticGameBoard.this.tmpX2,
                                StaticGameBoard.this.tmpY2
                            );
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode moveNode = mapper.valueToTree(move);
                        StaticGameBoard.this.listener.sendMove(moveNode);
                    }

                }
                repaint();
            }
        });
    }

    public void setTeamNames(String[] teamNames) {
        this.teamNames = teamNames;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    @Override 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Graphics2D g2 = (Graphics2D)g;
        g.drawImage(boardIcon.getImage(), xBoard, yBoard, this);
        //g.drawOval(leftX, bottomY, WIDTH, HEIGHT);

        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                if(yellows[i][j]) {
                    g.setColor(new Color(170,180,0,90));
                    g.fillRect(yellowX+xDist*j, yellowY+yDist*i, yellowXDist, yellowYDist);
                    g.drawRect(yellowX+xDist*j, yellowY+yDist*i, yellowXDist, yellowYDist);
                }
                
                if(board[i][j] != null) {
                    String team = board[i][j].getTeam();
                    String type = board[i][j].getType();
                    setPiece(g, pieceLeftX + xDist*j, pieceTopY+yDist*i, pieceSize, team, type);
                }
                
            }
        }
    }

    public void setPiece(Graphics g, int pieceX, int pieceY, int pieceSize, String team, String type) {



        if(team.equals(teamNames[1])) {
           // Image ico = type.equals("K") ? redKingIcon.getImage() : redPieceIcon.getImage();
           Image ico = this.pieceMap2.get(type).getImage();
            
            g.drawImage(ico, pieceX, pieceY, pieceSize, pieceSize, this);

        } else {
           // Image ico = type.equals("K") ? blackKingIcon.getImage() : blackPieceIcon.getImage();
           Image ico = this.pieceMap1.get(type).getImage();
            g.drawImage(ico, pieceX, pieceY, pieceSize, pieceSize, this);
        }

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
        return this.game.getTurn();
    }

    @Override
    public String getWinner() {
       return this.game.getWinner().getName();
    }

    @Override
    public boolean isGameOver() {
    return this.game.isGameOver();
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
    public void update(JsonNode gameState) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BoardGameState gameView = mapper.treeToValue(gameState, BoardGameState.class);
            this.game = gameView;
            List<CoordPairImpl> changed = gameView.getChangedPos();
            for(CoordPairImpl pos : changed) {
                BoardGamePiece piece = pos.getPiece();
                int x = pos.getX();
                int y = pos.getY();

                if(piece == null) {
                    board[x][y] = null;
                } else {
                    board[x][y] = new Piece(
                        piece.getName(),
                        piece.getTeam(),
                        piece.getType(),
                        piece.getVal()
                    );
                }
            }

            if(this.tmpX > -1 && this.tmpY > -1) {
                this.yellows[this.tmpX][this.tmpY] = false;
                this.tmpX = -1;
                this.tmpY = -1;
            }

            if(this.tmpX2 > -1 && this.tmpY2 > -1) {
                this.yellows[this.tmpX2][this.tmpY2] = false;
                this.tmpX2 = -1;
                this.tmpY2 = -1;
            }
            repaint();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void setPromotionChoices(String[] list) {
        this.listener.disabledBoard();
        String choice = (String) JOptionPane.showInputDialog(
                this, 
                "Pick one: ", 
                "Promotion", 
                JOptionPane.QUESTION_MESSAGE,
                null,
                list,
                list[0]
            );

        if(choice == null) {
            choice = list[0];
        }

        this.listener.sendPromoteChoice(choice);
    }
}
