package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class PowerUp extends Entity {

	protected boolean active = true;
	protected boolean used = false;
	protected boolean requiresManualActivation = false;
	protected BufferedImage icon;
	 
	public PowerUp(float x, float y, int size) {
		super(x, y, size, size);
	}

	public boolean requiresManualActivation() {
		return requiresManualActivation;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isActive() {
		return active;
	}

	public void deactivate() {
		active = false;
	}
	public BufferedImage getIcon() {
	    return icon; 
	}


	public abstract void applyToSnake(entities.Snake snake);

	public abstract void render(Graphics g);
}
