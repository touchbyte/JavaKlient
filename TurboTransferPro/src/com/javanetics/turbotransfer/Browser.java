// Licensed under Apache License version 2.0
// Original license LGPL

//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.javanetics.turbotransfer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.Response;

import org.jdesktop.swingx.JXBusyLabel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * User Interface for browsing JmDNS services.
 * 
 * @author Arthur van Hoff, Werner Randelshofer
 */
public class Browser extends JFrame implements ServiceListener,
		ServiceTypeListener, ListSelectionListener, ItemListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5750114542524415107L;
	private static Browser browser;
	JmmDNS jmmdns;
	ArrayList<File> filesToTransfer;
	ServicesTableModel servicesTableModel;
	ArrayList<TargetAlbum> albums;

	// Vector headers;
	DefaultListModel services;
	JTable table;
	JTextArea info;
	JXBusyLabel busyIndicator;
	JLabel devicesFound;
	JComboBox serviceAlbums;
	JLabel serviceAlbumsTitle;
	TargetAlbum selectedAlbum;
	JButton sendButton;
	int count = 0;
	private ServiceInfo selectedService;
	private ServiceInfo transferService;
	private int transferCount;
	
	private Browser() throws IOException
	{
		super(Localizer.sharedLocalizer().localizedString("SelectDevice"));
		this.jmmdns = JmmDNS.Factory.getInstance();
		albums = new ArrayList<TargetAlbum>();
		Color bg = new Color(230, 230, 230);
		EmptyBorder border = new EmptyBorder(5, 5, 5, 5);
		Container content = getContentPane();
				
		FormLayout layout = new FormLayout("80px, 8px, 40px, 8px, 160px, 20px, 100px, 10px, 70px, right:30px", // columns
				"top:15px, top:80px, 8px, p, 8px, p, 8px, p, 14px, p"); // rows
		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		JLabel picLabel = new JLabel(createImageIcon("/images/browsericon.png"));
		builder.add(picLabel, cc.xywh(1, 1, 1, 2));
		JLabel header = new JLabel();
		header.setFont(new Font("Helvetica", Font.BOLD, 13));
		header.setText(Localizer.sharedLocalizer().localizedString(
				"SelectTheDeviceHeader"));
		builder.add(header, cc.xyw(3, 1, 4));
		JLabel instructions = new JLabel();
		instructions.setFont(new Font("Helvetica", Font.PLAIN, 13));
		instructions.setText("<html>"+Localizer.sharedLocalizer().localizedString(
				"SelectTheDevice")+"</html>");
		builder.add(
				instructions,
				cc.xyw(3, 2, 8));
		
		builder.addLabel(
				Localizer.sharedLocalizer().localizedString("SearchForDevices"),
				cc.xyw(1, 4, 3));
		JComboBox modeSwitch = new JComboBox();
		String[] switchActions = {
				Localizer.sharedLocalizer().localizedString("Automatic"),
				Localizer.sharedLocalizer().localizedString("Manual") };
		for (int i = 0; i < switchActions.length; i++)
			modeSwitch.addItem(switchActions[i]);
		builder.add(modeSwitch, cc.xy(5, 4));

		String[] columnNames = {
				Localizer.sharedLocalizer().localizedString("Name"),
				Localizer.sharedLocalizer().localizedString("Type") };
		Object[][] data = new Object[][] {};
		servicesTableModel = new ServicesTableModel(data, columnNames);
		table = new JTable(servicesTableModel);
		table.setPreferredScrollableViewportSize(new Dimension(500, 150));
		table.setBackground(Color.WHITE);
		table.getSelectionModel().addListSelectionListener(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.getViewport().setBackground(Color.WHITE);
		builder.add(scrollPane, cc.xyw(1, 6, 10));

		devicesFound = new JLabel();
		devicesFound.setFont(new Font("Helvetica", Font.PLAIN, 12));
		devicesFound.setText("");
	  builder.add(devicesFound, cc.xyw(1, 8, 5));

	  busyIndicator = new JXBusyLabel();
	  busyIndicator.setToolTipText(Localizer.sharedLocalizer().localizedString("SearchingForDevices"));
	  busyIndicator.setBusy(true);
	  builder.add(busyIndicator, cc.xy(10, 8));
		
	  serviceAlbumsTitle = new JLabel();
	  serviceAlbumsTitle.setText(Localizer.sharedLocalizer().localizedString("Album") + ":");
		builder.add(serviceAlbumsTitle, cc.xy(1, 10));
	  
	  serviceAlbums = new JComboBox();
	  serviceAlbums.addItemListener(this);
		builder.add(serviceAlbums, cc.xyw(3, 10, 3));

  	serviceAlbums.setVisible(false);
  	serviceAlbumsTitle.setVisible(false);

		JButton cancelButton = new JButton();
		cancelButton.setText(Localizer.sharedLocalizer().localizedString("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
      	closeWindow();
      }
		});      
		builder.add(cancelButton, cc.xy(7, 10));
		sendButton = new JButton();
		sendButton.setText(Localizer.sharedLocalizer().localizedString("Send"));
		sendButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
      	startSending();
      }
		});      
		sendButton.setEnabled(false);
		builder.add(sendButton, cc.xyw(9, 10, 2));

		content.add(builder.getPanel());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(100, 100);
		// setSize(600, 400);

		// this.jmmdns.addServiceTypeListener(this);
		this.jmmdns.addServiceListener(
				Prefs.sharedPrefs().getAppPrefs().get(Prefs.PREF_SERVICE_TYPE, ""),
				this);

		/*
		 * // register some well known types // String list[] = new String[] {
		 * "_http._tcp.local.", "_ftp._tcp.local.", "_tftp._tcp.local.",
		 * "_ssh._tcp.local.", "_smb._tcp.local.", "_printer._tcp.local.",
		 * "_airport._tcp.local.", "_afpovertcp._tcp.local.", "_ichat._tcp.local.",
		 * // "_eppc._tcp.local.", "_presence._tcp.local.", "_rfb._tcp.local.",
		 * "_daap._tcp.local.", "_touchcs._tcp.local." }; String[] list = new
		 * String[] { "_photosync._tcp.local." };
		 * 
		 * for (int i = 0; i < list.length; i++) {
		 * this.jmmdns.registerServiceType(list[i]); }
		 */
	}
	
	protected void closeWindow()
	{
  	WindowEvent closingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
  	Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
	}

	protected static ImageIcon createImageIcon(String path) 
	{
		java.net.URL imgURL = Preferences.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Browser sharedBrowser()
	{
		if (null == browser)
		{
			try
			{
				browser = new Browser();
				browser.pack();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return browser;
	}

	public ArrayList<File> getFilesToTransfer()
	{
		return filesToTransfer;
	}

	public void setFilesToTransfer(ArrayList<File> filesToTransfer)
	{
		this.filesToTransfer = filesToTransfer;
		for (int i = 0; i < this.filesToTransfer.size(); i++)
		{
			System.out.println(this.filesToTransfer.get(i).getAbsolutePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceAdded(ServiceEvent event)
	{
		final String name = event.getName();
		System.out.println("ADD: " + name);
		if (null != name)
		{
			ServiceInfo[] serviceInfos = this.jmmdns.getServiceInfos(Prefs
					.sharedPrefs().getAppPrefs().get(Prefs.PREF_SERVICE_TYPE, ""), name);
			// This is actually redundant. getServiceInfo will force the resolution of
			// the service and call serviceResolved
			//this.dislayInfo(serviceInfos);
		}
		/*
		 * 
		 * 
		 * System.out.println("ADD: " + name); SwingUtilities.invokeLater(new
		 * Runnable() {
		 * 
		 * @Override public void run() { insertSorted(services, name); } });
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceRemoved(ServiceEvent event)
	{
		final String name = event.getName();

		System.out.println("REMOVE: " + name);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				removeRowWithValue(servicesTableModel, name);
			}
		});
	}

	@Override
	public void serviceResolved(ServiceEvent event)
	{
		final String name = event.getName();

		System.out.println("RESOLVED: " + name);
		final ServiceInfo[] serviceInfos = this.jmmdns.getServiceInfos(Prefs
				.sharedPrefs().getAppPrefs().get(Prefs.PREF_SERVICE_TYPE, ""), name);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				insertSorted(servicesTableModel, name, serviceInfos);
			}
		});
		/*
		 * if (name.equals(serviceList.getSelectedValue())) { ServiceInfo[]
		 * serviceInfos =
		 * this.jmmdns.getServiceInfos(Prefs.sharedPrefs().getAppPrefs
		 * ().get(Prefs.PREF_SERVICE_TYPE, ""), name);
		 * this.dislayInfo(serviceInfos); // this.dislayInfo(new ServiceInfo[] {
		 * event.getInfo() }); }
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.jmdns.ServiceTypeListener#serviceTypeAdded(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceTypeAdded(ServiceEvent event)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.jmdns.ServiceTypeListener#subTypeForServiceTypeAdded(javax.jmdns.
	 * ServiceEvent)
	 */
	@Override
	public void subTypeForServiceTypeAdded(ServiceEvent event)
	{
		System.out.println("SUBTYPE: " + event.getType());
	}

	protected boolean isMobile(String system)
	{
		return !isComputer(system);
	}

	protected boolean isComputer(String system)
	{
		if (system.toLowerCase().equals("mac")
				|| system.toLowerCase().equals("windows"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	void insertSorted(ServicesTableModel model, String value, ServiceInfo[] info)
	{
		if (info.length > 0)
		{
			ServiceInfo si = info[0];
			String system = si.getPropertyString(Prefs.sharedPrefs().getAppPrefs()
					.get(Prefs.PREF_SERVICE_PROPERTY_SYSTEM, "system"));
			if (true)
				/*
				((Prefs.sharedPrefs().getAppPrefs()
					.getBoolean(Prefs.PREF_MOBILES_ALLOWED, false) && isMobile(system))
					|| (Prefs.sharedPrefs().getAppPrefs()
							.getBoolean(Prefs.PREF_COMPUTERS_ALLOWED, false) && isComputer(system)))*/
			{
				for (int i = 0, n = model.getRowCount(); i < n; i++)
				{
					int result = value.compareToIgnoreCase((String) model
							.getValueAt(i, 0));
					if (result == 0)
					{
						return;
					}
					if (result < 0)
					{
						model.insertRow(i, new Object[] { value, info });
						return;
					}
				}
				model.addRow(new Object[] { value, info });
			}
		}
		updateDevicesFoundText();
	}

	void removeRowWithValue(ServicesTableModel model, String value)
	{
		for (int i = 0, n = model.getRowCount(); i < n; i++)
		{
			int result = value.compareToIgnoreCase((String) model.getValueAt(i, 0));
			if (result == 0)
			{
				model.removeRow(i);
				updateDevicesFoundText();
				table.clearSelection();
				setSelectedService(null);
				break;
			}
		}
	}

	protected void updateDevicesFoundText()
	{
		if (table.getModel().getRowCount() > 1)
		{
			devicesFound.setText(String.format(Localizer.sharedLocalizer().localizedString("NDevicesFound"), table.getModel().getRowCount()));
		}
		else if (table.getModel().getRowCount() == 1)
		{
			devicesFound.setText(Localizer.sharedLocalizer().localizedString("OneDeviceFound"));
		}
		else
		{
			devicesFound.setText("");
		}
	}
	
	@Override
	public String toString()
	{
		return "RVBROWSER";
	}
	
	protected void setSelectedService(ServiceInfo service)
	{
		selectedService = service;
		if (null != selectedService)
		{
    	String address = HttpUtils.sharedHttpUtils().getRequestRoot(service);
    	address += Prefs.PATH_ALBUMLIST;
    	HttpClient client = new HttpClient();
    	//client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
    	try
			{
    		albums.clear();
    		albums.add(new TargetAlbumLoadingIndicator());
    		setServiceAlbums();
      	serviceAlbums.setVisible(true);
      	serviceAlbumsTitle.setVisible(true);
    		client.start();
	    	//AlbumListExchange exchange = new AlbumListExchange(false);
	      ContentExchange exchange = new ContentExchange(false)
	      {
	          protected void onResponseComplete() throws IOException
	          {
	              int status = getResponseStatus();
	              System.out.println("Status = " + status);
              	System.out.println(getResponseContent());
	              if (status == 200 && getResponseContent().startsWith("{"))
	              {
	              	if (selectedService.getServer().equals(getAddress().getHost())) // only if the selected service is still the one which initiated this request
	              	{
		              	JSONTokener tokener = new JSONTokener(getResponseContent());
		              	try
										{
		              		JSONObject obj = new JSONObject(tokener);
		              		if (obj.get("mode") != null)
		              		{
		              			if (obj.get("mode").equals("folder"))
		              			{
													JSONArray arr = (JSONArray)(obj.get("folders"));
													if (arr.length() > 0)
													{
														for (int i = 0; i < arr.length(); i++)
														{
															JSONObject folder = arr.getJSONObject(i);
															albums.add(new TargetAlbumFolder(folder.getString("FolderUuid"), folder.getBoolean("FolderActive"),folder.getString("FolderTitle"),folder.getString("FolderPath")));
														}
													}
		              			}
		              			else if (obj.get("mode").equals("iphoto"))
		              			{
													JSONArray arr = (JSONArray)(obj.get("folders"));
													if (arr.length() > 0)
													{
														for (int i = 0; i < arr.length(); i++)
														{
															JSONObject iphoto = arr.getJSONObject(i);
															albums.add(new TargetAlbumiPhoto(iphoto.getString("FolderUuid"), iphoto.getBoolean("FolderActive"),iphoto.getString("FolderTitle"),iphoto.getString("FolderType")));
														}
													}
		              			}
		              			else if (obj.get("mode").equals("aperture"))
		              			{
													JSONArray arr = (JSONArray)(obj.get("folders"));
													if (arr.length() > 0)
													{
														for (int i = 0; i < arr.length(); i++)
														{
															JSONObject aperture = arr.getJSONObject(i);
															albums.add(new TargetAlbumAperture(aperture.getString("FolderUuid"), aperture.getBoolean("FolderActive"),aperture.getString("FolderTitle"),aperture.getString("FolderType")));
														}
													}
		              			}
		              		}
										}
										catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		              	albums.remove(0);
		            		setServiceAlbums();
	              	}
	              	else
	              	{
	              		System.out.println("server " + selectedService.getServer() + " was not equal to " + getAddress().getHost());
	              		return;
	              	}
	              }
	              else if (status == 200 && getResponseContent().startsWith("["))
	              {
	              	if (selectedService.getServer().equals(getAddress().getHost())) // only if the selected service is still the one which initiated this request
	              	{
		              	JSONTokener tokener = new JSONTokener(getResponseContent());
		              	try
										{
											JSONArray arr = new JSONArray(tokener);
											if (arr.length() > 0)
											{
												for (int i = 0; i < arr.length(); i++)
												{
													JSONObject obj = arr.getJSONObject(i);
													albums.add(new TargetAlbumiOS(obj.getString("id"), obj.getString("name"), obj.getInt("roll")));
												}
											}
										}
										catch (JSONException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		              	albums.remove(0);
		            		setServiceAlbums();
	              	}
	              }
	              else
	              {
	              	serviceAlbums.setVisible(false);
	              	serviceAlbumsTitle.setVisible(false);
	              }
	          }
	      };

				String system = service.getPropertyString(Prefs.sharedPrefs().getAppPrefs()
						.get(Prefs.PREF_SERVICE_PROPERTY_SYSTEM, "system"));
	      exchange.setURL(address);
				if (isComputer(system))
				{
					exchange.setMethod("GET");
		      System.out.println("GET " + address.toString());
				}
				else
				{
					exchange.setMethod("PUT");
		    	exchange.setRequestContent(new ByteArrayBuffer("albumList".getBytes()));
		    	exchange.setRequestContentType("application/json");
		      System.out.println("PUT " + address.toString());
				}
	      client.send(exchange);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			int row = table.getSelectedRow();
			System.out.println("Value changed " + row + ": " + e.toString());
			if (row > -1)
			{
				setSelectedService(((ServicesTableModel) table.getModel()).getServiceInfoAt(row));
			}
			else
			{
				setSelectedService(null);
	    	serviceAlbums.setVisible(false);
	    	serviceAlbumsTitle.setVisible(false);
			}
		}
	}
	
	protected void setServiceAlbums()
	{
		serviceAlbums.removeAllItems();
		for (int i = 0; i < albums.size(); i++)
		{
			TargetAlbum album = albums.get(i);
			serviceAlbums.addItem(album);
			if (album.isFolderActive()) serviceAlbums.setSelectedIndex(i);
		}
		if (0 == albums.size())
		{
    	serviceAlbums.setVisible(false);
    	serviceAlbumsTitle.setVisible(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
    if (arg0.getStateChange() == ItemEvent.SELECTED) {
      selectedAlbum = (TargetAlbum)serviceAlbums.getSelectedItem();
  		sendButton.setEnabled(true);
    }
    else
    {
    	selectedAlbum = null;
  		sendButton.setEnabled(false);
    }
	}
	
	protected void startSending()
	{
		closeWindow();
		TransferWindow transfer = new TransferWindow(selectedService.getName(), selectedService, selectedAlbum, filesToTransfer);
	}
}