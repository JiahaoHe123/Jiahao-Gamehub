package gamehub.snake.model;

public enum SnakeDifficulty {
    EASY("easy", "Easy", 5),
    MEDIUM("medium", "Medium", 8),
    HARD("hard", "Hard", 11),
    NIGHTMARE("nightmare", "Nightmare", 15),
    IMPOSSIBLE("impossible", "Impossible", 20);

    private final String storageKey;
    private final String displayName;
    private final int fps;

    SnakeDifficulty(String storageKey, String displayName, int fps) {
        this.storageKey = storageKey;
        this.displayName = displayName;
        this.fps = fps;
    }

    public String storageKey() {
        return storageKey;
    }

    public String displayName() {
        return displayName;
    }

    public int fps() {
        return fps;
    }

    public static SnakeDifficulty fromDisplayName(String displayName) {
        for (SnakeDifficulty difficulty : values()) {
            if (difficulty.displayName.equals(displayName)) {
                return difficulty;
            }
        }
        return HARD;
    }
}
