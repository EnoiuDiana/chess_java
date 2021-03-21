package com.chess.gui;

import com.chess.game.board.Board;
import com.chess.game.board.BoardUtils;
import com.chess.game.board.Move;
import com.chess.game.board.Square;
import com.chess.game.piece.Piece;
import com.chess.game.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {
    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(758, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600, 600);
    private static final Dimension SQUARE_PANEL_DIMENSION = new Dimension(68, 68);
    private static final String defaultPieceImagesPath = "src/com/chess/images/pieces/";
    private final Color lightSquareColor = Color.decode("#F0D9B7");
    private final Color darkSquareColor = Color.decode("#B48866");
    private final Color lightSquareHighlight = Color.decode("#F0EC96");
    private final Color darkSquareHighlight = Color.decode("#D8A541");
    private final Color attackedSquareHighlight = Color.decode("#DA3626");

    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private Board chessBoard;

    private Square sourceSquare;
    private Square destinationSquare;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private boolean highlightLegalMoves;
    private final JFrame gameFrame;

    public Table() {
        this.gameFrame = new JFrame("Chess.com");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.initializeStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = true;
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createEditMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }
    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createEditMenu() {
        final JMenu preferencesMenu = new JMenu("Edit");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(e -> {
            boardDirection = boardDirection.opposite();
            boardPanel.drawBoard(chessBoard);
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Highlight Legal Moves", true);
        legalMoveHighlighterCheckBox.addActionListener(e -> highlightLegalMoves = legalMoveHighlighterCheckBox.isSelected());
        preferencesMenu.add(legalMoveHighlighterCheckBox);
        return preferencesMenu;
    }
    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");
        final JMenuItem undoMoveMenuItem = new JMenuItem("Undo");
        undoMoveMenuItem.addActionListener(e -> {
            if(getMoveLog().size() > 0) {
                undoLastMove();
            }
        });
        optionsMenu.add(undoMoveMenuItem);
        optionsMenu.addSeparator();
        final JMenuItem undoAllMovesMenuItem = new JMenuItem("New Game");
        undoAllMovesMenuItem.addActionListener(e -> undoAllMoves());
        optionsMenu.add(undoAllMovesMenuItem);
        return optionsMenu;
    }

    private class BoardPanel extends JPanel {
        final List<SquarePanel> BoardSquares;

        BoardPanel() {
            super(new GridLayout(8,8));
            this.BoardSquares = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NO_OF_SQUARES; i++) {
                final SquarePanel squarePanel = new SquarePanel(this, i);
                this.BoardSquares.add(squarePanel);
                add(squarePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for(final SquarePanel squarePanel : boardDirection.traverse(BoardSquares)) {
                squarePanel.drawSquare(board);
                add(squarePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;
        public MoveLog() {
            this.moves = new ArrayList<>();
        }
        public List<Move> getMoves() {
            return this.moves;
        }
        public void addMove(final Move move) {
            this.moves.add(move);
        }
        public int size() {
            return this.moves.size();
        }
        public void clear() {
            this.moves.clear();
        }
        public void removeMove(final Move move) {
            this.moves.remove(move);
        }
        public Move removeMove (int index) {
            return this.moves.remove(index);
        }
    }

    public MoveLog getMoveLog() {
        return moveLog;
    }

    private void undoLastMove() {
        final Move lastMove = getMoveLog().removeMove(getMoveLog().size()-1);
        this.chessBoard = this.chessBoard.getCurrentPlayer().unmakeMove(lastMove).getTransitionBoard();
        getMoveLog().removeMove(lastMove);
        gameHistoryPanel.redo(chessBoard, getMoveLog());
        takenPiecesPanel.redo(getMoveLog());
        boardPanel.drawBoard(chessBoard);
    }

    private void undoAllMoves() {
        for(int i = getMoveLog().size() - 1; i >= 0; i--) {
            final Move lastMove = getMoveLog().removeMove(getMoveLog().size()-1);
            this.chessBoard = this.chessBoard.getCurrentPlayer().unmakeMove(lastMove).getTransitionBoard();
            getMoveLog().removeMove(lastMove);
        }
        getMoveLog().clear();
        gameHistoryPanel.redo(chessBoard, getMoveLog());
        takenPiecesPanel.redo(getMoveLog());
        boardPanel.drawBoard(chessBoard);
    }

    private class SquarePanel extends JPanel {
        private final int squareId;

        SquarePanel(final BoardPanel boardPanel, final int squareId) {
            super(new GridBagLayout());
            this.squareId = squareId;
            setPreferredSize(SQUARE_PANEL_DIMENSION);
            assignSquareColor();
            assignSquarePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    if (isRightMouseButton(e)){
                        sourceSquare = null;
                        destinationSquare = null;
                        humanMovedPiece = null;

                    } else if (isLeftMouseButton(e)){
                        if(sourceSquare == null) {
                            //first click
                            sourceSquare = chessBoard.getSquare(squareId);
                            humanMovedPiece = sourceSquare.getPiece();
                            if(humanMovedPiece == null) {
                                sourceSquare = null;
                            }

                        } else {
                            //second click
                            destinationSquare = chessBoard.getSquare(squareId);
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                    sourceSquare.getSquareCoordinate(),
                                    destinationSquare.getSquareCoordinate());
                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceSquare = null;
                            destinationSquare = null;
                            humanMovedPiece = null;
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        gameHistoryPanel.redo(chessBoard, moveLog);
                        takenPiecesPanel.redo(moveLog);
                        boardPanel.drawBoard(chessBoard);
                        if(chessBoard.getCurrentPlayer().isInCheckMate()) {
                            if(chessBoard.getCurrentPlayer().getColor().isWhite()) {
                                JOptionPane.showMessageDialog(gameFrame,
                                        "Black wins!");
                            } else if(chessBoard.getCurrentPlayer().getColor().isBlack()) {
                                JOptionPane.showMessageDialog(gameFrame,
                                        "White wins!");
                            }
                        }
                        if(chessBoard.getCurrentPlayer().isInStaleMate()) {
                            JOptionPane.showMessageDialog(gameFrame,
                                    "Draw!");
                        }
                    });
                }

                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();

        }
        public void drawSquare(Board board) {
            assignSquareColor();
            assignSquarePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }
        private void assignSquarePieceIcon(final Board board) {
            this.removeAll();
            if(board.getSquare(this.squareId).isSquareOccupied()) {
                try {
                    String path = defaultPieceImagesPath +
                            board.getSquare(this.squareId).getPiece().getPieceColor().toString().charAt(0) +
                            board.getSquare(this.squareId).getPiece().toString() +
                            ".png";
                    final BufferedImage image = ImageIO.read(new File(path));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void highlightLegalMoves(final Board board) {
            if(highlightLegalMoves) {
                for( final Move move : pieceLegalMoves(board)) {
                    if(move.getDestinationCoordinate() == this.squareId){
                        if(board.getSquare(squareId).isSquareOccupied()) {
                            setBackground(attackedSquareHighlight);
                        } else {
                            if(BoardUtils.FIRST_ROW[this.squareId] ||
                                    BoardUtils.THIRD_ROW[this.squareId] ||
                                    BoardUtils.FIFTH_ROW[this.squareId] ||
                                    BoardUtils.SEVENTH_ROW[this.squareId]) {
                                setBackground(this.squareId % 2 == 0 ? lightSquareHighlight : darkSquareHighlight);
                            } else if(BoardUtils.SECOND_ROW[this.squareId] ||
                                    BoardUtils.FORTH_ROW[this.squareId] ||
                                    BoardUtils.SIXTH_ROW[this.squareId] ||
                                    BoardUtils.EIGHTH_ROW[this.squareId]) {
                                setBackground(this.squareId % 2 == 0 ? darkSquareHighlight : lightSquareHighlight);
                            }
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceColor() == board.getCurrentPlayer().getColor()) {
                return calculateLegalMovesThatCanBeDone(humanMovedPiece.legalMoves(board));
            }
            return Collections.emptyList();
        }
        private Collection<Move> calculateLegalMovesThatCanBeDone(Collection<Move> legalMoves) {
            List<Move> legalMovesThatCanBeDone = new ArrayList<>();
            for(final Move move : legalMoves) {
                final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                if(transition.getMoveStatus().isDone()) {
                    if (transition.getMoveStatus().isDone()) {
                        legalMovesThatCanBeDone.add(move);
                    }
                }
            }
            legalMovesThatCanBeDone = ImmutableList.copyOf(Iterables.concat(legalMovesThatCanBeDone,
                    chessBoard.getCurrentPlayer().calculateCastleMove(legalMoves,
                            chessBoard.getCurrentPlayer().getOpponent().getLegalMoves())));
            return ImmutableList.copyOf(legalMovesThatCanBeDone);
        }

        private void assignSquareColor() {
            if(BoardUtils.FIRST_ROW[this.squareId] ||
                    BoardUtils.THIRD_ROW[this.squareId] ||
                    BoardUtils.FIFTH_ROW[this.squareId] ||
                    BoardUtils.SEVENTH_ROW[this.squareId]) {
                setBackground(this.squareId % 2 == 0 ? lightSquareColor : darkSquareColor);
            } else if(BoardUtils.SECOND_ROW[this.squareId] ||
                    BoardUtils.FORTH_ROW[this.squareId] ||
                    BoardUtils.SIXTH_ROW[this.squareId] ||
                    BoardUtils.EIGHTH_ROW[this.squareId]) {
                setBackground(this.squareId % 2 == 0 ? darkSquareColor : lightSquareColor);
            }
        }
    }
    public enum BoardDirection{
        NORMAL{
            @Override
            List<SquarePanel> traverse(List<SquarePanel> boardSquares) {
                return boardSquares;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<SquarePanel> traverse(List<SquarePanel> boardSquares) {
                return Lists.reverse(boardSquares);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };
        abstract List<SquarePanel> traverse (final List<SquarePanel> boardSquares);
        abstract BoardDirection opposite();

    }
    }


