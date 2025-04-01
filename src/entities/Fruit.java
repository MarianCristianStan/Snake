package entities;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;

import game.GamePanel;
import gameStates.Playing;
import utils.PlayingUtils;

public class Fruit extends Entity {

	GamePanel gamePanel;
	private BufferedImage appleImage;
	private BufferedImage pineappleImage;
	private boolean isEated = false;
	private String fruitType = "Apple";
	private float animationOffset = 0f;
	private long animationStartTime = System.currentTimeMillis();

	
	public Fruit(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadFruit();
		setFruitType("Apple");
	}

	public void render(Graphics graphics) {
	    long now = System.currentTimeMillis();
	    float time = (now - animationStartTime) / 100.0f;
	    animationOffset = (float) Math.sin(time) * 3; 

	    int drawX = (int) x;
	    int drawY = (int) (y + animationOffset);

	    if (fruitType.equals("Apple"))
	        graphics.drawImage(appleImage, drawX, drawY, null);
	    else if (fruitType.equals("Pineapple"))
	        graphics.drawImage(pineappleImage, drawX, drawY, null);

//	     drawHitbox(graphics);
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

		Point point = PlayingUtils.getValidRandomPosition(Playing.getTopBorder(), Playing.getBottomBorder(), Playing.getLeftBorder(), Playing.getRightBorder(), 24, 30);
		x = point.x;
		y = point.y;
		updateHitbox();
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

	public void setPosition(float newX, float newY) {
		this.x = newX;
		this.y = newY;
	}

}
