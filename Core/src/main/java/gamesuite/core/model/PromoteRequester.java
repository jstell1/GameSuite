package gamesuite.core.model;

import com.fasterxml.jackson.databind.JsonNode;

public interface PromoteRequester {
    public void requestPromoteChoice(JsonNode choices);
}
