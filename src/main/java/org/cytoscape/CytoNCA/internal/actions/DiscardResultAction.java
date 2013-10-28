package org.cytoscape.CytoNCA.internal.actions;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;


import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.AnalysisPanel;
import org.cytoscape.CytoNCA.internal.panels.EvaluationPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.jfree.chart.ChartFrame;

// Referenced classes of package org.cytoscape.mcode.internal:
//			AbstractMCODEAction

public class DiscardResultAction extends AbstractPAction
{

	private static final long serialVersionUID = 0x43a98ed598054cbL;
	public static final String REQUEST_USER_CONFIRMATION_COMMAND = "requestUserConfirmation";
	private final int resultId;
	private final CyServiceRegistrar registrar;
	private final ProteinUtil pUtil;

	public DiscardResultAction(String name, int resultId, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, ProteinUtil pUtil)
	{
		super(name, applicationManager, swingApplication, netViewManager, "always");
		this.resultId = resultId;
		this.registrar = registrar;
		this.pUtil = pUtil;
	}

	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		ResultPanel rpanel = pUtil.getResultPanel(this.resultId);
		EvaluationPanel epanel = rpanel.getEvaluationPanel();
		AnalysisPanel apanel = rpanel.geteAnalysisPanel();
		//if (rpanel != null && epanel != null)
		if (rpanel != null )
		{
			int resultId = rpanel.getResultId();
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
				
				
				if(rpanel.chartfs !=null && !rpanel.chartfs.isEmpty())
					for(ChartFrame cf : rpanel.chartfs)
						if(cf != null)
							cf.dispose();
				pUtil.setSelected(null, rpanel.getNetwork());
				registrar.unregisterService(rpanel, CytoPanelComponent.class);
				pUtil.removeNetworkResult(resultId);
				
				apanel.discard();
				registrar.unregisterService(apanel, CytoPanelComponent.class);
				if(epanel != null){
					epanel.discard(requestUserConfirmation);
					registrar.unregisterService(epanel, CytoPanelComponent.class);
				}
					
				
			
			}
		}
		//CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.EAST);
		CytoPanel cytoPanel = pUtil.getEastCytoPanel();
		System.out.println(cytoPanel.getCytoPanelComponentCount());
		
		if (cytoPanel.getCytoPanelComponentCount() == 0)
			cytoPanel.setState(CytoPanelState.HIDE);
		
		if (pUtil.getResultPanels().size() == 0)
			pUtil.resetResults();
	}
}
