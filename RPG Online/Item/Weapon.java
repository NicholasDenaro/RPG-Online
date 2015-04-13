import java.util.ArrayList;


public class Weapon extends Equip
{
	private boolean twoHanded;
	//private ArrayList<WeaponParameter> parameters;
	private String parameters;
	private boolean appraised;
	
	private int speed, accuracy, heaviness;
	
	public Weapon(int i,String n,int lvl,boolean th,int agi, int dex, int vit, int str, int def, int sp, int ac, int he)
	{
		super(i,n,lvl,agi,dex,vit,str,def);
		twoHanded=th;
		appraised=false;
		//parameters=new ArrayList<WeaponParameter>();
		parameters="";
		speed=sp;
		accuracy=ac;
		heaviness=he;
	}
	
	public Weapon(Weapon wep, String params, boolean appr)
	{
		super(wep);
		twoHanded=wep.twoHanded;
		setParameters(params);
		appraised=appr;
		speed=wep.speed;
		accuracy=wep.accuracy;
		heaviness=wep.heaviness;
	}
	
	public boolean isTwoHanded()
	{
		return(twoHanded);
	}
	
	public String getParametersAsString()
	{
		/*String params="";
		for(int i=0;i<parameters.size();i++)
			params+=parameters.get(i);
		return(params);*/
		return(parameters);
	}
	
	public void setParameters(String params)
	{
		/*parameters=new ArrayList<WeaponParameter>();
		int paramNum=0;
		while(paramNum<params.length()/2)
		{
			char param=params.charAt(paramNum);
			Integer val=new Integer(params.charAt(paramNum+1));
			parameters.add(new WeaponParameter(WeaponParameter.getType(param),val));
			paramNum++;
		}*/
		parameters=params;
	}
	
	public boolean isAppraised()
	{
		return(appraised);
	}
	
	public void setAppraised(boolean app)
	{
		appraised=app;
	}
	
	
	public int getSpeed()
	{
		int sp=speed;
		if(parameters.indexOf('S')!=-1)
			sp+=new Integer(parameters.charAt(parameters.indexOf('S')+1));
		return(sp);
	}
	
	public int getAccuracy()
	{
		int acc=accuracy;
		if(parameters.indexOf('A')!=-1)
			acc+=new Integer(parameters.charAt(parameters.indexOf('A')+1));
		return(acc);
	}
	
	public int getHeaviness()
	{
		int he=heaviness;
		if(parameters.indexOf('H')!=-1)
			he+=new Integer(parameters.charAt(parameters.indexOf('H')+1));
		return(he);
	}

	public int getDurability()
	{
		int dur=heaviness;
		if(parameters.indexOf('D')!=-1)
			dur+=new Integer(parameters.charAt(parameters.indexOf('D')+1));
		return(dur);
	}
}
