package de.ecconia.mc.jclient.gui.gl.helper;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class BufferWrapper implements Deleteable
{
	private final int vaoID;
	
	private final int amount;
	
	private final int vboID;
	private final int eabID;
	
	public BufferWrapper(GL3 gl, float[] data, int[] indices)
	{
		this.amount = indices.length;
		
		int[] ret = new int[3];
		
		//Generate the >Vertex Array Object< which holds the buffer we want to create later for quick later usage.
		gl.glGenVertexArrays(1, ret, 0);
		vaoID = ret[0];
		//Generate the >Vertex Buffer Object< and the >Element Array Buffer<
		gl.glGenBuffers(2, ret, 1);
		vboID = ret[1];
		eabID = ret[2];
		
		//Use the VAO and the VBO:
		gl.glBindVertexArray(vaoID);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboID);
		
		//Copy values into the VBO:
		FloatBuffer fBuf = Buffers.newDirectFloatBuffer(data);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, Float.BYTES * data.length, fBuf, GL3.GL_STATIC_DRAW);
		
		//Use the EAB:
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, eabID);
		
		//Copy values into the EAB:
		IntBuffer iBuf = Buffers.newDirectIntBuffer(indices);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, iBuf, GL3.GL_STATIC_DRAW);
		
		//Define, how the values should be used in the shader:
		//Position data: <x> <y> <z> - - -
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 0);
		gl.glEnableVertexAttribArray(0);
		//Color data: - - - <r> <g> <b>
		gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
		gl.glEnableVertexAttribArray(1);
		
		//Cleanup:
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
	}
	
	@Override
	public void delete(GL3 gl)
	{
		gl.glDeleteBuffers(2, new int[] {vboID, eabID}, 0);
		gl.glDeleteVertexArrays(1, new int[] {vaoID}, 0);
	}
	
	public void use(GL3 gl)
	{
		gl.glBindVertexArray(vaoID);
	}
	
	public void draw(GL3 gl)
	{
		gl.glDrawElements(GL3.GL_TRIANGLES, amount, GL3.GL_UNSIGNED_INT, 0);
	}
}
