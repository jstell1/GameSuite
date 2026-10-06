package gamesuite.boardgame.control;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.model.rules.*;
import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersPlayer;

public class BoardGameFactory extends GameManagerFactory {

    public static final boolean multiGame = true;
    protected Map<String, CheckersGamePiece.Builder> pieceList;
    protected final Map<String, CheckersGamePiece> retPieces = new HashMap<>();
    private Map<String, Constraint> constraints;
    private Map<String, Effect> effects;
    private Map<String, Action> actions;

    @Override
    public GameManager createGame(String playerName, String playerId, JsonNode gameDef) throws MalformedURLException {
        
        
        ObjectMapper mapper = new ObjectMapper();
        int sideLength = gameDef.get("board")
                                .get("dimensions") 
                                .get("height").asInt();

        CheckersGameBoard board = new CheckersGameBoard(sideLength);
        JsonNode pieces = gameDef.get("pieces");
        //Map<String, CheckersGamePiece.Builder> pieceList = 
        //setupPieceVectors(pieces);
        String pack = gameDef.get("rulePack").asText();
        //Map<String, Constraint> constraints = null;

        
        //loading constraints
        //try {
            this.constraints = this.rulesLoader.loadConstraints(pack);
        //} catch (Exception e) {
            // TODO Auto-generated catch block
        //    e.printStackTrace();
        //}
        
       // try {
            this.effects = this.rulesLoader.loadEffects(pack);
       // } catch (Exception e) {
        //    e.printStackTrace();
        //}

        //building actions map
        JsonNode actionsNode = gameDef.get("actions");
        this.actions = buildActions(actionsNode);
        Map<String, JsonNode> d = new HashMap<>();
        for(JsonNode n : actionsNode) {
            String nam = n.get("name").asText();
            d.put(nam, n);
        }

        JsonNode turnControlNode = gameDef.get("turnControl");
        String controlName = turnControlNode.get("type").asText();
        Effect turnControl = this.effects.get(controlName);
        if(turnControlNode.size() > 1) {
            ObjectNode tmp = turnControlNode.deepCopy();
            tmp.remove("type");
            turnControl.setExtraParams(tmp);
        }

        //setup the piecBuilders with the appropriate vectors, Actions, constraints, and effects
        this.pieceList = buildPieceBuilders(pieces);
      
        JsonNode initVector = gameDef
            .get("board")
            .get("initState");

        JsonNode playerNodes = gameDef.get("players");
        CheckersPlayer player = new CheckersPlayer(playerName, 0);
        player.setUserId(playerId);
        
        //setting up players
        ArrayList<String> pieceNames = new ArrayList<>();
        for(JsonNode p : playerNodes) {
            pieceNames.add(p.get("turnName").asText());
        }

        //insantiating game state
        CheckersGameState game = 
            new CheckersGameState.Builder()
                .setPlayer1(player)
                .setNumPlayers(1)
                .setTurn(1)
                .setWinner(null)
                .setIsDraw(false)
                .setP1Attacks(new HashSet<CheckersCoordPair>())
                .setP2Attacks(new HashSet<CheckersCoordPair>())
                .setP1Moves(new HashSet<CheckersCoordPair>())
                .setP2Moves(new HashSet<CheckersCoordPair>())
                .setFurtherAttacks(null)
                .setBoardSize(sideLength)
                .setTurnFactor(-1)
                .setBoardInit(false)
                .setTeamNames(pieceNames.toArray(new String[0]))
                .setChangedPos(new ArrayList<CheckersCoordPair>())
                .setGameOver(false)
                .setJustPromoted(new HashSet<CheckersCoordPair>())
                .build();

        //adding game board and game state to constraints and effects
        for(Constraint constraint : this.constraints.values()) {
            constraint.setBoard(board);
            constraint.setGameState(game);

            if(constraint instanceof ConstDependent) {
                ConstDependent dependent = (ConstDependent)constraint;
                List<String> dependencies = dependent.getDependencyList();
                Map<String, Constraint> dConst = new HashMap<>();

                for(String nm : dependencies) {
                    dConst.put(nm, this.constraints.get(nm));
                }
                dependent.addDependencies(dConst);
            }
        }

        for(Effect effect : this.effects.values()) {
            effect.setBoard(board);
            effect.setGameState(game);
        }
        
         //setting the pieces on the board from the gameDef
        for(JsonNode node : initVector) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            JsonNode pieceNode = node.get("piece");
            int pNum = pieceNode.get("player").asInt();
            String type = pieceNode.get("name").asText();
            CheckersGamePiece.Builder b = this.pieceList.get(type);
            
            if(pNum == 1) {
                String team = pieceNames.get(0);
                b.setName(team + type).setTeam(team);
            } else if(pNum == 2) {
                String team = pieceNames.get(1);
                b.setTeam(team).setName(team + type);
            }

            CheckersGamePiece piece = b.build();
            //piece.setName(playerNodes.get(pNum - 1).get("turnName").asText());
            board.setBoardPos(x, y, piece);
        }

        MoveController controller = new MoveController(board);
        String moveName = gameDef.get("validMove").asText();
        Constraint moveConst = this.constraints.get(moveName);
        controller.setMoveConstraint(moveConst);

        String attackName = gameDef.get("validAttack").asText();
        Constraint attackConst = this.constraints.get(attackName);
        controller.setAttackConstraint(attackConst);

