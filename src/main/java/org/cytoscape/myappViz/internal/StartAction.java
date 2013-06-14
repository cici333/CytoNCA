package org.cytoscape.myappViz.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;


public class StartAction extends AbstractCyAction {

	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelWest;
	private CyApplicationManager cyApplicationManagerServiceRef;
	private MainPanel mainPanel;
	
	public StartAction(CySwingApplication desktopApp,CyApplicationManager cyApplicationManagerServiceRef,
			MainPanel mainPanel){
		// Add a menu item -- Apps->sample02
		super("start");
		setPreferredMenu("Apps.clusterviz");

		this.desktopApp = desktopApp;
		this.cyApplicationManagerServiceRef=cyApplicationManagerServiceRef;
		//Note: myCytoPanel is bean we defined and registered as a service
		this.cytoPanelWest = this.desktopApp.getCytoPanel(CytoPanelName.WEST);
		this.mainPanel = mainPanel;
		mainPanel.setCyApplicationManagerServiceRef(cyApplicationManagerServiceRef);
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		
		// If the state of the cytoPanelWest is HIDE, show it
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		// Select my panel
		int index = cytoPanelWest.indexOfComponent(mainPanel);
		if (index == -1) {
			return;
		}
		cytoPanelWest.setSelectedIndex(index);
	
}

}