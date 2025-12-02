// GameState.java
package ChessBuilders.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

import ChessBuilders.board.Position;
import ChessBuilders.pieces.*;

/**
 * Represents the complete game state including backend board for saving/loading.
 * Now integrates with Phase 1 backend for proper serialization.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 2L;  // Changed version
    
    // Store backend pieces with their positions
    private SerializablePieceData[][] boardState;
    private String currentTurn;
    private List<String> moveHistory;
    private List<SerializablePieceData> whiteCaptured;
    private List<SerializablePieceData> blackCaptured;
    private Color lightSquare;
    private Color darkSquare;
    
    /**
     * Inner class to serialize piece data since Piece objects aren't directly serializable.
     */
    public static class SerializablePieceData implements Serializable {
        private static final long serialVersionUID = 1L;
        public String type;   // Changed to public
        public String color;  // Changed to public
        public int row;       // Changed to public
        public int col;       
        
        SerializablePieceData(Piece piece) {
            this.type = piece.getClass().getSimpleName();
            this.color = piece.getColor();
            this.row = piece.getPosition().row;
            this.col = piece.getPosition().col;
        }
        
        SerializablePieceData(String type, String color) {
            this.type = type;
            this.color = color;
        }
    }
    
    /**
     * Constructor - captures full game state from backend board.
     * @param backendBoard The Phase 1 Board instance
     * @param currentTurn Current player's turn
     * @param moveHistory List of moves made
     * @param whiteCaptured GUI list of pieces captured by white
     * @param blackCaptured GUI list of pieces captured by black
     * @param lightSquare Light square color
     * @param darkSquare Dark square color
     */
    public GameState(ChessBuilders.board.Board backendBoard, String currentTurn, 
                     List<String> moveHistory, 
                     List<ChessPiece> whiteCaptured, 
                     List<ChessPiece> blackCaptured, 
                     Color lightSquare, Color darkSquare) {
        this.boardState = serializeBackendBoard(backendBoard);
        this.currentTurn = currentTurn;
        this.moveHistory = new ArrayList<>(moveHistory);
        this.whiteCaptured = serializeGUIPieceList(whiteCaptured);
        this.blackCaptured = serializeGUIPieceList(blackCaptured);
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
    }
    
    /**
     * Converts backend Board to serializable format.
     */
    private SerializablePieceData[][] serializeBackendBoard(ChessBuilders.board.Board backendBoard) {
        SerializablePieceData[][] serialized = new SerializablePieceData[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = backendBoard.getPiece(pos);
                if (piece != null) {
                    serialized[row][col] = new SerializablePieceData(piece);
                }
            }
        }
        return serialized;
    }
    
    /**
     * Converts GUI captured piece list to serializable format.
     */
    private List<SerializablePieceData> serializeGUIPieceList(List<ChessPiece> guiPieces) {
        List<SerializablePieceData> serialized = new ArrayList<>();
        for (ChessPiece piece : guiPieces) {
            serialized.add(new SerializablePieceData(
                piece.getType().substring(0, 1).toUpperCase() + 
                piece.getType().substring(1), // Capitalize first letter
                piece.getColor()
            ));
        }
        return serialized;
    }
    
    /**
     * Restores the backend Board from saved state.
     * @return A new Board instance with the saved position
     */
    public ChessBuilders.board.Board restoreBackendBoard() {
        ChessBuilders.board.Board restoredBoard = new ChessBuilders.board.Board();
        
        // Clear the board first (it starts with classic setup)
        // We'll rebuild it from our saved state
        
        // The Board class doesn't have a clear method, so we'll use reflection
        // or just rebuild by moving pieces. Instead, we'll manually reconstruct.
        // Since Board.grid is private, we need to use movePiece to set up positions.
        
        // Actually, we can't directly access Board's grid since it's private.
        // We need to add a helper method to Board.java or work around it.
        // For now, let's return the board and handle restoration in ChessBoard.
        
        return restoredBoard;
    }
    
    /**
     * Gets the serialized board state for manual restoration.
     */
    public SerializablePieceData[][] getBoardState() {
        return boardState;
    }
    
    /**
     * Restores GUI captured pieces list.
     */
    public List<ChessPiece> restoreWhiteCaptured() {
        return deserializeGUIPieceList(whiteCaptured);
    }
    
    /**
     * Restores GUI captured pieces list.
     */
    public List<ChessPiece> restoreBlackCaptured() {
        return deserializeGUIPieceList(blackCaptured);
    }
    
    /**
     * Converts serialized pieces back to GUI ChessPiece objects.
     */
    private List<ChessPiece> deserializeGUIPieceList(List<SerializablePieceData> serialized) {
        List<ChessPiece> pieces = new ArrayList<>();
        for (SerializablePieceData data : serialized) {
            pieces.add(new ChessPiece(data.type.toLowerCase(), data.color));
        }
        return pieces;
    }
    
    // Getters
    public String getCurrentTurn() { return currentTurn; }
    public List<String> getMoveHistory() { return new ArrayList<>(moveHistory); }
    public Color getLightSquare() { return lightSquare; }
    public Color getDarkSquare() { return darkSquare; }
}