        JsonNode gameRules = gameDef.get("gameRules");
        JsonNode winConditions = gameDef.get("winConditions");
        JsonNode drawConditions = gameDef.get("drawConditions");
        JsonNode postChecks = gameDef.get("postChecks");

        //for(JsonNode rule : gameRules) {
            //JsonNode constNode = rule.get("constraints");
            for(JsonNode constraint : gameRules) {
                String type = constraint.get("type").asText();
                Constraint temp = this.constraints.get(type);
                controller.addGameConstraint(temp);
            }
       // }

        for(JsonNode action : winConditions) {
            Action act = this.actions.get(action.get("name").asText());
            controller.addEndCheck(act);
        }

        if(drawConditions != null) {
            for(JsonNode action : drawConditions) {
                Action currAct = this.actions.get(action.get("name").asText());
               controller.addEndCheck(currAct);
           }
        }
        
        Map<String, Effect> extras = new HashMap<>();
        for(JsonNode action : postChecks) {
            String name = action.get("name").asText();
            JsonNode ref = d.get(name);
            JsonNode effectsNode = ref.get("effects");
            Action act = this.actions.get(name);
            for(JsonNode effect : effectsNode) {
                String t = effect.get("type").asText();

                if(effect.size() > 1 && !extras.containsKey(t)) {
                    ObjectNode n = effect.deepCopy();
                    n.remove("type");
                    Effect tmp = this.effects.get(t);
                    tmp.setExtraParams(n);
                    extras.put(t, tmp);
                }
            }
            controller.addPostCheck(act);
        }
        
        BoardGameManager gm = new BoardGameManager(board, game, controller);//new BoardGameManager(board, validator, game, controller, mang);
        gm.mapSessionPlayer(playerId);
        gm.setPieceList(this.pieceList);

        for(Effect e : extras.values()) {
            e.setGameManager(gm);
        }

        gm.setTurnControl(turnControl);
        return gm;
    }

    @Override
    public boolean isMultiGame() {
        return multiGame;
    }

    protected Map<String, CheckersGamePiece.Builder> buildPieceBuilders(JsonNode pieces) {
        Map<String, CheckersGamePiece.Builder> pieceList = new HashMap<>();
        for(JsonNode piece : pieces) {
            CheckersGamePiece.Builder builder = new CheckersGamePiece.Builder();
            String name = piece.get("name").asText();
            int value = piece.get("value").asInt();
            JsonNode pieceActions = piece.get("actions");
            builder.setType(name).setVal(value);
            List<Action> moveRules = new ArrayList<>();
            List<Action> attackRules = new ArrayList<>();

            for(JsonNode action : pieceActions) {
                String actName = action.get("name").asText();
                Action act = this.actions.get(actName);
                if(act.getType().equals("move")){
                    moveRules.add(act);
                } else {
                    attackRules.add(act);
                }

            }

            JsonNode targVector = piece.get("targVector");
            int[][] vectList = new int[targVector.size()][2];
            
            int i = 0;
            for(JsonNode vect : targVector) {
                int x = vect.get("x").asInt();
                int y = vect.get("y").asInt();
                int[] temp = { x, y };
                vectList[i++] = temp;
            }
            builder.setValidMoves(vectList);
            
            if(piece.has("moveVector")) {
                  JsonNode moveVector = piece.get("attackVector");
                int[][] vectList2 = new int[moveVector.size()][2];
                
                int j = 0;
                for(JsonNode vect : moveVector) {
                    int x = vect.get("x").asInt();
                    int y = vect.get("y").asInt();
                    int[] temp = { x, y };
                    vectList2[j++] = temp;
                }

                builder.setAttackVectors(vectList);
            }            

            if(piece.has("attackVector")) {
                 JsonNode attackVector = piece.get("attackVector");
                int[][] vectList2 = new int[attackVector.size()][2];
                
                int j = 0;
                for(JsonNode vect : attackVector) {
                    int x = vect.get("x").asInt();
                    int y = vect.get("y").asInt();
                    int[] temp = { x, y };
                    vectList2[j++] = temp;
                }

                builder.setAttackVectors(vectList);
                
            }

            builder.setAttackRules(attackRules);
            builder.setMoveRules(moveRules);
            pieceList.put(name, builder);
        }
        return pieceList;
    }

    protected Map<String, Action> buildActions(JsonNode actionsNode) {
        Map<String, Action> actions = new HashMap<>();
         for(JsonNode action : actionsNode) {
            String actName = action.get("name").asText();
            String type = action.get("type").asText();
            ArrayList<String> constNames = new ArrayList<>();
            ArrayList<String> effectNames = new ArrayList<>();
            JsonNode constNode = action.get("constraints");
            JsonNode effectsNode = action.get("effects");

            for(JsonNode node : constNode) {
                constNames.add(node.get("type").asText());
            }

            for(JsonNode node : effectsNode) {
                effectNames.add(node.get("type").asText());
            }

            Constraint[] constFinal = new Constraint[constNames.size()];
            Effect[] effectsFinal = new Effect[effectNames.size()];
            int i = 0;
            for(String nm : constNames) {
                constFinal[i] = this.constraints.get(nm);
                i++;
            }
            i = 0;
            for(String nm : effectNames) {
                effectsFinal[i++] = this.effects.get(nm);
            }

            Action act = new Action(constFinal, effectsFinal);
            act.setName(actName);
            act.setType(type);
            actions.put(act.getName(), act);
        }
        return actions;
    }

    protected CheckersGameBoard setupBoard(JsonNode pieces) {

        return null;
    }
}
