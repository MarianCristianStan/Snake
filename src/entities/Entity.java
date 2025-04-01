package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {

	protected float x;
	protected float y;

	protected int width, height;
	protected Rectangle hitbox;

	public Entity(float x, float y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		initHitbox();
	}

	// drawHitbox method only for debugging the hitbox
	protected void drawHitbox(Graphics graphics) {
		graphics.setColor(Color.GREEN);
		graphics.drawRect(hitbox.x, hitbox.y, width, height);

	}

	private void initHitbox() {
		hitbox = new Rectangle((int) x, (int) y, width, height);
	}

	public void updateHitbox() {
		hitbox.x = (int) x;
		hitbox.y = (int) y;

	}

	public Rectangle getHitbox() {
		return hitbox;
	}
}
