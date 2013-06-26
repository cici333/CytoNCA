package org.cytoscape.CytoCluster.internal;
/*
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.cytoscape.myappViz.internal.algorithm.Algorithm;


*//**
 * the set of all the parameters used in clustering
 *//*
public class ParameterSet {
    private static ParameterSet ourInstance = new ParameterSet();
    private static HashMap<Long, ParameterSet> currentParams = new HashMap<Long, ParameterSet>();
    private static HashMap<Integer, ParameterSet> resultParams = new HashMap<Integer, ParameterSet>();
    
    //parameters
    public Long networkID;
    public int resultId;
    //scope
    public static String NETWORK = "network";
    public static String SELECTION = "selection";
    private String scope;
    private Long[] selectedNodes;    
    //algorithm
    public static String MCODE = "MCODE";
    public static String EAGLE = "EAGLE";
    public static String FAGEC = "FAG-EC";
    private String algorithm;
	public Algorithm alg;
    public void setAlg(Algorithm alg) {
		this.alg = alg;
	}
	//parameters used in MCODE
    //used in scoring stage
    private boolean includeLoops;
    private int degreeThreshold;
    private int kCore;
    //used in cluster finding stage
    private boolean optimize;
    private int maxDepthFromStart;
    private double nodeScoreThreshold;
    private boolean fluff;
    private boolean haircut;
    private double nodeDensityThreshold;   
    //parameter used when clustering using EAGLE
    private int cliqueSizeThreshold1;
    private int complexSizeThreshold1;  
    //used in clustering using FAG-EC
    private boolean overlapped;
    private double fThreshold;
    private int cliqueSizeThreshold;
    private int complexSizeThreshold;
    private boolean isWeak;
    //result viewing parameters (only used for dialog box of results)
    private int defaultRowHeight;
    private ClusterUtil clusterUtil;

    public ClusterUtil getClusterUtil() {
		return clusterUtil;
	}
	public void setClusterUtil(ClusterUtil clusterUtil) {
		this.clusterUtil = clusterUtil;
	}
	*//**
     * Constructor for the parameter set object. 
     *//*
    public ParameterSet() {
        setDefaultParams();
        defaultRowHeight = 80;
    }
    *//**
     * Constructor for non-default algorithm parameters.
     * Once an alalysis is conducted, new parameters must be saved so that they can be retrieved in the result panel
     * for exploration and export purposes.
     *//*    
    public ParameterSet(
    		Long networkID,
            String scope,
            String algorithm,
            Long[] selectedNodes,
            boolean includeLoops,
            int degreeThrshold,
            int kCore,
            boolean optimize,
            int maxDepthFromStart,
            double nodeScoreThreshold,
            boolean fluff,
            boolean haircut,
            double nodeDensityThreshold,
            int cliqueSizeThreshold1,
            int complexSizeThreshold1,
            double fThreshold,
            int cliqueSizeThreshold,
            int complexSizeThreshold,
            boolean isWeak,
            boolean overlapped,
            ClusterUtil clusterUtil
            ) {
       
    	setAllAlgorithmParams(
        		networkID,
                scope,
                algorithm,
                selectedNodes,
                includeLoops,
                degreeThrshold,
                kCore,
                optimize,
                maxDepthFromStart, 
                nodeScoreThreshold,
                fluff,
                haircut,
                nodeDensityThreshold,
                cliqueSizeThreshold1,
                complexSizeThreshold1,
                fThreshold,
                cliqueSizeThreshold,
                complexSizeThreshold,
                isWeak,
                overlapped,
                clusterUtil
        );
        defaultRowHeight = 80;
    }
    *//**
     * staic method to be used with getParamsCopy(String networkID) 
     * @return ourInstance the static instance of CurrentParameter
     *//*
    public static ParameterSet getInstance() {
        return ourInstance;
    }
    *//**
     * Get a copy of the current parameters for a particular network. 
     * usage:
     * Parameters.getInstance().getParamsCopy();  
     *//*
    public ParameterSet getParamsCopy(Long networkID) {
    	if (networkID != null) {
            return ((ParameterSet) currentParams.get(networkID)).copy();
        } else {
            ParameterSet newParams = new ParameterSet();
            return newParams.copy();
        }
    }
    
    public HashMap getAllParamSets(){
    	return resultParams;
    }
    
    public ParameterSet getResultParams(int resultId) {
        return ((ParameterSet) resultParams.get(resultId));
    }
    
    public static void removeResultParams(int resultId) {
        resultParams.remove(resultId);
    } 
    
    public void removeResultParams(int resultId)
	{
		resultParams.remove(Integer.valueOf(resultId));
	}
    *//**
     * Current parameters can only be updated using this method.
     *//*
    public ParameterSet setParams(ParameterSet newParams, int resultId, Long networkID) {
        //cannot simply equate the params and newParams classes since that creates a permanent reference
        //and prevents us from keeping 2 sets of the class such that the saved version is not altered
        //until this method is called
        ParameterSet currentParamSet = new ParameterSet(
        		newParams.getNetworkID(),
                newParams.getScope(),
                newParams.getAlgorithm(),
                newParams.getSelectedNodes(),
                newParams.isIncludeLoops(),
                newParams.getDegreeThreshold(),
                newParams.getKCore(),
                newParams.isOptimize(),
                newParams.getMaxDepthFromStart(),
                newParams.getNodeScoreCutoff(),
                newParams.isFluff(),
                newParams.isHaircut(),
                newParams.getFluffNodeDensityCutoff(),
                newParams.getCliqueSizeThreshold1(),
                newParams.getComplexSizeThreshold1(),
                newParams.getFThreshold(),
                newParams.getCliqueSizeThreshold(),
                newParams.getComplexSizeThreshold(),
                newParams.isWeak(),
                newParams.isOverlapped(),
                newParams.getClusterUtil()
                
        );
        //replace with new value
        currentParams.put(networkID, currentParamSet);
        ParameterSet resultParamSet = new ParameterSet(
        		newParams.getNetworkID(),
                newParams.getScope(),
                newParams.getAlgorithm(),
                newParams.getSelectedNodes(),
                newParams.isIncludeLoops(),
                newParams.getDegreeThreshold(),
                newParams.getKCore(),
                newParams.isOptimize(),
                newParams.getMaxDepthFromStart(),
                newParams.getNodeScoreCutoff(),
                newParams.isFluff(),
                newParams.isHaircut(),
                newParams.getFluffNodeDensityCutoff(),
                newParams.getCliqueSizeThreshold1(),
                newParams.getComplexSizeThreshold1(),
                newParams.getFThreshold(),
                newParams.getCliqueSizeThreshold(),
                newParams.getComplexSizeThreshold(),
                newParams.isWeak(),
                newParams.isOverlapped(),
                newParams.getClusterUtil()
        );
        resultParams.put(resultId, resultParamSet);
        return resultParamSet;
    }
    *//**
     * Method for setting all parameters to their default values
     *//*
    public void setDefaultParams() {
    //    setAllAlgorithmParams(Cytoscape.getCurrentNetwork().getIdentifier(), NETWORK, "", new Integer[0], 
      //  		false, 2, 2, false, 100, 0.2, false, true, 0.1, 3, 2, 1.0, 3, 2, true,false);
    	setAllAlgorithmParams(null netword suid, NETWORK, "", new Long[0], 
    		false, 2, 2, false, 100, 0.2, false, true, 0.1, 3, 2, 1.0, 3, 2, true,false,null);
    //currentParams.put(networkID, this);
    }

    *//**
     * Convenience method to set all the main algorithm parameters
     * 
     * @param networkID the identifier of the network
     * @param scope Scope of the search (equal to one of the two fields NETWORK or SELECTION)
     * @param algorithm The algorithm user choosed to cluster the network
     * @param selectedNodes Node selection for selection-based scope
     * @param includeLoops include loops or not
     * @param degreeThreshold degree threshold
     * @param kCore the value of k in K-Core
     * @param optimize Determines if parameters are customized by user/default or optimized
     * @param maxDepthFromStart max depth from seed node
     * @param nodeScoreThreshold node score threshold
     * @param fluff fluff the resulting clusters or not
     * @param haircut haircut the clusters or not
     * @param nodeDensityThreshold nodedesity thrshold
     * @param cliqueSizeThreshold1
     * @param complexSizeThreshold1
     * @param fThreshold
     * @param cliqueSizeThreshold
     * @param complexSizeThreshold
     * @param isWeak
     * @param overlapped
     *//*
    public void setAllAlgorithmParams(
    		Long networkID,
            String scope,
            String algorithm,
            Long[] selectedNodes,
            boolean includeLoops,
            int degreeThreshold,
            int kCore,
            boolean optimize,
            int maxDepthFromStart,
            double nodeScoreThreshold,
            boolean fluff,
            boolean haircut,
            double nodeDensityThreshold,
            int cliqueSizeThreshold1,
            int complexSizeThreshold1,
            double fThreshold,
            int cliqueSizeThreshold,
            int complexSizeThreshold,
            boolean isWeak,
            boolean overlapped,
            ClusterUtil clusterUtil) {
    	this.networkID=networkID;
        this.scope = scope;
        this.algorithm=algorithm;
        this.selectedNodes = selectedNodes;
        this.includeLoops = includeLoops;
        this.degreeThreshold = degreeThreshold;
        this.kCore = kCore;
        this.optimize = optimize;
        this.maxDepthFromStart = maxDepthFromStart;
        this.nodeScoreThreshold = nodeScoreThreshold;
        this.fluff = fluff;
        this.haircut = haircut;
        this.nodeDensityThreshold = nodeDensityThreshold;
        this.cliqueSizeThreshold1=cliqueSizeThreshold1;
        this.complexSizeThreshold1=complexSizeThreshold1;
        this.fThreshold=fThreshold;
        this.cliqueSizeThreshold=cliqueSizeThreshold;
        this.complexSizeThreshold=complexSizeThreshold;
        this.isWeak=isWeak;
        this.overlapped=overlapped;
        this.clusterUtil=clusterUtil;
    }

    *//**
     * Copies a parameter set object
     *
     * @return A copy of the parameter set
     *//*
    public ParameterSet copy() {
        ParameterSet newParam = new ParameterSet();
        newParam.setNetworkID(this.networkID);
        newParam.setScope(this.scope);
        newParam.setAlgorithm(this.algorithm,this.clusterUtil);
        newParam.setSelectedNodes(this.selectedNodes);
        newParam.setIncludeLoops(this.includeLoops);
        newParam.setDegreeThreshold(this.degreeThreshold);
        newParam.setKCore(this.kCore);
        newParam.setOptimize(this.optimize);
        newParam.setMaxDepthFromStart(this.maxDepthFromStart);
        newParam.setNodeScoreCutoff(this.nodeScoreThreshold);
        newParam.setFluff(this.fluff);
        newParam.setHaircut(this.haircut);
        newParam.setFluffNodeDensityCutoff(this.nodeDensityThreshold);
        newParam.setCliqueSizeThreshold1(this.cliqueSizeThreshold1);
        newParam.setComplexSizeThreshold1(this.complexSizeThreshold1);
        newParam.setCliqueSizeThreshold(this.cliqueSizeThreshold);
        newParam.setComplexSizeThreshold(this.complexSizeThreshold);
        newParam.setFThreshold(this.fThreshold);
        newParam.setWeak(this.isWeak);
        newParam.setOverlapped(this.overlapped);
        //results dialog box
        newParam.setDefaultRowHeight(this.defaultRowHeight);
        return newParam;
    }
		
	*//**
     * Generates a summary of the parameters. Only parameters that are necessary are included.
     * For example, if fluff is not turned on, the fluff density cutoff will not be included.
     * 
     * @return Buffered string summarizing the parameters
     *//*
	
    public String toString() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
    	sb.append("   Network: "+networkID);
        if(algorithm.equals(MCODE)){
        	sb.append("   Algorithm:  MCODE"+lineSep);
            sb.append("   Scoring:" + lineSep
                    + "      IncludeLoop: " + includeLoops + "  DegreeThreshold: " + degreeThreshold + lineSep);
            sb.append("   Clustering:" + lineSep
                    + "      NodeScoreThreshold: " + nodeScoreThreshold + "  Haircut: " + haircut +lineSep
                    + "      Fluff: " + fluff + ((fluff) ? ("  FluffNodeDensityThreshold " + nodeDensityThreshold) : "")+lineSep
                    + "      K-Core: " + kCore + "  Max.DepthFromSeed: " + maxDepthFromStart + lineSep);
        }
        else if(algorithm.equals(FAGEC)){
        	sb.append("   Algorithm:  FAG-EC"+lineSep);
            sb.append("   Clustering:" + lineSep
                    + "      DefinitionWay: " + ((isWeak)? ("Weak  In/OutThreshold: "+fThreshold ):"Strong") + lineSep
                    + "      Overlapped: " + overlapped + ((overlapped)? (" CliqueSizeThreshold: "+cliqueSizeThreshold ):"")+lineSep
                    + "      OutputThreshold: " + complexSizeThreshold + lineSep);        	
        }
        else if(algorithm.equals(EAGLE)){
        	sb.append("   Algorithm:  EAGLE"+lineSep);
            sb.append("   Clustering:" + lineSep
                    + "      CliqueSizeThrshold: " + cliqueSizeThreshold1 
                    + "  OutputThreshold: " + complexSizeThreshold1 + lineSep);
        }
        return sb.toString();
    }

    //parameter getting and setting	
    public Long getNetworkID() {
		return networkID;
	}
    
	public void setNetworkID(Long networkID) {
		this.networkID = networkID;
	}
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }

	*//**
	 * Get algorithm instance
	 *//*
	public Algorithm getAlg(){
		return this.alg;
	}

    public void setAlgorithm(String algorithm,ClusterUtil clusterUtil) {
        this.algorithm = algorithm;
        this.clusterUtil= clusterUtil;
		if(this.algorithm.equals(ParameterSet.MCODE)){
			this.alg = new org.cytoscape.myappViz.internal.algorithm.MCODE(this.getNetworkID(),clusterUtil);
		}if(this.algorithm.equals(ParameterSet.FAGEC)){
			this.alg = new org.cytoscape.myappViz.internal.algorithm.FAGEC(this.getNetworkID(),clusterUtil);
		}if(this.algorithm.equals(ParameterSet.EAGLE)){
			this.alg = new org.cytoscape.myappViz.internal.algorithm.EAGLE(this.getNetworkID(),clusterUtil);
		}else{
			}
		
    }

    public Long[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(Long[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public boolean isIncludeLoops() {
        return includeLoops;
    }

    public void setIncludeLoops(boolean includeLoops) {
        this.includeLoops = includeLoops;
    }

    public int getDegreeThreshold() {
        return degreeThreshold;
    }

    public void setDegreeThreshold(int degreeThreshold) {
        this.degreeThreshold = degreeThreshold;
    }

    public int getKCore() {
        return kCore;
    }

    public void setKCore(int kCore) {
        this.kCore = kCore;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public int getMaxDepthFromStart() {
        return maxDepthFromStart;
    }

    public void setMaxDepthFromStart(int maxDepthFromStart) {
        this.maxDepthFromStart = maxDepthFromStart;
    }

    public double getNodeScoreCutoff() {
        return nodeScoreThreshold;
    }

    public void setNodeScoreCutoff(double nodeScoreThreshold) {
        this.nodeScoreThreshold = nodeScoreThreshold;
    }

    public boolean isFluff() {
        return fluff;
    }

    public void setFluff(boolean fluff) {
        this.fluff = fluff;
    }

    public boolean isHaircut() {
        return haircut;
    }

    public void setHaircut(boolean haircut) {
        this.haircut = haircut;
    }

    public double getFluffNodeDensityCutoff() {
        return nodeDensityThreshold;
    }

    public void setFluffNodeDensityCutoff(double nodeDensityThreshold) {
        this.nodeDensityThreshold = nodeDensityThreshold;
    }

    public int getDefaultRowHeight() {
        return defaultRowHeight;
    }

    public void setDefaultRowHeight(int defaultRowHeight) {
        this.defaultRowHeight = defaultRowHeight;
    }

	public int getCliqueSizeThreshold1() {
		return cliqueSizeThreshold1;
	}

	public void setCliqueSizeThreshold1(int cliqueSizeThreshold1) {
		this.cliqueSizeThreshold1 = cliqueSizeThreshold1;
	}

	public int getComplexSizeThreshold1() {
		return complexSizeThreshold1;
	}

	public void setComplexSizeThreshold1(int complexSizeThreshold1) {
		this.complexSizeThreshold1 = complexSizeThreshold1;
	}

	public int getCliqueSizeThreshold() {
		return cliqueSizeThreshold;
	}

	public void setCliqueSizeThreshold(int cliqueSizeThreshold) {
		this.cliqueSizeThreshold = cliqueSizeThreshold;
	}

	public int getComplexSizeThreshold() {
		return complexSizeThreshold;
	}

	public void setComplexSizeThreshold(int complexSizeThreshold) {
		this.complexSizeThreshold = complexSizeThreshold;
	}

	public double getFThreshold() {
		return fThreshold;
	}

	public void setFThreshold(double fThreshold) {
		this.fThreshold = fThreshold;
	}

	public boolean isWeak() {
		return isWeak;
	}

	public void setWeak(boolean isWeak) {
		this.isWeak = isWeak;
	}

    public boolean isOverlapped() {
		return overlapped;
	}

	public void setOverlapped(boolean overlapped) {
		this.overlapped = overlapped;
	}	
		
    public int getResultTitle() {
		return resultId;
	}
	public void setResultTitle(int resultId) {
		this.resultId = resultId;
	}
}
*/


