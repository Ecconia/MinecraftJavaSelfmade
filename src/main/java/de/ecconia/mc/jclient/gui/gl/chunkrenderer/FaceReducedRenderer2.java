package de.ecconia.mc.jclient.gui.gl.chunkrenderer;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import de.ecconia.mc.jclient.gui.gl.helper.FaceBufferWrapper;
import de.ecconia.mc.jclient.gui.gl.models.BlockLib;

public class FaceReducedRenderer2 extends ChunkRenderer
{
	private float[] dataArray;
	private int[] indicesArray;
	
	public FaceReducedRenderer2(int cx, int cy, int[][][] blocks, BlockLib blockModels)
	{
		super(cx, cy);
		
		List<Face> xpFaces = new ArrayList<>();
		List<Face> ypFaces = new ArrayList<>();
		List<Face> zpFaces = new ArrayList<>();
		
		List<Face> xmFaces = new ArrayList<>();
		List<Face> ymFaces = new ArrayList<>();
		List<Face> zmFaces = new ArrayList<>();
		
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				int block = blocks[x][z][0];
				
				if(block != 0)
				{
					ymFaces.add(new Face(block, x, 0, z));
				}
			}
		}
		
		for(int y = 0; y < 256; y++)
		{
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					int block = blocks[x][z][y];
					
					if(block == 0)
					{
						if(y != 0)
						{
							int block2 = blocks[x][z][y - 1];
							if(block2 != 0)
							{
								ypFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(y != 255)
						{
							int block2 = blocks[x][z][y + 1];
							if(block2 != 0)
							{
								ymFaces.add(new Face(block2, x, y + 1, z));
							}
						}
						
						if(z != 15)
						{
							int block2 = blocks[x][z + 1][y];
							if(block2 != 0)
							{
								zmFaces.add(new Face(block2, x, y, z + 1));
							}
						}
						
						if(z != 0)
						{
							int block2 = blocks[x][z - 1][y];
							if(block2 != 0)
							{
								zpFaces.add(new Face(block2, x, y, z));
							}
						}
						
						if(x != 15)
						{
							int block2 = blocks[x + 1][z][y];
							if(block2 != 0)
							{
								xmFaces.add(new Face(block2, x + 1, y, z));
							}
						}
						
						if(x != 0)
						{
							int block2 = blocks[x - 1][z][y];
							if(block2 != 0)
							{
								xpFaces.add(new Face(block2, x, y, z));
							}
						}
					}
				}
			}
		}
		
		int floatsPerVertex = 9;
		int vertexPerFace = 1;
		
		int faceAmount = ymFaces.size() + ypFaces.size() + xmFaces.size() + xpFaces.size() + zmFaces.size() + zpFaces.size();
		dataArray = new float[faceAmount * floatsPerVertex * vertexPerFace];
		indicesArray = new int[faceAmount * 6];
		
		int pd = 0;
		int pi = 0;
		
		int oi = 0;
		
		for(Face f : ymFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x, f.y - 0.5f, f.z, color[0], color[1], color[2], 0, -1, 0,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
		
		for(Face f : xmFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x - 0.5f, f.y, f.z, color[0], color[1], color[2], -1, 0, 0,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
		
		for(Face f : zmFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x, f.y, f.z - 0.5f, color[0], color[1], color[2], 0, 0, -1,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
		
		for(Face f : ypFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x, f.y - 0.5f, f.z, color[0], color[1], color[2], 0, 1, 0,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
		
		for(Face f : xpFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x - 0.5f, f.y, f.z, color[0], color[1], color[2], 1, 0, 0,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
		
		for(Face f : zpFaces)
		{
			float[] color = blockModels.get(f.type).getColor();
			float[] data = {
				f.x, f.y, f.z - 0.5f, color[0], color[1], color[2], 0, 0, 1,
			};
			int[] indices = {
				oi + 0,
			};
			oi += vertexPerFace;
			
			System.arraycopy(data, 0, dataArray, pd, floatsPerVertex * vertexPerFace);
			pd += floatsPerVertex * vertexPerFace;
			System.arraycopy(indices, 0, indicesArray, pi, 1);
			pi += 1;
		}
	}
	
	private static class Face
	{
		public final int x;
		public final int y;
		public final int z;
		public final int type;
		
		public Face(int type, int x, int y, int z)
		{
			this.type = type;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	private FaceBufferWrapper buffer;
	private int amount;
	
	@Override
	public void load(GL3 gl)
	{
		buffer = new FaceBufferWrapper(gl, dataArray, indicesArray);
		amount = indicesArray.length;
		dataArray = null;
		indicesArray = null;
	}
	
	@Override
	public void draw(GL3 gl)
	{
		buffer.use(gl);
		gl.glDrawElements(GL3.GL_POINTS, amount, GL3.GL_UNSIGNED_INT, 0);
	}
	
	@Override
	public void delete(GL3 gl)
	{
		buffer.delete(gl);
	}
}
