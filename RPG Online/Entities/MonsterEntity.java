import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;


public class MonsterEntity extends LivingEntity implements BlockingEntity
{
	public static HashMap<String, MonsterEntity> monsterList=new HashMap<String, MonsterEntity>();
	
	protected String name;
	private boolean aggressive;
	//protected PlayerStatus stats;
	private MonsterSkillList skills;
	private int walk;
	protected int wait;
	private HashSet<Long> attackers;
	private int lastsecond;
	protected MonsterDrops drops;
	
	private boolean battleStance;
	private int battleStanceCount;
	
	public MonsterEntity(String n,boolean aggro, PlayerStatus s, MonsterSkillList skillist)
	{
		super(null,-1);
		name=n;
		sprite=new Sprite(name,16,24);
		aggressive=aggro;
		stats=s;
		skills=skillist;
		walk=0;
		if(ServerEngine.server!=null)
			wait=ServerEngine.server.getTickrate()*3;
		monsterList.put(n,this);
		
		if(ServerEngine.server!=null)
		{
			drops=ServerEngine.server.database.getMonsterDrops(n);
		}
	}
	
	public MonsterEntity(Zone z, long id, MonsterEntity other, short i, short a)
	{
		super(z,id);
		x=i*16;
		y=a*16;
		xtrue=i;
		ytrue=a;
		
		name=other.name;
		sprite=other.sprite;
		aggressive=other.aggressive;
		stats=other.stats.clone();
		skills=other.skills.clone();
		walk=0;
		wait=ServerEngine.server.getTickrate()*3;
		attackers=new HashSet<Long>();
		lastsecond=0;
		battleStance=false;
		battleStanceCount=0;
	}
	
