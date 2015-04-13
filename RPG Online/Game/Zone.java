import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.swing.JFileChooser;


public class Zone
{
	private World world;
	private short id;
	private String name;
	private short width, height;
	
	private ArrayList<Entity> entities;
	private ArrayList<TickingEntity> tickers;
	private HashMap<Integer,BufferedImage> background;
	
	private HashMap<Integer,ArrayList<Entity>> depthList;
	
	private boolean[][] walkArea;
	
	private ArrayList<ZoneWarpPoint> warps;
	
	private ArrayList<Long> uuids;
	
	protected LightLayer lights;
	
	public Zone(World wo, int i,String n,boolean draw)
	{
		world=wo;
		id=(short)i;
		name=n;
		entities=new ArrayList<Entity>();
		tickers=new ArrayList<TickingEntity>();
		loadZone(draw);
		uuids=new ArrayList<Long>();
		if(background!=null)
		{
			lights=new LightLayer(width*16,height*16);
			depthList=new HashMap<Integer,ArrayList<Entity>>();
		}
	}
	
	public short getId()
	{
		return(id);
	}
	
	public short getWidth()
	{
		return(width);
	}
	
	public short getHeight()
	{
		return(height);
	}
	
	public void tick()
	{
		ArrayList<TickingEntity> copy=(ArrayList<TickingEntity>)tickers.clone();
		for(TickingEntity e:copy)
			e.beginTick();
		for(TickingEntity e:copy)
			e.tick();
		for(TickingEntity e:copy)
			e.endTick();
	}
	
	public long getUUID()
	{
		long uuid=-1;
		while(uuid==-1||uuids.contains(uuid))
			uuid=UUID.randomUUID().getMostSignificantBits();
		uuids.add(uuid);
		return(uuid);
	}
	
	public void removeUUID(long uuid)
	{
		uuids.remove(uuid);
	}
	
	public void addEntity(Entity e)
	{
		if(e instanceof TickingEntity)
			tickers.add((TickingEntity)e);
		else
			entities.add(e);
		
		if(ServerEngine.server==null)
		{
			if(!depthList.containsKey(e.depth))
				depthList.put(e.depth,new ArrayList<Entity>());
			depthList.get(e.depth).add(e);
		}
	}
	
	public void removeEntity(Entity e)
	{
		if(e instanceof TickingEntity)
			tickers.remove(e);
		else
			entities.remove(e);
		
		if(e instanceof LivingEntity)
			uuids.remove(((LivingEntity)e).uuid);
		
		if(ServerEngine.server==null)
		{
			depthList.get(e.depth).remove(e);
		}
	}
	
	public ArrayList<Entity> getEntities(Class c)
	{
		ArrayList<Entity> ents=new ArrayList<Entity>();
		ArrayList<Entity> copy=(ArrayList<Entity>)entities.clone();
		for(Entity e:copy)
		{
			if(c.isAssignableFrom(e.getClass()))
			{
				ents.add(e);
			}
		}
		
		ArrayList<TickingEntity> copy2=(ArrayList<TickingEntity>)tickers.clone();
		for(TickingEntity e:copy2)
		{
			if(c.isAssignableFrom(e.getClass()))
			{
				ents.add(e);
			}
		}
		return(ents);
	}
	
	public LivingEntity getLiving(long uuid)
	{
		LivingEntity ent=null;
		ArrayList<TickingEntity> copy2=(ArrayList<TickingEntity>)tickers.clone();
		for(TickingEntity e:copy2)
			if(e instanceof LivingEntity)
			{
				LivingEntity living=(LivingEntity)e;
				//System.out.println("living found: "+living.uuid+":"+uuid);
				if(living.uuid==uuid)
				{
					if(ent==null)
						ent=living;
					else
						System.out.println("DUPLICATE!!!");
				}
			}
		return(ent);
	}
	
