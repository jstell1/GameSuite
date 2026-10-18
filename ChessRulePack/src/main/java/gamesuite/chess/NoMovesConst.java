package gamesuite.chess;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class NoMovesConst extends Constraint implements ConstDependent{

    private BoardGameState gameState;
    private GameBoardImpl board;
    private final String[] dependencies = {"validChessMove", "validChessAttack", "kingProtected", "isCheckmate"};
    private Constraint validMove;
    private Constraint validAttack;
    private KingProtectedConst kingProtected;
    private IsCheckmateConst isCheckmate;


    public NoMovesConst() {
        super("noMoves");
    }

    public NoMovesConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        
        this.name = "noMoves";
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

        for(int i = 0; i < this.board.getSideLength(); i++) {
            for(int j = 0; j < this.board.getSideLength(); j++) {

                BoardGamePiece piece = this.board.getBoardPos(i, j).getPiece();

                if(piece == null) {
                    continue;
                }
                String vectType = piece.getVectType();
                String team = piece.getTeam();
                String[] teams = this.gameState.getTeamNames();
                int turn = this.gameState.getTurn() %2 + 1;
                int turnFactor = this.gameState.getTurnFactor() * -1;

                if(!piece.getTeam().equals(teams[turn - 1])) {
                    continue;
                }
                 //checking attacking moves
                int[][] attacks = piece.getValidAttacks();
                List<Action> attackRules = piece.getAttackRules();
                CoordPairImpl king = this.kingProtected.getKingPos(turn);
                boolean valid = this.isCheckmate.isCheckmate(attacks, attackRules, turnFactor, i, j, vectType, king, this.validAttack);//isCheckmate(attacks,attackRules, turnFactor, i, j, vectType,this.validAttack);//hasNoMoves(attacks,i,j,turnFactor, attackRules, this.validAttack);

                if(!valid) {
                    return false;
                }

                //checking non-attacking moves
                int[][] moves = piece.getValidMoves();
                List<Action> moveRules = piece.getMoveRules();
                valid = this.isCheckmate.isCheckmate(moves, moveRules, turnFactor, i, j, vectType, king, this.validMove);//isCheckmate(attacks,attackRules, turnFactor, i, j, vectType,this.validMove);//hasNoMoves(moves, i, j, turnFactor, moveRules, this.validMove);

                if(!valid) {
                    return false;
                }

            }
        }
        return true;
    }


    public boolean hasNoMoves(int[][] attacks, int i, int j, int turnFactor, List<Action> attackRules, Constraint validAttack) {

        boolean valid = false;
        ObjectMapper mapper = new ObjectMapper();
        for(int[] vect : attacks) {
            int eX = i + vect[0] * turnFactor;
            int eY = j + vect[1] * turnFactor;
            BoardGameMove m = new BoardGameMove(i, j, eX, eY);
            JsonNode node = mapper.valueToTree(m);
            valid = validAttack.checkMove(node);

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
                    return false;
                }

            }
        }
        return true;
    }

    public boolean isCheckmate(int[][] moves, List<Action> moveRules, int turnFactor, int i, int j, String vectType, Constraint validMove) {
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

                if (valid) { //and kingProtected
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
        this.validAttack = dependencies.get("validChessAttack");
        this.validMove = dependencies.get("validChessMove");
        this.kingProtected = (KingProtectedConst)dependencies.get("kingProtected");
        this.isCheckmate = (IsCheckmateConst) dependencies.get("isCheckmate");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }
    
}
