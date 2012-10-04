package com.javanetics.turbotransfer;

import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PreferencesSend extends JComponent {

	public PreferencesSend() {
		FormLayout layout = new FormLayout(
		    "right:pref, 3dlu, pref", 
		    "p, 3dlu, p");      
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

	// Fill the grid with components; the builder can create
	// frequently used components, e.g. separators and labels.

	// Add a titled separator to cell (1, 1) that spans 7 columns.
	builder.addLabel("Sort by:",       cc.xy (1,  1));
//	builder.add(companyField,         cc.xy(2,  1));
	builder.addLabel("Resize images to:",  	     cc.xy (1,  2));
//	builder.add(contactField,         cc.xy(2,  2));

	// The builder holds the layout container that we now return.
	builder.getPanel();
		
	}

}
