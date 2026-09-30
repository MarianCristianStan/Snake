package gameStates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import game.Game;
import game.GamePanel;
import inputs.InputType;
import ui.MenuBackgroundDemo;
import utils.SoundPlayer;

public class Menu extends State implements StateMethods {

	private JButton playButton;
	private JButton exitButton;
	private JButton[] menuButtons;
	private JButton bestScoresButton;
	private JButton instructionsButton;
	private int selectedIndex = 0;
	private boolean buttonsAdded = false;

	private final MenuBackgroundDemo backgroundDemo;
	private Image keyboardImage;
	private Image controllerImage;

	private final Color BUTTON_NORMAL = new Color(251, 238, 203);
	private final Color BUTTON_SELECTED = new Color(224, 190, 120);

	private final Color BORDER_NORMAL = new Color(120, 85, 50);
	private final Color BORDER_SELECTED = new Color(100, 60, 25);

	private static final int PANEL_WIDTH = 420;
	private static final int PANEL_HEIGHT = 500;

	private enum ButtonState {
		NORMAL, SELECTED
	}

	public Menu(Game game) {
		super(game);
		backgroundDemo = new MenuBackgroundDemo(getPanelBounds());
		loadKeyboardImage();
		loadControllerImage();
	}

	@Override
	public void update() {
		backgroundDemo.update();
		game.getControllerInput().updateControls();

		if (game.getSelectedInput() == InputType.CONTROLLER && game.getControllerInput().isButtonPressed(1)) {
			game.setSelectedInput(InputType.KEYBOARD);
			System.out.println("Switched to KEYBOARD via B");
			return;
		}
	}

	public void moveCursorUp() {
		selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
		SoundPlayer.playSound("/assets/sounds/menu_select.wav");
		updateButtonSelection();
	}

	public void moveCursorDown() {
		selectedIndex = (selectedIndex + 1) % menuButtons.length;
		SoundPlayer.playSound("/assets/sounds/menu_select.wav");
		updateButtonSelection();
	}

	public void doSelect() {
		menuButtons[selectedIndex].doClick();
		SoundPlayer.playSound("/assets/sounds/enter.wav");

	}

	@Override
	public void draw(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		int panelX = getPanelX();
		int panelY = getPanelY();
		int panelCenterX = panelX + PANEL_WIDTH / 2;

		backgroundDemo.setAvoidArea(getPanelBounds());
		backgroundDemo.draw(g2d);

		g2d.setColor(new Color(251, 238, 203));
		g2d.fillRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);

