package othello;


import static othello.Player.*;

import java.util.stream.IntStream;


/**
 * Represents an Othello game.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Reversi">Wikipedia</a>
 * @author yolo
 */
public class Game
{

    /**
     * Dimension of an othello board.
     */
    public static final int DIM = 8;

    /**
     * Startplayer of othello.
     */
    public static final Player STARTPLAYER = DARK;

    /**
     * The board.<br>
     * Is always a {@link #DIM} * {@link #DIM} board, and never {@code null}.
     */
    private Player[][] board;

    /**
     * Current Player.
     */
    private Player currentPlayer;

    /**
     * Tells if the game is over.
     */
    private boolean gameOver;

    /**
     * The previous baord configuration.
     */
    private Game previous;

    /**
     * The next board configuration.
     */
    private Game next;


    /**
     * Returns a new Game.
     */
    public Game()
    {
        super();
        // board = new Player[DIM][DIM];
        board = IntStream.range(0, DIM).mapToObj(outer -> IntStream.range(0, DIM).mapToObj(inner -> NONE).toArray(Player[]::new)).toArray(
                                                                                                                                          Player[][]::new);
        // for (int x = 0; x < DIM; x++)
        // {
        // for (int y = 0; y < DIM; y++)
        // {
        // board[x][y] = NONE;
        // }
        // }
        board[3][3] = LIGHT;
        board[3][4] = DARK;
        board[4][4] = LIGHT;
        board[4][3] = DARK;
        currentPlayer = STARTPLAYER;
        gameOver = false;
        previous = null;
        next = null;
    }


    /**
     * Returns wether the game is over or not.
     *
     * @return {@code true}, if the game is over
     */
    public boolean isGameOver()
    {
        return gameOver;
    }


