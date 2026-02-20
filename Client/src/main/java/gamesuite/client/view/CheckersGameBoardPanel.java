package gamesuite.client.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gamesuite.client.view.CheckersCoordPair;
import gamesuite.client.view.CheckersGameBoard;
import gamesuite.client.view.CheckersGameState;
import gamesuite.core.model.Player;
import gamesuite.core.ui.GameBoardUI;
import gamesuite.core.ui.UIListener;

public class CheckersGameBoardPanel extends GameBoardUI {

    private CheckersGameState gameView; 
    private CheckersGameBoard board;
    private int size;
    private List<CheckersCoordPair> changed;
    private CheckersCoordPairPanel[][] boardPanel;
    private UIListener listener;
    private Set<CheckersCoordPairPanel> yellowed;
    private int tmpX;
    private int tmpY;
    private int playerTurn;

    public CheckersGameBoardPanel(CheckersGameBoard board, int size, UIListener listener) {
        super();
        this.tmpX = -1;
        this.tmpY = -1;
        this.listener = listener;
        this.boardPanel = new CheckersCoordPairPanel[size][size];
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
                CheckersCoordPairPanel pos = null;
                if ((i + j) % 2 == 0) {
                    pos = new CheckersCoordPairPanel(board.getBoardPos(i, j), this.listener, Color.LIGHT_GRAY, this);
                } else {
                    pos = new CheckersCoordPairPanel(board.getBoardPos(i, j), this.listener, Color.DARK_GRAY, this);
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

    public void updateBoard(List<CheckersCoordPair> changes) {
        
        //ObjectMapper mapper = new ObjectMapper();
        //List<CheckersCoordPair> changes = mapper.convertValue(
        //    changesNode,
        //    new TypeReference<List<CheckersCoordPair>>() {}
       // );
        
        this.changed = changes;
        if(this.changed != null) {
            CheckersCoordPairPanel[] updateList = new CheckersCoordPairPanel[this.changed.size()];
            for(int i = 0; i < this.changed.size(); i++) {
                updateList[i] = this.boardPanel[this.changed.get(i).getX()][this.changed.get(i).getY()];
                updateList[i].setPiece(this.changed.get(i).getPiece());
            }
            this.changed = null;        
        }
        
        for(CheckersCoordPairPanel pos : this.yellowed) {
            pos.resetBackground();
        }
        
        this.yellowed.clear();
    }
    
    @Override
    public void update(JsonNode gameState) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.gameView = mapper.treeToValue(gameState, CheckersGameState.class);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(this.gameView != null) {
            List<CheckersCoordPair> changed = this.gameView.getChangedPos();
            
            updateBoard(changed);
            //this.gameBoard.update();
        }
        this.tmpX = -1;
        this.tmpY = -1;

        /*if(!this.gameView.isGameOver()) {
            if(this.playerTurn == this.gameView.getTurn())
                this.listener.enableBoard();
            this.gui.setTurnLabel(this.gameView.getTurn());
        } else {
            Player winner = this.gameView.getWinner();
            this.gui.setGameOverLabel(winner.getName() + " is the winner");
        }
        return null;
            */  
    }

    public void setGameState(CheckersGameState gameView) {
        //ObjectMapper mapper = new ObjectMapper();
        //CheckersGameState gameView;
        try {
            //gameView = mapper.treeToValue(gameState, CheckersGameState.class);
            this.gameView = gameView;
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public int getTurn() {
        return this.gameView.getTurn();
    }
    
    public void removeYellowed(int x, int y) {
            this.yellowed.remove(this.boardPanel[x][y]);
    }

    public void addYellowedPanel(CheckersCoordPairPanel pos) {
        //ObjectMapper mapper = new ObjectMapper();
        //CheckersCoordPairPanel p;
        try {
            //p = mapper.treeToValue(pos, CheckersCoordPairPanel.class);
            this.yellowed.add(pos);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CheckersCoordPairPanel getBoardPos(int x, int y) {
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
            //SwingUtilities.invokeLater(() -> {
                //this.gui.removeYellowed(x, y);
            //});
            //this.gameBoard.removeYellowed(x, y);
        } else {
            this.listener.disabledBoard();
            //this.gameBoard.setEnabled(false);
            CheckersMove move = new CheckersMove(this.tmpX, this.tmpY, x, y);
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


}


