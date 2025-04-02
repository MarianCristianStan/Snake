package gameStates;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.io.IOException;

public class Menu extends State implements StateMethods {

    private JButton playButton;
    private JButton exitButton;
    private JButton[] menuButtons;
    private JButton bestScoresButton;
    private JButton instructionsButton;
    private int selectedIndex = 0;
    private boolean buttonsAdded = false;

    private Image menuBackground;
    private Image btnBackground;
    private Image keyboardImage; 
    private Image controllerImage; 
    private String inputType; 

    public Menu(Game game) {
        super(game);
        loadMenuBackground();
        loadKeyboardImage(); 
        loadControllerImage(); 
        inputType = "KEYBOARD"; 
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

    private void loadKeyboardImage() {
        try {
            InputStream keyboardStream = getClass().getResourceAsStream("/assets/ui/keyboard.png");
            if (keyboardStream != null) {
                keyboardImage = ImageIO.read(keyboardStream);
                keyboardStream.close();
            } else {
                System.err.println("Keyboard image not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadControllerImage() {
        try {
            InputStream controllerStream = getClass().getResourceAsStream("/assets/ui/joystick.png");
            if (controllerStream != null) {
                controllerImage = ImageIO.read(controllerStream);
                controllerStream.close();
            } else {
                System.err.println("Controller image not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        int centerX = GamePanel.getScreenWidth() / 2 - 500;
        int centerY = GamePanel.getScreenHeight() / 2 - 100;

        playButton = createModernButton("Play", centerX, centerY - 40);
        instructionsButton = createModernButton("Instructions", centerX, centerY + 30);
        bestScoresButton = createModernButton("Best Scores", centerX, centerY + 100);
        exitButton = createModernButton("Exit", centerX, centerY + 170);

        menuButtons = new JButton[]{playButton, instructionsButton, bestScoresButton, exitButton};

        playButton.addActionListener(e -> {
            GameState.state = GameState.PLAYING;
            game.getPlaying().restartGame();
            removeButtons();
            game.getGamePanel().requestFocusInWindow();
        });

        instructionsButton.addActionListener(e -> {
            GameState.state = GameState.INSTRUCTIONS;
            removeButtons();
            game.getGamePanel().requestFocusInWindow();
        });

        bestScoresButton.addActionListener(e -> {
            GameState.state = GameState.BESTSCORES;
            removeButtons();
            game.getGamePanel().requestFocusInWindow();
        });

        exitButton.addActionListener(e -> System.exit(0));

        game.getGamePanel().add(playButton);
        game.getGamePanel().add(instructionsButton);
        game.getGamePanel().add(bestScoresButton);
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
        game.getGamePanel().remove(instructionsButton);
        game.getGamePanel().remove(bestScoresButton);
        game.getGamePanel().remove(exitButton);
        buttonsAdded = false;
        game.getGamePanel().repaint();
    }

    @Override
    public void update() {
        // Update logic if needed
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

        int padding = 10; 
        int x = GamePanel.getScreenWidth() - 350 - padding;
        int y = padding;
        int width = 350; 
        int height = 120; 

        graphics.setColor(new Color(0, 0, 0, 100)); 
        graphics.fillRoundRect(x + 5, y + 5, width, height, 20, 20); 

        Graphics2D g2d = (Graphics2D) graphics;
        GradientPaint gradient = new GradientPaint(
                x, y, new Color(30, 144, 255), 
                x, y + height, new Color(0, 102, 204) 
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, width, height, 30, 30); 

        // Set text and image based on input type
        String inputText;
        if (inputType.equals("KEYBOARD")) {
            inputText = "You are currently using the KEYBOARD";
            if (keyboardImage != null) {
                graphics.drawImage(keyboardImage, x + 170, y + 60, 40, 40, null); 
            } else {
                System.err.println("Keyboard image is null!");
            }
        } else {
            inputText = "You are currently using the CONTROLLER";
            if (controllerImage != null) {
                graphics.drawImage(controllerImage, x + 170, y + 60, 40, 40, null); 
            } else {
                System.err.println("Controller image is null!");
            }
        }

        // Draw the input type text
        graphics.setColor(Color.WHITE); 
        graphics.setFont(new Font("Segoe UI", Font.BOLD, 16));
        graphics.drawString(inputText, x + 40, y + 30);
    }



    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        inputType = "KEYBOARD"; 

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
