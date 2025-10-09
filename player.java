import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class player {
    private String color;           // "W" or "B"
    private List<piece> pieces;     // Pieces still on the board

    public player(String color) {
        this.color = color.toUpperCase();
        this.pieces = new ArrayList<>();
    }

    public String getColor() {
        return color;
    }

    public List<piece> getPieces() {
        return pieces;
    }

    public void addPiece(piece p) {
        pieces.add(p);
    }

    public void removePiece(piece p) {
        pieces.remove(p);
    }

    /**
     * Prompts the player to input a move in standard format (e.g., "E2 E4")
     * and attempts to execute it on the board.
     */
    public void makeMove(board gameBoard) {
        Scanner sc = new Scanner(System.in);
        boolean validMove = false;

        while (!validMove) {
            System.out.print(color.equals("W") ? "White" : "Black");
            System.out.print(", enter your move (e.g., E2 E4): ");
            String input = sc.nextLine().trim();

            if (input.length() != 5 || input.charAt(2) != ' ') {
                System.out.println("Invalid input format. Use format like 'E2 E4'.");
                continue;
            }

            String from = input.substring(0, 2).toUpperCase();
String to = input.substring(3, 5).toUpperCase();

int startCol = from.charAt(0) - 'A';                  // 'A' -> 0, 'E' -> 4
int startRow = 8 - Character.getNumericValue(from.charAt(1)); // '2' -> 6
int endCol = to.charAt(0) - 'A';
int endRow = 8 - Character.getNumericValue(to.charAt(1));

            // Make sure the coordinates are on the board
            if (startRow < 0 || startRow > 7 || startCol < 0 || startCol > 7 ||
                endRow < 0 || endRow > 7 || endCol < 0 || endCol > 7) {
                System.out.println("Move is out of bounds. Try again.");
                continue;
            }

            piece selected = gameBoard.getPieceAt(startRow, startCol);

            // Check that the player is moving their own piece
            if (selected == null) {
                System.out.println("No piece at the starting square.");
                continue;
            }
            if (!selected.getColor().equals(color)) {
                System.out.println("You can only move your own pieces.");
                continue;
            }

            // Attempt the move using board's makeMove()
            if (gameBoard.makeMove(startRow, startCol, endRow, endCol)) {
                validMove = true; // move succeeded
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }
}
