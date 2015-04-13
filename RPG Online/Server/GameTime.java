
public class GameTime
{
	private ServerEngine server;
	private byte second, minute, hour, month, day;
	private int year;
	private int tickrate;
	private double tick;
	
	public GameTime(ServerEngine ser,byte sec, byte min, byte hr, byte d, byte mo, int yr)
	{
		server=ser;
		if(server!=null)
			tickrate=server.getTickrate();
		second=sec;
		minute=min;
		hour=hr;
		day=d;
		month=mo;
		year=yr;
		
		tick=0;
	}
	
	public void tick()
	{
		boolean send=false;
		tick++;
		if(tick>=tickrate/24.0)
		{
			tick-=tickrate/24.0;
			second++;
			send=true;
		}
		if(second>=60)
		{
			second=0;
			minute++;
			//send=true;
		}
		if(minute>=60)
		{
			minute=0;
			hour++;
		}
		if(hour>=24)
		{
			hour=0;
			day++;
		}
		if(day>=28+1)
		{
			day=1;
			month++;
		}
		if(month>=12+1)
		{
			month=1;
			year++;
		}
		
		if(send)
		{
			Message mes=new Message(Message.TIME);
			mes.addByte(second);
			mes.addByte(minute);
			mes.addByte(hour);
			mes.addByte(day);
			mes.addByte(month);
			mes.addInt(year);
			server.sendMessageAll(mes);
		}
	}
	
	public byte getSecond()
	{
		return(second);
	}
	
	public byte getMinute()
	{
		return(minute);
	}
	
	public byte getHour()
	{
		return(hour);
	}
	
	public byte getDay()
	{
		return(day);
	}
	
	public byte getMonth()
	{
		return(month);
	}
	
	public int getYear()
	{
		return(year);
	}
}