public class ParameterSet
{
  public static String NETWORK = "network";
  public static String SELECTION = "selection";
  private String scope;
  private Long[] selectedNodes;
  
  
  //parameter used when clustering using MCODE 
  private boolean includeLoops;
  private int degreeCutoff;
  private int kCore;
  private boolean optimize;
  private int maxDepthFromStart;
  private double nodeScoreCutoff;
  private boolean fluff;
  private boolean haircut;
  private double fluffNodeDensityCutoff;
  
  private int defaultRowHeight;
  
  
 
  
  //parameter used when clustering using EAGLE
  private int cliqueSizeThresholdEAGLE;
  private int complexSizeThresholdEAGLE;  
  
  //used in clustering using FAG-EC
  private boolean overlapped;
  private double fThresholdFAGEC;
  private int cliqueSizeThresholdFAGEC;
  private int complexSizeThresholdFAGEC;
  private boolean isWeakFAGEC;
  

//used in clustering using HC-PIN
  private double fThresholdHCPIN;
  private int complexSizeThresholdHCPIN;
  private boolean isWeakHCPIN;  
  
  
  
//used in clustering using OH-PIN
  private double fThresholdOHPIN;
  private double OverlappingScore;
  
  
//used in clustering using IPCA
  private int ShortestPathLength;
  private double TinThreshold;
  private int complexSizeThresholdIPCA;
  
