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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.UUID;

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

public class TransferWindow extends JFrame implements HttpPutTransferListener
{
	protected JLabel transferImage;
	protected JLabel transferProgress;
	protected JLabel fileProgress;
	protected JProgressBar progressBar;
	private ServiceInfo transferService;
	private TargetAlbum targetAlbum;
	private double percentage;
	ArrayList<File> filesToTransfer;
	private int numberOfFiles;
	private int actualFile;
	private String session;
	protected DefaultHttpClient client;

	public TransferWindow(String devicename, ServiceInfo service, TargetAlbum target, ArrayList<File> files) throws HeadlessException
	{
		super(devicename);
		transferService = service;
		filesToTransfer = files;
		targetAlbum = target;
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
		transferProgress.setText("");
		builder.add(transferProgress, cc.xy(1, 6));

		fileProgress = new JLabel();
		fileProgress.setText("");
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
    HttpParams params = new BasicHttpParams();
    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    client = new DefaultHttpClient(params);
		numberOfFiles = filesToTransfer.size();
		actualFile = 0;
		session = UUID.randomUUID().toString();
		if (filesToTransfer.size() > 0)
		{
	  	String address = HttpUtils.sharedHttpUtils().getRequestRoot(transferService);
			try
			{
				TransferStartThread thread = new TransferStartThread(client, address, Prefs.sharedPrefs().getAppPrefs().get(Prefs.PREF_SYSTEM_NAME, ""), targetAlbum.getFolderTitle(), targetAlbum.getFolderUuid(), filesToTransfer.size(), session);
				thread.setTransferListener(this);
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
    HttpParams params = new BasicHttpParams();
    params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
    client = new DefaultHttpClient(params);
  	String address = HttpUtils.sharedHttpUtils().getRequestRoot(transferService);
		try
		{
			TransferEndThread thread = new TransferEndThread(client, address);
			thread.run();
			thread.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void transferFile()
	{
		if (filesToTransfer.size() > 0)
		{
			actualFile++;
			transferProgress.setText("†bertrage " + actualFile + " von " + numberOfFiles);
			File f = filesToTransfer.get(0);
			filesToTransfer.remove(0);
		  String address = HttpUtils.sharedHttpUtils().getRequestRoot(transferService);
			try
			{
				TransferFileUploadThread thread = new TransferFileUploadThread(client, f, address, actualFile, numberOfFiles, session);
				thread.setTransferListener(this);
				thread.run();
				thread.join();
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			this.stopTransfer();
		}
	}

	@Override
	public void onTransferFinished()
	{
		System.out.println("next file");
		transferFile();
	}

	@Override
	public void onTransfer(long transferredSize, long totalSize)
	{
		this.percentage = (1.0*transferredSize)/(1.0*totalSize);
		System.out.println("File progress "+ NumberFormat.getPercentInstance().format(percentage) + " %");
	  SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	      // Here, we can safely update the GUI
	      // because we'll be called from the
	      // event dispatch thread
	    	fileProgress.setText("Datei Fortschritt: " + NumberFormat.getPercentInstance().format(percentage) + " %");
	    }
	  });
	}

	@Override
	public void onStartTransferFinished()
	{
		System.out.println("begin with first file");
		transferFile();
	}

	@Override
	public void onStopTransferFinished()
	{
		System.out.println("that's it. finished");
	}
}
