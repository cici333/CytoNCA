package org.cytoscape.myappViz.internal.algorithm;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.myappViz.internal.Clique;
import org.cytoscape.myappViz.internal.Cluster;
import org.cytoscape.myappViz.internal.ClusterGraph;
import org.cytoscape.myappViz.internal.ClusterUtil;
import org.cytoscape.myappViz.internal.ParameterSet;
import org.cytoscape.myappViz.internal.algorithm.Algorithm.NodeInfo;

import java.util.*;



/**
 * An implementation of the algorithm
 */
public abstract class Algorithm {
    protected boolean cancelled = false;//If set, will schedule the canceled algorithm  at the next convenient opportunity
    protected TaskMonitor taskMonitor = null;
    protected ParameterSet params;   //the parameters used for this instance of the algorithm
    protected CyNetwork currentNetwork;
    //states
    protected long lastScoreTime;	//the time taken by the last score operation
    protected long lastFindTime;	//the time taken by the last find operation
    protected long findCliquesTime=0;//time used to find maximal cliques
    
	//data structures useful to storing information for more than one cluster finding iteration
    protected HashMap<Long, NodeInfo> curNodeInfos = null;    //vector<Node> key is the node index, value is a NodeInfo instance    
    protected TreeMap<Double, List<Long>> curNodeScores = null; //key is node score, value is nodeIndex
    protected TreeMap curEdgeWeights=null;	//key is edge index,value is EdgeInfo instance
    protected HashMap curCliques=null;		//key is clique ID,value is Clique instance
    protected ArrayList curOptimalDivision=null;
    
    protected HashMap<Integer, Map<Long, NodeInfo>> nodeInfoResultsMap = new HashMap<Integer, Map<Long, NodeInfo>>(); //key is result, value is nodeInfroHashMap
    protected HashMap<Integer, SortedMap<Double, List<Long>>> nodeScoreResultsMap = new HashMap<Integer, SortedMap<Double, List<Long>>>();//key is result, value is nodeScoreSortedMap

    protected HashMap maximalCliquesNetworkMap=new HashMap();	//key is networkID, value is maximal Cliques
    protected HashMap edgeWeightNetworkMap=new HashMap();
    protected HashMap optimalDivisionKeyMap=new HashMap();
    
    private final ClusterUtil clusterUtil;
    
    //data structure for storing information required for each node
    protected class NodeInfo {
        double density;         //neighborhood density
        Long[] nodeNeighbors;    //stores node indices of all neighbors
        int numNodeNeighbors;	//the number of neighbors
        int coreLevel;          //e.g. 2 = a 2-core
        double coreDensity;     //density of the core neighborhood
        double score;           //node score
        int iComplex;
        ArrayList alComplex=new ArrayList();
        public NodeInfo() {
            this.density = 0.0;
            this.coreLevel = 0;
            this.coreDensity = 0.0;
    		this.iComplex=-1;
    		if(!alComplex.isEmpty())
    			alComplex.clear();
        }
        public void setComplex(int index){
        	iComplex=index;
        }
		public ArrayList getAlComplex() {
			return alComplex;
		}
		public void setAlComplex(ArrayList alComplex) {
			this.alComplex = alComplex;
 		}
    }
     
    /**
     * The constructor.
     *
     * @param networkID the algorithm use it to get the parameters of the focused network
     */
	public Algorithm(Long networkID, ClusterUtil clusterUtil) {
		
		this.clusterUtil = clusterUtil;
		this.params = this.clusterUtil.getCurrentParameters().getParamsCopy(networkID);
    }
    //This method is used in AnalyzeTask
    public void setTaskMonitor(TaskMonitor taskMonitor,long  networkID) {
    	this.params = this.clusterUtil.getCurrentParameters().getParamsCopy(networkID);
        this.taskMonitor = taskMonitor;
    }
    public long getLastScoreTime() {
        return lastScoreTime;
    }
    public long getLastFindTime() {
        return lastFindTime;
    }
    public ParameterSet getParams() {
        return params;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }    
    public long getFindCliquesTIme() {
		return findCliquesTime;
	}
    
