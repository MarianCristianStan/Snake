
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

public class Snake extends Entity {

	private List<SnakeSegment> segments = new ArrayList<>();
	private List<Point> trail = new ArrayList<>();
	private BufferedImage headImage, bodyImage;
	private double angle = 0;
	private final float SPEED = 0.75f;
	private final int SPACING = 24;
	private boolean isMoving = false;
	
	private int segmentsToAdd = 0;
	private int segmentAddCooldown = 0;
	private final int SEGMENT_ADD_RATE = 24;
	
	private int teleportCooldown = 0;
	private final int TELEPORT_COOLDOWN_FRAMES = 15;

	private boolean leftPressed = false;
	private boolean rightPressed = false;
	
	private Clip eatingSound;
	public Snake(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAssets();
		
		 for (int i = 0; i < 100; i++) {
		        float tx = x - (float)(Math.cos(angle) * i * 1f);
		        float ty = y - (float)(Math.sin(angle) * i * 1f);
		        trail.add(new Point((int)tx, (int)ty));
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
		float newX = head.x + (float) (Math.cos(angle) * SPEED);
		float newY = head.y + (float) (Math.sin(angle) * SPEED);

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
		checkCollision();
	}

	public void render(Graphics2D g2d) {
		if (!isMoving)
			return;

		for (int i = segments.size() - 1; i >= 0; i--) {
			SnakeSegment seg = segments.get(i);
			drawSegmentAnimate(g2d, seg, i == 0);

			// for debug
			/* seg.drawRotatedHitbox(g2d, i == 0); */
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

	    g2d.translate(seg.x + img.getWidth() / 2 + offsetX, seg.y + img.getHeight() / 2 + offsetY);
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
        segmentsToAdd += amount;
    }
    
    public void checkCollision() {
    	SnakeSegment head = segments.get(0);

    	for (int i = 4; i < segments.size(); i++) {
    		if (head.intersects(segments.get(i).getHitbox())) {
    			isMoving = false;
    			return;
    		}
    	}

    	if (teleportCooldown > 0) return;
    	Rectangle top = Playing.getTopBorder();
    	Rectangle bottom = Playing.getBottomBorder();
    	Rectangle left = Playing.getLeftBorder();
    	Rectangle right = Playing.getRightBorder();

    	boolean collided = false;

    	if (head.intersects(left)) {
    		if (Playing.getLeftBorderColor().equals(Color.RED)) {
    			collided = true;
    		} else {
    			this.x = right.x - width;
    			head.setPosition(this.x, this.y);
    			teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
    			updateHitbox();
    			return;
    		}
    	}
    	if (head.intersects(right)) {
    		if (Playing.getRightBorderColor().equals(Color.RED)) {
    			collided = true;
    		} else {
    			this.x = left.x + 1;
    			head.setPosition(this.x, this.y);
    			teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
    			updateHitbox();
    			return;
    		}
    	}
    	if (head.intersects(top)) {
    		if (Playing.getTopBorderColor().equals(Color.RED)) {
    			collided = true;
    		} else {
    			this.y = bottom.y - height;
    			head.setPosition(this.x, this.y);
    			teleportCooldown = TELEPORT_COOLDOWN_FRAMES;
    			updateHitbox();
    			return;
    		}
    	}
    	if (head.intersects(bottom)) {
    		if (Playing.getBottomBorderColor().equals(Color.RED)) {
    			collided = true;
    		} else {
    			this.y = top.y + 1;
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
