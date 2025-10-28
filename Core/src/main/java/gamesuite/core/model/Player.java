package gamesuite.core.model;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Player {

    public int getPoints();

    public String getUserId();

    public String getName();

    public Player copy();
    //public ObjectNode getObjectNode();
}