	public ActionEntity getEntityAt(Point p)
	{
		ArrayList<Entity> ents=getEntities(ActionEntity.class);
		for(int i=0;i<ents.size();i++)
		{
			ActionEntity ent=(ActionEntity)ents.get(i);
			if(ent instanceof LivingEntity)
			{
				LivingEntity liv=(LivingEntity)ent;
				if(liv.xtrue==p.x&&liv.ytrue==p.y)
					return(ent);
			}
		}
		return(null);
	}
	
	public Entity getEntityAt(Point p, Class c)
	{
		ArrayList<Entity> ents=getEntities(c);
		for(int i=0;i<ents.size();i++)
		{
			Entity ent=ents.get(i);
			if(ent instanceof LivingEntity)
			{
				LivingEntity liv=(LivingEntity)ent;
				if(liv.xtrue==p.x&&liv.ytrue==p.y)
					return(ent);
			}
			else
			{
				if(ent.x/16==p.x&&ent.y/16==p.y)
					return(ent);
			}
		}
		return(null);
	}
	
	public boolean canWalk(int i, int a)
	{
		try
		{
			if(ServerEngine.server==null)
				return(walkArea[i][a]);
			else
			{
				return(walkArea[i][a]&&getEntityAt(new Point(i,a),BlockingEntity.class)==null);
			}
		}
		catch(Exception ex)
		{
			return(false);
		}
	}
	
	public ZoneWarpPoint isWarpPoint(int i, int a)
	{
		for(int j=0;j<warps.size();j++)
		{
			ZoneWarpPoint warp=warps.get(j);
			if(warp.x==i&&warp.y==a)
				return(warp);
		}
		
		return(null);
	}
	
	public void changeDepth(Entity e,int olddepth, int newdepth)
	{
		if(ServerEngine.server==null)
		{
			depthList.get(olddepth).remove(e);
			
			if(!depthList.containsKey(newdepth))
				depthList.put(newdepth,new ArrayList<Entity>());
			depthList.get(newdepth).add(e);
		}
	}
	
	public void draw(Graphics2D g)
	{
		
		SortedSet<Integer> keys=new TreeSet<Integer>(background.keySet());
		HashMap<Integer,ArrayList<Entity>> depthListCopy=new HashMap<Integer,ArrayList<Entity>>();
		Iterator<Integer> itcopy=depthList.keySet().iterator();
		while(itcopy.hasNext())
		{
			int key=itcopy.next();
			depthListCopy.put(key,new ArrayList<Entity>(depthList.get(key)));
		}
		keys.addAll(depthListCopy.keySet());
		Iterator<Integer> it=keys.iterator();
		while(it.hasNext())
		{
			int key=it.next();
			if(background.containsKey(key))
				g.drawImage(background.get(key),0,0,null);
			if(depthListCopy.containsKey(key))
			{
				Iterator<Entity> itEnt=depthListCopy.get(key).iterator();
				while(itEnt.hasNext())
				{
					itEnt.next().draw(g);
				}
			}
		}
		
		//lights
		GameTime time=GamePanel.panel.hud.time;
		/*float darkness=0.5f;
		float val=0;
		if(time.getHour()<5)
			val=darkness;
		else if(time.getHour()<6)
			val=darkness-time.getMinute()*darkness/60;
		
		if(time.getHour()==17)
			val=time.getMinute()*darkness/60;
		else if(time.getHour()>17)
			val=darkness;
		
		lights.setLighting(Color.black,val);*/
		lights.setLighting(time);
		
		//draw lights
		lights.draw(g);
	}
	
