package org.cytoscape.CytoNCA.internal;


import java.awt.Component;
import java.awt.event.ActionEvent;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.actions.AbstractPAction;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

public class VisualStyleAction extends AbstractPAction
  implements CytoPanelComponentSelectedListener
{
  private static final long serialVersionUID = -6884537645922099638L;
  private final VisualMappingManager visualMappingMgr;
  private final ProteinUtil mcodeUtil;

  public VisualStyleAction(String title, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, VisualMappingManager visualMappingMgr, ProteinUtil mcodeUtil)
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

   //   double maxScore = resultsPanel.setNodeAttributesAndGetMaxScore();

      resultsPanel.selectProteins(null);

     // VisualStyle appStyle = this.mcodeUtil.getAppStyle(maxScore);
      VisualStyle appStyle = this.mcodeUtil.getAppStyle();
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