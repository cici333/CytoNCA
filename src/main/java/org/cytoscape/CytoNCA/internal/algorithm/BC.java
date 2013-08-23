package org.cytoscape.CytoNCA.internal.algorithm;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;

import java.util.AbstractQueue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


public class BC extends Algorithm {
	Map decorator = new HashMap<CyNode, BSData>();
	Map bcVertexDecorator = new HashMap<CyNode, Double>();
	
	public BC(Long networkID,ProteinUtil pUtil){
		super(networkID, pUtil);
	}
	
	@Override
	public ArrayList<Protein> run(CyNetwork inputNetwork, ArrayList<Protein> vertex) {
		currentNetwork = inputNetwork;
		computeBetweenness();
		for(Iterator it = vertex.iterator(); it.hasNext();){
			Protein p = (Protein) it.next();
			CyNode n = p.getN();
			
			System.out.println(bcVertexDecorator.get(n));
			p.setBC((Double) bcVertexDecorator.get(n));		
		}
		
		
		return vertex;
	}
	 


	protected void computeBetweenness()
	{
		Collection vertices = currentNetwork.getNodeList();
		double x = 1;
		
		for (Iterator it = vertices.iterator(); it.hasNext(); ) { 
    	
			CyNode s = (CyNode) it.next();
			initializeData();

			((BSData)decorator.get(s)).numSPs = 1.0D;
			((BSData)decorator.get(s)).distance = 0.0D;

			Stack stack = new Stack();
			Queue<CyNode> queue = new  ArrayDeque<CyNode>();
	
			queue.add(s);
     
			while (!queue.isEmpty()) {
				CyNode v = queue.remove();
				stack.push(v);

				for (Iterator itt = currentNetwork.getNeighborList(v, Type.ANY).iterator(); itt.hasNext(); ) { 
					CyNode w = (CyNode) itt.next();

					if (((BSData)decorator.get(w)).distance < 0.0D) {
						queue.add(w);
						((BSData)decorator.get(w)).distance = ((BSData)decorator.get(v)).distance + 1.0D;
					}

					if (((BSData)decorator.get(w)).distance == ((BSData)decorator.get(v)).distance + 1.0D) {
						((BSData)decorator.get(w)).numSPs += ((BSData)decorator.get(v)).numSPs;
						((BSData)decorator.get(w)).predecessors.add(v);
					}
				}
			}

			while (!stack.isEmpty()) {
				CyNode w = (CyNode) stack.pop();
				
				for (Iterator iit = ((BSData)decorator.get(w)).predecessors.iterator(); iit.hasNext(); ) { 
					
					CyNode v = (CyNode) iit.next();
					double partialDependency = ((BSData)decorator.get(v)).numSPs / ((BSData)decorator.get(w)).numSPs;
					partialDependency *= (1.0D + ((BSData)decorator.get(w)).dependency);
					((BSData)decorator.get(v)).dependency += partialDependency;
   
				}
				if (w != s) {
					double bcValue = ((Number)bcVertexDecorator.get(w)).doubleValue();
					bcValue += ((BSData)decorator.get(w)).dependency;
					bcVertexDecorator.put(w, Double.valueOf(bcValue));
					
					
				}
			}
			
			
			if (taskMonitor != null) {
                taskMonitor.setProgress((x) / vertices.size());
                x++;
            }
			
			if (cancelled) {
                break;
            }
			
			System.out.println();
		}
/*
  //  if ((currentNetwork.)) {
      for (Iterator ii = vertices.iterator(); ii.hasNext(); ) { 
    	  CyNode v = (CyNode) ii.next();
    	  double bcValue = ((Number)bcVertexDecorator.get(v)).doubleValue();
    	  bcValue /= 2.0D;
    	  bcVertexDecorator.put(v, Double.valueOf(bcValue));
      }
     
  //  }
*/
		for (Iterator it = vertices.iterator(); it.hasNext(); ) 
		{ 
			CyNode vertex = (CyNode) it.next();
			decorator.remove(vertex); 
		}
	}

	private void initializeData()
	{
	  
		for (Iterator it = currentNetwork.getNodeList().iterator(); it.hasNext(); ) { 
    	
			CyNode vertex = (CyNode) it.next();
			if(!bcVertexDecorator.containsKey(vertex))
				bcVertexDecorator.put(vertex, Double.valueOf(0.0D));
   			decorator.put(vertex, new BSData());
		}
   
	}

 
 
	class BSData { 
		double distance;
		double numSPs;
		List<CyNode> predecessors;
		double dependency;
		
		BSData() { 
			this.distance = -1.0D;
			this.numSPs = 0.0D;
			this.predecessors = new ArrayList();
			this.dependency = 0.0D;
		}
	}
}
	