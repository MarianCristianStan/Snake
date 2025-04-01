package entities;

import utils.HitboxBody;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class SnakeSegment extends Entity {

    public double angle;
    private Path2D.Float rotatedHitbox;

    public SnakeSegment(float x, float y, double angle, int width, int height) {
        super(x, y, width, height);
        this.angle = angle;
        updateRotatedHitbox();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateHitbox();
    }

    @Override
    public void updateHitbox() {
        hitbox.x = (int) x;
        hitbox.y = (int) y;
        updateRotatedHitbox();
    }

    private void updateRotatedHitbox() {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        Path2D.Float rect = new Path2D.Float();
        rect.moveTo(-width / 2f, -height / 2f);
        rect.lineTo(width / 2f, -height / 2f);
        rect.lineTo(width / 2f, height / 2f);
        rect.lineTo(-width / 2f, height / 2f);
        rect.closePath();

        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(angle);
        rotatedHitbox = (Path2D.Float) rect.createTransformedShape(transform);
    }

    public boolean intersects(Rectangle rect) {
        return rotatedHitbox.intersects(rect);
    }

    public void drawRotatedHitbox(Graphics2D g2d, boolean isHead) {
        g2d.setColor(isHead ? Color.RED : Color.YELLOW);
        g2d.draw(rotatedHitbox);
    }
    
    public float getX() {
    	return x;
    }
    public float getY() {
    	return y;
    }
}
