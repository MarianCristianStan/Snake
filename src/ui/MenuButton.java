package ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import gameStates.GameState;

public class MenuButton {
	
	private int xPos;
	private int yPos;
	private int rowIndex;
	private GameState state;
	private boolean mousePressed = false;
	private Rectangle bounds;
	private BufferedImage buttonImage;
	
	public MenuButton(int xPos, int yPos, int rowIndex , GameState state)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.rowIndex = 620*rowIndex;
		this.state = state;
		loadButton();
		initBounds();
	}

	private void initBounds() {
		
		//xPos, yPos, 300, 80 image draw 
		bounds = new Rectangle(xPos, yPos, 300, 80);
		
	}

	private void loadButton() {
	
		InputStream btnStream = getClass().getResourceAsStream("/assets/ui/menuBtn.png");
		try {
			BufferedImage  imgMenu = ImageIO.read(btnStream);
			buttonImage = imgMenu.getSubimage(430, rowIndex + 170, 1930, 560);
			
		}catch (IOException e)
		{
			e.printStackTrace();
		}finally
		{
			try {
				btnStream.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}

	}
	
	public void draw(Graphics graphics)
	{
		graphics.drawImage(buttonImage, xPos, yPos, 300, 80, null);
	}
	
	public void apllyGameState()
	{
		GameState.state = state;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}
	
	public void resetAction()
	{
		mousePressed = false;
	}
	
}
