package ArtificialIntelligence;

import TicTacToe.Board;

/**
 * Uses the Alpha-Beta Pruning algorithm to play a move in a game of Tic Tac Toe.
 */
class AlphaBetaPruning {

    private static double maxPly;

    /**
     * AlphaBetaPruning cannot be instantiated.
     */
    private AlphaBetaPruning () {}

    /**
     * Execute the algorithm.
     * @param player        the player that the AI will identify as
     * @param board         the Tic Tac Toe board to play on
     * @param maxPly        the maximum depth
     */
    static void run (Board.State player, Board board, double maxPly) {
        if (maxPly < 1) {
            throw new IllegalArgumentException("Maximum depth must be greater than 0.");
        }

        AlphaBetaPruning.maxPly = maxPly;
        alphaBetaPruning(player, board, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
    }

    /**
     * The meat of the algorithm.
     * @param player        the player that the AI will identify as
     * @param board         the Tic Tac Toe board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int alphaBetaPruning (Board.State player, Board board, double alpha, double beta, int currentPly) {
        if (currentPly++ == maxPly || board.isGameOver()) {
            return score(player, board);
        }

        if (board.getTurn() == player) {
            return getMax(player, board, alpha, beta, currentPly);
        } else {
            return getMin(player, board, alpha, beta, currentPly);
        }
    }

    /**
     * Play the move with the highest score.
     * @param player        the player that the AI will identify as
     * @param board         the Tic Tac Toe board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMax (Board.State player, Board board, double alpha, double beta, int currentPly) {
        int indexOfBestMove = -1;

        for (Integer theMove : board.getAvailableMoves()) {

            Board modifiedBoard = board.getDeepCopy();
            modifiedBoard.move(theMove);
            int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);

            if (score > alpha) {
                alpha = score;
                indexOfBestMove = theMove;
            }

            // Pruning.
            if (alpha >= beta) {
                break;
            }
        }

        if (indexOfBestMove != -1) {
            board.move(indexOfBestMove);
        }
        return (int)alpha;
    }

    /**
     * Play the move with the lowest score.
     * @param player        the player that the AI will identify as
     * @param board         the Tic Tac Toe board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMin (Board.State player, Board board, double alpha, double beta, int currentPly) {
        int indexOfBestMove = -1;

        for (Integer theMove : board.getAvailableMoves()) {

            Board modifiedBoard = board.getDeepCopy();
            modifiedBoard.move(theMove);

            int score = alphaBetaPruning(player, modifiedBoard, alpha, beta, currentPly);

            if (score < beta) {
                beta = score;
                indexOfBestMove = theMove;
            }

            // Pruning.
            if (alpha >= beta) {
                break;
            }
        }

        if (indexOfBestMove != -1) {
            board.move(indexOfBestMove);
        }
        return (int)beta;
    }

    /**
     * Get the score of the board.
     * @param player        the play that the AI will identify as
     * @param board         the Tic Tac Toe board to play on
     * @return              the score of the board
     */
    private static int score (Board.State player, Board board) {
        if (player == Board.State.Blank) {
            throw new IllegalArgumentException("Player must be X or O.");
        }

        Board.State opponent = (player == Board.State.X) ? Board.State.O : Board.State.X;

        if (board.isGameOver() && board.getWinner() == player) {
            return 10;
        } else if (board.isGameOver() && board.getWinner() == opponent) {
            return -10;
        } else {
            return 0;
        }
    }

}
