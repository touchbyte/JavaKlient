package com.javanetics.turbotransfer;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TransferFileUploadThread extends Thread
{
	protected DefaultHttpClient client;
	protected File file;
	protected HttpPutTransferListener transferListener;
	protected String targetAddress;
	protected String session;
	protected int fileCount;
	protected int fileTotal;
	
	public TransferFileUploadThread(DefaultHttpClient client, File f, String targetAddress, int count, int total, String uuid)
	{
		super();
		this.client = client;
		this.file = f;
		this.targetAddress = targetAddress + f.getName();
		this.fileCount = count;
		this.fileTotal = total;
		this.session = uuid;
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

	    put.addHeader("X-Session", this.session);
	    put.addHeader("X-PhotoSync-Filecount", new Integer(this.fileTotal).toString());
	    put.addHeader("X-PhotoSync-Currenttransfer", new Integer(this.fileCount).toString());
	    put.addHeader("X-Filename", this.file.getName());
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
	      if (null != this.transferListener)
	      {
	      	this.transferListener.onTransferFinished();
	      }
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
