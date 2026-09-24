package gamesuite.boardgame.control;

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
    protected final Map<String, CheckersGamePiece.Builder> pieceList = new HashMap<>();
    protected final Map<String, CheckersGamePiece> retPieces = new HashMap<>();

    @Override
    public GameManager createGame(String playerName, String playerId, JsonNode gameDef) {
        
        
        ObjectMapper mapper = new ObjectMapper();
        int sideLength = gameDef.get("board")
                                .get("dimensions") 
                                .get("height").asInt();

        CheckersGameBoard board = new CheckersGameBoard(sideLength);
        JsonNode pieces = gameDef.get("pieces");
        //Map<String, CheckersGamePiece.Builder> pieceList = 
        //setupPieceVectors(pieces);
        String pack = gameDef.get("rulePack").asText();
        Map<String, Constraint> constraints = null;

        
        try {
            constraints = this.rulesLoader.loadConstraints(pack);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Map<String, Effect> effects = null;
        
        try {
            effects = this.rulesLoader.loadEffects(pack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonNode turnControlNode = gameDef.get("turnControl");
        String controlName = turnControlNode.get("type").asText();

        Effect turnControl = effects.get(controlName);

        if(turnControlNode.size() > 1) {
            ObjectNode tmp = turnControlNode.deepCopy();
            tmp.remove("type");
            turnControl.setExtraParams(tmp);
        }


        //setup the piecBuilders with the appropriate vectors, Actions, constraints, and effects
        for(JsonNode piece : pieces) {
            CheckersGamePiece.Builder builder = new CheckersGamePiece.Builder();
            String name = piece.get("name").asText();
            int value = piece.get("value").asInt();
            JsonNode actions = piece.get("actions");
            builder.setType(name).setVal(value);
            List<Action> moveRules = new ArrayList<>();
            List<Action> attackRules = new ArrayList<>();

            for(JsonNode action : actions) {
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

                if(action.has("typeVector")) {
                    JsonNode typeVector = action.get("typeVector");
                    int[][] vectList = new int[typeVector.size()][2];
                    
                    int i = 0;
                    for(JsonNode vect : typeVector) {
                        int x = vect.get("x").asInt();
                        int y = vect.get("y").asInt();
                        int[] temp = { x, y };
                        vectList[i++] = temp;
                    }
                    
                    if(type.equals("move")) {
                        builder.setValidMoves(vectList);
                    } else if(type.equals("attack")) {
                        builder.setValidAttacks(vectList);
                    }
                }

                if(action.has("attackVector")) {
                     JsonNode attackVector = action.get("attackVector");
                    int[][] vectList = new int[attackVector.size()][2];
                    
                    int i = 0;
                    for(JsonNode vect : attackVector) {
                        int x = vect.get("x").asInt();
                        int y = vect.get("y").asInt();
                        int[] temp = { x, y };
                        vectList[i++] = temp;
                    }

                    builder.setAttackVectors(vectList);
                    
                }
                
                Constraint[] constFinal = new Constraint[constNames.size()];
                Effect[] effectsFinal = new Effect[effectNames.size()];
                int i = 0;
                for(String nm : constNames) {
                    constFinal[i] = constraints.get(nm);
                    i++;
                }
                i = 0;
                for(String nm : effectNames) {
                    effectsFinal[i++] = effects.get(nm);
                }

                Action act = new Action(constFinal, effectsFinal);
                act.setName(actName);
                act.setType(type);

                if(type.equals("move")) {
                    moveRules.add(act);
                } else if(type.equals("attack")) {
                    attackRules.add(act);
                }
            }

            builder.setAttackRules(attackRules);
            builder.setMoveRules(moveRules);
            this.pieceList.put(name, builder);
        }
      
        
        JsonNode initVector = gameDef
            .get("board")
            .get("initState");

        JsonNode playerNodes = gameDef.get("players");

       
       
        
        CheckersPlayer player = new CheckersPlayer(playerName, 0);
        player.setUserId(playerId);
        
        ArrayList<String> pieceNames = new ArrayList<>();
        for(JsonNode p : playerNodes) {
            pieceNames.add(p.get("turnName").asText());
        }

        CheckersGameState game = 
            new CheckersGameState.Builder()
                .setPlayer1(player)
                .setNumPlayers(playerNodes.size())
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
                .setJustPromoted(null)
                .build();


        for(Constraint constraint : constraints.values()) {
            constraint.setBoard(board);
            constraint.setGameState(game);
        }

        for(Effect effect : effects.values()) {
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
            CheckersGamePiece.Builder b = pieceList.get(type);
            
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
        RulesValidator validator = new RulesValidator(game, board);
        GameStateManager mang = new GameStateManager(game, board);
        MoveController controller = new MoveController(validator, mang, board);
        
        String moveName = gameDef.get("validMove").asText();
        Constraint moveConst = constraints.get(moveName);
        controller.setMoveConstraint(moveConst);

        String attackName = gameDef.get("validAttack").asText();
        Constraint attackConst = constraints.get(attackName);
        controller.setAttackConstraint(attackConst);

        JsonNode gameRules = gameDef.get("gameRules");
        JsonNode winConditions = gameDef.get("winConditions");
        JsonNode drawConditions = gameDef.get("drawConditions");
        JsonNode postChecks = gameDef.get("postChecks");

        for(JsonNode rule : gameRules) {
            JsonNode constNode = rule.get("constraints");
            for(JsonNode constraint : constNode) {
                String type = constraint.get("type").asText();
                Constraint temp = constraints.get(type);
                controller.addGameConstraint(temp);
            }
        }

        
        
        for(JsonNode action : winConditions) {
            JsonNode constNode = action.get("constraints");
            JsonNode effectsNode = action.get("effects");
            List<Constraint> constList = new ArrayList<>();
            List<Effect> effectsList = new ArrayList<>();

            for(JsonNode constraint : constNode) {
                String type = constraint.get("type").asText();
                constList.add(constraints.get(type));
            }

            for(JsonNode effect : effectsNode) {
                String type = effect.get("type").asText();
                effectsList.add(effects.get(type));
            }
            Constraint[] tmpConst = constList.toArray(new Constraint[0]);
            Effect[] tmpEffect = effectsList.toArray(new Effect[0]);
            Action currAct = new Action(tmpConst, tmpEffect);
            controller.addEndCheck(currAct);
        }

        if(drawConditions != null) {
            for(JsonNode action : drawConditions) {
               JsonNode constNode = action.get("constraints");
               JsonNode effectsNode = action.get("effects");
               List<Constraint> constList = new ArrayList<>();
               List<Effect> effectsList = new ArrayList<>();
   
               for(JsonNode constraint : constNode) {
                   String type = constraint.get("type").asText();
                   constList.add(constraints.get(type));
               }
   
               for(JsonNode effect : effectsNode) {
                   String type = effect.get("type").asText();
                   effectsList.add(effects.get(type));
               }
               Constraint[] tmpConst = constList.toArray(new Constraint[0]);
               Effect[] tmpEffect = effectsList.toArray(new Effect[0]);
               Action currAct = new Action(tmpConst, tmpEffect);
               controller.addEndCheck(currAct);
           }
        }
        
        Map<String, Effect> extras = new HashMap<>();
        for(JsonNode action : postChecks) {
             JsonNode constNode = action.get("constraints");
             JsonNode effectsNode = action.get("effects");
             List<Constraint> constList = new ArrayList<>();
             List<Effect> effectsList = new ArrayList<>();

            for(JsonNode constraint : constNode) {
                String type = constraint.get("type").asText();
                constList.add(constraints.get(type));
            }

            for(JsonNode effect : effectsNode) {
                String t = effect.get("type").asText();
                String type = effect.get("type").asText();
                Effect tmp = effects.get(type);
                effectsList.add(tmp);
                if(effect.size() > 1 && !extras.containsKey(t)) {
                    ObjectNode n = effect.deepCopy();
                    n.remove("type");
                    tmp.setExtraParams(n);
                    extras.put(t, tmp);
                }
            }
            Constraint[] tmpConst = constList.toArray(new Constraint[0]);
            Effect[] tmpEffect = effectsList.toArray(new Effect[0]);
            Action currAct = new Action(tmpConst, tmpEffect);
            controller.addPostCheck(currAct);
        }
        
        BoardGameManager gm = new BoardGameManager(board, validator, game, controller, mang);
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



    protected void setupPieceRules(JsonNode pieces) {
        //Map<String, CheckersGamePiece> retPieces = new HashMap<>();
        for(JsonNode pieceNode : pieces) {
            CheckersGamePiece.Builder pieceBuilder = this.pieceList.get(pieceNode.get("name").asText());
            JsonNode actions = pieceNode.get("actions");

            for(JsonNode action : actions) {
                JsonNode rules = action.get("rules");

                for(JsonNode rule : rules) {
                    JsonNode constraintNodes = rule.get("constraints");
                    ArrayList<Constraint> constraints = new ArrayList<>();

                    for(JsonNode constNode : constraintNodes) {
                       // Constraint constraint = new Constraint();
                    }
                }
            }
            
        }
    }

    protected CheckersGameBoard setupBoard(JsonNode pieces) {

        return null;
    }

    protected void initPieceList() {
        
    }

    /* 
    protected void setupPieceVectors(JsonNode pieces) {
         //Map<String, CheckersGamePiece.Builder> pieceList = new HashMap<>();

        for(JsonNode pieceNode : pieces) {

            CheckersGamePiece.Builder pieceBuilder =
                new CheckersGamePiece.Builder()
                    .setName(null)
                    .setType(pieceNode.get("name").asText())
                    .setVal(pieceNode.get("value").asInt());


            // CheckersGamePiece piece = new CheckersGamePiece(
            //      null,
            //      pieceNode.get("name").asText(),
            //      pieceNode.get("value").asInt()
            // );

             JsonNode actions = pieceNode.get("actions");

            for(JsonNode action : actions) {
                JsonNode typeVector = action.get("typeVector");
                ArrayList<int[]> vects = new ArrayList<>();
                for(JsonNode vect : typeVector) {
                    int[] tmpArr = new int[2];
                    tmpArr[0] = vect.get("x").asInt();
                    tmpArr[1] = vect.get("y").asInt();
                    vects.add(tmpArr);
                }

                pieceBuilder.setValidMoves(vects.toArray(new int[0][]));
                //piece.setValidMoves(vects.toArray(new int[0][]));

                if(action.get("type").asText().equals("attack")) {
                    JsonNode rulesNodes = action.get("rules");

                    for(JsonNode ruleNode : rulesNodes) {
                        
                        JsonNode constraintsNodes = ruleNode.get("constraints");
                        for(JsonNode constraintNode : constraintsNodes) {
                            if(constraintNode.get("type").asText().equals("attackVector")) {
                                
                                //int[] tmpArr = new int[2];
                                JsonNode vects1 = constraintNode.get("vector");
                                ArrayList<int[]> attacksList = new ArrayList<>();

                                for(JsonNode vect : vects1) {
                                    int[] tmpArr1 = new int[2];
                                    tmpArr1[0] = vect.get("x").asInt();
                                    tmpArr1[1] = vect.get("y").asInt();
                                    attacksList.add(tmpArr1);
                                }
                                //piece.setAttackVectors(attacksList.toArray(new int[0][]));
                                pieceBuilder.setAttackVectors(attacksList.toArray(new int[0][]));
                            }
                        }

                    }
                }
            }
            //CheckersGamePiece piece = pieceBuilder.build();
            this.pieceList.put(pieceNode.get("name").asText(), pieceBuilder);
        }
        //return pieceList;
    }
    */
}
