package org.cytoscape.CytoCluster.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

import org.cytoscape.CytoCluster.internal.Cluster;
import org.cytoscape.CytoCluster.internal.ClusterGraph;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.ParameterSet;
import org.cytoscape.CytoCluster.internal.algorithm.Algorithm.NodeInfo;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;



class Node{
	Long suid;
	int weight;
	int degree;
	String name;
	Node(Long suid){
		this.suid = suid;
	}
}

class SortNodesComparator implements Comparator {
	@Override
	public int compare( Object a, Object b ){
		Node i = (Node)a;
		Node j = (Node)b;
        if(i.weight > j.weight )
            return -1;
        
       if((i.weight == j.weight)&&(i.name.compareTo(j.name) < 0))
        	return -1;
        return 1;
 /*       else if(j.weight > i.weight  ){
            return 1;
        }
        else {
        	
        	if(i.degree > j.degree ){
                return -1;
            }
            else if(j.degree > i.degree  ){
                return 1;
            }
            else {
            	return 0;
            }         	
        }*/
    }	
}	






public class IPCA extends Algorithm{
	boolean[] marked;
	int[] route;
	Stack Q;	
	int sp;
	double tin;
	

	public IPCA(Long networkID,ClusterUtil clusterUtil){
		super(networkID, clusterUtil);
	}
	

	
	
/*****************Step 1************************/	
public ArrayList<Node> calNodeWeight(){
	
	ArrayList<Node> sortedn = new ArrayList<Node>();
	
	
	Iterator nodes=currentNetwork.getNodeList().iterator(); 
	while(nodes.hasNext()){
		CyNode cn =(CyNode)nodes.next();
		Node n = new Node(cn.getSUID());
		n.name = currentNetwork.getRow(cn).get("name", String.class);
		n.degree = super.getNodeDegree(currentNetwork, cn.getSUID());
		
		
		/***calculate the weight of node*********/
		int weight = 0;
		Iterator adje = currentNetwork.getAdjacentEdgeList(cn, Type.ANY).iterator();
		while(adje.hasNext()){
			CyEdge e = (CyEdge) adje.next();
			int ew = super.getCommonNeighbors(e.getSource().getSUID(), e.getTarget().getSUID()).size();//weight of edge
			weight += ew;
		}
		
		n.weight = weight;
		
		sortedn.add(n);		
	}
	
	Collections.sort(sortedn, new SortNodesComparator());
	
	Iterator r = sortedn.iterator();
	int x = 1;
	while(r.hasNext()){
		Node t = (Node) r.next();
		System.out.println(x+"  "+t.weight+"  "+t.name);
		x++;
	}
	
	
	return sortedn;	
	
}       	
        	
 /*************************Step 2**********************************/       	
public ArrayList<Cluster> SelectedSeed(ArrayList<Node> sortedn){
	ArrayList<Cluster> alcluster = new ArrayList<Cluster>();
	int i = 0;
	while (!sortedn.isEmpty()){
	
		/*select the topest one to  be seed */     
		
		Cluster newCluster = new Cluster(i);	
		newCluster.addnode(sortedn.remove(0).suid);
		ExtendingCluster(newCluster,0.5);
			
		/************remove all vertices in the cluster from the queue pq*******************/
		Iterator n = newCluster.getALNodes().iterator();		
		while(n.hasNext()){
			long s = (Long) n.next(); 
			
			for(int j = 0; j<sortedn.size();)
				if(sortedn.get(j).suid == s)
					sortedn.remove(j);
				else
					j++;
		}		
		
		Collections.sort(sortedn,new SortNodesComparator());
		i++;
		alcluster.add(newCluster);		
	}
	return alcluster;
	
	
	
	
}
    


/***************step 3**********************/


public ArrayList<Map.Entry> getNeighborsMap(Cluster k){
	TreeMap<Long, Double> neighborsmap = new TreeMap<Long, Double>();
	Iterator<Long> i = k.getALNodes().iterator();
	while(i.hasNext()){
		Iterator<Long> j = getNeighbors(i.next()).iterator(); 
		while(j.hasNext()){
			Long curn = j.next();
			if( !k.getALNodes().contains(curn) ){
				if(!neighborsmap.containsKey(curn))
					neighborsmap.put(curn, 1.0);
				else
					neighborsmap.put(curn, neighborsmap.get(curn) + 1.0);
				}	
			}
		}			
	
	/*****sorted by value*****/
	ArrayList a = new ArrayList(neighborsmap.entrySet());
	Collections.sort(a, new Comparator() {
		public int compare(Object o1, Object o2) {
			Map.Entry obj1 = (Map.Entry) o1;
			Map.Entry obj2 = (Map.Entry) o2;
			if((Double)obj1.getValue() > (Double)obj2.getValue())
				return -1;
			else 
				return 0;			
		}
	});
	
	return a;
}

