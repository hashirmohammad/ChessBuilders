package ChessBuilders.game;
import ChessBuilders.board.Board;
import ChessBuilders.board.Position;
import ChessBuilders.pieces.Piece;
import java.util.Scanner;

/**
 * Controls the flow of a chess game.
 */
public class Game {

    /** The chess board. */
    private final Board board;

    /** The white player. */
    private final Player white;

    /** The black player. */
    private final Player black;

    /** Current turn ("white" or "black"). */
    private String currentTurn;

    public Game(Board board, Player white, Player black) {
        this.board = board;
        this.white = white;
        this.black = black;
        this.currentTurn = "white";
    }

    /** Starts the game by displaying the board. */
    public void start() {
        board.display();
    }

    /** Ends the game. */
    public void end() {
        System.out.println("Game over.");
    }

    /** Runs the interactive CLI loop (same behavior you had in Main). */
  /** Runs the interactive CLI loop (same behavior you had in Main). */
public void runCli() {
    Scanner sc = new Scanner(System.in);
    System.out.println("Type moves like: E2 E4   |  'board' to reprint  |  'q' to quit");
    while (true) {
        System.out.print("[" + currentTurn + "] move> ");
        String line = sc.nextLine().trim();
        if (line.equalsIgnoreCase("q")) break;
        if (line.equalsIgnoreCase("board")) { board.display(); continue; }
        if (!line.matches("(?i)^[A-H][1-8]\\s+[A-H][1-8]$")) {
            System.out.println("Bad format. Example: E2 E4");
            continue;
        }
        String[] parts = line.split("\\s+");
        Position from = algebraic(parts[0].toUpperCase());
        Position to   = algebraic(parts[1].toUpperCase());
        Piece p = board.getPiece(from);
        if (p == null) { System.out.println("No piece at " + parts[0].toUpperCase()); continue; }
        // Use the getter instead of toString parsing
        String colorAtFrom = p.getColor(); // "white" or "black"
        if (!colorAtFrom.equals(currentTurn)) {
            System.out.println("It's " + currentTurn + "'s turn.");
            continue;
        }
        if (!board.movePiece(from, to)) {
            System.out.println("Illegal or blocked move.");
            continue;
        }
        board.display();
        
        //Check for check/checkmate after the move
        Board.Color nextColor = currentTurn.equals("white") ? Board.Color.BLACK : Board.Color.WHITE;
        if (board.isCheckmate(nextColor)) {
            System.out.println("CHECKMATE! " + currentTurn + " wins!");
            break;
        } else if (board.isCheck(nextColor)) {
            System.out.println("CHECK!");
        }
        
        
        currentTurn = currentTurn.equals("white") ? "black" : "white";
    }
    end();
}

    /** Algebraic like "E2" -> Position(row, col). */
    private static Position algebraic(String sq) {
        int col = Character.toUpperCase(sq.charAt(0)) - 'A';
        int row = 8 - (sq.charAt(1) - '0');
        return new Position(row, col);
    }
}
