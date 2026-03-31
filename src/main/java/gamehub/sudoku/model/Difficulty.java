package gamehub.sudoku.model;

public enum Difficulty {
    EASY("easy", 30),
    MEDIUM("medium", 40),
    HARD("hard", 50);

    private final String key;
    private final int emptyCells;

    Difficulty(String key, int emptyCells) {
        this.key = key;
        this.emptyCells = emptyCells;
    }

    public String key() {
        return key;
    }

    public int emptyCells() {
        return emptyCells;
    }

    public static Difficulty fromLevel(int level) {
        return switch (level) {
            case 0 -> EASY;
            case 1 -> MEDIUM;
            case 2 -> HARD;
            default -> throw new IllegalArgumentException(
                "Unknown difficulty level: " + level
            );
        };
    }
}
