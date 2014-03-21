package org.cytoscape.CytoNCA.internal.algorithm;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm.Matrix;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;

public class IC extends Algorithm {
	public IC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		List<CyNode> allNodes = currentNetwork.getNodeList();
		List<CyEdge> alledges = currentNetwork.getEdgeList();
		int nlength = allNodes.size();
		int elength = alledges.size();
		float x = 0;
		float allprocess =  elength + 10*nlength;
		
		float[] initial = new float[nlength * nlength];
		
		
		for(int i=0; i <nlength*nlength; i++){
			initial[i] = 1;
		}	
		Matrix CMatrix = new Matrix(nlength, initial);
		

		for(int i = 0; i < nlength; i++){
			float degree = currentNetwork.getNeighborList(allNodes.get(i), Type.ANY).size();
			CMatrix.setElement(i, i, degree+1);
		}
		
		if(!isweight){
			for(Iterator<CyEdge> it = alledges.iterator(); it.hasNext();){
				CyEdge e = it.next();
				int a = allNodes.indexOf(e.getSource());
				int b = allNodes.indexOf(e.getTarget());
				CMatrix.setElement(a, b, 0);
				CMatrix.setElement(b, a, 0);
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / allprocess);
	                x++;
	            //    System.out.println(x);
	            }
				
				if (cancelled) {
	                break;
	            }
			
			}
		}else{
			for(Iterator<CyEdge> it = alledges.iterator(); it.hasNext();){
				CyEdge e = it.next();
				int a = allNodes.indexOf(e.getSource());
				int b = allNodes.indexOf(e.getTarget());
				float dis = currentNetwork.getRow(e).get("weight", float.class);
				
				CMatrix.setElement(a, b, dis-1);
				CMatrix.setElement(b, a, dis-1);
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / allprocess);
	                x++;
	            //    System.out.println(x);
	            }
				
				if (cancelled) {
	                break;
	            }
			
			}
			
		}
		
		
	//	System.out.println(CMatrix.toString());
		//System.out.println(CMatrix.toString());
		x = CMatrix.invertGaussJordan(taskMonitor, x, allprocess);
	//	System.out.println(CMatrix.toString());
		
		if(x != -1){
			Matrix IMatrix = new Matrix(nlength);
			for(int i = 0; i < nlength; i++){
				for(int j = 0; j < nlength; j++){
					if(i != j){
						float v = CMatrix.getElement(i, i) + CMatrix.getElement(j, j) - 2*CMatrix.getElement(i, j);
						IMatrix.setElement(i, j, v);
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress((x) / allprocess);
	                x++;
	            }
				
				if (cancelled) {
	                break;
	            }
			}
			
			if(!isweight){
				for(int i = 0; i < nlength; i++){
					float sum = 0;
					for(int j = 0; j < nlength; j++){
						sum += IMatrix.getElement(i, j);
					}
					
					
					Protein p = vertex.get(i);
				//	System.out.println(nlength +"    "+ sum);
					p.setIC(nlength / sum);
					
					if (taskMonitor != null) {
		                taskMonitor.setProgress((x) / allprocess);
		                x++;
		        //        System.out.println(x);
		            }
					
					if (cancelled) {
		                break;
		            }
				}
			}else{
				for(int i = 0; i < nlength; i++){
					float sum = 0;
					for(int j = 0; j < nlength; j++){
						sum += IMatrix.getElement(i, j);
					}
					
					
					Protein p = vertex.get(i);
				//	System.out.println(nlength +"    "+ sum);
					p.setICW(nlength / sum);
					
					if (taskMonitor != null) {
		                taskMonitor.setProgress((x) / allprocess);
		                x++;
		        //        System.out.println(x);
		            }
					
					if (cancelled) {
		                break;
		            }
				}
			}
			
			
		}
		else 
			System.out.println("false!");
		
		return vertex;
		
	}
}
