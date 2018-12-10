package reading.helper;

public class ArrayProvider extends Provider
{
	private final byte[] bytes;
	private int position = 0;
	
	public ArrayProvider(byte[] bytes)
	{
		this.bytes = bytes;
	}
	
	@Override
	public int getByte()
	{
		return bytes[position++];
	}
	
	@Override
	public int remainingBytes()
	{
		return bytes.length - position;
	}
	
	@Override
	public void reset()
	{
		position = 0;
	}
}
