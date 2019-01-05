package fivebore;

/* Author : Rohit Vincent
 * Tree Structure Representing a Move on the board
 */

class Tree {
  // Attributes of tree
  int evaluationValue = 0;
  int board, player2, player1;

  // initialize root node
  public Tree(int board, int player2, int player1) {
    this.board = board;
    this.player2 = player2;
    this.player1 = player1;
  }

  // Following inbuilt methods are overwritten to ensure Map of(Tree,Key) gives
  // equality for killer moves
  @Override
  public boolean equals(Object o) {
    if (((Tree) o).board == board && ((Tree) o).player1 == player1
        && ((Tree) o).player2 == player2) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return board;
  }
}
