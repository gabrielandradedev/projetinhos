package chess.pieces;

import board.Board;
import board.Piece;
import board.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

    private boolean enPassantVulnerable;

    private boolean promotable;

    private ChessMatch match;

    public Pawn(Board board, Color color, ChessMatch match) {
        super(board, color);
        this.match = match;
    }

    public boolean isEnPassantVulnerable() {
        return this.enPassantVulnerable;
    }

    public void setEnPassantVulnerable(boolean enPassantVulnerable) {
        this.enPassantVulnerable = enPassantVulnerable;
    }

    public boolean isPromotable() {
        return this.promotable;
    }

    public void setPromotable(boolean promotable) {
        this.promotable = promotable;
    }

    @Override
    public String toString() {
        return "P";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] movesMap = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position front, frontPlus, diagonalLeft, diagonalRight, left, right;

        if (getColor() == Color.WHITE) {
            front = new Position(position.getRow() - 1, position.getColumn());
            frontPlus = new Position(position.getRow() - 2, position.getColumn());
            diagonalLeft = new Position(position.getRow() - 1, position.getColumn() - 1);
            diagonalRight = new Position(position.getRow() - 1, position.getColumn() + 1);
            left = new Position(position.getRow(), position.getColumn() - 1);
            right = new Position(position.getRow(), position.getColumn() + 1);
        } else {
            front = new Position(position.getRow() + 1, position.getColumn());
            frontPlus = new Position(position.getRow() + 2, position.getColumn());
            diagonalLeft = new Position(position.getRow() + 1, position.getColumn() + 1);
            diagonalRight = new Position(position.getRow() + 1, position.getColumn() - 1);
            left = new Position(position.getRow(), position.getColumn() + 1);
            right = new Position(position.getRow(), position.getColumn() - 1);
        }

        if (getBoard().positionExists(front) && !getBoard().isThereAPiece(front)) {
            movesMap[front.getRow()][front.getColumn()] = true;

            if (getMoveCount() == 0 && getBoard().positionExists(frontPlus) && !getBoard().isThereAPiece(frontPlus)) {
                movesMap[frontPlus.getRow()][frontPlus.getColumn()] = true;
            }
        }

        if (getBoard().positionExists(diagonalLeft) && getBoard().isThereAPiece(diagonalLeft)) {
            movesMap[diagonalLeft.getRow()][diagonalLeft.getColumn()] = true;
        }

        if (getBoard().positionExists(diagonalRight) && getBoard().isThereAPiece(diagonalRight)) {
            movesMap[diagonalRight.getRow()][diagonalRight.getColumn()] = true;
        }

        /* En Passant */
        if (getBoard().positionExists(left) && isThereOpponentPiece(left)) {
            Piece oponnent = getBoard().piece(left);

            if (oponnent instanceof Pawn && ((Pawn) oponnent).isEnPassantVulnerable()) {
                movesMap[diagonalLeft.getRow()][diagonalLeft.getColumn()] = true;
            }
        }

        if (getBoard().positionExists(right) && isThereOpponentPiece(right)) {
            Piece oponnent = getBoard().piece(right);

            if (oponnent instanceof Pawn && ((Pawn) oponnent).isEnPassantVulnerable()) {
                movesMap[diagonalRight.getRow()][diagonalRight.getColumn()] = true;
            }
        }

        return movesMap;
    }
}
