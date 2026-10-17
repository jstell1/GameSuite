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
    private final String[] dependencies = {"validChessMove", "validChessAttack"};
    private Constraint validMove;
    private Constraint validAttack;


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

                String team = piece.getTeam();
                String[] teams = this.gameState.getTeamNames();

                int turnFactor = -1;

                if(teams[1].equals(team)) {
                    turnFactor = 1;
                }

                 //checking attacking moves
                int[][] attacks = piece.getValidAttacks();
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
                            return false;
                        }

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
                            return false;
                        }

                    }
                }



            }
        }


        return true;
    }

    @Override
    public void addDependencies(Map<String, Constraint> dependencies) {
        this.validAttack = dependencies.get("validChessAttack");
        this.validMove = dependencies.get("validChessMove");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(Arrays.copyOf(this.dependencies, this.dependencies.length));
    }
    
}
