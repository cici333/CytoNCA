package org.cytoscape.CytoCluster.internal.algorithm;

import org.cytoscape.CytoCluster.internal.Cluster;
import org.cytoscape.CytoCluster.internal.ClusterGraph;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.ParameterSet;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

import java.util.*;
import java.util.Map.Entry;





public class HCPIN extends Algorithm{
	

	public HCPIN(Long networkID,ClusterUtil clusterUtil){
		super(networkID, clusterUtil);
	}
	

	/**
     * HC-PIN Algorithm Step 1: 
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
        	int aa=1;
        	while(edges.hasNext()&&!cancelled){	//for each edge, cal the weight(cluster value)
        		CyEdge e=(CyEdge)edges.next();
        		CyNode From = e.getSource();
        		CyNode To = e.getTarget();
        		Long nFrom = From.getSUID();
        		Long nTo = To.getSUID();
        		double wsum1 = 0.0f;
        		double wsum2 = 0.0f;
        		double FromWeight = 0.0f;
        		double ToWeight = 0.0f;
        		double Weight = 0.0f;
        //		System.out.println("#######"+aa+"     "+inputNetwork.getRow(From).get("name", String.class)+"##########");
        		
        		ArrayList<Long> commonneighbor = getCommonNeighbors(nFrom, nTo);
        		Iterator i1 = commonneighbor.iterator(); 
        		while(i1.hasNext()){
        			CyNode tempe = inputNetwork.getNode((Long)i1.next());
        			ArrayList aFrom = (ArrayList) inputNetwork.getConnectingEdgeList(From, tempe, Type.ANY);
        			if(!aFrom.isEmpty()){
        				for(Iterator i=aFrom.iterator();i.hasNext();){
        					CyEdge a = (CyEdge) i.next();     					
        					if(inputNetwork.getRow(a).get("weight", Double.class) != null)
        						wsum1 += inputNetwork.getRow(a).get("weight", Double.class);
        					else
        						wsum1 += 1.0;
        				}        					
            		}
        			ArrayList aTo = (ArrayList) inputNetwork.getConnectingEdgeList(To, tempe, Type.ANY);
        			if(!aTo.isEmpty()){
        				for(Iterator i=aTo.iterator();i.hasNext();){
        					CyEdge b = (CyEdge) i.next();     					
        					if(inputNetwork.getRow(b).get("weight", Double.class) != null)
        						wsum2 += inputNetwork.getRow(b).get("weight", Double.class);
        					else
        						wsum2 += 1.0;
        				}        				
        			}      			
        		}
        		FromWeight = getNodeDegreeWeight(inputNetwork, nFrom);
        		ToWeight = getNodeDegreeWeight(inputNetwork, nTo);
        		weight = (wsum1*wsum2)/(FromWeight*ToWeight);  //edge cluster value;
        /*		if(weight == 0.6 || weight== 0.5){
        			System.out.println(weight+" wsum1 = "+wsum1 +" wsum2 = "+wsum2+" FromWeight = "+FromWeight+" ToWeight= "+ToWeight);
        		}
        */		
        //		System.out.println(aa+"     "+inputNetwork.getRow(From).get("name", String.class)+"    "+inputNetwork.getRow(To).get("name", String.class)+"     "+weight);
        		
        		//add to the edge weights map
        		if(edgeWeightsMap.containsKey(new Double(weight))) {
        			al=(ArrayList)edgeWeightsMap.get(new Double(weight));
        			al.add(e.getSUID());
        		}else{
        			al=new ArrayList();
        			al.add(e.getSUID());
        			edgeWeightsMap.put(new Double(weight), al);
        		}
        		
