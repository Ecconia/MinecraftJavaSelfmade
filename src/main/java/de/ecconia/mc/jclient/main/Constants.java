package de.ecconia.mc.jclient.main;

public class Constants
{
	//TODO: Some get this setting from somewhere.
	public static final int amountBlockstates = 8599;
	
	public static final String shaderPath = "files/shaders/";
	public static final String textureFolder = "files/textures/blockdata/";
	public static final String textureFile = "blockdata.png";
	
	public static final String userFile = "files/user.json";
	
	//Turn to true on Windows...
	//TODO: Either move all debugging into a JOGL window, OR find a solution which works on all OS's (maybe GLCanvas)
	public static final boolean external3DWindow = false;
}
