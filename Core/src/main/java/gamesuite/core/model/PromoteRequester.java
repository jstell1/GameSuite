package gamesuite.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface PromoteRequester {
    public void requestPromoteChoice(JsonNode choices);
    public void sendUpdate(JsonNode update) throws IOException;
}
