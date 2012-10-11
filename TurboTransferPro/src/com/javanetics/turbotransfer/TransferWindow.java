package com.javanetics.turbotransfer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TransferWindow extends JFrame
{
	protected JLabel transferImage;
	protected JLabel transferProgress;
	protected JLabel fileProgress;
	protected JProgressBar progressBar;
	private ServiceInfo transferService;
	ArrayList<File> filesToTransfer;
	private int numberOfFiles;
	private int actualFile;
	protected DefaultHttpClient client;

	public TransferWindow(String devicename, ServiceInfo service, ArrayList<File> files) throws HeadlessException
	{
		super(devicename);
		transferService = service;
		filesToTransfer = files;
		Color bg = new Color(230, 230, 230);
		EmptyBorder border = new EmptyBorder(5, 5, 5, 5);
		Container content = getContentPane();
				
		FormLayout layout = new FormLayout("center:260px", // columns
				"25px, 90px, 15px, 25px, 10px, 15px, 10px, 15px, 25px, 25px"); // rows
		PanelBuilder builder = new PanelBuilder(layout);
		builder.border(Borders.DIALOG);
		CellConstraints cc = new CellConstraints();
		transferImage = new JLabel(createImageIcon("/images/browsericon.png"));
		builder.add(transferImage, cc.xy(1, 2));

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		builder.add(progressBar, cc.xy(1, 4));
		
		transferProgress = new JLabel();
		transferProgress.setText("†bertrage 1 von 5");
		builder.add(transferProgress, cc.xy(1, 6));

		fileProgress = new JLabel();
		fileProgress.setText("Datei Fortschritt: 72.0 %");
		builder.add(fileProgress, cc.xy(1, 8));

		JButton cancelButton = new JButton();
		cancelButton.setText(Localizer.sharedLocalizer().localizedString("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
      	closeWindow();
      }
		});      
		builder.add(cancelButton, cc.xy(1, 10));

		content.add(builder.getPanel());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		startTransfer();
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
	
	protected void startTransfer()
	{
		numberOfFiles = filesToTransfer.size();
		actualFile = 1;
    HttpParams params = new BasicHttpParams();
    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    client = new DefaultHttpClient(params);
		if (filesToTransfer.size() > 0)
		{
			File f = filesToTransfer.get(0);
			filesToTransfer.remove(0);
	  	String address = HttpUtils.sharedHttpUtils().getRequestRoot(transferService);
	  	address += f.getName();
			try
			{
				FileUploadThread thread = new FileUploadThread(client, f, address);
				thread.run();
				thread.join();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void stopTransfer()
	{
		
	}
	
	protected void transferFile()
	{
	}
}
