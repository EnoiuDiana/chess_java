package com.chess.game;

import com.chess.game.board.BoardUtils;
import com.chess.game.player.BlackPlayer;
import com.chess.game.player.Player;
import com.chess.game.player.WhitePlayer;

public enum Color {
    WHITE(){
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_ROW[position];
        }
    },
    BLACK(){
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHTH_ROW[position];
        }
    };

    public abstract int getDirection();

    public abstract int getOppositeDirection();

    public abstract boolean isBlack();

    public abstract boolean isWhite();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

    public abstract boolean isPawnPromotionSquare(int position);
}
