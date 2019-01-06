package de.ecconia.mc.jclient.gui.gl.models;

import de.ecconia.mc.jclient.Logger;

public class BlockLib
{
	private BlockModel lib[];
	
	public BlockLib()
	{
		//TODO: Keep updated?
		//Max block states in version 1.13.2
		lib = new BlockModel[8599];
	}
	
	public BlockModel get(int blockID)
	{
		if(lib.length <= blockID)
		{
			Logger.important("There are more block types, please update: " + blockID);
			
			BlockModel newLib[] = new BlockModel[blockID + 1];
			System.arraycopy(lib, 0, newLib, 0, lib.length);
			
			lib = newLib;
		}
		
		BlockModel model = lib[blockID];
		if(model == null)
		{
			model = new BlockModel.Color();
			lib[blockID] = model;
		}
		
		return model;
	}
}
