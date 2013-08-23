
package org.cytoscape.CytoNCA.internal.algorithm;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm.Matrix;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;


public class EC extends Algorithm {

	double x = 0;
	int len;
	public EC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public  ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex) {
		// TODO Auto-generated method stub
		currentNetwork = inputNetwork;
		int param=0,i,j;
		
		
		List<CyNode> nodes = inputNetwork.getNodeList();
	
		param = pUtil.isweight(inputNetwork);
		
		len=vertex.size();
		double[] tempData = new double[len * len];
	//	if(inputNetwork.getRow(nodes.get(0)).get("weight", String.class) != null)
	//		param=1;
		if(param==0){
			for(i=0;i<vertex.size();i++){
				for(j=0;j<vertex.size();j++){
					if(inputNetwork.getConnectingEdgeList(vertex.get(i).getN(), vertex.get(j).getN(), Type.ANY).size()>0){
						tempData[len*i+j]=1;
					}
					else{
					  tempData[len*i+j]=0.0;	
					}
				}
			}
			if (taskMonitor != null) {
                taskMonitor.setProgress(x / (len *2));
                x++;
            }
		}
		else if(param==1){
			for(i=0;i<vertex.size();i++){
				for(j=0;j<vertex.size();j++){
					List<CyEdge> edge=inputNetwork.getConnectingEdgeList(vertex.get(i).getN(), vertex.get(j).getN(), Type.ANY);
					if(edge.size()>0){
						tempData[len*i+j]=inputNetwork.getRow(edge.get(0)).get("weight", Double.class);
					}
					else{
					  tempData[len*i+j]=0.0;	
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));
	                x++;
	            }
			}
		}
		Matrix matx = new Matrix(len,tempData);
		Matrix mtxQ2 = new Matrix();
		Matrix mtxT2 = new Matrix();
		double[] bArray2 = new double[matx.getNumColumns()];
		double[] cArray2 = new double[matx.getNumColumns()];
		if (matx.makeSymTri(mtxQ2, mtxT2, bArray2, cArray2)) {
			// 2: compute eigenvalues and eigenvectors
			if (matx.computeEvSymTri(bArray2, cArray2, mtxQ2, 60, 0.0001)) {
               setMaxVector(vertex, mtxQ2, bArray2);
			} else {
				System.out.println("Ê§°Ü");
			}
		} else {
			System.out.println("Ê§°Ü");
		}
		return vertex;
	} 
	
	private void setMaxVector(ArrayList<Protein> vertex, Matrix matrix,
			double[] value) {
		double max = Double.MIN_VALUE;
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
		for (i = 0; i < matrix.getNumRows(); i++) {
			vertex.get(i).setEC(matrix.getElement(i, j));
		}
	}
}

		
		
		
		
		
		
		
		
		
		
	
		
		
		
		
		
		
		
	

		
	