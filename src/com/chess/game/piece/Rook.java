package com.chess.game.piece;

import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.google.common.collect.ImmutableList;

import com.chess.game.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Piece{

    private static final int[] POSSIBLE_MOVES_VECTOR_OFFSET = {-8, -1, 1, 8};

    public Rook(final int piecePosition, final Color pieceColor) {
        super(PieceType.ROOK, piecePosition, pieceColor, true);
    }
    public Rook(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.ROOK, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Collection<Move> legalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentOffset : POSSIBLE_MOVES_VECTOR_OFFSET) {
            int possibleDestinationOfPieceCoordinate = this.piecePosition;
            while (true){
                if(isFirstColumnSpecialCase(possibleDestinationOfPieceCoordinate,currentOffset) ||
                        isEighthColumnSpecialCase(possibleDestinationOfPieceCoordinate,currentOffset)){
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

    @Override
    public Rook movePiece(final Move move) {
        return new Rook(move.getDestinationCoordinate(),move.getPieceToBeMoved().getPieceColor(), false);
    }

    private static boolean isFirstColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (offset == -1);
    }

    private static boolean isEighthColumnSpecialCase(final int currentPosition, final int offset) {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && (offset == 1);
    }

    public String toString(){
        return PieceType.ROOK.toString();
    }
}
