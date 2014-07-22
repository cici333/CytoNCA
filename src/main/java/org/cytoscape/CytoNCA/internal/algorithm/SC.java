package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
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
		// TODO Auto-generated method stub
		
		int i, j;
		currentNetwork = inputNetwork;
		this.isweight = isweight;
		this.vertex = vertex;
		
		len = vertex.size();
		float[] tempData = new float[len * len];
		if (!isweight) {
			for (i = 0; i < vertex.size(); i++) {
				for (j = 0; j < vertex.size(); j++) {
					if (inputNetwork.getConnectingEdgeList(
							vertex.get(i).getN(), vertex.get(j).getN(),
							Type.ANY).size() > 0) {
						tempData[len * i + j] = 1;
					} else {
						tempData[len * i + j] = 0.0f;
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
								.get("weight", Double.class).floatValue();
					} else {
						tempData[len * i + j] = 0.0f;
					}
				}
				
				if (taskMonitor != null) {
	                taskMonitor.setProgress(x / (len *2));
	                x++;
	            }
				
			}
		}
	//	SmallMatrix matx = new SmallMatrix(len, tempData);
		SmallMatrix mtxQ2 = new SmallMatrix();
		
		float[] bArray2 = new float[mtxQ2.getWidth()];
		float[] cArray2 = new float[mtxQ2.getWidth()];
		System.err.println("run");
		if (mtxQ2.makeSymTri(bArray2, cArray2)) {
			// 2: compute eigenvalues and eigenvectors
			if (mtxQ2.computeEvSymTri(bArray2, cArray2, 60, 0.01f)) {
				setResult(vertex, mtxQ2, bArray2);
			} else {
				setCancelled(true);
				
			}
		} else {
			setCancelled(true);
			
		}
		return vertex;
	}
	private void setResult(ArrayList<Protein> vertex, SmallMatrix matrix,
			float[] value) {
		boolean[] flag = new boolean[value.length];
		int i = 0, j = 0;
		float result = 0;
		float temp = 0;
		
		for (i = 0; i < value.length; i++) {
			System.out.println(value[i]+"*******");
			}
		
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
				System.out.println(result+"****");
			}
		}
		
	}
}




