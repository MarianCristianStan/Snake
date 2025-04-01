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

public class ShieldPowerUp extends PowerUp {

	private final long duration = 3000;

	private static BufferedImage ICON;
	private static BufferedImage GRAY_ICON;
	
	static {
	    try {
	        InputStream is = ShieldPowerUp.class.getResourceAsStream("/assets/power_up/shield.png");
	        BufferedImage original = ImageIO.read(is);
	        ICON = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	        ICON.getGraphics().drawImage(original.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, null);
	        GRAY_ICON = PlayingUtils.makeGrayscale(ICON);
	        is.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	

	public ShieldPowerUp(float x, float y) {
		super(x, y, 32);
		this.requiresManualActivation = true;
		loadImageOnce();
		icon = ICON;
	}

	private void loadImageOnce() {
		if (ICON != null && GRAY_ICON != null) return;

		try {
			InputStream is = getClass().getResourceAsStream("/assets/power_up/shield.png");
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
		snake.addHeldPowerUp(this);
		deactivate();
	}

	public void activate(Snake snake) {
		snake.activateShield(duration);
		this.used = true;
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
			g.setColor(Color.CYAN);
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
