package gamehub.sudoku.view;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Control panel displayed at the bottom of the game page.
 *
 * <p>
 * This panel provides user controls for:
 * <ul>
 * <li>Returning to the home page</li>
 * <li>Checking the current board against the solution</li>
 * <li>Resetting all notes on the board</li>
 * <li>Toggling notes mode on and off</li>
 * </ul>
 *
 * <p>
 * The panel is designed to be reusable across games by
 * dynamically binding to the current {@link BoardPanel} instance.
 */
public class ControlPanel extends JPanel {

    /** Button to return to the home page */
    private final JButton homeBtn = new JButton("Home");

    /** Button to check the current board answer */
    private final JButton checkBtn = new JButton("Check");

    /** Button to clear all notes on the board */
    private final JButton resetBtn = new JButton("Reset Notes");

    /** Toggle button for enabling/disabling notes mode */
    private final JToggleButton notesMode = new JToggleButton("Notes Mode: OFF");

    /** Currently bound game board */
    private BoardPanel board;

    /**
     * Constructs the control panel.
     *
     * @param onHome callback invoked when the Home button is pressed
     */
    public ControlPanel(Runnable onHome) {
        homeBtn.addActionListener(e -> onHome.run());

        add(homeBtn);
        add(checkBtn);
        add(resetBtn);
        add(notesMode);
    }

    /**
     * Binds the control panel to a specific {@link BoardPanel} instance.
     *
     * <p>
     * This method:
     * <ul>
     * <li>Clears any previous listeners</li>
     * <li>Resets notes mode state</li>
     * <li>Wires buttons to the new board instance</li>
     * </ul>
     *
     * @param board the board to control
     */
    public void bindBoard(BoardPanel board) {
        this.board = board;

        // Remove old listeners to avoid duplicate actions
        for (var l : checkBtn.getActionListeners()) {
            checkBtn.removeActionListener(l);
        }
        for (var l : resetBtn.getActionListeners()) {
            resetBtn.removeActionListener(l);
        }
        for (var l : notesMode.getActionListeners()) {
            notesMode.removeActionListener(l);
        }

        // Reset notes mode state
        notesMode.setSelected(false);
        notesMode.setText("Notes Mode: OFF");
        board.setNoteMode(false);

        // Check current board against solution
        checkBtn.addActionListener(e -> {
            try {
                int[] current = this.board.getCurrentSolution();
                this.board.checkAnswer(current);
                // System.out.println(correct);
            } catch (Exception ex) {
                // System.err.println(ex.getMessage());
            }
            this.board.requestFocusInWindow();
        });

        // Clear all notes on the board
        resetBtn.addActionListener(e -> {
            this.board.refreshBoard();
            this.board.requestFocusInWindow();
        });

        // Toggle notes mode
        notesMode.addActionListener(e -> {
            boolean on = notesMode.isSelected();
            notesMode.setText(on ? "Notes Mode: ON" : "Notes Mode: OFF");
            this.board.setNoteMode(on);
            this.board.requestFocusInWindow();
        });
    }
}
