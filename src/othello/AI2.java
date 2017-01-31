package othello;


public class AI2
{

    public Game move(Game game)
    {
        Game best = null;
        int bestScore = Integer.MIN_VALUE;

        for (int x = 0; x < Game.DIM; x++)
        {
            for (int y = 0; y < Game.DIM; y++)
            {
                Game clone = game.clone();
                if (clone.put(x, y))
                {
                    int score = evaluate(clone, game.getCurrentPlayer());
                    if (score > bestScore)
                    {
                        best = clone;
                        bestScore = score;
                    }
                }
            }
        }
        return best;
    }


    private int evaluate(Game game, Player player)
    {
        if (game.isGameOver()) {
            return game.getScore(player) > game.getScore(player.getEnemy()) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        } else {
            return game.getScore(player) - game.getScore(player.getEnemy());
        }
    }

}
