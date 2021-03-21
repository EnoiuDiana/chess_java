package com.chess.game.board;

import com.chess.game.piece.Pawn;
import com.chess.game.piece.Piece;
import com.chess.game.piece.Queen;
import com.chess.game.piece.Rook;

import static com.chess.game.board.Board.*;

public abstract class Move {
    protected final Board board;
    protected final Piece pieceToBeMoved;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    private Move(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate) {
        this.board = board;
        this.pieceToBeMoved = pieceToBeMoved;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = pieceToBeMoved.isFirstMove();
    }
    private Move(final Board board,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.pieceToBeMoved = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result  = prime * result + this.pieceToBeMoved.hashCode();
        result = prime * result + this.pieceToBeMoved.getPiecePosition();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if(this == other) {
            return true;
        }
        if(!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getPieceToBeMoved() == otherMove.getPieceToBeMoved();

    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public Piece getPieceToBeMoved() {
        return pieceToBeMoved;
    }

    public int getCurrentCoordinate(){
        return this.pieceToBeMoved.getPiecePosition();
    }

    public boolean isAttack() {
        return false;
    }

    public Piece getAttackedPiece() {
        return null;
    }

    public Board getBoard() {
        return board;
    }

    public Board execute() {
        final Builder builder = new Builder();
        for(final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
            if(!this.pieceToBeMoved.equals(piece)) {
                builder.setPiece(piece);
            }
        }
        for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
            builder.setPiece(piece);
        }
        //move the piece to be moved
        builder.setPiece(this.pieceToBeMoved.movePiece(this));
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());
        return builder.build();
    }

    public Board undo() {
        final Builder builder = new Builder();
        for(final Piece piece : this.board.getAllPieces()){
            builder.setPiece(piece);
        }
        builder.setMoveMaker(this.board.getCurrentPlayer().getColor());
        return builder.build();
    }

    public static class BasicMove extends Move {
        public BasicMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate) {
            super(board, pieceToBeMoved, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof BasicMove && super.equals(other);

        }
        @Override
        public String toString() {
            return pieceToBeMoved.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate,
                          final Piece attackedPiece) {
            super(board, pieceToBeMoved, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece() {
            return this.attackedPiece;
        }
        @Override
        public String toString() {
            return pieceToBeMoved.getPieceType().toString() + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnPromotion extends Move {

        final Move decoratedMove;
        final Piece promotedPawn;
        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getPieceToBeMoved(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = decoratedMove.getPieceToBeMoved();
        }
        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }
        @Override
        public Board execute(){
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();
            for(final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePieces()) {
                if(!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            //Make the promoted Pawn to be a Queen
            builder.setPiece(new Queen(decoratedMove.getDestinationCoordinate(), decoratedMove.pieceToBeMoved.getPieceColor(),
                    false));
            builder.setMoveMaker(decoratedMove.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }
        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }
        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }
        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(decoratedMove.getDestinationCoordinate()) + "Q";
        }
    }

    public static final class PawnBasicMove extends BasicMove {
        public PawnBasicMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate) {
            super(board, pieceToBeMoved, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnBasicMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, pieceToBeMoved, destinationCoordinate, attackedPiece);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.pieceToBeMoved.getPiecePosition()).charAt(0) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttackMove extends AttackMove {
        public PawnEnPassantAttackMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate,
                              final Piece attackedPiece) {
            super(board, pieceToBeMoved, destinationCoordinate, attackedPiece);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof  PawnEnPassantAttackMove && super.equals(other);
        }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for( final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!this.pieceToBeMoved.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                if(!piece.equals(this.attackedPiece)) {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }

    }

    public static final class PawnJumpMove extends BasicMove {
        public PawnJumpMove(final Board board, final Piece pieceToBeMoved, final int destinationCoordinate) {
            super(board, pieceToBeMoved, destinationCoordinate);
        }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!(this.pieceToBeMoved.equals(piece))) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.pieceToBeMoved.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnJumpMove && super.equals(other);
        }
        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        private CastleMove(Board board,
                           Piece pieceToBeMoved,
                           int destinationCoordinate,
                           Rook castleRook,
                           int castleRookStart,
                           int castleRookDestination) {
            super(board, pieceToBeMoved, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook() {
            return castleRook;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces()) {
                if(!(this.pieceToBeMoved.equals(piece)) && !(this.castleRook.equals(piece))) {
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination, this.castleRook.getPieceColor(),false));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getColor());
            return builder.build();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }
    public static final class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(Board board,
                                   Piece pieceToBeMoved,
                                   int destinationCoordinate,
                                   Rook castleRook,
                                   int castleRookStart,
                                   int castleRookDestination) {
            super(board, pieceToBeMoved, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }

        @Override
        public String toString() {
            return "O-O";
        }
    }
    public static final class QueenSideCastleMove extends CastleMove {

        public QueenSideCastleMove(Board board,
                                    Piece pieceToBeMoved,
                                    int destinationCoordinate,
                                    Rook castleRook,
                                    int castleRookStart,
                                    int castleRookDestination) {
            super(board, pieceToBeMoved, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof QueenSideCastleMove)) {
                return false;
            }
            final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
            return super.equals(otherQueenSideCastleMove) && this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
        }
        @Override
        public String toString() {
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move {

        private NullMove() {
            super(null, -1);
        }

        @Override
        public int getCurrentCoordinate() {
            return -1;
        }
        @Override
        public int getDestinationCoordinate() {
            return -1;
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Cannot execute null move");
        }
        @Override
        public String toString() {
            return "Null Move";
        }
    }

    public static class MoveFactory {
        public MoveFactory() {
            throw new RuntimeException("Cannot instantiate move factory");
        }

        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if( move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return new NullMove();
        }
    }
}
