package gamesuite.core.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public interface GameState {

    public String[] getPieceNames();

    //public boolean isJustKinged(CoordPair pos);

    public boolean isGameOver();

    public boolean isBoardInit();

    public int getTurnFactor();

    //public Set<CoordPair> getJumps(int playerNum);

    public int getTurn();

    public Player getPlayer(int playerNum);
    

    public Player[] getPlayers();
    
    public CoordPair getFurtherJumps();

    public boolean getDraw();
    public Player getWinner();
    public int getPlayerPoints(int playerNum);


    public int getNumPlayers();

    //public List<CoordPair> getChangedPos();
    //public JsonNode getJsonNode();
}
