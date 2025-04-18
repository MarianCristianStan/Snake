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
			gamePanel.getGame().getMenu().keyPressed(e);
			break;
		case PLAYING:
			gamePanel.getGame().getPlaying().keyPressed(e);
			break;
		case ENDGAME:
			gamePanel.getGame().getEndGame().keyPressed(e);
			break;
		 case BESTSCORES:
			 gamePanel.getGame().getBestScores().keyPressed(e);
			break;
		 case INSTRUCTIONS:
			 gamePanel.getGame().getInstructions().keyPressed(e);
			break;
		default:
			break;
		
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch (GameState.state)
		{
		case MENU:
			gamePanel.getGame().getMenu().keyReleased(e);
			break;
		case PLAYING:
			gamePanel.getGame().getPlaying().keyReleased(e);
			break;
		case ENDGAME:
			gamePanel.getGame().getEndGame().keyPressed(e);
			break;
		 case BESTSCORES:
			 gamePanel.getGame().getBestScores().keyPressed(e);
			break;
		 case INSTRUCTIONS:
			 gamePanel.getGame().getInstructions().keyPressed(e);
			break;
		default:
			break;
		
		}
		
	}

}
