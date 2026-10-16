package gamesuite.chess;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.boardgame.model.BoardGameMove;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Action;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;

public class IsCheckmateConst extends Constraint implements ConstDependent{

    private GameBoardImpl board;
    private BoardGameState gameState;
    private final String[] dependencies = {"kingProtected", "validChessMove", "validChessAttack"};
    private KingProtectedConst kingProtected;
    private ValidChessMoveConst validMove;
    private ValidChessAttackConst validAttack;

    public IsCheckmateConst() {
        super("isCheckmate");
    }

    public IsCheckmateConst(GameState gameState, GameBoard board) {

        super(gameState, board);
        //TODO Auto-generated constructor stub
        this.name = "isCheckmate";
    }

    @Override
    public void setGameState(JsonNode game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setBoard(JsonNode board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setGameState(GameState game) {
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public boolean checkMove(JsonNode move) {

        int turn = this.gameState.getTurn();

        CoordPairImpl kingPos = this.kingProtected.getKingPos(turn % 2 + 1);
        BoardGamePiece king = kingPos.getPiece();
        int sX = kingPos.getX();
        int sY = kingPos.getY();
        String team = king.getTeam();
        String[] teams = this.gameState.getTeamNames();

        if(!this.kingProtected.checkKing(kingPos)) {
            return false;
        }

        //check if king can escape
        int[][] attacks = king.getValidAttacks();
        int turnFactor = -1;

        if(teams[1].equals(team)) {
            turnFactor = 1;
        }

        for(int[] vect : attacks) {
            int eX = sX + vect[0]*turnFactor;
            int eY = sY + vect[1]*turnFactor;

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("startX", sX);
            node.put("startY", sY);
            node.put("endX", eX);
            node.put("endY", eY);

            if(this.kingProtected.verifyMove(node, kingPos)) {
                return false;
            }

        }

        //check every square for the team to see if a move or attack gets out of check
        for(int i = 0; i < this.board.getSideLength(); i++) {
            for(int j = 0; j < this.board.getSideLength(); j++) {
                BoardGamePiece piece = this.board.getBoardPos(i, j).getPiece();

                if(piece == null || !piece.getTeam().equals(team)) {
                    continue;
                }

                //checking attacking moves
                attacks = piece.getValidAttacks();
                List<Action> attackRules = piece.getAttackRules();
                int eX;
                int eY;
                boolean valid = false;
                ObjectMapper mapper = new ObjectMapper();
                for(int[] vect : attacks) {
                    eX = i + vect[0] * turnFactor;
                    eY = j + vect[1] * turnFactor;
                    BoardGameMove m = new BoardGameMove(i, j, eX, eY);
                    JsonNode node = mapper.valueToTree(m);
                    valid = this.validAttack.checkMove(node);

                    if(!valid) {
                        continue;
                    }
                    
                    valid = false;
                    for(Action act : attackRules) {
                        boolean passed = true;
                        List<Constraint> constList = act.getConstraints();
                        for(Constraint constraint : constList) {
                            if(!constraint.checkMove(node)) {
                                passed = false;
                                break;
                            }
                        }
                        if(passed) {
                            valid = true;
                            break;
                        }

                    }

                    if(valid && this.kingProtected.verifyMove(node, kingPos)) {
                        return false;
                    }
                }


                //checking non-attacking moves
                int[][] moves = piece.getValidMoves();
                List<Action> moveRules = piece.getMoveRules();

                valid = false;
                for(int[] vect : moves) {
                    eX = i + vect[0] * turnFactor;
                    eY = j + vect[1] * turnFactor;
                    BoardGameMove m = new BoardGameMove(i, j, eX, eY);
                    JsonNode node = mapper.valueToTree(m);
                    valid = this.validMove.checkMove(node);

                    if(!valid) {
                        continue;
                    }
                    
                    valid = false;
                    for(Action act : moveRules) {
                        boolean passed = true;
                        List<Constraint> constList = act.getConstraints();
                        for(Constraint constraint : constList) {
                            if(!constraint.checkMove(node)) {
                                passed = false;
                                break;
                            }
                        }
                        if(passed) {
                            valid = true;
                            break;
                        }

                    }

                    if(valid && this.kingProtected.verifyMove(node, kingPos)) {
                        return false;
                    }
                }
            }
        }


        return true;

    }

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.kingProtected = (KingProtectedConst)dependencies.get("kingProtectd");
        this.validMove = (ValidChessMoveConst)dependencies.get("validChessMove");
        this.validAttack = (ValidChessAttackConst)dependencies.get("validChessAttack");

    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }
    
}
