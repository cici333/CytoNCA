package org.cytoscape.myappViz.internal;

import org.cytoscape.model.CyNetwork;

import org.cytoscape.myappViz.internal.algorithm.Algorithm;

import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import java.util.*;


/**
 * Stores various cluster information for simple get/set purposes.
 */
public class Cluster {
    private int complexID;		//the ID of the complex
    private ArrayList alNodes = null;
//    private GraphPerspective gpCluster = null;
//    private DCyNetworkView dgView = null; //keeps track of layout???
    private Long seedNode;
    //used in the exploring action
    private Map<Long, Boolean> nodeSeenHashMap; //stores the nodes that have already been included in higher ranking clusters
    private double clusterScore;
    private String clusterName; //Pretty much unsed so far, but could store name by user's input
    private int rank;
    private int resultTitle;

    private int inDegree;		//the indegree of the complex
    private int totalDegree;	//the total degree of the complex
    private boolean mergeable;	//the flag showing if the complex is mergeable
    private boolean module;		//the flag showing if this complex can be defined as a module
    private double modularity;

	private int resultId;
	private ClusterGraph graph;
	private List<Long> alCluster;
	private CyNetworkView view;
	private double score;
	private java.awt.Image image;
	private boolean disposed;
	
	private Algorithm algorithm;
	private String name;
    
	private int add;
	private Long edge;
	private int flag;
	
	
	public Cluster(){
		this.inDegree=0;
		this.totalDegree=0;
		 
	}
	
	public Cluster(Long edge){
		this.add = 1;
		this.inDegree=0;
		this.totalDegree=0;
		this.flag = 1;
		
	}
	
	
	  public Cluster(int resultId, Long seedNode, ClusterGraph graph, double score, List<Long> alCluster, Map<Long, Boolean> nodeSeenHashMap)
	  {
	    assert (seedNode != null);
	    assert (graph != null);
	    assert (alCluster != null);
	    assert (nodeSeenHashMap != null);

	    this.resultId = resultId;
	    this.seedNode = seedNode;
	    this.graph = graph;
	    this.setScore(score);
	    this.alCluster = alCluster;
	    this.nodeSeenHashMap = nodeSeenHashMap;
	  }
	public Cluster(int ID){
		alNodes=new ArrayList();
		this.complexID=ID;
		this.inDegree=0;
		this.totalDegree=0;
		this.mergeable=true;
		this.module=false;
		this.modularity=0.0;
	}
    public int getComplexID() {
		return complexID;
	}
	public void setComplexID(int complexID) {
		this.complexID = complexID;
	}
	public int getResultTitle() {
        return resultTitle;
    }
    public void setResultTitle(int resultTitle) {
        this.resultTitle = resultTitle;
    }
    public String getClusterName() {
        return clusterName;
    }
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
/*    public DCyNetworkView getDGView() {
        return dgView;
    }
    public void setDGView(DCyNetworkView dgView) {
        this.dgView = dgView;
    }*/
    public double getClusterScore() {
        return clusterScore;
    }
    public void setClusterScore(double clusterScore) {
        this.clusterScore = clusterScore;
    }
/*    public GraphPerspective getGPCluster() {
        return gpCluster;
    }
    public void setGPCluster(GraphPerspective gpCluster) {
        this.gpCluster = gpCluster;
    }*/
    public ArrayList getALNodes() {
        return alNodes;
    }
    public void setALNodes(ArrayList alNodes) {
        this.alNodes = alNodes;
    }
    public Long getSeedNode() {
        return seedNode;
    }
    public void setSeedNode(Long seedNode) {
        this.seedNode = seedNode;
    }
    public Map<Long, Boolean> getNodeSeenHashMap() {
        return nodeSeenHashMap;
    }
    public void setNodeSeenHashMap(Map<Long, Boolean> nodeSeenHashMap) {
        this.nodeSeenHashMap = nodeSeenHashMap;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
        this.clusterName = "Complex " + (rank + 1);
    }
	public int getInDegree() {
		return inDegree;
	}
	public void setInDegree(int inDegree) {
		this.inDegree = inDegree;
	}
	public boolean isMergeable() {
		return mergeable;
	}
	public void setMergeable(boolean mergeable) {
		this.mergeable = mergeable;
	}
	public boolean isModule() {
		return module;
	}
	public void setModule(boolean module) {
		this.module = module;
	}
	public int getTotalDegree() {
		return totalDegree;
	}
	public void setTotalDegree(int totalDegree) {
		this.totalDegree = totalDegree;
	}
	public double getModularity() {
		return modularity;
	}
	public void setModularity(double modularity) {
		this.modularity = modularity;
	}
	public void calModularity(CyNetwork currentNetwork){
    	int inDegree=0;
    	int totalDegree=0;
    	ArrayList nodes=this.getALNodes();
    	for(Iterator it=nodes.iterator();it.hasNext();){//for each node in merged C1
    		Long node=((Long)it.next()).longValue();
    		
//   		totalDegree+=currentNetwork.getDegree(node);//can this be useful?
    		
    		totalDegree+=algorithm.getNodeDegree(currentNetwork,node);//can this be useful?
    		Long[] neighbors=algorithm.getNeighborArray(currentNetwork, node);
    		
    		
//    		int[] neighbors=currentNetwork.neighborsArray(node);
    		for(int i=0;i<neighbors.length;i++)
    			if(nodes.contains(new Long(neighbors[i])))
    				inDegree++;
    	}
    	int outDegree=totalDegree-inDegree;
    	inDegree=inDegree/2;
    	this.setInDegree(inDegree);
    	this.setTotalDegree(totalDegree);
    	double fModule=0;
    	if(inDegree!=0)
    		fModule = (double)inDegree/(double)outDegree;
    	else	fModule=0;
    	setModularity(fModule);
	}
	public ClusterGraph getGraph() {
		return graph;
	}
	public void setGraph(ClusterGraph graph) {
		this.graph = graph;
	}
	public List<Long> getAlCluster() {
		return alCluster;
	}
	public void setAlCluster(List<Long> alCluster) {
		this.alCluster = alCluster;
	}
	public CyNetworkView getView() {
		return view;
	}
	public void setView(CyNetworkView view) {
		this.view = view;
	}
	public synchronized void dispose() {
		// TODO Auto-generated method stub
		if (isDisposed())
			return;
		if (view != null)
			view.dispose();
		graph.dispose();
		disposed = true;
	}
	public boolean isDisposed() {
		return disposed;
	}
	public void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}
	
	
	public synchronized CySubNetwork getNetwork()
	{
		return graph.getSubNetwork();
	}


	public java.awt.Image getImage() {
		return image;
	}


	public void setImage(java.awt.Image image) {
		this.image = image;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}

	public int getAdd() {
		return add;
	}

	public void setAdd(int add) {
		this.add = add;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	public void addnode(Long suid){
		this.alNodes.add(suid);
	}
	
}
