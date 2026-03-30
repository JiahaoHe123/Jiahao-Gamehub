package gamehub.sudoku.controller;

import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.view.BoardPanel;
import gamehub.sudoku.view.CellButton;

public class SudokuGameController {
    private final SudokuBoard sudokuBoard;
    private final BoardPanel boardPanel;

    private boolean notesMode = false;
    private int totalAdded = 0;
    private int wrongTimes = 0;
    private final int[] countOfEachNum = new int[9];

    private Runnable onCountsChanged = () -> {};
    private Runnable onWin = () -> {};
    private Runnable onLose = () -> {};
    private Runnable onAttemptsChanged = () -> {};

    public SudokuGameController(SudokuBoard sudokuBoard, BoardPanel boardPanel) {
        this.sudokuBoard = sudokuBoard;
        this.boardPanel = boardPanel;
    }

    public void handleKeyTyped(CellButton selectedButton, char ch) {
        // notes mode branch
        // normal input branch
        // call validateCell(...)
        // tell boardPanel how to update UI
    }

    public void setNoteMode(boolean notesMode) {
        this.notesMode = notesMode;
    }

    public boolean isNotesMode() {
        return notesMode;
    }

    public int[] getRemainingCounts() {
        int[] remaining = new int[9];
        for (int i = 0; i < 9; i++) {
            remaining[i] = 9 - countOfEachNum[i];
        }
        return remaining;
    }

    public int getRemainingAttempts() {
        return 3 - wrongTimes;
    }

    public void setOnCountsChanged(Runnable onCountsChanged) {
        this.onCountsChanged = onCountsChanged == null ? () -> {} : onCountsChanged;
    }

    public void setOnWin(Runnable onWin) {
        this.onWin = onWin == null ? () -> {} : onWin;
    }

    public void setOnLose(Runnable onLose) {
        this.onLose = onLose == null ? () -> {} : onLose;
    }

    public void setOnAttemptsChanged(Runnable onAttemptsChanged) {
        this.onAttemptsChanged =
            onAttemptsChanged == null ? () -> {} : onAttemptsChanged;
    }
}