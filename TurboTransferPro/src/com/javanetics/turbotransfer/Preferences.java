package com.javanetics.turbotransfer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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

		tabbedPane.addTab("", createImageIcon("/images/Send.png"), panelSend(), Localizer.sharedLocalizer().localizedString("Send"));
		tabbedPane.setSize(400, 400);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		content.add(tabbedPane);
	}
	
	protected JPanel panelSend()
	{
		FormLayout layout = new FormLayout(
			    "right:pref, 10px, pref", 
			    "p, 10px, p");      
			PanelBuilder builder = new PanelBuilder(layout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();

		// Fill the grid with components; the builder can create
		// frequently used components, e.g. separators and labels.

		// Add a titled separator to cell (1, 1) that spans 7 columns.
		builder.addLabel(Localizer.sharedLocalizer().localizedString("SortBy")+":",       cc.xy (1,  1));
		JComboBox sort = new JComboBox();
		String[] description = { 
				Localizer.sharedLocalizer().localizedString("SortNameAsc"), 
				Localizer.sharedLocalizer().localizedString("SortNameDesc"), 
				Localizer.sharedLocalizer().localizedString("SortDateAsc"), 
				Localizer.sharedLocalizer().localizedString("SortDateDesc") };
	    for (int i = 0; i < description.length; i++)
	        sort.addItem(description[i]);

		builder.add(sort,         cc.xy(3,  1));
		builder.addLabel(Localizer.sharedLocalizer().localizedString("ResizeImagesTo")+":",  	     cc.xy (1,  3));
		JComboBox resize = new JComboBox();
		String[] sizes = { 
				Localizer.sharedLocalizer().localizedString("SizeFull"), 
				Localizer.sharedLocalizer().localizedString("Size1920"), 
				Localizer.sharedLocalizer().localizedString("Size1280"), 
				Localizer.sharedLocalizer().localizedString("Size1024"),
				Localizer.sharedLocalizer().localizedString("Size800") };
	    for (int i = 0; i < sizes.length; i++)
	    	resize.addItem(sizes[i]);
		builder.add(resize,         cc.xy(3,  3));

		// The builder holds the layout container that we now return.
		return builder.getPanel();
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
