package gamesuite.core.model;

import com.fasterxml.jackson.databind.JsonNode;

public interface Move {

    public int getStartX();

    public int getStartY();

    public int getEndX();

    public int getEndY();

    public void setJsonNode(JsonNode node);
    public JsonNode getJsonNode();


}
