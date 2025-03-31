package gameStates;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import utils.SoundPlayer;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import game.Game;
import game.GamePanel;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

public class Menu extends State implements StateMethods {

    private JButton playButton;
    private JButton exitButton;
    private JButton[] menuButtons;
    private int selectedIndex = 0;
    private boolean buttonsAdded = false;

    private Image menuBackground;
    private Image btnBackground;

    public Menu(Game game) {
        super(game);
        loadMenuBackground();
    }

    private void loadMenuBackground() {
        try {
            InputStream menuBgStream = getClass().getResourceAsStream("/assets/ui/menuBackground.jpg");
            InputStream btnBgStream = getClass().getResourceAsStream("/assets/ui/paper1Background.png");
            if (menuBgStream != null) {
                menuBackground = ImageIO.read(menuBgStream);
                menuBgStream.close();
            }
            if (btnBgStream != null) {
                btnBackground = ImageIO.read(btnBgStream);
                btnBgStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        int centerX = GamePanel.getScreenWidth() / 2 - 500;
        int centerY = GamePanel.getScreenHeight() / 2 - 100;

        playButton = createModernButton("Play", centerX, centerY);
        exitButton = createModernButton("Exit", centerX, centerY + 70);

        menuButtons = new JButton[]{playButton, exitButton};

        playButton.addActionListener(e -> {
            GameState.state = GameState.PLAYING;
            game.getPlaying().restartGame();
            removeButtons();
            game.getGamePanel().requestFocusInWindow();
        });

        exitButton.addActionListener(e -> System.exit(0));

        game.getGamePanel().add(playButton);
        game.getGamePanel().add(exitButton);

        updateButtonSelection();

        buttonsAdded = true;
    }

    private JButton createModernButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 220, 55);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 144, 255));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 102, 204));
            }

            public void mouseExited(MouseEvent e) {
                // Only reset if it's not selected
                if (button != menuButtons[selectedIndex]) {
                    button.setBackground(new Color(30, 144, 255));
                }
            }
        });

        return button;
    }

    private void updateButtonSelection() {
        for (int i = 0; i < menuButtons.length; i++) {
            JButton btn = menuButtons[i];
            if (i == selectedIndex) {
                btn.setBackground(new Color(0, 102, 204));
                btn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3, true));
            } else {
                btn.setBackground(new Color(30, 144, 255));
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
            }
        }
    }

    private void removeButtons() {
        game.getGamePanel().remove(playButton);
        game.getGamePanel().remove(exitButton);
        buttonsAdded = false;
        game.getGamePanel().repaint();
    }

    @Override
    public void update() {
        // Not used for now
    }

    @Override
    public void draw(Graphics graphics) {
        if (menuBackground != null) {
            graphics.drawImage(menuBackground, 0, 0, GamePanel.getScreenWidth(), GamePanel.getScreenHeight(), null);
        }

        if (btnBackground != null) {
            graphics.drawImage(btnBackground,
                    GamePanel.getScreenWidth() / 2 - 700,
                    GamePanel.getScreenHeight() / 2 - 300,
                    600, 600, null);
        }

        if (!buttonsAdded) {
            initButtons();
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
    
        switch (e.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_W -> {
                selectedIndex = (selectedIndex - 1 + menuButtons.length) % menuButtons.length;
                SoundPlayer.playSound("/assets/sounds/menu_select.wav");

                updateButtonSelection();
            }
            case java.awt.event.KeyEvent.VK_S -> {
                selectedIndex = (selectedIndex + 1) % menuButtons.length;
                SoundPlayer.playSound("/assets/sounds/menu_select.wav");

                updateButtonSelection();
            }
            case java.awt.event.KeyEvent.VK_ENTER -> {
                menuButtons[selectedIndex].doClick();
                SoundPlayer.playSound("/assets/sounds/enter.wav");
            }
        }
    }

    @Override public void keyReleased(java.awt.event.KeyEvent e) {}
}
