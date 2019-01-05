package fivebore;

/* Author : Rohit Vincent
 * Play 5-Bore
 */
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

// import packages
import java.util.Scanner;

public class Play {
  // Local Objects
  private Board gameBoard;
  Search search;
  boolean history = false, killer = false;
  static int ply = -1;
  int depth = 4;
  static ArrayList<Tree> normal_moves = new ArrayList<Tree>(),
      killer_moves = new ArrayList<Tree>(), history_moves = new ArrayList<Tree>();
  static Scanner reader = new Scanner(System.in);

  // Initialize Game
  Play() {
    gameBoard = new Board();
    // Enter Depth of Search
    System.out.println("Enter Depth of Search you want:");
    depth = Integer.parseInt(reader.nextLine());
    while (depth < 1) {
      depth = Integer.parseInt(reader.nextLine());
    }
    // Enter choice for History/Search Heuristic
    System.out.println("Do you want History Heuristic?(Enter 1 for yes)");
    try {
      if (Integer.parseInt(reader.nextLine()) == 1) {
        history = killer = true;
        System.out.println("History heuristic set");
      } else {
        System.out.println("No history heuristic set");
      }
    } catch (Exception e) {
      System.out.println("No history heuristic set");
    }
    if (!history) {
      try {
        System.out.println("Do you want Killer Heuristic?(Enter 1 for yes)");
        if (Integer.parseInt(reader.nextLine()) == 1) {
          killer = true;
          System.out.println("Killer heuristic set");
        } else {
          System.out.println("No killer heuristic set");
        }
      } catch (Exception e) {
        System.out.println("No killer heuristic set");
      }
    }
    // Create search object
    search = new Search(killer, history);
    // Print Initial Board
    gameBoard.printBoard();

  }

  // For experimentation
  Play(boolean eval) {
    gameBoard = new Board();
    // Print Initial Board
    gameBoard.printBoard();

  }

  // Play Game
  void playGame() {
    ply = -1;
    while (true) {
      System.out.println("Enter Move You Want To Make:");
      String input = reader.nextLine();
      // Fetch input until valid move is made
      while (!gameBoard.makeMove(input)) {
        input = reader.nextLine();
      }
      // Print Game
      gameBoard.printBoard();
      ply++;
      // Check end of game for Player/ Computer
      if (gameBoard.checkEnd(false) || gameBoard.checkEnd(true)) {
        // Print Static count for the game
        Search.getStaticCount();
        break;
      }
      System.out.println("Computer's Turn..");
      // Get computers turn
      gameBoard.gameTree = gameBoard.generateTree(depth, ply++, -1, search);
      // Print Game
      gameBoard.printBoard();
      // Check end of game for Player/ Computer
      if (gameBoard.checkEnd(false) || gameBoard.checkEnd(true)) {
        // Print Static count for the game
        Search.getStaticCount();
        break;
      }
    }
  }

  // Play Computer vs Computer
  int evaluateGame(int depth, boolean killer, boolean history) {
    ply = -1;
    // Initialize low depth & increase depth for every 2th turn
    search = new Search(killer, history);
    while (true) {
      System.out.println("Your Turn..(Play : " + (ply + 1) + ")");
      gameBoard.gameTree = gameBoard.generateTree(depth, ++ply, 1, search);
      // Print Game
      gameBoard.printBoard();
      // Save game move
      if (!(killer || history)) {
        normal_moves.add(gameBoard.gameTree);
      } else if (history) {
        history_moves.add(gameBoard.gameTree);
      } else if (killer) {
        killer_moves.add(gameBoard.gameTree);
      }
      // Check end of game for Player/ Computer
      if (gameBoard.checkEnd(false) || gameBoard.checkEnd(true)) {
        // Print Static count for the game
        Search.getStaticCount();
        return ply;
      }
      System.out.println("Computer's Turn..(Play : " + (ply + 1) + ")");
      // Get computers turn
      gameBoard.gameTree = gameBoard.generateTree(depth, ++ply, -1, search);
      // Save game move
      if (!(killer || history)) {
        normal_moves.add(gameBoard.gameTree);
      } else if (history) {
        history_moves.add(gameBoard.gameTree);
      } else if (killer) {
        killer_moves.add(gameBoard.gameTree);
      }
      // Print Game
      gameBoard.printBoard();
      // Check end of game for Computer/Player
      if (gameBoard.checkEnd(true) || gameBoard.checkEnd(false)) {
        // Print Static count for the game
        Search.getStaticCount();
        return ply;
      }
    }
  }

