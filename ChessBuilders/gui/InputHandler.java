package ChessBuilders.gui;

import ChessBuilders.pieces.Piece;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;

/**
 * Handles Mouse clicks and Drag-and-Drop interactions.
 */
public class InputHandler {

    private final ChessBoard view;
    private final GameController controller;

    private int selectedRow = -1;
    private int selectedCol = -1;

    public InputHandler(ChessBoard view, GameController controller) {
        this.view = view;
        this.controller = controller;
    }

    /**
     * Attaches listeners to a specific square button.
     */
    public void attachListeners(JButton square, int row, int col) {
        // 1. Click Listener
        square.addActionListener(e -> handleClick(row, col));

        // 2. Drag Source (Starting a drag)
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(square, DnDConstants.ACTION_MOVE, dge -> {
            Piece p = controller.getPieceAt(row, col);
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                selectSquare(row, col); // Visual highlight
                Transferable t = new StringSelection(row + "," + col);
                ds.startDrag(dge, DragSource.DefaultMoveDrop, t, new DragSourceAdapter() {});
            }
        });

        // 3. Drop Target (Finishing a drag)
        new DropTarget(square, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                    String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    String[] coords = data.split(",");
                    int fromR = Integer.parseInt(coords[0]);
                    int fromC = Integer.parseInt(coords[1]);

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

    private void handleClick(int row, int col) {
        // If nothing selected, try to select
        if (selectedRow == -1) {
            Piece p = controller.getPieceAt(row, col);
            if (p != null && p.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                selectSquare(row, col);
            }
        } 
        // If something selected...
        else {
            // If clicked same square, deselect
            if (selectedRow == row && selectedCol == col) {
                resetSelection();
            } 
            // If clicked own piece, switch selection
            else {
                Piece target = controller.getPieceAt(row, col);
                if (target != null && target.getColor().equalsIgnoreCase(controller.getCurrentTurn())) {
                    resetSelection();
                    selectSquare(row, col);
                } else {
                    // Attempt move
                    controller.processMove(selectedRow, selectedCol, row, col);
                    resetSelection();
                }
            }
        }
    }

    private void selectSquare(int r, int c) {
        selectedRow = r;
        selectedCol = c;
        view.highlightSquare(r, c, true);
    }

    public void resetSelection() {
        if (selectedRow != -1) {
            view.highlightSquare(selectedRow, selectedCol, false);
        }
        selectedRow = -1;
        selectedCol = -1;
    }
}