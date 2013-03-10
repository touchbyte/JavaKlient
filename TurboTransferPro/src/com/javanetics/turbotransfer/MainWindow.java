package com.javanetics.turbotransfer;

import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

public class MainWindow extends JFrame implements HttpPutTransferListener {

	public MainWindow() throws HeadlessException {
		try {
			// Check if Nimbus is supported and get its classname
			for (UIManager.LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(lafInfo.getName())) {
					UIManager.setLookAndFeel(lafInfo.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			try {
				// If Nimbus is not available, set to the default Java (metal) look and feel
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		Container content = getContentPane();
		content.setLayout(new GridLayout(1, 1));
		
		JLabel ddlabel = new JLabel(Localizer.sharedLocalizer().localizedString("DDHere"), SwingConstants.CENTER);
		content.add(ddlabel);

		new FileDrop( content, new FileDrop.Listener() {
			public void filesDropped( java.io.File[] files )
		 {
				ArrayList<File> mediafiles = new ArrayList<File>();
				for (int i = 0; i < files.length; i++)
				{
					try
					{
						ImageFormat format = Sanselan.guessFormat(files[i]); 
						if (null != format && !format.IMAGE_FORMAT_UNKNOWN.equals(format))
						{
							mediafiles.add(files[i]);
						}
					}
					catch (ImageReadException e)
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
				if (mediafiles.size() > 0)
				{
					Browser.sharedBrowser().setFilesToTransfer(mediafiles);
					Browser.sharedBrowser().setVisible(true);
				}
		 }
		 });
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(100, 100);
		setSize(600, 400);

		this.setVisible(true);

		// A menu-bar contains menus. A menu contains menu-items (or sub-Menu)
		JMenuBar menuBar;   // the menu-bar
		JMenu menu;         // each menu in the menu-bar
		JMenuItem menuItem; // an item in a menu

		menuBar = new JMenuBar();

		// First Menu
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);  // alt short-cut key
		menuBar.add(menu);  // the menu-bar adds this menu

		menuItem = new JMenuItem("Send Photos/Videos...", KeyEvent.VK_S);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//display.setText(count + "");
			}
		});

		menu.addSeparator();

		menuItem = new JMenuItem("Preferences...", KeyEvent.VK_P);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Preferences.sharedPreferences().setVisible(true);
			}
		});

		menu.addSeparator();

		menuItem = new JMenuItem("Quick Start Help...", KeyEvent.VK_H);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//display.setText(count + "");
			}
		});

		menuItem = new JMenuItem("Check for Updates...", KeyEvent.VK_U);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//display.setText(count + "");
			}
		});


		menuItem = new JMenuItem("About...", KeyEvent.VK_A);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//display.setText(count + "");
			
			}
		});

		menu.addSeparator();

		menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		menu.add(menuItem); // the menu adds this item
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//display.setText(count + "");
			}
		});

		setJMenuBar(menuBar);  // "this" JFrame sets its menu-bar
	}

	public MainWindow(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MainWindow();
	}

	@Override
	public void onTransferFinished()
	{
		// TODO Auto-generated method stub
		System.out.println("Transfer finished");
	}

	@Override
	public void onTransfer(long transferredSize, long totalSize)
	{
		// TODO Auto-generated method stub
		System.out.println("Transferred " + transferredSize + " of " + totalSize);
	}

	@Override
	public void onStartTransferFinished()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTransferFinished()
	{
		// TODO Auto-generated method stub
		
	}

}