		g2d.setColor(new Color(120, 85, 50));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRoundRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 40, 40);

		String title = "🐍 Snake";

		Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 42);
		g2d.setFont(titleFont);

		int titleWidth = g2d.getFontMetrics().stringWidth(title);

		g2d.setColor(new Color(100, 80, 60, 120));
		g2d.drawString(title, panelCenterX - titleWidth / 2 + 2, panelY + 72);

		g2d.setColor(new Color(55, 40, 25));
		g2d.drawString(title, panelCenterX - titleWidth / 2, panelY + 70);

		if (!buttonsAdded) {
			initButtons();
		}

		drawInputHint(g2d, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
	}

	private void drawInputHint(Graphics2D g2d, int panelX, int panelY, int panelWidth, int panelHeight) {
		int iconSize = 28;
		int spacing = 10;
		int y = panelY + panelHeight - 52;

		Image activeImage;
		String text;

		if (game.getSelectedInput() == InputType.KEYBOARD) {
			activeImage = keyboardImage;
			text = "Keyboard active";
		} else {
			activeImage = controllerImage;
			text = "Controller active";
		}

		g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		g2d.setColor(new Color(120, 60, 0));

		int textWidth = g2d.getFontMetrics().stringWidth(text);
		int totalWidth = iconSize + spacing + textWidth;

		int startX = panelX + panelWidth / 2 - totalWidth / 2;

		if (activeImage != null) {
			g2d.drawImage(activeImage, startX, y - iconSize + 6, iconSize, iconSize, null);
		}

		g2d.drawString(text, startX + iconSize + spacing, y);
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

	public void simulateKeyPress(char key) {
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(key);
		if (keyCode == KeyEvent.VK_UNDEFINED)
			return;
		KeyEvent fakeEvent = new KeyEvent(game.getGamePanel(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
				keyCode, key);
		keyPressed(fakeEvent);
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_H) {
			if (game.getControllerInput().isConnected()) {
				game.setSelectedInput(InputType.CONTROLLER);
				System.out.println("Switched to CONTROLLER via key H");
			} else {
				System.out.println("No controller connected — staying on KEYBOARD");
			}
			return;
		}

		if (game.getSelectedInput() != InputType.CONTROLLER) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W -> {
				moveCursorUp();
			}
			case KeyEvent.VK_S -> {
				moveCursorDown();
			}
			case KeyEvent.VK_ENTER -> {
				doSelect();
			}
			}
		}
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
	}

	private void loadKeyboardImage() {
		try {
			InputStream keyboardStream = getClass().getResourceAsStream("/assets/ui/keyboard.png");
			if (keyboardStream != null) {
				keyboardImage = ImageIO.read(keyboardStream);
				keyboardStream.close();
			} else {
				System.err.println("Keyboard image not found!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadControllerImage() {
		try {
			InputStream controllerStream = getClass().getResourceAsStream("/assets/ui/joystick.png");
			if (controllerStream != null) {
				controllerImage = ImageIO.read(controllerStream);
				controllerStream.close();
			} else {
				System.err.println("Controller image not found!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initButtons() {

		int buttonWidth = 260;
		int panelX = getPanelX();

		int buttonX = panelX + PANEL_WIDTH / 2 - buttonWidth / 2;

		int startY = GamePanel.getScreenHeight() / 2 - 110;

		playButton = createModernButton("Play", buttonX, startY, 0);
		instructionsButton = createModernButton("Instructions", buttonX, startY + 70, 1);
		bestScoresButton = createModernButton("Best Scores", buttonX, startY + 140, 2);
		exitButton = createModernButton("Exit", buttonX, startY + 210, 3);

		menuButtons = new JButton[] { playButton, instructionsButton, bestScoresButton, exitButton };

		playButton.addActionListener(e ->

		{
			GameState.state = GameState.PLAYING;
			game.getPlaying().restartGame();
			removeButtons();
			game.getGamePanel().requestFocusInWindow();
		});

		instructionsButton.addActionListener(e -> {
			GameState.state = GameState.INSTRUCTIONS;
			removeButtons();
			game.getGamePanel().requestFocusInWindow();
		});

		bestScoresButton.addActionListener(e -> {
			GameState.state = GameState.BESTSCORES;
			removeButtons();
			game.getGamePanel().requestFocusInWindow();
		});

		exitButton.addActionListener(e -> System.exit(0));

		game.getGamePanel().add(playButton);
		game.getGamePanel().add(instructionsButton);
		game.getGamePanel().add(bestScoresButton);
		game.getGamePanel().add(exitButton);

		updateButtonSelection();

		buttonsAdded = true;

	}

	private JButton createModernButton(String text, int x, int y, int index) {
		JButton button = new JButton(text);

		button.setBounds(x, y, 260, 55);
		button.setFont(new Font("Segoe UI", Font.BOLD, 20));

		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setContentAreaFilled(true);
		button.setOpaque(true);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (selectedIndex != index) {
					selectedIndex = index;
					SoundPlayer.playSound("/assets/sounds/menu_select.wav");
					updateButtonSelection();
				}
			}
		});

		return button;
	}

	private void updateButtonSelection() {
		if (menuButtons == null) {
			return;
		}

		for (int i = 0; i < menuButtons.length; i++) {
			ButtonState state = i == selectedIndex ? ButtonState.SELECTED : ButtonState.NORMAL;
			applyButtonState(menuButtons[i], state);
		}
	}

	private void applyButtonState(JButton button, ButtonState state) {
		switch (state) {

		case NORMAL -> {
			button.setForeground(new Color(55, 40, 25));
			button.setBackground(BUTTON_NORMAL);
			button.setBorder(BorderFactory.createLineBorder(BORDER_NORMAL, 3, true));
		}

		case SELECTED -> {
			button.setForeground(new Color(70, 45, 20));
			button.setBackground(BUTTON_SELECTED);
			button.setBorder(BorderFactory.createLineBorder(BORDER_SELECTED, 4, true));
		}

		}
	}

	private void removeButtons() {
		game.getGamePanel().remove(playButton);
		game.getGamePanel().remove(instructionsButton);
		game.getGamePanel().remove(bestScoresButton);
		game.getGamePanel().remove(exitButton);
		buttonsAdded = false;
		game.getGamePanel().repaint();
	}
}
