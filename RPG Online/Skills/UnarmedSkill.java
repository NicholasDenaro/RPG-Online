
public class UnarmedSkill extends Skill
{

	
	public UnarmedSkill(int i,String n,String ty, int cos,double dmgMult,int coolsec,int coolmin,int coolhour,PlayerEffect ef)
	{
		super(i,n,ty,cos,dmgMult,coolsec,coolmin,coolhour,ef);
	}
	
	public UnarmedSkill(Skill s, int lvl, int cool)
	{
		super(s,lvl,cool);
	}

	public boolean canUse(Equipment equips, PlayerStatus stats)
	{
		return(cooldown==0&&stats.currentAp>=cost&&equips.getWeapon()==null);
	}
	
	@Override
	public boolean correctEquip(Equipment equips)
	{
		return(equips.getWeapon()==null);
	}
	
	public UnarmedSkill clone(int lvl, int cool)
	{
		return(new UnarmedSkill(this,lvl,cool));
	}
}
