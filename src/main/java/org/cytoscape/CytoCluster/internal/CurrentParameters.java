package org.cytoscape.CytoCluster.internal;


import java.util.HashMap;
import java.util.Map;

public class CurrentParameters
{
  private Map<Long, ParameterSet> currentParams = new HashMap<Long, ParameterSet>();
 

private Map<Integer, ParameterSet> resultParams = new HashMap<Integer, ParameterSet>();

  public ParameterSet getParamsCopy(Long networkID)
  {
    if (networkID != null) {
      return ((ParameterSet)this.currentParams.get(networkID)).copy();
    }
    ParameterSet newParams = new ParameterSet();
    return newParams.copy();
  }

  public void setParams(ParameterSet newParams, int resultId, Long networkID)
  {
    ParameterSet currentParamSet = new ParameterSet(newParams.getScope(), newParams.getSelectedNodes(), 
      newParams.isIncludeLoops(), newParams
      .getDegreeCutoff(), newParams.getKCore(), 
      newParams.isOptimize(), newParams
      .getMaxDepthFromStart(), newParams
      .getNodeScoreCutoff(), newParams.isFluff(), 
      newParams.isHaircut(), newParams
      .getFluffNodeDensityCutoff(), 
      
      newParams.getCliqueSizeThresholdEAGLE(),
      newParams.getComplexSizeThresholdEAGLE(),
      
      newParams.isOverlapped(),
      newParams.getfThresholdFAGEC(),
      newParams.getCliqueSizeThresholdEAGLE(),
      newParams.getComplexSizeThresholdFAGEC(),
      newParams.isWeakFAGEC(),
      

    //used in clustering using HC-PIN
      newParams.getfThresholdHCPIN(),
      newParams.getComplexSizeThresholdHCPIN(),
      newParams.isWeakHCPIN(),  
      
      
      
    //used in clustering using OH-PIN
      newParams.getfThresholdOHPIN(),
      newParams.getOverlappingScore(),
      
      
    //used in clustering using IPCA
      newParams.getShortestPathLength(),
      newParams.getTinThreshold(),
      newParams.getComplexSizeThresholdIPCA(),
      
      
      newParams.getAlgorithm());

    this.currentParams.put(networkID, currentParamSet);

    ParameterSet resultParamSet = new ParameterSet(newParams.getScope(), newParams.getSelectedNodes(), 
      newParams.isIncludeLoops(), newParams
      .getDegreeCutoff(), newParams.getKCore(), 
      newParams.isOptimize(), newParams
      .getMaxDepthFromStart(), newParams
      .getNodeScoreCutoff(), newParams.isFluff(), 
      newParams.isHaircut(), newParams
      .getFluffNodeDensityCutoff(),
      newParams.getCliqueSizeThresholdEAGLE(),
      newParams.getComplexSizeThresholdEAGLE(),
      
      newParams.isOverlapped(),
      newParams.getfThresholdFAGEC(),
      newParams.getCliqueSizeThresholdEAGLE(),
      newParams.getComplexSizeThresholdFAGEC(),
      newParams.isWeakFAGEC(),
      

    //used in clustering using HC-PIN
      newParams.getfThresholdHCPIN(),
      newParams.getComplexSizeThresholdHCPIN(),
      newParams.isWeakHCPIN(),  
      
      
      
    //used in clustering using OH-PIN
      newParams.getfThresholdOHPIN(),
      newParams.getOverlappingScore(),
      
      
    //used in clustering using IPCA
      newParams.getShortestPathLength(),
      newParams.getTinThreshold(),
      newParams.getComplexSizeThresholdIPCA(),
        
      newParams.getAlgorithm());

    this.resultParams.put(Integer.valueOf(resultId), resultParamSet);
  }

  public ParameterSet getResultParams(int resultId) {
    return ((ParameterSet)this.resultParams.get(Integer.valueOf(resultId))).copy();
  }

  public void removeResultParams(int resultId) {
    this.resultParams.remove(Integer.valueOf(resultId));
  }
  
  
  public Map<Long, ParameterSet> getAllParamSets() {
		return currentParams;
	}
}