
package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import gameStates.Playing;
import utils.SoundPlayer;

public class Snake extends Entity {

	private List<SnakeSegment> segments = new ArrayList<>();
	private List<Point> trail = new ArrayList<>();
	private BufferedImage headImage, bodyImage;
	private double angle = 0;
	private float baseSpeed = 0.75f;
	private final int SPACING = 24;
	private boolean isMoving = false;
	private int segmentsToAdd = 0;
	private int segmentAddCooldown = 0;
	private final int SEGMENT_ADD_RATE = 24;

	private int teleportCooldown = 0;
	private final int TELEPORT_COOLDOWN_FRAMES = 60;

	private boolean leftPressed = false;
	private boolean rightPressed = false;

	private String activePowerUpName = null;
	private long powerUpEndTime = 0;

	private float speedMultiplier = 1.0f;
	private long speedBoostEndTime = 0;
	private int growthMultiplier = 1;
	private long growthBoostEndTime = 0;
	private boolean magnetActive = false;
	private long magnetEndTime = 0;

	private boolean shieldActive = false;
	private long shieldEndTime = 0;
	private List<PowerUp> heldPowerUps = new ArrayList<>();

	private Clip eatingSound;
	private long lastShieldSoundTime = 0;
	private final long SHIELD_SOUND_COOLDOWN_MS = 1000; 


	public Snake(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAssets();

		for (int i = 0; i < 100; i++) {
			float tx = x - (float) (Math.cos(angle) * i * 1f);
			float ty = y - (float) (Math.sin(angle) * i * 1f);
			trail.add(new Point((int) tx, (int) ty));
		}

		segments.add(new SnakeSegment(x, y, angle, width, height));
		for (int i = 1; i < 5; i++) {
			int index = i * SPACING;
			if (index < trail.size()) {
				Point p = trail.get(index);
				segments.add(new SnakeSegment(p.x, p.y, angle, width, height));
			}
		}
	}

	private void loadAssets() {
		try {
			File soundFile = new File("src/assets/sounds/eating.wav");
			AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile);
			eatingSound = AudioSystem.getClip();
			eatingSound.open(stream);
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
		}

		try {
			InputStream headStream = getClass().getResourceAsStream("/assets/snake/snakeHead.png");
			InputStream bodyStream = getClass().getResourceAsStream("/assets/snake/snakeBody.png");
			headImage = ImageIO.read(headStream);
			bodyImage = ImageIO.read(bodyStream);
			headStream.close();
			bodyStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		if (!isMoving)
			return;

		if (teleportCooldown > 0)
			teleportCooldown--;

		if (leftPressed)
			angle -= Math.toRadians(1.5);
		if (rightPressed)
			angle += Math.toRadians(1.5);

		SnakeSegment head = segments.get(0);
		float speed = getCurrentSpeed();
		float newX = head.x + (float) (Math.cos(angle) * speed);
		float newY = head.y + (float) (Math.sin(angle) * speed);

		head.setPosition(newX, newY);
		head.angle = angle;

		this.x = newX;
		this.y = newY;
		updateHitbox();

		trail.add(0, new Point((int) newX, (int) newY));

		for (int i = 1; i < segments.size(); i++) {
			int index = i * SPACING;
			if (index < trail.size()) {
				Point p = trail.get(index);
				segments.get(i).setPosition(p.x, p.y);
			}
		}

		if (trail.size() > 1000)
			trail = trail.subList(0, 1000);

		if (segmentsToAdd > 0) {
			segmentAddCooldown++;
			if (segmentAddCooldown >= SEGMENT_ADD_RATE) {
				int segmentIndex = segments.size();
				int trailIndex = segmentIndex * SPACING;

				if (trailIndex < trail.size()) {
					Point p = trail.get(trailIndex);
					SnakeSegment newSeg = new SnakeSegment(p.x, p.y, angle, width, height);
					segments.add(newSeg);
					segmentsToAdd--;
					segmentAddCooldown = 0;
				}
			}
		}
		if (activePowerUpName != null && System.currentTimeMillis() > powerUpEndTime) {
			activePowerUpName = null;
		}
		if (growthMultiplier > 1 && System.currentTimeMillis() > growthBoostEndTime) {
			growthMultiplier = 1;
		}
		if (shieldActive && System.currentTimeMillis() > shieldEndTime) {
			shieldActive = false;
			heldPowerUps.removeIf(p -> p instanceof ShieldPowerUp && p.isUsed());
		}
		if (magnetActive && System.currentTimeMillis() > magnetEndTime) {
			magnetActive = false;
			heldPowerUps.removeIf(p -> p instanceof MagnetPowerUp && p.isUsed());
		}

		checkCollision();
	}

