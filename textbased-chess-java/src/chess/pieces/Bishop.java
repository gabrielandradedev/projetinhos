package chess.pieces;

import board.Board;
import board.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

    public Bishop(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] movesMap = new boolean[getBoard().getRows()][getBoard().getColumns()];

        // North-West
        for (Position pos = new Position(position.getRow() - 1, position.getColumn() - 1);
             getBoard().positionExists(pos);
             pos.setValues(pos.getRow() - 1, pos.getColumn() - 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // North-East
        for (Position pos = new Position(position.getRow() - 1, position.getColumn() + 1);
             getBoard().positionExists(pos);
             pos.setValues(pos.getRow() - 1, pos.getColumn() + 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // South-West
        for (Position pos = new Position(position.getRow() + 1, position.getColumn() - 1);
             getBoard().positionExists(pos);
             pos.setValues(pos.getRow() + 1, pos.getColumn() - 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // South-East
        for (Position pos = new Position(position.getRow() + 1, position.getColumn() + 1);
             getBoard().positionExists(pos);
             pos.setValues(pos.getRow() + 1, pos.getColumn() + 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        return movesMap;
    }
}
