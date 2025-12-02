package ChessBuilders.gui;

import ChessBuilders.board.Board;
import ChessBuilders.board.Position;
import ChessBuilders.pieces.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The "Controller" of the MVC pattern.
 * This class acts as the bridge between the View (ChessBoard) and the Model (Board).
 * It handles:
 * 1. User inputs (Move requests)
 * 2. Rule enforcement (calls backend)
 * 3. Game State (Turns, History, Undo, Save/Load)
 */
public class GameController {

    private final ChessBoard view; // Reference to the GUI
    private Board backendBoard;    // Reference to the Logic
    
    // --- Game State Data ---
    private String currentTurn = "white";
    
    // Text log of moves (e.g. "Pawn e2 -> e4") for the UI sidebar
    private final List<String> moveHistory = new ArrayList<>();
    
    // Lists of pieces captured by each side
    private final List<ChessPiece> whiteCaptured = new ArrayList<>();
    private final List<ChessPiece> blackCaptured = new ArrayList<>();
    
    // The "Stack" of previous states used for the Undo button
    private final List<GameState> gameHistory = new ArrayList<>();

    public GameController(ChessBoard view) {
        this.view = view;
        // Note: We do NOT call startNewGame() here to avoid circular dependency issues.
        // ChessBoard calls it manually after initialization.
    }

    /**
     * Resets the board, history, and UI for a fresh game.
     */
    public void startNewGame() {
        backendBoard = new Board();
        backendBoard.setupClassic();
        
        moveHistory.clear();
        whiteCaptured.clear();
        blackCaptured.clear();
        gameHistory.clear();
        currentTurn = "white";
        
        view.resetSelection();
        view.updateBoardFromBackend(backendBoard);
        updateUI();
    }
    
    /**
     * Helper to force the View to repaint the side panels (History/Captures).
     */
    public void refreshUI() {
        updateUI();
    }

    // ==========================================
    //           Core Game Logic
    // ==========================================

