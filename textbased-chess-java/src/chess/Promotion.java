package chess;

import board.Board;
import board.Piece;
import chess.exceptions.ChessException;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Queen;
import chess.pieces.Rook;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Promotion {
    BISHOP("B", Bishop.class),
    KNIGHT("N", Knight.class),
    QUEEN("Q", Queen.class),
    ROOK("R", Rook.class);

    private static final Map<String, Promotion> entries = new HashMap<>() {{
        for (Promotion promotion : EnumSet.allOf(Promotion.class)) {
            put(promotion.getLetter(), promotion);
        }
    }};

    private final String letter;
    private final Class<? extends ChessPiece> piece;

    Promotion(String letter, Class<? extends ChessPiece> piece) {
        this.letter = letter;
        this.piece = piece;
    }

    public String getLetter() {
        return this.letter;
    }

    public Class<? extends ChessPiece> getPiece() {
        return this.piece;
    }

    public static boolean exists(String letter) {
        return letter != null && entries.containsKey(letter);
    }

    public static Promotion entryOf(String letter) {
        if (!exists(letter.toUpperCase())) {
            throw new IllegalArgumentException("Invalid Letter!");
        }

        return entries.get(letter.toUpperCase());
    }

    public static Piece getInstance(Promotion promotion, Board board, Color color) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return promotion
                .getPiece()
                .getDeclaredConstructor(Board.class, Color.class)
                .newInstance(board, color);
    }
}
