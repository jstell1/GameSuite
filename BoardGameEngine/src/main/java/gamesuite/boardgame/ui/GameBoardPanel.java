package gamesuite.boardgame.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGameMove;

public class GameBoardPanel extends GameBoardUI {

    private BoardGameState gameView; 
    private GameBoardImpl board;
    private int size;
    private List<CoordPairImpl> changed;
    private CoordPairPanel[][] boardPanel;
    private UIListener listener;
    private Set<CoordPairPanel> yellowed;
    private int tmpX;
    private int tmpY;
    private int playerTurn;

    public GameBoardPanel(GameBoardImpl board, int size, UIListener listener) {
        super();
        this.tmpX = -1;
        this.tmpY = -1;
        this.listener = listener;
        this.boardPanel = new CoordPairPanel[size][size];
        this.yellowed = new HashSet<>();
        this.changed = null;
        this.board = board;
        this.size = size;
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                CoordPairPanel pos = null;
                if ((i + j) % 2 == 0) {
                    pos = new CoordPairPanel(board.getBoardPos(i, j), this.listener, Color.LIGHT_GRAY, this);
                } else {
                    pos = new CoordPairPanel(board.getBoardPos(i, j), this.listener, Color.DARK_GRAY, this);
                }

                pos.setPreferredSize(new Dimension(size / 8, size / 8));
                boardPanel[i][j] = pos;
                this.add(pos);
            }
        }
    }

    public void setListener(UIListener listener) {
        if(this.listener == null)
            this.listener = listener;
    }

    public void updateBoard(List<CoordPairImpl> changes) {
        
        
        this.changed = changes;
        if(this.changed != null) {
            CoordPairPanel[] updateList = new CoordPairPanel[this.changed.size()];
            for(int i = 0; i < this.changed.size(); i++) {
                CoordPairImpl pos = this.changed.get(i);
                BoardGamePiece piece = pos.getPiece();
                CoordPairPanel p = this.boardPanel[pos.getX()][pos.getY()];
                p.setPiece(piece);
                
            }
            this.changed = null;        
        }
        
        for(CoordPairPanel pos : this.yellowed) {
            pos.resetBackground();
        }
        
        this.yellowed.clear();
    }
    
    @Override
    public void update(JsonNode gameState) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.gameView = mapper.treeToValue(gameState, BoardGameState.class);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(this.gameView != null) {
            List<CoordPairImpl> changed = this.gameView.getChangedPos();
            
            updateBoard(changed);
        }
        this.tmpX = -1;
        this.tmpY = -1;

    }

    public void setGameState(BoardGameState gameView) {
            this.gameView = gameView;
            // TODO Auto-generated catch block
    }
    
    public int getTurn() {
        return this.gameView.getTurn();
    }
    
    public void removeYellowed(int x, int y) {
            this.yellowed.remove(this.boardPanel[x][y]);
    }

    public void addYellowedPanel(CoordPairPanel pos) {
            this.yellowed.add(pos);
            // TODO Auto-generated catch block
    }

    public CoordPairPanel getBoardPos(int x, int y) {
        return this.boardPanel[x][y];
    }

    

     public void sendChange(int x, int y) {
        
        if(this.tmpX == -1 && this.tmpY == -1) {
            this.tmpX = x;
            this.tmpY = y;
        } else if(x == this.tmpX && y == this.tmpY) {
            this.tmpX = -1;
            this.tmpY = -1;
            removeYellowed(x, y);
        } else {
            this.listener.disabledBoard();
            BoardGameMove move = new BoardGameMove(this.tmpX, this.tmpY, x, y);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode moveNode = mapper.valueToTree(move);
            this.listener.sendMove(moveNode);
            

        }
            
    }

    @Override
    public boolean isGameOver() {
        return this.gameView.isGameOver();
    }

    @Override
    public void setGameState(JsonNode gameState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void addYellowedPanel(JsonNode pos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addYellowedPanel'");
    }

    @Override
    public String getWinner() {
        return this.gameView.getWinner().getName();
    }

    @Override
    public void setPromotionChoices(String[] list) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPromotionChoices'");
    }


}


