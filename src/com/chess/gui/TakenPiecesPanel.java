package com.chess.gui;

import com.chess.game.board.Move;
import com.chess.game.piece.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.chess.gui.Table.*;

public class TakenPiecesPanel extends JPanel {
    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Color PANEL_COLOR = Color.decode("#F9EDD2");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(40,600);
    private static final String defaultPieceImagesPath = "src/com/chess/images/pieces/";

    public TakenPiecesPanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8,2));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECES_DIMENSION);
    }
    public void redo(final MoveLog moveLog) {
        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMoves()) {
            if(move.isAttack()) {
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceColor().isWhite()) {
                    whiteTakenPieces.add(takenPiece);
                } else if(takenPiece.getPieceColor().isBlack()) {
                    blackTakenPieces.add(takenPiece);
                } else {
                    throw new RuntimeException("should not reach here");
                }
            }
        }
        whiteTakenPieces.sort((p1, p2) -> Ints.compare(p1.getPieceType().getPieceValue(), p2.getPieceType().getPieceValue()));
        blackTakenPieces.sort((p1, p2) -> Ints.compare(p1.getPieceType().getPieceValue(), p2.getPieceType().getPieceValue()));
        for(final Piece takenPiece : whiteTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath +
                        takenPiece.getPieceColor().toString().charAt(0) +
                        "" +
                        takenPiece.toString() +
                        ".png"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                        icon.getIconWidth() - 35, icon.getIconWidth() - 35, Image.SCALE_SMOOTH)));
                this.southPanel.add(imageLabel);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(final Piece takenPiece : blackTakenPieces) {
            try {
                final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath +
                        takenPiece.getPieceColor().toString().charAt(0) +
                        "" +
                        takenPiece.toString() +
                        ".png"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(
                icon.getIconWidth() - 35, icon.getIconWidth() - 35, Image.SCALE_SMOOTH)));
                this.northPanel.add(imageLabel);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        validate();
    }
}
