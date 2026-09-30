package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import entities.Snake;
import entities.SnakeSegment;
import game.GamePanel;

public class MenuBackgroundDemo {

	private Snake snake;

	private Rectangle avoidArea;

	private int targetIndex = 0;

	private static final float MOVE_SPEED = 0.75f;
	private static final double TURN_SPEED = Math.toRadians(2.5);

	private static final int PADDING = 100;
	private static final int SAFE_MARGIN = 24;

	public MenuBackgroundDemo(Rectangle avoidArea) {
		this.avoidArea = avoidArea;

		resetSnake();
	}

	public void update() {
		if (avoidArea == null || snake == null) {
			return;
		}

		SnakeSegment head = snake.getSegments().get(0);

		float headX = head.getX();
		float headY = head.getY();

		float left = avoidArea.x - PADDING;
		float right = avoidArea.x + avoidArea.width + PADDING;
		float top = avoidArea.y - PADDING;
		float bottom = avoidArea.y + avoidArea.height + PADDING;

		float midX = avoidArea.x + avoidArea.width / 2f;
		float midY = avoidArea.y + avoidArea.height / 2f;

		float quarterX1 = left + (right - left) * 0.25f;
		float quarterX2 = left + (right - left) * 0.75f;

		float quarterY1 = top + (bottom - top) * 0.25f;
		float quarterY2 = top + (bottom - top) * 0.75f;

		float[][] points = { { quarterX1, top }, { midX, top }, { quarterX2, top },

				{ right, quarterY1 }, { right, midY }, { right, quarterY2 },

				{ quarterX2, bottom }, { midX, bottom }, { quarterX1, bottom },

				{ left, quarterY2 }, { left, midY }, { left, quarterY1 } };

		float targetX = points[targetIndex][0];
		float targetY = points[targetIndex][1];

		float dx = targetX - headX;
		float dy = targetY - headY;

		float distance = (float) Math.sqrt(dx * dx + dy * dy);

		if (distance < 18f) {
			targetIndex = (targetIndex + 1) % points.length;
			return;
		}

		double desiredAngle = Math.atan2(dy, dx);

		double currentAngle = snake.getAngle();

		double difference = normalizeAngle(desiredAngle - currentAngle);

		if (difference > TURN_SPEED) {
			currentAngle += TURN_SPEED;
		} else if (difference < -TURN_SPEED) {
			currentAngle -= TURN_SPEED;
		} else {
			currentAngle = desiredAngle;
		}

		currentAngle = normalizeAngle(currentAngle);

		float newX = headX + (float) Math.cos(currentAngle) * MOVE_SPEED;

		float newY = headY + (float) Math.sin(currentAngle) * MOVE_SPEED;

		Rectangle safeArea = new Rectangle(avoidArea.x - SAFE_MARGIN, avoidArea.y - SAFE_MARGIN,
				avoidArea.width + SAFE_MARGIN * 2, avoidArea.height + SAFE_MARGIN * 2);

		if (safeArea.contains(newX, newY)) {

			float distLeft = Math.abs(newX - safeArea.x);

			float distRight = Math.abs(newX - (safeArea.x + safeArea.width));

			float distTop = Math.abs(newY - safeArea.y);

			float distBottom = Math.abs(newY - (safeArea.y + safeArea.height));

			float minDistance = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

			if (minDistance == distTop) {
				currentAngle = 0;
			} else if (minDistance == distRight) {
				currentAngle = Math.PI / 2;
			} else if (minDistance == distBottom) {
				currentAngle = Math.PI;
			} else {
				currentAngle = -Math.PI / 2;
			}

			newX = headX + (float) Math.cos(currentAngle) * MOVE_SPEED;

			newY = headY + (float) Math.sin(currentAngle) * MOVE_SPEED;
		}

		snake.setAngle(currentAngle);
		snake.renderPositionUpdate(newX, newY);
	}

	public void draw(Graphics2D g2d) {
		int width = GamePanel.getScreenWidth();
		int height = GamePanel.getScreenHeight();

		g2d.setColor(new Color(10, 16, 22));
		g2d.fillRect(0, 0, width, height);

		g2d.setColor(new Color(80, 150, 85));

		g2d.setStroke(new BasicStroke(2));

		g2d.drawRoundRect(30, 70, width - 60, height - 140, 25, 25);

		if (snake != null) {
			snake.render(g2d);
		}
	}

	public void setAvoidArea(Rectangle avoidArea) {
		this.avoidArea = avoidArea;
	}

	private void resetSnake() {
		if (avoidArea == null) {
			return;
		}

		float startX = avoidArea.x + avoidArea.width / 2f;

		float startY = avoidArea.y - PADDING;

		snake = new Snake(startX, startY, 24, 24, 10);

		snake.setAngle(0);
		snake.setMoving(true);

	}

	private double normalizeAngle(double angle) {
		while (angle < -Math.PI) {
			angle += Math.PI * 2;
		}

		while (angle > Math.PI) {
			angle -= Math.PI * 2;
		}

		return angle;
	}
}