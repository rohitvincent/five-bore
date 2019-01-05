package fivebore;

/* Author : Rohit Vincent
 * Board containing values representing state of board
 */
import java.util.ArrayList;

class Board {

  Tree gameTree;
  static boolean setLayout;
  static ArrayList<ArrayList<Integer>> moves;

  // Set player piece to new_pos from last_pos
  private void setplayer(int last_pos, int new_pos, int turn) {
    // Set board to reflect new position
    gameTree.board &= ~(1l << last_pos);
    gameTree.board |= (1l << new_pos);
    // If Player
    if (turn == -1) {
      gameTree.player1 &= ~(1l << last_pos);
      gameTree.player1 |= (1l << new_pos);
    }
    // If Computer
    else {
      gameTree.player2 &= ~(1l << last_pos);
      gameTree.player2 |= (1l << new_pos);
    }
  }

  // Set Player pieces
  private void setPlayers() {
    // Set board & Player players initially
    // Computer
    this.setplayer(0, 9, 1);
    this.setplayer(0, 23, 1);
    // Player 1
    this.setplayer(0, 1, -1);
    this.setplayer(0, 15, -1);
  }

  // Fetch all valid moves
  private static ArrayList<ArrayList<Integer>> fetchAllMoves() {
    // Create array of moves
    ArrayList<ArrayList<Integer>> moves = new ArrayList<ArrayList<Integer>>();
    // For each position generate valid moves & add to list
    for (int i = 0; i < 25; i++) {
      moves.add(calculateMoves(i));
    }
    return moves;
  }

