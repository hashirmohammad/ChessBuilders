// GameState.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

/**
 * Represents the state of the chess game for saving/loading and undo functionality.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ChessPiece[][] board;
    private String currentTurn;
    private List<String> moveHistory;
    private List<ChessPiece> whiteCaptured;
    private List<ChessPiece> blackCaptured;
    private Color lightSquare;
    private Color darkSquare;
    
    /**
     * Constructor for GameState.
     * @param board The current board state
     * @param currentTurn The current player's turn
     * @param moveHistory List of moves made
     * @param whiteCaptured Pieces captured by white
     * @param blackCaptured Pieces captured by black
     * @param lightSquare Light square color
     * @param darkSquare Dark square color
     */
    public GameState(ChessPiece[][] board, String currentTurn, 
                     List<String> moveHistory, List<ChessPiece> whiteCaptured, 
                     List<ChessPiece> blackCaptured, Color lightSquare, Color darkSquare) {
        this.board = deepCopyBoard(board);
        this.currentTurn = currentTurn;
        this.moveHistory = new ArrayList<>(moveHistory);
        this.whiteCaptured = new ArrayList<>(whiteCaptured);
        this.blackCaptured = new ArrayList<>(blackCaptured);
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
    }
    
    private ChessPiece[][] deepCopyBoard(ChessPiece[][] original) {
        ChessPiece[][] copy = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (original[i][j] != null) {
                    copy[i][j] = new ChessPiece(original[i][j].getType(), 
                                                original[i][j].getColor());
                }
            }
        }
        return copy;
    }
    
    public ChessPiece[][] getBoard() { return board; }
    public String getCurrentTurn() { return currentTurn; }
    public List<String> getMoveHistory() { return moveHistory; }
    public List<ChessPiece> getWhiteCaptured() { return whiteCaptured; }
    public List<ChessPiece> getBlackCaptured() { return blackCaptured; }
    public Color getLightSquare() { return lightSquare; }
    public Color getDarkSquare() { return darkSquare; }
}