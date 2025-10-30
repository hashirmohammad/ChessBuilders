import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple Swing chess board display using Unicode chess piece codepoints.
 * Save as Display.java and run: java Display
 */
public class Display {

    private static final int SIZE = 8;
    private static JLabel selectedPiece = null;
    private static Point selectedLocation = null;
    private static JLabel[][] squares = new JLabel[SIZE][SIZE];
    private static boolean isDragging = false;

    // Unicode chess pieces (use \u2654 - \u265F codepoints)
    private static final String[][] START_BOARD = {
            // rank 8 (black major pieces)
            { "\u265C", "\u265E", "\u265D", "\u265B", "\u265A", "\u265D", "\u265E", "\u265C" },
            // rank 7 (black pawns)
            { "\u265F", "\u265F", "\u265F", "\u265F", "\u265F", "\u265F", "\u265F", "\u265F" },
            // empty ranks
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            { "", "", "", "", "", "", "", "" },
            // rank 2 (white pawns)
            { "\u2659", "\u2659", "\u2659", "\u2659", "\u2659", "\u2659", "\u2659", "\u2659" },
            // rank 1 (white major pieces)
            { "\u2656", "\u2658", "\u2657", "\u2655", "\u2654", "\u2657", "\u2658", "\u2656" }
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Display::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Chess Board (Unicode pieces)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel board = new JPanel(new GridLayout(SIZE, SIZE));
        Color light = new Color(0xF0D9B5); // light square
        Color dark = new Color(0xB58863);  // dark square

        Font pieceFont = new Font("SansSerif", Font.PLAIN, 48);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                squares[row][col] = new JLabel(START_BOARD[row][col], SwingConstants.CENTER);
                JLabel square = squares[row][col];
                square.setOpaque(true);
                square.setPreferredSize(new Dimension(64, 64));
                square.setFont(pieceFont);
                square.setForeground(Color.BLACK);

                final int finalRow = row;
                final int finalCol = col;

                MouseAdapter adapter = new MouseAdapter() {
                    private LineBorder previousBorder = null;

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!square.getText().isEmpty()) {
                            // Deselect previous
                            if (selectedPiece != null && selectedPiece != square) {
                                selectedPiece.setBorder(previousBorder);
                            }

                            selectedPiece = square;
                            selectedLocation = new Point(finalRow, finalCol);
                            previousBorder = (LineBorder) square.getBorder();
                            square.setBorder(new LineBorder(Color.RED, 3)); // highlight
                            isDragging = false;
                        }
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (selectedPiece != null) {
                            isDragging = true;
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (selectedPiece != null && isDragging) {
                            Point mousePoint = e.getPoint();
                            SwingUtilities.convertPointToScreen(mousePoint, square);

                            boolean dropped = false;

                            // Find the destination square
                            for (int r = 0; r < SIZE; r++) {
                                for (int c = 0; c < SIZE; c++) {
                                    JLabel destSquare = squares[r][c];
                                    Point squareLocation = destSquare.getLocationOnScreen();
                                    Rectangle bounds = new Rectangle(
                                            squareLocation.x,
                                            squareLocation.y,
                                            destSquare.getWidth(),
                                            destSquare.getHeight()
                                    );

                                    if (bounds.contains(mousePoint)) {
                                        // Only move if it's a *different* square
                                        if (r != selectedLocation.x || c != selectedLocation.y) {
                                            destSquare.setText(selectedPiece.getText());
                                            selectedPiece.setText("");
                                        }
                                        dropped = true;
                                        break;
                                    }
                                }
                            }

                            // If dropped outside or same square, do nothing
                            if (!dropped) {
                                // leave piece where it was
                            }

                            // Reset state
                            selectedPiece.setBorder(previousBorder);
                            selectedPiece = null;
                            selectedLocation = null;
                            isDragging = false;
                        } else if (selectedPiece != null) {
                            // If clicked but not dragged, just deselect
                            selectedPiece.setBorder(new LineBorder(Color.DARK_GRAY));
                            selectedPiece = null;
                            selectedLocation = null;
                        }
                    }
                };

                square.addMouseListener(adapter);
                square.addMouseMotionListener(adapter);

                // alternate square colors
                square.setBackground(((row + col) % 2 == 0) ? light : dark);
                square.setBorder(new LineBorder(Color.DARK_GRAY));

                // tooltip shows codepoint
                if (!START_BOARD[row][col].isEmpty()) {
                    int cp = START_BOARD[row][col].codePointAt(0);
                    square.setToolTipText(String.format("U+%04X", cp));
                }
                board.add(square);
            }
        }

        frame.add(board);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
