package org.cytoscape.CytoNCA.internal.actions;

import javax.swing.JOptionPane;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.EvaluationPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;


public class DiscardEvaluationAction extends AbstractPAction
{

	private static final long serialVersionUID = 0x43a98ed598054cbL;
	public static final String REQUEST_USER_CONFIRMATION_COMMAND = "requestUserConfirmation";
	private final int evaluationId;
	private final CyServiceRegistrar registrar;
	private final ProteinUtil pUtil;

	public DiscardEvaluationAction(String name, int evaluationId, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, ProteinUtil pUtil)
	{
		super(name, applicationManager, swingApplication, netViewManager, "always");
		this.evaluationId = evaluationId;
		this.registrar = registrar;
		this.pUtil = pUtil;
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		EvaluationPanel panel = pUtil.getEvaluationPanel(this.evaluationId);
		if (panel != null)
		{
			int evaluationId = panel.getEvaluationId();
			Integer confirmed = Integer.valueOf(0);
			boolean requestUserConfirmation = Boolean.valueOf(getValue("requestUserConfirmation").toString()).booleanValue();
			if (requestUserConfirmation)
			{
				String message = (new StringBuilder("You are about to dispose of Evaluation ")).append(evaluationId).append(".\nDo you wish to continue?").toString();
				confirmed = Integer.valueOf(JOptionPane.showOptionDialog(swingApplication.getJFrame(), ((Object) (new Object[] {
					message
				})), "Confirm", 0, 3, null, null, null));
			}
			if (confirmed.intValue() == 0)
			{
			 
				panel.setVisible(false);
				
			//	registrar.unregisterService(panel, CytoPanelComponent.class);
		//		pUtil.removeNetworkResult(resultId);
				
			
			}
		}
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		if (cytoPanel.getCytoPanelComponentCount() == 0)
			cytoPanel.setState(CytoPanelState.HIDE);
	/*	if (getResultPanels().size() == 0)
			pUtil.reset();
			*/
	}
}
