package com.chess.game.player;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.Move;
import com.chess.game.board.Square;
import com.chess.game.piece.Piece;
import com.chess.game.piece.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board, final Collection<Move> blackLegalMoves, final Collection<Move> whiteLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces () {
        return this.board.getBlackPieces();
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    public Collection<Move> calculateCastleMove(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        final List<Move> castleMoves = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !isInCheck()){
            //blacks king side castle
            if(IntStream.of(5, 6).noneMatch(i -> this.board.getSquare(i).isSquareOccupied())) {
                final Square rookSquare = this.board.getSquare(7);
                if(rookSquare.isSquareOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if(IntStream.of(5, 6).allMatch(i -> Player.calculateAttacksOnSquare(i, opponentsLegals).isEmpty()) &&
                            rookSquare.getPiece().getPieceType().isRook()) {
                        castleMoves.add(new Move.KingSideCastleMove(
                                this.board,
                                this.playerKing,
                                6,
                                (Rook)rookSquare.getPiece(),
                                rookSquare.getSquareCoordinate(),
                                5));
                    }
                }
            }
            //blacks queen side castle
            if(IntStream.of(1, 2, 3).noneMatch(i -> this.board.getSquare(i).isSquareOccupied())) {
                final Square rookSquare = this.board.getSquare(0);
                if(rookSquare.isSquareOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if(IntStream.of(1, 2, 3).allMatch(i -> Player.calculateAttacksOnSquare(i, opponentsLegals).isEmpty()) &&
                            rookSquare.getPiece().getPieceType().isRook()) {
                        castleMoves.add(new Move.QueenSideCastleMove(
                                this.board,
                                this.playerKing,
                                2,
                                (Rook)rookSquare.getPiece(),
                                rookSquare.getSquareCoordinate(),
                                3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(castleMoves);
    }
}
