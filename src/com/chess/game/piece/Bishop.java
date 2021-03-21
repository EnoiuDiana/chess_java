package com.chess.game.piece;

import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.google.common.collect.ImmutableList;

import com.chess.game.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Bishop extends Piece {

    private static final int[] POSSIBLE_MOVES_VECTOR_OFFSET = {-9, -7, 7, 9};

    public Bishop(final int piecePosition, final Color pieceColor) {
        super(PieceType.BISHOP, piecePosition, pieceColor, true);
    }

    public Bishop(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentOffset : POSSIBLE_MOVES_VECTOR_OFFSET) {
            int possibleDestinationOfPieceCoordinate = this.piecePosition;
            while (true) {
                if (isFirstColumnSpecialCase(possibleDestinationOfPieceCoordinate, currentOffset) ||
                        isEighthColumnSpecialCase(possibleDestinationOfPieceCoordinate, currentOffset)) {
                    //move not allowed there
                    break;
                }
                possibleDestinationOfPieceCoordinate += currentOffset;
                final Move move = PieceUtils.calculateLegalMoveForSlipperyPieces(board, possibleDestinationOfPieceCoordinate,
                        this, this.pieceColor);
                if (move == null) {
                    break;
                } else {
                    legalMoves.add(move);
                    if(move instanceof Move.AttackMove) {
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }
    private static boolean isFirstColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((offset == -9) || (offset == 7));
    }

    private static boolean isEighthColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && ((offset == -7) || (offset == 9));
    }

    @Override
    public Bishop movePiece(final Move move) {
        return new Bishop(move.getDestinationCoordinate(),move.getPieceToBeMoved().getPieceColor(),false);
    }
    public String toString(){
        return PieceType.BISHOP.toString();
    }
}
