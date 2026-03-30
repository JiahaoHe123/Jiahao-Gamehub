package gamehub.sudoku.view;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a single cell in the Sudoku board.
 *
 * <p>
 * A {@code CellButton} is responsible ONLY for:
 * <ul>
 * <li>Storing per-cell state (fixed, wrong, notes)</li>
 * <li>Rendering the cell value or note candidates</li>
 * <li>Handling per-cell UI behavior</li>
 * </ul>
 *
 * <p>
 * Game logic (validation, win/lose conditions, board coordination)
 * is handled by {@link BoardPanel}, not by this class.
 */
public class CellButton extends JButton {

    /** Whether this cell is fixed ({@code true} => cannot be changed) */
    public boolean isFixed = false;

    /** Cell location in the board: [row, col]. */
    public int[] location = new int[2];

    /** Possible visual states for a cell. */
    public enum State {
        NORMAL, WRONG
    }

    /** Current visual state of the cell. */
    public State state = State.NORMAL;

    /** Set of note candidates entered by the user (1–9). */
    public Set<Integer> notes = new LinkedHashSet<>();

    /** Which note (if any) should be highlighted. */
    public int noteToHighlight = 0;

    /** Font used to display the main cell value. */
    private static final Font ANSWER_FONT = new Font("SansSerif", Font.BOLD, 20);

    /**
     * Constructs a new Sudoku cell button.
     *
     * @param text initial text of the cell (empty or fixed value)
     */
    public CellButton(String text) {
        super(text);
        setFont(ANSWER_FONT);
    }

    /**
     * Updates the text display based on current notes.
     *
     * <p>
     * If the cell already has a main value, this method does nothing.
     * Otherwise, it converts the note set into a space-separated string.
     */
    public void updateDisplay() {
        String text = getText();
        if (text != null && !text.isEmpty()) {
            return;
        }

        if (notes.isEmpty()) {
            setText("");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int n : notes) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(n);
            }
            setText(sb.toString().trim());
        }
    }

    /**
     * Custom painting logic for rendering note candidates.
     *
     * <p>
     * Notes are rendered only when the cell has no main value.
     * Each note is drawn in a 3×3 grid layout inside the cell.
     * The highlighted note (if any) is drawn larger and in blue.
     *
     * @param g the Graphics context used for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Only draw notes if there is NO main value
        String text = getText();
        if (text != null && !text.isEmpty())
            return;
        if (notes.isEmpty())
            return;

        Graphics2D g2 = (Graphics2D) g.create();

        int w = getWidth();
        int h = getHeight();
        int cellW = w / 3;
        int cellH = h / 3;

        for (int n = 1; n <= 9; n++) {
            if (!notes.contains(n))
                continue;

            int row = (n - 1) / 3;
            int col = (n - 1) % 3;

            boolean highlighted = (n == noteToHighlight);

            Font base = getFont().deriveFont(10f);
            Font f = highlighted ? base.deriveFont(Font.BOLD, 11f) : base;
            g2.setFont(f);

            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(highlighted ? Color.BLUE : Color.DARK_GRAY);

            String s = String.valueOf(n);

            int cx = col * cellW + cellW / 2;
            int cy = row * cellH + cellH / 2;

            int textW = fm.stringWidth(s);
            int textH = fm.getAscent();

            int x = cx - textW / 2;
            int y = cy + textH / 2 - 2;

            g2.drawString(s, x, y);
        }

        g2.dispose();
    }
}
