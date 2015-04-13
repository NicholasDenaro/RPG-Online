
public class PlayerStat
{
	private byte stat;
	private int value;
	
	public PlayerStat(byte st, int val)
	{
		stat=st;
		value=val;
	}
	
	public void add(int val)
	{
		value+=val;
	}
	
	public void set(int val)
	{
		value=val;
	}
	
	public int value()
	{
		return(value);
	}
	
	public byte getId()
	{
		return(stat);
	}
	
	public String toStringStat()
	{
		switch(stat)
		{
			case health:
				return("HP");
			case durability:
				return("DUR");
			case stamina:
				return("STA");
			case hunger:
				return("HGR");
			case agility:
				return("AGI");
			case vitality:
				return("VIT");
			case strength:
				return("STR");
			case defense:
				return("DEF");
			case ap:
				return("AP");
		}
		return("");
	}
	
	public String toStringValue()
	{
		return((value>=0?"+":"")+value);
	}
	
	public String toString()
	{
		switch(stat)
		{
			case maxhealth:
				return("HP: "+value);
			case durability:
				return("DUR: "+value);
			case stamina:
				return("STA: "+value);
			case hunger:
				return("HGR: "+value);
			case agility:
				return("AGI: "+value);
			case vitality:
				return("VIT: "+value);
			case strength:
				return("STR: "+value);
			case defense:
				return("DEF: "+value);
			case ap:
				return("AP: "+value);
		}
		return("");
	}
	
	public static final byte level=0;
	public static final byte statpoints=1;
	public static final byte exp=2;
	public static final byte maxhealth=3;
	public static final byte health=4;
	public static final byte durability=5;
	public static final byte stamina=6;
	public static final byte hunger=7;
	public static final byte agility=8;
	public static final byte vitality=9;
	public static final byte dexterity=10;
	public static final byte strength=11;
	public static final byte defense=12;
	public static final byte ap=13;
	
	public static final byte LAST_STAT=13;
}
