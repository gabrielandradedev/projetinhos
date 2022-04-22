package application.ui;

import chess.ChessPosition;
import chess.Promotion;
import chess.exceptions.ChessException;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Reader {

    public static ChessPosition readChessPosition(Scanner scanner) {
        try {
            String line = scanner.nextLine();

            char column = line.charAt(0);
            int row = Integer.parseInt(line.substring(1, 2));

            return new ChessPosition(column, row);
        } catch (RuntimeException e) {
            throw new InputMismatchException("Error reading ChessPosition. Valid values are from a1 to h8.");
        }
    }

    public static Promotion readPromotion(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            String letter = line.substring(0, 1);

            return Promotion.entryOf(letter);
        } catch (IllegalArgumentException e) {
            System.out.print("Invalid letter! Assuming default: Queen.");

            return Promotion.QUEEN;
        }
    }
}
