package othello;


public class AI3
{

    // @formatter:off
    private static final int[][] MATRIX_NEW = {
                                           { 10000, -2000, 500, 200, 200, 500, -2000, 10000 },
                                           { -2000, -2500,  50, 150, 150,  50, -2500, -2000 },
                                           {   500,    50, 250, 100, 100, 250,    50,   500 },
                                           {   200,   150, 100,  50,  50, 100,   150,   200 },
                                           {   200,   150, 100,  50,  50, 100,   150,   200 },
                                           {   500,    50, 250, 100, 100, 250,    50,   500 },
                                           { -2000, -2500,  50, 150, 150,  50, -2500, -2000 },
                                           { 10000, -2000, 500, 200, 200, 500, -2000, 10000 },
    };
    // @formatter:on
    
    // @formatter:off
    private static final int[][] MATRIX = {
                                           { 10000,   5, 500, 200, 200, 500,   5, 10000 },
                                           {     5,   1,  50, 150, 150,  50,   1,     5 },
                                           {   500,  50, 250, 100, 100, 250,  50,   500 },
                                           {   200, 150, 100,  50,  50, 100, 150,   200 },
                                           {   200, 150, 100,  50,  50, 100, 150,   200 },
                                           {   500,  50, 250, 100, 100, 250,  50,   500 },
                                           {     5,   1,  50, 150, 150,  50,   1,     5 },
                                           { 10000,   5, 500, 200, 200, 500,   5, 10000 },
    };
    // @formatter:on


    public static Game move(Game game, int ply)
    {
        if (game.isGameOver()) {
            throw new IllegalArgumentException("game is already over");
        }
        if (ply < 1)
        {
            throw new IllegalArgumentException("ply has to be 1 or higher");
        }
        int correctPly = ply > game.getNumberOfFreeTiles() ? game.getNumberOfFreeTiles() : ply;
        Game best = move(game, correctPly, game.getCurrentPlayer(), 0, true);
        for (int i = 0; i < correctPly - 1; i++)
        {
            best.undo();
        }
        return best;
    }


    private static Game move(Game game, int ply, Player player, int currentPly, boolean max)
    {
        if (currentPly == ply)
        {
            return game;
        }
        else
        {
            Game best = null;
            int bestScore = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for (int x = 0; x < Game.DIM; x++)
            {
                for (int y = 0; y < Game.DIM; y++)
                {
                    Game clone = game.clone();
                    if (clone.put(x, y))
                    {
                        Game current = move(clone, ply, player, currentPly + 1, !max);
                        int currentScore = evaluate(current, player, max);
                        if (max)
                        {
                            if (currentScore >= bestScore)
                            {
                                best = current;
                                bestScore = currentScore;
                            }
                        }
                        else
                        {
                            if (currentScore <= bestScore)
                            {
                                best = current;
                                bestScore = currentScore;
                            }
                        }
                    }
                }
            }
            return best;
        }
    }


    private static int evaluate(Game game, Player player, boolean max)
    {
        if (game.isGameOver())
        {
            return game.getScore(player) > game.getScore(player.getEnemy()) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        else
        {
            int score = 0;
            for (int x = 0; x < Game.DIM; x++) {
                for (int y = 0; y < Game.DIM; y++) {
                    if (game.getTile(x, y) == player) {
                        score += MATRIX[x][y];
                    }
                    if (game.getTile(x, y) == player.getEnemy()) {
                        score -= MATRIX[x][y];
                    }
                }
            }
            return score;
        }
    }

}
