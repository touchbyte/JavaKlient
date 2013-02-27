package com.javanetics.turbotransfer;

public class TargetAlbumiOS extends TargetAlbum
{
	protected int roll;
	
	public TargetAlbumiOS(String uuid, boolean active, String title)
	{
		super(uuid, false, title);
	}

	public TargetAlbumiOS(String uuid, String title, int roll)
	{
		super(uuid, false, title);
		this.roll = roll;
	}

	public int getRoll()
	{
		return roll;
	}

	public void setRoll(int roll)
	{
		this.roll = roll;
	}

	public boolean isFolderActive()
	{
		return this.roll == 1;
	}
}
