
public class ItemStack// implements Comparable
{
	private Item item;
	private int count;
	
	public ItemStack(Item it)
	{
		item=it;
		count=1;
	}
	
	public ItemStack(Item it, int c)
	{
		item=it;
		count=c;
	}
	
	public ItemStack(int it, int c)
	{
		item=Item.get(it);
		count=c;
	}
	
	public Item getItem()
	{
		return(item);
	}
	
	public int count()
	{
		return(count);
	}
	
	public void add(int co)
	{
		count+=co;
	}
	
	public ItemStack clone()
	{
		return(new ItemStack(item,count));
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof ItemStack==false)
			return(false);
		ItemStack other=(ItemStack)o;
		if(item.getId()==other.item.getId()&&count==other.count)
		{
			if(item instanceof Weapon)
			{
				Weapon wep=(Weapon)item;
				Weapon wepother=(Weapon)other.item;
				return(wep.getParametersAsString().equals(wepother.getParametersAsString())&&(wep.isAppraised()==wepother.isAppraised()));
			}
			return(true);
		}
		return(false);
	}
	
	public String toString()
	{
		return(item.getName()+(count>1?" x"+count:""));
	}
}
