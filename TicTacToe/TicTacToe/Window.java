package TicTacToe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import ArtificialIntelligence.*;

public class Window extends JFrame {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;

    private Board board;
    private Panel panel;
    private BufferedImage imageBackground, imageX, imageO;

    private enum Mode {Player, AI}
    private Mode mode;

    /**
     * The center location of each of the cells is stored here.
     * Used for identifying which cell the player has clicked on.
     */
    private Point[] cells;

    /**
     * The distance away from the center of a cell that will register
     * as a click.
     */
    private static final int DISTANCE = 100;

    /**
     * Construct the Window.
     */
    private Window () {
        this(Mode.AI);
    }

    /**
     * Construct the Window.
     * @param mode      the game mode (Player vs. Player or Player vs. AI)
     */
    private Window (Mode mode) {
        this.mode = mode;
        board = new Board();
        loadCells();
        panel = createPanel();
        setWindowProperties();
        loadImages();
    }

    /**
     * Load the locations of the center of each of the cells.
     */
    private void loadCells () {
        cells = new Point[9];

        cells[0] = new Point(109, 109);
        cells[1] = new Point(299, 109);
        cells[2] = new Point(489, 109);
        cells[3] = new Point(109, 299);
        cells[4] = new Point(299, 299);
        cells[5] = new Point(489, 299);
        cells[6] = new Point(109, 489);
        cells[7] = new Point(299, 489);
        cells[8] = new Point(489, 489);
    }

    /**
     * Set the size, title, visibility etc...
     */
    private void setWindowProperties () {
        setResizable(false);
        pack();
        setTitle("Lazo's Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Create the panel that will be used for drawing Tic Tac Toe to the screen.
     * @return      the panel with the specified dimensions and mouse listener
     */
    private Panel createPanel () {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.addMouseListener(new MyMouseAdapter());
        return panel;
    }

    /**
     * Load the image of the background and the images of the X and O
     */
    private void loadImages () {
        imageBackground = getImage("background");
        imageX = getImage("x");
        imageO = getImage("o");
    }

    /**
     * Helper method for grabbing the images from the disk.
     * @param path      the name of the image
     * @return          the image that was grabbed
     */
    private static BufferedImage getImage (String path) {

        BufferedImage image;

        try {
            path = ".." + File.separator + "Assets" + File.separator + path + ".png";
            image = ImageIO.read(Window.class.getResource(path));
        } catch (IOException ex) {
            throw new RuntimeException("Image could not be loaded.");
        }

        return image;
    }

    /**
     * Used for drawing Tic Tac Toe to the screen.
     */
    private class Panel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintTicTacToe((Graphics2D) g);
        }

        /**
         * The main painting method that paints everything.
         * @param g     the Graphics object that will perform the panting
         */
        private void paintTicTacToe (Graphics2D g) {
            setProperties(g);
            paintBoard(g);
            paintWinner(g);
        }

        /**
         * Set the rendering hints of the Graphics object.
         * @param g     the Graphics object to set the rendering hints on
         */
        private void setProperties (Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(imageBackground, 0, 0, null);

            // The first time a string is drawn it tends to lag.
            // Drawing something trivial at the beginning loads the font up.
            // Better to lag at the beginning than during the middle of the game.
            g.drawString("", 0, 0);
        }

        /**
         * Paints the background image and the X's and O's.
         * @param g     the Graphics object that will perform the panting
         */
        private void paintBoard (Graphics2D g) {
            Board.State[][] boardArray = board.toArray();

            int offset = 20;

            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    if (boardArray[y][x] == Board.State.X) {
                        g.drawImage(imageX, offset + 190 * x, offset + 190 * y, null);
                    } else if (boardArray[y][x] == Board.State.O) {
                        g.drawImage(imageO, offset + 190 * x, offset + 190 * y, null);
                    }
                }
            }
        }

        /**
         * Paints who won to the screen.
         * @param g     the Graphics object that will perform the panting
         */
        private void paintWinner (Graphics2D g) {
            if (board.isGameOver()) {
                g.setColor(new Color(255, 255, 255));
                g.setFont(new Font("TimesRoman", Font.PLAIN, 50));

                String s;

                if (board.getWinner() == Board.State.Blank) {
                    s = "Draw";
                } else {
                    s = board.getWinner() + " Wins!";
                }

                g.drawString(s, 300 - getFontMetrics(g.getFont()).stringWidth(s)/2, 315);

            }
        }
    }

    /**
     * For detecting mouse clicks.
     */
    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mouseClicked(e);

            if (board.isGameOver()) {
                board.reset();
                panel.repaint();
            } else {
                playMove(e);
            }

        }

        /**
         * Plays the move that the user clicks, if the move is valid.
         * @param e     the MouseEvent that the user performed
         */
        private void playMove (MouseEvent e) {
            int move = getMove(e.getPoint());

            if (!board.isGameOver() && move != -1) {
                boolean validMove = board.move(move);
                if (mode == Mode.AI && validMove && !board.isGameOver()) {
                    Algorithms.alphaBetaAdvanced(board);
                }
                panel.repaint();
            }
        }

        /**
         * Translate the mouse click position to an index on the board.
         * @param point     the location of where the player pressed the mouse
         * @return          the index on the Tic Tac Toe board (-1 if invalid click)
         */
        private int getMove (Point point) {
            for (int i = 0; i < cells.length; i++) {
                if (distance(cells[i], point) <= DISTANCE) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Distance between two points. Used for determining if the player has pressed
         * on a cell to play a move.
         * @param p1    the first point (intended to be the location of the cell)
         * @param p2    the second point (intended to be the location of the mouse click)
         * @return      the distance between the two points
         */
        private double distance (Point p1, Point p2) {
            double xDiff = p1.getX() - p2.getX();
            double yDiff = p1.getY() - p2.getY();

            double xDiffSquared = xDiff*xDiff;
            double yDiffSquared = yDiff*yDiff;

            return Math.sqrt(xDiffSquared+yDiffSquared);
        }
    }

    public static void main(String[] args) {

        if (args.length == 1) {
            System.out.println("Game Mode: Player vs. Player");
            SwingUtilities.invokeLater(() -> new Window(Mode.Player));
        } else {
            System.out.println("Game Mode: Player vs. AI");
            SwingUtilities.invokeLater(() -> new Window(Mode.AI));
        }

    }

}
