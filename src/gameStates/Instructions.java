package gameStates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import game.Game;
import game.GamePanel;
import utils.SoundPlayer;

public class Instructions extends State implements StateMethods {

    private BufferedImage backgroundImage;

    public Instructions(Game game) {
        super(game);

        try {
            InputStream menuBgStream = getClass().getResourceAsStream("/assets/ui/menuBackground.jpg");
            backgroundImage = ImageIO.read(menuBgStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {}

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        int width = GamePanel.getScreenWidth();
        int height = GamePanel.getScreenHeight();
        int centerX = width / 2;

        g2d.drawImage(backgroundImage, 0, 0, width, height, null);

        // === Main Panel ===
        int panelWidth = 820;
        int panelHeight = 460;
        int panelX = centerX - panelWidth / 2;
        int panelY = height / 2 - panelHeight / 2;

        g2d.setColor(new Color(251, 238, 203));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 40, 40);
        g2d.setColor(new Color(120, 85, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 40, 40);

        // === Title ===
        String title = "🐍 Game Instructions";
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 34);
        g2d.setFont(titleFont);
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString(title, centerX - titleWidth / 2 + 2, panelY + 62);
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, centerX - titleWidth / 2, panelY + 60);

        // === Section Headers ===
        Font sectionFont = new Font("Segoe UI Emoji", Font.BOLD, 22);
        Font textFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);

        int leftColX = panelX + 40;
        int rightColX = panelX + panelWidth / 2 + 20;
        int yStart = panelY + 110;

        // === Controls Keyboard ===
        g2d.setFont(sectionFont);
        g2d.setColor(new Color(30, 30, 30));
        g2d.drawString("⌨ Controls (Keyboard) :", leftColX, yStart);

        g2d.setFont(textFont);
        int y = yStart + 30;
        g2d.drawString("⬅ A / D - Rotate the snake", leftColX + 24, y); y += 28;
        g2d.drawString("🛡 E - Activate Shield", leftColX + 24, y); y += 28;
        g2d.drawString("🧲 Q - Activate Magnet", leftColX + 24, y); y += 28;

        // === Controls Controller ===
        y = yStart;
        g2d.setFont(sectionFont);
        g2d.drawString("🎮 Controls (Controller) :", rightColX, y);

        g2d.setFont(textFont);
        y += 30;
        g2d.drawString("🎮 Left Stick - Rotate the snake", rightColX + 24, y); y += 28;
        g2d.drawString("🛡 [B] - Activate Shield", rightColX + 24, y); y += 28;
        g2d.drawString("🧲 [X] - Activate Magnet", rightColX + 24, y); y += 28;

        // === Tips ===
        g2d.setFont(sectionFont);
        y += 40;
        g2d.drawString("💡 Tips :", leftColX, y);

        g2d.setFont(textFont);
        y += 30;
        g2d.drawString("🟥 Avoid RED borders unless shield is active", leftColX + 24, y); y += 28;
        g2d.drawString("🍍 Pineapples give more growth", leftColX + 24, y); y += 28;
        g2d.drawString("🎯 Collect fruits to win!", leftColX + 24, y);


	     // === Switch Input === 
	     int ySwitch = y - 84; 
	     g2d.setFont(sectionFont);
	     g2d.drawString("🔁 Switch Input in Menu :", rightColX, ySwitch);
	
	     g2d.setFont(textFont);
	     ySwitch += 30;
	     g2d.drawString("⌨ Press H to switch to Controller", rightColX + 24, ySwitch); ySwitch += 28;
	     g2d.drawString("🎮 Press [B] to switch to Keyboard", rightColX + 24, ySwitch);


        // === ESC Footer ===
	     String footer = "🔙 Press ESC / [Y] to return";
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        g2d.setColor(new Color(120, 60, 0));
        int footerWidth = g2d.getFontMetrics().stringWidth(footer);
        g2d.drawString(footer, centerX - footerWidth / 2, panelY + panelHeight - 24);

        // === Enter Button ===
        String enterHint = "⚠ You can also press ENTER / [A] to activate all other buttons";
        Font enterHintFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        g2d.setFont(enterHintFont);
        g2d.setColor(new Color(120, 60, 0));
        g2d.drawString(enterHint, centerX - enterHint.length() * 3, panelY + panelHeight - 44);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    public void simulateKeyPress(char key) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(key);
        if (keyCode == KeyEvent.VK_UNDEFINED) return;

        KeyEvent fakeEvent = new KeyEvent(
            game.getGamePanel(),
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            keyCode,
            key
        );
        keyPressed(fakeEvent);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
           returnMenu();
        }
    }
    
    public void returnMenu() {
    	  SoundPlayer.playSound("/assets/sounds/menu_select.wav");
          GameState.state = GameState.MENU;
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
