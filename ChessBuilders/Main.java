package ChessBuilders;

import ChessBuilders.gui.ChessGame;
import javax.swing.SwingUtilities;

/**
 * Entry point for the chess program.
 * Launches the GUI chess game.
 */
public class Main {

    /**
     * Launches the chess game GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChessGame();
            }
        });
    }
}