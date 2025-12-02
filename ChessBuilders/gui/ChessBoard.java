package ChessBuilders.gui;

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
    private static final int BOARD_SIZE = 8;

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
        board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        squares = new JButton[BOARD_SIZE][BOARD_SIZE];
        moveHistory = new ArrayList<>();
        whiteCaptured = new ArrayList<>();
        blackCaptured = new ArrayList<>();
        gameHistory = new ArrayList<>();

        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        createSquares();
    }

    /**
     * Initializes the chess board with pieces in starting positions.
     */
    private void initializeBoard() {
        // We sync from the backend immediately to ensure truth is one place
        updateGUIFromBackend();
    }

    /**
     * Creates the GUI squares for the chess board.
     */
    private void createSquares() {
        removeAll(); // make sure panel is empty before rebuilding
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
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
        revalidate();
        repaint();
    }

    /**
     * Sets up drag and drop functionality for a square.
     */
    private void setupDragAndDrop(JButton square, int row, int col) {
        // Create a DragSource
        final DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(square, DnDConstants.ACTION_MOVE,
            new DragGestureListener() {
                public void dragGestureRecognized(DragGestureEvent dge) {
                    try {
                        Position pos = new Position(row, col);
                        Piece piece = backendBoard.getPiece(pos);

                        if (piece != null && piece.getColor().equals(currentTurn)) {
                            selectedRow = row;
                            selectedCol = col;
                            highlightSquare(row, col, true);

                            Transferable transferable = new StringSelection(row + "," + col);
                            ds.startDrag(dge, DragSource.DefaultMoveDrop, transferable, new DragSourceAdapter() {});
                        }
                    } catch (Exception ex) {
                        // swallow — nothing to do if drag cannot start
                    }
                }
            });

        // Drop target
        new DropTarget(square, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    Transferable transferable = dtde.getTransferable();
                    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                        String[] coords = data.split(",");
                        if (coords.length == 2) {
                            int fromRow = Integer.parseInt(coords[0].trim());
                            int fromCol = Integer.parseInt(coords[1].trim());

                            // We only move if it's a different square
                            if (fromRow != row || fromCol != col) {
                                makeMove(fromRow, fromCol, row, col);
                            }
                        }
                    }
                    highlightSquare(fromRowSafe(), fromColSafe(), false); // defensive unhighlight
                    selectedRow = -1;
                    selectedCol = -1;
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                }
            }

            // Helpers to safely get coordinates to unhighlight
            private int fromRowSafe() { return selectedRow >= 0 ? selectedRow : 0; }
            private int fromColSafe() { return selectedCol >= 0 ? selectedCol : 0; }
        });
    }

    /**
     * Handles click events on chess board squares.
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

                // Defensive: if somehow selectedPiece is null, reset selection
                if (selectedPiece == null) {
                    highlightSquare(selectedRow, selectedCol, false);
                    selectedRow = -1;
                    selectedCol = -1;
                    return;
                }

                // Check if trying to select a different piece of same color
                if (targetPiece != null &&
                    selectedPiece.getColor() != null &&
                    targetPiece.getColor() != null &&
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
     */
    private void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Position from = new Position(fromRow, fromCol);
        Position to = new Position(toRow, toCol);

        Piece movingPiece = backendBoard.getPiece(from);
        Piece capturedPiece = backendBoard.getPiece(to);

        if (movingPiece == null) {
            return;
        }

        // IMPORTANT: Save state BEFORE making the move
        GameState state = new GameState(
            backendBoard,
            currentTurn,
            new ArrayList<>(moveHistory),
            new ArrayList<>(whiteCaptured),
            new ArrayList<>(blackCaptured),
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

        // Check if pawn promotion occurred (backend replaced pawn with another piece)
        Piece finalPiece = backendBoard.getPiece(to);
        if (movingPiece instanceof Pawn && !(finalPiece instanceof Pawn)) {
            JOptionPane.showMessageDialog(this,
                "Pawn promoted!",
                "Promotion",
                JOptionPane.INFORMATION_MESSAGE);
        }

        // Track captured pieces in GUI (capturedPiece holds piece that WAS on 'to' before move)
        if (capturedPiece != null) {
            ChessPiece guiPiece = convertToGUIPiece(capturedPiece);
            if ("white".equals(movingPiece.getColor())) {
                whiteCaptured.add(guiPiece);
            } else {
                blackCaptured.add(guiPiece);
            }
        }

        // Record move in history
        String pieceName = movingPiece.getClass().getSimpleName();
        String move = safeColor(movingPiece.getColor()) + " " +
                      pieceName + " " + (char)('a' + fromCol) + (8 - fromRow) +
                      " → " + (char)('a' + toCol) + (8 - toRow);
        moveHistory.add(move);

        // CHECK FOR CHECK/CHECKMATE using backend
        Board.Color opponentColor = boardColorFromString(currentTurn.equals("white") ? "black" : "white");

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
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Position pos = new Position(row, col);
                Piece backendPiece = backendBoard.getPiece(pos);

                if (backendPiece == null) {
                    board[row][col] = null;
                    if (squares[row][col] != null) squares[row][col].setText("");
                } else {
                    board[row][col] = convertToGUIPiece(backendPiece);
                    if (squares[row][col] != null) squares[row][col].setText(getUnicodeSymbol(backendPiece));
                }
            }
        }
        repaint();
    }

    /**
     * Converts Phase 1 Piece to Phase 2 ChessPiece for GUI display.
     */
    private ChessPiece convertToGUIPiece(Piece backendPiece) {
        if (backendPiece == null) return null;
        String type = backendPiece.getClass().getSimpleName().toLowerCase();
        String color = backendPiece.getColor();
        return new ChessPiece(type, color);
    }

    /**
     * Gets Unicode symbol from backend Piece.
     */
    private String getUnicodeSymbol(Piece piece) {
        if (piece == null) return "";
        String className = piece.getClass().getSimpleName().toLowerCase();
        boolean isWhite = "white".equals(piece.getColor());

        switch (className) {
            case "king":
                return isWhite ? "\u2654" : "\u265A";
            case "queen":
                return isWhite ? "\u2655" : "\u265B";
            case "rook":
                return isWhite ? "\u2656" : "\u265C";
            case "bishop":
                return isWhite ? "\u2657" : "\u265D";
            case "knight":
                return isWhite ? "\u2658" : "\u265E";
            case "pawn":
                return isWhite ? "\u2659" : "\u265F";
            default:
                return "";
        }
    }

    /**
     * Highlights or unhighlights a square.
     */
    private void highlightSquare(int row, int col, boolean highlight) {
        if (row < 0 || col < 0 || row >= BOARD_SIZE || col >= BOARD_SIZE) return;
        if (squares[row][col] == null) return;
        if (highlight) {
            squares[row][col].setBackground(selectedColor);
        } else {
            Color squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
            squares[row][col].setBackground(squareColor);
        }
    }

    /**
     * Declares the winner and shows a dialog.
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
        // Reset backend board
        backendBoard = new Board();
        backendBoard.setupClassic();

        // Reset GUI structures
        board = new ChessPiece[BOARD_SIZE][BOARD_SIZE];
        moveHistory.clear();
        whiteCaptured.clear();
        blackCaptured.clear();
        gameHistory.clear();
        currentTurn = "white";
        selectedRow = -1;
        selectedCol = -1;

        // Recreate the visual grid
        createSquares(); 
        
        // Sync pieces
        updateGUIFromBackend();

        updateHistoryDisplay();
        updateCapturedDisplay();
        updateTurnLabel();
    }

    /**
     * Saves the current game state to a file.
     */
    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Chess Game");
        fileChooser.setSelectedFile(new File("chessgame.save"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            GameState state = new GameState(
                backendBoard,
                currentTurn,
                new ArrayList<>(moveHistory),
                new ArrayList<>(whiteCaptured),
                new ArrayList<>(blackCaptured),
                lightSquare,
                darkSquare
            );

            try (FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile());
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(state);
            }

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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Chess Game");

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            GameState state;
            try (FileInputStream fileIn = new FileInputStream(fileChooser.getSelectedFile());
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                state = (GameState) in.readObject();
            }

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

            // Recreate squares to apply potential color scheme changes
            createSquares();

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
                "Error loading game: " + e.getMessage(),
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
        String[][] pieceData = new String[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (boardData[r][c] != null) {
                    // Combine type and color for Board.setupFromSave logic
                    pieceData[r][c] = boardData[r][c].type + "," + boardData[r][c].color;
                } else {
                    pieceData[r][c] = null;
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

        // Force full redraw
        createSquares();
        updateGUIFromBackend();

        updateHistoryDisplay();
        updateCapturedDisplay();
        updateTurnLabel();
    }

    /**
     * Changes the board color scheme.
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
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (squares[row][col] != null) {
                    Color squareColor = (row + col) % 2 == 0 ? lightSquare : darkSquare;
                    squares[row][col].setBackground(squareColor);
                }
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

    // ---- Helper utilities ----

    private String safeColor(String c) {
        return c == null ? "unknown" : c;
    }

    private Board.Color boardColorFromString(String colorStr) {
        if (colorStr == null) return Board.Color.WHITE;
        if (colorStr.equalsIgnoreCase("white")) return Board.Color.WHITE;
        else return Board.Color.BLACK;
    }
}