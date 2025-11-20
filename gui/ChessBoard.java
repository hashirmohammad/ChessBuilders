package ChessBuilders.gui;

// ChessBoard.java
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Phase 1 backend imports

import ChessBuilders.board.Board;
import ChessBuilders.board.Position;
import ChessBuilders.pieces.*;

/**
 * Main chess board GUI panel that handles the game logic and display.
 * Integrated with Phase 1 backend for full chess rule enforcement.
 */
public class ChessBoard extends JPanel {
    private static final int SQUARE_SIZE = 70;
    
    // Backend board with all game logic from Phase 1
    private Board backendBoard;
    
    // GUI display structures
    private ChessPiece[][] board;
    private JButton[][] squares;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private String currentTurn = "white";
    private List<String> moveHistory;
    private List<ChessPiece> whiteCaptured;
    private List<ChessPiece> blackCaptured;
    private List<GameState> gameHistory;
    
    // Color scheme
    private Color lightSquare = new Color(240, 217, 181);
    private Color darkSquare = new Color(181, 136, 99);
    private Color selectedColor = new Color(127, 166, 80);
    
    // UI components
    private JTextArea historyArea;
    private JPanel capturedWhitePanel;
    private JPanel capturedBlackPanel;
    private JLabel turnLabel;
    
    /**
     * Constructor for ChessBoard.
     */
    public ChessBoard() {
        // Initialize backend board from Phase 1
        backendBoard = new Board();
        backendBoard.setupClassic();
        
        // Initialize GUI structures
        board = new ChessPiece[8][8];
        squares = new JButton[8][8];
        moveHistory = new ArrayList<>();
        whiteCaptured = new ArrayList<>();
        blackCaptured = new ArrayList<>();
        gameHistory = new ArrayList<>();
        
        setLayout(new GridLayout(8, 8));
        initializeBoard();
        createSquares();
    }
    
    /**
     * Initializes the chess board with pieces in starting positions.
     */
    private void initializeBoard() {
        // Black pieces
        String[] backRow = {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};
        for (int i = 0; i < 8; i++) {
            board[0][i] = new ChessPiece(backRow[i], "black");
            board[1][i] = new ChessPiece("pawn", "black");
        }
        
        // White pieces
        for (int i = 0; i < 8; i++) {
            board[6][i] = new ChessPiece("pawn", "white");
            board[7][i] = new ChessPiece(backRow[i], "white");
        }
    }
    
    /**
     * Creates the GUI squares for the chess board.
     */
    private void createSquares() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();
                square.setFont(new Font("Sans-Serif", Font.PLAIN, 50));
                square.setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                square.setFocusPainted(false);
                square.setOpaque(true);
                square.setBorderPainted(false);
                square.setContentAreaFilled(true);
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                
                // Set square color
                Color squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
                square.setBackground(squareColor);
                
                // Add piece if present
                if (board[row][col] != null) {
                    square.setText(board[row][col].getSymbol());
                }
                
                final int r = row;
                final int c = col;
                
                // Click handler
                square.addActionListener(e -> handleSquareClick(r, c));
                
                // Drag and Drop support
                setupDragAndDrop(square, r, c);
                
