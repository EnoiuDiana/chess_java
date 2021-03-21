package com.chess.game.piece;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private final static int[] POSSIBLE_MOVES_OFFSET = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Color pieceColor) {
        super(PieceType.KING, piecePosition, pieceColor, true);
    }
    public King(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.KING, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentOffset : POSSIBLE_MOVES_OFFSET) {
            final int possibleDestinationOfPieceCoordinate = this.piecePosition + currentOffset;
            if(BoardUtils.isValidCoordinate(possibleDestinationOfPieceCoordinate)) {
                if(isFirstColumnSpecialCase(this.piecePosition,currentOffset) ||
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
    public King movePiece(final Move move) {
        return new King(move.getDestinationCoordinate(),move.getPieceToBeMoved().getPieceColor(), false);
    }

    private static boolean isFirstColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((offset == -9) || (offset == -1)
                || ((offset) == 7));
    }

    private static boolean isEighthColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((offset == -7) || (offset == 1)
                || ((offset) == 9));
    }

    public String toString(){
        return PieceType.KING.toString();
    }
}
