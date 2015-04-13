import javax.swing.JFrame;


public class GameFrame extends JFrame
{
	private GameEngine engine;
	private GamePanel panel;
	private GameClient client;
	private World world;
	
	public GameFrame()
	{
		setResizable(false);
		world=new World("Default");
		world.load(true);
		
		panel=new GamePanel(this,new View(160,160,2));
		getContentPane().add(panel);
		pack();
		//setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel.requestFocus();
		
		client=new GameClient(this,world);
		client.start();
		engine=new GameEngine(this,panel,60,60);
		//engine.start();
	}
	
	public void startEngine()
	{
		engine.start();
	}
	
	public GamePanel getPanel()
	{
		return(panel);
	}
	
	public World getWorld()
	{
		return(world);
	}
	
	public PlayerEntity getPlayer()
	{
		if(client==null)
			return(null);
		return(client.getPlayer());
	}
}
