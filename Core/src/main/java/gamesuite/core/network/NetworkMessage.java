package gamesuite.core.network;

import com.fasterxml.jackson.databind.JsonNode;

public class NetworkMessage {
    private String messageType;
    private JsonNode payload;

    public NetworkMessage() {}

    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getMessageType() { return this.messageType; }

    public void setPayload(JsonNode payload) { this.payload = payload; }

    public JsonNode getPayload() { return this.payload; }
}
