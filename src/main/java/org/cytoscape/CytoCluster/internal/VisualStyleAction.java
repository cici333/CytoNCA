package org.cytoscape.CytoCluster.internal;


import java.awt.Component;
import java.awt.event.ActionEvent;

import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

public class VisualStyleAction extends AbstractVizAction
  implements CytoPanelComponentSelectedListener
{
  private static final long serialVersionUID = -6884537645922099638L;
  private final VisualMappingManager visualMappingMgr;
  private final ClusterUtil mcodeUtil;

  public VisualStyleAction(String title, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, VisualMappingManager visualMappingMgr, ClusterUtil mcodeUtil)
  {
    super(title, applicationManager, swingApplication, netViewManager, "network");
    this.visualMappingMgr = visualMappingMgr;
    this.mcodeUtil = mcodeUtil;
  }

  public void actionPerformed(ActionEvent event)
  {
  }

  public void handleEvent(CytoPanelComponentSelectedEvent event)
  {
    Component component = event.getCytoPanel().getSelectedComponent();

    if ((component instanceof ResultPanel)) {
    	ResultPanel resultsPanel = (ResultPanel)component;

      double maxScore = resultsPanel.setNodeAttributesAndGetMaxScore();

      resultsPanel.selectCluster(null);

      VisualStyle appStyle = this.mcodeUtil.getAppStyle(maxScore);

      this.mcodeUtil.registerVisualStyle(appStyle);

      CyNetworkView netView = resultsPanel.getNetworkView();

      if ((netView != null) && 
        (this.visualMappingMgr.getVisualStyle(netView) == appStyle)) {
        appStyle.apply(netView);
        netView.updateView();
      }
    }
  }
}