package com.javanetics.turbotransfer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

public class Prefs
{
	private static final String GENERAL_LOOK_NODE = "com/javanetics/turbotransfer/Preferences";
	public static final String PREF_SERVICE_TYPE = "ServiceType";
	public static final String PREF_SERVICE_PROPERTY_SYSTEM = "ServicePropertySystem";
	public static final String PREF_COMPUTERS_ALLOWED = "ComputersAllowed";
	public static final String PREF_MOBILES_ALLOWED = "MobilesAllowed";
	public static final String PREF_SYSTEM_NAME = "SystemName";
	public static final String PATH_ALBUMLIST = "albumList";
	private Preferences appPrefs;
	private static Prefs sharedPrefs;

	private Prefs()
	{
		appPrefs = Preferences.userRoot().node(GENERAL_LOOK_NODE);
		if (appPrefs.get(PREF_SERVICE_TYPE, "").length() == 0)
		{
			appPrefs.put(PREF_SERVICE_TYPE, "_photosync._tcp.local.");
			appPrefs.put(PREF_SERVICE_PROPERTY_SYSTEM, "system");
			appPrefs.putBoolean(PREF_COMPUTERS_ALLOWED, false);
			appPrefs.putBoolean(PREF_MOBILES_ALLOWED, true);
		}
		if (appPrefs.get(PREF_SYSTEM_NAME, "").length() == 0)
		{
			try
			{
				appPrefs.put(PREF_SYSTEM_NAME, InetAddress.getLocalHost().getHostName());
			}
			catch (UnknownHostException e)
			{
				appPrefs.put(PREF_SYSTEM_NAME, "PhotoSync");
			}
		}
	}
	
	public static Prefs sharedPrefs()
	{
		if (null == sharedPrefs)
		{
			sharedPrefs = new Prefs();
		}
		return sharedPrefs;
	}

	public Preferences getAppPrefs()
	{
		return appPrefs;
	}
	
}
