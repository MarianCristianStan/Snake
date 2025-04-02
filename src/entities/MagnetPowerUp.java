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

public class MagnetPowerUp extends PowerUp {

    private final long duration = 5000;
    private static BufferedImage ICON;
    private static BufferedImage GRAY_ICON;

    static {
	    try {
	        InputStream is = MagnetPowerUp.class.getResourceAsStream("/assets/power_up/magnet.png");
	        BufferedImage original = ImageIO.read(is);
	        ICON = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
	        ICON.getGraphics().drawImage(original.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, null);
	        GRAY_ICON = PlayingUtils.makeGrayscale(ICON);
	        is.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    public MagnetPowerUp(float x, float y) {
        super(x, y, 32);
        this.requiresManualActivation = true;
        icon = ICON;
    }

    @Override
    public void applyToSnake(Snake snake) {
    	PowerUpSounds.playCollect();
        snake.addHeldPowerUp(this);
        deactivate();
    }

    public void activate(Snake snake) {
    	PowerUpSounds.playActivate();
        snake.activateMagnet(duration);
        this.used = true;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }

    public static BufferedImage getIconStatic() {
        return ICON;
    }

    public static BufferedImage getGrayIconStatic() {
        return GRAY_ICON;
    }

    @Override
    public void render(Graphics g) {
        if (icon != null) {
            g.drawImage(icon, (int) x, (int) y, null);
        } else {
            g.setColor(Color.PINK);
            g.fillOval((int) x, (int) y, width, height);
        }
    }
}
