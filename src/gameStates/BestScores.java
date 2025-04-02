package gameStates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO; // Adaugă acest import
import java.awt.image.BufferedImage; // Adaugă acest import

import game.Game;
import game.GamePanel;
import utils.ScoreManager;
import utils.SoundPlayer;

public class BestScores extends State implements StateMethods {

    private List<Integer> bestScores;
    private BufferedImage backgroundImage; 

    public BestScores(Game game) {
        super(game);
        bestScores = ScoreManager.loadScores();
        
       
        try {
            InputStream menuBgStream = getClass().getResourceAsStream("/assets/ui/menuBackground.jpg");
            backgroundImage = ImageIO.read(menuBgStream);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    @Override
    public void update() {
        bestScores = ScoreManager.loadScores();
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        int width = GamePanel.getScreenWidth();
        int height = GamePanel.getScreenHeight();
        int centerX = width / 2;

        // === Draw Background Image ===
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
        String title = "🏆 Best Scores";
        Font titleFont = new Font("Segoe UI Emoji", Font.BOLD, 34);
        g2d.setFont(titleFont);

        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        int titleX = centerX - titleWidth / 2;
        int titleY = panelY + 60;

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString(title, titleX + 2, titleY + 2); 
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, titleX, titleY);

        // === Scores Section ===
        Font scoreFont = new Font("Segoe UI Emoji", Font.PLAIN, 24);
        g2d.setFont(scoreFont);
        int textX = panelX + 280; 
        int startY = titleY + 60; 

        if (bestScores.isEmpty()) {
            g2d.setColor(Color.LIGHT_GRAY);
            String noScoresMessage = "No scores yet...";
            int noScoresWidth = g2d.getFontMetrics().stringWidth(noScoresMessage);
            g2d.drawString(noScoresMessage, centerX - noScoresWidth / 2, startY);
        } else {
            g2d.setColor(new Color(30, 30, 30));
            for (int i = 0; i < bestScores.size(); i++) {
                int score = bestScores.get(i);
                String rank;

                switch (i + 1) {
                    case 1:
                        rank = "1st. " + score + " 🏅"; 
                        break;
                    case 2:
                        rank = "2nd. " + score + "🎖️"; 
                        break;
                    case 3:
                        rank = "3rd. " + score + " 🎉"; 
                        break;
                    case 4:
                        rank = "4th. " + score;
                        break;
                    case 5:
                        rank = "5th. " + score; 
                        break;
                    default:
                        rank = (i + 1) + "th. " + score; 
                        break;
                }

                int rankY = startY + i * 40; 
                g2d.drawString(rank, textX, rankY);
            }
        }

        // === ESC Footer ===
        String footer = "⎋ Press ESC to return";
        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        g2d.setColor(new Color(120, 60, 0));
        int footerWidth = g2d.getFontMetrics().stringWidth(footer);
        int footerY = panelY + panelHeight - 24;
        g2d.drawString(footer, centerX - footerWidth / 2, footerY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            SoundPlayer.playSound("/assets/sounds/menu_select.wav");
            GameState.state = GameState.MENU;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}
}
