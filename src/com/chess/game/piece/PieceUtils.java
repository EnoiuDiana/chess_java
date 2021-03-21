package com.chess.game.piece;

import com.chess.game.Color;
import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.chess.game.board.Square;

public class PieceUtils {
    private PieceUtils(){
        throw new RuntimeException("Cannot instantiate Piece Utils!");
    }

    public static Move calculateLegalMoveForSlipperyPieces(final Board board, final int possibleDestinationOfPieceCoordinate,
                                                           final Piece pieceToBeMoved, final Color pieceColor) {
        if(BoardUtils.isValidCoordinate(possibleDestinationOfPieceCoordinate)) {
            final Square possibleDestinationSquare = board.getSquare(possibleDestinationOfPieceCoordinate);
            if(!possibleDestinationSquare.isSquareOccupied()) {
               return new Move.BasicMove(board, pieceToBeMoved, possibleDestinationOfPieceCoordinate);
            } else {
                final Piece pieceAtDestination = possibleDestinationSquare.getPiece();
                final Color pieceAtDestinationColor = pieceAtDestination.getPieceColor();
                if(pieceColor != pieceAtDestinationColor) {
                    return new Move.AttackMove(board, pieceToBeMoved, possibleDestinationOfPieceCoordinate,
                            pieceAtDestination);
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    public static Move calculateLegalMoveForNonSlipperyPieces(final Board board, final int possibleDestinationOfPieceCoordinate,
                                                           final Piece pieceToBeMoved, final Color pieceColor) {
        final Square possibleDestinationSquare = board.getSquare(possibleDestinationOfPieceCoordinate);
        if(!possibleDestinationSquare.isSquareOccupied()) {
            return new Move.BasicMove(board, pieceToBeMoved, possibleDestinationOfPieceCoordinate);
        } else {
            final Piece pieceAtDestination = possibleDestinationSquare.getPiece();
            final Color pieceAtDestinationColor = pieceAtDestination.getPieceColor();
            if(pieceColor != pieceAtDestinationColor) {
                return new Move.AttackMove(board, pieceToBeMoved, possibleDestinationOfPieceCoordinate,
                        pieceAtDestination);
            }
        }
        return null;
    }
}
