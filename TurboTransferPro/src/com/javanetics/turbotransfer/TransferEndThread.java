package com.javanetics.turbotransfer;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TransferEndThread extends Thread
{
	protected DefaultHttpClient client;
	protected HttpPutTransferListener transferListener;
	protected String targetAddress;
	
	public TransferEndThread(DefaultHttpClient client, String targetAddress)
	{
		super();
		this.client = client;
		this.targetAddress = targetAddress + "stopTransfer";
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
	      	this.transferListener.onStopTransferFinished();
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
