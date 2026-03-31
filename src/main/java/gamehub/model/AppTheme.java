package gamehub.model;

public enum AppTheme {
    LIGHT,
    DARK;

    public boolean isDark() {
        return this == DARK;
    }
}
