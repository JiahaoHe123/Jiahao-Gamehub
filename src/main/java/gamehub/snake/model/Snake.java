package gamehub.snake.model;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Snake {
    private final Deque<Point> body = new ArrayDeque<>();
    private Direction direction = Direction.RIGHT;
    private Direction pendingDirection = Direction.RIGHT;

    public void reset(int centerX, int centerY) {
        body.clear();
        body.addFirst(new Point(centerX, centerY));
        body.addLast(new Point(centerX - 1, centerY));
        body.addLast(new Point(centerX - 2, centerY));
        direction = Direction.RIGHT;
        pendingDirection = Direction.RIGHT;
    }

    public void queueDirection(Direction proposed) {
        if (!proposed.isOpposite(direction)) {
            pendingDirection = proposed;
        }
    }

    public Point computeNextHead() {
        direction = pendingDirection;
        Point head = body.peekFirst();
        return new Point(head.x + direction.dx(), head.y + direction.dy());
    }

    public void moveTo(Point nextHead, boolean grows) {
        body.addFirst(nextHead);
        if (!grows) {
            body.removeLast();
        }
    }

    public boolean collidesAt(Point nextHead, boolean grows) {
        Set<Point> occupied = new HashSet<>(body);
        if (!grows) {
            occupied.remove(body.peekLast());
        }
        return occupied.contains(nextHead);
    }

    public Point head() {
        return body.peekFirst();
    }

    public Deque<Point> body() {
        return body;
    }

    public int size() {
        return body.size();
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getPendingDirection() {
        return pendingDirection;
    }
}
