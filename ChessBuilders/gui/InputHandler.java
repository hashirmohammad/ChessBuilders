package ChessBuilders.gui;

import ChessBuilders.pieces.Piece;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;

public class InputHandler {

    private final ChessBoard view;
    private final GameController controller;

    // Track selection
    private int selectedRow = -1;
    private int selectedCol = -1;

    public InputHandler(ChessBoard view, GameController controller) {
        this.view = view;
        this.controller = controller;
    }

    public void attachListeners(JButton square, int row, int col) {
        // 1. Click Listener
        square.addActionListener(e -> handleClick(row, col));

        // 2. Drag Support
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(square, DnDConstants.ACTION_MOVE, dge -> {
            Piece p = controller.getPieceAt(row, col);
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                // Select and highlight immediately on drag start
                handleClick(row, col); 
                Transferable t = new StringSelection(row + "," + col);
                ds.startDrag(dge, DragSource.DefaultMoveDrop, t, new DragSourceAdapter() {});
            }
        });

        // 3. Drop Support
        new DropTarget(square, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    String[] coords = data.split(",");
                    int fromR = Integer.parseInt(coords[0]);
                    int fromC = Integer.parseInt(coords[1]);

                    // Only process if moved to a different square
                    if (fromR != row || fromC != col) {
                        controller.processMove(fromR, fromC, row, col);
                    }
                    
                    // Cleanup
                    resetSelection();
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                }
            }
        });
    }

    private void handleClick(int row, int col) {
        // If nothing is currently selected
        if (selectedRow == -1) {
            Piece p = controller.getPieceAt(row, col);
            // Only allow selecting your own pieces
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                selectSquare(row, col);
            }
        } 
        // If a piece IS currently selected
        else {
            // Clicked the SAME piece? Deselect it.
            if (selectedRow == row && selectedCol == col) {
                resetSelection();
            } 
            // Clicked a different piece...
            else {
                Piece target = controller.getPieceAt(row, col);
                // Is it one of OUR pieces? Switch selection to that one.
                if (target != null && target.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                    selectSquare(row, col); // Switch selection
                } 
                // Is it an empty square or enemy? Try to MOVE.
                else {
                    controller.processMove(selectedRow, selectedCol, row, col);
                    resetSelection();
                }
            }
        }
    }

    private void selectSquare(int r, int c) {
        // 1. Clear everything first (Fixes the "Multiple Green Squares" glitch)
        view.clearAllHighlights();
        
        // 2. Set new selection
        selectedRow = r;
        selectedCol = c;
        view.highlightSquare(r, c, true);
    }

    public void resetSelection() {
        // Clear everything to be safe
        view.clearAllHighlights();
        selectedRow = -1;
        selectedCol = -1;
    }
}