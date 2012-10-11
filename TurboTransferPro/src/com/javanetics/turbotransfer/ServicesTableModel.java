package com.javanetics.turbotransfer;

import java.util.Enumeration;

import javax.jmdns.ServiceInfo;
import javax.swing.table.DefaultTableModel;

public class ServicesTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -1382792885120773976L;

	public ServicesTableModel(Object[][] data, String[] columnNames)
	{
		super(data, columnNames);
	}
	
	public ServiceInfo getServiceInfoAt(int row)
	{
		Object val = super.getValueAt(row, 1);
		if (null != val)
		{
			ServiceInfo[] infos = (ServiceInfo[])val;
			if (infos.length > 0)
			{
				ServiceInfo info = infos[0];
				return info;
			}
		}
		return null;
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		if (1 == column)
		{
			Object val = super.getValueAt(row, column);
			if (null != val)
			{
				ServiceInfo[] infos = (ServiceInfo[])val;
				if (infos.length > 0)
				{
					ServiceInfo info = infos[0];
					String system = info.getPropertyString(Prefs.sharedPrefs().getAppPrefs().get(Prefs.PREF_SERVICE_PROPERTY_SYSTEM, "system"));
					if (null != system)
					{
						return system;
					}
					else
					{
						return Localizer.sharedLocalizer().localizedString("Unknown");
					}
				}
				else return "";
			}
			else return "";
		}
		else
		{
			return super.getValueAt(row, column);
		}
	}
	
	
}
