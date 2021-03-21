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

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board, final Collection<Move> whiteLegalMoves, final Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    public Collection<Move> calculateCastleMove(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals) {
        final List<Move> castleMoves = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !isInCheck()){
            //whites king side castle
            if(IntStream.of(61, 62).noneMatch(i -> this.board.getSquare(i).isSquareOccupied())) {
                final Square rookSquare = this.board.getSquare(63);
                if(rookSquare.isSquareOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if(IntStream.of(61, 62).allMatch(i -> Player.calculateAttacksOnSquare(i, opponentsLegals).isEmpty()) &&
                            rookSquare.getPiece().getPieceType().isRook()) {
                        castleMoves.add(new Move.KingSideCastleMove(
                                this.board,
                                this.playerKing,
                                62,
                                (Rook)rookSquare.getPiece(),
                                rookSquare.getSquareCoordinate(),
                                61));
                    }
                }
            }
            //whites queen side castle
            if(IntStream.of(59, 58, 57).noneMatch(i -> this.board.getSquare(i).isSquareOccupied())) {
                final Square rookSquare = this.board.getSquare(56);
                if(rookSquare.isSquareOccupied() && rookSquare.getPiece().isFirstMove()) {
                    if(IntStream.of(59, 58, 57).allMatch(i -> Player.calculateAttacksOnSquare(i, opponentsLegals).isEmpty()) &&
                            rookSquare.getPiece().getPieceType().isRook()) {
                        castleMoves.add(new Move.QueenSideCastleMove(
                                this.board,
                                this.playerKing,
                                58,
                                (Rook)rookSquare.getPiece(),
                                rookSquare.getSquareCoordinate(),
                                59));
                    }
                }
            }
        }
        return ImmutableList.copyOf(castleMoves);
    }
}
