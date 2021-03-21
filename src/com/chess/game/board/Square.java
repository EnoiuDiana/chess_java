package com.chess.game.board;

import com.chess.game.piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.chess.game.board.BoardUtils.NO_OF_SQUARES;

public abstract class Square {
    protected final int squareCoordinate;

    private Square(final int squareCoordinate) {
        this.squareCoordinate = squareCoordinate;
    }

    private static final Map<Integer, EmptySquare> EMPTY_SQUARES_CACHE = createAllPossibleEmptySquares();

    private static Map<Integer, EmptySquare> createAllPossibleEmptySquares() {
        final Map<Integer, EmptySquare> emptySquareMap = new HashMap<>();
        for( int i=0; i<NO_OF_SQUARES; i++) {
            emptySquareMap.put(i,new EmptySquare(i));
        }

        return ImmutableMap.copyOf(emptySquareMap);
    }

    public static Square createSquare(final int squareCoordinate, final Piece piece) {
        return piece != null ? new OccupiedSquare(squareCoordinate,piece) : EMPTY_SQUARES_CACHE.get(squareCoordinate);
    }

    public abstract boolean isSquareOccupied();

    public abstract Piece getPiece();

    public int getSquareCoordinate() {
        return squareCoordinate;
    }

    public static final class EmptySquare extends Square {

        private EmptySquare(final int squareCoordinate) {
            super(squareCoordinate);
        }

        @Override
        public boolean isSquareOccupied() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedSquare extends Square {

        private final Piece pieceOnSquare;

        private OccupiedSquare(final int squareCoordinate, final Piece pieceOnSquare) {
            super(squareCoordinate);
            this.pieceOnSquare = pieceOnSquare;
        }

        @Override
        public boolean isSquareOccupied() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnSquare;
        }

        @Override
        public String toString() {
            return getPiece().getPieceColor().isBlack() ? getPiece().toString().toLowerCase(Locale.ROOT) :
                    getPiece().toString();
        }
    }
}
