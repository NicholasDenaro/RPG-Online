import java.nio.ByteBuffer;


public class Message
{
	protected ByteBuffer buffer;
	private byte id;
	
	public Message(byte b)
	{
		buffer=ByteBuffer.allocate(1024);
		buffer.put(b);
		id=b;
	}
	
	public int size()
	{
		return(buffer.position());
	}
	
	public byte getId()
	{
		return(id);
	}
	
	public byte[] getBytes()
	{
		int size=buffer.position();
		byte[] bytes=new byte[size];
		buffer.position(0);
		buffer.get(bytes,0,bytes.length);
		buffer.position(size);
		return(bytes);
	}
	
	public void addByte(int b)
	{
		buffer.put((byte)b);
	}
	
	public void addBoolean(boolean b)
	{
		if(b)
			buffer.put((byte)1);
		else
			buffer.put((byte)0);
	}
	
	public void addChar(char c)
	{
		buffer.putChar(c);
	}
	
	public void addShort(int s)
	{
		buffer.putShort((short)s);
	}
	
	public void addInt(int i)
	{
		buffer.putInt(i);
	}
	
	public void addLong(long l)
	{
		buffer.putLong(l);
	}
	
	public void addDouble(double d)
	{
		buffer.putDouble(d);
	}
	
	public void addString(String s)
	{
		buffer.putInt(s.length());
		for(int i=0;i<s.length();i+=1)
			buffer.putChar(s.charAt(i));
	}
	
	public static final byte REGISTER=0;
	public static final byte LOGIN=1;
	public static final byte LOGOUT=2;
	public static final byte ZONE=3;
	public static final byte MOVE=4;
	public static final byte SPAWN=5;
	public static final byte LEAVE=6;
	public static final byte WARP=7;
	public static final byte ADDITEM=8;
	public static final byte USEITEM=9;
	public static final byte STAT=10;
	public static final byte STATS=11;
	public static final byte EQUIPALL=12;
	public static final byte EQUIP=13;
	public static final byte EFFECT=14;
	public static final byte ACTION=15;
	public static final byte ADDSKILL=16;
	public static final byte SKILL=17;
	public static final byte UUID=18;
	public static final byte QUERY=19;
	public static final byte QUEST=20;
	public static final byte CRAFTITEM=21;
	public static final byte TIME=(byte)254;
	public static final byte END=(byte)255;
}

class Query
{
	public static final byte QUESTDONE=0;
	public static final byte HASQUEST=1;
	public static final byte STARTQUEST=2;
	public static final byte COMPLETEQUEST=3;
}
