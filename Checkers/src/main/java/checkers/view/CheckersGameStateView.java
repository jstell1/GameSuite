package checkers.view;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import checkers.model.CheckersCoordPair;
import checkers.view.CheckersCoordPairView;
import checkers.model.CheckersGameState;
import checkers.model.CheckersPlayer;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CheckersGameStateView implements GameState {

    CheckersGameState game;

    public CheckersGameStateView(CheckersGameState game) { this.game = game; }

    public String[] getPieceNames() { return this.game.getPieceNames(); }

    public boolean isJustKinged(CoordPair pos) { return pos.getPiece().isKing(); }

    public boolean isGameOver() { return this.game.isGameOver(); }

    public boolean isBoardInit() { return this.game.isBoardInit(); }

    public int getTurnFactor() { return this.game.getTurnFactor(); }

    public Set<CoordPair> getJumps(int playerNum) { 
        Set<CheckersCoordPair> set = this.game.getJumps(playerNum);
        Set<CoordPair> ret = new HashSet<>();
        for(CheckersCoordPair elm : set) {
            ret.add(new CheckersCoordPairView(elm));
        } 
        return ret;
    }

    public int getTurn() { return this.game.getTurn(); }

    public Player getPlayer(int playerNum) { 

        CheckersPlayer p = this.game.getPlayer(playerNum);
        if(p == null)
            return null;
        else 
            return new CheckersPlayerView(p); 
    }
    

    public Player[] getPlayers() { 
        CheckersPlayer[] players = this.game.getPlayers();
        CheckersPlayerView[] ret = new CheckersPlayerView[players.length];
        for(int i = 0; i < players.length; i++) {
            ret[i] = new CheckersPlayerView(players[i]);
        }
        return ret;
     }
    
    public CoordPair getFurtherJumps() { return new CheckersCoordPairView(this.game.getFurtherJumps()); }

    public boolean getDraw() { return this.game.getDraw(); }
    public Player getWinner() { 

        if(this.game.getWinner() == null)
            return null;
        else
            return new CheckersPlayerView(this.game.getWinner()); 
    }
    public int getPlayerPoints(int playerNum) { return this.game.getPlayerPoints(playerNum); }


    public int getNumPlayers() { return this.game.getNumPlayers(); }

    public List<CoordPair> getChangedPos() {

        List<CheckersCoordPair> lst = this.game.getChangedPos();
        List<CoordPair> ret = new ArrayList();
        for(CheckersCoordPair pos : lst) {
            ret.add(new CheckersCoordPairView(pos));
        }
        return ret;
    }

    @Override
    public JsonNode getJsonNode() {
         ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree(game);
    }
}
