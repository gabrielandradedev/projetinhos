package chess.pieces;

import board.Board;
import board.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

import java.util.Objects;

public class King extends ChessPiece {

    private ChessMatch match;

    public King(Board board, Color color, ChessMatch match) {
        super(board, color);
        this.match = match;
    }

    private boolean canMove(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);

        return Objects.isNull(piece) || piece.getColor() != getColor();
    }

    @Override
    public String toString() {
        return "K";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] movesMap = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position[] directions = {
                new Position(position.getRow() - 1, position.getColumn()), // top
                new Position(position.getRow() - 1, position.getColumn() + 1), // top-right
                new Position(position.getRow(), position.getColumn() + 1), // right
                new Position(position.getRow() + 1, position.getColumn() + 1), // bottom-right
                new Position(position.getRow() + 1, position.getColumn()), // bottom
                new Position(position.getRow() + 1, position.getColumn() - 1), // bottom-left
                new Position(position.getRow(), position.getColumn() - 1), // left
                new Position(position.getRow() - 1, position.getColumn() - 1) // top-left
        };

        for (Position direction : directions) {
            if (getBoard().positionExists(direction) && canMove(direction)) {
                movesMap[direction.getRow()][direction.getColumn()] = true;
            }
        }

        /* Castling */
        if (getMoveCount() == 0 && !match.testCheck(getColor())) {

            Position rightRook = new Position(position.getRow(), position.getColumn() + 3);
            Position leftRook = new Position(position.getRow(), position.getColumn() - 4);

            if (isACastableRook(rightRook)) {
                Position inBetween1 = new Position(position.getRow(), position.getColumn() + 1);
                Position inBetween2 = new Position(position.getRow(), position.getColumn() + 2);

                if (getBoard().piece(inBetween1) == null && getBoard().piece(inBetween2) == null) {
                    movesMap[position.getRow()][position.getColumn() + 2] = true;
                }
            }

            if (isACastableRook(leftRook)) {
                Position inBetween1 = new Position(position.getRow(), position.getColumn() - 1);
                Position inBetween2 = new Position(position.getRow(), position.getColumn() - 2);
                Position inBetween3 = new Position(position.getRow(), position.getColumn() - 3);

                if (getBoard().piece(inBetween1) == null && getBoard().piece(inBetween2) == null && getBoard().piece(inBetween3) == null) {
                    movesMap[position.getRow()][position.getColumn() - 2] = true;
                }
            }
        }

        return movesMap;
    }

    private boolean isACastableRook(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);

        return piece instanceof Rook && piece.getMoveCount() == 0;
    }
}
