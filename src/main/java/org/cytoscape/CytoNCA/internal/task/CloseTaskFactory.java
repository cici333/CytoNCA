package org.cytoscape.CytoNCA.internal.task;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.EpListPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class CloseTaskFactory
  implements TaskFactory, NetworkAboutToBeDestroyedListener
{
  private final CySwingApplication swingApplication;
  private final CyServiceRegistrar registrar;
  private final ProteinUtil pUtil;

  public CloseTaskFactory(CySwingApplication swingApplication, CyServiceRegistrar registrar, ProteinUtil pUtil)
  {
    this.swingApplication = swingApplication;
    this.registrar = registrar;
    this.pUtil = pUtil;
  }

  public TaskIterator createTaskIterator()
  {
    TaskIterator taskIterator = new TaskIterator(new Task[0]);
    Collection resultPanels = this.pUtil.getResultPanels();
    Collection eplistPanels = this.pUtil.getEpListPanels();
    CloseAllResultsTask closeResultsTask = new CloseAllResultsTask(this.swingApplication, this.pUtil);

    if (resultPanels.size() > 0 || eplistPanels.size() > 0) {
      taskIterator.append(closeResultsTask);
    }
    taskIterator.append(new CloseTask(closeResultsTask, this.registrar, this.pUtil));

    return taskIterator;
  }

  public boolean isReady()
  {
    return this.pUtil.isOpened();
  }

  public void handleEvent(NetworkAboutToBeDestroyedEvent e)
  {
    if (this.pUtil.isOpened()) {
      CyNetwork network = e.getNetwork();
      Set resultIds = this.pUtil.getNetworkResults(network.getSUID().longValue());
      Set eplistIds = this.pUtil.getNetworkEplists(network.getSUID().longValue());
      
      for (Iterator localIterator = resultIds.iterator(); localIterator.hasNext(); ) { 
    	int id = ((Integer)localIterator.next()).intValue();
      	ResultPanel panel = this.pUtil.getResultPanel(id);
        if (panel != null) 
        	panel.discard(false);
      }
      for (Iterator localIterator = eplistIds.iterator(); localIterator.hasNext(); ) { 
    	int id = ((Integer)localIterator.next()).intValue();
      	EpListPanel panel = this.pUtil.getEpListPanel(id);
      	if (panel != null) panel.discard(false);
     }
    }
  }
}