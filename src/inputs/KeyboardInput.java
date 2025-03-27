package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import game.GamePanel;
import gameStates.GameState;

public class KeyboardInput implements KeyListener{

	private GamePanel gamePanel;
	
	public KeyboardInput(GamePanel gamePanel)
	{
		this.gamePanel = gamePanel;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (GameState.state)
		{
		case MENU:
		
			break;
		case PLAYING:
			
			break;
		default:
			break;
		
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
		
	}

}
