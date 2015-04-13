import java.util.ArrayList;


public class UseItem extends Item
{
	private ItemEffect[] effects;

	public UseItem(int i,String n, ItemEffect[] ef)
	{
		super(i,n);
		effects=ef;
	}
	
	public ItemEffect[] getEffects()
	{
		return(effects);
	}
	
	public int size()
	{
		return(effects.length);
	}
	
	public ItemEffect getEffect(int i)
	{
		if(i>=0&&i<size())
			return(effects[i]);
		else
			return(null);
	}
}