      //  	aa++;	
        	}      
        	
    /*       Set<Map.Entry<Double, ArrayList>> yy = edgeWeightsMap.entrySet();
    		Iterator ii = yy.iterator();
    		while(ii.hasNext()){
    			Map.Entry<Double, ArrayList> temp = (Map.Entry<Double, ArrayList>)ii.next();
    			Iterator i2 = temp.getValue().iterator();
    			while(i2.hasNext()){
    				CyEdge e = inputNetwork.getEdge((Long)i2.next());
        			System.out.println(temp.getKey()+" ^^^^^^^"+inputNetwork.getRow(e.getSource()).get("name", String.class)+"  "+inputNetwork.getRow(e.getTarget()).get("name", String.class));
    			}
    		
    		}
    	*/	
        	curEdgeWeights=edgeWeightsMap;
        	edgeWeightNetworkMap.put(networkID, edgeWeightsMap);
    	}
    	else{
    		curEdgeWeights=(TreeMap)edgeWeightNetworkMap.get(networkID);
    	}
       System.out.println("********step1***********"); 		
   }
        	
        	
        	
        	
    

   /**
     * HC-PIN Algorithm Step 2 Generate complexes: 
     * @param inputNetwork The input network
     * @param resultTitle Title of the result, used as an identifier in various hash maps
     * @return the clusters identified
     */
   public Cluster[] HCPINFinder(CyNetwork inputNetwork,int resultTitle){
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
    	//	newCluster.setName(inputNetwork.getRow(n).get("name", String.class));
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
        int tt = 1;
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            //each weight may be associated with multiple edges, iterate over these lists
        	
      /*  	class c implements Comparator{
       		 public int compare(Object o1, Object o2) {
       			 
       			 CyNode n1t = currentNetwork.getEdge((Long)o1).getTarget();
       			 CyNode n1s = currentNetwork.getEdge((Long)o1).getSource();
       			 CyNode n2t = currentNetwork.getEdge((Long)o2).getTarget();
      			 CyNode n2s = currentNetwork.getEdge((Long)o2).getSource();
       			 
      			 String s1t = currentNetwork.getRow(n1t).get("name", String.class);
      			 String s1s = currentNetwork.getRow(n1s).get("name", String.class);
      			 String s2t = currentNetwork.getRow(n2t).get("name", String.class);
     			 String s2s = currentNetwork.getRow(n2s).get("name", String.class);
     			 
     			 String t1,tt1,t2,tt2;
     			 if(s1t.compareTo(s1s)<0){
     				 t1 = s1t;
     				 tt1 = s1s;
     			 }else{
     				tt1 = s1t;
    				t1 = s1s;
     			 }
     			if(s2t.compareTo(s2s)<0){
    				 t2 = s2t;
    				 tt2 = s2s;
    			 }else{
    				tt2 = s2t;
    				t2 = s2s;
    			 }
       			 if(t1.compareTo(t2)<0)
       				 return -1;
       			  if(t1.compareTo(t2) == 0)
       				 if(tt1.compareTo(tt2)<0)
       					 return -1;
       			return 1;
       		 }
       	}
        	
        */	
        	alEdgeWithSameWeight = (ArrayList) iterator.next();
        //	Collections.sort(alEdgeWithSameWeight,new c());
        	
        	
        	
        	
        	
        	
        	
        	
            for (int j = 0; j < alEdgeWithSameWeight.size(); j++) {//for each edge
                Long edgeIndex = ((Long) alEdgeWithSameWeight.get(j)).longValue();
                curEdge=inputNetwork.getEdge(edgeIndex);        
                System.out.println(tt+" "+inputNetwork.getRow(curEdge.getTarget()).get("name", String.class)+" "+inputNetwork.getRow(curEdge.getSource()).get("name", String.class));
                tt++;
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
        						if(params.isWeakHCPIN()) mergeComplexes1(cFrom, cTo);
        						else	mergeComplexes2(cFrom, cTo);   
        			//			System.out.println("@@@@@@@"+cFrom.getALNodes().size()+" "+cTo.getALNodes().size());
        			//			mergeComplexes1(cFrom, cTo);
        			//			System.out.println("  1  "+cFrom.getALNodes().size());
        					}
        					else{	//merge the smaller complex to the larger one
        						if(params.isWeakHCPIN())	mergeComplexes1(cTo, cFrom);
        						else	mergeComplexes2(cFrom, cTo);
        			//			System.out.println("@@@@@@@"+cFrom.getALNodes().size()+" "+cTo.getALNodes().size());
        				///		mergeComplexes1(cTo, cFrom);
        			//			System.out.println("  2  "+cTo.getALNodes().size());
        					}
        				else	//both of the two complexes are modules
        				{
        					cFrom.setMergeable(false);
        					cTo.setMergeable(false);
        		//			System.out.println("  3  ");
        				}
        			else	//either of the two complexes is not mergeable
        			{
        				cFrom.setMergeable(false);
    					cTo.setMergeable(false);
    		//			System.out.println("  4  ");
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
        	if(cluster.getALNodes().size()>=params.getComplexSizeThresholdHCPIN()){
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
        
        for(int t=0; t<clusters.length; t++){
        
        Iterator ii = clusters[t].getALNodes().iterator();
        System.out.println("cluster "+t);
     /*   int x=1;
        while(ii.hasNext()){
        	CyNode nn = inputNetwork.getNode((Long)ii.next());
        	System.out.println(x+"    "+inputNetwork.getRow(nn).get("name",String.class));
        	x++;
        }*/
        }
        System.out.println("********step2***********"); 
        
        params.setAlgorithm("HCPIN");
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
    	double fModule = (double)2*inDegree/(double)(outDegree);
    	if( fModule>params.getfThresholdHCPIN() )
    		c1.setModule(true);
    	c2.getALNodes().clear();
    	
    	
    	Iterator y = alNodes.iterator();
    	while(y.hasNext()){
    		CyNode u = (CyNode)currentNetwork.getNode((Long)y.next());
    		System.out.println("  $$$   "+currentNetwork.getRow(u).get("name", String.class));
    	}
    	
    	
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
    		if(fModule<0.5)
    			c1.setModule(false);
    	}
    }
    /**
     * merge overlapped complexes with weak module definition
     */
 /*   protected void mergeComplexes3(Cluster c1, Cluster c2){
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
    	if(fModule>params.getfThreshold())
    		c1.setModule(true);
    	//clear the content of nodes2
    	nodes2.clear();
    }
    /**
     * merge overlapped complexes using strong module definition
     */
 /*   protected void mergeComplexes4(Cluster c1, Cluster c2){
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
/*    public void getMaximalCliques(CyNetwork inputNetwork,int resultTitle){
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
    */
    @Override
	public Cluster[] run(CyNetwork inputNetwork, int resultTitle){
    	
    	currentNetwork = inputNetwork;
 		return(this.HCPINFinder(inputNetwork, resultTitle));
	}

}



