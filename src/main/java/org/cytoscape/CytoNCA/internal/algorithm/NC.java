package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;

public class NC extends Algorithm {

	public NC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex) {
		// TODO Auto-generated method stub
		currentNetwork = inputNetwork;
		int i, j, param = 0, k, du, m, len;
		double score;
		double x = 0;
		
		param = pUtil.isweight(inputNetwork);
		len = vertex.size();
		int[] degree = new int[len];
		for (i = 0; i < len; i++) {
            degree[i]+=inputNetwork.getAdjacentEdgeList(vertex.get(i).getN(), Type.ANY).size();
		}
		for (i = 0; i < len; i++) {
			score = 0;
			for (j = 0; j < len; j++) {
				m = 0;
				if (inputNetwork.getConnectingEdgeList(vertex.get(i).getN(),
						vertex.get(j).getN(), Type.ANY).size() > 0) {
					for (k = 0; k < len; k++) {
						if (inputNetwork.getConnectingEdgeList(
								vertex.get(i).getN(), vertex.get(k).getN(),
								Type.ANY).size() > 0
								&& inputNetwork.getConnectingEdgeList(
										vertex.get(k).getN(),
										vertex.get(j).getN(), Type.ANY)
										.size() > 0) {
							m++;
						}
					}
				}
				du = degree[i] < degree[j] ? (degree[i] - 1) : (degree[j] - 1);
				if (du != 0)
					score += (double) m / du;
			}
			vertex.get(i).setNC(score);
			
			if (taskMonitor != null) {
                taskMonitor.setProgress((x) / len);
                x++;
            }
		}
		return vertex;
	}
}

		
