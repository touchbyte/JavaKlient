package com.javanetics.turbotransfer;

public class TargetAlbumFolder extends TargetAlbum
{
	protected String folderPath;

	public TargetAlbumFolder(String uuid, boolean active, String title)
	{
		super(uuid, active, title);
	}
	
	public TargetAlbumFolder(String uuid, boolean active, String title, String path)
	{
		super(uuid, active, title);
		this.folderPath = path;
	}
	
	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public String toString()
	{
		return folderPath;
	}
}
