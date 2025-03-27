package game;


import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public GameFrame(GamePanel gamePanel) {
		
		this.add(gamePanel);
		this.setTitle("Snake");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);

	}
}
