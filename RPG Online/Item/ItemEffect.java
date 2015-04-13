
public class ItemEffect
{
	private PlayerStat stat;
	private int seconds;
	private int minutes;
	private int hours;
	
	
	public ItemEffect(PlayerStat st, int sec, int min, int ho)
	{
		stat=st;
		seconds=sec;
		hours=ho;
		minutes=min;
	}
	public ItemEffect(PlayerStat st)
	{
		stat=st;
		seconds=0;
		minutes=0;
		hours=0;
		
	}
	
	public PlayerStat getStat()
	{
		return(stat);
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
	
	public boolean isInstant()
	{
		return(seconds==0&&minutes==0&&hours==0);
	}
	
	public String toString()
	{
		String out=stat.toStringStat()+" "+stat.toStringValue();
		if(hours>0||minutes>0||seconds>0)
			out+=" "+hours+":"+(minutes<10?"0":"")+minutes+":"+(seconds<10?"0":"")+seconds;
		else
			out+=" instant";
		return(out);
			
	}
}
