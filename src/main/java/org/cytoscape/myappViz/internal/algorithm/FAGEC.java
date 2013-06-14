package org.cytoscape.myappViz.internal.algorithm;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.myappViz.internal.Cluster;
import org.cytoscape.myappViz.internal.ClusterGraph;
import org.cytoscape.myappViz.internal.ClusterUtil;
import org.cytoscape.myappViz.internal.ParameterSet;

import java.util.*;



public class FAGEC extends Algorithm{


	public FAGEC(Long networkID,ClusterUtil clusterUtil){
		super(networkID, clusterUtil);
	}
	

	/**
     * FAG-EC Algorithm Step 1: 
     * Calculate arc weights which is defined as 
     * ( sizOf(Ni Intersect Nj) +1 )/ min[(ki),(kj)]
     *
     * @param inputNetwork The network that will be calculated
     */    
    public void calEdgeWeight(CyNetwork inputNetwork){
        String callerID = "In Algorithm.calEdgeWeight";
    	Long networkID=inputNetwork.getSUID();
    	if(!edgeWeightNetworkMap.containsKey(networkID)){
            double weight;
            int degree1,degree2,min;
            ArrayList al;
        	TreeMap edgeWeightsMap=new TreeMap(new Comparator(){
        		//sort Doubles in descending order
        		public int compare(Object o1,Object o2){
        			double d1=((Double)o1).doubleValue();
        			double d2=((Double)o2).doubleValue();
        			if(d1==d2){
        				return 0;
        			}
        			else if(d1<d2){
        				return 1;
        			}
        			else return -1;
        		}
        	});  		
        	Iterator edges=inputNetwork.getEdgeList().iterator();
        	while(edges.hasNext()&&!cancelled){	//for each edge, cal the weight
        		weight=0.0;
        		int common=0;
        		CyEdge e=(CyEdge)edges.next();
        		Long nFrom=e.getSource().getSUID();
        		Long nTo=e.getTarget().getSUID();
        		
        		//cal the edge weght
        		Long[] neighbors1=getNeighborArray(inputNetwork,nFrom);
        		Long[] neighbors2=getNeighborArray(inputNetwork,nTo);
        		Arrays.sort(neighbors1);
        		for(int i=0;i<neighbors2.length;i++){
        			Long key=neighbors2[i];
        			if(Arrays.binarySearch(neighbors1, key)>=0)//exist a common neighbor of both nodes
        				common++;
        		}
        		common++; //sizOf(Ni Intersect Nj) +1 
        		degree1=super.getNodeDegree(inputNetwork,e.getSource().getSUID());
        		degree2=getNodeDegree(inputNetwork,e.getTarget().getSUID());
        		
        		
/*        		degree1=inputNetwork.getDegree(e.getSource());
        		degree2=inputNetwork.getDegree(e.getTarget());*/
        		min=degree1<degree2 ? degree1:degree2; //min[(ki),(kj)]
        		weight=(double)(common)/(double)min;  //edge cluster coefficient
        		
        		//add to the edge weights map
        		if(edgeWeightsMap.containsKey(new Double(weight))) {
        			al=(ArrayList)edgeWeightsMap.get(new Double(weight));
        			al.add(e.getSUID());
        		}else{
        			al=new ArrayList();
        			al.add(e.getSUID());
        			edgeWeightsMap.put(new Double(weight), al);
        		}
        	}       	
        	curEdgeWeights=edgeWeightsMap;
        	edgeWeightNetworkMap.put(networkID, edgeWeightsMap);
    	}
    	else{
    		curEdgeWeights=(TreeMap)edgeWeightNetworkMap.get(networkID);
    	}
    }

