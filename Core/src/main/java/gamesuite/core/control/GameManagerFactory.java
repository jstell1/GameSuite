package gamesuite.core.control;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import gamesuite.core.utils.PluginLoader;

public abstract class GameManagerFactory {
    protected PluginLoader rulesLoader;
    public abstract GameManager createGame(String playerName, String playerId, JsonNode gameDef) throws IOException;
    public abstract boolean isMultiGame();
    public void setRulesLoader(PluginLoader loader) {
        if(this.rulesLoader != null)
            throw new IllegalStateException("cannot reset rulesLoader once set");
        this.rulesLoader = loader;
    }
}
