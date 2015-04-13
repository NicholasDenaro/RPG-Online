import java.util.ArrayList;


public class MonsterSpawner extends TickingEntity
{
	private short xtrue, ytrue;
	private MonsterEntity monster;
	private int maxcount;
	private int rateminute, ratehour;
	private int tick;
	private int lastmin;
	private ArrayList<MonsterEntity> monsters;
	
	public MonsterSpawner(Zone z, double i, double a, MonsterEntity monst, int mc, int rm, int rh)
	{
		super(z);
		x=i*16;
		y=a*16;
		xtrue=(short)i;
		ytrue=(short)a;
		
		monster=monst;
		
		maxcount=mc;
		
		rateminute=rm;
		ratehour=rh;
		tick=0;
		
		lastmin=0;
		
		monsters=new ArrayList<MonsterEntity>();
	}
	
	public void tick()
	{
		if(lastmin!=ServerEngine.server.time.getMinute())
		{
			tick++;
			lastmin=ServerEngine.server.time.getMinute();
			for(int i=monsters.size()-1;i>=0;i--)
			{
				if(monsters.get(i).stats.getStat(PlayerStat.health).value()<=0)
					monsters.remove(i);
			}
		}
		
		if(monsters.size()<maxcount)
		{
			if(tick>=ratehour*60+rateminute)
			{
				MonsterEntity monst=new MonsterEntity(zone,zone.getUUID(),monster,(short)xtrue,(short)ytrue);
				zone.addEntity(monst);
				tick=0;
				Message mes=new Message(Message.SPAWN);
				mes.addBoolean(false);
				mes.addLong(monst.uuid);
				mes.addShort(xtrue);
				mes.addShort(ytrue);
				mes.addString(monster.name);
				ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
				monsters.add(monst);
			}
		}
	}
}
