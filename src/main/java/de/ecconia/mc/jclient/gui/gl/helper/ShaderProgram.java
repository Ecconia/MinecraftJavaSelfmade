package de.ecconia.mc.jclient.gui.gl.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

public class ShaderProgram
{
	private int id;
	private int[] uniformIDs;
	
	public ShaderProgram(GL3 gl, String name)
	{
		//Vertex Shader:
		String vShaderCode = loadFile(name + ".vs");
		if(vShaderCode == null)
		{
			throw new RuntimeException("Could not find shader file: " + name + ".vs");
		}
		int vertexShaderID = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
		gl.glShaderSource(vertexShaderID, 1, new String[] {vShaderCode}, new int[] {vShaderCode.length()}, 0);
		gl.glCompileShader(vertexShaderID);
		
		IntBuffer retBuf = IntBuffer.allocate(1);
		gl.glGetShaderiv(vertexShaderID, GL3.GL_COMPILE_STATUS, retBuf);
		if(retBuf.get(0) == 0)
		{
			int[] logLength = new int[1];
			gl.glGetShaderiv(vertexShaderID, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			ByteBuffer bBuf = ByteBuffer.allocate(logLength[0]);
			gl.glGetShaderInfoLog(vertexShaderID, bBuf.limit(), null, bBuf);
			
			throw new RuntimeException("Error loading Vertex shader: >" + new String(bBuf.array()) + "<");
		}
		
		//Geometry shader:
		String gShaderCode = loadFile(name + ".gs");
		int geometryShaderID = 0;
		if(gShaderCode != null)
		{
			geometryShaderID = gl.glCreateShader(GL3.GL_GEOMETRY_SHADER);
			gl.glShaderSource(geometryShaderID, 1, new String[] {gShaderCode}, new int[] {gShaderCode.length()}, 0);
			gl.glCompileShader(geometryShaderID);
			
			retBuf = IntBuffer.allocate(1);
			gl.glGetShaderiv(geometryShaderID, GL3.GL_COMPILE_STATUS, retBuf);
			if(retBuf.get(0) == 0)
			{
				int[] logLength = new int[1];
				gl.glGetShaderiv(geometryShaderID, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
				ByteBuffer bBuf = ByteBuffer.allocate(logLength[0]);
				gl.glGetShaderInfoLog(geometryShaderID, bBuf.limit(), null, bBuf);
				
				throw new RuntimeException("Error loading Geometry shader: >" + new String(bBuf.array()) + "<");
			}
		}
		
		//Fragment Shader:
		String fShaderCode = loadFile(name + ".fs");
		if(fShaderCode == null)
		{
			throw new RuntimeException("Could not find shader file: " + name + ".fs");
		}
		int fragmentShaderID = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fragmentShaderID, 1, new String[] {fShaderCode}, new int[] {fShaderCode.length()}, 0);
		gl.glCompileShader(fragmentShaderID);
		
		retBuf = IntBuffer.allocate(1);
		gl.glGetShaderiv(fragmentShaderID, GL3.GL_COMPILE_STATUS, retBuf);
		if(retBuf.get(0) == 0)
		{
			int[] logLength = new int[1];
			gl.glGetShaderiv(fragmentShaderID, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			ByteBuffer bBuf = ByteBuffer.allocate(logLength[0]);
			gl.glGetShaderInfoLog(fragmentShaderID, bBuf.limit(), null, bBuf);
			
			throw new RuntimeException("Error loading Fragment shader: >" + new String(bBuf.array()) + "<");
		}
		
		//Program:
		id = gl.glCreateProgram();
		gl.glAttachShader(id, vertexShaderID);
		if(gShaderCode != null)
		{
			gl.glAttachShader(id, geometryShaderID);
		}
		gl.glAttachShader(id, fragmentShaderID);
		gl.glLinkProgram(id);
		
		retBuf = IntBuffer.allocate(1);
		gl.glGetProgramiv(id, GL3.GL_LINK_STATUS, retBuf);
		if(retBuf.get(0) == 0)
		{
			int[] logLength = new int[1];
			gl.glGetProgramiv(id, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			ByteBuffer bBuf = ByteBuffer.allocate(logLength[0]);
			gl.glGetProgramInfoLog(id, bBuf.limit(), null, bBuf);
			
			throw new RuntimeException("Error creating Program: >" + new String(bBuf.array()) + "<");
		}
		
		gl.glDeleteShader(vertexShaderID);
		gl.glDeleteShader(fragmentShaderID);
		
		//Uniform stuff:
		List<String> uniforms = new ArrayList<>();
		for(String line : vShaderCode.split("\n"))
		{
			if(line.startsWith("uniform"))
			{
				String[] parts = line.split(" ");
				if(parts.length < 3)
				{
					throw new RuntimeException("Weird uniform variable declaration: " + line);
				}
				
				String variable = parts[2];
				variable = variable.substring(0, variable.indexOf(';'));
				
				//System.out.println("Shader " + name + " has uniform variable: " + variable);
				uniforms.add(variable);
			}
		}
		
		uniformIDs = new int[uniforms.size()];
		for(int i = 0; i < uniforms.size(); i++)
		{
			uniformIDs[i] = gl.glGetUniformLocation(id, uniforms.get(i));
		}
	}
	
	public void use(GL3 gl)
	{
		gl.glUseProgram(id);
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getUniformID(int location)
	{
		return uniformIDs[location];
	}
	
	public void setUniform(GL3 gl, int id, float[] matrix)
	{
		gl.glUniformMatrix4fv(uniformIDs[id], 1, false, matrix, 0);
	}
	
	private static String loadFile(String path)
	{
		try
		{
			File file = new File(path);
			if(!file.exists())
			{
				return null;
			}
			
			List<String> lines = Files.readAllLines(file.toPath());
			
			String ret = "";
			for(String line : lines)
			{
				ret += line + "\n";
			}
			return ret;
		}
		catch(IOException e)
		{
			if(e instanceof FileNotFoundException)
			{
				throw new RuntimeException("Could not find shader file: " + path);
			}
			
			e.printStackTrace();
			throw new RuntimeException("Could not load shader code: " + path);
		}
	}
}