	public void render(Graphics2D g2d) {
		if (!isMoving)
			return;

		for (int i = segments.size() - 1; i >= 0; i--) {
			SnakeSegment seg = segments.get(i);
			drawSegmentAnimate(g2d, seg, i == 0);

			// for debug
			// seg.drawRotatedHitbox(g2d, i == 0);
		}
	}

	private void drawSegment(Graphics2D g2d, SnakeSegment seg, boolean isHead) {
		BufferedImage img = isHead ? headImage : bodyImage;
		AffineTransform old = g2d.getTransform();
		g2d.translate(seg.x + img.getWidth() / 2, seg.y + img.getHeight() / 2);
		g2d.rotate(seg.angle);
		g2d.translate(-img.getWidth() / 2, -img.getHeight() / 2);
		g2d.drawImage(img, 0, 0, null);
		g2d.setTransform(old);
	}

	private void drawSegmentAnimate(Graphics2D g2d, SnakeSegment seg, boolean isHead) {
		BufferedImage img = isHead ? headImage : bodyImage;
		AffineTransform old = g2d.getTransform();

		int index = segments.indexOf(seg);
		double time = System.currentTimeMillis() / 150.0;
		double offset = Math.sin(time + index * 0.5) * 2.5;
		double offsetX = Math.cos(seg.angle) * offset;
		double offsetY = Math.sin(seg.angle) * offset;

		int cx = (int) (seg.x + img.getWidth() / 2 + offsetX);
		int cy = (int) (seg.y + img.getHeight() / 2 + offsetY);

		if (isHead && hasShield()) {
			g2d.setColor(new Color(0f, 0.7f, 1f, 0.4f));
			int radius = img.getWidth() + 12;
			g2d.fillOval(cx - radius / 2, cy - radius / 2, radius, radius);
		}
		if (isHead && hasMagnet()) {
			/* g2d.setColor(new Color(1f, 1f, 0f, 0.5f)); */

			int rays = 10;
			int length = 14;
			int innerRadius = img.getWidth() / 2 + 2;

			double baseAngle = System.currentTimeMillis() / 1000.0;
			float dynamicAlpha = (float) (0.2 + 0.4 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0)));
			g2d.setColor(new Color(1f, 1f, 0f, dynamicAlpha));

