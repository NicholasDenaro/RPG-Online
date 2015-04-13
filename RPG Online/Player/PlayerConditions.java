import java.util.ArrayList;


public class PlayerConditions
{
	private LivingEntity living;
	private ArrayList<PlayerEffect> effects;
	
	public PlayerConditions(LivingEntity liv)
	{
		living=liv;
		effects=new ArrayList<PlayerEffect>();
	}
	
	public void addEffect(PlayerEffect effect)
	{
		effects.add(effect);
	}
	
	public void remove(int index)
	{
		effects.remove(index);
	}
	
	public int size()
	{
		return(effects.size());
	}
	
	public PlayerEffect get(int i)
	{
		return(effects.get(i));
	}
	
	public PlayerEffect getEffect(int effect)
	{
		for(int i=0;i<effects.size();i++)
		{
			if(effects.get(i).getId()==effect)
				return(effects.get(i));
		}
		return(null);
	}
	
	public boolean contains(int effect)
	{
		for(int i=0;i<effects.size();i++)
		{
			if(effects.get(i).getId()==effect)
				return(true);
		}
		return(false);
	}
	
	public MessageCluster checkEffectsServer()
	{
		MessageCluster cluster=new MessageCluster();
		for(int i=effects.size()-1;i>=0;i--)
		{
			PlayerEffect effect=effects.get(i);
			if(!effect.hasTime())
			{
				Message mes=new Message(Message.EFFECT);
				mes.addLong(living.uuid);
				mes.addBoolean(false);
				mes.addInt(i);
				cluster.addMessage(mes);
				effects.remove(i);
			}
			else
			{
				if(effect.getId()==PlayerStat.health)
				{
					living.stats.increase(new PlayerStat(PlayerStat.health,effect.value()));
					Message mes=new Message(Message.STAT);
					mes.addBoolean(false);
					//System.out.println("uuid: "+living.uuid);
					mes.addLong(living.uuid);
					mes.addByte(PlayerStat.health);
					mes.addInt(living.stats.getStat(PlayerStat.health).value());
					cluster.addMessage(mes);
				}
			}
		}
		return(cluster);
	}
	
	public void checkEffects()
	{
		for(int i=effects.size()-1;i>=0;i--)
		{
			PlayerEffect effect=effects.get(i);
			effect.hasTime();
		}
	}
}
