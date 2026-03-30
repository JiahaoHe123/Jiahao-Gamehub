package gamehub.sudoku.util;

import javax.swing.*;
import java.awt.*;

/**
 * SquareWrap is a layout helper panel that forces its child component
 * to maintain a perfect square shape.
 *
 * <p>
 * This class is typically used to wrap components such as the Sudoku
 * board so that they:
 * </p>
 * <ul>
 * <li>Always remain square (width == height)</li>
 * <li>Stay centered within their parent container</li>
 * <li>Resize gracefully when the window size changes</li>
 * </ul>
 *
 * <p>
 * The wrapper itself uses a {@code null} layout and manually positions
 * its child in {@link #doLayout()}.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * Board board = new Board(matrix);
 * JPanel wrappedBoard = new SquareWrap(board);
 * </pre>
 */
public class SquareWrap extends JPanel {

    /** The component that should be kept square and centered. */
    private final JComponent child;

    /**
     * Create a new SquareWrap around the given child component.
     *
     * @param child the component to keep square (e.g., Sudoku board)
     */
    public SquareWrap(JComponent child) {
        super(null); // use manual layout
        this.child = child;
        setOpaque(false);
        add(child);
    }

    /**
     * Layout the child component as a centered square.
     *
     * <p>
     * The square size is determined by the smaller of the
     * wrapper's width and height.
     * </p>
     */
    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h);

        int x = (w - size) / 2;
        int y = (h - size) / 2;

        child.setBounds(x, y, size, size);
    }

    /**
     * Return the preferred size of this wrapper.
     *
     * <p>
     * This provides a reasonable default size for the Sudoku board,
     * while still allowing the layout manager to resize it.
     * </p>
     *
     * @return preferred square dimension
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(720, 720);
    }
}
