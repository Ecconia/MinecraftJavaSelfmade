package de.ecconia.mc.jclient.gui.gl.chunkrenderer;

import com.jogamp.opengl.GL2;

import de.ecconia.mc.jclient.gui.gl.Helper3D;
import de.ecconia.mc.jclient.gui.gl.models.BlockLib;
import de.ecconia.mc.jclient.gui.gl.models.BlockModel;

public class PrimitiveChunkRenderer extends ChunkRenderer
{
	private final int blocks[][][];
	private final BlockLib blockModels;
	
//	private float[][] colors;
	
	public PrimitiveChunkRenderer(int x, int y, int blocks[][][], BlockLib blockModels)
	{
		super(x, y);
		
		this.blocks = blocks;
		this.blockModels = blockModels;
		
		//Does no longer work, cause there are plenty more blockID's now.
//		generateColors();
	}
	
//	private void generateColors()
//	{
//		Random r = new Random();
//		
//		List<Integer> blocktypes = new ArrayList<>();
//		for(int iy = 0; iy < 256; iy++)
//		{
//			for(int ix = 0; ix < 16; ix++)
//			{
//				for(int iz = 0; iz < 16; iz++)
//				{
//					int block = blocks[ix][iz][iy];
//					if(block != 0)
//					{
//						blocktypes.add(block);
//					}
//				}
//			}
//		}
//		
//		final float[] c = {0f, 0.2f, 0.8f, 1f};
//		
//		colors = new float[blocktypes.size()][3];
//		for(int i = 0; i < blocktypes.size(); i++)
//		{
//			int red = r.nextInt(4);
//			int green = r.nextInt(4);
//			int blue = r.nextInt(4);
//			
//			colors[i][0] = c[red];
//			colors[i][1] = c[green];
//			colors[i][2] = c[blue];
//		}
//	}
	
	@Override
	public void render(GL2 gl)
	{
		gl.glTranslated(offsetX, 0, offsetZ);
		
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					int block = blocks[x][z][y];
					if(block != 0)
					{
//						gl.glColor3f(colors[block][0], colors[block][1], colors[block][2]);
						BlockModel model = blockModels.get(block);
						model.draw(gl, x, y, z);
						Helper3D.drawBlock(gl, x, y, z);
					}
				}
			}
		}
	}
}
