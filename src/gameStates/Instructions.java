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
    public void update() {
        
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        int width = GamePanel.getScreenWidth();
        int height = GamePanel.getScreenHeight();
        int centerX = width / 2;

        g2d.drawImage(backgroundImage, 0, 0, width, height, null); 

        // === Main Panel ===
        int panelWidth = 660;
        int panelHeight = 440;
        int panelX = centerX - panelWidth / 2;
        int panelY = height / 2 - panelHeight / 2;

        g2d.setColor(new Color(251, 238, 203));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 40, 40);
        g2d.setColor(new Color(120, 85, 50));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 40, 40);

        // === Title with Drop Shadow ===
        String title = "🐍 Game Instructions";
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 34);
        g2d.setFont(titleFont);

        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth / 2;
        int titleY = panelY + 60;

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString(title, titleX + 2, titleY + 2);
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, titleX, titleY);

        // === Section Headers ===
        Font sectionFont = new Font("Segoe UI Emoji", Font.BOLD, 22);
        Font textFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);
        int textX = panelX + 50;
        int y = titleY + 40;

        // === Controls ===
        g2d.setFont(sectionFont);
        g2d.setColor(new Color(30, 30, 30));
        g2d.drawString("🎮 Controls:", textX, y);
        y += 32;

        g2d.setFont(textFont);
        g2d.drawString("⬅ A / D - Rotate the snake", textX + 24, y); y += 28;
        g2d.drawString("🛡 E - Activate Shield", textX + 24, y); y += 28;
        g2d.drawString("🧲 Q - Activate Magnet", textX + 24, y); y += 40;

        // === Tips ===
        g2d.setFont(sectionFont);
        g2d.drawString("💡 Tips:", textX, y); y += 32;

        g2d.setFont(textFont);
        g2d.drawString("🟥 Avoid RED borders unless shield is active", textX + 24, y); y += 28;
        g2d.drawString("🍍 Pineapples give more growth", textX + 24, y); y += 28;
        g2d.drawString("🎯 Collect fruits to win!", textX + 24, y); y += 28;

        // === ESC Footer ===
        String footer = "⎋ Press ESC to return";
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        g2d.setColor(new Color(120, 60, 0));
        int footerWidth = g2d.getFontMetrics().stringWidth(footer);
        int footerY = panelY + panelHeight - 24;
        g2d.drawString(footer, centerX - footerWidth / 2, footerY);
        
        // === Enter Button ===
        String enterHint = "⚠ You can also press ENTER to activate all other buttons";
        Font enterHintFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        g2d.setFont(enterHintFont);
        g2d.setColor(new Color(120, 60, 0));
        int enterHintWidth = g2d.getFontMetrics().stringWidth(footer);
        int enterHintOffsetX = 100; 
        g2d.drawString(enterHint, centerX - enterHintWidth / 2 - enterHintOffsetX, panelY + panelHeight - 40);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle mouse click events if needed
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Handle mouse press events if needed
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Handle mouse release events if needed
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Handle mouse move events if needed
    }

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
            SoundPlayer.playSound("/assets/sounds/menu_select.wav");
            GameState.state = GameState.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key release events if needed
    }
}
