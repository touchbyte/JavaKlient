package com.javanetics.turbotransfer;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {
	private static Locale locale;
	private static ResourceBundle rb;
	private static Localizer me;
	
	private Localizer() {
		locale = new Locale("en", "US");
		rb = ResourceBundle.getBundle("properties/Strings", locale);
	}
	
	public static Localizer sharedLocalizer()
	{
		if (null == me)
		{
			me = new Localizer();
		}
		return me;
	}
	
	public String localizedString(String s)
	{
		return rb.getString(s);
	}

}
