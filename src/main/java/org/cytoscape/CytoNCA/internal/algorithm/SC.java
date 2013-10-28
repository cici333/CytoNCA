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

public class SC extends Algorithm {
	
	double x = 0;
	int len;
	
	public SC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		// TODO Auto-generated method stub
		
		int i, j;
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		
		len = vertex.size();
		double[] tempData = new double[len * len];
		if (!isweight) {
			for (i = 0; i < vertex.size(); i++) {
				for (j = 0; j < vertex.size(); j++) {
					if (inputNetwork.getConnectingEdgeList(
							vertex.get(i).getN(), vertex.get(j).getN(),
							Type.ANY).size() > 0) {
						tempData[len * i + j] = 1;
					} else {
						tempData[len * i + j] = 0.0;
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));
	               
	                x++;
	            }
				
				
			}
		} else if (isweight) {
			for (i = 0; i < vertex.size(); i++) {
				for (j = 0; j < vertex.size(); j++) {
					List<CyEdge> edge = inputNetwork.getConnectingEdgeList(
							vertex.get(i).getN(), vertex.get(j).getN(),
							Type.ANY);
					if (edge.size() > 0) {
						tempData[len * i + j] = inputNetwork
								.getRow(edge.get(0))
								.get("weight", Double.class);
					} else {
						tempData[len * i + j] = 0.0;
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));
	                x++;
	            }
				
			}
		}
		Matrix matx = new Matrix(len, tempData);
		Matrix mtxQ2 = new Matrix();
		Matrix mtxT2 = new Matrix();
		double[] bArray2 = new double[matx.getNumColumns()];
		double[] cArray2 = new double[matx.getNumColumns()];
		System.err.println("run");
		if (matx.makeSymTri(mtxQ2, mtxT2, bArray2, cArray2)) {
			// 2: compute eigenvalues and eigenvectors
			if (matx.computeEvSymTri(bArray2, cArray2, mtxQ2, 60, 0.0001)) {
				setResult(vertex, mtxQ2, bArray2);
			} else {
				System.out.println("Ê§°Ü");
			}
		} else {
			System.out.println("Ê§°Ü");
		}
		return vertex;
	}
	private void setResult(ArrayList<Protein> vertex, Matrix matrix,
			double[] value) {
		boolean[] flag = new boolean[value.length];
		int i = 0, j = 0;
		double result = 0;
		double temp = 0;
		for (i = 0; i < matrix.getNumColumns(); i++) {
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
			for (i = 0; i < matrix.getNumRows(); i++) {
				result = 0;
				temp = 0;
				for (j = 0; j < matrix.getNumColumns(); j++) {
					temp = matrix.getElement(i, j);
					result += temp * Math.exp(value[j]) * temp;
				}
				vertex.get(i).setSC(result);
			}
		}
		else{
			for (i = 0; i < matrix.getNumRows(); i++) {
				result = 0;
				temp = 0;
				for (j = 0; j < matrix.getNumColumns(); j++) {
					temp = matrix.getElement(i, j);
					result += temp * Math.exp(value[j]) * temp;
				}
				vertex.get(i).setSCW(result);
				System.out.println(result+"****");
			}
		}
		
	}
}




