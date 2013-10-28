package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.WeightTypes;

public class LAC extends Algorithm {
	
	public LAC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		// TODO Auto-generated method stub
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		double x = 0;
	
	
		/*
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
			*/
		if(!isweight){
			for(Protein p : vertex){
				List<CyNode> neibors = currentNetwork.getNeighborList(p.getN(), Type.ANY);
				double sum = 0;
				for(CyNode n : neibors){
					for(CyNode nn : currentNetwork.getNeighborList(n, Type.ANY)){
						if(neibors.contains(nn))
							sum++;
					}
				}
				
				double l = neibors.size();
				if(l != 0)
					p.setLAC(sum/l);

				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / vertex.size());
	                x++;
	            }
			}
		}
		else{
			for(Protein p : vertex){
				List<CyNode> neibors = currentNetwork.getNeighborList(p.getN(), Type.ANY);
				double sum = 0;
				for(CyNode n : neibors){
					for(CyNode nn : currentNetwork.getNeighborList(n, Type.ANY)){
						if(neibors.contains(nn)){
							sum += currentNetwork.getRow(currentNetwork.getConnectingEdgeList(n, nn, Type.ANY).get(0)).get("weight", Double.class);
						}
							
					}
				}
				
				double l = neibors.size();
				if(l != 0)
					p.setLACW(sum/l);
				
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / vertex.size());
	                x++;
	            }
			}
		}
		
		return vertex;
	}
}
