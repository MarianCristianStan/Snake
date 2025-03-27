package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.KeyboardInput;
import inputs.MouseInput;


public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 960;
	private static final int UNIT_SIZE = 1;
	private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	private Game game;

	public GamePanel(Game game) {
		this.game = game;
		setPanelSize();
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new KeyboardInput(this));
		this.addMouseListener(new MouseInput(this));
	}

	private void setPanelSize() {
		Dimension resolution = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setMinimumSize(resolution);
		this.setPreferredSize(resolution);
		this.setMaximumSize(resolution);

	}
	
	public void updateGame() {

	}

	@Override
	public void paint(Graphics graphics) {

		super.paint(graphics);
		// draw(graphics);
		game.render(graphics);
	}

	public void draw(Graphics graphics) {

	}

	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getGameUnits() {
		return GAME_UNITS;
	}

	public GamePanel getGamePanel() {
		return this;
	}

	public Game getGame() {
		return this.game;
	}

}
