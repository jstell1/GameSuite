package checkers.control;

import java.util.List;
import java.util.Set;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;

public class GameStateManager {

    private CheckersGameState game;
    private CheckersGameBoard board;

    public GameStateManager(CheckersGameState game, CheckersGameBoard board) {
        this.game = game;
        this.board = board;
    }

    public void incrTurn(boolean p1HasMoves, boolean p2HasMoves) {
        CheckersPlayer[] players = this.game.getPlayers();
        int p1Points = players[0].getPoints();
        int p2Points = players[1].getPoints();
             if(p2HasMoves && !p1HasMoves) {
            this.game.setWinnerNum(2);
            this.game.setGameOver(true);
        } else if(p1HasMoves && !p2HasMoves) {
            this.game.setWinnerNum(1);
            this.game.setGameOver(true);
        } else if(!p1HasMoves && !p2HasMoves) {
            if(p1Points > p2Points) 
                this.game.setWinnerNum(1);
            else if(p2Points > p1Points)
                this.game.setWinnerNum(2);
            else
                this.game.setDraw();
            this.game.setGameOver(true);
        } else {
            this.game.setTurn(this.game.getTurn() % 2 + 1);
            this.game.flipTurnFactor();
        }
        
    }

     public void incrPoints() {
        if(this.game.getTurn() == 1) {
            this.game.addPlayerPoints(1);
        }
        else if(this.game.getTurn() == 2)
            this.game.addPlayerPoints(2);
    }

    public void updateBoard(CheckersMove move) {
        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY(); 
        CheckersCoordPair start = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        end.setPiece(start.getPiece());
        start.setPiece(null);
    }
    
    public void setFurtherJumps(CheckersCoordPair pos) { this.game.setFurtherJumps(pos); }

    public void removeJumped(CheckersCoordPair pos) { pos.setPiece(null); }

    public void kingPiece(CheckersCoordPair pos) {
        if(pos == null || pos.getPiece() == null)
            return;

        CheckersGamePiece piece = pos.getPiece();
        String[] names = this.game.getPieceNames();
        boolean check = piece.getName().equals(names[0]) && pos.getX() == this.board.getSideLength() - 1;
        check = check || piece.getName().equals(names[1]) && pos.getX() == 0; 
        if(check) 
            piece.kingPiece();
    }
    
    public void removeFromJumps(CheckersCoordPair pos, boolean hasFurtherJumps) {
        if(pos == null)
            return;

        CheckersGamePiece piece = pos.getPiece();

        if(piece == null || !hasFurtherJumps) {
            this.game.removePlayerJumps(pos, 1);
            this.game.removePlayerJumps(pos, 2);
        } 
    } 

    public boolean initBoard() {
        if(this.game.isBoardInit())
            return false;

        initPlayerSide("B", "C", 1, 0, 2);
        initPlayerSide("R", "C", 1, 5, 7);
        this.game.setBoardInit();
        return true;
    }

     private void initPlayerSide(String name, String type, int val, int startRow, int endRow) {
        for(int i = startRow; i <= endRow; i++) {
            if(i % 2 == 0) {
                initRow(name, type, val, i, 1);
            } else {
                initRow(name, type, val, i, 0);
            }
        }
    }

    private void initRow(String name, String type, int val, int row, int startPos) {
        int size = this.board.getSideLength();
        for(int j = startPos; j < size; j += 2) {
            CheckersGamePiece piece = new CheckersGamePiece(name, type, val);
            this.board.setBoardPos(row, j, piece);
        }
    }

    public CheckersCoordPair getBoardPos(int x, int y) { return this.board.getBoardPos(x, y); }

    public String getBoardString() {
        return this.board.toString();
    }

    public CheckersCoordPair getFurtherJumps() { return this.game.getFurtherJumps(); }

    public CheckersPlayer getWinner() { return this.game.getWinner(); }

    public boolean getDraw() { return this.game.getDraw(); }

    public CheckersGameBoard getBoardCopy() { return this.board.copy(); }

    public int getTurn() { return this.game.getTurn(); }

    public void addPlayerJumps(CheckersCoordPair currPos, int playerNum) {
        this.game.addPlayerJumps(currPos, playerNum);
    }
    
    public Set<CheckersCoordPair> getJumps(int playerNum) {
        return this.game.getJumps(playerNum);
    }

    public void setChanged(List<CheckersCoordPair> changed) {
        this.game.setChangedPos(changed);
    }

    public void addJustKinged(CheckersCoordPair pos) {
        this.game.addJustKinged(pos);
    }

    public void clearJustKinged() {
        this.game.clearJustKinged();
    }

    public boolean isJustKinged(CheckersCoordPair pos) {
        return this.game.isJustKinged(pos);
    }

    public boolean addPlayer(CheckersPlayer player) {
        return this.game.addPlayer(player);
    }

    public void setWinner(int playerNum) {
        if(playerNum > 0 && playerNum <= 2) {
            this.game.setWinnerNum(playerNum);
            this.game.setGameOver(true);
        }
    }
}
