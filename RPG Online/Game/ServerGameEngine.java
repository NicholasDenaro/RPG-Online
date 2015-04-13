import java.io.IOException;


public class ServerGameEngine extends GameEngine
{
	//private GameFrame frame;
	//private GamePanel panel;
	private ServerEngine server;
	private boolean running;
	private int tps,fps;
	private int ticks, frames;
	private byte send;
	
	public ServerGameEngine(ServerEngine ser, int t, int f)
	{
		super(null,null,t,f);
		server=ser;
	}
	
	public void tick()
	{
		server.tick();
		if(send++>fps/5)
		{
			send=0;
			try
			{
				//System.out.println("sending zone messages");
				server.sendZoneMessages();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void draw()
	{
		//do nothing
	}
}
