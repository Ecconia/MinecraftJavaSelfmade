package old.reading;

import java.io.IOException;

@SuppressWarnings("serial")
public class DirtyIOException extends RuntimeException
{
	public DirtyIOException(IOException x)
	{
		super(x);
	}
}
