import java.util.ArrayList;


public class Inventory implements Comparable
{
	private ArrayList<ItemStack> items;
	
	public Inventory()
	{
		items=new ArrayList<ItemStack>();
	}
	
	public Inventory add(ItemStack is)
	{
		if(is.getItem() instanceof UseItem||is.getItem() instanceof MiscItem)
		{
			for(int i=0;i<items.size();i++)
				if(items.get(i).getItem().getId()==is.getItem().getId())
				{
					items.get(i).add(is.count());
					return(this);
				}
			items.add(is);
		}
		else
			items.add(is);
		return(this);
	}
	
	public ItemStack remove(int i,int count)
	{
		ItemStack is=items.get(i);
		if(is.getItem() instanceof UseItem||is.getItem() instanceof MiscItem)
		{
			int old=is.count();
			is.add(-count);
			if(is.count()<=0)
			{
				Item it=is.getItem();
				items.remove(i);
				return(new ItemStack(it,old-is.count()));
			}
			else
				return(new ItemStack(is.getItem(),count));
			
		}
		else
			return(items.remove(i));
	}
	
	public boolean contains(ItemStack stack)
	{
		for(int i=0;i<items.size();i++)
		{
			ItemStack is=items.get(i);
			if(is.getItem().getId()==stack.getItem().getId())
			{
				if(is.count()>=stack.count())
				{
					return(true);
				}
			}
		}
		return(false);
	}
	
	public int remove(ItemStack stack)
	{
		for(int i=0;i<items.size();i++)
		{
			ItemStack is=items.get(i);
			if(is.getItem().getId()==stack.getItem().getId())
			{
				if(is.count()>=stack.count())
				{
					remove(i,stack.count());
					return(i);
				}
			}
		}
		return(-1);
	}
	
	public ItemStack removeAll(int i)
	{
		return(items.remove(i));
	}
	
	public int size()
	{
		return(items.size());
	}
	
	public ItemStack get(int i)
	{
		return(items.get(i));
	}
	
	public Inventory clone()
	{
		Inventory inv=new Inventory();
		for(int i=0;i<size();i++)
		{
			inv.add(items.get(i).clone());
		}
		
		return(inv);
	}

	@Override
	public int compareTo(Object o)
	{
		if(o instanceof Inventory==false)
			return(-1);
		Inventory other=(Inventory)o;
		if(other.size()<size())
			return(-1);
		else if(other.size()>size())
			return(1);
		for(int i=0;i<size();i++)
		{
			if(!get(i).equals(other.get(i)))
				return(-1);
		}
		return 0;
	}
	
}
