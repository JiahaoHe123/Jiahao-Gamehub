package gamehub.snake.controller;

import java.awt.Point;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

import gamehub.snake.model.Direction;
import gamehub.snake.model.GameState;
import gamehub.snake.model.Snake;

public class SnakeGameController {
    private final int boardWidth;
    private final int boardHeight;
    private final int fps;
    private final int countdownSeconds;
    private final Runnable repaintCallback;

    private final Timer timer;
    private final Random random = new Random();
    private final Snake snake = new Snake();

    private Point food;
    private int score;
    private int bestScore;
    private GameState gameState = GameState.COUNTDOWN;
    private long countdownEndTimeMillis;

    public SnakeGameController(
        int boardWidth,
        int boardHeight,
        int fps,
        int countdownSeconds,
        Runnable repaintCallback
    ) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.fps = fps;
        this.countdownSeconds = countdownSeconds;
        this.repaintCallback = repaintCallback;

        resetGame();
        startCountdown();

        timer = new Timer(1000 / fps, event -> onTick());
        timer.start();
    }

    public void startNewGameWithCountdown() {
        resetGame();
        startCountdown();
        repaintCallback.run();
    }

    public void restartIfGameOver() {
        if (gameState == GameState.GAME_OVER) {
            startNewGameWithCountdown();
        }
    }

    public void queueDirection(Direction direction) {
        snake.queueDirection(direction);
    }

    private void startCountdown() {
        gameState = GameState.COUNTDOWN;
        countdownEndTimeMillis =
            System.currentTimeMillis() + countdownSeconds * 1000L;
    }

    private void resetGame() {
        snake.reset(boardWidth / 2, boardHeight / 2);
        score = 0;
        food = spawnFood();
        gameState = GameState.PLAYING;
    }

    private void onTick() {
        updateGame();
        repaintCallback.run();
    }

    private void updateGame() {
        if (gameState == GameState.COUNTDOWN) {
            if (System.currentTimeMillis() >= countdownEndTimeMillis) {
                gameState = GameState.PLAYING;
            }
            return;
        }

        if (gameState == GameState.GAME_OVER) {
            return;
        }

        Point next = snake.computeNextHead();
        if (!isInside(next) || snake.collidesAt(next, next.equals(food))) {
            gameState = GameState.GAME_OVER;
            return;
        }

        boolean grows = next.equals(food);
        snake.moveTo(next, grows);

        if (grows) {
            score++;
            bestScore = Math.max(bestScore, score);
            food = spawnFood();
        }
    }

    private boolean isInside(Point point) {
        return point.x >= 0
            && point.x < boardWidth
            && point.y >= 0
            && point.y < boardHeight;
    }

    private Point spawnFood() {
        Set<Point> occupied = Set.copyOf(snake.body());
        while (true) {
            Point candidate = new Point(
                random.nextInt(boardWidth),
                random.nextInt(boardHeight)
            );
            if (!occupied.contains(candidate)) {
                return candidate;
            }
        }
    }

    public Snake getSnake() {
        return snake;
    }

    public Point getFood() {
        return food;
    }

    public int getScore() {
        return score;
    }

    public int getBestScore() {
        return bestScore;
    }

    public GameState getGameState() {
        return gameState;
    }

    public long getCountdownEndTimeMillis() {
        return countdownEndTimeMillis;
    }

    public int getCountdownSeconds() {
        return countdownSeconds;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getFps() {
        return fps;
    }
}