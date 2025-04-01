package gameStates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import game.Game;
import game.GamePanel;
import utils.ScoreManager;
import utils.SoundPlayer;

public class BestScores extends State implements StateMethods {

    private List<Integer> bestScores;

    public BestScores(Game game) {
        super(game);
        bestScores = ScoreManager.loadScores(); 
    }
    
    @Override
    public void update() {
        bestScores = ScoreManager.loadScores(); 
    }

    @Override
    public void draw(Graphics g) {
       
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.getScreenWidth(), GamePanel.getScreenHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Best Scores", GamePanel.getScreenWidth() / 2 - 120, 100);

        if (bestScores.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 25));
            g.drawString("No scores yet...", GamePanel.getScreenWidth() / 2 - 100, 200);
        } else {
            g.setFont(new Font("Arial", Font.PLAIN, 30));
            for (int i = 0; i < bestScores.size(); i++) {
                g.drawString((i + 1) + ". " + bestScores.get(i),
                        GamePanel.getScreenWidth() / 2 - 50, 180 + i * 40);
            }
        }

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.GRAY);
        g.drawString("Press ESC to go back", 50, GamePanel.getScreenHeight() - 50);
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
