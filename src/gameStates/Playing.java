package gameStates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import entities.Fruit;
import entities.GrowthBoost;
import entities.MagnetPowerUp;
import entities.PowerUp;
import entities.ShieldPowerUp;
import entities.Snake;
import entities.SnakeSegment;
import entities.SpeedBoost;
import game.Game;
import game.GamePanel;
import inputs.ControllerInput;
import inputs.InputType;
import utils.BorderState;
import utils.PlayingUtils;
import utils.SoundPlayer;

public class Playing extends State implements StateMethods {

	private static final int ARENA_MARGIN = 30;
	private static final int ARENA_TOP = 84;
	private static final int ARENA_BOTTOM_MARGIN = 34;

	private static final Color BORDER_IDLE = new Color(70, 150, 85);
	private static final Color BORDER_WARNING = new Color(220, 170, 60);
	private static final Color BORDER_DANGER = new Color(180, 55, 45);

	// Borders
	private static Rectangle topBorder;
	private static Color topBorderColor = BORDER_IDLE;
	private static BorderState topState = BorderState.IDLE;
	private static long topWarningStart = 0;

	private static Rectangle bottomBorder;
	private static Color bottomBorderColor = BORDER_IDLE;
	private static BorderState bottomState = BorderState.IDLE;
	private static long bottomWarningStart = 0;

	private static Rectangle leftBorder;
	private static Color leftBorderColor = BORDER_IDLE;
	private static BorderState leftState = BorderState.IDLE;
	private static long leftWarningStart = 0;

	private static Rectangle rightBorder;
	private static Color rightBorderColor = BORDER_IDLE;
	private static BorderState rightState = BorderState.IDLE;
	private static long rightWarningStart = 0;

	private static long topDangerStart = 0;
	private static long bottomDangerStart = 0;
	private static long leftDangerStart = 0;
	private static long rightDangerStart = 0;
	private final ControllerInput controllerInput;

	private final int DANGER_DURATION = 7000;

	// Game logic
	private long startTime;

	private int fruitEaten;

	// Entities
	private Snake player;
	private List<Fruit> fruits = new ArrayList<>();
	private List<PowerUp> powerUps = new ArrayList<>();
	private long lastFruitSpawnTime = 0;
	private final int FRUIT_SPAWN_INTERVAL = 7000;
	private final int MAX_FRUITS = 4;
	private int pineappleCounter = 4;
	private long lastPowerUpTime = 0;
	private final long POWER_UP_INTERVAL = 14_000;

	public Playing(Game game) {
		super(game);
		this.controllerInput = game.getControllerInput(); // 👈
		initClasses();
	}

	public void initClasses() {
		player = new Snake(200, 200, 24, 24, 4);
		fruits.clear();
		Fruit fruit = new Fruit(300, 300, 24, 24);
		fruit.setIsEated(false);
		fruits.add(fruit);
		lastFruitSpawnTime = System.currentTimeMillis();
		startGame();
	}

	public void startGame() {
		loadInterface();
		player.setMoving(true);
		for (Fruit f : fruits) {
			f.setIsEated(false);
		}
		startTime = System.currentTimeMillis();
	}

	public void checkFruit() {
		Iterator<Fruit> it = fruits.iterator();
		while (it.hasNext()) {
			Fruit f = it.next();
			if (f.getHitbox().intersects(player.getHitbox())) {
				int growth = player.getGrowthMultiplier();
				if (f.getFruitType().equals("Apple")) {
					if (growth != 1) {
						fruitEaten += 3;
					} else {
						fruitEaten++;
					}
					player.eat(3);
				} else {
					if (growth != 1) {
						fruitEaten += 6;
					} else {
						fruitEaten += 2;
					}
					player.eat(6);
				}
				it.remove();
			}
		}
	}

	public void loadInterface() {
		topBorder = new Rectangle(ARENA_MARGIN, ARENA_TOP, GamePanel.getScreenWidth() - ARENA_MARGIN * 2, 2);

		bottomBorder = new Rectangle(ARENA_MARGIN, GamePanel.getScreenHeight() - ARENA_BOTTOM_MARGIN,
				GamePanel.getScreenWidth() - ARENA_MARGIN * 2, 2);

		leftBorder = new Rectangle(ARENA_MARGIN, ARENA_TOP, 2,
				GamePanel.getScreenHeight() - ARENA_TOP - ARENA_BOTTOM_MARGIN);

		rightBorder = new Rectangle(GamePanel.getScreenWidth() - ARENA_MARGIN, ARENA_TOP, 2,
				GamePanel.getScreenHeight() - ARENA_TOP - ARENA_BOTTOM_MARGIN);
	}

