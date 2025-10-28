package gamesuite.core.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Move {

    public int getStartX();

    public int getStartY();

    public int getEndX();

    public int getEndY();

    public void setObjectNode(ObjectNode node);
    public ObjectNode getObjectNode();


}
