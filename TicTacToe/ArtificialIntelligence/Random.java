package ArtificialIntelligence;

import TicTacToe.Board;

/**
 * Plays a random move in Tic Tac Toe.
 */
class Random {

    /**
     * Random cannot be instantiated.
     */
    private Random () {}

    /**
     * Execute the algorithm.
     * @param board     the Tic Tac Toe board to play on
     */
    static void run (Board board) {
        int[] moves = new int[board.getAvailableMoves().size()];
        int index = 0;

        for (Integer item : board.getAvailableMoves()) {
            moves[index++] = item;
        }

        int randomMove = moves[new java.util.Random().nextInt(moves.length)];
        board.move(randomMove);
    }

}
