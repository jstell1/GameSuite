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

        //verify if opponent king is in check, return false if not
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

            if(!this.board.isValidPos(sX, sY) || !this.board.isValidPos(eX, eY)) {
                continue;
            }

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
                String vectType = piece.getVectType();
                List<Action> attackRules = piece.getAttackRules();

                boolean valid = isCheckmate(attacks, attackRules, turnFactor, i, j, vectType, kingPos, this.validAttack);
                if(!valid) {
                    return false;
                }

                //checking non-attacking moves
                int[][] moves = piece.getValidMoves();
                List<Action> moveRules = piece.getMoveRules();

                valid = isCheckmate(moves, moveRules, turnFactor, i, j, vectType, kingPos, this.validMove);

                if(!valid) {
                    return false;
                }


            }
        }


        return true;

    }


    public boolean isCheckmate(int[][] moves, List<Action> moveRules, int turnFactor, int i, int j, String vectType, CoordPairImpl kingPos, Constraint validMove) {
        ObjectMapper mapper = new ObjectMapper();
        boolean valid = false;
        for (int[] vect : moves) {
            int eX = i + vect[0] * turnFactor;
            int eY = j + vect[1] * turnFactor;
            int rise = eY - j;
            int run = eX - i;

            BoardGamePiece targ = null;


            while (targ == null && this.board.isValidPos(eX, eY)) {
                BoardGameMove m = new BoardGameMove(i, j, eX, eY);
                JsonNode node = mapper.valueToTree(m);
                valid = validMove.checkMove(node);
                targ = this.board.getBoardPos(eX, eY).getPiece();
                if (!valid) {
                    if (vectType.equals("exact")) {
                        break;
                    }

                    if (rise == 0) {
                        eX += vect[0] * turnFactor;
                    } else if (run == 0) {
                        eY += vect[1] * turnFactor;
                    } else {
                        eX += vect[0] * turnFactor;
                        eY += vect[1] * turnFactor;
                    }
                    continue;
                }

                valid = false;
                for (Action act : moveRules) {
                    boolean passed = true;
                    List<Constraint> constList = act.getConstraints();
                    for (Constraint constraint : constList) {
                        if (!constraint.checkMove(node)) {
                            passed = false;
                            break;
                        }
                    }
                    if (passed) {
                        valid = true;
                        break;
                    }

                }

                if (valid && this.kingProtected.verifyMove(node, kingPos)) {
                    return false;
                }
                if (vectType.equals("exact")) {
                    break;
                }

                if (rise == 0) {
                    eX += vect[0] * turnFactor;
                } else if (run == 0) {
                    eY += vect[1] * turnFactor;
                } else {
                    eX += vect[0] * turnFactor;
                    eY += vect[1] * turnFactor;
                }
            }

        }
        return true;
    }

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.kingProtected = (KingProtectedConst)dependencies.get("kingProtected");
        this.validMove = (ValidChessMoveConst)dependencies.get("validChessMove");
        this.validAttack = (ValidChessAttackConst)dependencies.get("validChessAttack");

    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }
    
}
