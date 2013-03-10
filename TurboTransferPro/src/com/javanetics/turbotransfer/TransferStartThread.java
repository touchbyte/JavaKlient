package com.javanetics.turbotransfer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TransferStartThread extends Thread
{
	protected DefaultHttpClient client;
	protected HttpPutTransferListener transferListener;
	protected String targetAddress;
	protected String deviceName;
	protected String albumName;
	protected String albumIdentifier;
	protected String session;
	protected int fileCount;
	
	public TransferStartThread(DefaultHttpClient client, String targetAddress, String device, String album, String albumID, int count, String uuid)
	{
		super();
		this.client = client;
		this.targetAddress = targetAddress + "startTransfer";
		this.deviceName = device;
		this.albumName = album;
		this.fileCount = count;
		this.session = uuid;
		this.albumIdentifier = albumID;
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
	    put.addHeader("Accept", "application/json");
	    put.addHeader("Content-Type", "application/json");
	    // target computer
	    put.addHeader("X-PhotoSync-Devicename", new String(Base64.encodeBase64(deviceName.getBytes())));
	    put.addHeader("X-PhotoSync-Albumname", new String(Base64.encodeBase64(albumName.getBytes())));
	    // end target computer
	    // target iOS
	    put.addHeader("X-PhotoSync-TargetalbumID", albumIdentifier);
	    put.addHeader("X-PhotoSync-TargetalbumName", new String(Base64.encodeBase64(albumName.getBytes())));
	    // end target iOS
	    put.addHeader("X-PhotoSync-Filecount", new Integer(fileCount).toString());
	    put.addHeader("X-Session", this.session);
	    put.addHeader("X-PhotoSync-Devicemodel", "iPad"); // enter iPad Simulator if this is an iPad Simulator, iPhone, etc.
	    put.addHeader("X-PhotoSync-Create-Subdirs", "true");
      StringEntity entity = new StringEntity("action", "UTF-8");
      entity.setContentType("application/json");
      put.setEntity(entity);

	    HttpResponse response;
			response = client.execute(put);
	    HttpEntity responseEntity = response.getEntity();
	    if (entity != null)
	    {
	      serverResponse = EntityUtils.toString(responseEntity);
	      System.out.println(serverResponse);
	      if (null != this.transferListener)
	      {
	      	this.transferListener.onStartTransferFinished();
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
