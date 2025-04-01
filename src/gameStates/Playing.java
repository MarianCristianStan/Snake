package gameStates;

import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;

import entities.Fruit;
import entities.Snake;
import game.Game;
import game.GamePanel;

public class Playing extends State implements StateMethods {

    // Borders
    private static Rectangle topBorder;
    private static Color topBorderColor = Color.GREEN;
    private static Rectangle bottomBorder;
    private static Color bottomBorderColor = Color.GREEN;
    private static Rectangle leftBorder;
    private static Color leftBorderColor = Color.GREEN;
    private static Rectangle rightBorder;
    private static Color rightBorderColor = Color.GREEN;

    // Game logic
    private long startTime;
    private BufferedImage gameTitleImg;
    private int fruitEaten;

    // Entities
    private Snake player;
    private Fruit fruit;

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    public void initClasses() {
        player = new Snake(200, 200, 24, 24);
        fruit = new Fruit(300, 300, 24, 24);
        startGame();
    }

    public void startGame() {
        loadInterface();
        player.setMoving(true);
        fruit.setIsEated(false);
        startTime = System.currentTimeMillis();
    }

    public void checkFruit() {
        if (fruit.getHitbox().intersects(player.getHitbox())) {
            if (fruit.getFruitType().equals("Apple")) {
                fruitEaten++;
                player.eat(5);
            } else {
                fruitEaten += 2;
                player.eat(10);
            }
            fruit.setIsEated(true);
        }
    }

    public void loadInterface() {
        topBorder = new Rectangle(30, 84, GamePanel.getScreenWidth() - 60, 2);
        bottomBorder = new Rectangle(30, GamePanel.getScreenHeight() - 34, GamePanel.getScreenWidth() - 60, 2);
        leftBorder = new Rectangle(30, 84, 2, GamePanel.getScreenHeight() - 115);
        rightBorder = new Rectangle(GamePanel.getScreenWidth() - 30, 84, 2, GamePanel.getScreenHeight() - 115);

        InputStream gameTitleStream = getClass().getResourceAsStream("/assets/ui/snaketitle.jpg");
        try {
            gameTitleImg = ImageIO.read(gameTitleStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (gameTitleStream != null) gameTitleStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void borderChange() {
        if (System.currentTimeMillis() - startTime >= 5000) {
            int borderRandom = ThreadLocalRandom.current().nextInt(4);
            switch (borderRandom) {
                case 0 -> {
                    setTopBorderColor(Color.RED);
                    setBottomBorderColor(Color.GREEN);
                    setLeftBorderColor(Color.GREEN);
                    setRightBorderColor(Color.GREEN);
                }
                case 1 -> {
                    setTopBorderColor(Color.GREEN);
                    setBottomBorderColor(Color.RED);
                    setLeftBorderColor(Color.GREEN);
                    setRightBorderColor(Color.GREEN);
                }
                case 2 -> {
                    setTopBorderColor(Color.GREEN);
                    setBottomBorderColor(Color.GREEN);
                    setLeftBorderColor(Color.RED);
                    setRightBorderColor(Color.GREEN);
                }
                case 3 -> {
                    setTopBorderColor(Color.GREEN);
                    setBottomBorderColor(Color.GREEN);
                    setLeftBorderColor(Color.GREEN);
                    setRightBorderColor(Color.RED);
                }
            }
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void update() {
        checkFruit();
      
        player.update();
        fruit.update();
    }

    @Override
    public void draw(Graphics graphics) {
        player.render((Graphics2D) graphics);
        if (player.isMoving()) {
            fruit.render(graphics);
            drawBorders(graphics);
            graphics.drawImage(gameTitleImg, 0, 0, GamePanel.getScreenWidth(), 54, null);
            drawScore(graphics);
            if (fruitEaten >= 20) {
            	gameOver();
            }
        } else {
            gameOver();
        }
    }

    private void drawBorders(Graphics graphics) {
        graphics.setColor(topBorderColor);
        graphics.fillRect(topBorder.x, topBorder.y, topBorder.width, topBorder.height);
        graphics.setColor(bottomBorderColor);
        graphics.fillRect(bottomBorder.x, bottomBorder.y, bottomBorder.width, bottomBorder.height);
        graphics.setColor(leftBorderColor);
        graphics.fillRect(leftBorder.x, leftBorder.y, leftBorder.width, leftBorder.height);
        graphics.setColor(rightBorderColor);
        graphics.fillRect(rightBorder.x, rightBorder.y, rightBorder.width, rightBorder.height);
    }

    private void drawScore(Graphics graphics) {
        graphics.setColor(Color.GREEN);
        graphics.setFont(new Font("Ink free", Font.BOLD, 40));
        FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + fruitEaten,
                (GamePanel.getScreenWidth() - metrics.stringWidth("Score: " + fruitEaten)) - 100,
                graphics.getFont().getSize());
    }

    public void gameOver() {
        game.getEndGame().setFruitEaten(fruitEaten);
        game.getEndGame().setVictory(fruitEaten >= 20); 
        GameState.state = GameState.ENDGAME;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
     
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
       
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player.setLeftPressed(true);
            case KeyEvent.VK_D -> player.setRightPressed(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> player.setLeftPressed(false);
            case KeyEvent.VK_D -> player.setRightPressed(false);
        }
    }

    public void restartGame() {
        fruitEaten = 0;
        player = new Snake(200, 200, 24, 24);
        fruit = new Fruit(300, 300, 24, 24);
        loadInterface();
        player.setMoving(true);
        fruit.setIsEated(false);
        startTime = System.currentTimeMillis();
    }

    public Snake getPlayer() {
        return this.player;
    }

    public Fruit getFruit() {
        return this.fruit;
    }

    public static Rectangle getTopBorder() {
        return topBorder;
    }

    public static Rectangle getBottomBorder() {
        return bottomBorder;
    }

    public static Rectangle getLeftBorder() {
        return leftBorder;
    }

    public static Rectangle getRightBorder() {
        return rightBorder;
    }

    public static Color getTopBorderColor() {
        return topBorderColor;
    }

    public static void setTopBorderColor(Color color) {
        topBorderColor = color;
    }

    public static Color getBottomBorderColor() {
        return bottomBorderColor;
    }

    public static void setBottomBorderColor(Color color) {
        bottomBorderColor = color;
    }

    public static Color getLeftBorderColor() {
        return leftBorderColor;
    }

    public static void setLeftBorderColor(Color color) {
        leftBorderColor = color;
    }

    public static Color getRightBorderColor() {
        return rightBorderColor;
    }

    public static void setRightBorderColor(Color color) {
        rightBorderColor = color;
    }
}
