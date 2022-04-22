package chess;

import board.Board;
import board.Piece;
import board.Position;

import java.util.Objects;

public abstract class ChessPiece extends Piece {

    private Color color;

    private int moveCount;

    public ChessPiece(Board board, Color color) {
        super(board);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public void increaseMoveCount() {
        this.moveCount++;
    }

    public void decreaseMoveCount() {
        this.moveCount--;
    }

    public ChessPosition getChessPosition() {
        return ChessPosition.fromPosition(position);
    }

    protected boolean isThereOpponentPiece(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);

        return Objects.nonNull(piece) && piece.getColor() != color;
    }
}
