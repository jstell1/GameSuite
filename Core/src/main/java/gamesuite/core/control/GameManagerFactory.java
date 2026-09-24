package gamesuite.core.control;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class GameManagerFactory {
    protected PluginLoader rulesLoader;
    public abstract GameManager createGame(String playerName, String playerId, JsonNode gameDef);
    public abstract boolean isMultiGame();
    public void setRulesLoader(PluginLoader loader) {
        if(this.rulesLoader == null)
            this.rulesLoader = loader;
    }
}
