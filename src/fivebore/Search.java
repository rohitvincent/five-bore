package fivebore;

/* Author : Rohit Vincent
 * Get best move using Alpha-beta with Negamax with Killer & History Heuristic
 * Evaluation function based on board
 */
// import packages
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Class to evaluate a Tree & return True value
class Search {
  // Killer moves/History moves
  static List<Map<Tree, Integer>> killerMoves = new ArrayList<Map<Tree, Integer>>();
  // Flags for Killer/History Heuristic
  boolean killer_flag, history_flag;
  // Static counter of evaluation calls
  static long call_counter;

  // Constructor
  Search(boolean killer_flag, boolean history_flag) {
    // Initialize static counter to zero
    call_counter = 0;
    // Set Heuristic Flags
    this.killer_flag = killer_flag;
    this.history_flag = history_flag;
  }

  // Print static count
  static void getStaticCount() {
    System.out.println("The number of calls to static evaluation were :" + call_counter);
  }

  // Evaluation Function
  static int evaluate(int board, int computer, int player) {
    // Increase count to function
    Search.call_counter++;
    // Check end of game
    if (checkEnd(player, board)) {
      // Set end of game value to 20000
      return 21000;
    }
    // String representation of each board value & pieces
    String piece = String.format("%25s", Integer.toBinaryString(computer)).replace(' ', '0');
    String gameboard = String.format("%25s", Integer.toBinaryString(board)).replace(' ', '0');
    String opp_piece = String.format("%25s", Integer.toBinaryString(player)).replace(' ', '0');
    int other_piece = 0, i = 0, pieces = 2, counter = 0, evaluationValue = 0;
    // For each piece
    for (int index = 0; index < piece.length() && pieces > 0; index++) {
      if (piece.charAt(index) == '1') {
        i = 24 - index;
        // Found a piece
        pieces--;
        // Depth Charge next to position count
        counter = 0;
        // Get only depth charges
        int new_board = board ^ computer;
        new_board = new_board ^ player;
        String board_nw = String.format("%25s", Integer.toBinaryString(new_board)).replace(' ',
            '0');
        // Count of depth charges
        counter = countNextPos(board_nw, i);
        // Surfer at Corner cell
        if (checkCorner(i)) {
          // If no depth charge nearby
          if (counter == 0)
            evaluationValue = evaluationValue + 1000;
          // Reduce value for each depth charge nearby
          else
            evaluationValue = evaluationValue + 1000 - (counter * 500);
        }
        // Surfer at Edge
        else if (checkEdge(i)) {
          // If no depth charge nearby
          if (counter == 0)
            evaluationValue = evaluationValue + 2000;
          // Reduce value for each depth charge nearby
          else
            evaluationValue = evaluationValue + 2000 - (counter * 500);
          // If in middle of board
        } else {
          // If no depth charge nearby
          if (counter == 0)
            evaluationValue = evaluationValue + 4000;
          // Reduce value for each depth charge nearby
          else
            evaluationValue = evaluationValue + 4000 - (counter * 600);
        }
        // Identify second piece of player
        if (pieces == 2) {
          other_piece = i;
        }
        // Current Surfer unable to move next to partner(Using Second Piece)
        if (!((other_piece != 0 && Board.validateMove(i, other_piece - 1, board, computer, false))
            || (other_piece != 24
                && Board.validateMove(i, other_piece + 1, board, computer, false))
            || (other_piece + 5 < 25
                && Board.validateMove(i, other_piece + 5, board, computer, false))
            || (other_piece - 5 >= 0
                && Board.validateMove(i, other_piece - 5, board, computer, false))
            || (other_piece - 4 % 5 != 0 && other_piece - 4 >= 0
                && Board.validateMove(i, other_piece - 4, board, computer, false))
            || (other_piece + 4 % 5 != 4 && other_piece + 4 < 25
                && Board.validateMove(i, other_piece + 4, board, computer, false))
            || (other_piece - 6 >= 0 && (other_piece - 6) % 5 != 4
                && Board.validateMove(i, other_piece - 6, board, computer, false))
            || (other_piece + 6 < 25 && (other_piece + 6) % 5 != 0
                && Board.validateMove(i, other_piece - 6, board, computer, false)))) {
          evaluationValue = evaluationValue - 500;
        }

        // Surfer next to another surfer
        if (checkNextPos(gameboard, i)) {
          // Surfer next to partner
          if (checkNextPos(piece, i)) {
            evaluationValue = evaluationValue + 500;
          }
          // Surfer next to opponent
          else if (checkNextPos(opp_piece, i)) {
            evaluationValue = evaluationValue - 1000;
          }
        }
        // Surfer can move to a cell between two depth charges
        // For each move
        for (int move_nw = 0; move_nw < 25; move_nw++) {
          // If possible move
          if (Board.validateMove(i, move_nw, board, computer, false)) {
            // Get only depth charges
            new_board = board ^ computer;
            new_board = new_board ^ player;
            board_nw = String.format("%25s", Integer.toBinaryString(new_board)).replace(' ', '0');
            // Count of depth charges nearby
            counter = countNextPos(board_nw, move_nw);
            // If it can move between just two depth charges
            if (counter == 2) {
              // Player at Corner
              if (checkCorner(move_nw)) {
                evaluationValue = evaluationValue - 4000;
              }
              // Player at Edge
              else if (checkEdge(move_nw)) {
                evaluationValue = evaluationValue - 2000;
                // Player at any other position on board
              } else {
                evaluationValue = evaluationValue - 500;
              }
            }
            // For more or less than 2 depth charges
            else {
              // Player at Corner
              if (checkCorner(move_nw)) {

              }
              // Player at Edge
              else if (checkEdge(move_nw)) {
                evaluationValue = evaluationValue + 2000 - (counter * 500);
                // Player at any other position on board
              } else {
                evaluationValue = evaluationValue + 4000 - (counter * 600);
              }

            }
          }
        }
      }
    }
    // Return evaluation value
    return evaluationValue;
  }

