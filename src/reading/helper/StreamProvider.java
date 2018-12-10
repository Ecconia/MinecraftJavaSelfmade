package reading.helper;

import java.io.IOException;
import java.io.InputStream;

import reading.DirtyIOException;
import reading.DirtyStreamEndException;

public class StreamProvider extends Provider
{
	private final InputStream is;
	
	public StreamProvider(InputStream is)
	{
		this.is = is;
	}
	
	@Override
	public int getByte()
	{
		int i;
		try
		{
			i = is.read();
			
			if(i == -1)
			{
				throw new DirtyStreamEndException();
			}
			
			return i;
		}
		catch(IOException e)
		{
			//TODO: Unwrap.
			throw new DirtyIOException(e);
		}
	}
	
	@Override
	public int remainingBytes()
	{
		throw new UnsupportedOperationException("It is unsafe to get the remaining bytes from an InputStream.");
	}
	
	@Override
	public void reset()
	{
		throw new UnsupportedOperationException("Cannot reset an InputStream.");
	}
}
