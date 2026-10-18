package gamesuite.boardgame.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.control.GameManagerFactory;
import gamesuite.core.model.rules.*;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGamePlayer;

public class BoardGameFactory extends GameManagerFactory {

    public static final boolean multiGame = true;
    protected Map<String, BoardGamePiece.Builder> pieceList;
    protected final Map<String, BoardGamePiece> retPieces = new HashMap<>();
    private Map<String, Constraint> constraints;
    private Map<String, Effect> effects;
    private Map<String, Action> actions;

    @Override
    public GameManager createGame(String playerName, String playerId, JsonNode gameDef) throws IOException {
        
        
        ObjectMapper mapper = new ObjectMapper();
        int sideLength = gameDef.get("board")
                                .get("dimensions") 
                                .get("height").asInt();

        GameBoardImpl board = new GameBoardImpl(sideLength);
        JsonNode pieces = gameDef.get("pieces");
        String[] packs = mapper.treeToValue(gameDef.get("rulePacks"), String[].class);
        String ui = gameDef.get("ui").asText();
        String gameName = gameDef.get("game").asText();
        for(String pack : packs) {
            if(this.constraints == null) {
                this.constraints = this.rulesLoader.loadConstraints(pack);
            } else {
                this.constraints.putAll(this.rulesLoader.loadConstraints(pack));
            }

            if(this.effects == null) {
                this.effects = this.rulesLoader.loadEffects(pack);
            } else {
                this.effects.putAll(this.rulesLoader.loadEffects(pack));
            }
        }

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
        BoardGamePlayer player = new BoardGamePlayer(playerName, 0);
        player.setUserId(playerId);
        player.setTurn(1);

        //setting up players
        ArrayList<String> pieceNames = new ArrayList<>();
        for(JsonNode p : playerNodes) {
            pieceNames.add(p.get("turnName").asText());
        }

        //insantiating game state
        BoardGameState game = 
            new BoardGameState.Builder()
                .setPlayer1(player)
                .setNumPlayers(1)
                .setTurn(1)
                .setWinner(null)
                .setIsDraw(false)
                .setP1TrackedAttacks(new HashSet<CoordPairImpl>())
                .setP2TrackedAttacks(new HashSet<CoordPairImpl>())
                .setP1Moves(new HashSet<CoordPairImpl>())
                .setP2Moves(new HashSet<CoordPairImpl>())
                .setFurtherAttacks(null)
                .setBoardSize(sideLength)
                .setTurnFactor(-1)
                .setBoardInit(false)
                .setTeamNames(pieceNames.toArray(new String[0]))
                .setChangedPos(new ArrayList<CoordPairImpl>())
                .setGameOver(false)
                .setJustPromoted(new HashSet<CoordPairImpl>())
                .setGameName(gameName)
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

            if(effect instanceof ConstDependent) {
                ConstDependent dependent = (ConstDependent)effect;
                List<String> dependencies = dependent.getDependencyList();
                Map<String, Constraint> dConst = new HashMap<>();

                for(String nm : dependencies) {
                    dConst.put(nm, this.constraints.get(nm));
                }
                dependent.addDependencies(dConst);
            }
        }
        
         //setting the pieces on the board from the gameDef
        for(JsonNode node : initVector) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            JsonNode pieceNode = node.get("piece");
            int pNum = pieceNode.get("player").asInt();
            String type = pieceNode.get("name").asText();
            BoardGamePiece.Builder b = this.pieceList.get(type);
            
            if(pNum == 1) {
                String team = pieceNames.get(0);
                b.setName(team + type).setTeam(team);
            } else if(pNum == 2) {
                String team = pieceNames.get(1);
                b.setTeam(team).setName(team + type);
            }

            BoardGamePiece piece = b.build();
            board.setBoardPos(x, y, piece);
        }

        MoveController controller = new MoveController(board);
        String moveName = gameDef.get("validMove").asText();
        Constraint moveConst = this.constraints.get(moveName);

