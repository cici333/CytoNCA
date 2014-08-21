package org.cytoscape.CytoNCA.internal.algorithm;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm.LargeMatrix;
import org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm.Matrix;
import org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm.SmallMatrix;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;

public class IC extends Algorithm {
	float x = 0;
	int len;
	public IC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {

		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		List<CyEdge> eList = inputNetwork.getEdgeList();
		List<CyNode> nList = inputNetwork.getNodeList();
		len=vertex.size();
		boolean islarge = false;
		int elen = eList.size();

		float allprocess =  elen + 10*len;		
		Matrix mtxQ = null;
	

	//	try{
			
	//		mtxQ = new SmallMatrix(len);
			
	//	}catch(OutOfMemoryError e){
		
			try {
				islarge = true;
				mtxQ = new LargeMatrix(len, len, 1.0f);
				pUtil.addDiskFile(((LargeMatrix) mtxQ).getFile());
				
			    
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	//	}

		// C = D - A + J;
		for(int i = 0; i < len; i++){
			float degree = currentNetwork.getNeighborList(nList.get(i), Type.ANY).size();
			mtxQ.setElement(i, i, degree+1);
		}
		
		if(!isweight){
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);	
				mtxQ.setElement(s, t, 0);
				mtxQ.setElement(t, s, 0);
				
				taskMonitor.setProgress(x / elen);
	            x++;	  
	            
	            if (cancelled) {
	                return null;
	            }
			
			}
		}else{
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);

				mtxQ.setElement(s, t, 1 -(inputNetwork.getRow(e).get("weight", Double.class)).floatValue());
				mtxQ.setElement(t, s, 1 -(inputNetwork.getRow(e).get("weight", Double.class)).floatValue());
				
				taskMonitor.setProgress(x / elen);
	            x++;	  
	            
	            if (cancelled) {
	                return null;
	            }
			
			}
			
		}


		
		if(mtxQ.invertGaussJordan(taskMonitor)){
			
			for(int i = 0; i < len; i++){
				float sum = 0;
				for(int j = 0; j < len; j++){
					if(i != j){
						sum += mtxQ.getElement(i, i) + mtxQ.getElement(j, j) - 2*mtxQ.getElement(i, j);
					}
				}
				
				Protein p = vertex.get(i);
				if(!isweight){
					p.setIC(len / sum);
				}else{
					p.setICW(len / sum);
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / allprocess);
	                x++;
	            }
				
				if (cancelled) {
	                break;
	            }
			}
	
			
		}
		else 
			setCancelled(true);		
		
		return vertex;
		
	}
}
