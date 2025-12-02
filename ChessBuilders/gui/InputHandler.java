package ChessBuilders.gui;

import ChessBuilders.pieces.Piece;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;

/**
 * Handles all mouse input (clicks and drag-and-drop) for the ChessBoard.
 * Translates raw Swing events into game actions sent to the GameController.
 */
public class InputHandler {

    private final ChessBoard view;
    private final GameController controller;

    // Track the currently selected square (-1 means nothing selected)
    private int selectedRow = -1;
    private int selectedCol = -1;

    public InputHandler(ChessBoard view, GameController controller) {
        this.view = view;
        this.controller = controller;
    }

    /**
     * Attaches Click, Drag, and Drop listeners to a specific square button.
     */
    public void attachListeners(JButton square, int row, int col) {
        // 1. Standard Click Event
        square.addActionListener(e -> handleClick(row, col));

        // 2. Drag Start Event
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(square, DnDConstants.ACTION_MOVE, dge -> {
            Piece p = controller.getPieceAt(row, col);
            
            // Only allow dragging if it's the player's turn and their own piece
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                handleClick(row, col); // Select visually
                
                // create payload with coordinates
                Transferable t = new StringSelection(row + "," + col);
                ds.startDrag(dge, DragSource.DefaultMoveDrop, t, new DragSourceAdapter() {});
            }
        });

        // 3. Drop Event
        new DropTarget(square, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    
                    // Parse coordinates from payload
                    String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    String[] coords = data.split(",");
                    int fromR = Integer.parseInt(coords[0]);
                    int fromC = Integer.parseInt(coords[1]);

                    // Execute move if dropped on a different square
                    if (fromR != row || fromC != col) {
                        controller.processMove(fromR, fromC, row, col);
                    }
                    
                    resetSelection();
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                }
            }
        });
    }

    /**
     * logic for selecting pieces or moving them based on current state.
     */
    private void handleClick(int row, int col) {
        // Case A: Nothing selected -> Try to select a piece
        if (selectedRow == -1) {
            Piece p = controller.getPieceAt(row, col);
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                selectSquare(row, col);
            }
        } 
        // Case B: Piece already selected
        else {
            // Clicked same piece? -> Deselect
            if (selectedRow == row && selectedCol == col) {
                resetSelection();
            } 
            // Clicked different square -> Check target
            else {
                Piece target = controller.getPieceAt(row, col);
                
                // If clicked another own piece, switch selection
                if (target != null && target.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                    selectSquare(row, col); 
                } 
                // Otherwise, attempt to move there
                else {
                    controller.processMove(selectedRow, selectedCol, row, col);
                    resetSelection();
                }
            }
        }
    }

    private void selectSquare(int r, int c) {
        // Clear all highlights first to prevent visual glitches (e.g. fast clicking)
        view.clearAllHighlights();
        
        selectedRow = r;
        selectedCol = c;
        view.highlightSquare(r, c, true);
    }

    public void resetSelection() {
        view.clearAllHighlights();
        selectedRow = -1;
        selectedCol = -1;
    }
}