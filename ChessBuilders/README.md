Class: CS 3354.R01

Team Members: Andre Mercado, Zachary Terry, and Muhammad Hashir

# ChessBuilders - Java Chess Game

A fully functional Chess application built in Java. This project implements standard chess rules, a graphical user interface (GUI) using Swing, and features such as save/load functionality, undo history, and visual themes.

##  Project Structure

The project is organized by the game logic from the visual interface.

###  The Logic (Model)
* **`ChessBuilders.board.Board`**: This is the heavy lifter. It maintains the 8x8 grid of pieces. It contains the logic to:
    * Validate if a move path is clear.
    * Simulate moves to see if they result in "Check" (illegal).
    * Detect "Checkmate".
* **`ChessBuilders.pieces.*`**: Contains logic for individual pieces (e.g., `King.java`, `Queen.java`, `Knight.java`). Each file describes how one specific piece moves (e.g., "Rooks move in straight lines," "Knights jump in L-shapes").

###  The Interface (View)
* **`ChessBuilders.gui.ChessGame`**: The main window frame. It holds the "Menu Bar" (File, Settings), the "History" text area on the right, and the "Captured Pieces" panels. It creates the container for the board.
* **`ChessBuilders.gui.ChessBoard`**: The actual 64-square grid. This file is strictly visual; it handles drawing buttons, setting their background colors (Green/White/Wood), and drawing the piece icons. It does *not* know the rules of chess.

###  The Controls (Controller)
* **`ChessBuilders.gui.GameController`**: The "Brain" of the GUI. 
    * When the GUI starts, this creates the backend `Board`.
    * When a player moves, this checks if it's valid, updates the backend, records the move history string ("e2 -> e4"), and tells the GUI to repaint.
    * It handles saving and loading the `GameState` file.
* **`ChessBuilders.gui.InputHandler`**: The listener. It listens for mouse clicks and drag-and-drop events. It translates "User clicked pixel X,Y" into "User wants to move piece at Row 1, Col 4" and sends that request to the `GameController`.

---

##  Features

1.  **Full Rule Enforcement**: Valid moves, Check detection, Checkmate, and Pawn Promotion.
2.  **Cross-Platform GUI**:
    * Works on Windows, macOS, and Linux.
    * **Click-to-Move**: Click a piece (highlights green), then click the destination.
    * **Drag-and-Drop**: Drag a piece to a valid square.
3.  **Game State Management**:
    * **Undo Move**: Revert accidental moves.
    * **Save/Load**: Save your progress to a `.save` file and resume later.
4.  **Customization**: Choose from 4 distinct board color themes (Classic, Modern, Wood, Ocean).
5.  **History**: Visual log of moves and captured pieces.



---

##  How to Compile and Run

### Prerequisites
* Java Development Kit (JDK) 8 or higher.

### Option 1: Visual Studio Code (Recommended)
This is the easiest way to run the project on any operating system.

1.  **Install Extensions**: Ensure you have the **"Extension Pack for Java"** installed in VS Code.
2.  **Open Project**:
    * Go to `File` > `Open Folder...`
    * Select the **parent folder** that contains the `ChessBuilders` folder. (Do not open the `ChessBuilders` folder itself; open the one *containing* it).
3.  **Run**:
    * In the file explorer, navigate to `ChessBuilders/Main.java`.
    * Wait a moment for the Java language server to load.
    * Click the **Run** or **Play** button that appears just above the `public static void main` line, or press `F5`.

### Option 2: Command Line 
#### Windows
Open Command Prompt (`cmd`) or PowerShell and navigate to the folder containing the `ChessBuilders` directory.

**1. Compile:**
*Note: We use backslashes (`\`) for file paths on Windows.*
```
javac ChessBuilders\Main.java
java ChessBuilders.Main
```
#### Terminal

Open your terminal and navigate to the folder containing the ChessBuilders directory.

**1. Compile:** 
*Note: We use forward slashes (/) for file paths on Mac/Linux.
```
javac ChessBuilders/Main.java
java ChessBuilders.Main
```