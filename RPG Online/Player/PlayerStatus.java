
public class PlayerStatus
{
	public static final int FOODMAX=(int)(60*60*24*1.5);//1.5 days to full hunger
	
	private PlayerStat level;
	private PlayerStat statpoints;
	private PlayerStat exp;
	private PlayerStat maxHealth;
	private PlayerStat health;
	private PlayerStat durability;
	private PlayerStat stamina;
	private PlayerStat hunger;
	private PlayerStat agility;
	private PlayerStat vitality;
	private PlayerStat dexterity;
	private PlayerStat strength;
	private PlayerStat defense;
	private PlayerStat ap;
	
	protected int currentAp;
	private int aps;
	
	public PlayerStatus()
	{
		this(1,0,0,50,50,30,1,0,1,1,1,1,0);
	}
	
	public PlayerStatus(int lvl, int points, int ex, int maxhp, int hp, int dur, int sta, int hun, int agi, int dex, int vit, int str, int def)
	{
		//this(lvl,ex,maxhp,dur,sta,hun,agi,dex,vit,str,def);
		level=new PlayerStat(PlayerStat.level,lvl);
		statpoints=new PlayerStat(PlayerStat.statpoints,points);
		exp=new PlayerStat(PlayerStat.exp,ex);
		maxHealth=new PlayerStat(PlayerStat.maxhealth,maxhp);
		health=new PlayerStat(PlayerStat.health,hp);
		durability=new PlayerStat(PlayerStat.durability,dur);
		stamina=new PlayerStat(PlayerStat.stamina,sta);
		hunger=new PlayerStat(PlayerStat.hunger,hun);
		agility=new PlayerStat(PlayerStat.agility,agi);
		vitality=new PlayerStat(PlayerStat.dexterity,dex);
		dexterity=new PlayerStat(PlayerStat.vitality,vit);
		strength=new PlayerStat(PlayerStat.strength,str);
		defense=new PlayerStat(PlayerStat.defense,def);
		
		ap=new PlayerStat(PlayerStat.ap,(int)(-0.00005*(sta-1)*(sta-1)*(sta-1) + 0.0083*(sta-1)*(sta-1) + 0.0792*(sta-1) + 10));
		//aps=(int)(0.000005*(agi-1)*(agi-1) - 0.0008*(agi-1)*(agi-1) + 0.0567*(agi-1) + 1);
		currentAp=0;
	}
	
	public PlayerStat getStat(byte st)
	{
		switch(st)
		{
			case PlayerStat.level:
				return(level);
			case PlayerStat.statpoints:
				return(statpoints);
			case PlayerStat.exp:
				return(exp);
			case PlayerStat.maxhealth:
				return(maxHealth);
			case PlayerStat.health:
				return(health);
			case PlayerStat.durability:
				return(durability);
			case PlayerStat.stamina:
				return(stamina);
			case PlayerStat.hunger:
				return(hunger);
			case PlayerStat.agility:
				return(agility);
			case PlayerStat.dexterity:
				return(dexterity);
			case PlayerStat.vitality:
				return(vitality);
			case PlayerStat.strength:
				return(strength);
			case PlayerStat.defense:
				return(defense);
			case PlayerStat.ap:
				return(ap);
		}
		return(null);
	}
	
	public void increase(PlayerStat stat)
	{
		switch(stat.getId())
		{
			case PlayerStat.level:
				level.add(stat.value());
				return;
			case PlayerStat.statpoints:
				statpoints.add(stat.value());
				return;
			case PlayerStat.exp:
				exp.add(stat.value());
				if(exp.value()>5+level.value()*10)
				{
					int leftover=exp.value()-(5+level.value()*10);
					level.add(1);
					int oldhp=maxHealth.value();
					maxHealth.set((int)(50+(25*Math.pow(1.1,level.value()-1))));
					System.out.println("max health: "+maxHealth.value());
					health.set(health.value()+(maxHealth.value()-oldhp));
					statpoints.add(2);
					exp.set(leftover);
				}
				return;
			case PlayerStat.maxhealth:
				maxHealth.add(stat.value());
				return;
			case PlayerStat.health:
				health.add(stat.value());
				if(health.value()>maxHealth.value())
					health.set(maxHealth.value());
				return;
			case PlayerStat.durability:
				durability.add(stat.value());
				return;
			case PlayerStat.stamina:
				stamina.add(stat.value());
				return;
			case PlayerStat.hunger:
				hunger.add(stat.value());
				if(hunger.value()<0)
					hunger.set(0);
				if(hunger.value()>FOODMAX)
					hunger.set(FOODMAX);
				return;
			case PlayerStat.agility:
				agility.add(stat.value());
				return;
			case PlayerStat.dexterity:
				dexterity.add(stat.value());
				return;
			case PlayerStat.vitality:
				vitality.add(stat.value());
				return;
			case PlayerStat.strength:
				strength.add(stat.value());
				return;
			case PlayerStat.defense:
				defense.add(stat.value());
				return;
			case PlayerStat.ap:
				ap.add(stat.value());
				return;
		}
	}
	
	public int getAPs(Equipment equips)
	{
		int agi=agility.value()+(equips!=null?equips.getAgility():0);
		aps=(int)(0.000005*(agi-1)*(agi-1) - 0.0008*(agi-1)*(agi-1) + 0.0567*(agi-1) + 1);
		return(aps);
	}
	
	public PlayerStatus clone()
	{
		return(new PlayerStatus(level.value(),statpoints.value(),exp.value(), maxHealth.value(), health.value(), durability.value(), stamina.value(), hunger.value(), agility.value(), dexterity.value(), vitality.value(), strength.value(), defense.value()));
	}
}
