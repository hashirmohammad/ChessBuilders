import java.io.Serializable;

/**
 * Represents a chess piece with a type and color.
 */
public class ChessPiece implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String type;  // "pawn", "rook", "knight", "bishop", "queen", "king"
    private String color; // "white" or "black"
    
    /**
     * Constructor for ChessPiece.
     * @param type The type of the piece
     * @param color The color of the piece
     */
    public ChessPiece(String type, String color) {
        this.type = type;
        this.color = color;
    }
    
    public String getType() {
        return type;
    }
    
    public String getColor() {
        return color;
    }
    
    /**
     * Returns the Unicode symbol for this chess piece.
     * @return Unicode character representing the piece
     */
    public String getSymbol() {
        if (color.equals("white")) {
            switch (type) {
                case "king": return "♔";
                case "queen": return "♕";
                case "rook": return "♖";
                case "bishop": return "♗";
                case "knight": return "♘";
                case "pawn": return "♙";
            }
        } else {
            switch (type) {
                case "king": return "♚";
                case "queen": return "♛";
                case "rook": return "♜";
                case "bishop": return "♝";
                case "knight": return "♞";
                case "pawn": return "♟";
            }
        }
        return "";
    }
}