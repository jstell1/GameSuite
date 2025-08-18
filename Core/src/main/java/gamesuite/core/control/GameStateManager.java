package gamesuite.core.control;

import java.util.List;
import java.util.Set;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public class GameStateManager {

    private GameState game;
    private GameBoard board;

    public GameStateManager(GameState game, GameBoard board) {
        this.game = game;
        this.board = board;
    }

    public void incrTurn(boolean p1HasMoves, boolean p2HasMoves) {
        Player[] players = this.game.getPlayers();
        int p1Points = players[0].getPoints();
        int p2Points = players[1].getPoints();
             if(p2HasMoves && !p1HasMoves) {
            this.game.setWinner(2);
            this.game.setGameOver(true);
        } else if(p1HasMoves && !p2HasMoves) {
            this.game.setWinner(1);
            this.game.setGameOver(true);
        } else if(!p1HasMoves && !p2HasMoves) {
            if(p1Points > p2Points) 
                this.game.setWinner(1);
            else if(p2Points > p1Points)
                this.game.setWinner(2);
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

    public void updateBoard(Move move) {
        int sX = move.getStartX();
        int sY = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY(); 
        CoordPair start = this.board.getBoardPos(sX, sY);
        CoordPair end = this.board.getBoardPos(eX, eY);
        end.setPiece(start.getPiece());
        start.setPiece(null);
    }
    
    public void setFurtherJumps(CoordPair pos) { this.game.setFurtherJumps(pos); }

    public void removeJumped(CoordPair pos) { pos.setPiece(null); }

    public void kingPiece(CoordPair pos) {
        if(pos == null || pos.getPiece() == null)
            return;

        GamePiece piece = pos.getPiece();
        String[] names = this.game.getPieceNames();
        boolean check = piece.getName() == names[0] && pos.getX() == this.board.getSideLength() - 1;
        check = check || piece.getName() == names[1] && pos.getX() == 0; 
        if(check) 
            piece.kingPiece();
    }
    
    public void removeFromJumps(CoordPair pos, boolean hasFurtherJumps) {
        if(pos == null)
            return;

        GamePiece piece = pos.getPiece();

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
            GamePiece piece = new GamePiece(name, type, val);
            this.board.setBoardPos(row, j, piece);
        }
    }

    public CoordPair getBoardPos(int x, int y) { return this.board.getBoardPos(x, y); }

    public String getBoardString() {
        return this.board.toString();
    }

    public CoordPair getFurtherJumps() { return this.game.getFurtherJumps(); }

    public Player getWinner() { return this.game.getWinner(); }

    public boolean getDraw() { return this.game.getDraw(); }

    public GameBoard getBoardCopy() { return this.board.copy(); }

    public int getTurn() { return this.game.getTurn(); }

    public void addPlayerJumps(CoordPair currPos, int playerNum) {
        this.game.addPlayerJumps(currPos, playerNum);
    }
    
    public Set<CoordPair> getJumps(int playerNum) {
        return this.game.getJumps(playerNum);
    }

    public void setChanged(List<CoordPair> changed) {
        this.game.setChangedPos(changed);
    }

    public void addJustKinged(CoordPair pos) {
        this.game.addJustKinged(pos);
    }

    public void clearJustKinged() {
        this.game.clearJustKinged();
    }

    public boolean isJustKinged(CoordPair pos) {
        return this.game.isJustKinged(pos);
    }

    public boolean addPlayer(Player player) {
        return this.game.addPlayer(player);
    }
}
