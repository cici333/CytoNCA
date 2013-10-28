package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;

public class NC extends Algorithm {

	public NC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		// TODO Auto-generated method stub
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		/*
		int i, j, k, du, m, len;
		double score;
		double x = 0;
		
		
		
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
		*/
		
		
		
		
		if(!isweight){
			CalNCWithoutWeight();
		}else{
			CalNCWithWeight();
		}
		
		
		
		return vertex;
	}
	
	protected void CalNCWithoutWeight(){
		HashMap<CyEdge, Double> edgesmap = new HashMap<CyEdge, Double>();
		for(Protein p : vertex){
			double sum = 0;
			for(CyEdge e : currentNetwork.getAdjacentEdgeList(p.getN(), Type.ANY)){
				if(!edgesmap.containsKey(e)){
					double cnnum = 0;
					ArrayList<CyNode> tlist = (ArrayList<CyNode>) currentNetwork.getNeighborList(e.getTarget(), Type.ANY);
					ArrayList<CyNode> slist = (ArrayList<CyNode>) currentNetwork.getNeighborList(e.getSource(), Type.ANY);
					double dt = tlist.size();
					double ds = slist.size();
					for(CyNode tn : tlist){
						if(slist.contains(tn)){
							cnnum ++;
							slist.remove(tn);
						}
					}
					
					double min  = Math.min(dt-1, ds-1);
					double rs = 0;
					if(min != 0){
						rs = cnnum/min;
						//	 rs = (cnnum+1)/min;
					}
					
					sum += rs;
					edgesmap.put(e, rs);
				//	System.out.println(rs+"***");
				}else{
					sum += edgesmap.get(e);
				}
				   
			}
			p.setNC(sum);
		}
		
		
		
	}
	protected void CalNCWithWeight(){
		HashMap<CyEdge, Double> edgesmap = new HashMap<CyEdge, Double>();
		for(Protein p : vertex){
			double sum = 0;
			for(CyEdge e : currentNetwork.getAdjacentEdgeList(p.getN(), Type.ANY)){
				
				if(!edgesmap.containsKey(e)){
					double tcnw = 0, scnw = 0, dtw = 0, dsw = 0;
					CyNode t = e.getTarget(); 
					CyNode s = e.getSource();
					ArrayList<CyNode> tlist = (ArrayList<CyNode>) currentNetwork.getNeighborList(t, Type.ANY);
					ArrayList<CyNode> slist = (ArrayList<CyNode>) currentNetwork.getNeighborList(s, Type.ANY);
	/*
					for(CyEdge ee : currentNetwork.getAdjacentEdgeList(t, Type.ANY)){
						CyNode an = ee.getSource().equals(t) ? ee.getSource() : ee.getTarget();
						if(slist.contains(an)){
							tcnw += currentNetwork.getRow(e).get("weight", Double.class);
						}
					}
					
		*/			
					
					for(CyNode tn : tlist){
						
						if(slist.contains(tn)){						
							slist.remove(tn);
						
							tcnw += currentNetwork.getRow(currentNetwork.getConnectingEdgeList(t, tn, Type.ANY).get(0)).get("weight", Double.class);
							scnw += currentNetwork.getRow(currentNetwork.getConnectingEdgeList(s, tn, Type.ANY).get(0)).get("weight", Double.class);
					
						}	
					}
					
					for(CyEdge te : currentNetwork.getAdjacentEdgeList(t, Type.ANY)){
						dtw += currentNetwork.getRow(te).get("weight", Double.class);
					}
					for(CyEdge se : currentNetwork.getAdjacentEdgeList(s, Type.ANY)){
						dsw += currentNetwork.getRow(se).get("weight", Double.class);
					}
					
					double temp = tcnw *scnw;
					double min = Math.min(dtw-1, dsw-1);
					double rs = 0;
					if(min != 0 ){
						if(temp != 0){
							//rs += (Math.sqrt(temp)+1)/min;
							rs = Math.sqrt(temp)/min; 
							  
							
						}
						//else if(temp == 0){
							//rs += 1/min;
							//rs = 0;
						//}
						else if(temp < 0){
						//	sum -= (Math.sqrt(0-temp)-1)/min;
							rs = -(Math.sqrt(0-temp)/min);
							
						}
						sum += rs;
					}
					
					edgesmap.put(e, rs);
				//	System.out.println(rs+"***");
				}else{
					sum += edgesmap.get(e);
				}
				
				
								
			}
			p.setNCW(sum);
		}
	} 
	
	
}

		