	public void tick()
	{
		if(conditions.contains(PlayerEffect.paralysis))
			return;
			
		if(wait>0)
			wait--;
		else
		{
			if(targetId!=-1)
			{
				battleStance=true;
				LivingEntity living=zone.getLiving(targetId);
				if(living!=null)
				{
					if(distance(living)==1)
					{
						if(direction==direction(living))
						{
							if(skills.getSkill(0).canUse(stats))
							{
								MonsterSkill skill=skills.getSkill(0);
								if(living instanceof PlayerGhostEntity)
								{
									PlayerGhostEntity target=(PlayerGhostEntity)living;
									
									if(!living.isMoving()&&target.move==0)
									{
										skill.use(this,target);
										
										stats.currentAp-=skill.cost;
										
										/*int str=stats.getStat(PlayerStat.strength).value();
										int def=target.stats.getStat(PlayerStat.defense).value()+target.equipment.getDefense();
										
										int dmg=1+(int)(str*skill.getDamageMultiplier()*((100-def)/100.0));
										
										target.stats.increase(new PlayerStat(PlayerStat.health,-dmg));*/
										
										Message mes=new Message(Message.STAT);
										mes.addBoolean(true);
										mes.addByte(PlayerStat.health);
										mes.addInt(target.stats.getStat(PlayerStat.health).value());
										target.handler.addMessage(mes);
									}
									else
									{
										//System.out.println("moving");
									}
								}
							}
						}
						else//not facing the right direction
						{
							//System.out.println("imageIndex before: "+imageIndex);
							direction=direction(living);
							if(direction==NORTH)
								imageIndex=4;
							if(direction==SOUTH)
								imageIndex=0;
							if(direction==EAST)
								imageIndex=12;
							if(direction==WEST)
								imageIndex=8;
							//System.out.println("imageIndex after: "+imageIndex);
							Message mes=new Message(Message.MOVE);
							mes.addLong(uuid);
							mes.addBoolean(false);
							mes.addByte(imageIndex);
							ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
							//System.out.println("Changed direction: "+direction);
							
						}//direction
					}
					else if(distance(living)>1)
					{
						if(walk==0)
						{
							if(ytrue-living.ytrue>=1&&zone.canWalk(xtrue,ytrue-1))
							{
								walk=31;
								yspeed=-0.5;
								xspeed=0;
								imageIndex=4;
								ytrue--;
								direction=NORTH;
							}
							else if(living.ytrue-ytrue>=1&&zone.canWalk(xtrue,ytrue+1))
							{
								walk=31;
								yspeed=0.5;
								xspeed=0;
								imageIndex=0;
								ytrue++;
								direction=SOUTH;
							}
							else if(xtrue-living.xtrue>=1&&zone.canWalk(xtrue-1,ytrue))
							{
								walk=31;
								yspeed=0;
								xspeed=-0.5;
								imageIndex=8;
								xtrue--;
								direction=WEST;
							}
							else if(living.xtrue-xtrue>=1&&zone.canWalk(xtrue+1,ytrue))
							{
								walk=31;
								yspeed=0;
								xspeed=0.5;
								imageIndex=12;
								xtrue++;
								direction=EAST;
							}
						}
					}//distance
					else if(distance(living)==0)
					{
						if(walk==0)
						{
							if(zone.canWalk(xtrue,ytrue+1))
							{
								walk=31;
								yspeed=0.5;
								xspeed=0;
								imageIndex=0;
								ytrue++;
								direction=SOUTH;
							}
							else if(zone.canWalk(xtrue,ytrue-1))
							{
								walk=31;
								yspeed=-0.5;
								xspeed=0;
								imageIndex=4;
								ytrue--;
								direction=NORTH;
							}
							else if(zone.canWalk(xtrue-1,ytrue))
							{
								walk=31;
								yspeed=0;
								xspeed=-0.5;
								imageIndex=8;
								xtrue--;
								direction=WEST;
							}
							else if(zone.canWalk(xtrue+1,ytrue))
							{
								walk=31;
								yspeed=0;
								xspeed=0.5;
								imageIndex=12;
								xtrue++;
								direction=EAST;
							}
						}
					}
				}
				else
				{
					targetId=-1;
					battleStance=false;
					battleStanceCount=10*60;//in game minutes;
				}
			}
			else
			{
				wait=(int)((0.25+Math.random()*5)*ServerEngine.server.getTickrate());
				if(walk==0)
				{
					int direction=(int)(Math.random()*4);
					if(direction==0)
					{
						if(zone.canWalk(xtrue,ytrue-1)&&zone.getEntityAt(new Point(xtrue,ytrue-1),PlayerGhostEntity.class)==null)
						{
							walk=31;
							yspeed=-0.5;
							xspeed=0;
							imageIndex=4;
							ytrue--;
							direction=NORTH;
						}
						else
						{
							imageIndex=4;
							direction=NORTH;
							Message mes=new Message(Message.MOVE);
							mes.addLong(uuid);
							mes.addBoolean(false);
							mes.addByte(imageIndex);
							ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
						}
					}
					else if(direction==1)
					{
						if(zone.canWalk(xtrue,ytrue+1)&&zone.getEntityAt(new Point(xtrue,ytrue+1),PlayerGhostEntity.class)==null)
						{
							walk=31;
							yspeed=0.5;
							xspeed=0;
							imageIndex=0;
							ytrue++;
							direction=SOUTH;
						}
						else
						{
							imageIndex=0;
							direction=SOUTH;
							Message mes=new Message(Message.MOVE);
							mes.addLong(uuid);
							mes.addBoolean(false);
							mes.addByte(imageIndex);
							ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
						}
					}
					else if(direction==2)
					{
						if(zone.canWalk(xtrue-1,ytrue)&&zone.getEntityAt(new Point(xtrue-1,ytrue),PlayerGhostEntity.class)==null)
						{
						walk=31;
						yspeed=0;
						xspeed=-0.5;
						imageIndex=8;
						xtrue--;
						direction=WEST;
						}
						else
						{
							imageIndex=8;
							direction=WEST;
							Message mes=new Message(Message.MOVE);
							mes.addLong(uuid);
							mes.addBoolean(false);
							mes.addByte(imageIndex);
							ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
						}
					}
					else if(direction==3)
					{
						if(zone.canWalk(xtrue+1,ytrue)&&zone.getEntityAt(new Point(xtrue+1,ytrue),PlayerGhostEntity.class)==null)
						{
							walk=31;
							yspeed=0;
							xspeed=0.5;
							imageIndex=12;
							xtrue++;
							direction=EAST;
						}
						else
						{
							imageIndex=12;
							direction=EAST;
							Message mes=new Message(Message.MOVE);
							mes.addLong(uuid);
							mes.addBoolean(false);
							mes.addByte(imageIndex);
							ServerEngine.server.addZoneMessage(zone.getId(),mes,null);
						}
					}
				}//walk==0
			}//targetId!=-1
		}//wait==0
		
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
			battleStance=false;
			battleStanceCount=10*60;//in game minutes(1 hour in game)
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
	
	public void endTick()
	{
		if(ServerEngine.server.time.getSecond()!=lastsecond)
		{
			lastsecond=ServerEngine.server.time.getSecond();
			if(!conditions.contains(PlayerEffect.delay))
				skills.tick();
			MessageCluster cluster=conditions.checkEffectsServer();
			ServerEngine.server.addZoneMessageCluster(zone.getId(),cluster,null);
			if(battleStance)
			{
				if(stats.currentAp<stats.getStat(PlayerStat.ap).value())
				{
					if(!conditions.contains(PlayerEffect.stun))
						battleStanceCount++;
					if(battleStanceCount>=30)//30 in game seconds
					{
						battleStanceCount=0;
						stats.currentAp+=stats.getAPs(null);
					}
				}
			}
			else if(stats.currentAp>0)
			{
				battleStanceCount--;
				if(battleStanceCount<=0)
				{
					stats.currentAp=0;
				}
			}
		}
	}
	
	public void addAttacker(long uuid)
	{
		attackers.add(uuid);
		targetId=uuid;
		if(!battleStance)
		{
			stats.currentAp=skills.getSkill(0).cost;
		}
		battleStance=true;
	}
	
	public void draw(Graphics2D g)
	{
		//sprite.draw(g,x,y-12,imageIndex);
	}
	
	
	public static MonsterEntity slime=new MonsterEntity("Slime",true,new PlayerStatus(1,0,5,10,10,10,1,0,1,1,4,3,-25),new MonsterSkillList().addSkill(new MonsterSkill(MonsterSkill.pounce,1,0)));
}