	public void borderChange() {
		if (System.currentTimeMillis() - startTime >= 5000) {
			int borderRandom = ThreadLocalRandom.current().nextInt(4);
			startTime = System.currentTimeMillis();

			switch (borderRandom) {
			case 0 -> startBorderWarning("TOP");
			case 1 -> startBorderWarning("BOTTOM");
			case 2 -> startBorderWarning("LEFT");
			case 3 -> startBorderWarning("RIGHT");
			}
		}

		updateBorderStates();
	}

	@Override
	public void update() {
		controllerInput.updateControls();

		// Game logic
		spawnMoreFruits();
		checkFruit();
		checkMagnetPowerUp();
		borderChange();
		player.update();
		for (Fruit f : fruits)
			f.update();

		long now = System.currentTimeMillis();
		if (now - lastPowerUpTime > POWER_UP_INTERVAL) {
			spawnRandomPowerUp();
			lastPowerUpTime = now;
		}

		Iterator<PowerUp> iterator = powerUps.iterator();
		while (iterator.hasNext()) {
			PowerUp pu = iterator.next();
			if (pu.getHitbox().intersects(player.getHitbox())) {
				pu.applyToSnake(player);
				iterator.remove();
			}
		}
	}

	private double normalizeAngle(double angle) {
		while (angle < -Math.PI)
			angle += 2 * Math.PI;
		while (angle > Math.PI)
			angle -= 2 * Math.PI;
		return angle;
	}

	@Override
	public void draw(Graphics graphics) {

		player.render((Graphics2D) graphics);
		if (player.isMoving()) {
			for (Fruit f : fruits) {
				f.render(graphics);
			}
			for (PowerUp pu : powerUps)
				pu.render(graphics);

			drawTopBar(graphics);
			drawScore(graphics);
			drawBorders(graphics);
			drawPowerUpBar(graphics);

			if (fruitEaten >= 50) {
				SoundPlayer.playSound("/assets/sounds/winner.wav");
				gameOver();
			}
		} else {
			SoundPlayer.playSound("/assets/sounds/fail.wav");
			gameOver();
		}
	}

