
public class PlayerEffect
{
	private byte effect;
	private int value;
	private int seconds, minutes, hours;
	
	private int totalTime;
	
	public PlayerEffect(byte ef,int val,int sec, int min, int hr)
	{
		effect=ef;
		value=val;
		seconds=sec;
		minutes=min;
		hours=hr;
		totalTime=seconds+60*(minutes+(60*hours));
	}
	
	public byte getId()
	{
		return(effect);
	}
	
	public int value()
	{
		return(value);
	}
	
	public int getSeconds()
	{
		return(seconds);
	}
	
	public int getMinutes()
	{
		return(minutes);
	}
	
	public int getHours()
	{
		return(hours);
	}
	
	public boolean hasTime()
	{
		seconds--;
		
		if(seconds<=-1)
		{
			if(minutes-->0)
				seconds=59;
		}
		
		if(minutes<=-1)
		{
			if(hours-->0)
				minutes=59;
		}
		
		return(seconds>0||minutes>0||hours>0);
	}
	
	public double percentLeft()
	{
		return((seconds+60*(minutes+(60*hours)))*1.0/totalTime);
	}
	
	public String toStringEffect()
	{
		switch(effect)
		{
			case stun:
				return("Stun");
			case delay:
				return("Delay");
			case tumble:
				return("Tumble");
			case paralysis:
				return("Paralysis");
			case hate:
				return("Hate");
		}
		return("None");
	}
	
	public PlayerEffect copy()
	{
		return(new PlayerEffect(effect,value,seconds,minutes,hours));
	}
	
	public static final byte stun=PlayerStat.LAST_STAT+1;
	public static final byte delay=PlayerStat.LAST_STAT+2;
	public static final byte tumble=PlayerStat.LAST_STAT+3;
	public static final byte paralysis=PlayerStat.LAST_STAT+4;
	public static final byte hate=PlayerStat.LAST_STAT+5;
}
