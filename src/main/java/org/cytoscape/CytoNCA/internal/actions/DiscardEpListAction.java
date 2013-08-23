package org.cytoscape.CytoNCA.internal.actions;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;


import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.EpListPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;

// Referenced classes of package org.cytoscape.mcode.internal:
//			AbstractMCODEAction

public class DiscardEpListAction extends AbstractPAction
{

	private static final long serialVersionUID = 0x43a98ed598054cbL;
	public static final String REQUEST_USER_CONFIRMATION_COMMAND = "requestUserConfirmation";
	private final int EplistId;
	private final CyServiceRegistrar registrar;
	private final ProteinUtil pUtil;

	public DiscardEpListAction(String name, int EplistId, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, ProteinUtil pUtil)
	{
		super(name, applicationManager, swingApplication, netViewManager, "always");
		this.EplistId = EplistId;
		this.registrar = registrar;
		this.pUtil = pUtil;
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		EpListPanel panel = pUtil.getEpListPanel(this.EplistId);
		if (panel != null)
		{
			int eplistId = panel.getEplistId();
			Integer confirmed = Integer.valueOf(0);
			boolean requestUserConfirmation = Boolean.valueOf(getValue("requestUserConfirmation").toString()).booleanValue();
			if (requestUserConfirmation)
			{
				String message = (new StringBuilder("You are about to dispose of Essential Protein List")).append(EplistId).append(".\nDo you wish to continue?").toString();
				confirmed = Integer.valueOf(JOptionPane.showOptionDialog(swingApplication.getJFrame(), ((Object) (new Object[] {
					message
				})), "Confirm", 0, 3, null, null, null));
			}
			if (confirmed.intValue() == 0)
			{						
					
				panel.getBrowserPanel().getTable().clearSelection();
				registrar.unregisterService(panel, CytoPanelComponent.class);
				registrar.unregisterService(panel, SetCurrentNetworkListener.class);
				pUtil.removeNetworkEplist(EplistId);	
			}
		}
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		if (cytoPanel.getCytoPanelComponentCount() == 0)
			cytoPanel.setState(CytoPanelState.HIDE);
		if (pUtil.getEpListPanels().size() == 0){
			pUtil.resetEplists();
	
			}
	}
}
