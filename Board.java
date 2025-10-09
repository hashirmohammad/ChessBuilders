
public class Board {
    // This Cretes the 8x
    private final String[][] grid = new String[8][8];

    public Board() {
        initializeEmptyBoard();
    }

    // Fill the board pattern (## alternating)
private void initializeEmptyBoard() {
    for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 8; col++) {
            boolean isDarkSquare = (row + col) % 2 == 0;

            if (isDarkSquare) {
                grid[row][col] = "##";
            } else {
                grid[row][col] = "  ";
            }
        }
    }
}

   
public void display() {
    // Top labels
    System.out.println("    A  B  C  D  E  F  G  H");

    // Loop top (row 0) to bottom (row 7)
    for (int r = 0; r < 8; r++) {
        int rank = r + 1; // top row = 1, bottom row = 8
        System.out.print(rank + " ");

        for (int c = 0; c < 8; c++) {
            System.out.print(" " + grid[r][c]);
        }

        System.out.println("  " + rank);
    }

    // Bottom labels
    System.out.println("    A  B  C  D  E  F  G  H");
}


}

