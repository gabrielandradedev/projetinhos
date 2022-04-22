package chess.pieces;

import board.Board;
import board.Position;
import chess.ChessPiece;
import chess.Color;

public class Rook extends ChessPiece {

    public Rook(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "R";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] movesMap = new boolean[getBoard().getRows()][getBoard().getColumns()];

        // North
        for (Position pos = new Position(position.getRow() - 1, position.getColumn());
             getBoard().positionExists(pos);
             pos.setRow(pos.getRow() - 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // East
        for (Position pos = new Position(position.getRow(), position.getColumn() + 1);
             getBoard().positionExists(pos);
             pos.setColumn(pos.getColumn() + 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // South
        for (Position pos = new Position(position.getRow() + 1, position.getColumn());
             getBoard().positionExists(pos);
             pos.setRow(pos.getRow() + 1)) {

            if (getBoard().isThereAPiece(pos)) {
                if (isThereOpponentPiece(pos)) {
                    movesMap[pos.getRow()][pos.getColumn()] = true;
                }
                break;
            }

            movesMap[pos.getRow()][pos.getColumn()] = true;
        }

        // West
        for (Position pos = new Position(position.getRow(), position.getColumn() - 1);
             getBoard().positionExists(pos);
             pos.setColumn(pos.getColumn() - 1)) {

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
