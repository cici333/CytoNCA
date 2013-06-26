package org.cytoscape.CytoCluster.internal.algorithm;



import org.cytoscape.CytoCluster.internal.Cluster;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

import java.util.*;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;



public class MCODE extends Algorithm{
	
	public MCODE(Long networkID,ClusterUtil clusterUtil){
		super(networkID, clusterUtil);
	}




	@Override
	public Cluster[] run(CyNetwork inputNetwork, int resultTitle) {
		// TODO Auto-generated method stub
		return(this.K_CoreFinder(inputNetwork, resultTitle));
	}
}
