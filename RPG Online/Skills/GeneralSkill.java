
public class GeneralSkill extends Skill
{

	public GeneralSkill(int i,String n,String ty,int cos,double dmgMult,int coolsec,int coolmin,int coolhour,PlayerEffect ef)
	{
		super(i,n,ty,cos,dmgMult,coolsec,coolmin,coolhour,ef);
	}
	
	public GeneralSkill(Skill s, int lvl, int cool)
	{
		super(s,lvl,cool);
	}
	
	public GeneralSkill clone(int lvl, int cool)
	{
		return(new GeneralSkill(this,lvl,cool));
	}
	
}