  //result viewing parameters (only used for dialog box of results)
  
  
  
  
  public static String MCODE = "MCODE";
  public static String EAGLE = "EAGLE";
  public static String FAGEC = "FAG-EC";
  public static String HCPIN = "HC-PIN";
  public static String OHPIN = "OH-PIN";
  public static String IPCA = "IPCA";
  private String algorithm;
  public ParameterSet()
  {
    setDefaultParams();

    this.defaultRowHeight = 80;
  }

  
  

  public ParameterSet(String scope, Long[] selectedNodes, boolean includeLoops, int degreeCutoff, 
		  int kCore, boolean optimize, int maxDepthFromStart, double nodeScoreCutoff, boolean fluff,
		  boolean haircut, double fluffNodeDensityCutoff,
		/*  int cliqueSizeThreshold1,
		   int complexSizeThreshold1,  boolean overlapped,
		   double fThreshold,  int cliqueSizeThreshold,
		   int complexSizeThreshold,  boolean isWeak, 
		   double OverlappingScore , 
		   int ShortestPathLength, double TinThreshold,*/
		  
		  int cliqueSizeThresholdEAGLE, int complexSizeThresholdEAGLE,  
		  boolean overlapped, double fThresholdFAGEC, int cliqueSizeThresholdFAGEC, int complexSizeThresholdFAGEC, boolean isWeakFAGEC,
		 double fThresholdHCPIN, int complexSizeThresholdHCPIN, boolean isWeakHCPIN,
		 double fThresholdOHPIN, double OverlappingScore,
		 int ShortestPathLength, double TinThreshold, int complexSizeThresholdIPCA,
		  
		  
		  
		  
		  String algorithm)
  {
    setAllAlgorithmParams(scope, 
      selectedNodes, 
      includeLoops, 
      degreeCutoff, 
      kCore, 
      optimize, 
      maxDepthFromStart, 
      nodeScoreCutoff, 
      fluff, 
      haircut, 
      fluffNodeDensityCutoff,
      
      cliqueSizeThresholdEAGLE,
      complexSizeThresholdEAGLE,
      
	  overlapped,
	  fThresholdFAGEC,
	  cliqueSizeThresholdFAGEC,
	  complexSizeThresholdFAGEC,
	  isWeakFAGEC,
	  
	  fThresholdHCPIN,
	  complexSizeThresholdHCPIN,
	  isWeakHCPIN,
	  

	  fThresholdOHPIN,
	  OverlappingScore,
	  
	  ShortestPathLength,
	  TinThreshold,
	  complexSizeThresholdIPCA,
   
      
      algorithm);
      

    this.defaultRowHeight = 80;
  }

