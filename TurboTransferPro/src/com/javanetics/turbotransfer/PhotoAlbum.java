package com.javanetics.turbotransfer;

public class PhotoAlbum
{
	private String id;
	private String name;
	private int roll;
	
	public PhotoAlbum(String id, String name, int roll)
	{
		this.id = id;
		this.name = name;
		this.roll = roll;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRoll()
	{
		return roll;
	}

	public void setRoll(int roll)
	{
		this.roll = roll;
	}

	public String toString()
	{
		return name;
	}
}
