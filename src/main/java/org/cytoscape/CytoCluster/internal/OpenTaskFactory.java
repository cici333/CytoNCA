package org.cytoscape.CytoCluster.internal;

import org.cytoscape.CytoCluster.internal.AnalyzeAction;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class OpenTaskFactory
  implements TaskFactory
{
  private final CySwingApplication swingApplication;
  private final CyServiceRegistrar registrar;
  private final ClusterUtil mcodeUtil;
  private final AnalyzeAction analyzeAction;

  public OpenTaskFactory(CySwingApplication swingApplication, CyServiceRegistrar registrar, ClusterUtil mcodeUtil, AnalyzeAction analyzeAction)
  {
    this.swingApplication = swingApplication;
    this.registrar = registrar;
    this.mcodeUtil = mcodeUtil;
    this.analyzeAction = analyzeAction;
  }

  public TaskIterator createTaskIterator()
  {
    return new TaskIterator(new Task[] { new OpenTask(this.swingApplication, this.registrar, this.mcodeUtil, this.analyzeAction) });
  }

  public boolean isReady()
  {
    return !this.mcodeUtil.isOpened();
  }
}