    /**
     * Returns the current player.
     *
     * @return current player
     */
    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }


    /**
     * Returns the current enemy player.
     *
     * @return
     *         <ul>
     *         <li>{@link Player#LIGHT}, if {@link #getCurrentPlayer()} returns {@link Player#DARK}</li>
     *         <li>{@link Player#DARK}, if {@link #getCurrentPlayer()} returns {@link Player#LIGHT}</li>
     *         <li>{@link Player#NONE}, if {@link #getCurrentPlayer()} returns {@link Player#NONE}</li>
     *         </ul>
     */
    private Player getEnemyPlayer()
    {
        return currentPlayer.getEnemy();
    }


    /**
     * Puts a piece on the desired location.<br>
     * If the move is not valid, nothing happens.
     *
     * @param x x-coordinate, has to be in range {@code [0,} {@link #DIM} {@code - 1]}
     * @param y y-coordinate, has to be in range {@code [0,} {@link #DIM} {@code - 1]}
     * @return {@code true}, if the move was valid
     * @throws IllegalArgumentException if any argument is illegal
     */
    public boolean put(int x, int y)
    {
        if (!isDimValid(x))
        {
            throw new IllegalArgumentException("x has to be in range [0, " + (DIM - 1) + ']');
        }
        if (!isDimValid(y))
        {
            throw new IllegalArgumentException("y has to be in range [0, " + (DIM - 1) + ']');
        }
        if (!isGameOver() && (board[x][y] == NONE))
        {
            Game clone = clone();
         // @formatter:off
            if (turnFirst(x    , y - 1, Direction.N )
              | turnFirst(x + 1, y - 1, Direction.NE)
              | turnFirst(x + 1, y    , Direction. E)
              | turnFirst(x + 1, y + 1, Direction.SE)
              | turnFirst(x    , y + 1, Direction.S )
              | turnFirst(x - 1, y + 1, Direction.SW)
              | turnFirst(x - 1, y    , Direction. W)
              | turnFirst(x - 1, y - 1, Direction.NW)
               )
         // @formatter:on
            {
                board[x][y] = currentPlayer;
                nextPlayer();
                previous = clone;
                next = null;
                return true;
            }
        }
        return false;
    }


    /**
     * Calculates the next player.<br>
     * Should be applied after each valid turn.
     */
    private void nextPlayer()
    {
        if (isAnyMoveLeft(getEnemyPlayer()))
        {
            currentPlayer = getEnemyPlayer();
        }
        else if (!isAnyMoveLeft(currentPlayer))
        {
            gameOver = true;
        }
    }


    private boolean turnFirst(int x, int y, Direction direction)
    {
        if (isDimValid(x) && isDimValid(y) && (board[x][y] == getEnemyPlayer()) && turnAll(x, y, direction))
        {
            board[x][y] = currentPlayer;
            return true;
        }
        return false;
    }


    /**
     * Returns wether a number is in range {@code [0,} {@link #DIM} {@code - 1]} or not.
     *
     * @param n number to be checked
     * @return {@code true}, if n is in range {@code [0,} {@link #DIM} {@code - 1]}
     */
    private boolean isDimValid(int n)
    {
        return (n >= 0) && (n < DIM);
    }


    private boolean turnAll(int x, int y, Direction direction)
    {
        boolean valid = true;
        boolean done = false;
        int x2;
        int y2;
        int a;

        switch (direction)
        {
            case W:
                x2 = x - 1;
                while (valid && !done && x2 >= 0)
                {
                    if (board[x2][y] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y] == currentPlayer)
                    {
                        done = true;
                    }
                    x2--;
                }
                if (done)
                {
                    for (int x3 = x; x3 > x2; x3--)
                    {
                        board[x3][y] = currentPlayer;
                    }
                }
                break;

            case E:
                x2 = x + 1;
                while (valid && !done && x2 < DIM)
                {
                    if (board[x2][y] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y] == currentPlayer)
                    {
                        done = true;
                    }
                    x2++;
                }
                if (done)
                {
                    for (int x3 = x; x3 < x2; x3++)
                    {
                        board[x3][y] = currentPlayer;
                    }
                }
                break;

            case N:
                y2 = y - 1;
                while (valid && !done && y2 >= 0)
                {
                    if (board[x][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    y2--;
                }
                if (done)
                {
                    for (int y3 = y; y3 > y2; y3--)
                    {
                        board[x][y3] = currentPlayer;
                    }
                }
                break;

            case S:
                y2 = y + 1;
                while (valid && !done && y2 < DIM)
                {
                    if (board[x][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    y2++;
                }
                if (done)
                {
                    for (int y3 = y; y3 < y2; y3++)
                    {
                        board[x][y3] = currentPlayer;
                    }
                }
                break;

            case SE:
                x2 = x + 1;
                y2 = y + 1;
                while (valid && !done && x2 < DIM && y2 < DIM)
                {
                    if (board[x2][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    x2++;
                    y2++;
                }
                if (done)
                {
                    int x3 = x;
                    int y3 = y;
                    while (x3 < x2)
                    {
                        board[x3][y3] = currentPlayer;
                        x3++;
                        y3++;
                    }
                }
                break;

            case NW:
                x2 = x - 1;
                y2 = y - 1;
                while (valid && !done && x2 >= 0 && y2 >= 0)
                {
                    if (board[x2][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    x2--;
                    y2--;
                }
                if (done)
                {
                    int x3 = x;
                    int y3 = y;
                    while (x3 > x2)
                    {
                        board[x3][y3] = currentPlayer;
                        x3--;
                        y3--;
                    }
                }
                break;

            case SW:
                x2 = x - 1;
                y2 = y + 1;
                while (valid && !done && x2 >= 0 && y2 < DIM)
                {
                    if (board[x2][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    x2--;
                    y2++;
                }
                if (done)
                {
                    int x3 = x;
                    int y3 = y;
                    while (x3 > x2)
                    {
                        board[x3][y3] = currentPlayer;
                        x3--;
                        y3++;
                    }
                }
                break;

            case NE:
                x2 = x + 1;
                y2 = y - 1;
                while (valid && !done && x2 < DIM && y2 >= 0)
                {
                    if (board[x2][y2] == NONE)
                    {
                        valid = false;
                    }
                    if (board[x2][y2] == currentPlayer)
                    {
                        done = true;
                    }
                    x2++;
                    y2--;
                }
                if (done)
                {
                    int x3 = x;
                    int y3 = y;
                    while (x3 < x2)
                    {
                        board[x3][y3] = currentPlayer;
                        x3++;
                        y3--;
                    }
                }
                break;

            default:
                throw new IllegalArgumentException();
        }
        return done;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("  a   b   c   d   e   f   g   h\n");
        sb.append("+---+---+---+---+---+---+---+---+\n");
        for (int y = 0; y < DIM; y++)
        {
            for (int x = 0; x < DIM; x++)
            {
                sb.append("| ");
                sb.append(board[x][y]);
                sb.append(' ');
            }
            sb.append("| ");
            sb.append(y + 1);
            sb.append('\n');
            sb.append("+---+---+---+---+---+---+---+---+\n");
        }
        sb.append("currentPlayer = ");
        sb.append(currentPlayer);
        sb.append('\n');
        sb.append("gameOver = ");
        sb.append(gameOver);
        sb.append('\n');
        sb.append("getScore(");
        sb.append(DARK);
        sb.append(") = ");
        sb.append(getScore(DARK));
        sb.append('\n');
        sb.append("getScore(");
        sb.append(LIGHT);
        sb.append(") = ");
        sb.append(getScore(LIGHT));
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Represents all possible directions.
     */
    private enum Direction
    {
        /**
         * North.
         */
        N,

        /**
         * North-East.
         */
        NE,

        /**
         * East.
         */
        E,

        /**
         * South-East.
         */
        SE,

        /**
         * South.
         */
        S,

        /**
         * South-West.
         */
        SW,

        /**
         * West.
         */
        W,

        /**
         * North-West.
         */
        NW;
    }


    /**
     * Returns the owner of a specific tile.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return owner of the specified tile
     * @throws IllegalArgumentException if any argument is illegal
     */
    public Player getTile(int x, int y)
    {
        if (!isDimValid(x))
        {
            throw new IndexOutOfBoundsException("x has to be in range [0, " + (DIM - 1) + ']');
        }
        if (!isDimValid(y))
        {
            throw new IndexOutOfBoundsException("y has to be in range [0, " + (DIM - 1) + ']');
        }
        return board[x][y];
    }


    /**
     * Returns if there is at least one possible move left for a player.
     *
     * @param player player
     * @return {@code true}, if there is at least one possible move left
     */
    private boolean isAnyMoveLeft(Player player)
    {
        for (int x = 0; x < DIM; x++)
        {
            for (int y = 0; y < DIM; y++)
            {
                if (checkPut(x, y, player))
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns wether it's <b>possible</b> to put a piece on the desired location.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param player player
     * @return {@code true}, if it's possible to put a piece
     */
    private boolean checkPut(int x, int y, Player player)
    {
        // @formatter:off
        return board[x][y] == NONE
            && (checkTurnFirst(x    , y - 1, Direction.N , player)
             || checkTurnFirst(x + 1, y - 1, Direction.NE, player)
             || checkTurnFirst(x + 1, y    , Direction. E, player)
             || checkTurnFirst(x + 1, y + 1, Direction.SE, player)
             || checkTurnFirst(x    , y + 1, Direction.S , player)
             || checkTurnFirst(x - 1, y + 1, Direction.SW, player)
             || checkTurnFirst(x - 1, y    , Direction. W, player)
             || checkTurnFirst(x - 1, y - 1, Direction.NW, player)
               );
         // @formatter:on
    }


    private boolean checkTurnFirst(int x, int y, Direction direction, Player player)
    {
        return (isDimValid(x) && isDimValid(y) && (board[x][y] == player.getEnemy()) && checkTurnAll(x, y, direction, player));
    }


    private boolean checkTurnAll(int x, int y, Direction direction, Player player)
    {
        int x2;
        int y2;
        switch (direction)
        {
            case W:
                x2 = x - 1;
                while (x2 >= 0)
                {
                    if (board[x2][y] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y] == player)
                    {
                        return true;
                    }
                    x2--;
                }
                break;

            case E:
                x2 = x + 1;
                while (x2 < DIM)
                {
                    if (board[x2][y] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y] == player)
                    {
                        return true;
                    }
                    x2++;
                }
                break;

            case N:
                y2 = y - 1;
                while (y2 >= 0)
                {
                    if (board[x][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x][y2] == player)
                    {
                        return true;
                    }
                    y2--;
                }
                break;

            case S:
                y2 = y + 1;
                while (y2 < DIM)
                {
                    if (board[x][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x][y2] == player)
                    {
                        return true;
                    }
                    y2++;
                }
                break;

            case SE:
                x2 = x + 1;
                y2 = y + 1;
                while (x2 < DIM && y2 < DIM)
                {
                    if (board[x2][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y2] == player)
                    {
                        return true;
                    }
                    x2++;
                    y2++;
                }
                break;

            case NW:
                x2 = x - 1;
                y2 = y - 1;
                while (x2 >= 0 && y2 >= 0)
                {
                    if (board[x2][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y2] == player)
                    {
                        return true;
                    }
                    x2--;
                    y2--;
                }
                break;

            case SW:
                x2 = x - 1;
                y2 = y + 1;
                while (x2 >= 0 && y2 < DIM)
                {
                    if (board[x2][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y2] == player)
                    {
                        return true;
                    }
                    x2--;
                    y2++;
                }
                break;

            case NE:
                x2 = x + 1;
                y2 = y - 1;
                while (x2 < DIM && y2 >= 0)
                {
                    if (board[x2][y2] == NONE)
                    {
                        return false;
                    }
                    if (board[x2][y2] == player)
                    {
                        return true;
                    }
                    x2++;
                    y2--;
                }
                break;

            default:
                throw new IllegalArgumentException();
        }
        return false;
    }


    /**
     * Returns the current score of a player.<br>
     * Also works for {@link Player#NONE}.
     *
     * @param player player
     * @return
     *         <ul>
     *         <li>current score of the player</li>
     *         <li>{@code 0}, if {@code player} is {@code null}</li>
     *         </ul>
     */
    public int getScore(Player player)
    {
        int score = 0;
        for (int x = 0; x < DIM; x++)
        {
            for (int y = 0; y < DIM; y++)
            {
                if (board[x][y] == player)
                {
                    score++;
                }
            }
        }
        return score;
    }


    @Override
    public Game clone()
    {
        Game clone = new Game();
        clone.board = new Player[DIM][DIM];
        for (int x = 0; x < DIM; x++)
        {
            clone.board[x] = board[x].clone();
        }
        clone.currentPlayer = currentPlayer;
        clone.gameOver = gameOver;
        clone.previous = previous;
        clone.next = next;
        return clone;
    }


    /**
     * Undos the last move.<br>
     * If there is no move to undo, nothing happens.
     *
     * @return {@code true}, if the undo was valid
     */
    public boolean undo()
    {
        if (previous != null)
        {
            next = clone();
            board = previous.board;
            currentPlayer = previous.currentPlayer;
            gameOver = previous.gameOver;
            previous = previous.previous;
            return true;
        }
        return false;
    }


    public boolean isAnyUndoLeft()
    {
        return (previous != null);
    }


    public boolean redo()
    {
        if (next != null)
        {
            previous = clone();
            board = next.board;
            currentPlayer = next.currentPlayer;
            gameOver = next.gameOver;
            next = next.next;
            return true;
        }
        return false;
    }


    public boolean isAnyRedoLeft()
    {
        return (next != null);
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj.getClass() != getClass())
        {
            return false;
        }
        for (int x = 0; x < DIM; x++)
        {
            for (int y = 0; y < DIM; y++)
            {
                if (((Game)obj).board[x][y] != board[x][y])
                {
                    return false;
                }
            }
        }
        if (((Game)obj).currentPlayer != currentPlayer)
        {
            return false;
        }
        if (((Game)obj).gameOver != gameOver)
        {
            return false;
        }
        if (((Game)obj).previous != previous)
        {
            return false;
        }
        if (((Game)obj).next != next)
        {
            return false;
        }
        return true;
    }


    public int getNumberOfFreeTiles()
    {
        int numberOfFreeTiles = 0;
        for (int x = 0; x < DIM; x++)
        {
            for (int y = 0; y < DIM; y++)
            {
                if ((board[x][y] != LIGHT) && (board[x][y] != DARK))
                {
                    numberOfFreeTiles++;
                }
            }
        }
        return numberOfFreeTiles;
    }

}
