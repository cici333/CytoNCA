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
					if(D.get(w) > D.get(v) + 1){
						D.put(w, D.get(v)+1);
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
					
					//System.out.println(D.get(yn));
				}
				
			}
			
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / nlength);
                x++;
                System.out.println(x);
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
		//	System.out.println("$$           "+sum);
			p.setCC((nlength-1)/sum);
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
					
					//System.out.println(D.get(yn));
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
	//		System.out.println("@@           "+sum);
			p.setCCW((nlength-1)/sum);
		}
		
	}
	
	
}
