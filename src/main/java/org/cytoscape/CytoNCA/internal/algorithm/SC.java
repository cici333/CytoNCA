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

public class SC extends Algorithm {
	
	float x = 0;
	int len;
	
	public SC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {

		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		List<CyEdge> eList = inputNetwork.getEdgeList();
		List<CyNode> nList = inputNetwork.getNodeList();
		len=vertex.size();
		boolean islarge = false;
		
		Matrix mtxQ = null;
	
		
		
		try{
			
			mtxQ = new SmallMatrix(len);
			
		}catch(OutOfMemoryError e){
		
			try {
				islarge = true;
				mtxQ = new LargeMatrix(len, len);
				pUtil.addDiskFile(((LargeMatrix) mtxQ).getFile());
			    
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		//taskMonitor.setStatusMessage("Step 1: Initial Matrix");
		float elen = eList.size();	
		if(!isweight){
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);	
				mtxQ.setElement(s, t, 1);
				mtxQ.setElement(t, s, 1);
				
	            taskMonitor.setProgress(x / elen);
	            x++;	  
	            
	            if (cancelled) {
	                return null;
	            }
			}
			
		}
		else if(isweight){
			
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);

				mtxQ.setElement(s, t, (inputNetwork.getRow(e).get("weight", Double.class)).floatValue());
				mtxQ.setElement(t, s, (inputNetwork.getRow(e).get("weight", Double.class)).floatValue());
				
				taskMonitor.setProgress(x / elen);
	            x++;
	            
	            if (cancelled) {
	                return null;
	            }

			}
		}
	
		float[] bArray2 = new float[len];
		float[] cArray2 = new float[len];
		
		if (mtxQ.makeSymTri(bArray2, cArray2, taskMonitor)) {
			
			// 2: compute eigenvalues and eigenvectors
			if (mtxQ.computeEvSymTri(bArray2, cArray2, 60, 0.01f, taskMonitor)) {
               setResult(vertex, mtxQ, bArray2);
			} 
			else {
				setCancelled(true);				
			}
		} else {
			setCancelled(true);			
		}
		
		if(islarge){

			
			((LargeMatrix) mtxQ).closefile();
			
		}

		return vertex;
	}
	private void setResult(ArrayList<Protein> vertex, Matrix matrix,
			float[] value) {
		boolean[] flag = new boolean[value.length];
		int i = 0, j = 0;
		float result = 0;
		float temp = 0;
		
		
		taskMonitor.setProgress(0);
		taskMonitor.setStatusMessage("Step 6...");
		
		for (i = 0; i < matrix.getWidth(); i++) {
			flag[i] = true;
			for (j = 0; j < i - 1; j++) {
				if (value[j] == value[i]) {
					flag[j] = false;
					break;
				}
			}
			
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / (len *2));           
                x++;
            }
		}
		
		if(!isweight){
			for (i = 0; i < matrix.getHeight(); i++) {
				result = 0;
				temp = 0;
				for (j = 0; j < matrix.getWidth(); j++) {
					temp = matrix.getElement(i, j);
					result += temp * Math.exp(value[j]) * temp;
				}
				vertex.get(i).setSC(result);
			
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));           
	                x++;
	            }
			}
			
			
		}
		else{
			for (i = 0; i < matrix.getHeight(); i++) {
				result = 0;
				temp = 0;
				for (j = 0; j < matrix.getWidth(); j++) {
					temp = matrix.getElement(i, j);
					result += temp * Math.exp(value[j]) * temp;
				}
				vertex.get(i).setSCW(result);
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));           
	                x++;
	            }
			}
		}
		
		
	}
}




