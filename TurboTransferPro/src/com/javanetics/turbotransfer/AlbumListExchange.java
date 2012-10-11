package com.javanetics.turbotransfer;

import java.io.IOException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpExchange;

public class AlbumListExchange extends ContentExchange
{

	public AlbumListExchange()
	{
		super();
	}

	public AlbumListExchange(boolean cacheFields)
	{
		super(cacheFields);
	}

	@Override
	protected void onResponseComplete() throws IOException
	{
		super.onResponseComplete();
	}
}
