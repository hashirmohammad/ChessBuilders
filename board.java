public class board {

        private piece[][] grid = new piece[8][8];   

        public board() {
        initializeBoard();
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
            //white
            grid[6][0] = new pawn("P" , "W",6,0);
            grid[6][1] = new pawn("P" , "W",6,1);
            grid[6][2] = new pawn("P" , "W",6,2);
            grid[6][3] = new pawn("P" , "W",6,3);
            grid[6][4] = new pawn("P" , "W",6,4);
            grid[6][5] = new pawn("P" , "W",6,5);
            grid[6][6] = new pawn("P" , "W",6,6);
            grid[6][7] = new pawn("P" , "W",6,7);
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




    public static void main(String[] args) {
        board chessBoard = new board();
        chessBoard.display();
   
}
}


