package com.javanetics.turbotransfer;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.io.Buffer;

public class FileContentExchange extends ContentExchange
{
	long transferredSize;
	
	public FileContentExchange()
	{
		super();
		initExchange();
	}

	public FileContentExchange(boolean cacheFields)
	{
		super(cacheFields);
		initExchange();
	}
	
	protected void initExchange()
	{
		this.setMethod("PUT");
		transferredSize = 0;
	}

	@Override
	public synchronized void setFileForUpload(File fileForUpload)
			throws IOException
	{
		super.setFileForUpload(fileForUpload);
  	this.setRequestHeader("Content-Type", "application/octet-stream");
  	this.setRequestHeader("Content-Length", String.valueOf(fileForUpload.length()));
	}

	protected void onResponseComplete() throws IOException
  {
      int status = getResponseStatus();
      System.out.println("Status = " + status);
  }

	
	
	@Override
	protected synchronized void onResponseHeader(Buffer arg0, Buffer arg1)
			throws IOException
	{
		System.out.println("onREsponseHeader " + arg0.length() + ":" + arg1.length());
		super.onResponseHeader(arg0, arg1);
	}

	@Override
	protected synchronized void onResponseStatus(Buffer version, int status,
			Buffer reason) throws IOException
	{
		System.out.println("onREsponseStatus " + version.length() + ":status:" + status + ":" + reason.length());
		super.onResponseStatus(version, status, reason);
	}

	@Override
	protected synchronized void onResponseContent(Buffer content)
			throws IOException
	{
		transferredSize += content.length();
		System.out.println("Total transferred: " + transferredSize);
		super.onResponseContent(content);
	}

	
}
