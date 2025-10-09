package board;
import pieces.*;

public class Board {
    private final Piece[][] grid = new Piece[8][8];

    public Board() {
        initEmpty();
        initStart();
    }

    private void initEmpty() {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                grid[r][c] = null;
    }

    private void initStart() {
        // black back rank (row 0 => rank 8)
        grid[0][0] = new Rook(0,0,"black");
        grid[0][1] = new Knight(0,1,"black");
        grid[0][2] = new Bishop(0,2,"black");
        grid[0][3] = new Queen(0,3,"black");
        grid[0][4] = new King (0,4,"black");
        grid[0][5] = new Bishop(0,5,"black");
        grid[0][6] = new Knight(0,6,"black");
        grid[0][7] = new Rook(0,7,"black");

        // black pawns (row 1 => rank 7)
        for (int c = 0; c < 8; c++) grid[1][c] = new Pawn(1,c,"black");

        // white pawns (row 6 => rank 2)
        for (int c = 0; c < 8; c++) grid[6][c] = new Pawn(6,c,"white");

        // white back rank (row 7 => rank 1)
        grid[7][0] = new Rook(7,0,"white");
        grid[7][1] = new Knight(7,1,"white");
        grid[7][2] = new Bishop(7,2,"white");
        grid[7][3] = new Queen(7,3,"white");
        grid[7][4] = new King (7,4,"white");
        grid[7][5] = new Bishop(7,5,"white");
        grid[7][6] = new Knight(7,6,"white");
        grid[7][7] = new Rook(7,7,"white");
    }

    public void display() {
        System.out.println("    A  B  C  D  E  F  G  H");
        for (int r = 0; r < 8; r++) {
            int rank = 8 - r; // 8 at top, 1 at bottom
            System.out.print(rank + " ");
            for (int c = 0; c < 8; c++) {
                if (grid[r][c] == null) {
                    // reversed checker pattern for ## as requested
                    System.out.print(((r + c) % 2 == 0) ? "   " : " ##");
                } else {
                    System.out.print(" " + grid[r][c]); // e.g., bR, wP
                }
            }
            System.out.println("  " + rank);
        }
        System.out.println("    A  B  C  D  E  F  G  H");
    }
}
