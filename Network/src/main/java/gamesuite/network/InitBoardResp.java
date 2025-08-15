package gamesuite.network;

import gamesuite.core.model.CoordPair;

public class InitBoardResp {
    public CoordPair[][] board;

    public InitBoardResp(CoordPair[][] board) {
        this.board = board;
    }

    
}