  public void setDefaultParams()
  {
    setAllAlgorithmParams(NETWORK,  new Long[0], 	
    		true, 2, 2, true, 100, 0.2,true,true,0.1,  
    		3, 3, 
    		false,2.0, 3, 3,true,
    		2.0, 3, true,
    		2.0, 0.5, 
    		2 , 0.5, 2,
    		"");
    
    
    
    
    
  }

  public void setAllAlgorithmParams(String scope, Long[] selectedNodes, 
		  boolean includeLoops,int degreeCutoff, int kCore, boolean optimize, int maxDepthFromStart,double nodeScoreCutoff, boolean fluff, boolean haircut, double fluffNodeDensityCutoff,
		  int cliqueSizeThresholdEAGLE, int complexSizeThresholdEAGLE,  
		  boolean overlapped, double fThresholdFAGEC, int cliqueSizeThresholdFAGEC, int complexSizeThresholdFAGEC, boolean isWeakFAGEC,
		  double fThresholdHCPIN, int complexSizeThresholdHCPIN, boolean isWeakHCPIN,
		  double fThresholdOHPIN, double OverlappingScore,
		  int ShortestPathLength, double TinThreshold, int complexSizeThresholdIPCA,	  
		  
		  String algorithm)
  {
    this.scope = scope;
    this.selectedNodes = selectedNodes;
    this.includeLoops = includeLoops;
    this.degreeCutoff = degreeCutoff;
    this.kCore = kCore;
    this.optimize = optimize;
    this.maxDepthFromStart = maxDepthFromStart;
    this.nodeScoreCutoff = nodeScoreCutoff;
    this.fluff = fluff;
    this.haircut = haircut;
    this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
    
    //parameter used when clustering using EAGLE
    this.cliqueSizeThresholdEAGLE = cliqueSizeThresholdEAGLE;
    this.complexSizeThresholdEAGLE = complexSizeThresholdEAGLE;  
    
    //used in clustering using FAG-EC
    this.overlapped = overlapped;
    this.fThresholdFAGEC = fThresholdFAGEC;
    this.cliqueSizeThresholdFAGEC = cliqueSizeThresholdFAGEC;
    this.complexSizeThresholdFAGEC = complexSizeThresholdFAGEC;
    this.isWeakFAGEC = isWeakFAGEC;
    

  //used in clustering using HC-PIN
    this.fThresholdHCPIN = fThresholdHCPIN;
    this.complexSizeThresholdHCPIN = complexSizeThresholdHCPIN;
    this.isWeakHCPIN = isWeakHCPIN;  
    
    
    
  //used in clustering using OH-PIN
    this.fThresholdOHPIN = fThresholdOHPIN;
    this.OverlappingScore = OverlappingScore;
    
    
  //used in clustering using IPCA
    this.ShortestPathLength = ShortestPathLength;
    this.TinThreshold = TinThreshold;
    this.complexSizeThresholdIPCA = complexSizeThresholdIPCA;
    
    this.algorithm=algorithm;
  }

