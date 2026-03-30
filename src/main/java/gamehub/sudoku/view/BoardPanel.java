package gamehub.sudoku.view;

import javax.swing.*;

import gamehub.sudoku.model.SudokuBoard;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Board represents the main interactive Sudoku grid.
 *
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 * <li>Render a 9×9 grid of {@link CellButton}s</li>
 * <li>Handle user input (keyboard + mouse)</li>
 * <li>Support normal input mode and notes mode</li>
 * <li>Validate answers against the solution</li>
 * <li>Track mistakes, remaining attempts, and win/lose state</li>
 * <li>Notify external UI components via callbacks</li>
 * </ul>
 *
 * <p>
 * This class is UI-focused and delegates Sudoku rule validation
 * to {@link util.SudokuRules} and puzzle generation to {@link SudokuBoard}.
 * </p>
 */
public class BoardPanel extends JPanel {

    /** Board dimension (9×9). */
    public static final int SIZE = 9;

    /** Maximum allowed wrong attempts before losing the game. */
    public static final int TOLERANCE = 3;

    /* -------------------- UI colors -------------------- */

    /** Background color for the board. */
    private final Color BOARD_BG = new Color(235, 220, 160);

    /** Background for the currently selected cell. */
    private final Color SELECTED_CELL = new Color(255, 255, 0);

    /** Highlight color for cells with the same number. */
    private final Color SAME_NUMBER = new Color(255, 255, 180);

    /** Background color for a correctly filled cell. */
    private final Color CORRECT_CELL = new Color(160, 210, 170);

    /** Background color for an incorrect cell. */
    private final Color WRONG_CELL = new Color(240, 120, 120);

    /* -------------------- Board state -------------------- */

    /** 9×9 grid of cell buttons. */
    private CellButton[][] cells;

    /** Currently selected cell (may be null). */
    private CellButton selectedButton = null;

    /** Solved board (row-major list of 81 values). */
    private List<Integer> solution;

    /** Underlying puzzle matrix (generator + rules). */
    private SudokuBoard m;

    /** Whether note-taking mode is enabled. */
    private boolean notesMode = false;

    /** Number of correctly filled user entries. */
    private int totalAdded = 0;

    /** Count of how many times each number (1–9) is already placed. */
    private int[] countOfEachNum;

    /** Callback when number counts change (e.g., NumberBar update). */
    private Runnable onCountsChanged;

    /** Callback triggered when the player wins. */
    private Runnable onWin;

    /** Callback triggered when the player loses. */
    private Runnable onLose;

    /** Number of wrong attempts made by the user. */
    private int wrongTimes = 0;

    /** Callback when remaining attempts change. */
    private Runnable onAttemptsChanged;

    /**
     * Construct a new Board using the given Sudoku matrix.
     *
     * <p>
     * This initializes all cells, applies borders for 3×3 blocks,
     * sets fixed cells, and installs input handlers.
     * </p>
     *
     * @param matrix generated Sudoku puzzle
     */
    public BoardPanel(SudokuBoard matrix) {

        this.countOfEachNum = new int[SIZE];

        setFocusable(true);
        setLayout(new GridLayout(SIZE, SIZE));
        setBackground(BOARD_BG);

        m = matrix;
        solution = m.getSolution();

        cells = new CellButton[SIZE][SIZE];

        int index = 0;
        for (int num : m) {
            String text = (num == 0) ? "" : String.valueOf(num);

            if (num != 0)
                countOfEachNum[num - 1] += 1;

            int row = index / SIZE;
            int col = index % SIZE;

            CellButton btn = new CellButton(text);

            if (!text.equals("")) {
                btn.isFixed = true;
            }

            // Draw thicker borders for 3×3 blocks
            int top = (row == 0 || row == 3 || row == 6) ? 3 : 1;
            int left = (col == 0 || col == 3 || col == 6) ? 3 : 1;
            int bottom = (row == SIZE - 1) ? 3 : 1;
            int right = (col == SIZE - 1) ? 3 : 1;

            btn.setBorder(
                    BorderFactory.createMatteBorder(
                            top, left, bottom, right, Color.DARK_GRAY));

            addSelectedBehavior(btn);

            cells[row][col] = btn;
            btn.location[0] = row;
            btn.location[1] = col;

            add(btn);
            index++;
        }

        addEditTextBehavior();
    }

