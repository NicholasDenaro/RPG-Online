import java.util.HashMap;


public class MonsterSkill
{
	public static HashMap<Integer, MonsterSkill> list=new HashMap<Integer, MonsterSkill>();
	
	private int id;
	private String name;
	private double damageMultiplier;
	protected int cost;
	private int maxcooldown;
	private int cooldown;
	private PlayerEffect effect;
	private int level;
	
	public MonsterSkill(int i, String n, int cos, double dmgMult, int coolsec, int coolmin, int coolhour, PlayerEffect ef)
	{
		id=i;
		name=n;
		damageMultiplier=dmgMult;
		cost=cos;
		maxcooldown=coolsec+60*(coolmin+(60*coolhour));
		effect=ef;
		level=0;
		list.put(id,this);
	}
	
	public MonsterSkill(MonsterSkill other, int lvl, int cool)
	{
		id=other.id;
		name=other.name;
		damageMultiplier=other.damageMultiplier;
		cost=other.cost;
		maxcooldown=other.maxcooldown;
		cooldown=cool;
		effect=other.effect;
		level=lvl;
	}
	
	public boolean canUse(PlayerStatus stats)
	{
		return(cooldown==0&&stats.currentAp>=cost);
	}
	
	public void use()
	{
		cooldown=maxcooldown;
	}
	
	public int use(LivingEntity user, LivingEntity target)
	{
		cooldown=maxcooldown;
		
		int dmg=0;
		
		//damage stuffs
		int str=user.stats.getStat(PlayerStat.strength).value();
		int dex=user.stats.getStat(PlayerStat.dexterity).value();
		
		
		int def=target.stats.getStat(PlayerStat.defense).value()+(target instanceof PlayerGhostEntity?((PlayerGhostEntity)target).equipment.getDefense():0);
		int agi=target.stats.getStat(PlayerStat.agility).value()+(target instanceof PlayerGhostEntity?((PlayerGhostEntity)target).equipment.getAgility():0);
		
		/*double mult=1;
		if(dex<agi)
			mult=1+(dex-agi)/10.0;//dampen hit
		else
			mult=1+Math.max(dex-agi,10)/20.0;//similar to like crit
		
		double rand=0.5+Math.random()/2;
		
		dmg=(int)Math.max(0,((str*2)*getDamageMultiplier()*((100-def)/100.0)*mult*rand));*/
		dmg=Skill.calculateDamage(str,dex,def,agi,getDamageMultiplier());
		target.stats.increase(new PlayerStat(PlayerStat.health,-dmg));
		
		if(dmg>0)
		{
			if(effect!=null)
			{
				//double rand=Math.random();
				//if(rand<=effect.getChance(skill.getLevel()))
				{
					PlayerEffect peff=null;
					if(effect.getId()==PlayerEffect.stun)
						target.conditions.addEffect(peff=effect.copy());
					else if(effect.getId()==PlayerEffect.paralysis)
						target.conditions.addEffect(peff=effect.copy());
					else if(effect.getId()==PlayerEffect.delay)
						target.conditions.addEffect(peff=effect.copy());
					else if(effect.getId()==PlayerEffect.tumble)
					{
						target.stats.currentAp-=effect.value();
						if(target.stats.currentAp<0)
							target.stats.currentAp=0;
					}
					if(peff!=null)
					{
						Message effmes=new Message(Message.EFFECT);
						effmes.addLong(target.uuid);
						effmes.addBoolean(true);
						effmes.addByte(peff.getId());
						effmes.addInt(peff.value());
						effmes.addInt(peff.getSeconds());
						effmes.addInt(peff.getMinutes());
						effmes.addInt(peff.getHours());
						ServerEngine.server.addZoneMessage(target.zone.getId(),effmes,null);
					}
				}
			}
		}
		
		return(dmg);
	}
	
	public void tick()
	{
		if(cooldown>0)
			cooldown--;
	}
	
	public int getId()
	{
		return(id);
	}
	
	public int getLevel()
	{
		return(level);
	}
	
	public int getCooldown()
	{
		return(cooldown);
	}
	
	public double getDamageMultiplier()
	{
		return(damageMultiplier);
	}
	
	public String toString()
	{
		return(id+" "+name+"");
	}
	
	public static MonsterSkill pounce=new MonsterSkill(0,"Pounce",2,0.5,45,0,0,null);
}
