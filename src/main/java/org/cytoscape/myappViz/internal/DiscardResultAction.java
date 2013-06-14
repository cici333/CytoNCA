package org.cytoscape.myappViz.internal;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.myappViz.internal.Cluster;
import org.cytoscape.myappViz.internal.ClusterUtil;
import org.cytoscape.myappViz.internal.ResultPanel;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;

// Referenced classes of package org.cytoscape.mcode.internal:
//			AbstractMCODEAction

public class DiscardResultAction extends AbstractVizAction
{

	private static final long serialVersionUID = 0x43a98ed598054cbL;
	public static final String REQUEST_USER_CONFIRMATION_COMMAND = "requestUserConfirmation";
	private final int resultId;
	private final CyServiceRegistrar registrar;
	private final ClusterUtil mcodeUtil;

	public DiscardResultAction(String name, int resultId, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, ClusterUtil mcodeUtil)
	{
		super(name, applicationManager, swingApplication, netViewManager, "always");
		this.resultId = resultId;
		this.registrar = registrar;
		this.mcodeUtil = mcodeUtil;
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		ResultPanel panel = getResultPanel(this.resultId);
		if (panel != null)
		{
			int resultId = panel.getResultId();
			Integer confirmed = Integer.valueOf(0);
			boolean requestUserConfirmation = Boolean.valueOf(getValue("requestUserConfirmation").toString()).booleanValue();
			if (requestUserConfirmation)
			{
				String message = (new StringBuilder("You are about to dispose of Result ")).append(resultId).append(".\nDo you wish to continue?").toString();
				confirmed = Integer.valueOf(JOptionPane.showOptionDialog(swingApplication.getJFrame(), ((Object) (new Object[] {
					message
				})), "Confirm", 0, 3, null, null, null));
			}
			if (confirmed.intValue() == 0)
			{
				List clusters = panel.getClusters();
				registrar.unregisterService(panel, CytoPanelComponent.class);
				mcodeUtil.removeNetworkResult(resultId);
				if (clusters != null)
				{
					Cluster c;
					for (Iterator iterator = clusters.iterator(); iterator.hasNext(); c.dispose())
						c = (Cluster)iterator.next();

				}
			}
		}
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		if (cytoPanel.getCytoPanelComponentCount() == 0)
			cytoPanel.setState(CytoPanelState.HIDE);
		if (getResultPanels().size() == 0)
			mcodeUtil.reset();
	}
}
