package com.javanetics.turbotransfer;

public class TargetAlbumiPhoto extends TargetAlbum
{
	protected String folderType;

	public TargetAlbumiPhoto(String uuid, boolean active, String title)
	{
		super(uuid, active, title);
	}
	
	public TargetAlbumiPhoto(String uuid, boolean active, String title, String foldertype)
	{
		super(uuid, active, title);
		this.folderType = foldertype;
	}
	
	public String getFolderType()
	{
		return folderType;
	}

	public void setFolderType(String folderType)
	{
		this.folderType = folderType;
	}
}