  // Check if position is next to another filled position
  private static boolean checkNextPos(String gameboard, int i) {
    if ((i != 0 && gameboard.charAt(i - 1) == '1')
        || ((i + 1) % 5 != 0 && gameboard.charAt(i + 1) == '1')
        || (i + 5 < 25 && gameboard.charAt(i + 5) == '1')
        || (i - 5 > 0 && gameboard.charAt(i - 5) == '1')
        || ((i + 4) % 5 != 4 && i + 4 < 25 && gameboard.charAt(i + 4) == '1')
        || ((i - 4) % 5 != 0 && i - 4 >= 0 && gameboard.charAt(i - 4) == '1')
        || ((i + 6) < 25 && (i + 6) % 5 != 0 && gameboard.charAt(i + 6) == '1')
        || ((i - 6) >= 0 && (i - 6) % 5 != 4 && gameboard.charAt(i - 6) == '1')) {
      // Position is next to another position
      return true;
    }
    // Position is not next to another position
    return false;
  }

  // Return count of positions filled next to current position
  private static int countNextPos(String gameboard, int i) {
    int counter = 0;
    if (i != 0 && gameboard.charAt(i - 1) == '1') {
      counter++;
    }
    if ((i + 1) % 5 != 0 && gameboard.charAt(i + 1) == '1') {
      counter++;
    }
    if ((i + 1) % 5 != 0 && gameboard.charAt(i + 1) == '1') {
      counter++;
    }
    if (i + 5 < 25 && gameboard.charAt(i + 5) == '1') {
      counter++;
    }
    if (i - 5 > 0 && gameboard.charAt(i - 5) == '1') {
      counter++;
    }
    if ((i + 4) % 5 != 4 && i + 4 < 25 && gameboard.charAt(i + 4) == '1') {
      counter++;
    }
    if ((i - 4) % 5 != 0 && i - 4 >= 0 && gameboard.charAt(i - 4) == '1') {
      counter++;
    }
    if ((i + 6) < 25 && (i + 6) % 5 != 0 && gameboard.charAt(i + 6) == '1') {
      counter++;
    }
    if ((i - 6) >= 0 && (i - 6) % 5 != 4 && gameboard.charAt(i - 6) == '1') {
      counter++;
    }
    return counter;
  }

  // Return true if position is at corner
  private static boolean checkCorner(int i) {
    if (i == 0 || i == 4 || i == 20 || i == 24) {
      // Corner cell
      return true;
    }
    // Not a corner cell
    return false;
  }

  // Return true if position is at edge
  private static boolean checkEdge(int i) {
    if (i < 4 || i > 20 || i % 5 == 0 || i % 5 == 4) {
      // Edge cell
      return true;
    }
    // Not an edge cell
    return false;
  }

  // Check end of game for temporary move
  private static boolean checkEnd(int player, int board) {
    String piece;
    int count_piece = 0;
    // Check if computer won
    piece = String.format("%25s", Integer.toBinaryString(player)).replace(' ', '0');
    // For each piece
    for (int position = 0; position < piece.length() && count_piece < 0; position++) {
      if (piece.charAt(position) == '1') {
        count_piece++;
        // Get valid moves from position
        ArrayList<Integer> pos_moves = Board.moves.get(24 - position);
        // Check if move is possible
        for (int move = 0; move < pos_moves.size(); move++) {
          if ((pos_moves.get(move) & board) == 0 && pos_moves.get(move) != 0) {
            // Move available
            return false;
          }
        }
      }
    }
    // Game has ended
    return true;
  }

