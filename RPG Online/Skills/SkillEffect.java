
public class SkillEffect
{
	private int effect;
	private double chance;
	private double perlevel;
	
	public SkillEffect(int ef, double base, double perlvl)
	{
		effect=ef;
		chance=base;
		perlevel=perlvl;
	}
	
	public double getChance(int lvl)
	{
		return(chance+perlevel*lvl);
	}
	
	public static final int stun=0;
	public static final int delay=1;
	public static final int tumble=2;
	public static final int paralysis=3;
	public static final int hate=4;
}
