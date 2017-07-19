package othello;

import java.util.concurrent.Semaphore;

public class AI
{
    Semaphore sem = new Semaphore(0);
    
    int threads = 0;
    
    Point best = null;

    int bestScore;

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


    public Game move(Game game, int ply)
    {
        if (game.isGameOver())
        {
            throw new IllegalArgumentException("game is already over");
        }
        if (ply < 1)
        {
            throw new IllegalArgumentException("ply has to be 1 or higher");
        }
        int correctPly = ply > game.getNumberOfFreeTiles() ? game.getNumberOfFreeTiles() : ply;
        Point best = move(game, correctPly, game.getCurrentPlayer());
        game.put(best.x, best.y);
        return game;
    }


    private Point move(Game game, int ply, Player player)
    {
        threads = 0;
        bestScore = Integer.MIN_VALUE;
        for (int x = 0; x < Game.DIM; x++)
        {
            for (int y = 0; y < Game.DIM; y++)
            {
            	final int xCopy = x;
            	final int yCopy = y;
                Game clone = game.clone();
                if (clone.put(x, y))
                {
                    threads++;
                    new Thread(() -> {
                        Game current = move(clone, ply, player.getEnemy(), false, 1);
                        int currentScore = evaluate(current, player, true);
                        if (currentScore >= bestScore)
                        {
                            best = new Point(xCopy, yCopy);
                            bestScore = currentScore;
                        }
                        sem.release();
                    }).start();
                }
            }
        }
        for (int i = 0; i < threads; i++) {
            try
            {
                sem.acquire();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }   
        }
        return best;
    }


    private Game move(Game game, int ply, Player player, boolean max, int currentPly)
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
                        Game current = move(clone, ply, player, !max, currentPly + 1);
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


    private int evaluate(Game game, Player player, boolean max)
    {
        if (game.isGameOver())
        {
            return game.getScore(player) > game.getScore(player.getEnemy()) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        else
        {
            int score = 0;
            for (int x = 0; x < Game.DIM; x++)
            {
                for (int y = 0; y < Game.DIM; y++)
                {
                    if (game.getTile(x, y) == player)
                    {
                        score += MATRIX[x][y];
                    }
                    if (game.getTile(x, y) == player.getEnemy())
                    {
                        score -= MATRIX[x][y];
                    }
                }
            }
            return score;
        }
    }
    
    private class Point {
    	
    	public final int x;
    	
    	public final int y;
    	
    	public Point(int x, int y) {
    		this.x = x;
    		this.y = y;
    	}
    }

}