	private void drawTopBar(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		int width = GamePanel.getScreenWidth();

		int barX = 30;
		int barY = 12;
		int barWidth = width - 60;
		int barHeight = 58;

		g2d.setColor(new Color(251, 238, 203));
		g2d.fillRoundRect(barX, barY, barWidth, barHeight, 24, 24);

		g2d.setColor(new Color(120, 85, 50));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawRoundRect(barX, barY, barWidth, barHeight, 24, 24);

		String title = "🐍 Snake";

		g2d.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));

		int titleWidth = g2d.getFontMetrics().stringWidth(title);

		int titleX = width / 2 - titleWidth / 2;

		int titleY = barY + 39;

		g2d.setColor(new Color(100, 80, 60, 120));
		g2d.drawString(title, titleX + 2, titleY + 2);

		g2d.setColor(new Color(55, 40, 25));
		g2d.drawString(title, titleX, titleY);
	}

	private void drawBorders(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		int idleThickness = 2;
		int warningThickness = 4;
		int dangerThickness = 6;

		// === TOP ===
		int topH = switch (topState) {
		case IDLE -> idleThickness;
		case WARNING -> warningThickness;
		case DANGER -> dangerThickness;
		};
		g2d.setColor(topBorderColor);
		g2d.fillRect(topBorder.x, topBorder.y, topBorder.width, topH);

		// === BOTTOM ===
		int bottomH = switch (bottomState) {
		case IDLE -> idleThickness;
		case WARNING -> warningThickness;
		case DANGER -> dangerThickness;
		};
		g2d.setColor(bottomBorderColor);
		g2d.fillRect(bottomBorder.x, bottomBorder.y + (bottomBorder.height - bottomH), bottomBorder.width, bottomH);

		// === LEFT ===
		int leftW = switch (leftState) {
		case IDLE -> idleThickness;
		case WARNING -> warningThickness;
		case DANGER -> dangerThickness;
		};
		g2d.setColor(leftBorderColor);
		g2d.fillRect(leftBorder.x, leftBorder.y, leftW, leftBorder.height);

		// === RIGHT ===
		int rightW = switch (rightState) {
		case IDLE -> idleThickness;
		case WARNING -> warningThickness;
		case DANGER -> dangerThickness;
		};
		g2d.setColor(rightBorderColor);
		g2d.fillRect(rightBorder.x + (rightBorder.width - rightW), rightBorder.y, rightW, rightBorder.height);
	}

	private void startBorderWarning(String side) {

		long now = System.currentTimeMillis();

		switch (side) {
		case "TOP" -> {
			topState = BorderState.WARNING;
			topWarningStart = now;
		}
		case "BOTTOM" -> {
			bottomState = BorderState.WARNING;
			bottomWarningStart = now;
		}
		case "LEFT" -> {
			leftState = BorderState.WARNING;
			leftWarningStart = now;
		}
		case "RIGHT" -> {
			rightState = BorderState.WARNING;
			rightWarningStart = now;
		}
		}
	}

	private void updateBorderStates() {
		long now = System.currentTimeMillis();

		// === TOP ===
		if (topState == BorderState.WARNING) {
			if (now - topWarningStart >= 3000) {
				topState = BorderState.DANGER;
				topDangerStart = now;
				setTopBorderColor(BORDER_DANGER);
			} else {
				long phase = (now - topWarningStart) / 300;
				setTopBorderColor((phase % 2 == 0) ? BORDER_WARNING : BORDER_IDLE);
			}
		} else if (topState == BorderState.DANGER && now - topDangerStart >= DANGER_DURATION) {
			topState = BorderState.IDLE;
			setTopBorderColor(BORDER_IDLE);
		}

		// === BOTTOM ===
		if (bottomState == BorderState.WARNING) {
			if (now - bottomWarningStart >= 3000) {
				bottomState = BorderState.DANGER;
				bottomDangerStart = now;
				setBottomBorderColor(BORDER_DANGER);
			} else {
				long phase = (now - bottomWarningStart) / 300;
				setBottomBorderColor((phase % 2 == 0) ? BORDER_WARNING : BORDER_IDLE);
			}
		} else if (bottomState == BorderState.DANGER && now - bottomDangerStart >= DANGER_DURATION) {
			bottomState = BorderState.IDLE;
			setBottomBorderColor(BORDER_IDLE);
		}

		// === LEFT ===
		if (leftState == BorderState.WARNING) {
			if (now - leftWarningStart >= 3000) {
				leftState = BorderState.DANGER;
				leftDangerStart = now;
				setLeftBorderColor(BORDER_DANGER);
			} else {
				long phase = (now - leftWarningStart) / 300;
				setLeftBorderColor((phase % 2 == 0) ? BORDER_WARNING : BORDER_IDLE);
			}
		} else if (leftState == BorderState.DANGER && now - leftDangerStart >= DANGER_DURATION) {
			leftState = BorderState.IDLE;
			setLeftBorderColor(BORDER_IDLE);
		}

		// === RIGHT ===
		if (rightState == BorderState.WARNING) {
			if (now - rightWarningStart >= 3000) {
				rightState = BorderState.DANGER;
				rightDangerStart = now;
				setRightBorderColor(BORDER_DANGER);
			} else {
				long phase = (now - rightWarningStart) / 300;
				setRightBorderColor((phase % 2 == 0) ? BORDER_WARNING : BORDER_IDLE);
			}
		} else if (rightState == BorderState.DANGER && now - rightDangerStart >= DANGER_DURATION) {
			rightState = BorderState.IDLE;
			setRightBorderColor(BORDER_IDLE);
		}
	}

	private void spawnMoreFruits() {
		long now = System.currentTimeMillis();

		if (fruits.isEmpty() || (now - lastFruitSpawnTime >= FRUIT_SPAWN_INTERVAL && fruits.size() < MAX_FRUITS)) {
			Fruit fruit = new Fruit(0, 0, 24, 24);
			fruit.newFruit();

			if (pineappleCounter == 0) {
				fruit.setFruitType("Pineapple");
				pineappleCounter = ThreadLocalRandom.current().nextInt(3, 7);
			} else {
				fruit.setFruitType("Apple");
				pineappleCounter--;
			}

			fruits.add(fruit);
			lastFruitSpawnTime = now;
		}
	}

	private void spawnRandomPowerUp() {
		int padding = 32;
		int size = 24;

		List<Class<? extends PowerUp>> types = new ArrayList<>();
		types.add(SpeedBoost.class);
		types.add(GrowthBoost.class);
		types.add(ShieldPowerUp.class);
		types.add(MagnetPowerUp.class);

		for (int i = 0; i < types.size(); i++) {
			int index = (int) (Math.random() * types.size());
			Class<? extends PowerUp> selectedType = types.get(index);

			boolean exists = powerUps.stream().anyMatch(p -> selectedType.isInstance(p));
			if (exists)
				continue;

			boolean alreadyHeld = player.getHeldPowerUps().stream()
					.anyMatch(p -> selectedType.isInstance(p) && !p.isUsed());
			if (alreadyHeld)
				continue;

			Point point = PlayingUtils.getValidRandomPosition(topBorder, bottomBorder, leftBorder, rightBorder, size,
					padding);
			int x = point.x;
			int y = point.y;

			PowerUp powerUp = null;
			if (selectedType == SpeedBoost.class) {
				powerUp = new SpeedBoost(x, y);
			} else if (selectedType == GrowthBoost.class) {
				powerUp = new GrowthBoost(x, y);
			} else if (selectedType == ShieldPowerUp.class) {
				powerUp = new ShieldPowerUp(x, y);
			} else if (selectedType == MagnetPowerUp.class) {
				powerUp = new MagnetPowerUp(x, y);
			}

			if (powerUp != null) {
				powerUps.add(powerUp);
				break;
			}
		}
	}

	private void drawPowerUpBar(Graphics g) {
		Class<?>[] allTypes = { SpeedBoost.class, GrowthBoost.class, ShieldPowerUp.class, MagnetPowerUp.class };

		int iconSize = 30;
		int padding = 18;

		int x = 55;
		int y = 26;

		Graphics2D g2d = (Graphics2D) g;

		for (int i = 0; i < allTypes.length; i++) {
			Class<?> type = allTypes[i];
			int drawX = x + i * (iconSize + padding);
			int drawY = y;

			PowerUp held = null;
			for (PowerUp p : player.getHeldPowerUps()) {
				if (type.isInstance(p)) {
					held = p;
					break;
				}
			}

			boolean isActive = false;
			long remaining = 0;
			BufferedImage icon = null;
			BufferedImage grayIcon = null;
			boolean used = false;

			if (type == SpeedBoost.class) {
				isActive = player.getSpeedBoostEndTime() > System.currentTimeMillis();
				remaining = player.getSpeedBoostEndTime() - System.currentTimeMillis();
				icon = SpeedBoost.getIconStatic();
				grayIcon = SpeedBoost.getGrayIconStatic();
			} else if (type == GrowthBoost.class) {
				isActive = player.getGrowthBoostEndTime() > System.currentTimeMillis();
				remaining = player.getGrowthBoostEndTime() - System.currentTimeMillis();
				icon = GrowthBoost.getIconStatic();
				grayIcon = GrowthBoost.getGrayIconStatic();
			} else if (type == ShieldPowerUp.class) {
				isActive = player.hasShield();
				remaining = player.getShieldEndTime() - System.currentTimeMillis();
				icon = ShieldPowerUp.getIconStatic();
				grayIcon = ShieldPowerUp.getGrayIconStatic();
			} else if (type == MagnetPowerUp.class) {
				isActive = player.hasMagnet();
				remaining = player.getMagnetEndTime() - System.currentTimeMillis();
				icon = MagnetPowerUp.getIconStatic();
				grayIcon = MagnetPowerUp.getGrayIconStatic();
			}

			if (held != null)
				used = held.isUsed();

			if (isActive) {
				if (type == SpeedBoost.class) {
					g2d.setColor(new Color(255, 190, 60, 190));
				} else if (type == GrowthBoost.class) {
					g2d.setColor(new Color(190, 80, 255, 190));
				} else if (type == ShieldPowerUp.class) {
					g2d.setColor(new Color(70, 170, 255, 190));
				} else if (type == MagnetPowerUp.class) {
					g2d.setColor(new Color(255, 90, 90, 190));
				}

				g2d.fillOval(drawX - 5, drawY - 5, iconSize + 10, iconSize + 10);
			}

			BufferedImage toDraw = (!isActive && (held == null || used)) ? grayIcon : icon;

			if (toDraw != null) {
				g2d.drawImage(toDraw, drawX, drawY, iconSize, iconSize, null);
			}

			if (isActive && remaining > 0) {
				String time = (remaining / 1000) + "s";

				g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));

				if (type == SpeedBoost.class) {
					g2d.setColor(new Color(180, 110, 20));
				} else if (type == GrowthBoost.class) {
					g2d.setColor(new Color(150, 40, 200));
				} else if (type == ShieldPowerUp.class) {
					g2d.setColor(new Color(30, 100, 190));
				} else if (type == MagnetPowerUp.class) {
					g2d.setColor(new Color(190, 45, 45));
				}

				g2d.drawString(time, drawX + iconSize + 4, drawY + iconSize - 4);
			}
		}
	}

	private void checkMagnetPowerUp() {
		if (player.isMagnetActive()) {
			SnakeSegment head = player.getSegments().get(0);
			for (Fruit f : fruits) {
				float dx = head.getX() - f.getX();
				float dy = head.getY() - f.getY();
				float dist = (float) Math.sqrt(dx * dx + dy * dy);
				float step = 2.0f;

				if (dist > step) {
					float newX = f.getX() + dx / dist * step;
					float newY = f.getY() + dy / dist * step;
					f.setPosition(newX, newY);
					f.updateHitbox();
				}
			}
		}
	}

	private void drawScore(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;

		String scoreText = "Score: " + fruitEaten;

		g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));

		g2d.setColor(new Color(80, 55, 30));

		int textWidth = g2d.getFontMetrics().stringWidth(scoreText);

		int x = GamePanel.getScreenWidth() - 60 - textWidth;

		int y = 49;

		g2d.drawString(scoreText, x, y);
	}

	public void gameOver() {
		resetBorderStates();
		if (fruitEaten > 50) {
			fruitEaten = 50;
		}
		game.getEndGame().setFruitEaten(fruitEaten);
		game.getEndGame().setVictory(fruitEaten >= 50);
		GameState.state = GameState.ENDGAME;
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

		if (e.getKeyCode() == KeyEvent.VK_H) {
			game.setSelectedInput(InputType.CONTROLLER);
			System.out.println("Switched to CONTROLLER via key H");
			return;
		}

		if (game.getSelectedInput() != InputType.KEYBOARD)
			return;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_A -> player.setLeftPressed(true);
		case KeyEvent.VK_D -> player.setRightPressed(true);

		case KeyEvent.VK_E -> {
			activateShield();
		}

		case KeyEvent.VK_Q -> {
			activateMagnet();
		}
		}
	}

	public void activateShield() {
		for (PowerUp pu : player.getHeldPowerUps()) {
			if (!pu.isUsed() && pu instanceof ShieldPowerUp sp) {
				sp.activate(player);
				break;
			}
		}

	}

	public void activateMagnet() {
		for (PowerUp pu : player.getHeldPowerUps()) {
			if (pu instanceof MagnetPowerUp mp && !mp.isUsed()) {
				mp.activate(player);
				break;
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A -> player.setLeftPressed(false);
		case KeyEvent.VK_D -> player.setRightPressed(false);

		}
	}

	public void restartGame() {
		fruitEaten = 0;
		lastFruitSpawnTime = 0;
		lastPowerUpTime = 0;
		pineappleCounter = 4;
		player = new Snake(200, 200, 24, 24, 4);
		player.resetPowerUps();
		fruits.clear();
		powerUps.clear();
		Fruit fruit = new Fruit(300, 300, 24, 24);
		fruit.setIsEated(false);
		fruits.add(fruit);

		lastFruitSpawnTime = System.currentTimeMillis();
		loadInterface();
		player.setMoving(true);

		startTime = System.currentTimeMillis();
		resetBorderStates();

	}

	private void resetBorderStates() {
		topState = BorderState.IDLE;
		bottomState = BorderState.IDLE;
		leftState = BorderState.IDLE;
		rightState = BorderState.IDLE;

		topWarningStart = 0;
		bottomWarningStart = 0;
		leftWarningStart = 0;
		rightWarningStart = 0;

		setTopBorderColor(BORDER_IDLE);
		setBottomBorderColor(BORDER_IDLE);
		setLeftBorderColor(BORDER_IDLE);
		setRightBorderColor(BORDER_IDLE);
	}

	public Snake getPlayer() {
		return this.player;
	}

	public static Rectangle getTopBorder() {
		return topBorder;
	}

	public static Rectangle getBottomBorder() {
		return bottomBorder;
	}

	public static Rectangle getLeftBorder() {
		return leftBorder;
	}

	public static Rectangle getRightBorder() {
		return rightBorder;
	}

	public static Color getTopBorderColor() {
		return topBorderColor;
	}

	public static void setTopBorderColor(Color color) {
		topBorderColor = color;
	}

	public static Color getBottomBorderColor() {
		return bottomBorderColor;
	}

	public static void setBottomBorderColor(Color color) {
		bottomBorderColor = color;
	}

	public static Color getLeftBorderColor() {
		return leftBorderColor;
	}

	public static void setLeftBorderColor(Color color) {
		leftBorderColor = color;
	}

	public static Color getRightBorderColor() {
		return rightBorderColor;
	}

	public static void setRightBorderColor(Color color) {
		rightBorderColor = color;
	}

	public static Color getBorderDangerColor() {
		return BORDER_DANGER;
	}

}
