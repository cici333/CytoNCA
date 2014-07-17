
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
	@Override
	public  ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		// TODO Auto-generated method stub
		int i,j;
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		
		len=vertex.size();
		
		Matrix matx = null, mtxQ = null, mtxT = null;
	
		
		
		try{
			matx = new SmallMatrix(len);
			mtxQ = new SmallMatrix(len);
		    mtxT = new SmallMatrix(len);
	
		}catch(OutOfMemoryError e){
		
			try {
				matx = new LargeMatrix(len, len);
				mtxQ = new LargeMatrix(len, len);
			    mtxT = new LargeMatrix(len, len);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//throw e;
		
		}
		
	
		if(!isweight){
			for(i=0;i<vertex.size();i++){
				for(j=0;j<vertex.size();j++){
					if(!inputNetwork.getConnectingEdgeList(vertex.get(i).getN(), vertex.get(j).getN(), Type.ANY).isEmpty()){
						matx.setElement(i, j, 1);
					}
					else{
						matx.setElement(i, j, 0.0f);
					}
				}
			}
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / (len *2));
                x++;
            }
		}
		else if(isweight){
			for(i=0;i<vertex.size();i++){
				for(j=0;j<vertex.size();j++){
					List<CyEdge> edge=inputNetwork.getConnectingEdgeList(vertex.get(i).getN(), vertex.get(j).getN(), Type.ANY);
					if(!edge.isEmpty()){
						matx.setElement(i, j, (inputNetwork.getRow(edge.get(0)).get("weight", Double.class)).floatValue());
					}
					else{
						matx.setElement(i, j, 0.0f);	
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));
	                x++;
	            }
			}
		}
	
		float[] bArray2 = new float[matx.getWidth()];
		float[] cArray2 = new float[matx.getWidth()];
		if (matx.makeSymTri(mtxQ, mtxT, bArray2, cArray2)) {
			// 2: compute eigenvalues and eigenvectors
			System.out.println("hahahahah");
			if (matx.computeEvSymTri(bArray2, cArray2, mtxQ, 60, 0.01f)) {
               setMaxVector(vertex, mtxQ, bArray2);
			} else {
				setCancelled(true);
				
			}
		} else {
			setCancelled(true);
			
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

		
		
		
		
		
		
		
		
		
		
	
		
		
		
		
		
		
		
	

		
	