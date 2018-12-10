package reading;

import java.io.IOException;

@SuppressWarnings("serial")
public class DirtyIOException extends RuntimeException
{
	private final IOException x;
	
	public DirtyIOException(IOException x)
	{
		this.x = x;
	}
	
	public IOException getException()
	{
		return x;
	}
}
