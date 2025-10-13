public class board {

        private piece[][] grid = new piece[8][8];   
        private String currentTurn;  // "W" or "B" to track whose turn it is

        public board() {
        initializeBoard();
        currentTurn = "W";
        }

         private void initializeBoard() {
            //black
            grid[1][0] = new pawn("P" , "B",1,0);
            grid[1][1] = new pawn("P" , "B",1,1);
            grid[1][2] = new pawn("P" , "B",1,2);
            grid[1][3] = new pawn("P" , "B",1,3);
            grid[1][4] = new pawn("P" , "B",1,4);
            grid[1][5] = new pawn("P" , "B",1,5);
            grid[1][6] = new pawn("P" , "B",1,6);
            grid[1][7] = new pawn("P" , "B",1,7);
            grid[0][0] = new rook("R" , "B",0,0);
            grid[0][7] = new rook("R" , "B",0,7);
            grid[0][2] = new bishop("B" , "B",0,2);
            grid[0][5] = new bishop("B" , "B",0,5);
            grid[0][1] = new knight("K", "B", 0, 1); 
            grid[0][6] = new knight("K", "B", 0, 6); 
            grid[0][3] = new queen("Q", "B", 0, 3); 



            //white
            grid[6][0] = new pawn("P" , "W",6,0);
            grid[6][1] = new pawn("P" , "W",6,1);
            grid[6][2] = new pawn("P" , "W",6,2);
            grid[6][3] = new pawn("P" , "W",6,3);
            grid[6][4] = new pawn("P" , "W",6,4);
            grid[6][5] = new pawn("P" , "W",6,5);
            grid[6][6] = new pawn("P" , "W",6,6);
            grid[6][7] = new pawn("P" , "W",6,7);
            grid[7][0] = new rook("R" , "W",7,0);
            grid[7][7] = new rook("R" , "W",7,7);
            grid[7][2] = new bishop("B" , "W",7,2);
            grid[7][5] = new bishop("B" , "W",7,5);
            grid[7][1] = new knight("K", "W", 7, 1); 
            grid[7][6] = new knight("K", "W", 7, 6); 
            grid[7][3] = new queen("Q", "W", 7, 3); 
         }

    public piece[][] getGrid() {
        return grid;
    }

    public piece getPieceAt(int row, int col) {
        return grid[row][col];
    }

    public void setPieceAt(int row, int col, piece p) {
        grid[row][col] = p;
    }
      public void display(){
       System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.printf("%-4s", c);
        }
        System.out.println();

        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                String squareColor = ((i + j) % 2 == 0) ? "  " : "##";

                if (grid[i][j] == null) {
                    System.out.printf("%-4s", squareColor);
                } else {
                    System.out.printf("%-4s", grid[i][j]);
                }
            }
            System.out.println(" " );
        }


    }

    public static String getPiece(String[][] grid, String position) {
    if (position.length() != 2) return null;

    char file = position.toUpperCase().charAt(0); // e.g., 'E'
    char rank = position.charAt(1);               // e.g., '4'

    int col = file - 'A' + 1;                     // 'A' → 1
    int row = 8 - Character.getNumericValue(rank); // rank 8 → row 1, rank 1 → row 8

    if (row < 0 || row > 7 || col < 0 || col > 7) {
        return null;
    }

    return grid[row][col];
}
// Returns the current turn: "W" or "B"
public String getCurrentTurn() {
    return currentTurn;
}

// Attempts to move a piece and returns true if successful
public boolean makeMove(int startRow, int startCol, int endRow, int endCol) {
    piece selected = grid[startRow][startCol];

    if (selected == null) {
        System.out.println("No piece at that square.");
        return false;
    }

    if (!selected.getColor().equals(currentTurn)) {
        System.out.println("It's not " + (currentTurn.equals("W") ? "White" : "Black") + "'s turn!");
        return false;
    }

    if (!selected.isValidMove(endRow, endCol, grid)) {
        System.out.println("Invalid move for this piece.");
        return false;
    }

    // Move piece
    grid[endRow][endCol] = selected;
    grid[startRow][startCol] = null;
    selected.setPosition(endRow, endCol);

    switchTurn();  // Switch turns
    return true;
}
public void switchTurn() {
    currentTurn = currentTurn.equals("W") ? "B" : "W";
}




    public static void main(String[] args) {
    board chessBoard = new board();
    player white = new player("W");
    player black = new player("B");

    // Assign pieces to players
    piece[][] grid = chessBoard.getGrid();
    for (int r = 0; r < 8; r++) {
        for (int c = 0; c < 8; c++) {
            if (grid[r][c] != null) {
                if (grid[r][c].getColor().equals("W")) white.addPiece(grid[r][c]);
                else black.addPiece(grid[r][c]);
            }
        }
    }

    // Game loop (simplified)
    while (true) {
        chessBoard.display();
        if (chessBoard.getCurrentTurn().equals("W")) white.makeMove(chessBoard);
        else black.makeMove(chessBoard);
    }
}

        }
    

   