  // Negamax with Alpha-Beta with History & Killer Heuristic flags
  Tree Negamax(Tree gameTree, int depth, int turn, int alpha, int beta, int ply) {
    ArrayList<Tree> moves = null;
    Tree killer = null;
    Map<Tree, Integer> killer_moves = null;
    Tree achievable = gameTree;
    int value = 0;
    // if leaf node
    if (depth <= 0) {
      // Return evaluation value for move
      gameTree.evaluationValue = turn
          * evaluate(gameTree.board, gameTree.player2, gameTree.player1);
      return gameTree;
    }
    // If History Heuristic - depth not required
    if (history_flag)
      ply = 0;
    // Generate moves for position
    moves = generateMove(gameTree.board, gameTree.player2, gameTree.player1, turn);
    // If Killer Heuristic- Make list
    if (killer_flag) {
      while (killerMoves.size() <= ply) {
        killerMoves.add(new HashMap<Tree, Integer>());
      }
      // Get killer moves for current turn
      killer_moves = killerMoves.get(ply);
      if (!killer_moves.isEmpty()) {
        // Get best possible killer
        killer_moves = killer_moves.entrySet().stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors
                .toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, HashMap::new));
        for (Tree killer_move : killer_moves.keySet()) {
          killer = killer_move;
          // Use top killer move
          if (moves.contains(killer)) {
            // Recursive call will killer move
            value = -Negamax(killer, depth - 1, -turn, -beta, -alpha, ply + 1).evaluationValue;
            // Beta cut-off
            if (value > beta) {
              // Increase killer count
              killerMoves.get(ply).put(killer, killerMoves.get(ply).get(killer) + 1);
              return killer;
            }
            // If value is better than achievable
            if (value >= alpha) {
              // Set new achievable
              alpha = value;
              achievable = killer_move;
            }
            // If no cut-off process as normal
            break;
          }
        }
      }
    }
    // For each move
    for (Tree move : moves) {
      // Avoid killer move
      if (this.killer_flag && !killer_moves.isEmpty() && killer != null && killer.equals(move)) {
        continue;
      }
      // Recursive call to negamax
      value = -Negamax(move, depth - 1, -turn, -beta, -alpha, ply + 1).evaluationValue;
      // Beta cut-off
      if (value > beta) {
        // Add killer move
        if (killer_flag) {
          // Increase count if already exists
          if (killerMoves.get(ply).containsKey(move)) {
            killerMoves.get(ply).put(move, killerMoves.get(ply).get(move) + 1);
          } else {
            killerMoves.get(ply).put(move, 1);
          }
        }
        // Return best move
        return move;
      }
      // If value is better than achievable
      if (value > alpha) {
        // Set new achievable
        alpha = value;
        achievable = move;
      }
    }
    // Return best achievable move
    return achievable;
  }

  // Generate moves for current position of player2
  static ArrayList<Tree> generateMove(int board, int player2, int player1, int turn) {
    int position, piece_count = 0;
    String player;
    ArrayList<Tree> moves = new ArrayList<>();
    // You
    if (turn == -1) {
      player = String.format("%25s", Integer.toBinaryString(player2)).replace(' ', '0');
    } else {
      player = String.format("%25s", Integer.toBinaryString(player1)).replace(' ', '0');
    }
    // For each piece
    for (int index = 0; index < player.length() && piece_count < 2; index++) {
      if (player.charAt(index) == '1') {
        piece_count++;
        position = 24 - index;
        // Get moves
        for (int move = 0; move < 25; move++) {
          // Validate the moves
          // Get moves for computer
          if (turn == -1)
            moves.addAll(getMove(position, move, board, player2, player1, player2, false));
          // Get moves for player1
          else if (turn == 1)
            moves.addAll(getMove(position, move, board, player2, player1, player1, true));

        }
      }
    }
    return moves;
  }

  // Validate & Generate each arrow move
  static ArrayList<Tree> getMove(int position, int move, int board, int player2, int player1,
      int curr_player, boolean turn) {
    ArrayList<Tree> moves = new ArrayList<>();
    if (Board.validateMove(position, move, board, curr_player, false)) {
      for (int arrow = 0; arrow < 25; arrow++) {
        board &= ~(1l << position);
        board |= (1l << move);
        curr_player |= (1l << move);
        curr_player &= ~(1l << position);
        // Validate charge
        if (Board.validateCharge(move, arrow, board, false)) {
          board = Board.setCharge(arrow, board);
          if (turn)
            moves.add(new Tree(board, player2, curr_player));
          else
            moves.add(new Tree(board, curr_player, player1));
          board = Board.unsetCharge(arrow, board);
        }
        board &= ~(1l << move);
        board |= (1l << position);
        curr_player |= (1l << position);
        curr_player &= ~(1l << move);
      }
    }
    return moves;
  }
}
