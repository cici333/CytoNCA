package org.cytoscape.CytoNCA.internal.task;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.actions.AnalyzeAction;
import org.cytoscape.CytoNCA.internal.actions.OpenEplistAction;
import org.cytoscape.application.CyApplicationManager;
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
  private final ProteinUtil pUtil;
  private final AnalyzeAction analyzeAction;
  private final OpenEplistAction openEplistAction;

  
  public OpenTaskFactory(CySwingApplication swingApplication, CyServiceRegistrar registrar, ProteinUtil pUtil, AnalyzeAction analyzeAction, OpenEplistAction openEplistAction)
  {
    this.swingApplication = swingApplication;
    this.registrar = registrar;
    this.pUtil = pUtil;
    this.analyzeAction = analyzeAction;
    this.openEplistAction =  openEplistAction;
   
  }

  public TaskIterator createTaskIterator()
  {
    return new TaskIterator(new Task[] { new OpenTask(this.swingApplication, this.registrar, this.pUtil, this.analyzeAction, this.openEplistAction) });
  }

  public boolean isReady()
  {
    return !this.pUtil.isOpened();
  }
}