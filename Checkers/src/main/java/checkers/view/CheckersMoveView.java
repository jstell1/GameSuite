package checkers.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import checkers.model.CheckersMove;
import gamesuite.core.model.Move;

public class CheckersMoveView implements Move {

    public CheckersMove move;

    public CheckersMoveView(CheckersMove move) { this.move = move; }
    
    public int getStartX() { return this.move.getStartX(); }

    public int getStartY() { return this.move.getStartY(); }

    public int getEndX() { return this.move.getEndX(); }

    public int getEndY() { return this.move.getEndY(); }

    @Override
    public void setObjectNode(ObjectNode node) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.move = mapper.treeToValue(node, CheckersMove.class);
            
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public ObjectNode getObjectNode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getObjectNode'");
    }
}
