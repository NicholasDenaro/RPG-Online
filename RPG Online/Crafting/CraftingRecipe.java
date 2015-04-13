import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class CraftingRecipe
{
	public static TreeMap<Inventory,CraftingRecipe> recipes=new TreeMap<Inventory,CraftingRecipe>();
	
	private ItemStack product;
	private Item tool;
	private List<ItemStack> ingredients;
	
	public CraftingRecipe(ItemStack prod, Item tl, ItemStack[] ingr)
	{
		product=prod;
		tool=tl;
		Inventory inv=new Inventory();
		for(ItemStack is:ingr)
			inv.add(is);
		recipes.put(inv,this);
	}
	
	public ItemStack getProduct()
	{
		return(product);
	}
	
	public Item getTool()
	{
		return(tool);
	}
	
	public static BlacksmithRecipe wooden_sword=new BlacksmithRecipe(new ItemStack(Item.wooden_sword),Item.blacksmith_hammer,new ItemStack[]{new ItemStack(Item.wood,5)});
}