    /**
     * Attempts to process a move requested by the UI (InputHandler).
     * @param fromRow Source row
     * @param fromCol Source column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void processMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        Piece movingPiece = backendBoard.getPiece(from);
        Piece capturedPiece = backendBoard.getPiece(to);

        if (movingPiece == null) return;

        // 1. CRITICAL: Save current state to history BEFORE making the move
        // This ensures "Undo" goes back to exactly how the board looks right now.
        saveStateToHistory();

        // 2. Attempt move on backend (Validates rules, path, and check)
        boolean success = backendBoard.movePiece(from, to);
        if (!success) {
            JOptionPane.showMessageDialog(view, "Illegal move!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            // If move failed, we don't switch turns or update UI
            return;
        }

        // 3. Handle Pawn Promotion (Backend auto-promotes to Queen, we just notify user)
        Piece finalPiece = backendBoard.getPiece(to);
        if (movingPiece instanceof Pawn && !(finalPiece instanceof Pawn)) {
            JOptionPane.showMessageDialog(view, "Pawn Promoted!", "Promotion", JOptionPane.INFORMATION_MESSAGE);
        }

        // 4. Update Captured List for UI
        if (capturedPiece != null) {
            // Create a simple UI piece representation for the side panel
            ChessPiece guiPiece = new ChessPiece(capturedPiece.getClass().getSimpleName(), capturedPiece.getColor());
            if ("white".equals(movingPiece.getColor())) whiteCaptured.add(guiPiece);
            else blackCaptured.add(guiPiece);
        }

        // 5. Record Move String for History Sidebar
        String moveStr = movingPiece.getClass().getSimpleName() + " " + 
                         (char)('a' + fromCol) + (8 - fromRow) + " -> " + 
                         (char)('a' + toCol) + (8 - toRow);
        moveHistory.add(moveStr);

        // 6. Switch Turn
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        
        // 7. Sync Frontend with Backend
        view.updateBoardFromBackend(backendBoard);
        updateUI();

        // 8. Check for End Game Conditions
        checkGameStatus();
    }

    /**
     * Checks if the move resulted in Check or Checkmate.
     */
    private void checkGameStatus() {
        Board.Color oppColor = currentTurn.equals("white") ? Board.Color.WHITE : Board.Color.BLACK;
        
        if (backendBoard.isCheckmate(oppColor)) {
            JOptionPane.showMessageDialog(view, "CHECKMATE! " + (currentTurn.equals("white") ? "Black" : "White") + " wins!");
        } else if (backendBoard.isCheck(oppColor)) {
            JOptionPane.showMessageDialog(view, "CHECK!", "Check", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Reverts the game to the previous state.
     */
    public void undoMove() {
        if (gameHistory.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No moves to undo.");
            return;
        }
        // Pop the last state off the stack and restore it
        restoreState(gameHistory.remove(gameHistory.size() - 1));
        view.updateBoardFromBackend(backendBoard);
        updateUI();
    }

    // ==========================================
    //           Save / Load System
    // ==========================================

    public void saveGame() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()))) {
                
                // We create a "Package" list containing:
                // Index 0: The Current State (Board positions, turn, etc.)
                // Index 1: The Undo History (So you can still undo after loading)
                List<Object> savePackage = new ArrayList<>();
                savePackage.add(createCurrentState()); 
                savePackage.add(gameHistory);          
                
                out.writeObject(savePackage);
                JOptionPane.showMessageDialog(view, "Game Saved.");
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error saving file: " + e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void loadGame() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()))) {
                
                Object loadedObj = in.readObject();
                
                // Compatibility Check:
                // Is this a new save file (List) or an old one (GameState)?
                if (loadedObj instanceof List) {
                    // New Format: Restore both state and the undo history
                    List<Object> savePackage = (List<Object>) loadedObj;
                    GameState stateToRestore = (GameState) savePackage.get(0);
                    List<GameState> historyToRestore = (List<GameState>) savePackage.get(1);
                    
                    restoreState(stateToRestore);
                    
                    // Restore the undo stack
                    this.gameHistory.clear();
                    this.gameHistory.addAll(historyToRestore);
                    
                } else if (loadedObj instanceof GameState) {
                    // Old Format: Only restore current state
                    restoreState((GameState) loadedObj);
                    this.gameHistory.clear(); // Clear history to prevent glitches
                }

                view.updateBoardFromBackend(backendBoard);
                updateUI();
                JOptionPane.showMessageDialog(view, "Game Loaded.");
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(view, "Error loading file.");
            }
        }
    }

    // ==========================================
    //           Helper Methods
    // ==========================================

    /**
     * Snapshots the current game and adds it to the undo stack.
     */
    private void saveStateToHistory() {
        gameHistory.add(createCurrentState());
    }

    /**
     * Packs all current data into a Serializable GameState object.
     */
    private GameState createCurrentState() {
        return new GameState(backendBoard, currentTurn, new ArrayList<>(moveHistory),
                new ArrayList<>(whiteCaptured), new ArrayList<>(blackCaptured),
                view.getLightColor(), view.getDarkColor());
    }

    /**
     * Unpacks a GameState object and applies it to the running application.
     */
    private void restoreState(GameState state) {
        // 1. Rebuild the Backend Board
        backendBoard = new Board();
        
        // We must convert the serialized "PieceData" back into strings for the backend setup
        GameState.SerializablePieceData[][] data = state.getBoardState();
        String[][] strData = new String[8][8];
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                if(data[r][c] != null) strData[r][c] = data[r][c].type + "," + data[r][c].color;
            }
        }
        backendBoard.setupFromSave(strData);

        // 2. Restore Variables
        this.currentTurn = state.getCurrentTurn();
        
        // 3. Restore Lists (We must clear first, then addAll)
        this.moveHistory.clear();
        this.moveHistory.addAll(state.getMoveHistory());
        
        this.whiteCaptured.clear();
        this.whiteCaptured.addAll(state.restoreWhiteCaptured());
        
        this.blackCaptured.clear();
        this.blackCaptured.addAll(state.restoreBlackCaptured());
        
        // 4. Restore Visual Theme
        view.setColors(state.getLightSquare(), state.getDarkSquare());
    }

    /**
     * Pushes data updates to the View components.
     */
    private void updateUI() {
        view.updateHistory(moveHistory);
        view.updateCaptured(whiteCaptured, blackCaptured);
        view.updateTurnLabel(currentTurn);
    }

    // Getters used by InputHandler
    public String getCurrentTurn() { return currentTurn; }
    public Piece getPieceAt(int r, int c) { return backendBoard.getPiece(new Position(r, c)); }
}