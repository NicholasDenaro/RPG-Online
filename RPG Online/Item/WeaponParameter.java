
public class WeaponParameter
{
	private byte parameter;
	private int value;
	
	public WeaponParameter(byte param, int val)
	{
		parameter=param;
		value=val;
	}
	
	public static byte getType(char c)
	{
		switch(c)
		{
			case 'S':
				return(speed);
			case 'A':
				return(accuracy);
			case 'H':
				return(heaviness);
			case 'D':
				return(durability);
		}
		return(-1);
	}
	
	public String toString()
	{
		switch(parameter)
		{
			case speed:
				return("S"+value);
			case accuracy:
				return("A"+value);
			case heaviness:
				return("H"+value);
			case durability:
				return("D"+value);
		}
		return("V0");
	}
	
	public static final byte speed=0;
	public static final byte accuracy=1;
	public static final byte heaviness=2;
	public static final byte durability=3;
}
