package org.cytoscape.CytoNCA.internal.task;

import java.util.Collection;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.EpListPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class CloseAllResultsTask
  implements Task
{

  @Tunable(description="<html>You are about to close the CytoNCA app.<br />Do you want to continue?</html>", params="ForceSetDirectly=true")
  public boolean close = true;
  private final CySwingApplication swingApplication;
  private final ProteinUtil pUtil;

  public CloseAllResultsTask(CySwingApplication swingApplication, ProteinUtil pUtil)
  {
    this.swingApplication = swingApplication;
    this.pUtil = pUtil;
  }

  @ProvidesTitle
  public String getTitle() {
    return "Close CytoNCA";
  }

  public void run(TaskMonitor taskMonitor) throws Exception
  {
    if (this.close) {
      Collection<ResultPanel> resultPanels = this.pUtil.getResultPanels();
      Collection<EpListPanel> eplistPanels = this.pUtil.getEpListPanels();
      
      for (ResultPanel panel : resultPanels) {
        panel.discard(false);
      }
      for (EpListPanel panel : eplistPanels) {
          panel.discard(false);
        }

      CytoPanel cytoPanel = this.swingApplication.getCytoPanel(CytoPanelName.WEST);

      if (cytoPanel.getCytoPanelComponentCount() == 0)
        cytoPanel.setState(CytoPanelState.HIDE);
      
	  pUtil.deleteDiskFiles();
    }
  }

  public void cancel()
  {
  }
}