package com.chess;

import com.chess.game.board.Board;
import com.chess.gui.Table;

public class MainChess {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        Board board = Board.initializeStandardBoard();
        Table table = new Table();
    }
}
