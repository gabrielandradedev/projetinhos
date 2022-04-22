package application.ui;

import board.Piece;
import chess.*;
import chess.exceptions.ChessException;

import java.lang.reflect.InvocationTargetException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Renderer {

    private static Scanner scanner = new Scanner(System.in);

    public static void render(ChessMatch match) {
        while (!match.testCheckMate(match.getCurrentPlayer())) {
            try {
                clearScreen();
                printMatch(match);

                System.out.print("\nSource: ");
                ChessPosition source = Reader.readChessPosition(scanner);

                clearScreen();
                printBoard(match.getPieces(), match.possibleMoves(source));

                System.out.print("\nTarget: ");
                ChessPosition target = Reader.readChessPosition(scanner);

                match.performChessMove(source, target);

                if (match.getPromotable().isPresent()) {
                    System.out.print("Enter piece for promotion (B/N/R/Q): ");
                    Promotion promotion = Reader.readPromotion(scanner);

                    try {
                        match.replacePromotedPiece(promotion);
                    } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                        throw new IllegalStateException("Failed to instantiate promotion!");
                    }
                }
            } catch (ChessException | InputMismatchException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }

        clearScreen();
        printMatch(match);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printMatch(ChessMatch match) {
        printBoard(match.getPieces());
        printCapturedPieces(match.getCapturedPieces());

        if (!match.testCheckMate(match.getCurrentPlayer())) {
            printTurn(match.getTurn());
            printWaitingPlayer(match.getCurrentPlayer());
            printCheck(match);
        } else {
            printTurn(match.getTurn() - 1);
            printCheckmate(match);
        }
    }

    public static void printBoard(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");

            for (int j = 0; j < pieces[i].length; j++) {
                printPiece(pieces[i][j], false);
            }

            System.out.println();
        }

        System.out.println("  a b c d e f g h");
    }

    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");

            for (int j = 0; j < pieces[i].length; j++) {
                printPiece(pieces[i][j], possibleMoves[i][j]);
            }

            System.out.println();
        }

        System.out.println("  a b c d e f g h");
    }

    private static void printPiece(ChessPiece piece, boolean withBackground) {
        if (withBackground) {
            System.out.print(Colors.ANSI_YELLOW_BACKGROUND);
        }

        if (piece == null) {
            System.out.print("-" + Colors.ANSI_RESET);
        } else if (piece.getColor().equals(Color.WHITE)) {
            System.out.print(Colors.ANSI_BLUE + piece + Colors.ANSI_RESET);
        } else {
            System.out.print(Colors.ANSI_RED + piece + Colors.ANSI_RESET);
        }

        System.out.print(" ");
    }

    private static void printCapturedPieces(List<Piece> captured) {
        List<ChessPiece> parsed = captured.stream()
                .map(piece -> (ChessPiece) piece)
                .collect(Collectors.toList());

        List<ChessPiece> white = parsed.stream()
                .filter(piece -> piece.getColor() == Color.WHITE)
                .collect(Collectors.toList());

        List<ChessPiece> black = parsed.stream()
                .filter(piece -> piece.getColor() == Color.BLACK)
                .collect(Collectors.toList());

        System.out.println("\nCaptured pieces:");

        System.out.print("White: ");
        System.out.print(Colors.ANSI_BLUE);
        System.out.println(white);

        System.out.print(Colors.ANSI_RESET);

        System.out.print("Black: ");
        System.out.print(Colors.ANSI_RED);
        System.out.println(black);

        System.out.print(Colors.ANSI_RESET);
    }

    private static void printTurn(int turn) {
        System.out.println("\nTurn: " + turn);
    }

    private static void printWaitingPlayer(Color currentPlayer) {
        System.out.print("Waiting player: ");

        if (currentPlayer == Color.WHITE) {
            System.out.print(Colors.ANSI_BLUE);
        } else {
            System.out.print(Colors.ANSI_RED);
        }

        System.out.println(currentPlayer);

        System.out.print(Colors.ANSI_RESET);
    }

    private static void printCheck(ChessMatch match) {
        if (match.testCheck(match.getCurrentPlayer())) {
            System.out.print("CHECK!");
        }
    }

    private static void printCheckmate(ChessMatch match) {
        System.out.println("CHECKMATE!");
        System.out.print("Winner: ");

        Color winner = match.opponent(match.getCurrentPlayer());

        if (winner == Color.WHITE) {
            System.out.println(Colors.ANSI_BLUE + "WHITE" + Colors.ANSI_RESET + "!");
        } else {
            System.out.println(Colors.ANSI_RED + "BLACK" + Colors.ANSI_RESET + "!");
        }
    }
}
