package application;

import application.ui.Renderer;
import chess.ChessMatch;

public class Program {

    public static void main(String[] args) {
        Renderer.render(new ChessMatch());
    }
}
