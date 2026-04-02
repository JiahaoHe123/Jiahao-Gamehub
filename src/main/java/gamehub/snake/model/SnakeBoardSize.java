package gamehub.snake.model;

public enum SnakeBoardSize {
    SMALL("small", "Small", 20, 16),
    MEDIUM("medium", "Medium", 25, 20),
    LARGE("large", "Large", 32, 24);

    private final String storageKey;
    private final String displayName;
    private final int width;
    private final int height;

    SnakeBoardSize(String storageKey, String displayName, int width, int height) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.width = width;
        this.height = height;
    }

    public String storageKey() {
        return storageKey;
    }

    public String displayName() {
        return displayName;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
