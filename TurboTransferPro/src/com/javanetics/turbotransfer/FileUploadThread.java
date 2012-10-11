package com.javanetics.turbotransfer;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class FileUploadThread extends Thread
{
	protected DefaultHttpClient client;
	protected File file;
	protected HttpPutTransferListener transferListener;
	protected String targetAddress;
	
	public FileUploadThread(DefaultHttpClient client, File f, String targetAddress)
	{
		super();
		this.client = client;
		this.file = f;
		this.targetAddress = targetAddress;
	}

	public HttpPutTransferListener getTransferListener()
	{
		return transferListener;
	}

	public void setTransferListener(HttpPutTransferListener transferListener)
	{
		this.transferListener = transferListener;
	}

	@Override
	public void run()
	{
		try
		{
	    String serverResponse = null;
	    HttpPut put = new HttpPut(targetAddress);

	    FileEntityWithListener fileEntity = new FileEntityWithListener(this.file, "binary/octet-stream");
	    fileEntity.setListener(getTransferListener());
	    put.setEntity(fileEntity);

	    HttpResponse response;
			response = client.execute(put);
	    HttpEntity entity = response.getEntity();
	    if (entity != null)
	    {
	      serverResponse = EntityUtils.toString(entity);
	      System.out.println(serverResponse);
	    }
		}
		catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
