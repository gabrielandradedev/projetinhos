package chess;

import board.Board;
import board.Piece;
import board.Position;
import chess.exceptions.ChessException;
import chess.pieces.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;

    private Board board;

    private Color currentPlayer;

    private List<Piece> remainingPieces;

    private List<Piece> capturedPieces;

    public ChessMatch() {
        this.turn = 1;
        this.board = new Board(8, 8);
        this.currentPlayer = Color.WHITE;
        this.remainingPieces = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();

        initialSetup();
    }

    public int getTurn() {
        return this.turn;
    }

    public Color getCurrentPlayer() {
        return this.currentPlayer;
    }

    public List<Piece> getRemainingPieces() {
        return this.remainingPieces;
    }

    public List<Piece> getCapturedPieces() {
        return this.capturedPieces;
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] pieces = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                pieces[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return pieces;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();

        validateSourcePosition(position);

        return board.piece(position).possibleMoves();
    }

    public void performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();

        validateSourcePosition(source);
        validateTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target);
        Piece movingPiece = board.piece(target);

        /* Promotion */
        if (movingPiece instanceof Pawn) {
            Pawn movingPawn = (Pawn) movingPiece;

            if ((movingPawn.getColor() == Color.WHITE && target.getRow() == 0) || (movingPawn.getColor() == Color.BLACK && target.getRow() == 7)) {
                movingPawn.setPromotable(true);
            }
        }

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);

            throw new ChessException("You can't put yourself in check!");
        }

        /* En Passant */
        if (movingPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
            ((Pawn) movingPiece).setEnPassantVulnerable(true);
        } else {
            removeEnPassantVulnerability();
        }

        nextTurn();
    }

    public Optional<Pawn> getPromotable() {
        return remainingPieces.stream()
                .filter(piece -> piece instanceof Pawn)
                .map(Pawn.class::cast)
                .filter(Pawn::isPromotable)
                .findFirst();
    }

    public void replacePromotedPiece(Promotion promotion) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Optional<Pawn> promotable = getPromotable();

        if (promotable.isEmpty()) {
            throw new IllegalStateException("There is no piece to be promoted!");
        }

        Pawn pawn = promotable.get();
        Position position = pawn.getChessPosition().toPosition();
        Piece old = board.removePiece(position);

        remainingPieces.remove(old);
        board.placePiece(Promotion.getInstance(promotion, board, pawn.getColor()), position);
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece movingPiece = (ChessPiece) board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);

        movingPiece.increaseMoveCount();
        board.placePiece(movingPiece, target);

        if (capturedPiece != null) {
            remainingPieces.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        /* Castling */
        if (movingPiece instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position rookSource = new Position(source.getRow(), source.getColumn() + 3);
            Position rookTarget = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rightRook = (ChessPiece) board.removePiece(rookSource);

            board.placePiece(rightRook, rookTarget);
            rightRook.increaseMoveCount();
        }

        if (movingPiece instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position rookSource = new Position(source.getRow(), source.getColumn() - 4);
            Position rookTarget = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece leftRook = (ChessPiece) board.removePiece(rookSource);

            board.placePiece(leftRook, rookTarget);
            leftRook.increaseMoveCount();
        }

        /* En Passant */
        if (movingPiece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece == null) {
            if (movingPiece.getColor() == Color.WHITE) {
                capturedPiece = board.removePiece(new Position(target.getRow() + 1, target.getColumn()));
            } else {
                capturedPiece = board.removePiece(new Position(target.getRow() - 1, target.getColumn()));
            }

            remainingPieces.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece returningPiece = (ChessPiece) board.removePiece(target);

        returningPiece.decreaseMoveCount();
        board.placePiece(returningPiece, source);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);

            capturedPieces.remove(capturedPiece);
            remainingPieces.add(capturedPiece);
        }

        /* Castling */
        if (returningPiece instanceof King && target.getColumn() == source.getColumn() + 2) {
            Position rookSource = new Position(source.getRow(), source.getColumn() + 3);
            Position rookTarget = new Position(source.getRow(), source.getColumn() + 1);
            ChessPiece rightRook = (ChessPiece) board.removePiece(rookTarget);

            board.placePiece(rightRook, rookSource);
            rightRook.decreaseMoveCount();
        }

        if (returningPiece instanceof King && target.getColumn() == source.getColumn() - 2) {
            Position rookSource = new Position(source.getRow(), source.getColumn() - 4);
            Position rookTarget = new Position(source.getRow(), source.getColumn() - 1);
            ChessPiece leftRook = (ChessPiece) board.removePiece(rookTarget);

            board.placePiece(leftRook, rookSource);
            leftRook.decreaseMoveCount();
        }

        /* En Passant */
        if (returningPiece instanceof Pawn && source.getColumn() != target.getColumn() && capturedPiece != null && ((Pawn) capturedPiece).isEnPassantVulnerable()) {
            ChessPiece pawn = (ChessPiece) board.removePiece(target);

            if (returningPiece.getColor() == Color.WHITE) {
                board.placePiece(pawn, new Position(target.getRow() + 1, target.getColumn()));
            } else {
                board.placePiece(pawn, new Position(target.getRow() - 1, target.getColumn()));
            }
        }

        /* Promotion */
        if (returningPiece instanceof Pawn) {
            ((Pawn) returningPiece).setPromotable(false);
        }
    }

    private void removeEnPassantVulnerability() {
        remainingPieces.stream()
                .filter(piece -> piece instanceof Pawn)
                .map(Pawn.class::cast)
                .forEach(pawn -> pawn.setEnPassantVulnerable(false));
    }

    private void validateSourcePosition(Position position) {
        if (!board.isThereAPiece(position)) {
            throw new ChessException("There is no piece on source position.");
        }

        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours.");
        }

        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece.");
        }
    }

    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The chosen piece can't move to target position.");
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = opponent(currentPlayer);
    }

    public Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        Optional<ChessPiece> king = remainingPieces.stream()
                .map(ChessPiece.class::cast)
                .filter(piece -> piece.getColor() == color)
                .filter(piece -> piece instanceof King)
                .findAny();

        if (king.isEmpty()) {
            throw new IllegalStateException("There is no " + color + " king on the board.");
        }

        return king.get();
    }

    public boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();

        Optional<Piece> checker = remainingPieces.stream()
                .filter(piece -> ((ChessPiece) piece).getColor() == opponent(color))
                .filter(piece -> !(piece instanceof King))
                .filter(piece -> piece.possibleMove(kingPosition))
                .findAny();

        return checker.isPresent();
    }

    public boolean testCheckMate(Color color) {
        if (!testCheck(color)) return false;

        List<ChessPiece> allies = remainingPieces.stream()
                .map(ChessPiece.class::cast)
                .filter(piece -> piece.getColor() == color)
                .collect(Collectors.toList());

        for (ChessPiece ally : allies) {
            List<Position> possibleMoves = ally.possibleMovesList();

            for (Position target : possibleMoves) {
                Position source = ally.getChessPosition().toPosition();
                Piece capturedPiece = makeMove(source, target);
                boolean isCheck = testCheck(color);
                undoMove(source, target, capturedPiece);

                if (!isCheck) return false;
            }
        }

        return true;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());

        remainingPieces.add(piece);
    }

    private void initialSetup() {
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));

        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
    }
}
