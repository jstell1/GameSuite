package gamesuite.core.model;


public interface GameState {

    public String[] getTeamNames();


    public boolean isGameOver();

    public boolean isBoardInit();

    public int getTurnFactor();


    public int getTurn();

    public Player getPlayer(int gameTurn);
    

    public Player[] getPlayers();
    
    public CoordPair getFurtherJumps();

    public boolean getDraw();
    public Player getWinner();
    public int getPlayerPoints(int playerNum);


    public int getNumPlayers();

}
