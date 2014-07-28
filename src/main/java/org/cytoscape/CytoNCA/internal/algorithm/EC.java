
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
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;


public class EC extends Algorithm {
/*************************************************/
	float x = 0;
	int len;
	public EC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	

	public  ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		// TODO Auto-generated method stub

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
		
	
		if(!isweight){
			int a = 0;
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);	
				mtxQ.setElement(s, t, 1);
				mtxQ.setElement(t, s, 1);
			//	System.out.println(a++ + "  1 ");
			}
			/*
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / (len *2));
                x++;
            }
            */
		}
		else if(isweight){
			
			for(CyEdge e : eList){
				
				CyNode sn = e.getSource();
				CyNode tn = e.getTarget();
				int s = nList.indexOf(sn);
				int t = nList.indexOf(tn);

				mtxQ.setElement(s, t, (inputNetwork.getRow(e).get("weight", Double.class)).floatValue());
				mtxQ.setElement(t, s, (inputNetwork.getRow(e).get("weight", Double.class)).floatValue());

			}
		}
	
		float[] bArray2 = new float[len];
		float[] cArray2 = new float[len];
		if (mtxQ.makeSymTri(bArray2, cArray2)) {
			// 2: compute eigenvalues and eigenvectors
			if (mtxQ.computeEvSymTri(bArray2, cArray2, 60, 0.01f)) {
               setMaxVector(vertex, mtxQ, bArray2);
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
	
	private void setMaxVector(ArrayList<Protein> vertex, Matrix matrix,
			float[] value) {
		float max = Float.MIN_VALUE;
		int i = 0, j = 0;

		
		for (i = 0; i < value.length; i++) {
			if (value[i] > max) {
				max = value[i];
				j = i;
			}
			
			if (taskMonitor != null) {
                taskMonitor.setProgress((x) / len *2);
                x++;
            }
		}
		
		int l = matrix.getHeight();
		double temp;
		if(!isweight){
			for (i = 0; i < l; i++) {
				//vertex.get(i).setEC(0-matrix.getElement(i, j));
				temp = matrix.getElement(i, j);
				vertex.get(i).setEC(temp > 0 ? temp : 0 - temp); // outputted as absolute value
			}
		}
		else{
			for (i = 0; i < l; i++) {
				//vertex.get(i).setECW(0-matrix.getElement(j, i));
				temp = matrix.getElement(i, j);
				vertex.get(i).setECW(temp > 0 ? temp : 0 - temp);
			
			}
		}
		
	}
	
	
}

		
		
		
		
		
		
		
		
		
		
	
		
		
		
		
		
		
		
	

		
	