package gamehub.sudoku.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import gamehub.sudoku.model.GameRecord;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Home page panel of the Sudoku application.
 *
 * <p>
 * This panel serves as the main entry screen where the user can:
 * <ul>
 * <li>Select a difficulty level (Easy / Medium / Hard)</li>
 * <li>View their historical game statistics (wins, losses, win rate)</li>
 * <li>Quit the application</li>
 * </ul>
 *
 * <p>
 * The page uses a centered "card" layout to remain visually balanced
 * across different window sizes and fullscreen mode.
 */
public class SudokuHomePanel extends JPanel {

    /** Persistent game record used to display statistics. */
    private final GameRecord record;

    /** Label that displays win/loss statistics for all difficulty levels. */
    private final JLabel statsLabel;

    /**
     * Creates the Home page UI.
     *
     * @param record   persistent game record for displaying statistics
     * @param onEasy   callback for starting an Easy game
     * @param onMedium callback for starting a Medium game
     * @param onHard   callback for starting a Hard game
     * @param onQuit   callback for quitting the application
     */
    public SudokuHomePanel(
            GameRecord record,
            Runnable onEasy,
            Runnable onMedium,
            Runnable onHard,
            Runnable onQuit) {
        super(new GridBagLayout());
        this.record = record;

        // Root background
        setBackground(new Color(245, 245, 245));
        setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30));

        // Center card container
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(
                                25, 30, 25, 30)));

        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title label
        JLabel title = new JLabel("Sudoku");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));

        // Subtitle label
        JLabel subtitle = new JLabel("Please choose difficulty level");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Statistics label (HTML used for formatting)
        statsLabel = new JLabel("", SwingConstants.CENTER);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 0, 8, 0));
        refreshStats(); // initial load

        // Difficulty & quit buttons
        JButton easyBtn = new JButton("Easy");
        JButton mediumBtn = new JButton("Medium");
        JButton hardBtn = new JButton("Hard");
        JButton quitBtn = new JButton("Quit");

        // Apply consistent styling
        styleButton(easyBtn);
        styleButton(mediumBtn);
        styleButton(hardBtn);
        styleButton(quitBtn);

        // Bind button actions
        easyBtn.addActionListener(e -> onEasy.run());
        mediumBtn.addActionListener(e -> onMedium.run());
        hardBtn.addActionListener(e -> onHard.run());
        quitBtn.addActionListener(e -> onQuit.run());

        // Build card layout
        card.add(Box.createVerticalStrut(12));
        card.add(statsLabel);

        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(25));

        card.add(easyBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(mediumBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(hardBtn);
        card.add(Box.createVerticalStrut(25));
        card.add(quitBtn);

        // Center card in root panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        add(card, gbc);
    }

    /**
     * Refreshes the statistics displayed on the Home page.
     *
     * <p>
     * This method queries {@link GameRecord} for wins, losses,
     * and win rates, then updates the stats label using HTML formatting.
     */
    public void refreshStats() {
        int easyWin = record.getWins(GameRecord.Difficulty.EASY);
        int mediumWin = record.getWins(GameRecord.Difficulty.MEDIUM);
        int hardWin = record.getWins(GameRecord.Difficulty.HARD);

        int easyLose = record.getLosses(GameRecord.Difficulty.EASY);
        int mediumLose = record.getLosses(GameRecord.Difficulty.MEDIUM);
        int hardLose = record.getLosses(GameRecord.Difficulty.HARD);

        double easyRate = record.getWinRate(GameRecord.Difficulty.EASY);
        double mediumRate = record.getWinRate(GameRecord.Difficulty.MEDIUM);
        double hardRate = record.getWinRate(GameRecord.Difficulty.HARD);
        statsLabel.setText(
                "<html><div style='text-align:center;'>"
                        + "<b>Record</b><br>"
                        + "Easy: " + easyWin + " Wins / " + easyLose + " losses ("
                        + String.format("%.1f", easyRate) + "%)<br>"
                        + "Medium: " + mediumWin + " Wins / " + mediumLose + " losses ("
                        + String.format("%.1f", mediumRate) + "%)<br>"
                        + "Hard: " + hardWin + " Wins / " + hardLose + " losses ("
                        + String.format("%.1f", hardRate) + "%)"
                        + "</div></html>");
    }

    /**
     * Applies consistent styling to buttons on the Home page.
     *
     * @param b the button to style
     */
    private void styleButton(JButton b) {
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(new Font("SansSerif", Font.PLAIN, 16));
        b.setFocusPainted(false);
        b.setMaximumSize(new java.awt.Dimension(260, 40));
        b.setPreferredSize(new java.awt.Dimension(260, 40));
    }
}
