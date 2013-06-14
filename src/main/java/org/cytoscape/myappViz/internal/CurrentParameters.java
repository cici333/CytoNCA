package org.cytoscape.myappViz.internal;


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
      
      
//    this.nodeDensityThreshold=nodeDensityThreshold;
      newParams.getCliqueSizeThreshold1(),
      newParams.getComplexSizeThreshold1(),
      newParams.isOverlapped(),
      newParams.getfThreshold(),
      newParams.getCliqueSizeThreshold(),
      newParams.getComplexSizeThreshold(),
      newParams.isWeak(),
      newParams.getOverlappingScore(),
      newParams.getShortestPathLength(),
      newParams.getTinThreshold(),
      
      
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
      newParams.getCliqueSizeThreshold1(),
      newParams.getComplexSizeThreshold1(),
      newParams.isOverlapped(),
      newParams.getfThreshold(),
      newParams.getCliqueSizeThreshold(),
      newParams.getComplexSizeThreshold(),
        newParams.isWeak(),
        newParams.getOverlappingScore(), 
        newParams.getShortestPathLength(),
        newParams.getTinThreshold(),
        
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