   /**
     * FAG-EC Algorithm Step 2 Generate complexes: 
     * @param inputNetwork The input network
     * @param resultTitle Title of the result, used as an identifier in various hash maps
     * @return the clusters identified
     */
   public Cluster[] FAG_ECFinder(CyNetwork inputNetwork,int resultTitle){
        String callerID = "Algorithm.FAG_ECFinder";
    	System.out.println("In "+callerID);
    	params=getParams();

    	currentNetwork=inputNetwork;
    	calNodeInfos(inputNetwork);
        calEdgeWeight(inputNetwork);
        if (curEdgeWeights == null || curNodeInfos==null) {
            System.err.println("In " + callerID + ": nodeInfos Map or edgeWeights Map was null.");
            return (null);
        }
        long msTimeBefore = System.currentTimeMillis();
        int findingProgress = 0;
        int findingTotal = 0;
        Collection values = curEdgeWeights.values(); //returns a Collection sorted by key order (descending)
        for (Iterator i1 = values.iterator(); i1.hasNext();) {
            ArrayList value = (ArrayList) i1.next();
            for(Iterator i2 = value.iterator(); i2.hasNext();) {
                i2.next();
                findingTotal++;
            }
        }
        //stores the list of clusters as ArrayLists of node indices in the input Network
        ArrayList alOriginalClusters = new ArrayList(inputNetwork.getNodeCount());
        /************************First, we sort each single node into a clique*************************/
        int i=0;
        Iterator nodes = inputNetwork.getNodeList().iterator();
        while(nodes.hasNext()){
        	CyNode n=(CyNode) nodes.next();
        	
        	int degree=super.getNodeDegree(inputNetwork,n.getSUID());
    		//int degree=inputNetwork.getDegree(n);
    		Cluster newCluster = new Cluster(i);
    		ArrayList alNodes=new ArrayList();
    		alNodes.add(n.getSUID());
    		newCluster.setALNodes(alNodes);
    		newCluster.setTotalDegree(degree);
    		Long nodeIndex= n.getSUID();
    		((NodeInfo)curNodeInfos.get(nodeIndex)).setComplex(i);
    		i++;
    		alOriginalClusters.add(newCluster);
        }
        /**********************************************************************************************
			Then, Operation UNION:	according to different situation, in which the two nodes consisting 
				this arc may belong to different Complexes or an identical Complex and that the 
				attributes of the Complexes varies, we take on different action 
         ***********************************************************************************************/
        ArrayList alEdgeWithSameWeight;  
        CyEdge curEdge;                                                                                                                          
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            //each weight may be associated with multiple edges, iterate over these lists
        	alEdgeWithSameWeight = (ArrayList) iterator.next();
            for (int j = 0; j < alEdgeWithSameWeight.size(); j++) {//for each edge
                Long edgeIndex = ((Long) alEdgeWithSameWeight.get(j)).longValue();
                curEdge=inputNetwork.getEdge(edgeIndex);                
        		Long inFrom = curEdge.getSource().getSUID();
        		Long inTo   = curEdge.getTarget().getSUID();
        		NodeInfo fromNI=(NodeInfo)curNodeInfos.get(inFrom);	//source node info
        		NodeInfo toNI=(NodeInfo)curNodeInfos.get(inTo);	//target node info
        		
        		int icFrom=fromNI.iComplex;	//complex that the source node belongs to
        		int icTo=toNI.iComplex;		//complex that the target node belongs to 
        		if(icFrom != icTo)    //we have take some actions only if the two complexes are not the same
        		{
        			Cluster cFrom=(Cluster)alOriginalClusters.get(icFrom);
        			Cluster cTo=(Cluster)alOriginalClusters.get(icTo);
        			if(cFrom.isMergeable() && cTo.isMergeable())	//the two complexes are both mergeable
        				if(!cFrom.isModule() || !cTo.isModule())	//either of the two complexes are not modules yet
        					if(cFrom.getALNodes().size() >= cTo.getALNodes().size()){//merge the smaller complexe to the larger one
        						if(params.isWeakFAGEC()) mergeComplexes1(cFrom, cTo);
        						else	mergeComplexes2(cFrom, cTo);   
        					}
        					else{	//merge the smaller complex to the larger one
        						if(params.isWeakFAGEC())	mergeComplexes1(cTo, cFrom);
        						else	mergeComplexes2(cFrom, cTo);
        					}
        				else	//both of the two complexes are modules
        				{
        					cFrom.setMergeable(false);
        					cTo.setMergeable(false);
        				}
        			else	//either of the two complexes is not mergeable
        			{
        				cFrom.setMergeable(false);
    					cTo.setMergeable(false);
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
        ArrayList alClusters = new ArrayList();
        Iterator it=alOriginalClusters.iterator();
        while(it.hasNext()){
        	Cluster cluster=(Cluster)it.next();
        	if(cluster.getALNodes().size()>=params.getComplexSizeThresholdFAGEC()){
        		ArrayList<Long> alNodes=cluster.getALNodes();
        		ClusterGraph gpCluster = this.createClusterGraph(alNodes, inputNetwork);
        		//cluster.setComplexID(counter++);
        		cluster.setGraph(gpCluster);
        		cluster.setClusterScore(0.0);
        		cluster.setSeedNode(alNodes.get(0));
        		cluster.setResultTitle(resultTitle);
        		int ind=cluster.getInDegree();
        		int outd=cluster.getTotalDegree()-2*ind;
        		if(ind!=0 && outd!=0)
        			cluster.setModularity((double)ind/(double)outd);
        		else
            		cluster.calModularity(inputNetwork);
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
        return clusters;
    }

    /**
     * merge two unoverlapped complexes ,weak definition
     */
    protected void mergeComplexes1(Cluster c1,Cluster c2){
    	int inDegree=c1.getInDegree();
    	int totalDegree=c1.getTotalDegree()+c2.getTotalDegree();
    	
    	ArrayList alNodes=c1.getALNodes();
    	Iterator i=c2.getALNodes().iterator();
    	while(i.hasNext()){
    		Long nodeIndex=((Long)i.next()).longValue();
    		Long[] adjs=getNeighborArray(currentNetwork,nodeIndex);
    		for(int j=0;j<adjs.length;j++)  //计算c2中每个节点   与c1相连  的节点个数    
    			if(alNodes.contains(adjs[j]))
    					inDegree++; //c1 入度+1
    		alNodes.add(nodeIndex); //将c2的该节点加入c1
    		NodeInfo node=(NodeInfo)curNodeInfos.get(nodeIndex);
    		node.setComplex(c1.getComplexID());
    	}
    	c1.setInDegree(inDegree);
    	c1.setTotalDegree(totalDegree);
    	int outDegree=totalDegree-2*inDegree;
    	if(outDegree<0)
    		System.err.println("Error outDegree!");
    	float fModule = (float)2*inDegree/(float)(outDegree);
    	if( fModule>params.getfThresholdFAGEC() )
    		c1.setModule(true);
    	c2.getALNodes().clear();
    }
    /**
     * merge two unoverlapped complexes ,strong definition
     */
    protected void mergeComplexes2(Cluster c1,Cluster c2){
    	ArrayList alNodes=c1.getALNodes();
    	Iterator i=c2.getALNodes().iterator();
    	while(i.hasNext()){
    		Long nodeIndex=((Long)i.next()).longValue();
    		NodeInfo node=(NodeInfo)curNodeInfos.get(new Long(nodeIndex));
    		node.setComplex(c1.getComplexID());
    		alNodes.add(new Long(nodeIndex));
    	}
    	c2.getALNodes().clear();
    	i=alNodes.iterator();
    	c1.setModule(true);
    	int nodeInDegree,nodeTotalDegree;
    	while(i.hasNext()){
    		Long nodeIndex=((Long)i.next()).longValue();
    		Long[] adjs=getNeighborArray(currentNetwork,nodeIndex);
    		nodeInDegree=0;
    		for(int j=0;j<adjs.length;j++)
    			if(alNodes.contains(adjs[j]))
    					nodeInDegree++;
    		
    		nodeTotalDegree=super.getNodeDegree(currentNetwork,nodeIndex);
    		//nodeTotalDegree=currentNetwork.getDegree(nodeIndex);
        	double fModule = (double)nodeInDegree/(double)(nodeTotalDegree);
    		if(fModule<0.5)//indegree < outdegree
    			c1.setModule(false);
    	}
    }
    /**
     * merge overlapped complexes with weak module definition
     */
    protected void mergeComplexes3(Cluster c1, Cluster c2){
    	ArrayList nodes1=c1.getALNodes();
    	ArrayList nodes2=c2.getALNodes();
    	//add the unoverlapped nodes and set the subComplexes of all nodes in C2
    	NodeInfo nodeNI;
    	ArrayList subComplexes;
    	for(Iterator it=nodes2.iterator();it.hasNext();){//for each node in C2
    		Long node=(Long)it.next();
    		nodeNI=(NodeInfo)curNodeInfos.get(node);
    		subComplexes=nodeNI.getAlComplex();
    		if(!nodes1.contains(node)){//this is not a overlapped node
        		int index=subComplexes.indexOf(new Integer(c2.getComplexID()));
        		subComplexes.remove(index);
        		subComplexes.add(new Integer(c1.getComplexID()));
    			nodes1.add(node);
    		}
    		else{	//this node already exists in C1
        		int index=subComplexes.indexOf(new Integer(c2.getComplexID()));
        		subComplexes.remove(index);
    		}
    	}
    	//calculate the other informations for C1
    	int inDegree=0;
    	int totalDegree=0;
    	for(Iterator it=nodes1.iterator();it.hasNext();){//for each node in merged C1
    		Long node=((Long)it.next()).longValue();
    		totalDegree+=super.getNodeDegree(currentNetwork,node);
    		//totalDegree+=currentNetwork.getDegree(node);//can this be useful?
    		Long[] neighbors=getNeighborArray(currentNetwork,node);
    		for(int i=0;i<neighbors.length;i++)
    			if(nodes1.contains(neighbors[i]))
    				inDegree++;
    	}
    	int outDegree=totalDegree-inDegree;
    	inDegree=inDegree/2;
    	c1.setInDegree(inDegree);
    	c1.setTotalDegree(totalDegree);
    	double fModule = (double)inDegree/(double)outDegree;
    	if(fModule>params.getfThresholdFAGEC())
    		c1.setModule(true);
    	//clear the content of nodes2
    	nodes2.clear();
    }
    /**
     * merge overlapped complexes using strong module definition
     */
    protected void mergeComplexes4(Cluster c1, Cluster c2){
    	ArrayList nodes1=c1.getALNodes();
    	ArrayList nodes2=c2.getALNodes();
    	//add the unoverlapped nodes and set the subComplexes of all nodes in C2
    	NodeInfo nodeNI;
    	ArrayList subComplexes;
    	for(Iterator it=nodes2.iterator();it.hasNext();){//for each node in C2
    		Long node=(Long)it.next();
    		nodeNI=(NodeInfo)curNodeInfos.get(node);
    		subComplexes=nodeNI.getAlComplex();
    		if(!nodes1.contains(node)){//this is not a overlapped node
        		int index=subComplexes.indexOf(new Integer(c2.getComplexID()));
        		subComplexes.remove(index);
        		subComplexes.add(new Integer(c1.getComplexID()));
    			nodes1.add(node);
    		}
    		else{	//this node already exists in C1
        		int index=subComplexes.indexOf(new Integer(c2.getComplexID()));
        		subComplexes.remove(index);
    		}
    	}
    	c1.setModule(true);
    	int nodeInDegree,nodeTotalDegree;
    	for(Iterator i=nodes1.iterator();i.hasNext();){
    		Long nodeIndex=((Long)i.next()).longValue();
    		Long[] adjs=getNeighborArray(currentNetwork,nodeIndex);
    		nodeInDegree=0;
    		for(int j=0;j<adjs.length;j++)
    			if(nodes1.contains(adjs[j]))
    					nodeInDegree++;
    		
    		
    		nodeTotalDegree=super.getNodeDegree(currentNetwork,nodeIndex);
 //   		nodeTotalDegree=currentNetwork.getDegree(nodeIndex);
        	float fModule = (float)nodeInDegree/(float)(nodeTotalDegree);
    		if(fModule<0.5){
    			c1.setModule(false);
    		}
    	}
    	//clear the content of nodes2
    	nodes2.clear();
    }


	/**
     * EAGLE Step1 and FEA-EC Step2(optional) get Maximal Cliques: 
     * get all the maximal cliques in the network
     * @param inputNetwork the operated network
     */
    public void getMaximalCliques(CyNetwork inputNetwork,int resultTitle){
        String callerID = "Algorithm.getMaximalCliques";
        long startTime=System.currentTimeMillis();
        if (inputNetwork == null) {
            System.err.println("In " + callerID + ": inputNetwork was null.");
            return;
        }
    	currentNetwork=inputNetwork;
    	params=getParams();

		Long net=inputNetwork.getSUID();
    	if(!maximalCliquesNetworkMap.containsKey(inputNetwork.getSUID())){
    		System.out.println("Get MaximalCliques for This Network........");
            long msTimeBefore = System.currentTimeMillis();
    		HashMap cliques = new HashMap();
    		
        	//initialize states
    		Vector alCur=new Vector();
    		Vector alFini=new Vector();
    		Vector alNodes=new Vector(inputNetwork.getNodeCount());
    		for(Iterator i=inputNetwork.getNodeList().iterator();i.hasNext();){
    			Long node=new Long(((CyNode)i.next()).getSUID());
    			alNodes.add(node);
    		}    		
    		//The critical internal process
    		expand(cliques,alCur,alNodes,alFini);
    		
    		curCliques=cliques;
    		maximalCliquesNetworkMap.put(net, cliques);
    		findCliquesTime=System.currentTimeMillis()-msTimeBefore;
    	}
    	else
    		curCliques=(HashMap)maximalCliquesNetworkMap.get(net);
    	findCliquesTime=System.currentTimeMillis()-startTime;
    }
    
    @Override
	public Cluster[] run(CyNetwork inputNetwork, int resultTitle){
 		return(this.FAG_ECFinder(inputNetwork, resultTitle));
	}

}
