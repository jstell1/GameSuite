package gamesuite.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.*;

import java.util.List;

public class MockGameManager implements GameManager {
    @Override
    public JsonNode joinGame(String player, String playerId) {
        return null;
    }

    @Override
    public void sendMove(Move move) {

    }

    @Override
    public boolean sendMove(ObjectNode move, String playerId) throws Exception {
        return false;
    }

    @Override
    public boolean addPlayer(String player) {
        return false;
    }

    @Override
    public boolean isGameReady() {
        return false;
    }

    @Override
    public String getBoardString() {
        return "";
    }

    @Override
    public Player getWinner() {
        return null;
    }

    @Override
    public GameBoard getBoard() {
        return null;
    }

    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public boolean initBoard() {
        return false;
    }

    @Override
    public int getTurn() {
        return 0;
    }

    @Override
    public GameState getGameState() {
        return null;
    }

    @Override
    public JsonNode getGameStateJson() {
        return null;
    }

    @Override
    public GameState quitGame(String playerId) {
        return null;
    }

    @Override
    public int getNumPlayers() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean checkPlayerSession(String playerId) {
        return false;
    }

    @Override
    public List<String> getUserIdList() {
        return List.of();
    }

    @Override
    public int getNumMappedPlayers() {
        return 0;
    }

    @Override
    public String getUI() {
        return "";
    }

    @Override
    public void sendPromotionChoice(String type) throws Exception {

    }

    @Override
    public void sendPromotionRequest(JsonNode move, JsonNode choices) {

    }

    @Override
    public void setPromoteRequester(PromoteRequester requester) {

    }

    @Override
    public void setGameId(String gameId) {

    }

    @Override
    public boolean getOverride() {
        return false;
    }
}
