package com.chess.game.piece;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight extends Piece{

    private static final int[] POSSIBLE_MOVES_OFFSET = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(final int piecePosition, final Color pieceColor) {
        super(PieceType.KNIGHT, piecePosition, pieceColor, true);
    }
    public Knight(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves= new ArrayList<>();

        for(int currentOffset : POSSIBLE_MOVES_OFFSET) {
            final int possibleDestinationOfPieceCoordinate = this.piecePosition + currentOffset;
            if(BoardUtils.isValidCoordinate(possibleDestinationOfPieceCoordinate)) {
                if(isFirstColumnSpecialCase(this.piecePosition,currentOffset) ||
                        isSecondColumnSpecialCase(this.piecePosition,currentOffset) ||
                        isSeventhColumnSpecialCase(this.piecePosition,currentOffset) ||
                        isEighthColumnSpecialCase(this.piecePosition,currentOffset)){
                    //move not allowed there
                    continue;
                }
                final Move move = PieceUtils.calculateLegalMoveForNonSlipperyPieces(board, possibleDestinationOfPieceCoordinate,
                        this, this.pieceColor);
                if (move != null) {
                    legalMoves.add(move);
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(final Move move) {
        return new Knight(move.getDestinationCoordinate(),move.getPieceToBeMoved().getPieceColor(), false);
    }

    private static boolean isFirstColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((offset == - 17) || (offset == -10)
                || ((offset) == 6) || (offset == 15));
    }
    private static boolean isSecondColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((offset == -10) || ((offset) == 6));
    }

    private static boolean isSeventhColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.SEVEN_COLUMN[currentPosition] && ((offset == -6) || ((offset) == 10));
    }

    private static boolean isEighthColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((offset == 17) || (offset == 10)
                || ((offset) == -6) || (offset == -15));
    }

    public String toString(){
        return PieceType.KNIGHT.toString();
    }
}
