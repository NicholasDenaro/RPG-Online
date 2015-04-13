import java.util.HashMap;
import java.util.Iterator;


public class World
{
	private String name;
	private HashMap<Short,Zone> zones;
	//protected Zone currentZone;
	protected TileArray tileArray;
	
	public World(String n)
	{
		name=n;
		zones=new HashMap<Short,Zone>();
		
		tileArray=new TileArray("Tiles");
		
		//zones.put((short)0,new Zone(this,0,"Tutorial"));
		//currentZone=null;
	}
	
	public void addZone(Zone z)
	{
		zones.put(z.getId(),z);
	}
	
	public void load(boolean draw)
	{
		//zones.put((short)0,new Zone(this,0,"Tutorial",draw));
	}
	
	public void tick()
	{
		Iterator<Zone> it=zones.values().iterator();
		while(it.hasNext())
			it.next().tick();
	}
	
	public Zone getZone(int i)
	{
		return(zones.get((short)i));
	}
	
	public String getName()
	{
		return(name);
	}
}
