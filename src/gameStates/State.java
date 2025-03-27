package gameStates;

import java.awt.event.MouseEvent;

import game.Game;
import ui.MenuButton;

public class State {

	protected Game game;
	
	public State(Game game)
	{
		this.game = game;
	}
	
	public Game getGame()
	{
		return this.game;
	}
	
	public boolean IsIn(MouseEvent e, MenuButton btn)
	{
		return btn.getBounds().contains(e.getX(), e.getY());
	}
	
}
