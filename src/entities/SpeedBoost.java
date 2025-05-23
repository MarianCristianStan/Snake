package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import utils.PlayingUtils;
import utils.PowerUpSounds;

public class SpeedBoost extends PowerUp {

	private final long duration = 4000;

	private static BufferedImage ICON;
	private static BufferedImage GRAY_ICON;

	
	static {
	    try {
	        InputStream is = SpeedBoost.class.getResourceAsStream("/assets/power_up/speed.png");
	        BufferedImage original = ImageIO.read(is);
	        ICON = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	        ICON.getGraphics().drawImage(original.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, null);
	        GRAY_ICON = PlayingUtils.makeGrayscale(ICON);
	        is.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public SpeedBoost(float x, float y) {
		super(x, y, 32);
		loadImageOnce();
		icon = ICON; 
	}

	private void loadImageOnce() {
		if (ICON != null && GRAY_ICON != null) return;

		try {
			InputStream is = getClass().getResourceAsStream("/assets/power_up/speed.png");
			BufferedImage original = ImageIO.read(is);
			ICON = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = ICON.createGraphics();
			g.drawImage(original.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			g.dispose();

			GRAY_ICON = PlayingUtils.makeGrayscale(ICON);

			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyToSnake(Snake snake) {
		PowerUpSounds.playCollect();
		snake.activateSpeedBoost(1.85f, duration);
		snake.activatePowerUp("Speed", duration);
		deactivate();
	}

	@Override
	public BufferedImage getIcon() {
		return icon;
	}
	public BufferedImage getGrayIcon() {
		return GRAY_ICON;
	}

	@Override
	public void render(Graphics g) {
		if (icon != null) {
			g.drawImage(icon, (int) x, (int) y, null);
		} else {
			g.setColor(Color.YELLOW);
			g.fillOval((int) x, (int) y, width, height);
		}
	}
	public static BufferedImage getIconStatic() {
		 return ICON;
		
	}
	public static BufferedImage getGrayIconStatic() {
	    return GRAY_ICON;
	}
}