        /**
     * Bron-Kerbosch Algorithm for finding all the maximal cliques
     * 
     * @param cur stands for the currently growing clique
     * @param cand the candidate nodes to be added, they are common neighbors of nodes in cur
     * @param not nodes already processed, so there is no common elements between cand and not
     */
    protected void expand(HashMap cliques,Vector curr, Vector cand, 
    		Vector not){
    	if(cand.isEmpty() && not.isEmpty()){//the expanding process has come to an end
    		int num=cliques.size();
    		Clique pc=new Clique(num);	//the node in cur can form a new maximal clique
    		ArrayList alNodes=new ArrayList();
    		Iterator it=curr.iterator();
    		while(it.hasNext()){
    			Long node=(Long)it.next();
    			alNodes.add(node);
    		}
    		pc.setCliqueNodes(alNodes);
    		cliques.put(new Integer(pc.getCliuqueID()), pc);	//add to the maximal clique list
    	}	
    	else
    	{
    		Long p;
    		int i;
    		Long q;
    		Vector candq;
    		Vector notq;
    		Vector cutcand=new Vector((Vector)cand.clone());
    		p=getPivot(cand,not);	//get the index of the pivot node 
    		ArrayList cuts=getNeighbors(p);
    		for(Iterator it=cuts.iterator();it.hasNext();)//get the trimmed candidates
    			cutcand.remove((Long)it.next());
    		for(i=0;i<cutcand.size();++i)//for each non-adjacent node
    		{
    			q=(Long)cutcand.get(i);
    			cand.remove(q);	//remove from candidate list 
    			curr.add(q);	//add the expanded node
    			ArrayList adjs=getNeighbors(q.longValue());//1.2 get the adjacent
    			candq=getIntersect( cand,adjs );//2.1 get insertion
    			notq=getIntersect(not,adjs);
    			expand(cliques,curr,candq,notq);//2.3 recursive process
    			curr.remove(curr.lastElement());//pop the top element a new cursive process
                if (cancelled) {
                    break;
                }
    		}
    		//TODO: here we need to set the monitor progress
    		/*
            if (taskMonitor != null) {
                i++;
                taskMonitor.setProgress((i * 100) / inputNetwork.getNodeCount());
            }*/
    	}
    }
    /**
     * Choose a vertex from cand&not with the largest number of connections to the vertices in cand
     */
    private Long getPivot(Vector cand, Vector not){
    	Long ret,nodeIndex;
    	
    	int most=0;
    	int intNum,i;
    	
    	ret= new Long(-1);
    	ArrayList neighbors;
    	//TODO: here 
    	//if(cand.size()==1)	//if there is only one node in cand, then we simply choose it
    		//return ((Integer)cand.get(0)).intValue();
    	for(i=0;i<cand.size();++i){
    		nodeIndex=((Long)(cand.get(i))).longValue();
    		neighbors=getNeighbors(nodeIndex);
    		intNum=0;
    		for(Iterator it=neighbors.iterator();it.hasNext();){
    			//get the number of intersection between cand and neighbors[i];
    			Long e = (Long)it.next();
    			if(cand.contains(e))
    				intNum++;
    		}
    		if(intNum>=most){
    			most=intNum;
    			ret=nodeIndex;
    		}
    	}
    	for(i=0;i<not.size();++i){
    		nodeIndex=((Long)not.get(i)).longValue();
    		neighbors=getNeighbors(nodeIndex);
    		intNum=0;
    		for(Iterator it=neighbors.iterator();it.hasNext();){
    			Long e=(Long)it.next();
    			if(cand.contains(e))
    				intNum++;
    		}
    		if(intNum>=most){
    			most=intNum;
    			ret=nodeIndex;
    		}
    	}
    	return ret;
    }
    protected ArrayList getNeighbors(Long nodeIndex){
    	
    	
    	ArrayList ret=new ArrayList();
    	for (CyNode node : currentNetwork.getNeighborList(currentNetwork.getNode(nodeIndex), CyEdge.Type.ANY)) {
			if(node.getSUID() != nodeIndex)
				ret.add(node.getSUID());
		}
    	
    	return ret;
    	
/*    	ArrayList ret=new ArrayList();
    	int[] o=currentNetwork.neighborsArray(nodeIndex);
    	for(int i=0;i<o.length;++i){
    		if(o[i]!=nodeIndex)
    			ret.add(new Integer(o[i]));
    	}
    	return ret;*/
    }
    static public Long[] getNeighborArray(CyNetwork network, Long nodeIndex){
    	
    	
    	ArrayList ret=new ArrayList();
    	for (CyNode node : network.getNeighborList(network.getNode(nodeIndex), CyEdge.Type.ANY)) {
			ret.add(node.getSUID());
		}
    	Long[] neighbors=ClusterUtil.convertIntArrayList2array(ret);
    	return neighbors;

/*    	
    	ArrayList ret=new ArrayList();
    	int[] o=network.neighborsArray(nodeIndex);
    	for(int i=0;i<o.length;++i){
    		if(o[i]!=nodeIndex)
    			ret.add(new Integer(o[i]));
    	}
    	int[] neighbors=ClusterUtil.convertIntArrayList2array(ret);
    	return neighbors;*/
    }
    private Vector getIntersect(Vector a,ArrayList b){
    	Vector r=new Vector();
    	for(Iterator i=a.iterator();i.hasNext();){
    		Long n=(Long)i.next();
    		if(b.contains(n))
    			r.add(n);
    	}
    	return r;
    }
    
    
    
    private boolean different(ArrayList a, ArrayList b){
    	if(a.size()!=b.size()) return true;
    	Iterator it=a.iterator();
    	while(it.hasNext()){
    		Long e=(Long)it.next();
    		if(!b.contains(e))
    			return true;
    	}
    	return false;
    }

	protected int searchInComplexes(ArrayList alClusters, Long node){
    	int counter=0;
    	Iterator it=alClusters.iterator();
    	while(it.hasNext()){
    		Cluster cur=(Cluster) it.next();
    		ArrayList alNodes=cur.getALNodes();
    		if(alNodes.contains(node))
    			counter++;
    	}
    	return counter;
    }

    /**
     * calculate the similarities between a pairs of complexes
     *   S=1/2m*( sumof(Aij-ki*kj/2m) ) of which i IEO C1,j IEO C2, and i!=j
     */
    protected double calSimilarity(Cluster c1,Cluster c2){
    	double S=0,temp;
    	int A,degree1,degree2;
    	Long[] neigh;
    	int m=currentNetwork.getEdgeCount();
    	ArrayList nodes1=c1.getALNodes();
    	ArrayList nodes2=c2.getALNodes();
    	for(Iterator it1=nodes1.iterator();it1.hasNext();){
    		Long n1=(Long)it1.next();
    		neigh=getNeighborArray( currentNetwork,n1 );
    		degree1=neigh.length;
    		Arrays.sort(neigh);
    		for(Iterator it2=nodes2.iterator();it2.hasNext();){
    			Long n2=(Long)it2.next();
    			if(n1.longValue()!=n2.longValue()){//n1 and n2 is not the same node
    			
    				degree2 =getNodeDegree(currentNetwork,n2);

        			//degree2=currentNetwork.getDegree(n2.intValue());
        			A=( Arrays.binarySearch(neigh, n2.longValue() )<0 )? 0:1;
        			S+=A;
        			temp=degree1*degree2;        			
        			S-=temp/2/m;
    			}
    		}
    	}
    	//S=S/2/m;
		return S;
    }
    /**
     * evaluate the quality of the resulting complexes,by which we decide which division is optimal
     *    EQ=1/2m*(sumof( (Aij-ki*kj/2m)/(Oi*Oj) ))	of which i,j IEO Cx
     * @param alClusters all the clusters at present
     */
    protected double calModularity(ArrayList alClusters){
    	double M=0.0,temp;
    	int A,i,j;
    	Long n1,n2;
    	int d1,d2,c1,c2;
    	Long[] neighs;
    	int m=currentNetwork.getEdgeCount();
    	Iterator i0=alClusters.iterator();
    	while(i0.hasNext()){//for each Complex
    		Cluster cur=(Cluster)i0.next();
    		ArrayList alNodes=cur.getALNodes();
    		Long[] nodes=ClusterUtil.convertIntArrayList2array(alNodes);
    		for(i=0;i<nodes.length-1;i++){//for each pairs of nodes
				n1=nodes[i];
				neighs=getNeighborArray(currentNetwork,n1);
				Arrays.sort(neighs);
				d1=neighs.length;	
				c1=searchInComplexes(alClusters,new Long(n1));
    			for(j=0;j<nodes.length;j++){
    				n2=nodes[j];
    				d2 =getNodeDegree(currentNetwork,n2);
        			//d2=currentNetwork.getDegree(n2);
        			c2=searchInComplexes(alClusters,new Long(n2));
        			A=( Arrays.binarySearch(neighs, n2 )<0 )? 0:1;
        			temp=(d1*d2);
        			temp=temp/2/m;
        			temp=-temp;
        			temp+=A;
        			M+=temp/c1/c2;    				
    			}
    		}
    	}
    	//M=M/2/m;
    	return M;    	
    }
    
