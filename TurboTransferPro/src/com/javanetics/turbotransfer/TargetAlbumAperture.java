package com.javanetics.turbotransfer;

public class TargetAlbumAperture extends TargetAlbum
{
	protected String folderType;

	public TargetAlbumAperture(String uuid, boolean active, String title)
	{
		super(uuid, active, title);
	}
	
	public TargetAlbumAperture(String uuid, boolean active, String title, String foldertype)
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