			for (int i = 0; i < rays; i++) {
				double angle = baseAngle + i * (2 * Math.PI / rays);
				double cos = Math.cos(angle);
				double sin = Math.sin(angle);

				int x1 = (int) (cx + cos * innerRadius);
				int y1 = (int) (cy + sin * innerRadius);
				int x2 = (int) (cx + cos * (innerRadius + length));
				int y2 = (int) (cy + sin * (innerRadius + length));
				g2d.drawLine(x1, y1, x2, y2);
			}
		}

		g2d.translate(cx, cy);
		g2d.rotate(seg.angle);
		g2d.translate(-img.getWidth() / 2, -img.getHeight() / 2);
		g2d.drawImage(img, 0, 0, null);
		g2d.setTransform(old);
	}

	public void eat(int amount) {
		if (eatingSound != null) {
			eatingSound.setMicrosecondPosition(0);
			eatingSound.start();
		}
		int actualAmount = amount * growthMultiplier;
		segmentsToAdd += actualAmount;

	}

	public void checkCollision() {
		SnakeSegment head = segments.get(0);

		for (int i = 4; i < segments.size(); i++) {
			if (head.intersects(segments.get(i).getHitbox())) {
				if (!hasShield()) {
					isMoving = false;
					return;
				}else {
					long now = System.currentTimeMillis();
				    if (now - lastShieldSoundTime > SHIELD_SOUND_COOLDOWN_MS) {
				        SoundPlayer.playSound("/assets/sounds/shield_block.wav");
				        lastShieldSoundTime = now;
				    }
				}

			}
		}

		if (teleportCooldown > 0)
			return;
		Rectangle top = Playing.getTopBorder();
		Rectangle bottom = Playing.getBottomBorder();
		Rectangle left = Playing.getLeftBorder();
		Rectangle right = Playing.getRightBorder();

		boolean collided = false;

		if (head.intersects(left)) {
			if (Playing.getLeftBorderColor().equals(Color.RED) && !hasShield()) {
				collided = true;

			} else {
				if(hasShield() && Playing.getLeftBorderColor().equals(Color.RED))
				{
					SoundPlayer.playSound("/assets/sounds/shield_block.wav");
				}
				this.x = right.x - width - 1;
				this.x += Math.cos(angle) * 4;
				this.y += Math.sin(angle) * 4;
				head.setPosition(this.x, this.y);
				teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
				updateHitbox();
				return;
			}
		}
		if (head.intersects(right)) {
			if (Playing.getRightBorderColor().equals(Color.RED) && !hasShield()) {
				collided = true;

			} else {
				if(hasShield() && Playing.getLeftBorderColor().equals(Color.RED))
				{
					SoundPlayer.playSound("/assets/sounds/shield_block.wav");
				}
				this.x = left.x + 1;
				this.x += Math.cos(angle) * 4;
				this.y += Math.sin(angle) * 4;
				head.setPosition(this.x, this.y);
				teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
				updateHitbox();
				return;
			}
		}
		if (head.intersects(top)) {
			if (Playing.getTopBorderColor().equals(Color.RED) && !hasShield()) {

				collided = true;

			} else {
				if(hasShield() && Playing.getLeftBorderColor().equals(Color.RED))
				{
					SoundPlayer.playSound("/assets/sounds/shield_block.wav");
				}
				this.y = bottom.y - height - 1;
				this.x += Math.cos(angle) * 4;
				this.y += Math.sin(angle) * 4;
				head.setPosition(this.x, this.y);
				teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
				updateHitbox();
				return;
			}
		}
		if (head.intersects(bottom)) {
			if (Playing.getBottomBorderColor().equals(Color.RED) && !hasShield()) {
				collided = true;

			} else {
				if(hasShield() && Playing.getLeftBorderColor().equals(Color.RED))
				{
					SoundPlayer.playSound("/assets/sounds/shield_block.wav");
				}
				this.y = top.y + 1;
				this.x += Math.cos(angle) * 4;
				this.y += Math.sin(angle) * 4;
				head.setPosition(this.x, this.y);
				teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
				updateHitbox();
				return;
			}
		}

		if (collided) {
			isMoving = false;
		}
	}

	public void activateSpeedBoost(float multiplier, long durationMs) {
		this.speedMultiplier = multiplier;
		this.speedBoostEndTime = System.currentTimeMillis() + durationMs;
	}

	public void activateGrowthBoost(int multiplier, long duration) {
		this.growthMultiplier = multiplier;
		this.growthBoostEndTime = System.currentTimeMillis() + duration;
	}

	public void activatePowerUp(String name, long durationMillis) {
		activePowerUpName = name;
		powerUpEndTime = System.currentTimeMillis() + durationMillis;
	}

	public String getActivePowerUpName() {
		return activePowerUpName;
	}

	public long getPowerUpRemainingMillis() {
		return Math.max(0, powerUpEndTime - System.currentTimeMillis());
	}

	private float getCurrentSpeed() {
		if (System.currentTimeMillis() > speedBoostEndTime) {
			speedMultiplier = 1.0f;
		}
		return baseSpeed * speedMultiplier;
	}

	public int getGrowthMultiplier() {
		if (System.currentTimeMillis() > growthBoostEndTime) {
			growthMultiplier = 1;
		}
		return growthMultiplier;
	}

	public long getSpeedBoostEndTime() {
		return speedBoostEndTime;
	}

	public long getGrowthBoostEndTime() {
		return growthBoostEndTime;
	}

	public long getShieldEndTime() {
		return shieldEndTime;
	}

	public void addHeldPowerUp(PowerUp p) {
		heldPowerUps.add(p);
	}

	public List<PowerUp> getHeldPowerUps() {
		return heldPowerUps;

	}

	public void resetPowerUps() {
		heldPowerUps.clear();
		activePowerUpName = null;
		speedMultiplier = 1.0f;
		growthMultiplier = 1;
		shieldActive = false;
		magnetActive = false;

		speedBoostEndTime = 0;
		growthBoostEndTime = 0;
		shieldEndTime = 0;
		magnetEndTime = 0;
	}

	public void activateMagnet(long duration) {
		magnetActive = true;
		magnetEndTime = System.currentTimeMillis() + duration;
	}

	public boolean isMagnetActive() {
		return magnetActive && System.currentTimeMillis() < magnetEndTime;
	}

	public long getMagnetEndTime() {
		return magnetEndTime;
	}

	public void activateShield(long duration) {
		shieldActive = true;
		shieldEndTime = System.currentTimeMillis() + duration;
	}

	public boolean hasShield() {
		return shieldActive;
	}

	public boolean hasMagnet() {
		return magnetActive;
	}

	public List<SnakeSegment> getSegments() {
		return segments;
	}

	public void setLeftPressed(boolean value) {
		leftPressed = value;
	}

	public void setRightPressed(boolean value) {
		rightPressed = value;
	}

	public void setMoving(boolean move) {
		isMoving = move;
	}

	public boolean isMoving() {
		return isMoving;
	}

	@Override
	public void updateHitbox() {
		hitbox.x = (int) x;
		hitbox.y = (int) y;
	}
}
