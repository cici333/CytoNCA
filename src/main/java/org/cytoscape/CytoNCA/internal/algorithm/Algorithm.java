package org.cytoscape.CytoNCA.internal.algorithm;






import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinGraph;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

import java.math.BigDecimal;
import java.util.*;



/**
 * An implementation of the algorithm
 */
public abstract class Algorithm {
    protected boolean cancelled = false;//If set, will schedule the canceled algorithm  at the next convenient opportunity
    protected TaskMonitor taskMonitor = null;
    protected ParameterSet params;   //the parameters used for this instance of the algorithm
    protected CyNetwork currentNetwork;
    //states
    protected long lastScoreTime;	//the time taken by the last score operation
    protected long lastFindTime;	//the time taken by the last find operation
    protected long findCliquesTime=0;//time used to find maximal cliques
    
    protected ProteinUtil pUtil;
   /*
     * The constructor.
     *
     * @param networkID the algorithm use it to get the parameters of the focused network
     */
	public Algorithm(Long networkID, ProteinUtil pUtil) {		
		this.pUtil = pUtil;
		this.params = this.pUtil.getCurrentParameters().getParamsCopy(networkID);
    }
    //This method is used in AnalyzeTask
    public void setTaskMonitor(TaskMonitor taskMonitor,long  networkID) {
    	this.params = this.pUtil.getCurrentParameters().getParamsCopy(networkID);
        this.taskMonitor = taskMonitor;
    }
    public long getLastScoreTime() {
        return lastScoreTime;
    }
    public long getLastFindTime() {
        return lastFindTime;
    }
    public ParameterSet getParams() {
        return params;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }    
    public long getFindCliquesTIme() {
		return findCliquesTime;
	}
    abstract ArrayList<Protein> run(CyNetwork network, ArrayList<Protein> resultAll);
 
	
}
