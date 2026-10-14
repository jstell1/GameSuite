package gamesuite.chess;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;

public class ValidChessAttackConst extends Constraint {

    private GameBoardImpl board;
    private BoardGameState gameState;

    public ValidChessAttackConst() {
        super("validChessAttack");
    }

    public ValidChessAttackConst(GameState gameState, GameBoard board) {
        super(gameState, board);
        this.name = "validChessAttack";
        //TODO Auto-generated constructor stub
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
        
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

         if(!this.board.isValidPos(sX, sY) || !this.board.isValidPos(eX, eY)) {
            return false;
        }

        int turnFactor = this.gameState.getTurnFactor();
        String[] teamNames = this.gameState.getTeamNames();
        int turn = this.gameState.getTurn();
        CoordPairImpl start = this.board.getBoardPos(sX, sY);
        CoordPairImpl end = this.board.getBoardPos(eX, eY);

        BoardGamePiece piece = start.getPiece();
        String team = piece.getTeam();
        
        //int maxDist = piece.getMaxDist();
        String vectType = piece.getVectType();
        int[][] validJumps = piece.getValidJumps();
        
        BoardGamePiece piece2 = end.getPiece();
        if(piece2 != null && piece2.getTeam().equals(team)) {
            return false;
        }
        
        //confirm vector
        if(vectType.equals("exact")) {
            boolean check = false;
            for(int[] pair : validJumps) {
                int x = start.getX() + pair[0] * turnFactor;
                int y = start.getY() + pair[1] * turnFactor;
                
                if(end.getX() == x && end.getY() == y) {
                    check = true;
                    break;
                }
            }

            
            return check;
             
        } else if(vectType.equals("directional")) {
            int rise = eY - sY;
            int run = eX - sX;
            int slope = rise / run;
            if(rise == 0) {

                 for(int[] pair : validJumps) {
                    int xDirect = pair[0] * turnFactor;
                    int tmpX = sX + xDirect;
    
                    if(sameSign(xDirect, run)) {
                        while(!reachedFinal(xDirect, eX, tmpX)) {
                            CoordPairImpl pos = this.board.getBoardPos(tmpX, sY);
                            BoardGamePiece tmp = pos.getPiece();
                            if(tmp != null) {
                                return false;
                            }
                            tmpX += xDirect;
                        }
                        return true;
                    }
    
                }
            } else if(run == 0) {

                 for(int[] pair : validJumps) {
                    int yDirect = pair[1] * turnFactor;
                    int tmpY = sY + yDirect;
    
                    if(sameSign(yDirect, rise)) {
                        while(!reachedFinal(yDirect, eY, tmpY)) {
                            CoordPairImpl pos = this.board.getBoardPos(sX, tmpY);
                            BoardGamePiece tmp = pos.getPiece();
                            if(tmp != null) {
                                return false;
                            }
                            tmpY += yDirect;
                        }
                        return true;
                    }
    
                }
            } else {
                
                if(slope != 1 || slope != -1) {
                    return false;
                }
                
                for(int[] pair : validJumps) {
                    int xDirect = pair[0] * turnFactor;
                    int yDirect = pair[1] * turnFactor;
                    int tmpX = sX + xDirect;
                    int tmpY = sY + yDirect;
    
                    if(sameSign(xDirect, run) && sameSign(yDirect, rise)) {
                        while(!reachedFinal(xDirect, eX, tmpX) && !reachedFinal(yDirect, eY, tmpY)) {
                            CoordPairImpl pos = this.board.getBoardPos(tmpX, tmpY);
                            BoardGamePiece tmp = pos.getPiece();
                            if(tmp != null) {
                                return false;
                            }
                            tmpX += xDirect;
                            tmpY += yDirect;
                        }
                        return true;
                    }
    
                }
            }

        }
        
        return false;
    }

    private boolean sameSign(int num1, int num2) {
        if(num1 < 0 && num2 < 0) {
            return true;
        }
        if(num1 > 0 && num2 > 0) {
            return true;
        }
        if(num1 == num2) {
            return true;
        }
        return false;
    }

    private boolean reachedFinal(int dir, int finalPos, int targPos) {

        if(dir < 0) {
            return targPos <= finalPos;
        } else if(dir > 0) {
            return targPos >= finalPos;
        }
        return false;
    }
    
}
