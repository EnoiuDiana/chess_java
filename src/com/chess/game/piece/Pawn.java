package com.chess.game.piece;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.chess.game.board.Move.*;
import com.google.common.collect.ImmutableList;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Pawn extends Piece{

    private final static int[] POSSIBLE_MOVES_OFFSET = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Color pieceColor) {
        super(PieceType.PAWN, piecePosition, pieceColor, true);
    }
    public Pawn(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for(int currentOffset : POSSIBLE_MOVES_OFFSET) {
            final int possibleDestinationOfPieceCoordinate = this.piecePosition + (this.pieceColor.getDirection() * currentOffset);

            if(!BoardUtils.isValidCoordinate(possibleDestinationOfPieceCoordinate)) {
                continue;
            }

            if(currentOffset == 8 && !board.getSquare(possibleDestinationOfPieceCoordinate).isSquareOccupied()) {
                if(this.pieceColor.isPawnPromotionSquare(possibleDestinationOfPieceCoordinate)) {
                    legalMoves.add(new PawnPromotion(new PawnBasicMove(board, this, possibleDestinationOfPieceCoordinate)));
                } else {
                    legalMoves.add(new PawnBasicMove(board, this, possibleDestinationOfPieceCoordinate));
                }
            } else if (currentOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.piecePosition] && this.pieceColor.isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePosition] && this.pieceColor.isWhite()))){
                final int behindDestinationOfPiece = this.piecePosition + (this.pieceColor.getDirection() * 8);
                if(!board.getSquare(behindDestinationOfPiece).isSquareOccupied() &&
                !board.getSquare(possibleDestinationOfPieceCoordinate).isSquareOccupied()) {
                    legalMoves.add(new PawnJumpMove(board, this, possibleDestinationOfPieceCoordinate));
                }
            } else if(currentOffset == 7 && !(BoardUtils.EIGHT_COLUMN[this.piecePosition] && (this.pieceColor.isWhite()) ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceColor.isBlack()))) {
                if(board.getSquare(possibleDestinationOfPieceCoordinate).isSquareOccupied()) {
                    final Piece attackedPiece = board.getSquare(possibleDestinationOfPieceCoordinate).getPiece();
                    if(attackedPiece.getPieceColor() != this.pieceColor) {
                        if(this.pieceColor.isPawnPromotionSquare(possibleDestinationOfPieceCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate, attackedPiece)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate, attackedPiece));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) {
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceColor.getOppositeDirection()))) {
                        final Piece attackedPiece = board.getEnPassantPawn();
                        if(this.pieceColor != attackedPiece.getPieceColor()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate,attackedPiece));
                        }
                    }
                }
            } else if(currentOffset == 9 && !(BoardUtils.EIGHT_COLUMN[this.piecePosition] && (this.pieceColor.isBlack()) ||
                    (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.pieceColor.isWhite()))) {
                if(board.getSquare(possibleDestinationOfPieceCoordinate).isSquareOccupied()) {
                    final Piece attackedPiece = board.getSquare(possibleDestinationOfPieceCoordinate).getPiece();
                    if(attackedPiece.getPieceColor() != this.pieceColor) {
                        if(this.pieceColor.isPawnPromotionSquare(possibleDestinationOfPieceCoordinate)) {
                            legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate, attackedPiece)));
                        } else {
                            legalMoves.add(new PawnAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate, attackedPiece));
                        }
                    }
                } else if(board.getEnPassantPawn() != null) {
                    if(board.getEnPassantPawn().getPiecePosition() == (this.piecePosition - (this.pieceColor.getOppositeDirection()))) {
                        final Piece attackedPiece = board.getEnPassantPawn();
                        if(this.pieceColor != attackedPiece.getPieceColor()) {
                            legalMoves.add(new PawnEnPassantAttackMove(board, this,
                                    possibleDestinationOfPieceCoordinate,attackedPiece));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getPieceToBeMoved().getPieceColor(), false);
    }

    public String toString(){
        return PieceType.PAWN.toString();
    }
}
