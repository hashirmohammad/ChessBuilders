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
 * Manages the game state, business logic, and backend interaction.
 */
public class GameController {

    private final ChessBoard view;
    private Board backendBoard;
    
    // Game State Data
    private String currentTurn = "white";
    private final List<String> moveHistory = new ArrayList<>();
    private final List<ChessPiece> whiteCaptured = new ArrayList<>();
    private final List<ChessPiece> blackCaptured = new ArrayList<>();
    private final List<GameState> gameHistory = new ArrayList<>();

    public GameController(ChessBoard view) {
        this.view = view;
        
    }

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
    
    // Add this method to allow ChessBoard to force a UI refresh
    public void refreshUI() {
        updateUI();
    }

    /**
     * Attempts to process a move requested by the UI.
     */
    public void processMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        Piece movingPiece = backendBoard.getPiece(from);
        Piece capturedPiece = backendBoard.getPiece(to);

        if (movingPiece == null) return;

        // Save state for Undo BEFORE moving
        saveStateToHistory();

        // Attempt move on backend
        boolean success = backendBoard.movePiece(from, to);
        if (!success) {
            JOptionPane.showMessageDialog(view, "Illegal move!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Handle Pawn Promotion Message
        Piece finalPiece = backendBoard.getPiece(to);
        if (movingPiece instanceof Pawn && !(finalPiece instanceof Pawn)) {
            JOptionPane.showMessageDialog(view, "Pawn Promoted!", "Promotion", JOptionPane.INFORMATION_MESSAGE);
        }

        // Update Captured List
        if (capturedPiece != null) {
            ChessPiece guiPiece = new ChessPiece(capturedPiece.getClass().getSimpleName(), capturedPiece.getColor());
            if ("white".equals(movingPiece.getColor())) whiteCaptured.add(guiPiece);
            else blackCaptured.add(guiPiece);
        }

        // Record Move String
        String moveStr = movingPiece.getClass().getSimpleName() + " " + 
                         (char)('a' + fromCol) + (8 - fromRow) + " -> " + 
                         (char)('a' + toCol) + (8 - toRow);
        moveHistory.add(moveStr);

        // Switch Turn
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        
        // Update View
        view.updateBoardFromBackend(backendBoard);
        updateUI();

        // Check Win Condition
        checkGameStatus();
    }

    private void checkGameStatus() {
        Board.Color oppColor = currentTurn.equals("white") ? Board.Color.WHITE : Board.Color.BLACK;
        if (backendBoard.isCheckmate(oppColor)) {
            JOptionPane.showMessageDialog(view, "CHECKMATE! " + (currentTurn.equals("white") ? "Black" : "White") + " wins!");
        } else if (backendBoard.isCheck(oppColor)) {
            JOptionPane.showMessageDialog(view, "CHECK!", "Check", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void undoMove() {
        if (gameHistory.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No moves to undo.");
            return;
        }
        restoreState(gameHistory.remove(gameHistory.size() - 1));
        view.updateBoardFromBackend(backendBoard);
        updateUI();
    }

    public void saveGame() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fc.getSelectedFile()))) {
                
                // FIX: We create a list containing both the Current State AND the History
                List<Object> savePackage = new ArrayList<>();
                savePackage.add(createCurrentState()); // Index 0: Current State
                savePackage.add(gameHistory);          // Index 1: The Undo Stack
                
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
                
                // Check if this is a "New Style" save (List) or "Old Style" (Single Object)
                if (loadedObj instanceof List) {
                    // NEW FIX: Restore both state and history
                    List<Object> savePackage = (List<Object>) loadedObj;
                    GameState stateToRestore = (GameState) savePackage.get(0);
                    List<GameState> historyToRestore = (List<GameState>) savePackage.get(1);
                    
                    restoreState(stateToRestore);
                    
                    // Restore the undo stack
                    this.gameHistory.clear();
                    this.gameHistory.addAll(historyToRestore);
                    
                } else if (loadedObj instanceof GameState) {
                    // Fallback for old save files (Undo won't work for pre-save moves here, but game loads)
                    restoreState((GameState) loadedObj);
                    this.gameHistory.clear(); // Clear old history to prevent glitches
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

    // --- Helpers ---

    private void saveStateToHistory() {
        gameHistory.add(createCurrentState());
    }

    private GameState createCurrentState() {
        return new GameState(backendBoard, currentTurn, new ArrayList<>(moveHistory),
                new ArrayList<>(whiteCaptured), new ArrayList<>(blackCaptured),
                view.getLightColor(), view.getDarkColor());
    }

    private void restoreState(GameState state) {
        // Restore Backend
        backendBoard = new Board();
        // Convert serialized data back to strings for setupFromSave
        GameState.SerializablePieceData[][] data = state.getBoardState();
        String[][] strData = new String[8][8];
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                if(data[r][c] != null) strData[r][c] = data[r][c].type + "," + data[r][c].color;
            }
        }
        backendBoard.setupFromSave(strData);

        // Restore Data
        this.currentTurn = state.getCurrentTurn();
        this.moveHistory.clear();
        this.moveHistory.addAll(state.getMoveHistory());
        this.whiteCaptured.clear();
        this.whiteCaptured.addAll(state.restoreWhiteCaptured());
        this.blackCaptured.clear();
        this.blackCaptured.addAll(state.restoreBlackCaptured());
        
        // Restore Colors
        view.setColors(state.getLightSquare(), state.getDarkSquare());
    }

    private void updateUI() {
        view.updateHistory(moveHistory);
        view.updateCaptured(whiteCaptured, blackCaptured);
        view.updateTurnLabel(currentTurn);
    }

    public String getCurrentTurn() { return currentTurn; }
    public Piece getPieceAt(int r, int c) { return backendBoard.getPiece(new Position(r, c)); }
}