import board.Board;
import board.Position;
import pieces.Piece;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        b.setupClassic();   // make sure Board has this helper
        b.display();

        String turn = "white";
        Scanner sc = new Scanner(System.in);
        System.out.println("Type moves like: E2 E4   |  'board' to reprint  |  'q' to quit");

        while (true) {
            System.out.print("[" + turn + "] move> ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("q")) break;
            if (line.equalsIgnoreCase("board")) { b.display(); continue; }
            if (!line.matches("(?i)^[A-H][1-8]\\s+[A-H][1-8]$")) { System.out.println("Bad format. Example: E2 E4"); continue; }

            String[] parts = line.split("\\s+");
            Position from = algebraic(parts[0].toUpperCase());
            Position to   = algebraic(parts[1].toUpperCase());

            Piece p = b.getPiece(from);
            if (p == null) { System.out.println("No piece at " + parts[0].toUpperCase()); continue; }

            char side = p.toString().charAt(0);           // 'w' or 'b'
            String colorAtFrom = (side == 'w') ? "white" : "black";
            if (!colorAtFrom.equals(turn)) { System.out.println("It's " + turn + "'s turn."); continue; }

            if (!b.movePiece(from, to)) { System.out.println("Illegal or blocked move."); continue; }

            b.display();
            turn = turn.equals("white") ? "black" : "white";
        }
        System.out.println("Game over.");
    }

    private static Position algebraic(String sq) {
        int col = Character.toUpperCase(sq.charAt(0)) - 'A';
        int row = 8 - (sq.charAt(1) - '0');
        return new Position(row, col);
    }
}
