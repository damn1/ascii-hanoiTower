
import static java.lang.Thread.sleep;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author damiano
 */
public class D4mnAsciiHanoiTower {

    // variables to print in console with colors:
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // variables to manage user options:
    static final char PLAY = 'p';
    static final char LEARN = 'l';

    // variables to manage the HANOI TOWER:
    static int STARTING_PIECES;
    static final int SOURCE = 0;
    static final int HELPER = 1;
    static final int TARGET = 2;

    // hanoi tower is an integer matrix, STARTING_PIECESx3, containing the number that indicates the size of the block.
    // if there is no block, there is a 0 in the matrix.
    static int hanoiTower[][];

    
    static int sleepTime = 0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        presentation();
        char choose = inputs();
        init();
        printTower();
        
        switch (choose) {
            case LEARN:
                sleepTime = 100;
                // recursive solution
                moveEdu(STARTING_PIECES, SOURCE, HELPER, TARGET);
                break;
            case PLAY:
                // show user format of the input:
                System.out.println("Make your moves in the form \"s,t\" where s,t ∈ {1,2,3} are respectively the number"
                        + "of the source tower and the target one for the move you want to make."
                        + "\nYou can't choose the number of disks to move, it will always be 1.\n");
                sleepTime = 500;
                // show the objective:
                printTowerColor(STARTING_PIECES, SOURCE, HELPER, TARGET, ANSI_RED);
                boolean end = false;
                int movesCount = 0;
                //play until the end.
                while(!end) {
                    moveUser();
                    movesCount++;
                    printTower();
                    end = checkEnd();
                }
                int leastMoves =  (int)Math.pow(2, STARTING_PIECES) -1;
                if (movesCount == leastMoves )
                    System.out.println("Congratulations. Done with the least number of moves possible: 2^N_disks - 1 = " + leastMoves);
                else 
                    System.out.println("Can do better, the least number of moves possible is 2^N_disks - 1 = " + leastMoves + ". You made " + movesCount + " moves.");
                break;
        }

    }

    /**
     * Check for the end of the game.
     * The most simple way to check it: if the rules are correctly implemented,
     * at the end of the game the smallest disk as to be on top of the initial
     * TARGET tower.
     * @return true if the game has ended.
     */
    private static boolean checkEnd() {
        return hanoiTower[0][TARGET] == 1;
    }
    
    /**
     * Interface method to present the game, its aim and rules.
     */
    private static void presentation() {
        System.out.println("Welcome to the ascii-hanoi-tower. ");
        System.out.println("The aim of the game is to move N disks from tower " + (SOURCE +1) + " to tower " + (TARGET +1) +", using tower " + (HELPER +1) +" as helper."
                + "\nThere are only two rules:"
                + "\n\tR1: can only move one disk at a time"
                + "\n\tR2: can only move smaller disks on top of bigger ones\n");
    }
    
    /**
     * Method to make a move from the user.
     * Asks for the source and target tower to move one piece from the former to the latter.
     * Inputs are required in the format "s,t" where s and t are integer numbers indicating
     * the number of the source and target tower.
     * Checks if the move is legal and execute it.
     */
    private static void moveUser() {
        Scanner gameScanner = new Scanner(System.in);
        String input;
        boolean correct;
        do {
            System.out.print("Move: ");
            input = gameScanner.next();
            correct = Pattern.matches("[1-3],[1-3]", input);
            if (!correct) System.out.println("Make your moves in the form \"s,t\" where s,t ∈ {1,2,3}.");
        } while (!correct);
        int source = Integer.parseInt(input.split(",")[0]) -1;
        int target = Integer.parseInt(input.split(",")[1]) -1;
        if (legalMove(source,target)) moveOne(source,target);
        else System.out.println("R2: can only move smaller disks on top of bigger ones.");
    }
    
    /**
     * Method to check a move given by the user.
     * Check if the move is legal under the rules of the game.
     * 
     * @param source the number of the source tower (in {0,1,2})
     * @param target the number of the target tower (in {0,1,2})
     * @return true if it is possible to move one disk from source to target
     */
    private static boolean legalMove(int source, int target) {
        int wantToMove = 0;
        int onTopOf = 0;
        // search in the source tower for the disk the user want to move (from above: 0 -> STARTING_PIECES):
        for (int pos = 0; pos < STARTING_PIECES; pos++) {
            if (hanoiTower[pos][source] != 0) {
                // FOUND:
                wantToMove = hanoiTower[pos][source];
                break;
            }
        }
        // search in the target tower for the disk above which the user want to move the disk (from above: 0 -> STARTING_PIECES):
        for (int pos = 0; pos < STARTING_PIECES; pos++) {
            if (hanoiTower[pos][target] != 0) {
                // FOUND:
                onTopOf = hanoiTower[pos][target];
                break;
            }
        }
        if (wantToMove == 0) System.out.println("You want to move disks from empty tower... ?");
        if (source == target) System.out.println("Genius...");

        // if target tower is empty move is legal.
        // if target tower's top disk is greater then source tower's top disk, move is legal
        return onTopOf == 0 || wantToMove <= onTopOf;
    }
    
    /**
     * moveOne is the method to move one disk from a tower to another. Since it
     * does not need the helper to move one disk, this method takes as
     * parameters just the position ( in {0,1,2} ) of the source tower and the
     * target one. It sinks for the first disk (from above) in the source tower
     * and place it in the first free position (from the bottom) of the target
     * tower.
     * Assumes the move is legal, dows not check it.
     * @param source column of the matrix indicating position of the source
     * tower ( in {0,1,2} )
     * @param target column of the matrix indicating position of the target
     * tower ( in {0,1,2} )
     */
    private static void moveOne(int source, int target) {
        // move first disk (from above: 0 -> STARTING_PIECES) of the source tower:
        for (int pos = 0; pos < STARTING_PIECES; pos++) {
            if (hanoiTower[pos][source] != 0) {
                // once found, move to the target tower
                // place it in first free position (from bottom: STARTING_PIECES-1 -> 0).
                for (int posDest = STARTING_PIECES - 1; posDest >= 0; posDest--) {
                    if (hanoiTower[posDest][target] == 0) {
                        hanoiTower[posDest][target] = hanoiTower[pos][source];
                        hanoiTower[pos][source] = 0;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Method to move a certain number of pieces from one tower to another,
     * using the third tower as helper. It is a recursive method that explicitly
     * consider the distinction between base case and other. 
     * When I'm not able to move n disks from source to target, 
     * I decompose the problem into 3 steps: 
     *      1 - move n-1 disks from source to helper, 
     *      2 - then move the remaining one to target,
     *      3 - then move the n-1 disks moved with step 1, from the helper to the target.
     * 
     * Prints in color helper phrases to understand what part of this function is 
     * being executed.
     * 
     * @param disksNumber number of blocks we want to move from source to target
     * @param source column of the matrix indicating position of the source
     * tower ( in {0,1,2} )
     * @param helper column of the matrix indicating position of the helper
     * tower ( in {0,1,2} )
     * @param target column of the matrix indicating position of the target
     * tower ( in {0,1,2} )
     */
    private static void moveEdu(int disksNumber, int source, int helper, int target) {
        if (disksNumber == 1) {
            printTowerColor(1, source, helper, target, ANSI_GREEN);
            // base case: I'm able to solve it with moveOne method.
            System.out.println(ANSI_GREEN + "Base case. Solve this directly." + ANSI_RESET);
            moveOne(source, target);
            printTower();
        } else {
            printTowerColor(disksNumber, source, helper, target, ANSI_RED);
            // otherwise, deocompose the problem into 3 steps:
            System.out.println(ANSI_RED + "Not able to do this. Decompose the problem:" + ANSI_RESET);
            System.out.println("Step 1: move " + (disksNumber - 1) + " disks to helper. First recursion:");
            moveEdu(disksNumber - 1, source, target, helper);
            System.out.println("Step 2: ok, " + (disksNumber - 1) + " disks have been moved to helper. Now I'm able to move one to target.");
            moveEdu(1, source, helper, target);
            System.out.println("Step 3: finally have to move " + (disksNumber - 1) + " disks from the helper to the final target. Second recursion:");
            moveEdu(disksNumber - 1, helper, source, target);
        }
    }

    /**
     * This is an equivalent method to moveEdu. 
     * Moves N blocks from a tower source to another target, using the third as 
     * a helper, decomposing the problem in a recursive way. It is easy to write
     * but its structure doesn't allow to manage the interface as moveEdu.
     * 
     * This version does not explicitly consider the base case, so maybe it could
     * be harder to catch.
     * 
     * @param disksNumber number of blocks we want to move from source to target
     * @param source column of the matrix indicating position of the source
     * tower ( in {0,1,2} )
     * @param helper column of the matrix indicating position of the helper
     * tower ( in {0,1,2} )
     * @param target column of the matrix indicating position of the target
     * tower ( in {0,1,2} )
     */
    private static void move(int disksNumber, int source, int helper, int target) {
        if (disksNumber > 0) {
            printTowerColor(disksNumber, source, helper, target, ANSI_RED);
            move(disksNumber - 1, source, target, helper);
            printTowerColor(1, source, helper, target, ANSI_GREEN);
            moveOne(source, target);
            printTower();
            move(disksNumber - 1, helper, source, target);
        }
    }

    /**
     * The method to initialize the tower.
     * Fill the global matrix hanoiTower with the disks.
     * Disks are represented by integer numbers indicating the size of the disk.
     * 0 size means there is no disk in that position.
     */
    static void init() {
        hanoiTower = new int[STARTING_PIECES][3];
        for (int i = 0; i < STARTING_PIECES; i++) {
            hanoiTower[i][SOURCE] = i + 1;
            hanoiTower[i][HELPER] = 0;
            hanoiTower[i][TARGET] = 0;
        }
    }

    /**
     * Interface method to represent the towers.
     * Print the state of the tower, all in the same color.
     */
    static void printTower() {
        // cime delle torri
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print(" ");
            }
            System.out.print("┌┐");
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print(" ");
            }
        }
        System.out.println("");

        // dischi, se presenti:
        for (int i = 0; i < STARTING_PIECES; i++) {
            for (int j = 0; j < 3; j++) {
                for (int ws = 0; ws < STARTING_PIECES - hanoiTower[i][j]; ws++) {
                    System.out.print(" ");
                }
                for (int p = 0; p < hanoiTower[i][j]; p++) {
                    System.out.print("█");
                }
                if (hanoiTower[i][j] > 0) {
                    System.out.print("██");
                } else {
                    System.out.print("││");
                }
                for (int p = 0; p < hanoiTower[i][j]; p++) {
                    System.out.print("█");
                }
                for (int ws = 0; ws < STARTING_PIECES - hanoiTower[i][j]; ws++) {
                    System.out.print(" ");
                }
            }
            System.out.println("");

        }

        // suolo
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
            System.out.print("┴┴");
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
        }
        System.out.println("");
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
            System.out.print("──");
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
        }
        System.out.println("");

        try {
            sleep(sleepTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(D4mnAsciiHanoiTower.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Interface method to print the towers state and move we'd like to do.
     * It is useful to understand the recursive way of reasoning beyond the game.
     * It prints in a different color the disks we want to move frome the source to target tower.
     * Prints the target tower in the same color of the disks to move.
     * Under each tower it is printed the role of the tower in the current move (source, helper, or target)
     * @param n_pieces
     * @param source
     * @param helper
     * @param target
     * @param color 
     */
    static void printTowerColor(int n_pieces, int source, int helper, int target, String color) {
        System.out.println("move ( " + n_pieces + " pieces , source : " + (source +1) + " , helper : " + (helper+1) + " , target : " + (target+1) + " ) ");
        // cime delle torri
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print(" ");
            }
            System.out.print((j == target ? color + "╔╗" : "┌┐") + ANSI_RESET);
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print(" ");
            }
        }
        System.out.println("");

        // dischi, se presenti:
        int numeroPezziColorati = 0;
        for (int i = 0; i < STARTING_PIECES; i++) {

            for (int j = 0; j < 3; j++) {
                boolean pezzoColorato = (j == source && hanoiTower[i][j] > 0 && numeroPezziColorati < n_pieces);

                for (int ws = 0; ws < STARTING_PIECES - hanoiTower[i][j]; ws++) {
                    System.out.print(" ");
                }
                for (int p = 0; p < hanoiTower[i][j]; p++) {
                    System.out.print((pezzoColorato ? color : "") + "█" + ANSI_RESET);
                }
                if (hanoiTower[i][j] > 0) {
                    System.out.print((pezzoColorato ? color : "") + "██" + ANSI_RESET);
                } else {
                    System.out.print((j == target ? color + "║║" : "││")  + ANSI_RESET);
                }
                for (int p = 0; p < hanoiTower[i][j]; p++) {
                    System.out.print((pezzoColorato ? color : "") + "█" + ANSI_RESET);
                }

                for (int ws = 0; ws < STARTING_PIECES - hanoiTower[i][j]; ws++) {
                    System.out.print(" ");
                }
                if (pezzoColorato) {
                    numeroPezziColorati++;
                }
            }

            System.out.println("");

        }

        // suolo, tutto stesso colore.
        // 1 row
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
            System.out.print((j == target ? color + "╨╨" : "┴┴")  + ANSI_RESET);
            for (int ws = 0; ws < STARTING_PIECES; ws++) {
                System.out.print("─");
            }
        }
        System.out.println("");
        // 2 row
        for (int j = 0; j < 3; j++) {
            for (int ws = 0; ws < STARTING_PIECES - 2; ws++) {
                System.out.print("─");
            }

            System.out.print(j == source ? "SOURCE" : (j == helper ? "HELPER" : "TARGET"));
            for (int ws = 0; ws < STARTING_PIECES - 2; ws++) {
                System.out.print("─");
            }
        }
        System.out.println("");

        try {
            sleep(sleepTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(D4mnAsciiHanoiTower.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method to ask user for inputs. Checks the inputs format and logical
     * validity. First input: PLAY or LEARN Second input: number of starting
     * pieces. Saved in a global variable.
     *
     * @return the first input: PLAY or LEARN
     */
    private static char inputs() {
        Scanner scannerDarkly = new Scanner(System.in);
        char choose;
        boolean chooseOk;
        do {
            System.out.print("Do u want to play (p) or to learn something about recursion (l)? ");
            choose = scannerDarkly.next().toLowerCase().charAt(0);
            chooseOk = (choose == PLAY || choose == LEARN);
            System.out.print(!chooseOk ? "Don't fck with ascii-hanoi-tower. " : "");
        } while (!chooseOk);

        do {
            System.out.print("How many disks (least 3)? ");
            try {
                STARTING_PIECES = scannerDarkly.nextInt();
                chooseOk = (STARTING_PIECES >= 3);
                System.out.print(!chooseOk ? "Don't fck with ascii-hanoi-tower. " : "");
            } catch (InputMismatchException e) {
                String discard = scannerDarkly.next();
                STARTING_PIECES = 0;
                System.out.println("If u can't distinguish numbers from other u should not be here.");
                chooseOk = false;
            }
        } while (!chooseOk);
        return choose;
    }

}
