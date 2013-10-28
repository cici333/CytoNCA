package org.cytoscape.CytoNCA.internal.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class CC1 extends Algorithm {
	
	public CC1(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex, boolean isweight) {
		currentNetwork = inputNetwork;
		
		int k = 0, i, j, m,param=0;
		double score;
		double min,addLen=0;
		boolean flag;		
	
		param = pUtil.isweight(inputNetwork);
		
		int len = vertex.size();
		double[] dist = new double[len];
		int[] set = new int[len];
		List<CyEdge> temp;
		double x = 1;
		
		for (i = 0; i < len; i++) {
			Protein p=vertex.get(i);
			for (j = 0; j < len; j++) {
				temp=inputNetwork.getConnectingEdgeList(p.getN(), vertex.get(j).getN(), Type.ANY);
				if(temp.size()>0){
				    if(param==0) {
				    	dist[j]=1;
				    }
				    else if(param==1){
						dist[j]=inputNetwork.getRow(temp.get(0)).get("weight", double.class);
				    }
				}
				else
					dist[j]=Double.MAX_VALUE;
				set[j] = 0;
			}
			set[i] = 1;
			for (m = 0; m < len; m++) {// 找len-1次
				flag = false;
				min = Double.MAX_VALUE;
				k = 0;
				for (j = 0; j < len; j++) {
					if (set[j] == 0 &&dist[j]!=Double.MAX_VALUE&&dist[j] < min) {
						min = dist[j];
						k = j;
						flag = true;
					}
				}
				if (flag) {
					set[k] = 1;// 标记为已使用
					Protein tempK=vertex.get(k);
					for (j = 0; j < len; j++) {
						temp=tempK.getNetwork().getConnectingEdgeList(tempK.getN(), vertex.get(j).getN(),Type.ANY);
						if(temp.size()>0)//说明该边存在
						{
							if (param==0){
								addLen=1;
							}
							else if(param==1)
							{
								addLen=tempK.getNetwork().getRow(temp.get(0)).get("weight", Double.class);
							} 
						if(set[j] == 0
								&& dist[k] +addLen < dist[j]) {
							dist[j] = dist[k] + addLen;}
						}
					}
				}
				else
					break;
			}
			score = 0;
			for (j = 0; j < len; j++) {
				if (j != i) {
					if (dist[j] == Double.MAX_VALUE)
						score += len;// 重点
					else
						score += dist[j];
				}
			}
			vertex.get(i).setCC(((double)(len - 1) / score));
			
			if (taskMonitor != null) {
                taskMonitor.setProgress((x) / len);
                x++;
            }
			
			if (cancelled) {
                break;
            }
			
		}
		return vertex;
	}

}
