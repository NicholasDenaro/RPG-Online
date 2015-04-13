import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class ServerEngine
{
	public static final int PORT=9400;
	public static ServerEngine server;
	private ServerListener listener;
	private ServerConsole console;
	private HashMap<Long,ServerClientHandler> clients;
	private HashMap<Short,ArrayList<Long>> clientsByZone;
	protected ServerDatabase database;
	private World world;
	private ServerGameEngine engine;
	protected GameTime time;

	
	//private boolean cansend, canwrite;
	
	public ServerEngine()
	{
		server=this;
		
		String user="",pass="";
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		try
		{
			System.out.print("user: ");
			user=in.readLine();
			System.out.print("pass: ");
			pass=in.readLine();
		}catch(Exception ex){};
		database=new ServerDatabase(this,"jdbc:mysql://localhost:3306/RPGOnlineDB",user,pass);
		listener=new ServerListener(this);
		clients=new HashMap<Long,ServerClientHandler>();
		clientsByZone=new HashMap<Short,ArrayList<Long>>();
		world=new World("Default");
		world.load(false);
		engine=new ServerGameEngine(this,60,60);
		loadZones();
		time=database.loadTime();
		
		//cansend=true;
		//canwrite=true;
		
		listener.start();
		engine.start();
		
		console=new ServerConsole(this);
		console.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
	        	shutdown();
			}
		}, "Shutdown-thread"));
	}
	
	public void addClient(ServerClientHandler handler, long uuid)
	{
		clients.put(uuid,handler);
	}
	
	public void removeClient(ServerClientHandler handler)
	{
		clients.remove(handler.getUUID());
		clientsByZone.get(handler.getZone()).remove(handler.getUUID());
	}
	
	/*public long newUUID()
	{
		long uuid=UUID.randomUUID().getMostSignificantBits();
		while(uuid==-1||clients.containsKey(uuid))
		{
			uuid=UUID.randomUUID().getMostSignificantBits();
		}
		return(uuid);
	}*/
	
	public void tick()
	{
		world.tick();
		time.tick();
	}
	
	public void setClientZone(short zone, long uuid)
	{
		if(!clientsByZone.containsKey(zone))
			clientsByZone.put(zone,new ArrayList<Long>());
		clientsByZone.get(zone).add(uuid);
	}
	
	public void changeClientZone(short oldz, short newz, long uuid)
	{
		clientsByZone.get(oldz).remove(uuid);
		setClientZone(newz,uuid);
	}
	
	synchronized public void addZoneMessage(short zone, Message mes, ServerClientHandler sentfrom)
	{
		//while(!canwrite){};
		//cansend=false;
		//System.out.println("started addZoneMessage()");
		if(clientsByZone.get(zone)!=null)
		{
			Iterator<Long> itClients=clientsByZone.get(zone).iterator();
			while(itClients.hasNext())
			{
				ServerClientHandler handler=clients.get(itClients.next());
				if(handler!=sentfrom)
				{
					handler.addMessage(mes);
				}
			}
		}
		//System.out.println("finished addZoneMessage()");
		//cansend=true;
	}
	
	synchronized public void addZoneMessageCluster(short zone, MessageCluster cluster, ServerClientHandler sentfrom)
	{
		//while(!canwrite){};
		//cansend=false;
		//System.out.println("started addZoneMessage()");
		if(clientsByZone.get(zone)!=null)
		{
			Iterator<Long> itClients=clientsByZone.get(zone).iterator();
			while(itClients.hasNext())
			{
				ServerClientHandler handler=clients.get(itClients.next());
				if(handler!=sentfrom)
				{
					handler.addMessageCluster(cluster);
				}
			}
		}
		//System.out.println("finished addZoneMessage()");
		//cansend=true;
	}
	
	synchronized public void sendMessageAll(Message mes)
	{
		Iterator<ServerClientHandler> it=clients.values().iterator();
		
		while(it.hasNext())
		{
			ServerClientHandler handle=it.next();
			if(handle.getUUID()!=-1)
			{
				handle.addMessage(mes);
				handle.flush();
			}
		}
	}
	
	public MessageCluster getZoneSnap(short zone)
	{
		MessageCluster cluster=new MessageCluster();
		Message zmes=new Message(Message.ZONE);
		zmes.addShort(zone);
		cluster.addMessage(zmes);
		if(clientsByZone.get(zone)!=null)
		{
			Iterator<Long> itClients=clientsByZone.get(zone).iterator();
			while(itClients.hasNext())
			{
				ServerClientHandler handler=clients.get(itClients.next());
				Message mes=new Message(Message.SPAWN);
				mes.addBoolean(true);
				mes.addLong(handler.getUUID());
				mes.addLong(handler.getPlayerUUID());
				mes.addShort(handler.getPlayerX());
				mes.addShort(handler.getPlayerY());
				cluster.addMessage(mes);
			}
		}
		ArrayList<Entity> monsters=(ArrayList<Entity>)world.getZone(zone).getEntities(MonsterEntity.class).clone();
		for(int i=0;i<monsters.size();i++)
		{
			MonsterEntity monster=(MonsterEntity)monsters.get(i);
			Message mes=new Message(Message.SPAWN);
			mes.addBoolean(false);
			mes.addLong(monster.uuid);
			mes.addShort(monster.xtrue);
			mes.addShort(monster.ytrue);
			mes.addString(monster.name);
			cluster.addMessage(mes);
			
			mes=new Message(Message.STAT);
			mes.addBoolean(false);
			mes.addLong(monster.uuid);
			mes.addByte(PlayerStat.health);
			mes.addInt(monster.stats.getStat(PlayerStat.health).value());
			cluster.addMessage(mes);
		}
		
		ArrayList<Entity> npcs=(ArrayList<Entity>)world.getZone(zone).getEntities(NPCEntity.class).clone();
		for(int i=0;i<npcs.size();i++)
		{
			NPCEntity npc=(NPCEntity)npcs.get(i);
			Message mes=new Message(Message.SPAWN);
			mes.addBoolean(false);
			mes.addLong(npc.uuid);
			mes.addShort(npc.xtrue);
			mes.addShort(npc.ytrue);
			mes.addString("NPC");
			mes.addString(npc.name);
			cluster.addMessage(mes);
		}
		
		return(cluster);
	}
	
	synchronized public void sendZoneMessages()
	{
		//while(!cansend){}
		//canwrite=false;
		Iterator<ArrayList<Long>> itlist=clientsByZone.values().iterator();
		
		while(itlist.hasNext())
		{
			Iterator<Long> itlong=itlist.next().iterator();
			while(itlong.hasNext())
			{
				ServerClientHandler handler=clients.get(itlong.next());
				handler.flush();
			}
		}
		//canwrite=true;
	}
	
	public void loadZones()
	{
		ResultSet results=database.getZones();
		
		try
		{
			while(results.next())
			{
				Zone z=new Zone(world,results.getShort(1),results.getString(2),false);
				world.addZone(z);
				if(z.getId()==1)
				{
					z.addEntity(new NPCEntity(z,z.getUUID(),5,3,"Instructor",false));
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Zone getZone(int z)
	{
		return(world.getZone(z));
	}
	
	public int getTickrate()
	{
		return(engine.getTickrate());
	}
	
	public void shutdown()
	{
		database.updateTime(time);
		Iterator<ServerClientHandler> it=clients.values().iterator();
		while(it.hasNext())
		{
			it.next().logout();
			System.out.println("hanging....");
		}
		System.out.println("The server has shut down.");
	}
	
	public static void main(String args[])
	{
		new ServerEngine();
	}
}
