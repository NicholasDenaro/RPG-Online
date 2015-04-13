import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;


public abstract class Skill
{
	public static final int MAX_LEVEL=20;
	public static HashMap<Integer, Skill> list=new HashMap<Integer, Skill>();
	public static BufferedImage images=null;
	
	private int id;
	private String name;
	private double damageMultiplier;
	protected int cost;
	private int maxcooldown;
	protected int cooldown;
	private PlayerEffect effect;
	protected int level;
	
	protected String type;
	
	public Skill(int i, String n,String ty,int cos, double dmgMult, int coolsec, int coolmin, int coolhour, PlayerEffect ef)
	{
		id=i;
		name=n;
		damageMultiplier=dmgMult;
		cost=cos;
		maxcooldown=coolsec+60*(coolmin+(60*coolhour));
		effect=ef;
		level=0;
		list.put(id,this);
		type=ty;
	}
	
	public Skill(Skill other, int lvl, int cool)
	{
		id=other.id;
		name=other.name;
		damageMultiplier=other.damageMultiplier;
		cost=other.cost;
		maxcooldown=other.maxcooldown;
		cooldown=cool;
		effect=other.effect;
		level=lvl;
		type=other.type;
	}
	
	public boolean canUse(Equipment equips, PlayerStatus stats)
	{
		return(cooldown==0&&stats.currentAp>=cost);
	}
	
	public boolean correctEquip(Equipment equips)
	{
		return(true);
	}
	
	public void use()
	{
		cooldown=maxcooldown;
	}
	
	public static int calculateDamage(int str, int dex, int def, int agi, double dmgmult)
	{
		double mult=1;
		if(dex<agi)
			mult=1+(dex-agi)/10.0;//dampen hit
		else
			mult=1+Math.max(dex-agi,10)/20.0;//similar to like crit
		
		double rand=0.5+Math.random()/2;
		int dmg=(int)Math.max(0,((str*2)*dmgmult*((100-def)/100.0)*mult*rand));
		
		return(dmg);
	}
	
	public int use(LivingEntity user, LivingEntity target)
	{
		cooldown=maxcooldown;
		
		int dmg=0;
		
		//damage stuffs
		int str=user.stats.getStat(PlayerStat.strength).value();
		if(user instanceof PlayerGhostEntity)
			str+=((PlayerGhostEntity)user).equipment.getStrength();
		int dex=user.stats.getStat(PlayerStat.dexterity).value();
		if(user instanceof PlayerGhostEntity)
			str+=((PlayerGhostEntity)user).equipment.getDexterity();
		
		if(user instanceof PlayerGhostEntity&&((PlayerGhostEntity)user).equipment.getWeapon()!=null)
			dex*=(1+((PlayerGhostEntity)user).equipment.getWeapon().getAccuracy()/100.0);
		
		int def=target.stats.getStat(PlayerStat.defense).value()+(target instanceof PlayerGhostEntity?((PlayerGhostEntity)target).equipment.getDefense():0);
		int agi=target.stats.getStat(PlayerStat.agility).value()+(target instanceof PlayerGhostEntity?((PlayerGhostEntity)target).equipment.getAgility():0);

		dmg=calculateDamage(str,dex,def,agi,getDamageMultiplier());
		target.stats.increase(new PlayerStat(PlayerStat.health,-dmg));
		
		if(dmg>0)
		{
			if(effect!=null)
			{
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
	
	public int getMaxCooldown()
	{
		return(maxcooldown);
	}
	
	public double getDamageMultiplier()
	{
		return(damageMultiplier);
	}
	
	public PlayerEffect getEffect()
	{
		return(effect);
	}
	
	public String getName()
	{
		return(name);
	}
	
	public String toString()
	{
		return(id+" "+name+"");
	}
	
	public String getType()
	{
		return(type);
	}
	
	public static Image getImage(int i)
	{
		if(images==null)
		{
			try
			{
				images=ImageIO.read(Skill.class.getResourceAsStream("Skills.png"));
			}
			catch(Exception ex)
			{
				System.out.println("Could not load Skills.png");
			}
		}
		return(images.getSubimage(i%(images.getWidth()/32)*32,i/(images.getWidth()/32)*32,32,32));
	}
	
	abstract public Skill clone(int lvl, int cool);
	
	public static GeneralSkill stance=new GeneralSkill(0,"Battle Stance","Battle",0,0,0,0,0,null);
	public static UnarmedSkill punch=new UnarmedSkill(1,"Punch","Unarmed",2,1.0,30,0,0,null);
	public static UnarmedSkill uppercut=new UnarmedSkill(2,"Uppercut","Unarmed",4,1.5,0,5,0,new PlayerEffect(PlayerEffect.stun,1,0,2,0));
	public static SwordSkill slash=new SwordSkill(3,"Slash","Sword",3,1.1,0,1,0,null);
}