  static void printDifference(int play) {
    int norm_kill_count = 0, norm_hist_count = 0, kill_hist_count = 0;
    for (int i = 0; i <= play; i++) {
      if (normal_moves.size() - 1 >= i && killer_moves.size() - 1 >= i) {
        if (!normal_moves.get(i).equals(killer_moves.get(i))) {
          System.out.println("Different move between AlphaBeta & Killer Heuristic at Play:" + i);
          norm_kill_count++;
        }
      } else if (normal_moves.size() - 1 >= i && history_moves.size() - 1 >= i) {
        if (!normal_moves.get(i).equals(history_moves.get(i))) {
          System.out.println("Different move between AlphaBeta & History Heuristic at Play:" + i);
          norm_hist_count++;
        }
      } else if (killer_moves.size() - 1 >= i && history_moves.size() - 1 >= i) {
        if (!killer_moves.get(i).equals(history_moves.get(i))) {
          System.out.println("Different move between History & Killer Heuristic at Play:" + i);
          kill_hist_count++;
        }
      }
    }
    System.out
        .println("Alpha Beta disagrees with Killer Heuristic " + norm_kill_count + " times.");
    System.out
        .println("Alpha Beta disagrees with History Heuristic " + norm_hist_count + " times.");
    System.out.println(
        "History Heuristic disagrees with Killer Heuristic " + kill_hist_count + " times.");
  }

  // Main Function
  public static void main(String args[]) throws FileNotFoundException {
    int play = 0, temp_ply = 0;
    // Systematic Experimentation
    System.out.println("Press 0 to PLAY or 1 to run Experimental Evaluation");
    int input = Integer.parseInt(reader.nextLine());
    if (input == 1) {
      // Adjust Layout
      Board.setLayout = false;
      // Enter Depth
      System.out.println("Enter Depth:");
      int depth = Integer.parseInt(reader.nextLine());
      // Save output to file
      System.out.println("Save output to output.txt?(Enter 1 for yes)");
      input = Integer.parseInt(reader.nextLine());
      System.out.println("Generating output");
      if (input == 1) {
        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        // Set output console to file
        System.setOut(out);
      }
      // For Alpha Beta Game
      System.out.println("Alpha Beta Game");
      // Evaluate without Killer or History Heuristic
      Play evaluate = new Play(true);
      temp_ply = evaluate.evaluateGame(depth, false, false);
      if (temp_ply > play)
        play = temp_ply;
      System.out.println("Alpha Beta + Killer Heuristic Game");
      // Evaluate with Killer Heuristic
      evaluate = new Play(true);
      temp_ply = evaluate.evaluateGame(depth, true, false);
      if (temp_ply > play)
        play = temp_ply;
      System.out.println("Alpha Beta + History Heuristic Game");
      // Evaluate with History Heuristic
      evaluate = new Play(true);
      temp_ply = evaluate.evaluateGame(depth, true, true);
      if (temp_ply > play)
        play = temp_ply;
      // Print differences
      printDifference(play);
      // Reset output
      System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
      System.out.println("Game ended. Output Saved");
    }
    // Game
    else {
      // Adjust Layout
      Board.setLayout = true;
      // Instantiate Game
      System.out.println("FIVE BORE");
      Play game = new Play();
      // Play Game
      game.playGame();

    }

  }

}
