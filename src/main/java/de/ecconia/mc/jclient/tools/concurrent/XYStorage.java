package de.ecconia.mc.jclient.tools.concurrent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class XYStorage<T>
{
	private final ReentrantLock lock = new ReentrantLock(true);
	private final Map<Integer, Map<Integer, T>> xList = new HashMap<>();
	
	public T put(int x, int z, T type)
	{
		lock.lock();
		
		try
		{
			Map<Integer, T> zList = xList.get(x);
			if(zList == null)
			{
				zList = new HashMap<>();
				xList.put(x, zList);
			}
			
			return zList.put(z, type);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public T get(int x, int z)
	{
		lock.lock();
		
		try
		{
			Map<Integer, T> zList = xList.get(x);
			if(zList == null)
			{
				return null;
			}
			
			return zList.get(z);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public Iterator<T> iterator()
	{
		lock.lock();
		
		Iterator<Map<Integer, T>> outer = xList.values().iterator();
		
		return new Iterator<T>()
		{
			private T next;
			private Iterator<T> inner;
			
			{
				if(outer.hasNext())
				{
					inner = outer.next().values().iterator();
					makeNext();
				}
				else
				{
					lock.unlock();
				}
			}
			
			private void makeNext()
			{
				if(inner.hasNext())
				{
					next = inner.next();
				}
				else
				{
					if(outer.hasNext())
					{
						inner = outer.next().values().iterator();
						makeNext();
					}
					else
					{
						next = null;
						lock.unlock();
					}
				}
			}
			
			@Override
			public T next()
			{
				T tmp = next;
				makeNext();
				return tmp;
			}
			
			@Override
			public boolean hasNext()
			{
				return next != null;
			}
		};
	}
	
	public void clear()
	{
		lock.lock();
		
		xList.clear();
		
		lock.unlock();
	}
}
