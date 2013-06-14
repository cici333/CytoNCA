/*package org.cytoscape.myappViz.internal;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.model.CyNetworkViewManager;

public class MCODEAboutAction extends AbstractVizAction
{
	  private static final long serialVersionUID = -8445425993916988045L;
	  private final OpenBrowser openBrowser;
	  private final ClusterUtil mcodeUtil;
//	  private MCODEAboutDialog aboutDialog;

	  public MCODEAboutAction(String name, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, OpenBrowser openBrowser, ClusterUtil mcodeUtil)
	  {
	    super(name, applicationManager, swingApplication, netViewManager, "always");
	    this.openBrowser = openBrowser;
	    this.mcodeUtil = mcodeUtil;
	    setPreferredMenu("Apps.MCODE");
	  }

	  public void actionPerformed(ActionEvent e)
	  {
/*	    synchronized (this) {
	      if (this.aboutDialog == null) {
	        this.aboutDialog = new MCODEAboutDialog(this.swingApplication, this.openBrowser, this.mcodeUtil);
	      }

	      if (!this.aboutDialog.isVisible()) {
	        this.aboutDialog.setLocationRelativeTo(null);
	        this.aboutDialog.setVisible(true);
	      }
	    }

	    this.aboutDialog.toFront();*/
//	  }
//}
