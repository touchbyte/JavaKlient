package com.javanetics.turbotransfer;

public class TargetAlbum
{
	protected String folderUuid;
	protected boolean folderActive;
	protected String folderTitle;
	
	public TargetAlbum(String uuid, boolean active, String title)
	{
		this.folderActive = active;
		this.folderTitle = title;
		this.folderUuid = uuid;
	}
	
	public String getFolderUuid()
	{
		return folderUuid;
	}
	public void setFolderUuid(String folderUuid)
	{
		this.folderUuid = folderUuid;
	}
	public boolean isFolderActive()
	{
		return folderActive;
	}
	public void setFolderActive(boolean folderActive)
	{
		this.folderActive = folderActive;
	}
	public String getFolderTitle()
	{
		return folderTitle;
	}
	public void setFolderTitle(String folderTitle)
	{
		this.folderTitle = folderTitle;
	}
	public String toString()
	{
		return folderTitle;
	}
}
