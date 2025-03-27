package gameStates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


import javax.imageio.ImageIO;

import game.Game;
import game.GamePanel;
import ui.MenuButton;

public class Menu extends State implements StateMethods {

	private MenuButton[] buttons = new MenuButton[2];
	private BufferedImage menuBackground;
	private BufferedImage btnBackground;
	
	
	public Menu(Game game) {
		super(game);
		loadMenuBackground();
		loadButtons();
	}

	private void loadButtons() {
		
		buttons[0] = new MenuButton(GamePanel.getScreenWidth() / 2 - 550, GamePanel.getScreenHeight() / 2 - 100, 0,
				GameState.PLAYING);
		buttons[1] = new MenuButton(GamePanel.getScreenWidth() / 2 - 550, GamePanel.getScreenHeight() / 2, 3,
				GameState.QUIT);

	}

	private void loadMenuBackground() {
		
		InputStream menuBackgroundStream = getClass().getResourceAsStream("/assets/ui/menuBackground.jpg");
		InputStream btnBackgroundStream = getClass().getResourceAsStream("/assets/ui/paper1Background.png");
		try {
			BufferedImage  imgMenuBackground = ImageIO.read(menuBackgroundStream);
			BufferedImage  imgBtnBackground = ImageIO.read(btnBackgroundStream);
		
			menuBackground = imgMenuBackground;
			btnBackground = imgBtnBackground;
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}finally
		{
			try {
				menuBackgroundStream.close();
				btnBackgroundStream.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void draw(Graphics graphics) {
		// TODO Auto-generated method stub
		graphics.drawImage(menuBackground, 0, 0, GamePanel.getScreenWidth(), GamePanel.getScreenHeight(), null);
		graphics.drawImage(btnBackground, GamePanel.getScreenWidth() / 2 - 700, GamePanel.getScreenHeight() / 2-300,600,600, null);
		buttons[0].draw(graphics);
		buttons[1].draw(graphics);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (MenuButton btn : buttons)
			if (IsIn(e, btn)) {
				btn.setMousePressed(true);
				break;
			}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		for (MenuButton btn : buttons)
			if (IsIn(e, btn)) {
				if (btn.isMousePressed())
					btn.apllyGameState();
				break;

			}
		resetButtons();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			GameState.state = GameState.PLAYING;

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	private void resetButtons() {
		for (MenuButton btn : buttons)
			btn.resetAction();
	}

}
