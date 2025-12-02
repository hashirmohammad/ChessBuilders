package ChessBuilders.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the Chess Game application.
 * Creates the main window and integrates all components.
 */
public class ChessGame extends JFrame {
    private ChessBoard chessBoard;
    private JTextArea historyArea;
    private JPanel capturedWhitePanel;
    private JPanel capturedBlackPanel;
    private JLabel turnLabel;
    
    /**
     * Constructor for ChessGame.
     */
    public ChessGame() {
        setTitle("Chess Game - Phase 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create chess board
        chessBoard = new ChessBoard();
        
        // Create turn label
        turnLabel = new JLabel("Current Turn: WHITE", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        chessBoard.setTurnLabel(turnLabel);
        
        // Create side panel for history
        JPanel sidePanel = new JPanel(new BorderLayout(5, 5));
        sidePanel.setPreferredSize(new Dimension(300, 0));
        
        // Undo button
        JButton undoButton = new JButton("Undo Last Move");
        undoButton.addActionListener(e -> chessBoard.undoMove());
        
        // Move history
        JLabel historyLabel = new JLabel("Move History:");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(280, 250));
        chessBoard.setHistoryArea(historyArea);
        
        // Captured pieces panels
        JLabel capturedLabel = new JLabel("Captured Pieces:");
        capturedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel whiteCaptLabel = new JLabel("Captured by White:");
        capturedWhitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        capturedWhitePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        capturedWhitePanel.setPreferredSize(new Dimension(280, 60));
        chessBoard.setCapturedWhitePanel(capturedWhitePanel);
        
        JLabel blackCaptLabel = new JLabel("Captured by Black:");
        capturedBlackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        capturedBlackPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        capturedBlackPanel.setPreferredSize(new Dimension(280, 60));
        chessBoard.setCapturedBlackPanel(capturedBlackPanel);
        
        // Add components to side panel
        JPanel sidePanelTop = new JPanel(new BorderLayout(5, 5));
        sidePanelTop.add(undoButton, BorderLayout.NORTH);
        
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.add(historyLabel, BorderLayout.NORTH);
        historyPanel.add(historyScroll, BorderLayout.CENTER);
        sidePanelTop.add(historyPanel, BorderLayout.CENTER);
        
        JPanel capturedPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        capturedPanel.add(capturedLabel);
        capturedPanel.add(whiteCaptLabel);
        capturedPanel.add(capturedWhitePanel);
        capturedPanel.add(blackCaptLabel);
        capturedPanel.add(capturedBlackPanel);
        
        sidePanel.add(sidePanelTop, BorderLayout.CENTER);
        sidePanel.add(capturedPanel, BorderLayout.SOUTH);
        
        // Add components to main panel
        mainPanel.add(turnLabel, BorderLayout.NORTH);
        mainPanel.add(chessBoard, BorderLayout.CENTER);
        mainPanel.add(sidePanel, BorderLayout.EAST);
        
        add(mainPanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Creates the menu bar with game controls and settings.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> chessBoard.newGame());
        
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener(e -> chessBoard.saveGame());
        
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(e -> chessBoard.loadGame());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);
        
        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        
        JMenuItem boardStyleItem = new JMenuItem("Board Style");
        boardStyleItem.addActionListener(e -> showSettingsDialog());
        
        settingsMenu.add(boardStyleItem);
        
        menuBar.add(gameMenu);
        menuBar.add(settingsMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Shows the settings dialog for customizing board appearance.
     */
    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new GridLayout(3, 2, 10, 10));
        settingsDialog.setSize(400, 200);
        
        // Board style selection
        JLabel styleLabel = new JLabel("Board Style:");
        String[] styles = {"Classic", "Modern Green", "Wooden", "Ocean Blue"};
        JComboBox<String> styleCombo = new JComboBox<>(styles);
        
        settingsDialog.add(styleLabel);
        settingsDialog.add(styleCombo);
        
        // Apply and Close buttons
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            String selectedStyle = (String) styleCombo.getSelectedItem();
            chessBoard.changeBoardStyle(selectedStyle);
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> settingsDialog.dispose());
        
        settingsDialog.add(applyButton);
        settingsDialog.add(closeButton);
        
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }
    
    /**
     * Main method to start the chess game.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGame());
    }
}