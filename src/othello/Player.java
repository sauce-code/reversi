package othello;


/**
 * All Players.
 *
 * @author t.krueger
 */
public enum Player
{
    /**
     * Light.
     */
    LIGHT,

    /**
     * Dark.
     */
    DARK,

    /**
     * None.
     */
    NONE;

    /**
     * Returns the enemy Player.
     *
     * @return
     *         <ul>
     *         <li>{@link #DARK}, if {@code this} is {@link #LIGHT}</li>
     *         <li>{@link #LIGHT}, if {@code this} is {@link #DARK}</li>
     *         <li>{@link #NONE}, if {@code this} is {@link #NONE}</li>
     *         <li>{@link IllegalArgumentExceptionl}, else</li>
     *         </ul>
     */
    public Player getEnemy()
    {
        switch (this)
        {
            case LIGHT:
                return DARK;
            case DARK:
                return LIGHT;
            case NONE:
                return NONE;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Override
    public String toString()
    {
        switch (this)
        {
            case LIGHT:
                return "O";
            case DARK:
                return "X";
            case NONE:
                return " ";
            default:
                throw new IllegalArgumentException();
        }
    }

}