	public void ExtendingCluster(Cluster k, double tinthreshold){
		ArrayList<Map.Entry> neighborsmap = getNeighborsMap(k);
		
		while(!neighborsmap.isEmpty()){
			Map.Entry node = neighborsmap.remove(0);
			double nodesnum = k.getALNodes().size();
			if((Double)node.getValue() > tinthreshold * nodesnum )
				if( SPJudgement( k, (Long)node.getKey()) ){
					k.addnode((Long)node.getKey());
					ExtendingCluster(k, tinthreshold);
				}
			
		}		
	
	}

/***************step 4**********************/

	public boolean SPJudgement(Cluster k, Long suid){
		Cluster t = k;
		t.addnode(suid);
		marked = new boolean[t.getALNodes().size()] ;
		route = new int[t.getALNodes().size()];
		Q = new Stack();	
		
		Q.push(t.getALNodes().get(0));
		route[0] = 0;
		while(!Q.isEmpty()){
			Long n = (Long) Q.pop();
			Iterator<CyNode> adjn = currentNetwork.getNeighborList(currentNetwork.getNode(n), Type.ANY).iterator();
			while(adjn.hasNext()){
				Long a = adjn.next().getSUID();
				if(t.getALNodes().contains(a) ){
					int i = t.getALNodes().indexOf(a);
					if(marked[i] == false){
						route[i] = route[t.getALNodes().indexOf(n)]+1;
						marked[i] = true;
						Q.push(a);
					}
						
				}
			}		
		}
		
		int maxroute = 0;
		for(int j=0; j<route.length; j++)
		{
			if(maxroute<route[j])
				maxroute=route[j];
		}
			
		if(maxroute <= sp)	
			return true;
		else 
			return false;
	
	
	}
	
	
	public void dfs(Cluster t, Long suid){
		
		
	}

  
   public Cluster[] IPCAFinder(CyNetwork inputNetwork,int resultTitle){
        String callerID = "Algorithm.IPCAFinder";
    	System.out.println("In "+callerID);
    	params=getParams();
    	currentNetwork=inputNetwork;
    	ArrayList NodeWeight = calNodeWeight();   	
    	calNodeInfos(inputNetwork);
    	
    	System.out.println("&&&&&&&&&&&&&&&&1");
        if (curNodeInfos==null) {
            System.err.println("In " + callerID + ": nodeInfos Map or edgeWeights Map was null.");
            return (null);
        }
        long msTimeBefore = System.currentTimeMillis();
        int findingProgress = 0;
        int findingTotal = 0;
       // Collection values = curEdgeWeights.values(); //returns a Collection sorted by key order (descending)
        
               findingTotal = NodeWeight.size();
            
        
        //stores the list of clusters as ArrayLists of node indices in the input Network
        
        
        System.out.println("&&&&&&&&&&&&&&&2");
        ArrayList alOriginalClusters = new ArrayList(inputNetwork.getNodeCount());
        System.out.println("&&&&&&&&&&&&&&&&3");
        alOriginalClusters = SelectedSeed(NodeWeight);
        
        System.out.println("&&&&&&&&&&&&&&&&4");
        /**********************************************************************************************
			Then, Operation UNION:	according to different situation, in which the two nodes consisting 
				this arc may belong to different Complexes or an identical Complex and that the 
				attributes of the Complexes varies, we take on different action 
         ***********************************************************************************************/
       
        ArrayList alClusters = new ArrayList();
        Iterator it=alOriginalClusters.iterator();
        while(it.hasNext()){
        	Cluster cluster=(Cluster)it.next();
        	if(cluster.getALNodes().size()>=params.getComplexSizeThresholdIPCA()){
        		ArrayList<Long> alNodes=cluster.getALNodes();
        		ClusterGraph gpCluster = this.createClusterGraph(alNodes, inputNetwork);
        		//cluster.setComplexID(counter++);
        		cluster.setGraph(gpCluster);
        		cluster.setResultTitle(resultTitle);
        		alClusters.add(cluster);
        	}
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
        //Finally convert the arraylist into a fixed array
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
        params.setAlgorithm("IPCA");
        return clusters;
       	
   }

   
    
    @Override
	public Cluster[] run(CyNetwork inputNetwork, int resultTitle){
    	
    //	this.inputNetwork = inputNetwork;
    	if(inputNetwork == null)
    		System.out.println("hahah");
    	currentNetwork = inputNetwork;
    	this.sp = getParams().getShortestPathLength();
    	this.tin = getParams().getTinThreshold();
 		return(this.IPCAFinder(inputNetwork, resultTitle));
	}

}

