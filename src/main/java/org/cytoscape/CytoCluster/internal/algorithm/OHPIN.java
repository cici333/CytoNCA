package org.cytoscape.CytoCluster.internal.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.cytoscape.CytoCluster.internal.Cluster;
import org.cytoscape.CytoCluster.internal.ClusterGraph;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.ParameterSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

class OS{
	Cluster c1, c2;
	double os = 0;
	public OS(Cluster c1, Cluster c2){
		this.c1 = c1;
		this.c2 = c2;		
	}
}


class CCV{
	Cluster c1, c2;
	double ccv = 0;
	public CCV(Cluster c1, Cluster c2){
		this.c1 = c1;
		this.c2 = c2;		
	}
}


public class OHPIN extends Algorithm{
	int findingProgress = 0;
    int findingTotal = 0;
		
	public OHPIN(Long networkID,ClusterUtil clusterUtil){
		super(networkID, clusterUtil);
	}
	
	
	public boolean isBelong(Cluster c1, Cluster c2){
		return c2.getALNodes().containsAll(c1.getALNodes());
	}
	
	public Cluster calB_cluster(CyEdge e){
		Long from = e.getSource().getSUID();
		Long to = e.getTarget().getSUID();
		Cluster B_cluster = null;
		ArrayList<Long> commonneighbor = getCommonNeighbors(from,to);
		if(!commonneighbor.isEmpty()){
			commonneighbor.add(from);
			commonneighbor.add(to);	
			B_cluster = new Cluster(e.getSUID());
			B_cluster.setALNodes(commonneighbor);
		}		
		return B_cluster;		
	}
	
	
	/*****Step 1*******/
	public ArrayList<Cluster> calC_set(CyNetwork inputNetwork){
		ArrayList<Cluster> C_set = new ArrayList<Cluster>();
		
		Iterator edges = inputNetwork.getEdgeList().iterator();
		findingTotal = inputNetwork.getEdgeList().size();
		while(edges.hasNext()){
        	CyEdge e = (CyEdge) edges.next();
        	Cluster B_cluster = calB_cluster(e);
        	if(B_cluster != null){
        		if(!C_set.isEmpty()){
            	//	Iterator i = C_set.iterator();
            	//	while(i.hasNext()){
            	//		Cluster ci = (Cluster) i.next();
        			
        			for(int i = 0; i<C_set.size(); i++){
        				Cluster ci = (Cluster)C_set.get(i);
            			if(isBelong(B_cluster,ci)){//if B_cluster belongs to ci
            				B_cluster.setAdd(0);
            				break;
            			}
            		}
            		
        			if(B_cluster.getAdd() == 1){
        //				Iterator j = C_set.iterator();  //此处使用迭代报错
        //				while(j.hasNext()){
        //					Cluster cj = (Cluster)j.next();
        				for(int j = 0; j<C_set.size(); ){
        					Cluster cj = (Cluster)C_set.get(j);
                			if(isBelong(cj,B_cluster)){
                				C_set.remove(j);                				
                			}
                			else 
                				j++;
                		}
        				C_set.add(B_cluster);
            		}	
            	}
        		else
        			C_set.add(B_cluster);
        		
        		
        	}
        	   
        	
        	if (taskMonitor != null) {
                findingProgress++;
                //We want to be sure that only progress changes are reported and not
                //miniscule decimal increments so that the taskMonitor isn't overwhelmed
                int newProgress = (findingProgress * 100) / findingTotal;
                int oldProgress = ((findingProgress-1) * 100) / findingTotal;
                if (newProgress != oldProgress) {
                    taskMonitor.setProgress(newProgress);
                }
            }
            if (cancelled) {
                break;
            }
        }
		return C_set;
		
	}
	
	/*************************************************************************************/
	public double calCommonNode(Cluster c1, Cluster c2){
		double num=0;
		for(int i = 0; i < c1.getALNodes().size(); i++){
			for(int j = 0; j < c2.getALNodes().size(); j++)
				if(c1.getALNodes().get(i) == c2.getALNodes().get(j)){
					num++;
					break;
				}			
		}
		
		return num;
	}
	
	
	public double calOS(Cluster c1, Cluster c2){
		double commonnodes = calCommonNode(c1, c2);
		double c1nodes = c1.getALNodes().size();
		double c2nodes = c2.getALNodes().size();
		
		double os = (commonnodes*commonnodes)/(c1nodes*c2nodes);
		System.out.println(os);
		
		return os;
	}
	
	
	public ArrayList<Long> merge(Cluster c1, Cluster c2){
		ArrayList<Long> c = c1.getALNodes();
		for(int i = 0; i<c2.getALNodes().size(); i++){
			long x = (Long) c2.getALNodes().get(i);
			if(!c.contains(x))
				c.add(x);
		}
		return c;
	}
	
