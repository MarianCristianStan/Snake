package gameStates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import game.Game;
import game.GamePanel;
import ui.MenuBackgroundDemo;
import utils.ScoreManager;
import utils.SoundPlayer;

public class BestScores extends State implements StateMethods {

	private List<Integer> bestScores;

	private static final int PANEL_WIDTH = 660;
	private static final int PANEL_HEIGHT = 440;

	private MenuBackgroundDemo backgroundDemo;

	public BestScores(Game game) {
		super(game);
		bestScores = ScoreManager.loadScores();
		backgroundDemo = new MenuBackgroundDemo(getPanelBounds());
	}

	@Override
	public void update() {
		bestScores = ScoreManager.loadScores();

		backgroundDemo.update();
	}

	@Override
	public void draw(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;
		int centerX = GamePanel.getScreenWidth() / 2;
		int panelX = getPanelX();
		int panelY = getPanelY();
		backgroundDemo.setAvoidArea(getPanelBounds());
		backgroundDemo.draw(g2d);

		g2d.setColor(new Color(251, 238, 203));
		g2d.fillRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);
		g2d.setColor(new Color(120, 85, 50));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);

		// === Title with Drop Shadow ===
		String title = "🏆 Best Scores";
		Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 34);
		g2d.setFont(titleFont);

		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		int titleX = centerX - titleWidth / 2;
		int titleY = panelY + 60;

		g2d.setColor(new Color(80, 80, 80));
		g2d.drawString(title, titleX + 2, titleY + 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString(title, titleX, titleY);

		// === Scores Section ===
		Font scoreFont = new Font("Segoe UI Emoji", Font.PLAIN, 24);
		g2d.setFont(scoreFont);
		int textX = panelX + 280;
		int startY = titleY + 60;

		if (bestScores.isEmpty()) {
			g2d.setColor(Color.LIGHT_GRAY);
			String noScoresMessage = "No scores yet...";
			int noScoresWidth = g2d.getFontMetrics().stringWidth(noScoresMessage);
			g2d.drawString(noScoresMessage, centerX - noScoresWidth / 2, startY);
		} else {
			g2d.setColor(new Color(30, 30, 30));
			for (int i = 0; i < bestScores.size(); i++) {
				int score = bestScores.get(i);
				String rank;

				switch (i + 1) {
				case 1:
					rank = "1st. " + score + " 🏅";
					break;
				case 2:
					rank = "2nd. " + score + "🎖️";
					break;
				case 3:
					rank = "3rd. " + score + " 🎉";
					break;
				case 4:
					rank = "4th. " + score;
					break;
				case 5:
					rank = "5th. " + score;
					break;
				default:
					rank = (i + 1) + "th. " + score;
					break;
				}

				int rankY = startY + i * 40;
				g2d.drawString(rank, textX, rankY);
			}
		}

		// === ESC Footer ===
		String footer = "⎋ Press ESC to return";
		g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
		g2d.setColor(new Color(120, 60, 0));
		int footerWidth = g2d.getFontMetrics().stringWidth(footer);
		int footerY = panelY + PANEL_HEIGHT - 24;
		g2d.drawString(footer, centerX - footerWidth / 2, footerY);
	}

	public void simulateKeyPress(char key) {
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(key);
		if (keyCode == KeyEvent.VK_UNDEFINED)
			return;

		KeyEvent fakeEvent = new KeyEvent(game.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
				keyCode, key);
		keyPressed(fakeEvent);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			returnMenu();
		}
	}

	public void returnMenu() {
		SoundPlayer.playSound("/assets/sounds/menu_select.wav");
		GameState.state = GameState.MENU;

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
}
