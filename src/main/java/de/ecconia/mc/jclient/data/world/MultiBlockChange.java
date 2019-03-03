package de.ecconia.mc.jclient.data.world;

import java.util.ArrayList;
import java.util.List;

public class MultiBlockChange
{
	private final int x;
	private final int z;
	
	private final List<BlockChange> changes;
	
	public MultiBlockChange(int x, int z, int amountHint)
	{
		this.x = x;
		this.z = z;
		
		this.changes = new ArrayList<>(amountHint);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public void add(BlockChange change)
	{
		changes.add(change);
	}
	
	public List<BlockChange> getChanges()
	{
		return changes;
	}
	
	public static class BlockChange
	{
		private final int x;
		private final int y;
		private final int z;
		private final int data;
		
		public BlockChange(int x, int y, int z, int data)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.data = data;
		}
		
		public int getX()
		{
			return x;
		}
		
		public int getY()
		{
			return y;
		}
		
		public int getZ()
		{
			return z;
		}
		
		public int getData()
		{
			return data;
		}
	}
}