    /**
     * @return a defensive copy of the solution list
     */
    public List<Integer> getSolution() {
        return new ArrayList<>(solution);
    }

    /**
     * Return the user's current board state as a flat int array.
     *
     * <p>
     * If the board is not fully filled, a warning dialog is shown.
     * </p>
     *
     * @return 81-length array of values, or null if incomplete
     */
    public int[] getCurrentSolution() {
        if (totalAdded < m.numOfEmptyCells()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill the board first",
                    "Cells partial filled",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int[] result = new int[SIZE * SIZE];

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String text = cells[r][c].getText();
                int num;
                try {
                    num = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Something went wrong");
                }
                result[r * SIZE + c] = num;
            }
        }
        return result;
    }

    /**
     * Validate the entire board against Sudoku rules.
     *
     * @param answer flattened user solution
     * @return true if valid, false otherwise
     */
    public boolean checkAnswer(int[] answer) {
        if (answer.length != 81) {
            throw new IllegalArgumentException("Please give a valid answer");
        }

        for (int i = 0; i < answer.length; i++) {
            int num = answer[i];
            boolean bad = m.checkDuplicate(i / 9, i % 9, num);
            if (bad) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your answer for this is wrong",
                        "Wrong answer",
                        JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Your are a winner!",
                "Win",
                JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    /**
     * Install mouse-selection behavior for a cell.
     *
     * @param btn cell button
     */
    private void addSelectedBehavior(CellButton btn) {
        btn.addActionListener(e -> {

            selectedButton = btn;
            selectedButton.setBackground(SELECTED_CELL);
            selectedButton.setOpaque(true);

            requestFocusInWindow();
            highlightSameNumbers(selectedButton.getText());
        });
    }

    /**
     * Install keyboard input handling for the board.
     *
     * <p>
     * Supports:
     * </p>
     * <ul>
     * <li>Normal mode (placing numbers)</li>
     * <li>Notes mode (toggling candidate numbers)</li>
     * <li>Backspace / clear behavior</li>
     * </ul>
     */
    private void addEditTextBehavior() {
        BoardPanel cur = this;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (selectedButton == null) {
                    return;
                }

                if (selectedButton.isFixed) {
                    JOptionPane.showMessageDialog(
                            cur,
                            "The value of this cell cannot be changed",
                            "Fixed Cell",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                char ch = e.getKeyChar();

                // Notes mode logic
                if (notesMode) {
                    if (ch >= '1' && ch <= '9') {
                        int val = ch - '0';

                        selectedButton.setText("");
                        selectedButton.setBackground(SELECTED_CELL);
                        selectedButton.state = CellButton.State.NORMAL;

                        if (selectedButton.notes.contains(val)) {
                            selectedButton.notes.remove(val);
                        } else {
                            selectedButton.notes.add(val);
                        }

                        // no main answer in note mode -> clear text then show notes
                        selectedButton.repaint();
                        highlightSameNumbers("");
                    } else if (ch == '0' || ch == ' ' || ch == '\b') {
                        // clear all notes
                        selectedButton.notes.clear();
                        selectedButton.setText("");
                        selectedButton.updateDisplay();
                    }
                    return;
                }

                // Normal mode logic
                selectedButton.notes.clear();

                if (ch >= '1' && ch <= '9') {
                    selectedButton.setText(String.valueOf(ch));
                    checkCellAnswer();
                } else if (ch == '0' || ch == ' ' || ch == '\b') {
                    selectedButton.setText("");
                    selectedButton.setBackground(SELECTED_CELL);
                }
                highlightSameNumbers(selectedButton.getText());
            }
        });
    }

    /**
     * Remove an answer value from notes that have become impossible (illegal)
     * after the player correctly fills a cell.
     *
     * <p>
     * Rule: If a cell is solved with value {@code answer},
     * then no other cell in the same row, same column,
     * or same 3×3 box can still keep {@code answer} as a candidate note.
     * </p>
     *
     * @param row    row index of the solved cell
     * @param col    column index of the colved cell
     * @param answer the correct number placed in (row, col)
     */
    private void removeIllegalNotes(int row, int col, int answer) {

        // 1) Remove this answer from all notes in the same row
        for (int c = 0; c < SIZE; c++) {
            if (c == col) {
                continue; // skip the solved cell itself
            }
            CellButton btn = cells[row][c];
            if (btn.notes.contains(answer)) {
                btn.notes.remove(answer); // remove if present
            }
        }

        // 2) Remove this answer from all notes in the same column
        for (int r = 0; r < SIZE; r++) {
            if (r == row) {
                continue; // skip the solved cell itself
            }
            CellButton btn = cells[r][col];
            if (btn.notes.contains(answer)) {
                btn.notes.remove(answer); // remove if present
            }
        }

        // 3) Remove this answer from all notes in the same 3×3 box
        int rowStart = row / 3;
        int colStart = col / 3;
        for (int r = rowStart * 3; r < (rowStart + 1) * 3; r++) {
            for (int c = colStart * 3; c < (colStart + 1) * 3; c++) {
                if (r == row && c == col) {
                    continue; // skip the solved cell itself
                }
                CellButton btn = cells[r][c];
                if (btn.notes.contains(answer)) {
                    btn.notes.remove(answer); // remove if present
                }
            }
        }
    }

    /**
     * Validate the current input in {@code selectedButton}
     * against the real solution.
     *
     * <p>
     * Behavior:
     * </p>
     * <ul>
     * <li>
     * If wrong: mark cell as WRONG,
     * decrement remaining attempts, maybe trigger lose.
     * </li>
     * <li>
     * If correct: mark cell as fixed, update counts,
     * remove illegal notes, maybe trigger win.
     * </li>
     * </ul>
     *
     * <p>
     * This method assumes {@code selectedButton} currently
     * contains a valid digit '1'..'9'.
     * </p>
     */
    private void checkCellAnswer() {
        int answer = Integer.parseInt(selectedButton.getText());

        // Convert the selected cell location to a 1D index for solution lookup
        int rowNum = selectedButton.location[0];
        int colNum = selectedButton.location[1];
        int index = rowNum * SIZE + colNum;

        // Real solution value at this position
        int realSolu = solution.get(index);

        // Wrong answer path
        if (answer != realSolu) {
            wrongTimes++;

            // Notify UI (like attempts label) to refresh
            if (onAttemptsChanged != null)
                onAttemptsChanged.run();

            // Mark the cell visually and logically wrong
            selectedButton.state = CellButton.State.WRONG;
            selectedButton.setBackground(WRONG_CELL);

            // If user used up all allowed mistakes, trigger lose callback
            if (wrongTimes == TOLERANCE) {
                if (onLose != null)
                    onLose.run();
            }

        } else {
            // Correct answer path: lock the cell and mark success
            selectedButton.setBackground(CORRECT_CELL);
            selectedButton.state = CellButton.State.NORMAL;
            selectedButton.isFixed = true;
            totalAdded++;

            // Update count tracking for NumberBar (remaining digits)
            countOfEachNum[answer - 1] += 1;

            // Notify NumberBar/UI that counts changed
            if (onCountsChanged != null)
                onCountsChanged.run();

            // Remove this number from candidate notes in row/col/box
            removeIllegalNotes(rowNum, colNum, answer);

            // If all empty cells are now correctly filled, user wins
            if (totalAdded == m.numOfEmptyCells()) {
                if (onWin != null)
                    onWin.run();
            }
        }
    }

    /**
     * Clear notes and note-highlight state on every cell.
     *
     * <p>
     * This is used by the "Reset Notes" feature.
     * </p>
     * <p>
     * It does not change fixed values or
     * user-entered answers—only notes UI state.
     * </p>
     */
    public void refreshBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                CellButton btn = cells[r][c];

                // Remove all candidate notes
                btn.notes.clear();

                // Clear any highlighted candidate notes
                btn.noteToHighlight = 0;

                // Redraw the cell so changes appear immediately
                btn.repaint();
            }
        }
    }

    /**
     * Highlight behavior after a selection or input change.
     *
     * <p>
     * Rules:
     * </p>
     * <ul>
     * <li>
     * Reset all non-selected cells back to
     * normal background (or wrong background if wrong).
     * </li>
     * <li>If the selected value is a digit, highlight:</li>
     * <ul>
     * <li>Cells whose main text equals that digit (SAME_NUMBER)</li>
     * <li>Cells whose notes contain that digit (noteToHighlight set)</li>
     * </ul>
     * <li>
     * Wrong cells keep their wrong color;
     * if also same-number, use orange as special override.
     * </li>
     * </ul>
     *
     * @param value the selected cell's text
     */
    private void highlightSameNumbers(String value) {

        // Parse the target digit if the selected cell has exactly one digit
        Integer target = null;
        if (value != null
                && value.length() == 1
                && Character.isDigit(value.charAt(0))) {
            target = value.charAt(0) - '0';
        }

        boolean hasTarget = target != null;

        // Single pass: reset + apply highlights
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                CellButton btn = cells[r][c];

                // Default: clear note highlight every time we recompute highlights
                btn.noteToHighlight = 0;

                if (btn != selectedButton) {

                    boolean sameAnswer = hasTarget && value.equals(btn.getText());
                    boolean hasNote = hasTarget && btn.notes.contains(target);

                    if (btn.state == CellButton.State.WRONG) {
                        // Wrong cells keep wrong background
                        btn.setBackground(WRONG_CELL);

                        // If it's also same-number, override with orange
                        if (sameAnswer) {
                            btn.setBackground(new Color(255, 165, 0));
                        }
                    } else {
                        // Normal cells get board background by default
                        btn.setBackground(BOARD_BG);

                        // If same number as selected: highlight cell background
                        if (sameAnswer) {
                            btn.setOpaque(true);
                            btn.setBackground(SAME_NUMBER);

                            // Else if the note contains the selected digit:
                            // highlight note text in paintComponent
                        } else if (hasNote) {
                            btn.noteToHighlight = target;
                            // background stays BOARD_BG
                        }
                    }

                } else {
                    // Selected cell: keep its own background color (selectedColor)
                    // Only ensure no note highlight persists
                    btn.noteToHighlight = 0;
                }

                // Ensure UI updates immediately
                btn.repaint();
            }
        }
    }

    /**
     * Enable/disable notes mode.
     *
     * <p>
     * When notes mode is ON, typing 1-9 toggles candidate notes
     * instead of setting the main cell value.
     * </p>
     *
     * @param notesMode true = notes mode on, false = normal mode
     */
    public void setNoteMode(boolean notesMode) {
        this.notesMode = notesMode;
    }

    /**
     * @return true if notes mode is enabled
     */
    public boolean isNotesMode() {
        return notesMode;
    }

    /**
     * Set a callback that runs when the remaining-count values change
     * (typically used to refresh {@link NumberBar}).
     *
     * @param r callback runnable (may be null)
     */
    public void setOnCountsChanged(Runnable r) {
        this.onCountsChanged = r;
    }

    /**
     * Compute how many of each number (1..9) are still missing from the board.
     *
     * <p>
     * If {@code countOfEachNum[i]} is how many times digit (i+1) appears,
     * then remaining is {@code 9 - countOfEachNum[i]}.
     * </p>
     *
     * @return int[9] where index 0 means remaining count for digit 1,
     *         index 8 for digit 9
     */
    public int[] getRemainingCounts() {
        int[] remaining = new int[9];
        for (int i = 0; i < 9; i++) {
            remaining[i] = 9 - countOfEachNum[i];
        }
        return remaining;
    }

    /**
     * Set callback triggered when the player wins the game.
     *
     * @param onWin runnable callback (may be null)
     */
    public void setOnWin(Runnable onWin) {
        this.onWin = onWin;
    }

    /**
     * Set callback triggered when the player loses the game.
     *
     * @param onLose runnable callback (may be null)
     */
    public void setOnLose(Runnable onLose) {
        this.onLose = onLose;
    }

    /**
     * @return how many mistakes the player can still make before losing
     */
    public int getRemainingAttempts() {
        return TOLERANCE - wrongTimes;
    }

    /**
     * Set callback triggered when remaining attempts change
     * (typically used to refresh attempts UI label).
     *
     * @param r runnable callback (may be null)
     */
    public void setOnAttemptsChanged(Runnable r) {
        this.onAttemptsChanged = r;
    }
}
