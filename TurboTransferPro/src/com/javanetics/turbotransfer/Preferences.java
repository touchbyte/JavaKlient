package com.javanetics.turbotransfer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class Preferences extends JFrame {
	private static Preferences prefs;

	private Preferences() throws HeadlessException {
		// TODO Auto-generated constructor stub
		initComponents();
	}

	private Preferences(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		initComponents();
	}

	private Preferences(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
		initComponents();
	}

	private Preferences(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
		initComponents();
	}

	public static Preferences sharedPreferences()
	{
		if (null == prefs)
		{
			prefs =  new Preferences(Localizer.sharedLocalizer().localizedString("Preferences"));
		}
		prefs.pack();
		return prefs;
	}

	protected void initComponents()
	{
		Container content = getContentPane();
		content.setLayout(new GridLayout(1, 1));

		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = makeTextPanel("Panel #1");
		tabbedPane.addTab("", createImageIcon("/images/General.png"), panel1, Localizer.sharedLocalizer().localizedString("General"));
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = makeTextPanel("Panel #2");
		tabbedPane.addTab("", createImageIcon("/images/Receive.png"), panel2, Localizer.sharedLocalizer().localizedString("Receive"));
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent panel3 = makeTextPanel("Panel #3");
		tabbedPane.addTab("", createImageIcon("/images/Send.png"), panel3, Localizer.sharedLocalizer().localizedString("Send"));
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		content.add(tabbedPane);
	}

	protected JComponent makeTextPanel(String text) 
	{
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
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
}
