package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.GamePanel;
import gameStates.GameState;

public class MouseInput implements MouseListener, MouseMotionListener{

	private GamePanel gamePanel;
	
	public MouseInput(GamePanel gamePanel)
	{
		this.gamePanel = gamePanel;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
	public void mousePressed(MouseEvent e) {
		
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
	public void mouseReleased(MouseEvent e) {

		switch (GameState.state)
		{
		case MENU:
			gamePanel.getGame().getMenu().mouseReleased(e);
			break;
		case PLAYING:
			
			break;
		default:
			break;
		
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
