package org.cytoscape.CytoCluster.internal;

import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.algorithm.Algorithm;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class AnalyzeTaskFactory
  implements TaskFactory
{
  private final CyNetwork network;
  private final int analyze;
  private final int resultId;
  private final Algorithm alg;
  private final ClusterUtil mcodeUtil;
  private final AnalysisCompletedListener listener;

  public AnalyzeTaskFactory(CyNetwork network, int analyze, int resultId, Algorithm alg, ClusterUtil mcodeUtil, AnalysisCompletedListener listener)
  {
    this.network = network;
    this.analyze = analyze;
    this.resultId = resultId;
    this.alg = alg;
    this.mcodeUtil = mcodeUtil;
    this.listener = listener;
  }

  public TaskIterator createTaskIterator()
  {
    return new TaskIterator(new Task[] { new AnalyzeTask(this.network, this.analyze, this.resultId, this.alg, this.mcodeUtil, this.listener) });
  }

  public boolean isReady()
  {
/*	  boolean isR = (network==null);
	  isR &=this.mcodeUtil.getCurrentParameters().getParamsCopy(network.getSUID()).getAlgorithm().isEmpty();
	  
	  return isR;*/
    return true;
  }
}