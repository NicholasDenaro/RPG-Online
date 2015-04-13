
public class NPCEntity extends LivingEntity implements ActionEntity, BlockingEntity
{
	protected String name;
	private byte walk;
	private short wait;
	private boolean moves;
	
	public NPCEntity(Zone z,long id, int i, int a, String n, boolean mv)
	{
		super(z,id);
		xtrue=(short)i;
		ytrue=(short)a;
		x=i*16;
		y=a*16;
		wait=0;
		walk=0;
		name=n;
		moves=mv;
	}
	
	public void tick()
	{
		super.tick();
		
		if(!moves)
			return;
		
		if(wait>0)
			wait--;
		else
		{
			wait=(short)((0.25+Math.random()*5)*ServerEngine.server.getTickrate());
			if(walk==0)
			{
				int direction=(int)(Math.random()*4);
				if(direction==0&&zone.canWalk(xtrue,ytrue-1))
				{
					walk=31;
					yspeed=-0.5;
					xspeed=0;
					imageIndex=4;
					ytrue--;
					direction=NORTH;
				}
				else if(direction==1&&zone.canWalk(xtrue,ytrue+1))
				{
					walk=31;
					yspeed=0.5;
					xspeed=0;
					imageIndex=0;
					ytrue++;
					direction=SOUTH;
				}
				else if(direction==2&&zone.canWalk(xtrue-1,ytrue))
				{
					walk=31;
					yspeed=0;
					xspeed=-0.5;
					imageIndex=8;
					xtrue--;
					direction=WEST;
				}
				else if(direction==3&&zone.canWalk(xtrue+1,ytrue))
				{
					walk=31;
					yspeed=0;
					xspeed=0.5;
					imageIndex=12;
					xtrue++;
					direction=EAST;
				}
			}//walk==0
		}
		
		if(walk==31)
		{
			byte sendxspeed=(byte)Math.round(xspeed*2);
			byte sendyspeed=(byte)Math.round(yspeed*2);
			if(xspeed!=0||yspeed!=0)
			{
				Message mes=new Message(Message.MOVE);
				mes.addLong(uuid);
				mes.addBoolean(true);
				mes.addByte(sendxspeed);
				mes.addByte(sendyspeed);
				ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
			}
		}
		
		if(walk==0)
		{
			xspeed=0;
			yspeed=0;
		}
		else
			walk--;
		
		x+=xspeed;
		y+=yspeed;
	}
	
	public void turnToFace(byte dir)
	{
		switch(dir)
		{
			case NORTH:
				direction=SOUTH;
				imageIndex=0;
			return;
			case SOUTH:
				direction=NORTH;
				imageIndex=4;
			return;
			case EAST:
				direction=WEST;
				imageIndex=8;
			return;
			case WEST:
				direction=EAST;
				imageIndex=12;
			return;
		}
	}

	@Override
	public byte getAction()
	{
		return(Action.TALK);
	}
	
}