	public void loadZone(boolean draw)
	{
		try
		{
			BufferedInputStream fin=new BufferedInputStream(Zone.class.getResourceAsStream(world.getName()+"/"+name+".mp"));
			
			byte[] bytes=new byte[8];
			fin.read(bytes);
			ByteBuffer buffer=ByteBuffer.wrap(bytes);
			
			int w=buffer.getInt();
			int h=buffer.getInt();
			
			width=(short)w;
			height=(short)h;
			
			//move area
			bytes=new byte[width*height];
			fin.read(bytes);
			buffer=ByteBuffer.wrap(bytes);
			
			walkArea=new boolean[width][height];
			
			for(int a=0;a<height;a++)
			{
				for(int i=0;i<width;i++)
				{
					walkArea[i][a]=buffer.get()==0;
				}
			}
			
			//layers
			bytes=new byte[4];
			fin.read(bytes);
			buffer=ByteBuffer.wrap(bytes);
			int layers=buffer.getInt();
			
			HashMap<Integer,int[][]> layerlist=new HashMap<Integer,int[][]>();
			
			bytes=new byte[layers*(4+width*height*4)];
			fin.read(bytes);
			buffer=ByteBuffer.wrap(bytes);
			
			for(int l=0;l<layers;l++)
			{
				int layer=buffer.getInt();
				layerlist.put(layer,new int[width][height]);
				for(int a=0;a<height;a++)
				{
					for(int i=0;i<width;i++)
					{
						layerlist.get(layer)[i][a]=buffer.getInt()-1;
					}
				}
			}
			
			//draw
			if(draw)
			{
				background=new HashMap<Integer,BufferedImage>();
				
				SortedSet<Integer> keys=new TreeSet<Integer>(layerlist.keySet());
				Iterator<Integer> it=keys.iterator();
				while(it.hasNext())
				{
					BufferedImage bg=new BufferedImage(width*16,height*16,BufferedImage.TYPE_INT_ARGB);
					Graphics2D g=(Graphics2D)bg.getGraphics();
					int key=it.next();
					for(int a=0;a<height;a++)
					{
						for(int i=0;i<width;i++)
						{
							g.drawImage(TileArray.tileArray.getTile(layerlist.get(key)[i][a]),i*16,a*16,null);
						}
					}
					background.put(key,bg);
				}
			}
			
			//warps
			bytes=new byte[4];
			fin.read(bytes);
			buffer=ByteBuffer.wrap(bytes);
			int warpcount=buffer.getInt();
			
			bytes=new byte[warpcount*10];
			fin.read(bytes);
			buffer=ByteBuffer.wrap(bytes);
			
			warps=new ArrayList<ZoneWarpPoint>();
			
			for(int i=0;i<warpcount;i++)
			{
				warps.add(new ZoneWarpPoint(buffer.getShort(),buffer.getShort(),buffer.getShort(),buffer.getShort(),buffer.getShort()));
			}
			
			//commands
			if(ServerEngine.server!=null)
			{
				CustomInputStream in=new CustomInputStream(fin);
				in.read();
				try
				{
					byte c=in.readByte();
					for(int i=0;i<c;i++)
					{
						String comm=in.readString();
						parseCommand(comm);
					}
				}
				catch(Exception ex)
				{
					System.out.println("error on commands(or just not there...)");
					//ex.printStackTrace();
				}
			}
			fin.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void parseCommand(String command)
	{
		String comm=command.substring(0,command.indexOf(' '));
		String arg=command.substring(command.indexOf(' ')+1);
		ArrayList<String> ars=new ArrayList<String>();
		while(arg.length()>0)
		{
			if(arg.contains(" "))
			{
				ars.add(arg.substring(0,arg.indexOf(' ')));
				arg=arg.substring(arg.indexOf(' ')+1);
			}
			else
			{
				ars.add(arg);
				arg="";
			}
		}
		String args[]=new String[ars.size()];
		for(int i=0;i<ars.size();i++)
			args[i]=ars.get(i);
		if(comm.equalsIgnoreCase("Entity"))
		{
			if(args[0].equalsIgnoreCase("MonsterSpawner"))
			{
				short x=new Short(args[1]);
				short y=new Short(args[2]);
				String monster=args[3];
				System.out.println("monster: "+monster);
				int count=new Integer(args[4]);
				int ratemin=new Integer(args[5]);
				int ratehour=new Integer(args[6]);
				MonsterSpawner spawner=new MonsterSpawner(this,x,y,MonsterEntity.monsterList.get(monster),count,ratemin,ratehour);
				addEntity(spawner);
			}
		}
	}
	
	public String toString()
	{
		return(id+": "+name);
	}
}
