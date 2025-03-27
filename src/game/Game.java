package game;

import java.awt.Graphics;
import gameStates.GameState;
import gameStates.Menu;
import gameStates.Playing;

public class Game implements Runnable{
	
	// panel & frame
	private GameFrame gameFrame;
	private GamePanel gamePanel;
	
	//game loop
	private Thread gameThread;
	private final int FPS_SET = 120;
	private final int UPS_SET = 200;
	
	//gameState 
	private Playing playing;
	private Menu menu;
	
	
	public Game()
	{
		
		menu = new Menu(this);
		initClassesAndGame();
		gamePanel = new GamePanel(this);
		gameFrame = new GameFrame(gamePanel);
		gamePanel.requestFocus();
		startGameLoop();
	
	}
	
	// initClasses before the game if neccesary
	private void initClassesAndGame() {
		
	}


	private void startGameLoop()
	{
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	// update game using Game States
	public void update()
	{
		switch(GameState.state)
		{
		case MENU:
			menu.update();
			break;
		case PLAYING:
			
			break;
		case QUIT:
		default:
			System.exit(0);
			break;
		
		}
	
	}
	
	//render game using Game States
	public void render(Graphics graphics)
	{
		switch(GameState.state)
		{
		case MENU:
			menu.draw(graphics);
			break;
		case PLAYING:
		
			break;
		case QUIT:
		default:
			break;
		
		}
	}
	
	// control the game 
	@Override
	public void run() {
		
		double timePerFrame = 1000000000.0 / FPS_SET;
		double timePerUpdate = 1000000000.0 / UPS_SET;
		long previousTime = System.nanoTime(); 
		
		int frames = 0;
		int updates = 0;
		
		double deltaU = 0;
		double deltaF = 0;
		
		long lastCheck = System.currentTimeMillis();
		while(true)
		{
	
			long currentTime = System.nanoTime();
			
			deltaU += (currentTime - previousTime)/timePerUpdate;
			deltaF += (currentTime - previousTime)/timePerFrame;
			previousTime = currentTime;
			
			if(deltaU >=1)
			{	update();
				updates++;
				deltaU--;
			}
			
			if(deltaF >= 1)
			{
				gamePanel.repaint();
				frames++;
				deltaF--;
			}			
			if(System.currentTimeMillis() - lastCheck >= 1000)
			{
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: "+frames + " | UPS: " + updates);
				frames=0;
				updates = 0;
			}
		}
	}


	public Playing getPlaying() {
		return playing;
	}

	public Menu getMenu() {
		return menu;
	}
	

}
