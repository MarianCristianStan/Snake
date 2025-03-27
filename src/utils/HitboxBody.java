package utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class HitboxBody {

    private Rectangle hitboxBody;
    private Color color;

    public HitboxBody() {
        this(-50, -50, 1, 1, Color.MAGENTA);
    }

    public HitboxBody(int xBody, int yBody, int width, int height, Color color) {
        hitboxBody = new Rectangle(xBody, yBody, width, height);
        this.color = color;
    }

    public void updateHitboxBody(int xBody, int yBody) {
        hitboxBody.x = xBody;
        hitboxBody.y = yBody;
    }

    public void drawHitboxBody(Graphics g) {
        g.setColor(color);
        g.drawRect(hitboxBody.x, hitboxBody.y, hitboxBody.width, hitboxBody.height);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Rectangle getHitbox() {
        return hitboxBody;
    }
}
