package com.javanetics.turbotransfer;

public class TargetAlbumLoadingIndicator extends TargetAlbum
{
	public TargetAlbumLoadingIndicator()
	{
		super("", false, "");
	}
	
	public String getTitle()
	{
		return Localizer.sharedLocalizer().localizedString("LoadingList");
	}
}
