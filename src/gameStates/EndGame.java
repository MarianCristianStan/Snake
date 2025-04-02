package gameStates;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import game.Game;
import game.GamePanel;
import utils.ScoreManager;
import utils.SoundPlayer;

public class EndGame extends State implements StateMethods{
	private int fruitEaten;
	private boolean victory;
	private JButton backToMenuButton;
	private boolean buttonAdded = false;
	private boolean isSelected = false;


	
	public EndGame(Game game) {
		super(game);
		
	}

	@Override
	public void update() {
		
		
	}
	public void setFruitEaten(int fruitEaten) {
	    this.fruitEaten = fruitEaten;
	}

	public void setVictory(boolean victory) {
	    this.victory = victory;
	}


	@Override
	public void draw(Graphics graphics) {
	    if (!buttonAdded) {
	        isSelected = false; 
	        initBackToMenuButton();
	        buttonAdded = true;
	    }
	    if (victory) {
	        graphics.setColor(Color.YELLOW);
	        graphics.setFont(new Font("Ink free", Font.BOLD, 80));
	        FontMetrics metrics = graphics.getFontMetrics();
	        graphics.drawString("You Win!",
	            (GamePanel.getScreenWidth() - metrics.stringWidth("You Win!")) / 2,
	            (GamePanel.getScreenHeight() / 2) - 50);
	    } else {
	        graphics.setColor(Color.RED);
	        graphics.setFont(new Font("Ink free", Font.BOLD, 100));
	        FontMetrics metrics = graphics.getFontMetrics();
	        graphics.drawString("Game Over",
	            (GamePanel.getScreenWidth() - metrics.stringWidth("Game Over")) / 2,
	            (GamePanel.getScreenHeight() / 2) - 50);
	    }

	    graphics.setColor(Color.GREEN);
	    graphics.setFont(new Font("Ink free", Font.BOLD, 60));
	    FontMetrics metricsScore = graphics.getFontMetrics();
	    graphics.drawString("Your Score : " + fruitEaten,
	        (GamePanel.getScreenWidth() - metricsScore.stringWidth("Your Score : " + fruitEaten)) / 2,
	        graphics.getFont().getSize() + 100);

	    if (!buttonAdded) {
	        initBackToMenuButton();
	        buttonAdded = true;
	    }
	    updateButtonStyle();
	    
	}
	
	private void initBackToMenuButton() {
	    backToMenuButton = new JButton("Back to Menu");
	    backToMenuButton.setBounds(GamePanel.getScreenWidth() / 2 - 100, GamePanel.getScreenHeight() / 2 + 100, 200, 50);
	    backToMenuButton.setFont(new Font("Arial", Font.BOLD, 18));
	    backToMenuButton.setForeground(Color.WHITE);
	    backToMenuButton.setBackground(new Color(34, 139, 34));
	    backToMenuButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
	    backToMenuButton.setFocusPainted(false);
	    backToMenuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

	    backToMenuButton.addActionListener(e -> {
	        ScoreManager.saveScore(fruitEaten); 
	        GameState.state = GameState.MENU;
	        game.getPlaying().restartGame();
	        buttonAdded = false;
	        game.getGamePanel().remove(backToMenuButton);
	        game.getGamePanel().repaint();
	    });

	    game.getGamePanel().add(backToMenuButton);
	    backToMenuButton.setVisible(true);
	    game.getGamePanel().repaint();
	}


	private void updateButtonStyle() {
	    if (isSelected) {
	        backToMenuButton.setBackground(new Color(0, 102, 204)); 
	        backToMenuButton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3, true));
	    } else {
	        backToMenuButton.setBackground(new Color(34, 139, 34)); 
	        backToMenuButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
	    }
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
	    switch (e.getKeyCode()) {
	        case KeyEvent.VK_S -> {
	            if (!isSelected) {
	                isSelected = true;
	                updateButtonStyle();
	                SoundPlayer.playSound("/assets/sounds/menu_select.wav");
	            }
	        }
	        case KeyEvent.VK_ENTER -> {
	            if (isSelected) {
	                SoundPlayer.playSound("/assets/sounds/enter.wav");
	                backToMenuButton.doClick();
	            }
	        }
	    }
	}





	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