  public ParameterSet copy()
  {
	  ParameterSet newParam = new ParameterSet();
    newParam.setScope(this.scope);
    newParam.setSelectedNodes(this.selectedNodes);
    newParam.setIncludeLoops(this.includeLoops);
    newParam.setDegreeCutoff(this.degreeCutoff);
    newParam.setKCore(this.kCore);
    newParam.setOptimize(this.optimize);
    newParam.setMaxDepthFromStart(this.maxDepthFromStart);
    newParam.setNodeScoreCutoff(this.nodeScoreCutoff);
    newParam.setFluff(this.fluff);
    newParam.setHaircut(this.haircut);
    newParam.setFluffNodeDensityCutoff(this.fluffNodeDensityCutoff);

    newParam.setDefaultRowHeight(this.defaultRowHeight);
    
    newParam.setCliqueSizeThresholdEAGLE(this.cliqueSizeThresholdEAGLE);
    newParam.setComplexSizeThresholdEAGLE(this.complexSizeThresholdEAGLE);
    
    newParam.setOverlapped(this.overlapped);
    newParam.setfThresholdFAGEC(this.fThresholdFAGEC);
    newParam.setCliqueSizeThresholdEAGLE(this.cliqueSizeThresholdFAGEC);
    newParam.setComplexSizeThresholdFAGEC(this.complexSizeThresholdFAGEC);
    newParam.setWeakFAGEC(this.isWeakFAGEC);
    

  //used in clustering using HC-PIN
    newParam.setfThresholdHCPIN(this.fThresholdHCPIN);
    newParam.setComplexSizeThresholdHCPIN(this.complexSizeThresholdHCPIN);
    newParam.setWeakHCPIN(this.isWeakHCPIN);  
    
    
    
  //used in clustering using OH-PIN
    newParam.setfThresholdOHPIN(this.fThresholdOHPIN);
    newParam.setOverlappingScore(this.OverlappingScore);
    
    
  //used in clustering using IPCA
    newParam.setShortestPathLength(this.ShortestPathLength);
    newParam.setTinThreshold(this.TinThreshold);
    newParam.setComplexSizeThresholdIPCA(this.complexSizeThresholdIPCA);
    
    newParam.setAlgorithm(this.algorithm);
    return newParam;
  }

