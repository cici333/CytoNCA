package org.cytoscape.CytoNCA.internal.algorithm;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;



import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;


public class CC extends Algorithm {

	public CC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		if(!isweight){
			CalAllShortestPath();
		}
		else{
			CalAllShortestPathWithWeight();
		}
			
		return vertex;
	}
	
	public void CalAllShortestPath(){
		List<CyNode> allNodes = currentNetwork.getNodeList();
		int nlength = allNodes.size();
		double x = 1.0; 
		double sum;
		
		//double[][] shortestpath = new double[nlength][nlength];
		Map<CyNode, Integer> QM = new HashMap<CyNode, Integer>();
		Map<CyNode, Double> D = new HashMap<CyNode, Double>(); 
		
		for(int i = 0; i < nlength; i++){
			CyNode n = allNodes.get(i);
			
			
			/*Initial Data*/
			for(CyNode nn : allNodes){				
				QM.put(nn, 0);
				D.put(nn, Double.MAX_VALUE) ;
			}
			
			
			Queue<CyNode> queue = new  ArrayDeque<CyNode>();
			queue.add(n);
			
			QM.put(n, 1);
			D.put(n, 0.0);
			
			while(!queue.isEmpty()){
				CyNode v = queue.remove(); 
				QM.put(v, 0);
				
				for(CyNode w : currentNetwork.getNeighborList(v, Type.ANY)){
					if(D.get(w) > D.get(v) + 1){
						D.put(w, D.get(v)+1);
						if(QM.get(w) == 0){
							queue.add(w);
							QM.put(w, 1);
						}
					}
				}
			}
			
			sum = 0;
			for(int y = 0; y < nlength; y++){
				
				CyNode yn = allNodes.get(y);			
				if(y != i){	
					
					if (D.get(yn) == Double.MAX_VALUE)
						sum += nlength;
					else
						sum += D.get(yn);									 
				}			
			}
			
			vertex.get(i).setCC((nlength-1)/sum);
			
			
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / nlength);
                x++;
            }
			
			if (cancelled) {
                break;
            }
		}

	}

	
	public void CalAllShortestPathWithWeight(){
		List<CyNode> allNodes = currentNetwork.getNodeList();
		int nlength = allNodes.size();
		int x = 1; 
		
		double[][] shortestpath = new double[nlength][nlength];
		Map<CyNode, Integer> QM = new HashMap<CyNode, Integer>();
		Map<CyNode, Double> D = new HashMap<CyNode, Double>(); 
		
		for(int i = 0; i < nlength; i++){
			CyNode n = allNodes.get(i);
			
			/*Initial Data*/
			for(CyNode nn : allNodes){				
				QM.put(nn, 0);
				D.put(nn, Double.MAX_VALUE) ;
			}
			
			
			Queue<CyNode> queue = new  ArrayDeque<CyNode>();
			queue.add(n);
			
			QM.put(n, 1);
			D.put(n, 0.0);
			
			while(!queue.isEmpty()){
				CyNode v = queue.remove(); 
				QM.put(v, 0);
				
				for(CyNode w : currentNetwork.getNeighborList(v, Type.ANY)){
					double dis = currentNetwork.getRow(currentNetwork.getConnectingEdgeList(w, v, Type.ANY).get(0)).get("weight", Double.class);					
					/**
					 * distance = 1 / weight;
					 * @author TangYu
					 * @date: 2014年8月20日 下午3:57:08
					 */
					if(dis <= 0){
						setCancelled(true);
						return;
					}				
						dis = 1/ dis;  //distance = 1 / weight;
					
					
					if(D.get(w) > D.get(v) + dis){
						D.put(w, D.get(v)+dis);
						if(QM.get(w) == 0){
							queue.add(w);
							QM.put(w, 1);
						}
					}
				}
			}
			
			for(int y = 0; y < nlength; y++){
				if(y != i){
					CyNode yn = allNodes.get(y);
					shortestpath[i][y] = D.get(yn);
					shortestpath[y][i] = D.get(yn);
					
				}
				
			}
			
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / nlength);
                x++;
            }
			
			if (cancelled) {
                break;
            }
		}

		for(int i = 0; i < nlength; i++){
			Protein p = vertex.get(i);
			double sum = 0;
			for(int y = 0; y < nlength; y++){
				if(y != i){
					if (shortestpath[i][y] == Double.MAX_VALUE)
						sum += nlength;
					else
						sum += shortestpath[i][y];
				}
					
			}
			p.setCCW((nlength-1)/sum);
		}
		
	}
	
	
}