  // Generate valid moves for a position
  private static ArrayList<Integer> calculateMoves(int position) {
    ArrayList<Integer> moves = new ArrayList<Integer>();
    int valid_moves[][] = new int[8][5];
    int j = 0;
    // Moves towards left of position
    for (int i = position + 1; i % 5 != 0; i++) {
      if (j != 0) {
        valid_moves[0][j] = valid_moves[0][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[0][j] |= (1 << i);
      moves.add(valid_moves[0][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards up of position
    j = 0;
    for (int i = position + 5; i < 25; i = i + 5) {
      if (j != 0) {
        valid_moves[1][j] = valid_moves[1][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[1][j] |= (1 << i);
      moves.add(valid_moves[1][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards right of position
    j = 0;
    for (int i = position - 1; i % 5 != 4 && i >= 0; i--) {
      if (j != 0) {
        valid_moves[2][j] = valid_moves[2][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[2][j] |= (1 << i);
      moves.add(valid_moves[2][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards down of position
    j = 0;
    for (int i = position - 5; i >= 0; i = i - 5) {
      if (j != 0) {
        valid_moves[3][j] = valid_moves[3][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[3][j] |= (1 << i);
      moves.add(valid_moves[3][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards bottom-left of position
    j = 0;
    for (int i = position - 4; i >= 0 && i % 5 != 0; i = i - 4) {
      if (j != 0) {
        valid_moves[4][j] = valid_moves[4][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[4][j] |= (1 << i);
      moves.add(valid_moves[4][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards top-left of position
    j = 0;
    for (int i = position + 6; i < 25 && i % 5 != 0; i = i + 6) {
      if (j != 0) {
        valid_moves[5][j] = valid_moves[5][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[5][j] |= (1 << i);
      moves.add(valid_moves[5][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    j = 0;
    // Moves towards top-right of position
    for (int i = position + 4; (i < 25 && i % 5 != 4); i = i + 4) {
      if (j != 0) {
        valid_moves[6][j] = valid_moves[6][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[6][j] |= (1 << i);
      moves.add(valid_moves[6][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Moves towards bottom-right of position
    j = 0;
    for (int i = position - 6; i >= 0 && i % 5 != 4; i = i - 6) {
      if (j != 0) {
        valid_moves[7][j] = valid_moves[7][j - 1];
      }
      // Set bit for current & previous move
      valid_moves[7][j] |= (1 << i);
      moves.add(valid_moves[7][j]);
      // Limit up to 3 moves
      if (++j == 3)
        break;
    }
    // Return all the moves
    return moves;
  }

  Board() {
    // Initialize empty board
    gameTree = new Tree(0, 0, 0);
    // Update board with players
    setPlayers();
    // If moves not already calculated
    if (moves == null) {
      // Fetch All the moves for each position
      moves = fetchAllMoves();
    }

  }

  // Convert alphabet position to numeric
  private static int getColumn(char column) {
    switch (column) {
    case 'a':
      return 4;
    case 'b':
      return 3;
    case 'c':
      return 2;
    case 'd':
      return 1;
    case 'e':
      return 0;
    default:
      return -1;
    }
  }

  // Set Depth Charge to position on board
  static int setCharge(int arrow, int board) {
    return board |= (1l << arrow);
  }

  // Clear Depth Charge at position on board
  static int unsetCharge(int arrow, int board) {
    return board &= ~(1l << arrow);
  }

  // Check end of game
  boolean checkEnd(boolean turn) {
    String piece, output;
    int piece_count = 0;
    // Check if computer won
    if (turn) {
      piece = String.format("%25s", Integer.toBinaryString(gameTree.player1)).replace(' ', '0');
      output = "The Computer Won!";
    }
    // Check if you won
    else {
      piece = String.format("%25s", Integer.toBinaryString(gameTree.player2)).replace(' ', '0');
      output = "You Won!";
    }
    // For each piece
    for (int position = 0; position < piece.length() && piece_count < 2; position++) {
      if (piece.charAt(position) == '1') {
        piece_count++;
        // Get valid moves from position
        ArrayList<Integer> pos_moves = moves.get(24 - position);
        // Check if move is possible
        for (int move = 0; move < pos_moves.size(); move++) {
          if ((pos_moves.get(move) & gameTree.board) == 0 && pos_moves.get(move) != 0) {
            // Move available
            return false;
          }
        }
      }
    }
    System.out.println(output);
    // Game has ended
    return true;
  }

  // Validate move from old position to new position
  static boolean validateMove(int old_position, int new_position, int board, int player1,
      boolean output) {
    // Validate move
    if ((player1 & (1L << old_position)) == 0) {
      if (output) {
        System.out.println("There is no player to surf on that position");
      }
      // Invalid move
      return false;
    }
    // For all moves from current position
    ArrayList<Integer> old_moves = moves.get(old_position);
    // For each move
    for (int move = 0; move < old_moves.size(); move++) {
      // Check if move collides
      if ((old_moves.get(move) & (1 << new_position)) != 0) {
        if ((old_moves.get(move) & board) != 0) {
          if (output) {
            System.out.println("Surfer is blocked, Try Again!");
          }
          // Invalid move
          return false;
        }
        // Surfer Moved
        return true;
      }
    }
    // If invalid move
    if (output) {
      System.out.println("Surfer cannot move this way! Renter the move.");
    }
    return false;
  }

  // Validate depth charge from position
  static boolean validateCharge(int position, int arrow, int board, boolean output) {
    // Get possible positions of arrow
    ArrayList<Integer> new_moves = moves.get(position);
    // For each move
    for (int move = 0; move < new_moves.size(); move++) {
      // Check if move is possible
      if ((new_moves.get(move) & (1 << arrow)) != 0) {
        // If move is blocked
        if ((new_moves.get(move) & board) != 0) {
          if (output) {
            System.out.println("Depth Charge is blocked, Try Again!");
          }
          // Invalid depth charge
          return false;
        }
        // Place charge
        if (output) {
          System.out.println("Surfer has moved & Depth Charge Armed!");
        }
        return true;
      }
    }
    // Invalid move
    if (output) {
      System.out.println("Depth Charge cannot be fired this way! Renter the move.");
    }
    return false;
  }

  // Make move from input
  boolean makeMove(String inputs) {
    // Split input
    String moves[] = inputs.trim().toLowerCase().split(" ", 3);
    int bit_position = 0, new_position = 0, arrow = 0;
    // Check if input is correct
    if (moves.length != 3) {
      System.out.println("Invalid Input");
      return false;
    }
    try {
      // For each input
      for (int input = 0; input < 3; input++) {
        // Get column value
        int column = getColumn(moves[input].charAt(0));
        if (column == -1) {
          System.out.println("Invalid Column Input");
          return false;
        }
        int row = Integer.parseInt("" + moves[input].charAt(1));
        if (row > 5 || 1 > row) {
          System.out.println("Invalid Row Input");
          return false;
        }
        // Get inputs
        switch (input) {
        // Current piece
        case 0:
          bit_position = ((row - 1) * 5) + column;
          continue;
        // New Position
        case 1:
          new_position = ((row - 1) * 5) + column;
          continue;
        // Arrow
        case 2:
          arrow = ((row - 1) * 5) + column;
          continue;
        }
      }
    } catch (Exception ex) {
      System.out.println("Invalid Input");
      return false;
    }
    // Check if move is valid
    if (validateMove(bit_position, new_position, gameTree.board, gameTree.player1, true)) {
      // Set the player to new position
      setplayer(bit_position, new_position, -1);
      // Check if charge can be placed
      if (validateCharge(new_position, arrow, gameTree.board, true)) {
        // place charge
        gameTree.board = setCharge(arrow, gameTree.board);
        // Move made
        return true;
      } else {
        // Reverse move which was made
        setplayer(new_position, bit_position, -1);
        return false;
      }
    }
    // If move was invalid
    return false;
  }

  // print the board
  void printBoard() {
    // Get string representation of each piece & board
    String output = String.format("%25s", Integer.toBinaryString(gameTree.board)).replace(' ',
        '0');
    String player1 = String.format("%25s", Integer.toBinaryString(gameTree.player1)).replace(' ',
        '0');
    String opponent = String.format("%25s", Integer.toBinaryString(gameTree.player2)).replace(' ',
        '0');
    int j = 32;
    // Print board
    System.out.println("   a   b   c   d   e");
    // Do not print to file
    if (Board.setLayout) {
      while (j > 0) {
        System.out.print("_");
        j--;
      }
    }
    System.out.println("");
    for (int i = 1; i < 26; i++) {
      if (i % 5 == 1) {
        System.out.print((5 - i / 5) + "\u2551");
      }
      if (output.charAt(i - 1) == '0') {
        System.out.print(" \u2591 |");
      } else if (player1.charAt(i - 1) == '1') {
        System.out.print(" # |");
      } else if (opponent.charAt(i - 1) == '1') {
        System.out.print(" O |");
      } else {
        System.out.print(" X |");
      }
      if (i % 5 == 0) {
        System.out.print("\u2551" + (6 - i / 5) + "\n");
        j = 0;
        // Do not print to file
        if (Board.setLayout) {
          while (j < 25) {
            System.out.print("_");
            j++;
          }
        }
        System.out.println(" ");
      }

    }
    System.out.println("   a   b   c   d   e");
  }

  // Generate tree of depth for Play number with search config
  Tree generateTree(int depth, int ply, int turn, Search search) {
    return search.Negamax(gameTree, depth, turn, -21000, 21000, ply);
  }
}
