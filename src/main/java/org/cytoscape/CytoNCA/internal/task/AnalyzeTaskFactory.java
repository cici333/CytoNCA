package org.cytoscape.CytoNCA.internal.task;

import java.util.ArrayList;

import org.cytoscape.CytoNCA.internal.AnalysisCompletedListener;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.algorithm.Algorithm;
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
  private final ArrayList<Algorithm> algSet;
  private final ProteinUtil pUtil;
  private final AnalysisCompletedListener listener;
  private final ArrayList<String> algnames;

  public AnalyzeTaskFactory(CyNetwork network, int analyze, int resultId, ArrayList<Algorithm> algSet, ProteinUtil pUtil, AnalysisCompletedListener listener, ArrayList<String> algnames)
  {
    this.network = network;
    this.analyze = analyze;
    this.resultId = resultId;
    this.algSet = algSet;
    this.pUtil = pUtil;
    this.listener = listener;
    this.algnames = algnames;
  }

  public TaskIterator addTask(TaskIterator taskIterator)
  {
	  if(taskIterator != null){
		  taskIterator.append(new AnalyzeTask(this.network, this.analyze, this.resultId, algSet, this.pUtil, this.listener, this.algnames));
	  }else{
		  taskIterator = new TaskIterator(new AnalyzeTask(this.network, this.analyze, this.resultId, algSet, this.pUtil, this.listener, this.algnames));
	  }
	  
	  return taskIterator;
	  
	//  return new TaskIterator(new Task[] { new AnalyzeTask(this.network, this.analyze, this.resultId, algSet, this.pUtil, this.listener, this.algnames) });
  }

  public boolean isReady()
  {
/*	  boolean isR = (network==null);
	  isR &=this.mcodeUtil.getCurrentParameters().getParamsCopy(network.getSUID()).getAlgorithm().isEmpty();
	  
	  return isR;*/
    return true;
  }

@Override
public TaskIterator createTaskIterator() {
	// TODO Auto-generated method stub
	return null;
}
}