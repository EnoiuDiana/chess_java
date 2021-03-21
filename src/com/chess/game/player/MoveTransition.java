package com.chess.game.player;

import com.chess.game.board.Board;

public class MoveTransition {
    private final Board transitionBoard;
    private final MoveStatus moveStatus;

    public MoveTransition(Board transitionBoard,MoveStatus moveStatus) {
        this.transitionBoard = transitionBoard;
        this.moveStatus = moveStatus;
    }
    public Board getTransitionBoard() {
        return transitionBoard;
    }

    public MoveStatus getMoveStatus() {
        return moveStatus;
    }
}
