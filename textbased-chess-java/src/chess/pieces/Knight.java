package chess.pieces;

import board.Board;
import board.Position;
import chess.ChessPiece;
import chess.Color;

import java.util.Objects;

public class Knight extends ChessPiece {

    public Knight(Board board, Color color) {
        super(board, color);
    }

    private boolean canMove(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);

        return Objects.isNull(piece) || piece.getColor() != getColor();
    }

    @Override
    public String toString() {
        return "N";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] movesMap = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position[] directions = {
                new Position(position.getRow() - 1, position.getColumn() - 2),
                new Position(position.getRow() - 2, position.getColumn() - 1),
                new Position(position.getRow() - 2, position.getColumn() + 1),
                new Position(position.getRow() - 1, position.getColumn() + 2),
                new Position(position.getRow() + 1, position.getColumn() + 2),
                new Position(position.getRow() + 2, position.getColumn() + 1),
                new Position(position.getRow() + 2, position.getColumn() - 1),
                new Position(position.getRow() + 1, position.getColumn() - 2)
        };

        for (Position direction : directions) {
            if (getBoard().positionExists(direction) && canMove(direction)) {
                movesMap[direction.getRow()][direction.getColumn()] = true;
            }
        }

        return movesMap;
    }
}
