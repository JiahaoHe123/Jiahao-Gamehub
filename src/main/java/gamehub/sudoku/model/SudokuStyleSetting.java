package gamehub.sudoku.model;

public class SudokuStyleSetting {
    private GameTheme theme = GameTheme.LIGHT;

    public GameTheme getTheme() {
        return theme;
    }

    public void setTheme(GameTheme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }
}