  public String getScope()
  {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public Long[] getSelectedNodes() {
    return this.selectedNodes;
  }

  public void setSelectedNodes(Long[] selectedNodes) {
    this.selectedNodes = selectedNodes;
  }

  public boolean isIncludeLoops() {
    return this.includeLoops;
  }

  public void setIncludeLoops(boolean includeLoops) {
    this.includeLoops = includeLoops;
  }

  public int getDegreeCutoff() {
    return this.degreeCutoff;
  }

  public void setDegreeCutoff(int degreeCutoff) {
    this.degreeCutoff = degreeCutoff;
  }

  public int getKCore() {
    return this.kCore;
  }

  public void setKCore(int kCore) {
    this.kCore = kCore;
  }

  public void setOptimize(boolean optimize) {
    this.optimize = optimize;
  }

  public boolean isOptimize() {
    return this.optimize;
  }

  public int getMaxDepthFromStart() {
    return this.maxDepthFromStart;
  }

  public void setMaxDepthFromStart(int maxDepthFromStart) {
    this.maxDepthFromStart = maxDepthFromStart;
  }

  public double getNodeScoreCutoff() {
    return this.nodeScoreCutoff;
  }

  public void setNodeScoreCutoff(double nodeScoreCutoff) {
    this.nodeScoreCutoff = nodeScoreCutoff;
  }

  public boolean isFluff() {
    return this.fluff;
  }

  public void setFluff(boolean fluff) {
    this.fluff = fluff;
  }

  public boolean isHaircut() {
    return this.haircut;
  }

  public void setHaircut(boolean haircut) {
    this.haircut = haircut;
  }

  public double getFluffNodeDensityCutoff() {
    return this.fluffNodeDensityCutoff;
  }

  public void setFluffNodeDensityCutoff(double fluffNodeDensityCutoff) {
    this.fluffNodeDensityCutoff = fluffNodeDensityCutoff;
  }

  public int getDefaultRowHeight() {
    return this.defaultRowHeight;
  }

  public void setDefaultRowHeight(int defaultRowHeight) {
    this.defaultRowHeight = defaultRowHeight;
  }

  
public String getAlgorithm() {
	return algorithm;
}

public void setAlgorithm(String algorithm) {
	this.algorithm = algorithm;
}




public int getCliqueSizeThresholdEAGLE() {
	return cliqueSizeThresholdEAGLE;
}




public void setCliqueSizeThresholdEAGLE(int cliqueSizeThresholdEAGLE) {
	this.cliqueSizeThresholdEAGLE = cliqueSizeThresholdEAGLE;
}




public int getComplexSizeThresholdEAGLE() {
	return complexSizeThresholdEAGLE;
}




public void setComplexSizeThresholdEAGLE(int complexSizeThresholdEAGLE) {
	this.complexSizeThresholdEAGLE = complexSizeThresholdEAGLE;
}




public boolean isOverlapped() {
	return overlapped;
}




public void setOverlapped(boolean overlapped) {
	this.overlapped = overlapped;
}




public double getfThresholdFAGEC() {
	return fThresholdFAGEC;
}




public void setfThresholdFAGEC(double fThresholdFAGEC) {
	this.fThresholdFAGEC = fThresholdFAGEC;
}




public int getCliqueSizeThresholdFAGEC() {
	return cliqueSizeThresholdFAGEC;
}




public void setCliqueSizeThresholdFAGEC(int cliqueSizeThresholdFAGEC) {
	this.cliqueSizeThresholdFAGEC = cliqueSizeThresholdFAGEC;
}




public int getComplexSizeThresholdFAGEC() {
	return complexSizeThresholdFAGEC;
}




public void setComplexSizeThresholdFAGEC(int complexSizeThresholdFAGEC) {
	this.complexSizeThresholdFAGEC = complexSizeThresholdFAGEC;
}




public boolean isWeakFAGEC() {
	return isWeakFAGEC;
}




public void setWeakFAGEC(boolean isWeakFAGEC) {
	this.isWeakFAGEC = isWeakFAGEC;
}




public double getfThresholdHCPIN() {
	return fThresholdHCPIN;
}




public void setfThresholdHCPIN(double fThresholdHCPIN) {
	this.fThresholdHCPIN = fThresholdHCPIN;
}




public int getComplexSizeThresholdHCPIN() {
	return complexSizeThresholdHCPIN;
}




public void setComplexSizeThresholdHCPIN(int complexSizeThresholdHCPIN) {
	this.complexSizeThresholdHCPIN = complexSizeThresholdHCPIN;
}




public boolean isWeakHCPIN() {
	return isWeakHCPIN;
}




public void setWeakHCPIN(boolean isWeakHCPIN) {
	this.isWeakHCPIN = isWeakHCPIN;
}




public double getfThresholdOHPIN() {
	return fThresholdOHPIN;
}




public void setfThresholdOHPIN(double fThresholdOHPIN) {
	this.fThresholdOHPIN = fThresholdOHPIN;
}




public double getOverlappingScore() {
	return OverlappingScore;
}




public void setOverlappingScore(double overlappingScore) {
	OverlappingScore = overlappingScore;
}




public int getShortestPathLength() {
	return ShortestPathLength;
}




public void setShortestPathLength(int shortestPathLength) {
	ShortestPathLength = shortestPathLength;
}




public double getTinThreshold() {
	return TinThreshold;
}




public void setTinThreshold(double tinThreshold) {
	TinThreshold = tinThreshold;
}




public int getComplexSizeThresholdIPCA() {
	return complexSizeThresholdIPCA;
}




public void setComplexSizeThresholdIPCA(int complexSizeThresholdIPCA) {
	this.complexSizeThresholdIPCA = complexSizeThresholdIPCA;
}




public String toString()
{
  String lineSep = System.getProperty("line.separator");
  StringBuffer sb = new StringBuffer();
  sb.append("   Network Scoring:" + lineSep + "      Include Loops: " + this.includeLoops + "  Degree Cutoff: " + 
    this.degreeCutoff + lineSep);
  sb.append("   Cluster Finding:" + lineSep + "      Node Score Cutoff: " + this.nodeScoreCutoff + "  Haircut: " + 
    this.haircut + "  Fluff: " + this.fluff + (
    this.fluff ? "  Fluff Density Cutoff " + this.fluffNodeDensityCutoff : "") + "  K-Core: " + this.kCore + 
    "  Max. Depth from Seed: " + this.maxDepthFromStart + lineSep);
  return sb.toString();
}


}

