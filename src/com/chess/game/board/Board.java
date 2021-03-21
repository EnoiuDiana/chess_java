package com.chess.game.board;

import com.chess.game.Color;
import com.chess.game.piece.*;
import com.chess.game.player.BlackPlayer;
import com.chess.game.player.Player;
import com.chess.game.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

    private final List<Square> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    public Square getSquare(final int squareCoordinate) {
        return gameBoard.get(squareCoordinate);
    }

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Color.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Color.BLACK);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> allWhiteLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> allBlackLegalMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, allWhiteLegalMoves, allBlackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, allBlackLegalMoves, allWhiteLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public Collection<Piece> getBlackPieces () {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces () {
        return this.whitePieces;
    }

    public Collection<Piece> getAllPieces() {
        return Stream.concat(this.whitePieces.stream(),
                this.blackPieces.stream()).collect(Collectors.toList());
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }
    public Player getBlackPlayer() {
        return this.blackPlayer;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        final List<Move> LegalMoves = new ArrayList<>();

        for(final Piece piece : pieces) {
            LegalMoves.addAll(piece.legalMoves(this));
        }
        return ImmutableList.copyOf(LegalMoves);
    }

    private static Collection<Piece> calculateActivePieces(final List<Square> gameBoard, final Color color) {
        final List<Piece> activePieces = new ArrayList<>();
        for(final Square square : gameBoard) {
            if(square.isSquareOccupied()) {
                final Piece piece = square.getPiece();
                if(piece.getPieceColor() == color) {
                    activePieces.add(piece);
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    private static List<Square> createGameBoard (final Builder builder) {
        final Square[] squares = new Square[BoardUtils.NO_OF_SQUARES];
        for (int i=0; i< BoardUtils.NO_OF_SQUARES; i++) {
            squares[i] = Square.createSquare(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(squares);
    }

    public static Board initializeStandardBoard() {
        final Builder builder = new Builder();
        //set the black pieces
        builder.setPiece(new Rook(0, Color.BLACK));
        builder.setPiece(new Knight(1, Color.BLACK));
        builder.setPiece(new Bishop(2, Color.BLACK));
        builder.setPiece(new Queen(3, Color.BLACK));
        builder.setPiece(new King(4, Color.BLACK));
        builder.setPiece(new Bishop(5, Color.BLACK));
        builder.setPiece(new Knight(6, Color.BLACK));
        builder.setPiece(new Rook(7, Color.BLACK));
        builder.setPiece(new Pawn(8, Color.BLACK));
        builder.setPiece(new Pawn(9, Color.BLACK));
        builder.setPiece(new Pawn(10, Color.BLACK));
        builder.setPiece(new Pawn(11, Color.BLACK));
        builder.setPiece(new Pawn(12, Color.BLACK));
        builder.setPiece(new Pawn(13, Color.BLACK));
        builder.setPiece(new Pawn(14, Color.BLACK));
        builder.setPiece(new Pawn(15, Color.BLACK));

        //set the white pieces
        builder.setPiece(new Pawn(48, Color.WHITE));
        builder.setPiece(new Pawn(49, Color.WHITE));
        builder.setPiece(new Pawn(50, Color.WHITE));
        builder.setPiece(new Pawn(51, Color.WHITE));
        builder.setPiece(new Pawn(52, Color.WHITE));
        builder.setPiece(new Pawn(53, Color.WHITE));
        builder.setPiece(new Pawn(54, Color.WHITE));
        builder.setPiece(new Pawn(55, Color.WHITE));
        builder.setPiece(new Rook(56, Color.WHITE));
        builder.setPiece(new Knight(57, Color.WHITE));
        builder.setPiece(new Bishop(58, Color.WHITE));
        builder.setPiece(new Queen(59, Color.WHITE));
        builder.setPiece(new King(60, Color.WHITE));
        builder.setPiece(new Bishop(61, Color.WHITE));
        builder.setPiece(new Knight(62, Color.WHITE));
        builder.setPiece(new Rook(63, Color.WHITE));

        //white moves first
        builder.setMoveMaker(Color.WHITE);

        return builder.build();
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    public static class Builder {
        Map<Integer, Piece> boardConfig;
        Color nextMoveMaker;
        private Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public void setMoveMaker(final Color nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }
}
