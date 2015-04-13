import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ServerConsole extends Thread
{
	private ServerEngine server;
	private boolean running;
	
	public ServerConsole(ServerEngine ser)
	{
		server=ser;
		running=false;
	}
	
	public void run()
	{
		running=true;
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		
		while(running)
		{
			String command="";
			try
			{
				command=in.readLine();
				String args[]=new String[command.length()-command.replaceAll(" ","").length()];
				if(command.contains(" "))
				{
					String arg=command.substring(command.indexOf(" ")+1);
					command=command.substring(0,command.indexOf(" "));
					int i=0;
					while(arg.length()!=0)
					{
						if(arg.contains(" "))
						{
							args[i]=arg.substring(0,arg.indexOf(" "));
							arg=arg.substring(arg.indexOf(" ")+1);
						}
						else
						{
							args[i]=arg;
							arg="";
						}
					}
				}
				if(command.equalsIgnoreCase("Exit"))
				{
					System.exit(0);
				}
				else if(command.equalsIgnoreCase("LogoutAll"))
				{
					server.database.logoutAll();
				}
				else if(command.equalsIgnoreCase("Delete"))
				{
					server.database.deleteUser(args[0]);
				}
				else
				{
					System.out.println("invalid command.");
				}
			}
			catch(Exception ex)
			{
				//running=false;
				//ex.printStackTrace();
				System.out.println("Error with command: "+command);
			}
		}
	}
}
