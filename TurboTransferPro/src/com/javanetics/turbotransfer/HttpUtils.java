package com.javanetics.turbotransfer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jmdns.ServiceInfo;

public class HttpUtils
{
	private static HttpUtils utils;

	private HttpUtils()
	{
		// TODO Auto-generated constructor stub
	}
	
	public static HttpUtils sharedHttpUtils()
	{
		if (null == utils)
		{
			utils = new HttpUtils();
		}
		return utils;
	}

	public String getRequestRoot(ServiceInfo service)
	{
		return "http://" + service.getServer() + ":" + String.format("%d", service.getPort()) + "/";
//		return "http://" + service.getInet4Addresses()[0].getHostAddress() + ":" + String.format("%d", service.getPort()) + "/";
	}	
}
