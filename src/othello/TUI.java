package othello;


import java.util.Scanner;


/**
 * A simple TUI for {@link Game}.
 *
 * @author t.krueger
 */
public class TUI
{

    /**
     * Runs the application.
     *
     * @param args unused
     */
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        System.out.println(game);
        while (!game.isGameOver())
        {
            int x = Integer.parseInt(scanner.nextLine());
            int y = Integer.parseInt(scanner.nextLine());
            game.put(x, y);
            System.out.println(game);
        }
        scanner.close();
    }

}
