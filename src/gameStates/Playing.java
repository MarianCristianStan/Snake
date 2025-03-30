package gameStates;

import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import entities.Fruit;
import entities.Snake;
import game.Game;
import game.GamePanel;

public class Playing extends State implements StateMethods {

	// border
	private static Rectangle topBorder;
	private static Color topBorderColor = Color.GREEN;
	private static Rectangle bottomBorder;
	private static Color bottomBorderColor = Color.GREEN;
	private static Rectangle leftBorder;
	private static Color leftBorderColor = Color.GREEN;
	private static Rectangle rightBorder;
	private static Color rightBorderColor = Color.GREEN;

	// game logic
	private long startTime;
	private BufferedImage gameTitleImg;
	private int fruitEaten;

	// entities
	private Snake player;
	private Fruit fruit;

	public Playing(Game game) {
		super(game);
		initClasses();
	}

	//init entities and start game
	public void initClasses() {
		player = new Snake(200, 200, 24, 24);
		fruit = new Fruit(300, 300, 24, 24);
		startGame();
	}

	public void startGame() {
		loadInterface();
		player.setMoving(true);
		fruit.setIsEated(false);
		startTime = System.currentTimeMillis();
	}

	// game logic , check for fruit
	public void checkFruit() {
		if (fruit.getHitbox().intersects(player.getHitbox())) {
			if (fruit.getFruitType().compareTo("Apple") == 0) {
				fruitEaten++;
				player.eat(5);
			} else {
				fruitEaten += 2;
				player.eat(10);
			}
			fruit.setIsEated(true);
		}
	}

	// load playing interface
	public void loadInterface() {
		topBorder = new Rectangle(30, 84, GamePanel.getScreenWidth() - 60, 2);
		bottomBorder = new Rectangle(30, GamePanel.getScreenHeight() - 34, GamePanel.getScreenWidth() - 60, 2);
		leftBorder = new Rectangle(30, 84, 2, GamePanel.getScreenHeight() - 115);
		rightBorder = new Rectangle(GamePanel.getScreenWidth() - 30, 84, 2, GamePanel.getScreenHeight() - 115);

		InputStream gameTitleStream = getClass().getResourceAsStream("/assets/ui/snaketitle.jpg");
		try {
			BufferedImage imgTitle = ImageIO.read(gameTitleStream);
			gameTitleImg = imgTitle;

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				gameTitleStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// border 
	public void borderChange() {

		if (System.currentTimeMillis() - startTime >= 5000) {

			int borderRandom = ThreadLocalRandom.current().nextInt() % 4;
			if (borderRandom == 0) {
				setTopBorderColor(Color.RED);
				setBottomBorderColor(Color.GREEN);
				setLeftBorderColor(Color.GREEN);
				setRightBorderColor(Color.GREEN);

			} else if (borderRandom == 1) {
				setTopBorderColor(Color.GREEN);
				setBottomBorderColor(Color.RED);
				setLeftBorderColor(Color.GREEN);
				setRightBorderColor(Color.GREEN);

			} else if (borderRandom == 2) {
				setTopBorderColor(Color.GREEN);
				setBottomBorderColor(Color.GREEN);
				setLeftBorderColor(Color.RED);
				setRightBorderColor(Color.GREEN);
			} else if (borderRandom == 3) {
				setTopBorderColor(Color.GREEN);
				setBottomBorderColor(Color.GREEN);
				setLeftBorderColor(Color.GREEN);
				setRightBorderColor(Color.RED);
			}
			startTime = System.currentTimeMillis();
		}
	}

	// update my game
	@Override
	public void update() {
		checkFruit();
		/* borderChange(); */
		player.update();
		fruit.update();

	}


	@Override
	public void draw(Graphics graphics) {
		
		player.render((Graphics2D)graphics);
		
		if (player.isMoving() == true) {
			fruit.render(graphics);
			graphics.setColor(topBorderColor);
			graphics.fillRect(topBorder.x, topBorder.y, topBorder.width, topBorder.height);
			graphics.setColor(bottomBorderColor);
			graphics.fillRect(bottomBorder.x, bottomBorder.y, bottomBorder.width, bottomBorder.height);
			graphics.setColor(leftBorderColor);
			graphics.fillRect(leftBorder.x, leftBorder.y, leftBorder.width, leftBorder.height);
			graphics.setColor(rightBorderColor);
			graphics.fillRect(rightBorder.x, rightBorder.y, rightBorder.width, rightBorder.height);

			graphics.drawImage(gameTitleImg, 0, 0, GamePanel.getScreenWidth(), 54, null);
			graphics.setColor(Color.GREEN);
			graphics.setFont(new Font("Ink free", Font.BOLD, 40));
			FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
			graphics.drawString("Score: " + fruitEaten,
					(GamePanel.getScreenWidth() - metrics.stringWidth("Score: " + fruitEaten)) - 100,
					graphics.getFont().getSize());
		} else if (player.isMoving() == false)
			gameOver(graphics);

	}

	// game over render
	public void gameOver(Graphics graphics) {

		graphics.setColor(Color.RED);
		graphics.setFont(new Font("Ink free", Font.BOLD, 100));
		FontMetrics metricsEnd = graphics.getFontMetrics(graphics.getFont());
		graphics.drawString("Game Over", (GamePanel.getScreenWidth() - metricsEnd.stringWidth("Game Over")) / 2,
				(GamePanel.getScreenHeight() / 2));

		graphics.setColor(Color.GREEN);
		graphics.setFont(new Font("Ink free", Font.BOLD, 60));
		FontMetrics metricsScore = graphics.getFontMetrics(graphics.getFont());
		graphics.drawString("Your Score : " + fruitEaten,
				(GamePanel.getScreenWidth() - metricsScore.stringWidth("Your Score : " + fruitEaten)) / 2,
				graphics.getFont().getSize() + 100);
	}

	
	// events in playing mode
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
	    switch (e.getKeyCode()) {
	        case KeyEvent.VK_A -> player.setLeftPressed(true);
	        case KeyEvent.VK_D -> player.setRightPressed(true);
	        case KeyEvent.VK_BACK_SPACE -> GameState.state = GameState.MENU;
	    }
	}

	@Override
	public void keyReleased(KeyEvent e) {
	    switch (e.getKeyCode()) {
	        case KeyEvent.VK_A -> player.setLeftPressed(false);
	        case KeyEvent.VK_D -> player.setRightPressed(false);
	    }
	}

	public Snake getPlayer() {
		return this.player;
	}

	public Fruit getFruit() {
		return this.fruit;
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
