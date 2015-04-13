import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerListener extends Thread
{
	private ServerEngine server;
	private ServerSocket socket;
	private boolean running;
	
	public ServerListener(ServerEngine ser)
	{
		server=ser;
		running=false;
		try
		{
			socket=new ServerSocket();
			//socket.setReuseAddress(true);
			socket.bind(new InetSocketAddress(socket.getInetAddress(),ServerEngine.PORT));
		}
		catch(Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public void run()
	{
		running=true;
		while(running)
		{
			try
			{
				Socket soc=socket.accept();
				ServerClientHandler handler=new ServerClientHandler(server,soc,-1);
				handler.start();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				running=false;
			}
		}
	}
}