	/*****Step 2*******/
	public ArrayList<Cluster> OSMerge(ArrayList<Cluster> C_set,double os_th){
		int s = C_set.size();
		double[][] matrix = new double[s][s] ;
		PriorityQueue<OS> pq = new PriorityQueue<OS>(11, new Comparator<OS>(){
            public int compare( OS a, OS b ){
                if(a.os > b.os ){
                    return -1;
                }
                else if(b.os > a.os  ){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        }); 
		
		System.out.println(s);
		
		for(int i = 0; i < s; i++)
			C_set.get(i).setComplexID(i);
			
		
		for(int i = 0; i < s-1; i++){
			
	//		C_set.get(i).setComplexID(i);		
			Cluster c1 = C_set.get(i);
			
			for(int j = i+1; j < s; j++){									
				Cluster c2 = C_set.get(j);
				
				OS a = new OS(c1, c2);
				a.os = calOS(c1, c2);
				pq.add(a);
				
				matrix[i][j] = a.os;
				System.out.println(a.os+"######");
			}
		}
		
		
		while(!pq.isEmpty()){
			OS max = pq.poll();
			System.out.println(max.os+"^^^^^^^^^^");
			if(max.os > os_th){
				Cluster c1 = max.c1;
				Cluster c2 = max.c2;
				int i = c1.getComplexID();
				int j = c2.getComplexID();
				C_set.get(i).setALNodes(merge(c1, c2)); //merge c1. c2			
				C_set.get(j).setFlag(0);  //C_set - c2
				System.out.println(i+"   "+j+"GGGGGG");
				
				System.out.println(C_set.get(0).getFlag()+"$$$$$$");
				
				
				
				for(int k = 0; k<s ; k++){
    				Cluster ck = (Cluster)C_set.get(k);
    				if(ck.getFlag() == 1){
    					matrix[j][k] = 0; 
    					matrix[k][j] = 0;
    				}
    			}
				for(int k = 0; k<s; k++){
					Cluster ck = (Cluster)C_set.get(k);
					if(k != i && ck.getFlag() == 1){
						if(isBelong(ck,C_set.get(i))){
							C_set.get(k).setFlag(0);
							System.out.println(C_set.get(0).getFlag()+"$$$$$$");
							
							
							for(int t = 0; t < s; t++){
			    				Cluster ct = (Cluster)C_set.get(t);
			    				if(ck.getFlag() == 1){
			    					matrix[k][t] = 0; 
			    					matrix[t][k] = 0;
			    				}
							}
						
						}
						else{
							double o = calOS(ck, C_set.get(i));
							if(i < k)
								matrix[i][k] = o;
							else 
								matrix[k][i] = o;	
							System.out.println(o+"&&&&&&&");
							
						}
					}
					
				}	
				
				for(i = 0; i < s-1; i++){
					
					
					for(j =i+1 ; j< s; j++){
						System.out.print("line  "+ j+matrix[i][j]+"   ");
					}
					System.out.println(i+"row");
				}
				pq.clear();
				for( i = 0; i < s-1; i++){
					c1 = C_set.get(i);
					if(c1.getFlag() == 1){
						for( j = i+1; j < s; j++){
							c2 = C_set.get(j);
							if(c2.getFlag() == 1){
								OS temp = new OS( c1,  c2);
								temp.os = matrix[i][j];
								pq.add(temp);
							}
							else 
								continue;
							
						}
					}
					else
						continue;
				}	
				
			}
			else
				break;
		}
		
		for(int i = 0 ; i < C_set.size() ; ){
			if(C_set.get(i).getFlag() == 0)
				C_set.remove(i);
			
			else
				i++;		
			
		}
		
		return C_set;
		
	}
	 
	
	
	
	
	/*********************************************************************************/
	
	public double calCommonedgeW (Cluster c1, Cluster c2,CyNetwork inputNetwork){
		double sum = 0;
		
		ArrayList<Long> allnodes1 = c1.getALNodes();
		ArrayList<Long> allnodes2 = c2.getALNodes();
		
		Iterator<Long> nodes = allnodes1.iterator();
		while(nodes.hasNext()){
			Long n = nodes.next();
			ArrayList<CyEdge>edges = (ArrayList<CyEdge>) inputNetwork.getAdjacentEdgeList(inputNetwork.getNode(n), Type.ANY);
			for(int i=0; i<edges.size(); i++){
				CyEdge e = edges.get(i);
				if(allnodes2.contains(e.getSource().getSUID()) || allnodes2.contains(e.getSource().getSUID()))
					if(inputNetwork.getRow(e).get("weight", Double.class) != null)
						sum += inputNetwork.getRow(e).get("weight", Double.class);
	        		else
	        			sum += 1.0;
			}
				
		}
		return sum;	
	}
	
	public double calCCV(Cluster c1, Cluster c2,CyNetwork inputNetwork){
		double ccv = calCommonedgeW(c1, c2, inputNetwork)/ (c1.getALNodes().size()*c2.getALNodes().size());
		return ccv;
	}
	
	
	
	
	public boolean isModule(Cluster c1, double thereshold, CyNetwork inputNetwork){
		
		ArrayList<Long> allnodes = c1.getALNodes();
		Iterator<Long> nodes = allnodes.iterator();
		int indegree = 0;
		int outdegree = 0;
		while(nodes.hasNext()){
			Long n = nodes.next();
			ArrayList<CyEdge>edges = (ArrayList<CyEdge>) inputNetwork.getAdjacentEdgeList(inputNetwork.getNode(n), Type.ANY);
			for(int i=0; i<edges.size(); i++){
				CyEdge e = edges.get(i);
				if(allnodes.contains(e.getSource().getSUID()) || allnodes.contains(e.getSource().getSUID()))
					indegree++;
				else
					outdegree++;
			}
					
		}
		if((indegree/outdegree) >= thereshold)
			return true;
		else 
			return false;
		
	}
	
	
	
	public ArrayList<Cluster> ModuleMerge(ArrayList<Cluster> C_set,double thereshold,CyNetwork inputNetwork){
		int s = C_set.size();
		double[][] ccvmatrix = new double[s][s] ;
		
		PriorityQueue<CCV> ccvq = new PriorityQueue<CCV>(11, new Comparator<CCV>(){
            public int compare( CCV a, CCV b ){
                if(a.ccv > b.ccv ){
                    return -1;
                }
                else if(b.ccv > a.ccv ){
                    return 1;
                }
                else{
                    return 0;
                }
            }
        }); 
		
		
		for(int i = 0; i < s; i++)
			C_set.get(i).setComplexID(i);
			
		
		for(int i = 0; i < s-1; i++){
			Cluster c1 = C_set.get(i);
			for(int j = i+1; j < s; j++){									
				Cluster c2 = C_set.get(j);
				
				CCV a = new CCV(c1, c2);
				a.ccv = calCCV(c1, c2,inputNetwork);
				ccvq.add(a);
				
				ccvmatrix[i][j] = a.ccv;
				System.out.println(a.ccv+"######");
			}
		}
		
		
		while(!ccvq.isEmpty()){
			CCV max = ccvq.poll();
			System.out.println(max.ccv+"^^^^^^^^^^");
			if(max.ccv > 0){
				Cluster c1 = max.c1;
				Cluster c2 = max.c2;
				int i = c1.getComplexID();
				int j = c2.getComplexID();
				
				if(c1.isModule() && c2.isModule()){
					ccvmatrix[i][j] = -1;
				}
				else{
					C_set.get(i).setALNodes(merge(c1, c2)); //merge c1. c2
					C_set.get(j).setFlag(0);  //C_set - c2
					
					System.out.println(i+"   "+j+"GGGGGG");
					
					System.out.println(C_set.get(0).getFlag()+"$$$$$$");
					
					
					
					for(int k = 0; k<s ; k++){
	    				Cluster ck = (Cluster)C_set.get(k);
	    				if(ck.getFlag() == 1){
	    					ccvmatrix[j][k] = 0; 
	    					ccvmatrix[k][j] = 0;
	    				}
	    			}
					
					for(int k = 0; k<s; k++){
						Cluster ck = (Cluster)C_set.get(k);
						if(k != i && ck.getFlag() == 1){
							if(isBelong(ck,C_set.get(i))){
								C_set.get(k).setFlag(0);
								System.out.println(C_set.get(0).getFlag()+"$$$$$$");
								
								
								for(int t = 0; t < s; t++){
				    				Cluster ct = (Cluster)C_set.get(t);
				    				if(ck.getFlag() == 1){
				    					ccvmatrix[k][t] = 0; 
				    					ccvmatrix[t][k] = 0;
				    				}
								}
							
							}
							else{
								double o = calOS(ck, C_set.get(i));
								if(i < k)
									ccvmatrix[i][k] = o;
								else 
									ccvmatrix[k][i] = o;	
								System.out.println(o+"&&&&&&&");
								
							}
						}
						
					}	
					
					for(i = 0; i < s-1; i++){
						
						
						for(j =i+1 ; j< s; j++){
							System.out.print("line  "+ j+ ccvmatrix[i][j]+"   ");
						}
						System.out.println(i+"row");
					}
					ccvq.clear();
					for( i = 0; i < s-1; i++){
						c1 = C_set.get(i);
						if(c1.getFlag() == 1){
							for( j = i+1; j < s; j++){
								c2 = C_set.get(j);
								if(c2.getFlag() == 1){
									CCV temp = new CCV( c1,  c2);
									temp.ccv = ccvmatrix[i][j];
									ccvq.add(temp);
								}
								else 
									continue;
								
							}
						}
						else
							continue;
					}	
    			}			
			}
			else
				break;
		}
		
		for(int i = 0 ; i < C_set.size() ; ){
			if(C_set.get(i).getFlag() == 0)
				C_set.remove(i);
			else 
				i++;
		}
		
		return C_set;
		
		
		
		
		
	}
	
	
	/***************************************************************************************************/
	
	public Cluster[] OHPINFinder(CyNetwork inputNetwork,int resultTitle){
		 String callerID = "Algorithm.OH-PINFinder";
	    	System.out.println("In "+callerID);
	    	params=getParams();
	    	currentNetwork=inputNetwork;
	    	long msTimeBefore = System.currentTimeMillis();
	    	ArrayList<Cluster> C_set = new ArrayList<Cluster>();
	
	    	C_set = calC_set(currentNetwork);
	    	
	    	for(int t=0; t<C_set.size(); t++){
		        
		        Iterator ii = C_set.get(t).getALNodes().iterator();
		        System.out.println("cluster "+t);
		        int x=1;
		        while(ii.hasNext()){
		        	CyNode nn = inputNetwork.getNode((Long)ii.next());
		        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
		        	x++;
		        }
		        }
	    	
	    	
	    	C_set =  OSMerge(C_set, params.getOverlappingScore());
	    	
	    	for(int t=0; t<C_set.size(); t++){
		        
		        Iterator ii = C_set.get(t).getALNodes().iterator();
		        System.out.println("cluster "+t);
		        int x=1;
		        while(ii.hasNext()){
		        	CyNode nn = inputNetwork.getNode((Long)ii.next());
		        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
		        	x++;
		        }
		       }
	    	
	    	
	    	ArrayList alOriginalClusters = ModuleMerge(C_set, params.getfThresholdOHPIN(), inputNetwork);

	    	
	    	/* 
	    	ArrayList alClusters1 = calC_set(currentNetwork);
	    	//Finally convert the arraylist into a fixed array
	        Cluster[] clusters = new Cluster[alClusters1.size()];
	        for (int c = 0; c < clusters.length; c++) {
	            clusters[c] = (Cluster) alClusters1.get(c);
	        }
	        long msTimeAfter = System.currentTimeMillis();
	        lastFindTime = msTimeAfter - msTimeBefore;
	        
	        for(int t=0; t<clusters.length; t++){
	        
	        Iterator ii = clusters[t].getALNodes().iterator();
	        System.out.println("cluster "+t);
	        int x=1;
	        while(ii.hasNext()){
	        	CyNode nn = inputNetwork.getNode((Long)ii.next());
	        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
	        	x++;
	        }
	        }
	        
	        
	        
	        alClusters1 = OSMerge(alClusters1, params.getOverlappingScore());
	        System.out.println(alClusters1.size()+"@@@@@@");
	    	//Finally convert the arraylist into a fixed array
	        clusters = new Cluster[alClusters1.size()];
	        for (int c = 0; c < clusters.length; c++) {
	            clusters[c] = (Cluster) alClusters1.get(c);
	        }
	        msTimeAfter = System.currentTimeMillis();
	        lastFindTime = msTimeAfter - msTimeBefore;
	        
	        for(int t=0; t<clusters.length; t++){
	        
	        Iterator ii = clusters[t].getALNodes().iterator();
	        System.out.println("cluster "+t+"*******");
	        int x=1;
	        while(ii.hasNext()){
	        	CyNode nn = inputNetwork.getNode((Long)ii.next());
	        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
	        	x++;
	        }
	        }
	       
	        alClusters1 = ModuleMerge(alClusters1, params.getfThreshold(), inputNetwork);
	        System.out.println(alClusters1.size()+"@@@@@@");
	    	//Finally convert the arraylist into a fixed array
	        clusters = new Cluster[alClusters1.size()];
	        for (int c = 0; c < clusters.length; c++) {
	            clusters[c] = (Cluster) alClusters1.get(c);
	        }
	        msTimeAfter = System.currentTimeMillis();
	        lastFindTime = msTimeAfter - msTimeBefore;
	        
	        for(int t=0; t<clusters.length; t++){
	        
	        Iterator ii = clusters[t].getALNodes().iterator();
	        System.out.println("cluster "+t+"*******");
	        int x=1;
	        while(ii.hasNext()){
	        	CyNode nn = inputNetwork.getNode((Long)ii.next());
	        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
	        	x++;
	        }
	        }
	        
	   */      
	        
	        
	        ArrayList alClusters = new ArrayList();
	        Iterator it=alOriginalClusters.iterator();
	        while(it.hasNext()){
	        	Cluster cluster=(Cluster)it.next();
	       // 	if(cluster.getALNodes().size()>=params.getComplexSizeThreshold()){
	        		ArrayList<Long> alNodes=cluster.getALNodes();
	        		ClusterGraph gpCluster = this.createClusterGraph(alNodes, inputNetwork);
	        		//cluster.setComplexID(counter++);
	        		cluster.setGraph(gpCluster);
	        //		cluster.setClusterScore(0.0);
	        		cluster.setSeedNode(alNodes.get(0));
	        		cluster.setResultTitle(resultTitle);
	        //		int ind=cluster.getInDegree();
	        //		int outd=cluster.getTotalDegree()-2*ind;
	        //		if(ind!=0 && outd!=0)
	        //			cluster.setModularity((double)ind/(double)outd);
	        //		else
	        //    		cluster.calModularity(inputNetwork);
	        		alClusters.add(cluster);
	   //     	}
	        }
	        //Once the clusters have been found we either return them or in the case of selection scope,
	        //we select only the ones that contain the selected node(s) and return those
	        ArrayList selectedALClusters = new ArrayList();
	        if (!params.getScope().equals(ParameterSet.NETWORK)) {
	            for (Iterator ic = alClusters.iterator(); ic.hasNext();){
	                Cluster cluster = (Cluster) ic.next();
	                ArrayList alNodes = cluster.getALNodes();
	                ArrayList alSelectedNodes = new ArrayList();
	                for (int c = 0; c < params.getSelectedNodes().length; c++) {
	                    alSelectedNodes.add(params.getSelectedNodes()[c]);
	                }
	                //method for returning all clusters that contain any of the selected nodes
	                boolean hit = false;
	                for (Iterator in = alSelectedNodes.iterator(); in.hasNext();) {
	                    if (alNodes.contains((Long) in.next())) {
	                        hit = true;
	                    }
	                }
	                if (hit) {
	                    selectedALClusters.add(cluster);
	                }
	            }
	            alClusters = selectedALClusters;
	        }
	        
	        Cluster[] clusters = new Cluster[alClusters.size()];
	        for (int c = 0; c < clusters.length; c++) {
	            clusters[c] = (Cluster) alClusters.get(c);
	        }
	        long msTimeAfter = System.currentTimeMillis();
	        lastFindTime = msTimeAfter - msTimeBefore;
	        
	        for(int t=0; t<clusters.length; t++){
	        
	        Iterator ii = clusters[t].getALNodes().iterator();
	        System.out.println("cluster "+t);
	        int x=1;
	        while(ii.hasNext()){
	        	CyNode nn = inputNetwork.getNode((Long)ii.next());
	        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
	        	x++;
	        }
	        }
	        System.out.println("********step2***********"); 
	        
	        params.setAlgorithm("OHPIN");
	        return clusters;
	        
	        
	        
	        
	        
	        
	 }
	
	
	@Override
	public Cluster[] run(CyNetwork inputNetwork, int resultTitle){
 		return(this.OHPINFinder(inputNetwork, resultTitle));
	}
}
