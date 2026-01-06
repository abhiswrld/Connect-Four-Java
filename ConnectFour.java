import java.util.Scanner;

/**
 * A Console-based implementation of the classic Connect Four game.
 * Features include:
 * - 2-Player Local Multiplayer
 * - Dynamic Board Rendering with ANSI Color Support
 * - Input Validation and Error Handling
 * - Optimized Win-Detection Algorithms (Horizontal, Vertical, Diagonal)
 */
public class ConnectFour {

    // Game Configuration
    static final int HEIGHT = 6;
    static final int WIDTH = 7;
    static char[][] grid;
    
    // Logic: Tracks winning coordinates for visual highlighting
    static boolean[][] winningCells;

    // Player Information
    static String p1Name;
    static String p2Name;
    static boolean won = false;

    // ANSI Color Codes for UI enhancement
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m"; // Winning Line
    public static final String ANSI_RED = "\u001B[31m";    // Player 1
    public static final String ANSI_YELLOW = "\u001B[33m"; // Player 2

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("\n--- Welcome to Connect 4 ---");
        
        // Game Setup
        System.out.print("Enter Player 1 Name: ");
        p1Name = input.nextLine();
        System.out.print("Enter Player 2 Name: ");
        p2Name = input.nextLine();

        System.out.println("\nMatch Started: " + p1Name + " (Red) vs " + p2Name + " (Yellow)");

        // Initialize Board
        grid = new char[HEIGHT][WIDTH];
        winningCells = new boolean[HEIGHT][WIDTH];

        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH; c++) {
                grid[r][c] = '.';
            }
        }

        printBoard();
        playGame(input);
        
        input.close(); // Clean up resources
    }

    /**
     * Renders the current state of the game board to the console.
     * Handles ANSI color formatting for players and winning highlights.
     */
    public static void printBoard() {
        System.out.println("\n  0 1 2 3 4 5 6");
        System.out.println(" --------------- ");

        for (int r = 0; r < HEIGHT; r++) {
            System.out.print("| ");
            for (int c = 0; c < WIDTH; c++) {
                String symbol = String.valueOf(grid[r][c]);

                if (winningCells[r][c]) {
                    System.out.print(ANSI_PURPLE + symbol + ANSI_RESET + " ");
                } else if (grid[r][c] == 'X') {
                    System.out.print(ANSI_RED + symbol + ANSI_RESET + " ");
                } else if (grid[r][c] == 'O') {
                    System.out.print(ANSI_YELLOW + symbol + ANSI_RESET + " ");
                } else {
                    System.out.print(symbol + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println(" --------------- ");
    }

    /**
     * Main Game Loop. Handles turn alternation and win condition checks.
     * @param input The shared Scanner instance for user input.
     */
    public static void playGame(Scanner input) {
        while (!won) {
            // --- Player 1 Turn ---
            boolean turnComplete = false;
            while (!turnComplete) {
                int col = askForColumn(input, p1Name);
                turnComplete = dropPiece(col, 'X');
            }

            won = checkForWin();
            if (won) {
                System.out.println("\n" + ANSI_PURPLE + "GAME OVER: " + p1Name + " Wins!" + ANSI_RESET);
                break;
            }

            // --- Player 2 Turn ---
            turnComplete = false;
            while (!turnComplete) {
                int col = askForColumn(input, p2Name);
                turnComplete = dropPiece(col, 'O');
            }

            won = checkForWin();
            if (won) {
                System.out.println("\n" + ANSI_PURPLE + "GAME OVER: " + p2Name + " Wins!" + ANSI_RESET);
                break;
            }
        }
    }

    /**
     * Validates user input to ensure it is an integer within the board's bounds.
     */
    public static int askForColumn(Scanner input, String playerName) {
        while (true) {
            System.out.print("\n" + playerName + "'s turn, enter column (0-6): ");
            
            if (input.hasNextInt()) {
                int col = input.nextInt();
                if (col >= 0 && col < WIDTH) {
                    return col;
                }
                System.out.println("Invalid column. Please enter a number between 0 and " + (WIDTH - 1) + ".");
            } else {
                // Consume invalid token to prevent infinite loop
                String invalid = input.next();
                System.out.println("Invalid input '" + invalid + "'. Please enter a number.");
            }
        }
    }

    /**
     * Simulates gravity to place a token in the lowest available row of a column.
     * @return true if placement was successful, false if column is full.
     */
    public static boolean dropPiece(int col, char token) {
        for (int r = HEIGHT - 1; r >= 0; r--) {
            if (grid[r][col] == '.') {
                grid[r][col] = token;
                printBoard();
                return true;
            }
        }
        System.out.println("Column " + col + " is full. Please choose another.");
        return false;
    }

    /**
     * Scans the board for 4 connected tokens horizontally, vertically, or diagonally.
     * If a win is found, updates the winningCells grid for highlighting.
     * @return true if a win is detected.
     */
    public static boolean checkForWin() {
        // 1. Horizontal Check
        for (int r = 0; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH - 3; c++) {
                if (grid[r][c] != '.' && checkLine(r, c, 0, 1)) return true;
            }
        }

        // 2. Vertical Check
        for (int r = 0; r < HEIGHT - 3; r++) {
            for (int c = 0; c < WIDTH; c++) {
                if (grid[r][c] != '.' && checkLine(r, c, 1, 0)) return true;
            }
        }

        // 3. Diagonal Down-Right Check
        for (int r = 0; r < HEIGHT - 3; r++) {
            for (int c = 0; c < WIDTH - 3; c++) {
                if (grid[r][c] != '.' && checkLine(r, c, 1, 1)) return true;
            }
        }

        // 4. Diagonal Up-Right Check
        for (int r = 3; r < HEIGHT; r++) {
            for (int c = 0; c < WIDTH - 3; c++) {
                if (grid[r][c] != '.' && checkLine(r, c, -1, 1)) return true;
            }
        }
        return false;
    }

    /**
     * Helper to check for 4 consecutive matching tokens.
     * @param r Start row
     * @param c Start col
     * @param dr Row change (delta)
     * @param dc Col change (delta)
     */
    private static boolean checkLine(int r, int c, int dr, int dc) {
        char token = grid[r][c];
        if (grid[r + dr][c + dc] == token &&
            grid[r + 2 * dr][c + 2 * dc] == token &&
            grid[r + 3 * dr][c + 3 * dc] == token) {
            
            // Mark cells for highlighting
            winningCells[r][c] = true;
            winningCells[r + dr][c + dc] = true;
            winningCells[r + 2 * dr][c + 2 * dc] = true;
            winningCells[r + 3 * dr][c + 3 * dc] = true;
            printBoard();
            return true;
        }
        return false;
    }
}