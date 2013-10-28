package org.cytoscape.CytoNCA.internal.task;


import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.MainPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class CloseTask
  implements Task
{
  private final CloseAllResultsTask closeAllResultsTask;
  private final CyServiceRegistrar registrar;
  private final ProteinUtil pUtil;

  public CloseTask(CloseAllResultsTask closeAllResultsTask, CyServiceRegistrar registrar, ProteinUtil pUtil)
  {
    this.closeAllResultsTask = closeAllResultsTask;
    this.registrar = registrar;
    this.pUtil = pUtil;
  }

  public void run(TaskMonitor taskMonitor) throws Exception
  {
    if ((this.closeAllResultsTask == null) || (this.closeAllResultsTask.close)) {
     MainPanel mainPanel = this.pUtil.getMainPanel();

      if (mainPanel != null) {
    	  if(mainPanel.getUploadbioinfopanel() != null)
    		  mainPanel.getUploadbioinfopanel().dispose();
        this.registrar.unregisterService(mainPanel, CytoPanelComponent.class);
      }

      this.pUtil.reset();
    }
  }

  public void cancel()
  {
  }
}