package ChessBuilders.gui;

import ChessBuilders.board.Board;
import ChessBuilders.board.Position;
import ChessBuilders.pieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChessBoard extends JPanel {
    private static final int BOARD_SIZE = 8;
    private final JButton[][] squares = new JButton[8][8];
    
    // Components
    private GameController controller;
    private InputHandler inputHandler;

    // UI Elements passed from ChessGame frame
    private JTextArea historyArea;
    private JPanel whitePanel;
    private JPanel blackPanel;
    private JLabel turnLabel;

    // Colors
    private Color lightColor = new Color(240, 217, 181);
    private Color darkColor = new Color(181, 136, 99);
    private final Color highlightColor = new Color(127, 166, 80);

    public ChessBoard() {
        setLayout(new GridLayout(8, 8));
        
        // initialize controller and input handler
        this.controller = new GameController(this);
        this.inputHandler = new InputHandler(this, controller);
        
        createBoardGrid();
        
        // Start game only AFTER inputHandler is created
        this.controller.startNewGame(); 
    }

    private void createBoardGrid() {
        removeAll();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                JButton btn = new JButton();
                
                // --- MAC COMPATIBILITY FIXES ---
                btn.setOpaque(true);               // REQUIRED for background colors on Mac
                btn.setContentAreaFilled(true);    // REQUIRED for background colors to paint
                btn.setBorderPainted(true);        // Ensures the border line is drawn
                
                btn.setFocusPainted(false);
                btn.setFont(new Font("Sans-Serif", Font.PLAIN, 50));
                
                // Restore the black grid lines
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                
                // Color
                btn.setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
                
                // Attach Logic
                inputHandler.attachListeners(btn, r, c);
                
                squares[r][c] = btn;
                add(btn);
            }
        }
        revalidate();
        repaint();
    }

    // --- Update Methods called by Controller ---

    public void updateBoardFromBackend(Board backend) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = backend.getPiece(new Position(r, c));
                squares[r][c].setText(getSymbol(p));
            }
        }
        repaint();
    }

    public void highlightSquare(int r, int c, boolean isOn) {
        if(isOn) squares[r][c].setBackground(highlightColor);
        else squares[r][c].setBackground((r + c) % 2 == 0 ? lightColor : darkColor);
    }
    public void clearAllHighlights() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                // Determine what the original color should be
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

    // --- Styling ---
    
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
        for(int r=0; r<8; r++) {
            for(int c=0; c<8; c++) {
                squares[r][c].setBackground((r+c)%2==0 ? light : dark);
            }
        }
        repaint();
    }

    // --- Setters for UI components ---
    public void setHistoryArea(JTextArea a) { this.historyArea = a; }
    
    public void setCapturedWhitePanel(JPanel panel) { 
        this.whitePanel = panel; 
        controller.refreshUI();
    }

    public void setCapturedBlackPanel(JPanel panel) { 
        this.blackPanel = panel; 
        controller.refreshUI();
    }

    public void setTurnLabel(JLabel l) { this.turnLabel = l; }

    // --- Forwarding methods to Controller ---
    public void newGame() { controller.startNewGame(); }
    public void saveGame() { controller.saveGame(); }
    public void loadGame() { controller.loadGame(); }
    public void undoMove() { controller.undoMove(); }

    // --- UI Update Helpers ---
    public void updateHistory(List<String> moves) {
        if(historyArea == null) return;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<moves.size(); i++) sb.append(i+1).append(". ").append(moves.get(i)).append("\n");
        historyArea.setText(sb.toString());
    }

    public void updateCaptured(List<ChessPiece> white, List<ChessPiece> black) {
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