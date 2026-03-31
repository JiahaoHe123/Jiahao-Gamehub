package gamehub.snake.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import gamehub.model.AppTheme;
import gamehub.snake.controller.SnakeNavigationController;
import gamehub.snake.model.GameTheme;
import gamehub.snake.model.SnakeStyleSetting;

/**
 * Root snake module panel that can be embedded in Game Hub.
 */
public class SnakeModulePanel extends JPanel {

    private final SnakeStyleSetting styleSetting;
    private final SnakeNavigationController navigationController;
    private final HomePanel homePanel;
    private final StyleCustomizationPanel customizationPanel;
    private final GamePanel gamePanel;
    private final JPanel topBar;
    private final JButton backButton;

    public SnakeModulePanel(Runnable onBackToHub) {
        super(new BorderLayout());

        styleSetting = new SnakeStyleSetting();

        CardLayout cardLayout = new CardLayout();
        JPanel moduleRoot = new JPanel(cardLayout);

        homePanel = new HomePanel(styleSetting);
        customizationPanel = new StyleCustomizationPanel(styleSetting);
        gamePanel = new GamePanel(styleSetting);
        customizationPanel.setThemeManagedExternally(true);

        navigationController = new SnakeNavigationController(
            moduleRoot,
            cardLayout,
            homePanel,
            customizationPanel,
            gamePanel
        );
        navigationController.initialize();

        topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("Back to Hub");
        backButton.addActionListener(event -> onBackToHub.run());
        topBar.add(backButton);

        add(topBar, BorderLayout.NORTH);
        add(moduleRoot, BorderLayout.CENTER);

        applyTheme(AppTheme.LIGHT);
    }

    public void activate() {
        navigationController.showHome();
        homePanel.requestFocusInWindow();
    }

    public void applyTheme(AppTheme appTheme) {
        if (appTheme == null) {
            return;
        }

        styleSetting.setTheme(appTheme.isDark() ? GameTheme.DARK : GameTheme.LIGHT);

        homePanel.refreshTheme();
        gamePanel.refreshTheme();
        customizationPanel.syncFromSettings();

        GameTheme theme = styleSetting.getTheme();
        topBar.setBackground(theme.getHudBackground());
        backButton.setForeground(theme.getText());
        backButton.setBackground(theme.getButtonBackground());
        backButton.setBorder(
            javax.swing.BorderFactory.createLineBorder(theme.getButtonBorder(), 1, true)
        );
        backButton.setOpaque(true);
        setBackground(theme.getBackground());
    }
}
