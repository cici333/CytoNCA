package org.cytoscape.CytoNCA.internal;


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
      newParams.getAlgorithmSet(), newParams.getEprotein());
    
    this.currentParams.put(networkID, currentParamSet);

    ParameterSet resultParamSet = new ParameterSet(newParams.getScope(), newParams.getSelectedNodes(), 
      newParams.getAlgorithmSet(), newParams.getEprotein());
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