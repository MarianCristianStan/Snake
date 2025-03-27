package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import game.GamePanel;
import gameStates.Playing;

public class Fruit extends Entity {

	GamePanel gamePanel;
	private BufferedImage appleImage;
	private BufferedImage pineappleImage;
	private boolean isEated = false;
	private Random random;
	private String fruitType = "Apple";
	private int randomCounterPineapple=5;

	public Fruit(float x, float y, int width, int height) {
		super(x, y, width, height);
		random = new Random();
		loadFruit();
		setFruitType("Apple");
	}

	public void render(Graphics graphics) {
		if (fruitType.compareTo("Apple") == 0)
			graphics.drawImage(appleImage, (int) x, (int) y, null);
		else if (fruitType.compareTo("Pineapple") == 0)
			graphics.drawImage(pineappleImage, (int) x, (int) y, null);
		drawHitbox(graphics);
	}

	public void update() {

		if (getIsEated()) {
			newFruit();
			updateHitbox();
			isEated = false;
		}
	}

	private void loadFruit() {
		InputStream appleStream = getClass().getResourceAsStream("/assets/fruit/apple.png");
		InputStream pineappleStream = getClass().getResourceAsStream("/assets/fruit/pineapple.png");

		try {
			BufferedImage appleFruit = ImageIO.read(appleStream);
			BufferedImage pineappleFruit = ImageIO.read(pineappleStream);

			appleImage = appleFruit;
			pineappleImage = pineappleFruit;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				pineappleStream.close();
				appleStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void newFruit() {

		if (randomCounterPineapple == 0) {
			this.fruitType = "Pineapple";
			randomCounterPineapple = (ThreadLocalRandom.current().nextInt() % 4) + 4;

		} else {
			randomCounterPineapple--;
			this.fruitType = "Apple";
		}
		Rectangle topBorder = Playing.getTopBorder();
		Rectangle bottomBorder = Playing.getBottomBorder();
		Rectangle leftBorder = Playing.getLeftBorder();
		Rectangle rightBorder = Playing.getRightBorder();
		x = random.nextInt((int) (rightBorder.x - leftBorder.x - 20)) + leftBorder.x;
		y = random.nextInt((int) (bottomBorder.y - topBorder.y - 20)) + topBorder.y;

	}

	public boolean getIsEated() {
		return isEated;
	}

	public void setIsEated(boolean eated) {
		this.isEated = eated;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public String getFruitType() {
		return fruitType;
	}

	public void setFruitType(String fruitType) {
		this.fruitType = fruitType;
	}

}
