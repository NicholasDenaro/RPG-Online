
public class SwordSkill extends Skill
{

	public SwordSkill(int i, String n, String ty,int cos, double dmgMult, int coolsec, int coolmin, int coolhour ,PlayerEffect ef)
	{
		super(i,n,ty,cos,dmgMult,coolsec,coolmin,coolhour,ef);
	}
	
	public SwordSkill(Skill s, int lvl, int cool)
	{
		super(s,lvl,cool);
	}
	
	
	public boolean canUse(Equipment equips, PlayerStatus stats)
	{
		return(cooldown==0&&stats.currentAp>=cost&&equips.getWeapon()!=null&&(equips.getWeapon() instanceof Sword));
	}
	
	@Override
	public boolean correctEquip(Equipment equips)
	{
		return(equips.getWeapon()!=null&&(equips.getWeapon() instanceof Sword));
	}
	
	public SwordSkill clone(int lvl, int cool)
	{
		return(new SwordSkill(this,lvl,cool));
	}
}
