package org.cytoscape.CytoCluster.internal;

import java.util.Properties;

import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.MainPanel;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;


import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class OpenTask
  implements Task
{
  private final CySwingApplication swingApplication;
  private final CyServiceRegistrar registrar;
  private final ClusterUtil mcodeUtil;
  private final AnalyzeAction analyzeAction;

  public OpenTask(CySwingApplication swingApplication, CyServiceRegistrar registrar, ClusterUtil mcodeUtil, AnalyzeAction analyzeAction)
  {
    this.swingApplication = swingApplication;
    this.registrar = registrar;
    this.mcodeUtil = mcodeUtil;
    this.analyzeAction = analyzeAction;
  }

  public void run(TaskMonitor taskMonitor)
    throws Exception
  {
    synchronized (this) {
    	MainPanel mainPanel = null;

      if (!this.mcodeUtil.isOpened()) {
        mainPanel = new MainPanel(this.swingApplication, this.mcodeUtil);
        mainPanel.addAction(this.analyzeAction);

        this.registrar.registerService(mainPanel, CytoPanelComponent.class, new Properties());
        this.analyzeAction.updateEnableState();
      } else {
        mainPanel = this.mcodeUtil.getMainPanel();
      }

      if (mainPanel != null) {
        CytoPanel cytoPanel = this.mcodeUtil.getControlCytoPanel();
        int index = cytoPanel.indexOfComponent(mainPanel);
        cytoPanel.setSelectedIndex(index);
      }
    }
  }

  public void cancel()
  {
  }
}