                squares[row][col] = square;
                add(square);
            }
        }
    }
    
    /**
     * Sets up drag and drop functionality for a square.
     * @param square The button to set up
     * @param row The row of the square
     * @param col The column of the square
     */
    private void setupDragAndDrop(JButton square, int row, int col) {
        // Drag source
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(square, DnDConstants.ACTION_MOVE,
            new DragGestureListener() {
                public void dragGestureRecognized(DragGestureEvent dge) {
                    Position pos = new Position(row, col);
                    Piece piece = backendBoard.getPiece(pos);
                    
                    if (piece != null && piece.getColor().equals(currentTurn)) {
                        selectedRow = row;
                        selectedCol = col;
                        highlightSquare(row, col, true);
                        
                        Transferable transferable = new StringSelection(row + "," + col);
                        dragSource.startDrag(dge, DragSource.DefaultMoveDrop, transferable, new DragSourceAdapter() {});
                    }
                }
            });
        
        // Drop target
        new DropTarget(square, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    Transferable transferable = dtde.getTransferable();
                    String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    String[] coords = data.split(",");
                    int fromRow = Integer.parseInt(coords[0]);
                    int fromCol = Integer.parseInt(coords[1]);
                    
                    // Check if trying to capture own piece using backend
                    Position fromPos = new Position(fromRow, fromCol);
                    Position toPos = new Position(row, col);
                    Piece fromPiece = backendBoard.getPiece(fromPos);
                    Piece toPiece = backendBoard.getPiece(toPos);
                    
                    if (toPiece != null && toPiece.getColor().equals(fromPiece.getColor())) {
                        highlightSquare(fromRow, fromCol, false);
                        selectedRow = -1;
                        selectedCol = -1;
                        dtde.dropComplete(false);
                        return;
                    }
                    
                    makeMove(fromRow, fromCol, row, col);
                    highlightSquare(fromRow, fromCol, false);
                    selectedRow = -1;
                    selectedCol = -1;
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                }
            }
        });
    }
    
    /**
     * Handles click events on chess board squares.
     * @param row The row of the clicked square
     * @param col The column of the clicked square
     */
    private void handleSquareClick(int row, int col) {
        if (selectedRow == -1) {
            // Select a piece using backend
            Position pos = new Position(row, col);
            Piece piece = backendBoard.getPiece(pos);
            
            if (piece != null && piece.getColor().equals(currentTurn)) {
                selectedRow = row;
                selectedCol = col;
                highlightSquare(row, col, true);
            }
        } else {
            // Try to move the piece
            if (selectedRow == row && selectedCol == col) {
                // Deselect
                highlightSquare(selectedRow, selectedCol, false);
                selectedRow = -1;
                selectedCol = -1;
            } else {
                // Check piece colors using backend
                Position selectedPos = new Position(selectedRow, selectedCol);
                Position targetPos = new Position(row, col);
                Piece selectedPiece = backendBoard.getPiece(selectedPos);
                Piece targetPiece = backendBoard.getPiece(targetPos);
                
                // Check if trying to select a different piece of same color
                if (targetPiece != null && 
                    targetPiece.getColor().equals(selectedPiece.getColor())) {
                    // Select the new piece instead
                    highlightSquare(selectedRow, selectedCol, false);
                    selectedRow = row;
                    selectedCol = col;
                    highlightSquare(row, col, true);
                } else {
                    // Make the move
                    makeMove(selectedRow, selectedCol, row, col);
                    highlightSquare(selectedRow, selectedCol, false);
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
    }
    
    /**
     * Makes a move on the chess board.
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);
        
        Piece movingPiece = backendBoard.getPiece(from);
        Piece capturedPiece = backendBoard.getPiece(to);
        
        // IMPORTANT: Save state BEFORE making the move
        GameState state = new GameState(
            backendBoard,
            currentTurn, 
            moveHistory,
            whiteCaptured, 
            blackCaptured, 
            lightSquare, 
            darkSquare
        );
        
        // Now attempt the move using backend validation
        boolean moveSuccessful = backendBoard.movePiece(from, to);
        
        if (!moveSuccessful) {
            JOptionPane.showMessageDialog(this, 
                "Illegal move! This violates chess rules.",
                "Invalid Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Move succeeded - add state to history
        gameHistory.add(state);
        
        // Update GUI to match backend
        updateGUIFromBackend();
        
        // Track captured pieces in GUI
        if (capturedPiece != null) {
            ChessPiece guiPiece = convertToGUIPiece(capturedPiece);
            if (movingPiece.getColor().equals("white")) {
                whiteCaptured.add(guiPiece);
            } else {
                blackCaptured.add(guiPiece);
            }
            
            // Check for king capture (game over)
            if (capturedPiece instanceof King) {
                declareWinner(movingPiece.getColor());
                return;
            }
        }
        
        // Record move in history
        String move = movingPiece.getColor() + " " + 
                      movingPiece.getClass().getSimpleName() + 
                      " " + (char)('a' + fromCol) + (8 - fromRow) + 
                      " → " + (char)('a' + toCol) + (8 - toRow);
        moveHistory.add(move);
        
        // CHECK FOR CHECK/CHECKMATE using backend
        Board.Color opponentColor = currentTurn.equals("white") ? 
            Board.Color.BLACK : Board.Color.WHITE;
        
        if (backendBoard.isCheckmate(opponentColor)) {
            JOptionPane.showMessageDialog(this, 
                "CHECKMATE! " + currentTurn.toUpperCase() + " wins!",
                "Game Over", 
                JOptionPane.INFORMATION_MESSAGE);
            declareWinner(currentTurn);
            return;
        } else if (backendBoard.isCheck(opponentColor)) {
            JOptionPane.showMessageDialog(this, 
                "CHECK!",
                "Check", 
                JOptionPane.WARNING_MESSAGE);
        }
        
        // Switch turn
        currentTurn = currentTurn.equals("white") ? "black" : "white";
        
        // Update displays
        updateHistoryDisplay();
        updateCapturedDisplay();
        updateTurnLabel();
    }
    
    /**
     * Updates GUI board to match backend board state.
     */
    private void updateGUIFromBackend() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece backendPiece = backendBoard.getPiece(pos);
                
                if (backendPiece == null) {
                    board[row][col] = null;
                    squares[row][col].setText("");
                } else {
                    board[row][col] = convertToGUIPiece(backendPiece);
                    squares[row][col].setText(getUnicodeSymbol(backendPiece));
                }
            }
        }
    }
    
    /**
     * Converts Phase 1 Piece to Phase 2 ChessPiece for GUI display.
     */
    private ChessPiece convertToGUIPiece(Piece backendPiece) {
        String type = backendPiece.getClass().getSimpleName().toLowerCase();
        String color = backendPiece.getColor();
        return new ChessPiece(type, color);
    }
    
    /**
     * Gets Unicode symbol from backend Piece.
     */
    private String getUnicodeSymbol(Piece piece) {
        String className = piece.getClass().getSimpleName().toLowerCase();
        boolean isWhite = piece.getColor().equals("white");
        
        switch (className) {
    case "king":
        return isWhite ? "♔" : "♚";
    case "queen":
        return isWhite ? "♕" : "♛";
    case "rook":
        return isWhite ? "♖" : "♜";
    case "bishop":
        return isWhite ? "♗" : "♝";
    case "knight":
        return isWhite ? "♘" : "♞";
    case "pawn":
        return isWhite ? "♙" : "♟";
    default:
        return "";
}

    }
    
    /**
     * Highlights or unhighlights a square.
     * @param row The row of the square
     * @param col The column of the square
     * @param highlight True to highlight, false to unhighlight
     */
    private void highlightSquare(int row, int col, boolean highlight) {
        if (highlight) {
            squares[row][col].setBackground(selectedColor);
        } else {
            Color squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
            squares[row][col].setBackground(squareColor);
        }
    }
    
    /**
     * Declares the winner and shows a dialog.
     * @param winner The color of the winning player
     */
    private void declareWinner(String winner) {
        String message = winner.toUpperCase() + " WINS!";
        int response = JOptionPane.showConfirmDialog(this, 
            message + "\n\nWould you like to start a new game?",
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            newGame();
        } else {
            System.exit(0);
        }
    }
    
    /**
     * Starts a new game by resetting the board.
     */
    public void newGame() {
        removeAll();
        
        // Reset backend board
        backendBoard = new Board();
        backendBoard.setupClassic();
        
        // Reset GUI structures
        board = new ChessPiece[8][8];
        squares = new JButton[8][8];
        moveHistory.clear();
        whiteCaptured.clear();
        blackCaptured.clear();
        gameHistory.clear();
        currentTurn = "white";
        selectedRow = -1;
        selectedCol = -1;
        
        initializeBoard();
        createSquares();
        revalidate();
        repaint();
        
        updateHistoryDisplay();
        updateCapturedDisplay();
        updateTurnLabel();
    }
    
    /**
     * Saves the current game state to a file.
     */
    public void saveGame() {
        try {
            // Create GameState with backend board
            GameState state = new GameState(
                backendBoard,
                currentTurn, 
                moveHistory,
                whiteCaptured, 
                blackCaptured, 
                lightSquare, 
                darkSquare
            );
            
            FileOutputStream fileOut = new FileOutputStream("chessgame.save");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(state);
            out.close();
            fileOut.close();
            
            JOptionPane.showMessageDialog(this, 
                "Game saved successfully!",
                "Save Game",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving game: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Loads a saved game state from a file.
     */
    public void loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream("chessgame.save");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            GameState state = (GameState) in.readObject();
            in.close();
            fileIn.close();
            
            // Restore backend board from saved state
            restoreBackendFromGameState(state);
            
            // Restore GUI state
            currentTurn = state.getCurrentTurn();
            moveHistory = new ArrayList<>(state.getMoveHistory());
            whiteCaptured = state.restoreWhiteCaptured();
            blackCaptured = state.restoreBlackCaptured();
            lightSquare = state.getLightSquare();
            darkSquare = state.getDarkSquare();
            selectedRow = -1;
            selectedCol = -1;
            
            // Update GUI to match backend
            updateGUIFromBackend();
            
            updateHistoryDisplay();
            updateCapturedDisplay();
            updateTurnLabel();
            
            JOptionPane.showMessageDialog(this, 
                "Game loaded successfully!",
                "Load Game",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading game: " + e.getMessage() + 
                "\nMake sure a saved game exists.",
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading game: Incompatible save file format.",
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Helper method to restore backend board from GameState.
     */
    private void restoreBackendFromGameState(GameState state) {
        // Get the saved board data
        GameState.SerializablePieceData[][] boardData = state.getBoardState();
        
        // Create string array for Board.setupFromSave()
        String[][] pieceData = new String[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (boardData[r][c] != null) {
                    pieceData[r][c] = boardData[r][c].type + "," + boardData[r][c].color;
                }
            }
        }
        
        // Restore the backend board
        backendBoard.setupFromSave(pieceData);
    }
    
    /**
     * Undoes the last move.
     */
    public void undoMove() {
        if (gameHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No moves to undo!",
                "Undo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get previous state
        GameState previousState = gameHistory.remove(gameHistory.size() - 1);
        
        // Restore backend board
        restoreBackendFromGameState(previousState);
        
        // Restore GUI state
        currentTurn = previousState.getCurrentTurn();
        moveHistory = new ArrayList<>(previousState.getMoveHistory());
        whiteCaptured = previousState.restoreWhiteCaptured();
        blackCaptured = previousState.restoreBlackCaptured();
        lightSquare = previousState.getLightSquare();
        darkSquare = previousState.getDarkSquare();
        selectedRow = -1;
        selectedCol = -1;
        
        // Update GUI to match backend
        updateGUIFromBackend();
        
        updateHistoryDisplay();
        updateCapturedDisplay();
        updateTurnLabel();
    }
    
    /**
     * Changes the board color scheme.
     * @param style The color style name
     */
    public void changeBoardStyle(String style) {
        switch (style) {
            case "Classic":
                lightSquare = new Color(240, 217, 181);
                darkSquare = new Color(181, 136, 99);
                break;
            case "Modern Green":
                lightSquare = new Color(238, 238, 210);
                darkSquare = new Color(118, 150, 86);
                break;
            case "Wooden":
                lightSquare = new Color(222, 184, 135);
                darkSquare = new Color(139, 69, 19);
                break;
            case "Ocean Blue":
                lightSquare = new Color(176, 224, 230);
                darkSquare = new Color(70, 130, 180);
                break;
        }
        
        // Update all squares with new colors
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
                squares[row][col].setBackground(squareColor);
            }
        }
        repaint();
    }
    
    // Setter methods for UI components
    public void setHistoryArea(JTextArea area) {
        this.historyArea = area;
        updateHistoryDisplay();
    }
    
    public void setCapturedWhitePanel(JPanel panel) {
        this.capturedWhitePanel = panel;
        updateCapturedDisplay();
    }
    
    public void setCapturedBlackPanel(JPanel panel) {
        this.capturedBlackPanel = panel;
        updateCapturedDisplay();
    }
    
    public void setTurnLabel(JLabel label) {
        this.turnLabel = label;
        updateTurnLabel();
    }
    
    private void updateHistoryDisplay() {
        if (historyArea != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < moveHistory.size(); i++) {
                sb.append((i + 1)).append(". ").append(moveHistory.get(i)).append("\n");
            }
            historyArea.setText(sb.toString());
        }
    }
    
    private void updateCapturedDisplay() {
        if (capturedWhitePanel != null) {
            capturedWhitePanel.removeAll();
            for (ChessPiece piece : whiteCaptured) {
                JLabel label = new JLabel(piece.getSymbol());
                label.setFont(new Font("Sans-Serif", Font.PLAIN, 30));
                capturedWhitePanel.add(label);
            }
            capturedWhitePanel.revalidate();
            capturedWhitePanel.repaint();
        }
        
        if (capturedBlackPanel != null) {
            capturedBlackPanel.removeAll();
            for (ChessPiece piece : blackCaptured) {
                JLabel label = new JLabel(piece.getSymbol());
                label.setFont(new Font("Sans-Serif", Font.PLAIN, 30));
                capturedBlackPanel.add(label);
            }
            capturedBlackPanel.revalidate();
            capturedBlackPanel.repaint();
        }
    }
    
    private void updateTurnLabel() {
        if (turnLabel != null) {
            turnLabel.setText("Current Turn: " + currentTurn.toUpperCase());
        }
    }
}