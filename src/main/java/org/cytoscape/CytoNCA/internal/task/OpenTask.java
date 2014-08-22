package org.cytoscape.CytoNCA.internal.task;

import java.util.Properties;

import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.actions.AnalyzeAction;
import org.cytoscape.CytoNCA.internal.actions.OpenEplistAction;
import org.cytoscape.CytoNCA.internal.panels.MainPanel;
import org.cytoscape.application.CyApplicationManager;
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
  private final ProteinUtil pUtil;
  private final AnalyzeAction analyzeAction;
  private final OpenEplistAction openEplistAction;


  public OpenTask(CySwingApplication swingApplication, CyServiceRegistrar registrar, ProteinUtil pUtil, AnalyzeAction analyzeAction, OpenEplistAction openEplistAction)
  {
    this.swingApplication = swingApplication;
    this.registrar = registrar;
    this.pUtil = pUtil;
    this.analyzeAction = analyzeAction;
    this.openEplistAction = openEplistAction;
  
  }

  public void run(TaskMonitor taskMonitor)
    throws Exception
  {
    synchronized (this) {
    	MainPanel mainPanel = null;

      if (!this.pUtil.isOpened()) {
        mainPanel = new MainPanel(this.swingApplication, this.pUtil);
        mainPanel.addAction(this.analyzeAction, ParameterSet.ANALYZE);
        mainPanel.addAction(this.openEplistAction, ParameterSet.OPENEPLIST);
        

        this.registrar.registerService(mainPanel, CytoPanelComponent.class, new Properties());
        this.analyzeAction.updateEnableState();
      } else {
        mainPanel = this.pUtil.getMainPanel();
      }

      if (mainPanel != null) {
        CytoPanel cytoPanel = this.pUtil.getControlCytoPanel();
        int index = cytoPanel.indexOfComponent(mainPanel);
        cytoPanel.setSelectedIndex(index);
      }
    }
  }

  public void cancel()
  {
  }
}
