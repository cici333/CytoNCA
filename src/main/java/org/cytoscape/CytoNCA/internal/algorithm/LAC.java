package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class LAC extends Algorithm {
	
	public LAC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex) {
		// TODO Auto-generated method stub
		currentNetwork = inputNetwork;
		int i, j, k, num,param=0;
		double score;
		double x = 0;
	
		param = pUtil.isweight(inputNetwork);
		for (i = 0; i < vertex.size(); i++) {
				score = 0;
				num = 0;
				for (j = 0; j < vertex.size(); j++) {
					if (inputNetwork.getConnectingEdgeList(
							vertex.get(i).getN(), vertex.get(j).getN(),
							Type.ANY).size() > 0) {
						num++;
						for (k = 0; k < vertex.size(); k++) {
							if (inputNetwork.getConnectingEdgeList(
									vertex.get(i).getN(), vertex.get(k).getN(),
									Type.ANY).size() > 0
									&& inputNetwork.getConnectingEdgeList(
											vertex.get(k).getN(),
											vertex.get(j).getN(),
											Type.ANY).size() > 0) {
								score++;
							}
						}
					}
				}
				vertex.get(i).setLAC((score + num) / num);
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / vertex.size());
	                x++;
	            }
			}
		return vertex;
		}
}