//        if(moveConst instanceof Updater) {
//            JsonNode vmUp = gameDef.get("vmUpdatees");
//            Updater mc = (Updater)moveConst;
//            Map<String, Updatable> map = new HashMap<>();
//            for(JsonNode node : vmUp ) {
//                String name = node.asText();
//                Updatable up = (Updatable)this.constraints.get(name);
//                map.put(name, up);
//            }
//            mc.addUpdatables(map);
//        }
        controller.setMoveConstraint(moveConst);

        String attackName = gameDef.get("validAttack").asText();
        Constraint attackConst = this.constraints.get(attackName);
        controller.setAttackConstraint(attackConst);

        JsonNode gameRules = gameDef.get("gameRules");
        JsonNode winConditions = gameDef.get("winConditions");
        JsonNode drawConditions = gameDef.get("drawConditions");
        JsonNode postChecks = gameDef.get("postChecks");


        Map<String, Effect> extras = new HashMap<>();
        Map<String, Constraint> extras2 = new HashMap<>();

        for(JsonNode constraint : gameRules) {
            String type = constraint.get("type").asText();
            Constraint temp = this.constraints.get(type);
            controller.addGameConstraint(temp);
            if(constraint.size() > 1 && !extras.containsKey(type)) {
                ObjectNode n = constraint.deepCopy();
                n.remove("type");
                Constraint tmp = this.constraints.get(type);
                tmp.setExtraParams(n);
                extras2.put(type, tmp);
            }
        }
       

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
    
        for(JsonNode action : postChecks) {
            String name = action.get("name").asText();
            JsonNode ref = d.get(name);
            JsonNode effectsNode = ref.get("effects");
            Action act = this.actions.get(name);
            controller.addPostCheck(act);
        }


        //setting the extra params variable in all constraints and effects

        for(JsonNode action : actionsNode) {
            String name = action.get("name").asText();
            JsonNode ref = d.get(name);
            JsonNode effectsNode = ref.get("effects");
            JsonNode constraintsNode = ref.get("constraints");
            Action act = this.actions.get(name);
            for(JsonNode effect : effectsNode) {
                String t = effect.get("type").asText();

                if(effect.has("updatees")) {

                    JsonNode updatees = effect.get("updatees");

                    Updater f = (Updater)this.effects.get(effect.get("type").asText());
                    Map<String, Updatable> ups = new HashMap<>();
                    for(JsonNode u : updatees) {
                        if(this.constraints.get(u.asText()) != null) {
                            ups.put(u.asText(), (Updatable)this.constraints.get(u.asText()));
                        } else if(this.effects.get(u.asText()) != null) {
                            ups.put(u.asText(), (Updatable)this.effects.get(u.asText()));
                        
                        }    
                    }
                    f.addUpdatables(ups);
                }


                if(effect.size() > 1 && !extras.containsKey(t)) {
                    ObjectNode n = effect.deepCopy();
                    n.remove("type");
                    Effect tmp = this.effects.get(t);
                    tmp.setExtraParams(n);
                    extras.put(t, tmp);
                }
            }
            for(JsonNode constraint : constraintsNode) {
                String t = constraint.get("type").asText();

                if(constraint.has("updatees")) {

                    JsonNode updatees = constraint.get("updatees");

                    Updater f = (Updater)this.constraints.get(constraint.get("type").asText());
                    Map<String, Updatable> ups = new HashMap<>();
                    for(JsonNode u : updatees) {
                        if(this.constraints.get(u.asText()) != null) {
                            ups.put(u.asText(), (Updatable)this.constraints.get(u.asText()));
                        } else if(this.effects.get(u.asText()) != null) {
                            ups.put(u.asText(), (Updatable)this.effects.get(u.asText()));
                        
                        }    
                    }
                    f.addUpdatables(ups);
                }

                if(constraint.size() > 1 && !extras2.containsKey(t)) {
                    ObjectNode n = constraint.deepCopy();
                    n.remove("type");
                    Constraint tmp = this.constraints.get(t);
                    tmp.setExtraParams(n);
                    extras2.put(t, tmp);
                }
            }
        }
        
        BoardGameManager gm = new BoardGameManager(board, game, controller);
        gm.mapSessionPlayer(playerId);
        gm.setPieceList(this.pieceList);

        for(Effect e : extras.values()) {
            e.setGameManager(gm);
        }

        for(Constraint c : extras2.values()) {
            c.setGameManager(gm);
        }

        gm.setTurnControl(turnControl);
        gm.setUI(ui);

        if(gameDef.has("gmUpdatee")) {
            String name = gameDef.get("gmUpdatee").asText();
            Map<String, Updatable> m = new HashMap<>();
            Constraint c = this.constraints.get(name);
            c.setGameManager(gm);
            m.put(name, (Updatable)this.constraints.get(name));
            gm.addUpdatables(m);
        }

       
        return gm;
    }

    @Override
    public boolean isMultiGame() {
        return multiGame;
    }

    protected Map<String, BoardGamePiece.Builder> buildPieceBuilders(JsonNode pieces) {
        Map<String, BoardGamePiece.Builder> pieceList = new HashMap<>();
        for(JsonNode piece : pieces) {
            BoardGamePiece.Builder builder = new BoardGamePiece.Builder();
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
            builder.setValidAttacks(vectList);
            
            if(piece.has("moveVector")) {
                  JsonNode moveVector = piece.get("moveVector");
                int[][] vectList2 = new int[moveVector.size()][];
                
                int j = 0;
                for(JsonNode vect : moveVector) {
                    int x = vect.get("x").asInt();
                    int y = vect.get("y").asInt();
                    int[] temp = { x, y };
                    vectList2[j++] = temp;
                }

                builder.setValidMoves(vectList2);
            } else {
                int[][] arr = new int[0][];
                builder.setValidMoves(arr);
            }

            if(piece.has("attackVector")) {
                 JsonNode attackVector = piece.get("attackVector");
                int[][] vectList2 = new int[attackVector.size()][];
                
                int j = 0;
                for(JsonNode vect : attackVector) {
                    int x = vect.get("x").asInt();
                    int y = vect.get("y").asInt();
                    int[] temp = { x, y };
                    vectList2[j++] = temp;
                }

                builder.setAttackVectors(vectList2);
                
            }else {
                int[][] arr = new int[0][];
                builder.setAttackVectors(arr);
            }

            if(piece.has("maxDist")) {
                int maxDist = piece.get("maxDist").asInt();
                builder.setMaxDist(maxDist);
            }

            if(piece.has("vectType")) {
                String vectType = piece.get("vectType").asText();
                builder.setVectType(vectType);
            }

            builder.setAttackRules(attackRules);
            builder.setMoveRules(moveRules);
            pieceList.put(name, builder);
        }
        return pieceList;
    }

    protected Map<String, Action> buildActions(JsonNode actionsNode) throws IOException {
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
                if(constFinal[i] == null) {
                    throw new IOException("this constraint was not in the manifest");
                }
                i++;
            }
            i = 0;
            for(String nm : effectNames) {
                effectsFinal[i] = this.effects.get(nm);
                if(effectsFinal[i] == null) {
                    throw new IOException("this effect was not in the manifest");
                }
                i++;
            }

            Action act = new Action(constFinal, effectsFinal);
            act.setName(actName);
            act.setType(type);
            actions.put(act.getName(), act);
        }
        return actions;
    }

    protected GameBoardImpl setupBoard(JsonNode pieces) {

        return null;
    }
}
