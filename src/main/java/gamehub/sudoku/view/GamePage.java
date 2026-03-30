package gamehub.sudoku.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gamehub.sudoku.model.SudokuBoard;
import gamehub.sudoku.util.SquareWrap;
import gamehub.sudoku.model.GameRecord;

/**
 * Game page panel shown after the user starts a Sudoku game.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 * <li>
 * Creating a new {@link BoardPanel} from a {@link SudokuBoard} puzzle
 * </li>
 * <li>
 * Displaying the board inside a
 * centered square wrapper ({@link SquareWrap})
 * </li>
 * <li>
 * Showing UI helpers like the {@link NumberBar}
 * (remaining counts) and mistakes left
 * </li>
 * <li>Binding a {@link ControlPanel} to the current board instance</li>
 * <li>
 * Handling win/lose events and updating the persistent {@link GameRecord}
 * </li>
 * <li>Navigating back to the Home page via the provided callback</li>
 * </ul>
 *
 * <p>
 * Layout:
 * <ul>
 * <li>NORTH: top bar (number bar + mistakes label)</li>
 * <li>CENTER: board card (square, centered)</li>
 * <li>SOUTH: control buttons</li>
 * </ul>
 */
public class GamePage extends JPanel {

    /** Callback used to navigate back to the Home page. */
    private final Runnable onHome;

    /** Current game board (changes whenever a new game is started). */
    private BoardPanel board;

    /** Wrapper panel that centers the board card in the page. */
    private final JPanel centerWrap;

    /**
     * Card panel that holds the board
     * component and provides borders/background.
     */
    private final JPanel boardCard;

    /** Bottom control area (Home / Check / Reset Notes / Notes Mode toggle). */
    private final ControlPanel controlPanel;

    /**
     * Displays remaining counts for each number
     * (1..9) based on the board state.
     */
    private final NumberBar numberBar;

    /** Displays how many mistakes are left in the current game. */
    private final JLabel attemptsLabel = new JLabel();

    /** Persistent record system (wins/losses) shared across the app. */
    private final GameRecord record;

    /** The currently selected difficulty level for the ongoing game. */
    private int currentLevel = 0;

    /**
     * Creates the Game page UI.
     *
     * @param onHome callback invoked when
     *               the game should return to the Home page
     * @param record shared persistent record system for wins/losses
     */
    public GamePage(Runnable onHome, GameRecord record) {
        super(new BorderLayout());
        this.onHome = onHome;
        this.record = record;

        setBackground(new Color(245, 245, 245));

        // -------------------- Top bar --------------------
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        numberBar = new NumberBar();

        topBar.add(numberBar, BorderLayout.CENTER);
        attemptsLabel.setFont(
                new java.awt.Font(
                        "SansSerif", java.awt.Font.BOLD, 14));
        attemptsLabel.setForeground(new Color(180, 0, 0));
        attemptsLabel.setBorder(
                javax.swing.BorderFactory.createEmptyBorder(
                        0, 0, 0, 12));
        attemptsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        topBar.add(attemptsLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // -------------------- Center area --------------------
        centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        boardCard = new JPanel(new BorderLayout());
        boardCard.setBackground(Color.WHITE);
        boardCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Use GridBagConstraints to keep board card centered and responsive
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        centerWrap.add(boardCard, gbc);

        // -------------------- Bottom control panel --------------------
        controlPanel = new ControlPanel(() -> this.onHome.run());

        add(centerWrap, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    /**
     * Starts a new game at the given difficulty level.
     *
     * <p>
     * This will:
     * <ul>
     * <li>Create a new {@link SudokuBoard} puzzle</li>
     * <li>Create a new {@link BoardPanel} bound to that puzzle</li>
     * <li>
     * Wire up UI callbacks for number bar, attempts label, win/lose events
     * </li>
     * <li>Replace the center board component with the new board</li>
     * </ul>
     *
     * @param level difficulty level (0=EASY, 1=MEDIUM, 2=HARD)
     */
    public void startNewGame(int level) {
        this.currentLevel = level;

        SudokuBoard m = new SudokuBoard(level);
        this.board = new BoardPanel(m);

        attemptsLabel.setText("Mistakes left: " + board.getRemainingAttempts());

        // Update attempts label whenever mistakes change
        board.setOnAttemptsChanged(() -> {
            attemptsLabel.setText("Mistakes left: " + board.getRemainingAttempts());
            attemptsLabel.revalidate();
            attemptsLabel.repaint();
        });

        numberBar.setRemaining(board.getRemainingCounts());

        // Board emits win/lose events; we handle dialogs and navigation on the EDT
        board.setOnWin(() -> SwingUtilities.invokeLater(this::handleWin));
        board.setOnLose(() -> SwingUtilities.invokeLater(this::handleLose));

        // Board emits count updates; refresh the NumberBar
        board.setOnCountsChanged(
                () -> numberBar.setRemaining(
                        board.getRemainingCounts()));

        // Replace the board component (wrapped in SquareWrap to keep it square)
        boardCard.removeAll();
        boardCard.add(new SquareWrap(board), BorderLayout.CENTER);

        // Re-bind the control panel actions to this new board instance
        controlPanel.bindBoard(board);

        revalidate();
        repaint();

        // Give keyboard focus to the board so typing works immediately
        SwingUtilities.invokeLater(() -> board.requestFocusInWindow());
    }

    /**
     * Called when the board reports the user has solved the puzzle.
     *
     * <p>
     * This method:
     * <ul>
     * <li>Records a win in {@link GameRecord}</li>
     * <li>Asks whether the user wants to play another game</li>
     * <li>If "Yes": navigates back to Home (to pick a new level)</li>
     * <li>If "No": asks whether to quit</li>
     * </ul>
     */
    private void handleWin() {
        record.recordWinByLevel(currentLevel);

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Congratulations, you are a winner!\n\nPlay another game?",
                "Game finished",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Go to HOME so user can choose difficulty again
            onHome.run();
        } else {
            int close = JOptionPane.showConfirmDialog(
                    this,
                    "Quit?\n",
                    "Game finished",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (close == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                return;
            }
        }
    }

    /**
     * Called when the board reports the user has used all allowed mistakes.
     *
     * <p>
     * This method:
     * <ul>
     * <li>Records a loss in {@link GameRecord}</li>
     * <li>Shows a failure message</li>
     * <li>Navigates back to Home</li>
     * </ul>
     */
    private void handleLose() {
        record.recordLossByLevel(currentLevel);
        JOptionPane.showMessageDialog(
                this,
                "You have used all your chance",
                "Game failed",
                JOptionPane.INFORMATION_MESSAGE);
        onHome.run();
    }

}
