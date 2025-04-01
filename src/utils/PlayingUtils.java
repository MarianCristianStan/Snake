package utils;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.concurrent.ThreadLocalRandom;

public class PlayingUtils {
	
	  public static Point getValidRandomPosition(Rectangle top, Rectangle bottom, Rectangle left, Rectangle right, int objectSize, int padding) {
	        int minX = left.x + left.width + padding;
	        int maxX = right.x - objectSize - padding;

	        int minY = top.y + top.height + padding;
	        int maxY = bottom.y - objectSize - padding;

	        int x = ThreadLocalRandom.current().nextInt(minX, maxX);
	        int y = ThreadLocalRandom.current().nextInt(minY, maxY);

	        return new Point(x, y);
	    }
	  
	  public static BufferedImage makeGrayscale(BufferedImage original) {
		    BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2d = gray.createGraphics();
		    ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		    op.filter(original, gray);
		    g2d.dispose();
		    return gray;
		}

}
