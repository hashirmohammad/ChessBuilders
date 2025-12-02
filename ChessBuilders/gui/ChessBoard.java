package ChessBuilders.gui;

import ChessBuilders.board.Board;
import ChessBuilders.board.Position;
import ChessBuilders.pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The main View class for the Chess Game. 
 * Responsibilities:
 * 1. Draws the 8x8 grid of buttons.
 * 2. Handles visual updates (colors, highlighting, piece icons).
 * 3. Acts as the bridge between the GameController and the UI components.
 */
public class ChessBoard extends JPanel {
    private static final int BOARD_SIZE = 8;
    
    // The visual grid of buttons
    private final JButton[][] squares = new JButton[8][8];
    
    // MVC Components
    private GameController controller;
    private InputHandler inputHandler;

    // External UI references (Sidebar elements)
    private JTextArea historyArea;
    private JPanel whitePanel;
    private JPanel blackPanel;
    private JLabel turnLabel;

    // Theme Colors (Default: Classic)
    private Color lightColor = new Color(240, 217, 181);
    private Color darkColor = new Color(181, 136, 99);
    private final Color highlightColor = new Color(127, 166, 80); // Green for selection

    public ChessBoard() {
        setLayout(new GridLayout(8, 8));
        
        // Initialize the Controller (Logic) and InputHandler (Mouse Events)
        this.controller = new GameController(this);
        this.inputHandler = new InputHandler(this, controller);
        
        createBoardGrid();
        
        // IMPORTANT: Start the game only AFTER inputHandler is fully created.
        // This prevents a NullPointerException when the game tries to reset selection on startup.
        this.controller.startNewGame(); 
    }

    /**
     * Builds the visual 8x8 grid of JButtons.
     */
    private void createBoardGrid() {
        removeAll(); // Clear if rebuilding (e.g., new game)
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = new JButton();
                
                // --- Cross-Platform Compatibility ---
                // By default, macOS buttons are transparent/glassy. 
                // These 3 lines force the background color to paint correctly on Mac & Windows.
                btn.setOpaque(true);               
                btn.setContentAreaFilled(true);    
                btn.setBorderPainted(true);        
                
                // Visual styling
                btn.setFocusPainted(false); // No "clicked" outline
                btn.setFont(new Font("Sans-Serif", Font.PLAIN, 50)); // Font size for chess icons
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Grid lines
                
                // Set initial background color based on checkerboard pattern
                btn.setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
                
                // Attach mouse click and drag listeners
                inputHandler.attachListeners(btn, r, c);
                
                squares[r][c] = btn;
                add(btn);
            }
        }
        revalidate();
        repaint();
    }

    // ==========================================
    //           View Update Methods
    // ==========================================

    /**
     * Syncs the visual board with the backend logic board.
     * Updates the text/icon of every button.
     */
    public void updateBoardFromBackend(Board backend) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = backend.getPiece(new Position(r, c));
                squares[r][c].setText(getSymbol(p));
            }
        }
        repaint();
    }

    /**
     * Toggles the green highlight on a specific square.
     */
    public void highlightSquare(int r, int c, boolean isOn) {
        if(isOn) {
            squares[r][c].setBackground(highlightColor);
        } else {
            // Revert to original checkerboard color
            squares[r][c].setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
        }
    }

    /**
     * Forcefully resets ALL squares to their original colors.
     * Essential for preventing visual glitches where multiple squares stay green
     * if the user clicks very fast.
     */
    public void clearAllHighlights() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                Color original = (r + c) % 2 == 0 ? lightColor : darkColor;
                squares[r][c].setBackground(original);
            }
        }
    }

    public void resetSelection() {
        if (inputHandler != null) {
            inputHandler.resetSelection();
        }
    }

    // ==========================================
    //           Styling & Customization
    // ==========================================
    
    public void changeBoardStyle(String styleName) {
        switch (styleName) {
            case "Classic":      setColors(new Color(240, 217, 181), new Color(181, 136, 99)); break;
            case "Modern Green": setColors(new Color(238, 238, 210), new Color(118, 150, 86)); break;
            case "Wooden":       setColors(new Color(222, 184, 135), new Color(139, 69, 19)); break;
            case "Ocean Blue":   setColors(new Color(176, 224, 230), new Color(70, 130, 180)); break;
        }
    }

    public void setColors(Color light, Color dark) {
        this.lightColor = light;
        this.darkColor = dark;
        // Re-apply colors to existing buttons
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                squares[r][c].setBackground((r+c)%2==0 ? light : dark);
            }
        }
        repaint();
    }

    // ==========================================
    //        External Component Linkage
    // ==========================================
    // These methods allow the main JFrame (ChessGame) to pass in UI references
    
    public void setHistoryArea(JTextArea a) { this.historyArea = a; }
    
    public void setCapturedWhitePanel(JPanel panel) { 
        this.whitePanel = panel; 
        controller.refreshUI(); // Force immediate update
    }

    public void setCapturedBlackPanel(JPanel panel) { 
        this.blackPanel = panel; 
        controller.refreshUI(); // Force immediate update
    }

    public void setTurnLabel(JLabel l) { this.turnLabel = l; }

    // ==========================================
    //        Controller Forwarding
    // ==========================================
    // Pass-through methods so the MenuBar can trigger game actions
    
    public void newGame() { controller.startNewGame(); }
    public void saveGame() { controller.saveGame(); }
    public void loadGame() { controller.loadGame(); }
    public void undoMove() { controller.undoMove(); }

    // ==========================================
    //          UI Update Helpers
    // ==========================================
    
    public void updateHistory(List<String> moves) {
        if(historyArea == null) return;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<moves.size(); i++) {
            sb.append(i+1).append(". ").append(moves.get(i)).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    public void updateCaptured(List<ChessPiece> white, List<ChessPiece> black) {
        // Helper to refresh a capture panel with piece icons
        if(whitePanel != null) {
            whitePanel.removeAll();
            for(ChessPiece p : white) whitePanel.add(createPieceLabel(p));
            whitePanel.revalidate(); whitePanel.repaint();
        }
        if(blackPanel != null) {
            blackPanel.removeAll();
            for(ChessPiece p : black) blackPanel.add(createPieceLabel(p));
            blackPanel.revalidate(); blackPanel.repaint();
        }
    }

    public void updateTurnLabel(String turn) {
        if(turnLabel != null) turnLabel.setText("Current Turn: " + turn.toUpperCase());
    }

    private JLabel createPieceLabel(ChessPiece p) {
        JLabel l = new JLabel(p.getSymbol());
        l.setFont(new Font("Sans-Serif", Font.PLAIN, 25));
        return l;
    }

    /**
     * Converts a backend Piece object into a Unicode String symbol.
     */
    private String getSymbol(Piece p) {
        if(p == null) return "";
        boolean w = p.getColor().equalsIgnoreCase("white");
        switch(p.getClass().getSimpleName()) {
            case "King": return w ? "♔" : "♚";
            case "Queen": return w ? "♕" : "♛";
            case "Rook": return w ? "♖" : "♜";
            case "Bishop": return w ? "♗" : "♝";
            case "Knight": return w ? "♘" : "♞";
            case "Pawn": return w ? "♙" : "♟";
            default: return "";
        }
    }
    
    public Color getLightColor() { return lightColor; }
    public Color getDarkColor() { return darkColor; }
}