package gameStates;

import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

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
import utils.BorderState;
import utils.PlayingUtils;
import utils.SoundPlayer;

public class Playing extends State implements StateMethods {

	// Borders
	private static Rectangle topBorder;
	private static Color topBorderColor = Color.GREEN;
	private static BorderState topState = BorderState.IDLE;
	private static long topWarningStart = 0;

	private static Rectangle bottomBorder;
	private static Color bottomBorderColor = Color.GREEN;
	private static BorderState bottomState = BorderState.IDLE;
	private static long bottomWarningStart = 0;

	private static Rectangle leftBorder;
	private static Color leftBorderColor = Color.GREEN;
	private static BorderState leftState = BorderState.IDLE;
	private static long leftWarningStart = 0;

	private static Rectangle rightBorder;
	private static Color rightBorderColor = Color.GREEN;
	private static BorderState rightState = BorderState.IDLE;
	private static long rightWarningStart = 0;

	private static long topDangerStart = 0;
	private static long bottomDangerStart = 0;
	private static long leftDangerStart = 0;
	private static long rightDangerStart = 0;

	private final int DANGER_DURATION = 7000;

	// Game logic
	private long startTime;
	private BufferedImage gameTitleImg;
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
		initClasses();
	}

	public void initClasses() {
		player = new Snake(200, 200, 24, 24);
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
		topBorder = new Rectangle(30, 84, GamePanel.getScreenWidth() - 60, 2);
		bottomBorder = new Rectangle(30, GamePanel.getScreenHeight() - 34, GamePanel.getScreenWidth() - 60, 2);
		leftBorder = new Rectangle(30, 84, 2, GamePanel.getScreenHeight() - 115);
		rightBorder = new Rectangle(GamePanel.getScreenWidth() - 30, 84, 2, GamePanel.getScreenHeight() - 115);

		InputStream gameTitleStream = getClass().getResourceAsStream("/assets/ui/snaketitle.jpg");
		try {
			gameTitleImg = ImageIO.read(gameTitleStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gameTitleStream != null)
					gameTitleStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		spawnMoreFruits();
		checkFruit();
		checkMagnetPowerUp();
		borderChange();
		player.update();
		for (Fruit f : fruits) {
			f.update();
		}

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

	@Override
	public void draw(Graphics graphics) {
		player.render((Graphics2D) graphics);
		if (player.isMoving()) {
			for (Fruit f : fruits) {
				f.render(graphics);
			}
			for (PowerUp pu : powerUps)
				pu.render(graphics);

			graphics.drawImage(gameTitleImg, 0, 0, GamePanel.getScreenWidth(), 54, null);

			drawScore(graphics);
			drawBorders(graphics);
			drawPowerUpBar(graphics);

			if (fruitEaten >= 20) {
				SoundPlayer.playSound("/assets/sounds/winner.wav");
				gameOver();
			}
		} else {
			SoundPlayer.playSound("/assets/sounds/fail.wav");
			gameOver();
		}
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
				setTopBorderColor(Color.RED);
			} else {
				long phase = (now - topWarningStart) / 300;
				setTopBorderColor((phase % 2 == 0) ? Color.YELLOW : Color.GREEN);
			}
		} else if (topState == BorderState.DANGER && now - topDangerStart >= DANGER_DURATION) {
			topState = BorderState.IDLE;
			setTopBorderColor(Color.GREEN);
		}

		// === BOTTOM ===
		if (bottomState == BorderState.WARNING) {
			if (now - bottomWarningStart >= 3000) {
				bottomState = BorderState.DANGER;
				bottomDangerStart = now;
				setBottomBorderColor(Color.RED);
			} else {
				long phase = (now - bottomWarningStart) / 300;
				setBottomBorderColor((phase % 2 == 0) ? Color.YELLOW : Color.GREEN);
			}
		} else if (bottomState == BorderState.DANGER && now - bottomDangerStart >= DANGER_DURATION) {
			bottomState = BorderState.IDLE;
			setBottomBorderColor(Color.GREEN);
		}

		// === LEFT ===
		if (leftState == BorderState.WARNING) {
			if (now - leftWarningStart >= 3000) {
				leftState = BorderState.DANGER;
				leftDangerStart = now;
				setLeftBorderColor(Color.RED);
			} else {
				long phase = (now - leftWarningStart) / 300;
				setLeftBorderColor((phase % 2 == 0) ? Color.YELLOW : Color.GREEN);
			}
		} else if (leftState == BorderState.DANGER && now - leftDangerStart >= DANGER_DURATION) {
			leftState = BorderState.IDLE;
			setLeftBorderColor(Color.GREEN);
		}

		// === RIGHT ===
		if (rightState == BorderState.WARNING) {
			if (now - rightWarningStart >= 3000) {
				rightState = BorderState.DANGER;
				rightDangerStart = now;
				setRightBorderColor(Color.RED);
			} else {
				long phase = (now - rightWarningStart) / 300;
				setRightBorderColor((phase % 2 == 0) ? Color.YELLOW : Color.GREEN);
			}
		} else if (rightState == BorderState.DANGER && now - rightDangerStart >= DANGER_DURATION) {
			rightState = BorderState.IDLE;
			setRightBorderColor(Color.GREEN);
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

		int iconSize = 32;
		int padding = 40;
		int x = 50;
		int y = 10;

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
			}else if (type == MagnetPowerUp.class) {
				isActive = player.hasMagnet();
				remaining = player.getMagnetEndTime() - System.currentTimeMillis();
				icon = MagnetPowerUp.getIconStatic();
				grayIcon = MagnetPowerUp.getGrayIconStatic();
			}

			if (held != null)
				used = held.isUsed();

			if (isActive) {
				g2d.setColor(new Color(255, 255, 0, 128));
				g2d.fillOval(drawX - 4, drawY - 4, iconSize + 8, iconSize + 8);
			}

			BufferedImage toDraw = (!isActive && (held == null || used)) ? grayIcon : icon;

			if (toDraw != null) {
				g2d.drawImage(toDraw, drawX, drawY, iconSize, iconSize, null);
			}

			if (isActive && remaining > 0) {
				String time = (remaining / 1000) + "s";
				g2d.setFont(new Font("Arial", Font.PLAIN, 12));
				g2d.setColor(Color.WHITE);
				g2d.drawString(time, drawX + iconSize + 4, drawY + iconSize - 4);
			}
		}
	}
	
	private void checkMagnetPowerUp()
	{
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
		graphics.setColor(Color.GREEN);
		graphics.setFont(new Font("Ink free", Font.BOLD, 40));
		FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
		graphics.drawString("Score: " + fruitEaten,
				(GamePanel.getScreenWidth() - metrics.stringWidth("Score: " + fruitEaten)) - 100,
				graphics.getFont().getSize());
	}

	public void gameOver() {
		resetBorderStates();
		if (fruitEaten > 20) {
			fruitEaten = 20;
		}
		game.getEndGame().setFruitEaten(fruitEaten);
		game.getEndGame().setVictory(fruitEaten >= 20);
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
	    if (keyCode == KeyEvent.VK_UNDEFINED) return;

	    KeyEvent fakeEvent = new KeyEvent(
	        game.getGamePanel(),
	        KeyEvent.KEY_PRESSED,
	        System.currentTimeMillis(),
	        0,
	        keyCode,
	        key
	    );
	    keyPressed(fakeEvent);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A -> player.setLeftPressed(true);
		case KeyEvent.VK_D -> player.setRightPressed(true);
		
		case KeyEvent.VK_E -> {
			for (PowerUp pu : player.getHeldPowerUps()) {
				if (!pu.isUsed() && pu instanceof ShieldPowerUp sp) {
					sp.activate(player);
					break;
				}

			}
		}
		case KeyEvent.VK_Q -> {
		    for (PowerUp pu : player.getHeldPowerUps()) {
		        if (pu instanceof MagnetPowerUp mp && !mp.isUsed()) {
		            mp.activate(player);
		            break;
		        }
		    }
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
		player = new Snake(200, 200, 24, 24);
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

		setTopBorderColor(Color.GREEN);
		setBottomBorderColor(Color.GREEN);
		setLeftBorderColor(Color.GREEN);
		setRightBorderColor(Color.GREEN);
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
}
