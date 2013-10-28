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
import org.jfree.chart.ChartFrame;


public class DiscardEvaluationAction  extends AbstractPAction
{


	private final ProteinUtil pUtil;
	private final EvaluationPanel panel;

	public DiscardEvaluationAction(EvaluationPanel panel, ProteinUtil pUtil)
	{
		super("Discard Panel", pUtil.getApplicationMgr(), pUtil.getSwingApplication(), pUtil.getNetworkViewMgr(), "always");
		this.pUtil = pUtil;
		this.panel = panel;
		
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		
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
			 
				if(panel.chartfs !=null && !panel.chartfs.isEmpty())
					for(ChartFrame cf : panel.chartfs)
						if(cf != null)
							cf.dispose();			
				pUtil.getRegistrar().unregisterService(panel, CytoPanelComponent.class);
				pUtil.getResultPanel(evaluationId).setEvaluationPanel(null);
	
				
			
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
