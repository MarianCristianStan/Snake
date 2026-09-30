package gameStates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import game.Game;
import game.GamePanel;
import ui.MenuBackgroundDemo;
import utils.ScoreManager;
import utils.SoundPlayer;

public class EndGame extends State implements StateMethods {

	private static final int PANEL_WIDTH = 560;
	private static final int PANEL_HEIGHT = 400;

	private static final int BUTTON_WIDTH = 260;
	private static final int BUTTON_HEIGHT = 55;

	private final Color BUTTON_NORMAL = new Color(251, 238, 203);
	private final Color BUTTON_SELECTED = new Color(224, 190, 120);

	private final Color BORDER_NORMAL = new Color(120, 85, 50);
	private final Color BORDER_SELECTED = new Color(100, 60, 25);

	private int fruitEaten;
	private boolean victory;

	private JButton backToMenuButton;

	private boolean buttonAdded = false;
	private boolean isSelected = false;

	private final MenuBackgroundDemo backgroundDemo;

	public EndGame(Game game) {
		super(game);

		backgroundDemo = new MenuBackgroundDemo(getPanelBounds());
	}

	@Override
	public void update() {
		backgroundDemo.update();
	}

	@Override
	public void draw(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		int panelX = getPanelX();
		int panelY = getPanelY();
		int panelCenterX = panelX + PANEL_WIDTH / 2;

		backgroundDemo.setAvoidArea(getPanelBounds());
		backgroundDemo.draw(g2d);

		// === Main Panel ===
		g2d.setColor(new Color(251, 238, 203));
		g2d.fillRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);

		g2d.setColor(new Color(120, 85, 50));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);

		// === Title ===
		String title = victory ? "🏆 You Win!" : "💀 Game Over";

		Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 42);

		g2d.setFont(titleFont);

		FontMetrics titleMetrics = g2d.getFontMetrics();

		int titleX = panelCenterX - titleMetrics.stringWidth(title) / 2;

		int titleY = panelY + 85;

		// Shadow
		g2d.setColor(new Color(100, 80, 60, 120));
		g2d.drawString(title, titleX + 2, titleY + 2);

		// Main title
		if (victory) {
			g2d.setColor(new Color(155, 110, 20));
		} else {
			g2d.setColor(new Color(130, 55, 45));
		}

		g2d.drawString(title, titleX, titleY);

		// === Score ===
		String scoreText = "Your Score: " + fruitEaten;

		Font scoreFont = new Font("Segoe UI Emoji", Font.BOLD, 26);

		g2d.setFont(scoreFont);
		g2d.setColor(new Color(55, 40, 25));

		FontMetrics scoreMetrics = g2d.getFontMetrics();

		int scoreX = panelCenterX - scoreMetrics.stringWidth(scoreText) / 2;

		int scoreY = panelY + 155;

		g2d.drawString(scoreText, scoreX, scoreY);

		// === Message ===
		String message;

		if (victory) {
			message = "Great job! You completed the game.";
		} else {
			message = "Better luck next time!";
		}

		Font messageFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);

		g2d.setFont(messageFont);
		g2d.setColor(new Color(100, 70, 45));

		int messageWidth = g2d.getFontMetrics().stringWidth(message);

		g2d.drawString(message, panelCenterX - messageWidth / 2, panelY + 205);

		// === Button ===
		if (!buttonAdded) {
			initBackToMenuButton();

			buttonAdded = true;
		}

		updateButtonStyle();

		// === Footer ===
		String footer = "Press S to select • ENTER to confirm";

		g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		g2d.setColor(new Color(120, 60, 0));

		int footerWidth = g2d.getFontMetrics().stringWidth(footer);

		g2d.drawString(footer, panelCenterX - footerWidth / 2, panelY + PANEL_HEIGHT - 28);
	}

	public void setFruitEaten(int fruitEaten) {
		this.fruitEaten = fruitEaten;
	}

	public void setVictory(boolean victory) {
		this.victory = victory;
	}

	public void simulateKeyPress(char key) {
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(key);

		if (keyCode == KeyEvent.VK_UNDEFINED) {
			return;
		}

		KeyEvent fakeEvent = new KeyEvent(game.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
				keyCode, key);

		keyPressed(fakeEvent);
	}

	public void doSelect() {
		if (isSelected && backToMenuButton != null) {
			SoundPlayer.playSound("/assets/sounds/enter.wav");

			backToMenuButton.doClick();
		}
	}

	public void moveCursorDown() {
		if (!isSelected) {
			isSelected = true;

			updateButtonStyle();

			SoundPlayer.playSound("/assets/sounds/menu_select.wav");
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {

		case KeyEvent.VK_S -> {
			moveCursorDown();
		}

		case KeyEvent.VK_ENTER -> {
			doSelect();
		}

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	private void initBackToMenuButton() {
		int buttonX = getPanelX() + PANEL_WIDTH / 2 - BUTTON_WIDTH / 2;

		int buttonY = getPanelY() + 245;

		backToMenuButton = new JButton("Back to Menu");

		backToMenuButton.setBounds(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);

		backToMenuButton.setFont(new Font("Segoe UI", Font.BOLD, 20));

		backToMenuButton.setForeground(new Color(55, 40, 25));

		backToMenuButton.setBackground(BUTTON_NORMAL);

		backToMenuButton.setBorder(BorderFactory.createLineBorder(BORDER_NORMAL, 3, true));

		backToMenuButton.setFocusPainted(false);
		backToMenuButton.setContentAreaFilled(true);
		backToMenuButton.setOpaque(true);

		backToMenuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		backToMenuButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!isSelected) {
					isSelected = true;

					SoundPlayer.playSound("/assets/sounds/menu_select.wav");

					updateButtonStyle();
				}
			}
		});

		backToMenuButton.addActionListener(e -> {
			ScoreManager.saveScore(fruitEaten);

			GameState.state = GameState.MENU;

			game.getPlaying().restartGame();

			game.getGamePanel().remove(backToMenuButton);

			buttonAdded = false;
			isSelected = false;

			game.getGamePanel().requestFocusInWindow();

			game.getGamePanel().repaint();
		});

		game.getGamePanel().add(backToMenuButton);

		backToMenuButton.setVisible(true);

		game.getGamePanel().repaint();
	}

	private void updateButtonStyle() {
		if (backToMenuButton == null) {
			return;
		}

		if (isSelected) {
			backToMenuButton.setForeground(new Color(70, 45, 20));

			backToMenuButton.setBackground(BUTTON_SELECTED);

			backToMenuButton.setBorder(BorderFactory.createLineBorder(BORDER_SELECTED, 4, true));

		} else {
			backToMenuButton.setForeground(new Color(55, 40, 25));

			backToMenuButton.setBackground(BUTTON_NORMAL);

			backToMenuButton.setBorder(BorderFactory.createLineBorder(BORDER_NORMAL, 3, true));
		}
	}

	private int getPanelX() {
		return GamePanel.getScreenWidth() / 2 - PANEL_WIDTH / 2;
	}

	private int getPanelY() {
		return GamePanel.getScreenHeight() / 2 - PANEL_HEIGHT / 2;
	}

	private Rectangle getPanelBounds() {
		return new Rectangle(getPanelX(), getPanelY(), PANEL_WIDTH, PANEL_HEIGHT);
	}
}