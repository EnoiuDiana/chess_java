package com.chess.game.player;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.Move;
import com.chess.game.piece.King;
import com.chess.game.piece.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateCastleMove(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnSquare(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();
    }

    public King getPlayerKing() {
        return playerKing;
    }
    public Collection<Move> getLegalMoves() {
        return legalMoves;
    }
    public abstract Collection<Piece> getActivePieces();
    public abstract Color getColor();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateCastleMove(Collection<Move> playerLegals, Collection<Move> opponentsLegals);

    protected static Collection<Move> calculateAttacksOnSquare(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for(final Move move : moves) {
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing() {
        for(final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()) {
                return (King)piece;
            }
        }
        throw new RuntimeException("Not a valid board");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && hasNotEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && hasNotEscapeMoves();
    }

    protected boolean hasNotEscapeMoves() {
        for(final Move move: this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()) {
                return false;
            }
        }
        return true;
    }

    public MoveTransition makeMove(final Move move) {
        if(!isMoveLegal(move)) {
            return new MoveTransition(this.board, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnSquare
                (transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                        transitionBoard.getCurrentPlayer().getLegalMoves());
        if(!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, MoveStatus.DONE);
    }

    public MoveTransition unmakeMove (final Move move) {
        return new MoveTransition(move.undo(), MoveStatus.DONE);
    }
}