    /**
     * K-Core Algorithm Step 1: 
     * Score the graph and save scores as node attributes.  Scores are also
     * saved internally in your instance of Algorithm.
     *
     * @param inputNetwork The network that will be scored
     * @param resultTitle Title of the result, used as an identifier in various hash maps
     */
    public void scoreGraph(CyNetwork inputNetwork, int resultTitle) {
        params = getParams();
        String callerID = "Algorithm.scorGraph";
        if (inputNetwork == null) {
            System.err.println("In " + callerID + ": inputNetwork was null.");
            return;
        }
        
        long msTimeBefore = System.currentTimeMillis();
        HashMap<Long, NodeInfo> nodeInfoHashMap = new HashMap<Long, NodeInfo>(inputNetwork.getNodeCount());
        TreeMap nodeScoreSortedMap = new TreeMap(new Comparator() { //will store Doubles (score) as the key, Lists as values
            //sort Doubles in descending order
            public int compare(Object o1, Object o2) {
                double d1 = ((Double) o1).doubleValue();
                double d2 = ((Double) o2).doubleValue();
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        //iterate over all nodes and calculate node score
        NodeInfo nodeInfo = null;
        double nodeScore;
        ArrayList<Long> al;
        int i = 0;
        

		Iterator<CyNode> nodes = inputNetwork.getNodeList().iterator();
	        while (nodes.hasNext() && (!cancelled)) {
	            CyNode n = (CyNode) nodes.next();
	            
	            nodeInfo = calcNodeInfo(inputNetwork, n.getSUID());
	            nodeInfoHashMap.put(n.getSUID(), nodeInfo);
	            //nodeInfoHashMap.put(new Integer(n.getRootGraphIndex()), nodeInfo);
	            //score node TODO: add support for other scoring functions (low priority)
	            nodeScore = scoreNode(nodeInfo);
	            if (nodeScoreSortedMap.containsKey(new Double(nodeScore))) {
	                al = (ArrayList) nodeScoreSortedMap.get(new Double(nodeScore));
	                al.add(n.getSUID());
	                //al.add(new Integer(n.getRootGraphIndex()));
	            } else {
	                al = new ArrayList<Long>();
	                al.add(n.getSUID());
	                //al.add(new Integer(n.getRootGraphIndex()));
	                nodeScoreSortedMap.put(new Double(nodeScore), al);
	            }
	            if (taskMonitor != null) {
	                i++;
	                taskMonitor.setProgress((i * 100) / inputNetwork.getNodeCount());
	            }
	        }
		
		
		
        /*Iterator nodes = inputNetwork.nodesIterator();
        while (nodes.hasNext() && (!cancelled)) {
            CyNode n = (CyNode) nodes.next();
            nodeInfo = calcNodeInfo(inputNetwork, n.getRootGraphIndex());
            nodeInfoHashMap.put(new Integer(n.getRootGraphIndex()), nodeInfo);
            //score node TODO: add support for other scoring functions (low priority)
            nodeScore = scoreNode(nodeInfo);
            if (nodeScoreSortedMap.containsKey(new Double(nodeScore))) {
                al = (ArrayList) nodeScoreSortedMap.get(new Double(nodeScore));
                al.add(new Integer(n.getRootGraphIndex()));
            } else {
                al = new ArrayList();
                al.add(new Integer(n.getRootGraphIndex()));
                nodeScoreSortedMap.put(new Double(nodeScore), al);
            }
            if (taskMonitor != null) {
                i++;
                taskMonitor.setProgress((i * 100) / inputNetwork.getNodeCount());
            }
        }*/
        nodeScoreResultsMap.put(resultTitle, nodeScoreSortedMap);
        nodeInfoResultsMap.put(resultTitle, nodeInfoHashMap);
        curNodeScores = nodeScoreSortedMap;
        curNodeInfos = nodeInfoHashMap;

        long msTimeAfter = System.currentTimeMillis();
        lastScoreTime = msTimeAfter - msTimeBefore;
    }
    /**
     * K-Core Algorithm Step 2: 
     * Find all complexes given a scored graph.  If the input network has not been scored,
     * this method will return null.  This method is called when the user selects network scope or
     * single node scope.
     *
     * @param inputNetwork The scored network to find clusters in.
     * @param resultTitle Title of the result
     * @return An array containing an Cluster object for each cluster.
     */
    public Cluster[] K_CoreFinder(CyNetwork inputNetwork, int resultTitle) {
        String callerID = "Algorithm.K_CliqueFinder";
    	System.out.println("In"+callerID);
        TreeMap nodeScoreSortedMap;
        HashMap nodeInfoHashMap;
        if (!nodeScoreResultsMap.containsKey(resultTitle)) {//use the node score used last time
            nodeScoreSortedMap = curNodeScores;
            nodeInfoHashMap = curNodeInfos;            
            nodeScoreResultsMap.put(resultTitle, nodeScoreSortedMap);
            nodeInfoResultsMap.put(resultTitle, nodeInfoHashMap);
        } else {//the scoring parameters haven't changed
            nodeScoreSortedMap = (TreeMap) nodeScoreResultsMap.get(resultTitle);
            nodeInfoHashMap = (HashMap) nodeInfoResultsMap.get(resultTitle);
        }
        params = getParams();
        Cluster currentCluster;
        if (inputNetwork == null) {
            System.err.println("In " + callerID + ": inputNetwork was null.");
            return (null);
        }
        if ((nodeInfoHashMap == null) || (nodeScoreSortedMap == null)) {
            System.err.println("In " + callerID + ": nodeInfoHashMap or nodeScoreSortedMap was null.");
            return (null);
        }

        //initialization
        long msTimeBefore = System.currentTimeMillis();
        HashMap nodeSeenHashMap = new HashMap(); //key is nodeIndex, value is true/false
        Long currentNode;
        int findingProgress = 0;
        int findingTotal = 0;
        Collection values = nodeScoreSortedMap.values(); //returns a Collection sorted by key order (descending)
        for (Iterator iterator1 = values.iterator(); iterator1.hasNext();) {
            ArrayList value = (ArrayList) iterator1.next();
            for(Iterator iterator2 = value.iterator(); iterator2.hasNext();) {
                iterator2.next();
                findingTotal++;
            }
        }
        //stores the list of clusters as ArrayLists of node indices in the input Network
        ArrayList alClusters = new ArrayList();
        ArrayList alNodesWithSameScore;                                                                                                                            
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            //each score may be associated with multiple nodes, iterate over these lists
            alNodesWithSameScore = (ArrayList) iterator.next();
            for (int j = 0; j < alNodesWithSameScore.size(); j++) {
                currentNode = (Long) alNodesWithSameScore.get(j);
                if (!nodeSeenHashMap.containsKey(currentNode)) {
                    currentCluster = new Cluster();
                    currentCluster.setSeedNode(currentNode);//store the current node as the seed node
                    //we store the current node seen hash map for later exploration purposes
                    HashMap nodeSeenHashMapSnapShot = new HashMap((HashMap)nodeSeenHashMap.clone());
                    ArrayList alNodes = getClusterCore(currentNode, nodeSeenHashMap, params.getNodeScoreCutoff(), params.getMaxDepthFromStart(), nodeInfoHashMap);//here we use the original node score cutoff
                    if (alNodes.size() > 0) {
                        //make sure seed node is part of cluster, if not already in there
                        if (!alNodes.contains(currentNode)) {
                            alNodes.add(currentNode);
                        }
                        //create an input graph for the filter and haircut methods
                        
                        ClusterGraph clusterGraph = createClusterGraph(alNodes, inputNetwork);

                        if (!filterCluster(clusterGraph)) {
                          if (this.params.isHaircut()) {
                            haircutCluster(clusterGraph, alNodes);
                          }
                          if (this.params.isFluff()) {
                            fluffClusterBoundary(alNodes, nodeSeenHashMap, nodeInfoHashMap);
                          }
                          currentCluster.setALNodes(alNodes);
                          clusterGraph = createClusterGraph(alNodes, inputNetwork);
                          
                          currentCluster.setGraph(clusterGraph);
                          currentCluster.setClusterScore(scoreCluster(clusterGraph));
//                        //store all the nodes that have already been seen and incorporated in other clusters
                          currentCluster.setNodeSeenHashMap(nodeSeenHashMapSnapShot);
                          currentCluster.setResultTitle(resultTitle);
                          //store detected cluster for later
                          alClusters.add(currentCluster);
                        }
                        
                        
                        /*GraphPerspective gpCluster = Algorithm.createGraphPerspective(alNodes, inputNetwork);
                        if (!filterCluster(gpCluster)) {//only do this when the cluster need not filter
                            if (params.isHaircut()) {
                                haircutCluster(gpCluster, alNodes, inputNetwork);
                            }
                            if (params.isFluff()) {
                                fluffClusterBoundary(alNodes, nodeSeenHashMap, nodeInfoHashMap);
                            }
                            currentCluster.setALNodes(alNodes);;
                            gpCluster = Algorithm.createGraphPerspective(alNodes, inputNetwork);
                            currentCluster.setGPCluster(gpCluster);
                            currentCluster.setClusterScore(scoreCluster(currentCluster));
//                          //store all the nodes that have already been seen and incorporated in other clusters
                            currentCluster.setNodeSeenHashMap(nodeSeenHashMapSnapShot);
                            currentCluster.setResultTitle(resultTitle);
                            //store detected cluster for later
                            alClusters.add(currentCluster);
                        }*/
                    }
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
        }
        //Once the clusters have been found we either return them or in the case of selection scope,
        //we select only the ones that contain the selected node(s) and return those
        for(Iterator it=alClusters.iterator();it.hasNext();){
        	Cluster cluster=(Cluster)it.next();
    		cluster.calModularity(inputNetwork);
        }
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
        return clusters;
    }

    protected void calNodeInfos(CyNetwork net){
		HashMap nodeInfos=new HashMap();
		Iterator nodes=net.getNodeList().iterator();
		while(nodes.hasNext() && (!cancelled)){
			NodeInfo nodeInfo=new NodeInfo();
			Long node=((CyNode)nodes.next()).getSUID();
			ArrayList adjs=getNeighbors(node);
			nodeInfo.nodeNeighbors=ClusterUtil.convertIntArrayList2array(adjs);
			nodeInfos.put(new Long(node), nodeInfo);
		}
		curNodeInfos=nodeInfos;
    }
    /**
     * Calculates node information for each node.
     * This information is used at the first stage of the K-Clique algorithm.
     * This is a utility function for the algorithm.
     *
     * @param inputNetwork The input network for reference
     * @param nodeIndex    The index of the node in the input network to score
     * @return A NodeInfo object containing node information required for the algorithm
     */
    private NodeInfo calcNodeInfo(CyNetwork inputNetwork, Long nodeIndex) {
        Long[] neighborhood;
        String callerID = "Algorithm.calcNodeInfo";
        if (inputNetwork == null) {
            System.err.println("In " + callerID + ": gpInputGraph was null.");
            return null;
        }

        //get neighborhood of this node (including the node)
        
        CyNode rootNode = inputNetwork.getNode(nodeIndex.longValue());
        List neighborsL = inputNetwork.getNeighborList(rootNode, CyEdge.Type.ANY);
        Long[] neighbors = getNeighborArray(inputNetwork,nodeIndex);
        if (neighbors.length < 2) {
            //if there are no neighbors or just one neighbor
            NodeInfo nodeInfo = new NodeInfo();
            if (neighbors.length == 1) {//only one neighbor
                nodeInfo.coreLevel = 1;
                nodeInfo.coreDensity = 1.0;
                nodeInfo.density = 1.0;
                //nodeInfo.numNodeNeighbors=2;//????
                //nodeInfo.nodeNeighbors=????
            }//else it is a isolated node
            return (nodeInfo);
        }        
        //add original node to extract complete neighborhood
        Arrays.sort(neighbors);
        if (Arrays.binarySearch(neighbors, nodeIndex) < 0) {//add itself as its neighbor
            neighborhood = new Long[neighbors.length + 1];
            System.arraycopy(neighbors, 0, neighborhood, 1, neighbors.length);
            neighborhood[0] = nodeIndex;
            neighborsL.add(rootNode);
        } else {
            neighborhood = neighbors;
        }
        //extract neighborhood subgraph
/*        Set nodes = new HashSet();
        for(int i=0;i<neighbors.length;i++) nodes.add((Long)neighbors[i]);*/
        
        ClusterGraph gpNodeNeighborhood = clusterUtil.createGraph(inputNetwork, neighborsL);
        
        
//        GraphPerspective gpNodeNeighborhood = inputNetwork.createGraphPerspective(neighborhood);
        if (gpNodeNeighborhood == null) {//this shouldn't happen
            System.err.println("In " + callerID + ": gpNodeNeighborhood was null.");
            return null;
        }
        //calculate the node information for each node
        NodeInfo nodeInfo = new NodeInfo();
        //density
        if (gpNodeNeighborhood != null) {
            nodeInfo.density = calcDensity(gpNodeNeighborhood, params.isIncludeLoops());
        }
        nodeInfo.numNodeNeighbors = neighborhood.length;
        //calculate the highest k-core
        
        
        
        Integer k = null;
        Object[] returnArray = getHighestKCore(gpNodeNeighborhood);
        k = (Integer)returnArray[0];
        ClusterGraph kCore = (ClusterGraph)returnArray[1];
        nodeInfo.coreLevel = k.intValue();

        if (kCore != null) {
          nodeInfo.coreDensity = calcDensity(kCore, this.params.isIncludeLoops());
        }
        
        /*GraphPerspective gpCore = null;
        Integer k = null;
        Object[] returnArray = getHighestKCore(gpNodeNeighborhood);
        k = (Integer) returnArray[0];
        gpCore = (GraphPerspective) returnArray[1];
        nodeInfo.coreLevel = k.intValue();
        //calculate the core density - amplifies the density of heavily interconnected regions and attenuates
        //that of less connected regions
        if (gpCore != null) {
            nodeInfo.coreDensity = calcDensity(gpCore, params.isIncludeLoops());
        }*/
        //record neighbor array for later use in cluster detection step
        nodeInfo.nodeNeighbors = neighborhood;
        return (nodeInfo);
    }
    /**
     * Score node using the formula from original paper.
     * This formula selects for larger, denser cores.
     * This is a utility function for the algorithm.
     *
     * @param nodeInfo The internal data structure to fill with node information
     * @return The score of this node.
     */
    
    private double scoreNode(NodeInfo nodeInfo)
    {
      if (nodeInfo.numNodeNeighbors > this.params.getDegreeCutoff())
        nodeInfo.score = (nodeInfo.coreDensity * nodeInfo.coreLevel);
      else {
        nodeInfo.score = 0.0D;
      }

      return nodeInfo.score;
    }
/*
    private double scoreNode(NodeInfo nodeInfo) {
        if (nodeInfo.numNodeNeighbors > params.getDegreeCutoff()) {
            nodeInfo.score = nodeInfo.coreDensity * (double) nodeInfo.coreLevel;
        } else {
            nodeInfo.score = 0.0;
        }
        return (nodeInfo.score);
    }*/
    /**
     * Gets the calculated node score of a node from a given result.  Used in ResultsPanel
     * during the attribute setting method.
     *
     * @param rootGraphIndex Integer which is used to identify the nodes in the score-sorted tree map
     * @param resultTitle Title of the results for which we are retrieving a node score
     * @return node score as a Double
     */
    

    public double getNodeScore(Long nodeId, int resultId)
    {
      Map nodeScoreSortedMap = (Map)this.nodeScoreResultsMap.get(Integer.valueOf(resultId));

      for (Iterator localIterator = nodeScoreSortedMap.keySet().iterator(); localIterator.hasNext(); ) { 
    	  double nodeScore = ((Double)localIterator.next()).doubleValue();
        List nodes = (List)nodeScoreSortedMap.get(Double.valueOf(nodeScore));

        if (nodes.contains(nodeId)) {
          return nodeScore;
        }
      }

      return 0.0D;
    }
    
/*    public Double getNodeScore(int rootGraphIndex, String resultTitle) {
        Double nodeScore = new Double(0.0);
        TreeMap nodeScoreSortedMap = (TreeMap) nodeScoreResultsMap.get(resultTitle);        
        for (Iterator score = nodeScoreSortedMap.keySet().iterator(); score.hasNext();) {
        	nodeScore = (Double) score.next();
            ArrayList nodes = (ArrayList) nodeScoreSortedMap.get(nodeScore);
            if (nodes.contains(new Integer(rootGraphIndex))) {
                return nodeScore;
            }
        }
        return nodeScore;
    }*/
    /**
     * Gets the highest node score in a given result.  Used in the VisualStyleAction class to
     * re-initialize the visual calculators.
     */
    public double getMaxScore(int resultTitle) {
        TreeMap nodeScoreSortedMap = (TreeMap) nodeScoreResultsMap.get(resultTitle);
        Double nodeScore = (Double) nodeScoreSortedMap.firstKey();
        return nodeScore.doubleValue();
    }
    /**
     * create graphPerspective for nodes in a cluster
     * @param alNode the nodes
     * @param inputNetwork the original network
     * @return the graph perspective created
     */
    public  ClusterGraph createClusterGraph(ArrayList<Long> alNode, CyNetwork inputNetwork) {
        //convert Integer array to int array
        /*int[] clusterArray = new int[alNode.size()];
        for (int i = 0; i < alNode.size(); i++) {
            int nodeIndex = ((Integer) alNode.get(i)).intValue();
            clusterArray[i] = nodeIndex;
        }*/
    	
    	Set nodes = new HashSet();

        for (Long id : alNode) {
          CyNode n = inputNetwork.getNode(id.longValue());
          nodes.add(n);
        }
        ClusterGraph clusterGraph = clusterUtil.createGraph(inputNetwork, nodes);

        //ClusterGraph gpCluster = inputNetwork.createGraphPerspective(clusterArray);
        return clusterGraph;
    }
    /**
     * Score a cluster.  Currently this ranks larger, denser clusters higher, although
     * in the future other scoring functions could be created
     *
     * @param cluster - The GINY GraphPerspective version of the cluster
     * @return The score of the cluster
     */
    public double scoreCluster(ClusterGraph cluster) {
        int numNodes = 0;
        double density = 0.0, score = 0.0;
        numNodes = cluster.getNodeCount();
        density = calcDensity(cluster, this.params.isIncludeLoops());
        score = density * numNodes;


        return (score);
    }
    /**
     * Find the high-scoring central region of the cluster.
     * This is a utility function for the algorithm.
     *
     * @param startNode       The node that is the seed of the cluster
     * @param nodeSeenHashMap The list of nodes seen already
     * @param nodeScoreCutoff Slider input used for cluster exploration
     * @param maxDepthFromStart Limits the number of recursions
     * @param nodeInfoHashMap Provides the node scores
     * @return A list of node IDs representing the core of the cluster
     */
    private ArrayList getClusterCore(Long seedNode, HashMap nodeSeenHashMap, double nodeScoreCutoff, int maxDepthFromStart, HashMap nodeInfoHashMap) {
        ArrayList cluster = new ArrayList(); //stores Integer nodeIndices
        getClusterCoreInternal(seedNode,((NodeInfo) nodeInfoHashMap.get(seedNode)).score, 
        		nodeSeenHashMap,1, cluster, nodeScoreCutoff, maxDepthFromStart, nodeInfoHashMap);
        return (cluster);
    }

    /**
     * An internal function that does the real work of getClusterCore, implemented to enable recursion.
     *
     * @param startNode         The node that is the seed of the cluster
     * @param nodeSeenHashMap   The list of nodes seen already
     * @param startNodeScore    The score of the seed node
     * @param currentDepth      The depth away from the seed node that we are currently at
     * @param cluster           The cluster to add to if we find a cluster node in this method
     * @param nodeScoreCutoff   Helps determine if the nodes being added are within the given threshold
     * @param maxDepthFromStart Limits the recursion
     * @param nodeInfoHashMap   Provides score info
     * @return true
     */
    private boolean getClusterCoreInternal(Long startNode, double startNodeScore, 
    		HashMap nodeSeenHashMap, int currentDepth, ArrayList cluster, 
    		double nodeScoreCutoff, int maxDepthFromStart,  HashMap nodeInfoHashMap){
        //base cases for recursion
        if (nodeSeenHashMap.containsKey(startNode)) {
            return (true);  //don't recheck a node
        }
        nodeSeenHashMap.put(startNode, new Boolean(true));
        if (currentDepth > maxDepthFromStart) {
            return (true);  //don't exceed given depth from start node
        }
        //Initialization
        Long currentNeighbor;
        int i = 0;
        for (i = 0; i < (((NodeInfo) nodeInfoHashMap.get(startNode)).numNodeNeighbors); i++) {
            //go through all currentNode neighbors to check their core density for cluster inclusion
            currentNeighbor = new Long(((NodeInfo) nodeInfoHashMap.get(startNode)).nodeNeighbors[i]);
            if ((!nodeSeenHashMap.containsKey(currentNeighbor)) &&
                    (((NodeInfo) nodeInfoHashMap.get(currentNeighbor)).score >=
                    (startNodeScore - startNodeScore * nodeScoreCutoff))) {
                //add current neighbor
                if (!cluster.contains(currentNeighbor)) {
                    cluster.add(currentNeighbor);
                }
                //try to extend cluster at this node
                getClusterCoreInternal(currentNeighbor, startNodeScore, nodeSeenHashMap, currentDepth + 1, cluster, nodeScoreCutoff, maxDepthFromStart, nodeInfoHashMap);
            }
        }
        return (true);
    }
    /**
     * Fluff up the cluster at the boundary by adding lower scoring, non cluster-core neighbors
     * This implements the cluster fluff feature.
     *
     * @param cluster         The cluster to fluff
     * @param nodeSeenHashMap The list of nodes seen already
     * @param nodeInfoHashMap Provides neighbour info
     * @return true
     */
    private boolean fluffClusterBoundary(ArrayList cluster, HashMap nodeSeenHashMap, HashMap nodeInfoHashMap) {
        Long currentNode , nodeNeighbor;
        //create a temp list of nodes to add to avoid concurrently modifying 'cluster'
        ArrayList nodesToAdd = new ArrayList();

        //Keep a separate internal nodeSeenHashMap because nodes seen during a fluffing should not be marked as permanently seen,
        //they can be included in another cluster's fluffing step.
        HashMap nodeSeenHashMapInternal = new HashMap();

        //add all current neighbour's neighbours into cluster (if they have high enough clustering coefficients) and mark them all as seen
        for (int i = 0; i < cluster.size(); i++) {
            currentNode = ((Long) cluster.get(i)).longValue();
            for (int j = 0; j < ((NodeInfo) nodeInfoHashMap.get(new Long(currentNode))).numNodeNeighbors; j++) {
                nodeNeighbor = ((NodeInfo) nodeInfoHashMap.get(new Long(currentNode))).nodeNeighbors[j];
                if ((!nodeSeenHashMap.containsKey(new Long(nodeNeighbor))) && (!nodeSeenHashMapInternal.containsKey(new Long(nodeNeighbor))) &&
                        ((((NodeInfo) nodeInfoHashMap.get(new Long(nodeNeighbor))).density) > params.getFluffNodeDensityCutoff())) {
                    nodesToAdd.add(new Long(nodeNeighbor));
                    nodeSeenHashMapInternal.put(new Long(nodeNeighbor), new Boolean(true));
                }
            }
        }

        //Add fluffed nodes to cluster
        if (nodesToAdd.size() > 0) {
            cluster.addAll(nodesToAdd.subList(0, nodesToAdd.size()));
        }

        return (true);
    }

    /**
     * Checks if the cluster needs to be filtered according to heuristics in this method
     *
     * @param gpClusterGraph The cluster to check if it passes the filter
     * @return true if cluster should be filtered, false otherwise
     */
    private boolean filterCluster(ClusterGraph gpClusterGraph) {
        if (gpClusterGraph == null) {
            return (true);
        }
        //filter if the cluster does not satisfy the user specified k-core
        ClusterGraph gpCore = getKCore(gpClusterGraph, params.getKCore());
        if (gpCore == null) {
            return (true);
        }
        return (false);
    }

    /**
     * Gives the cluster a haircut (removed singly connected nodes by taking a 2-core)
     *
     * @param gpClusterGraph The cluster graph
     * @param cluster        The cluster node ID list (in the original graph)
     * @param gpInputGraph   The original input graph
     * @return true
     */
    private boolean haircutCluster(ClusterGraph gpClusterGraph, ArrayList cluster) {
        //get 2-core
    	ClusterGraph gpCore = getKCore(gpClusterGraph, 2);
        if (gpCore != null) {
            //clear the cluster and add all 2-core nodes back into it
            cluster.clear();
            //must add back the nodes in a way that preserves gpInputGraph node indices
            
            for (CyNode n : gpCore.getNodeList()) {
                cluster.add(n.getSUID());
              }
            
            /*Long[] rootGraphIndices = gpCore.getNodeIndicesArray();
            for (int i = 0; i < rootGraphIndices.length; i++) {
                cluster.add(new Integer(gpInputGraph.getRootGraphNodeIndex(rootGraphIndices[i])));
            }*/
        }
        return (true);
    }

    /**
     * Calculate the density of a graph
     * The density is defined as the number of edges/the number of possible edges
     *
     * @param gpInputGraph The input graph to calculate the density of
     * @param includeLoops Include the possibility of loops when determining the number of
     *                     possible edges.
     * @return The density of the network
     */
    public double calcDensity(ClusterGraph gpInputGraph, boolean includeLoops) {
        int possibleEdgeNum = 0, actualEdgeNum = 0, loopCount = 0;
        double density = 0;
        
        if (gpInputGraph == null) {
            //logger.error("In " + callerID + ": network was null.");
            return -1.0D;
          }

          int nodeCount = gpInputGraph.getNodeCount();
          actualEdgeNum = getMergedEdgeCount(gpInputGraph, includeLoops);
          possibleEdgeNum = 0;

          if (includeLoops)
            possibleEdgeNum = nodeCount * (nodeCount + 1) / 2;
          else {
            possibleEdgeNum = nodeCount * (nodeCount - 1) / 2;
          }
          density = possibleEdgeNum != 0 ? actualEdgeNum / possibleEdgeNum : 0.0D;

          return density;
          
/*        String callerID = "Algorithm.calcDensity";
        if (gpInputGraph == null) {
            System.err.println("In " + callerID + ": gpInputGraph was null.");
            return (-1.0);
        }
        if (includeLoops) {
            //count loops
            Iterator nodes = gpInputGraph.getNodeList().iterator();
            while (nodes.hasNext()) {
                CyNode n = (CyNode) nodes.next();
                if (gpInputGraph.isNeighbor(n, n)) {
                    loopCount++;
                }
            }
            possibleEdgeNum = gpInputGraph.getNodeCount() * gpInputGraph.getNodeCount();
            actualEdgeNum = gpInputGraph.getEdgeCount() - loopCount;
        } else {
            possibleEdgeNum = gpInputGraph.getNodeCount() * gpInputGraph.getNodeCount();
            actualEdgeNum = gpInputGraph.getEdgeCount();
        }

        density = (double) actualEdgeNum / (double) possibleEdgeNum;
        return (density);*/
    }
    

    private int getMergedEdgeCount(ClusterGraph graph, boolean includeLoops) {
      Set suidPairs = new HashSet();

      for (CyEdge e : graph.getEdgeList()) {
        Long id1 = e.getSource().getSUID();
        Long id2 = e.getTarget().getSUID();

        if ((includeLoops) || (id1 != id2))
        {
          String pair = id2 + "_" + id1;
          suidPairs.add(pair);
        }
      }
      return suidPairs.size();
    }
    /**
     * Find a k-core of a graph. A k-core is a subgraph of minimum degree k
     *
     * @param gpInputGraph The input network
     * @param k  The k value of the k-core
     * @return Returns a subgraph with k-core, if any was found at given k
     */
    public ClusterGraph getKCore(ClusterGraph gpInputGraph, int k) {
        String callerID = "Algorithm.getKCore";
        if (gpInputGraph == null) {
            System.err.println("In " + callerID + ": gpInputGraph was null.");
            return (null);
        }

        //filter all nodes with degree less than k until convergence
        boolean firstLoop = true;
        int numDeleted;
        ClusterGraph gpOutputGraph = null;
        while (true) {
            numDeleted = 0;
            ArrayList alCoreNodeIndices = new ArrayList(gpInputGraph.getNodeCount());
            int degree;
            Iterator nodes = gpInputGraph.getNodeList().iterator();
            while (nodes.hasNext()) {
                CyNode n = (CyNode) nodes.next();
                /*if (gpInputGraph.getDegree(n) >= k) {*/
                degree = gpInputGraph.getAdjacentEdgeList(n, CyEdge.Type.ANY).size();

                if (degree >= k){
                    alCoreNodeIndices.add(new Long(n.getSUID())); //contains all nodes with degree >= k
                } else {
                    numDeleted++;
                }
            }
            if ((numDeleted > 0) || (firstLoop)) {
                //convert ArrayList to int[] for creation of a GraphPerspective for this core
            	
                Set outputNodes = new HashSet();
                Long[] outputNodeIndices = new Long[alCoreNodeIndices.size()];
                int j = 0;
                for (Iterator i = alCoreNodeIndices.iterator(); i.hasNext(); j++) {
                    outputNodeIndices[j] = ((Long) i.next()).longValue();
                    outputNodes.add(gpInputGraph.getNode(outputNodeIndices[j]));
                }
                gpOutputGraph = clusterUtil.createGraph(gpInputGraph.getRootNetwork(), outputNodes);
               // gpOutputGraph = gpInputGraph.createGraphPerspective(outputNodeIndices);
                if (gpOutputGraph.getNodeCount() == 0) {
                    return (null);
                }
                //iterate again, but with a new k-core input graph
                gpInputGraph = gpOutputGraph;
                if (firstLoop) {
                    firstLoop = false;
                }
            } else 
                break;
        }
        return (gpOutputGraph);
    }
    /**
     * Find the highest k-core in the input graph.
     *
     * @param gpInputGraph The input network
     * @return Returns the k-value and the core as an Object array.
     *         The first object is the highest k value i.e. objectArray[0]
     *         The second object is the highest k-core as a GraphPerspective i.e. objectArray[1]
     */
    public Object[] getHighestKCore(ClusterGraph gpInputGraph) {
        String callerID = "Algorithm.getHighestKCore";
        if (gpInputGraph == null) {
            System.err.println("In " + callerID + ": gpInputGraph was null.");
            return (null);
        }
        int i = 1;
        ClusterGraph gpCurCore = null, gpPrevCore = null;
        while ((gpCurCore = getKCore(gpInputGraph, i)) != null) {
            gpInputGraph = gpCurCore;
            gpPrevCore = gpCurCore;
            i++;
        }
        Integer k = new Integer(i - 1);
        Object[] returnArray = new Object[2];
        returnArray[0] = k;
        returnArray[1] = gpPrevCore;
        return (returnArray);
    }
    /**
     * Finds the cluster based on user's input via size slider.
     * this function is only called in ResultPanel.SizeAction
     *
     * @param cluster cluster being explored
     * @param nodeScoreCutoff slider source value
     * @param inputNetwork network
     * @param resultId title of the result set being explored
     * @return explored cluster
     */
    public Cluster exploreCluster(Cluster cluster, double nodeScoreCutoff, CyNetwork inputNetwork, int resultId) {
        HashMap nodeInfoHashMap = (HashMap) nodeInfoResultsMap.get(Integer.valueOf(resultId));
        this.params = this.clusterUtil.getCurrentParameters().getResultParams(cluster.getResultTitle());
        //ParameterSet params = ParameterSet.getInstance().getResultParams(cluster.getResultTitle()).copy();
        HashMap nodeSeenHashMap;
        if (nodeScoreCutoff <= params.getNodeScoreCutoff()) {
            nodeSeenHashMap = new HashMap(cluster.getNodeSeenHashMap());
        } else
            nodeSeenHashMap = new HashMap();
        Long seedNode = cluster.getSeedNode();
        ArrayList alNodes = getClusterCore(seedNode, nodeSeenHashMap, nodeScoreCutoff, params.getMaxDepthFromStart(), nodeInfoHashMap);
        if (!alNodes.contains(seedNode))//make sure seed node is part of cluster, if not already in there
            alNodes.add(seedNode);
        //create an input graph for the filter and haircut methods
        ClusterGraph gpCluster = createClusterGraph(alNodes, inputNetwork);
       // ClusterGraph gpCluster = Algorithm.createGraphPerspective(alNodes, inputNetwork);
        if (params.isHaircut())
            haircutCluster(gpCluster, alNodes);
        if (params.isFluff())
            fluffClusterBoundary(alNodes, nodeSeenHashMap, nodeInfoHashMap);
        cluster.setALNodes(alNodes);
        
        gpCluster = createClusterGraph(alNodes, inputNetwork);
       // gpCluster = Algorithm.createGraphPerspective(alNodes, inputNetwork);
        //cluster.setGPCluster(gpCluster);
 //       cluster.setClusterScore(scoreCluster(gpCluster));
        double score = scoreCluster(gpCluster);
        
        Cluster newCluster = new Cluster(resultId, seedNode, gpCluster, score, alNodes, 
        	      nodeSeenHashMap);
        	    newCluster.setRank(cluster.getRank());
        return cluster;
    }

	abstract public Cluster[] run(CyNetwork inputNetwork, int resultTitle);
	
	
	static public int getNodeDegree(CyNetwork currentNetwork2, Long node) {
		// TODO Auto-generated method stub
		CyNode cynode=currentNetwork2.getNode(node);
		
		return currentNetwork2.getAdjacentEdgeList(cynode, CyEdge.Type.ANY).size();
		
		 
	}
	
	static public float getNodeDegreeWeight(CyNetwork network, Long node) {
		// TODO Auto-generated method stub
		float sum = 0.0f;
		CyNode cynode=network.getNode(node);		
		Iterator edges= network.getAdjacentEdgeList(cynode, CyEdge.Type.ANY).iterator();
		while(edges.hasNext()){
			
			CyEdge e = (CyEdge)edges.next();
			if(network.getRow(e).get("weight", Double.class) != null)
			{	
			//	System.out.println(network.getRow(e).get("weight", Double.class));
				sum += network.getRow(e).get("weight", Double.class);
			}
			else 
				sum += 1.0f;
			
		}
		 return sum;
	}
	
	
	public ArrayList getCommonNeighbors(Long a,Long b){
    	ArrayList r=new ArrayList();
    	ArrayList an = getNeighbors(a);
    	ArrayList bn = getNeighbors(b);
    	for(Iterator i=an.iterator();i.hasNext();){
    		Long n=(Long)i.next();
    		if(bn.contains(n))
    			r.add(n);
    	}
    	return r;
    }
	
	
}
