package org.cytoscape.CytoNCA.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ParameterSet
{
  public static String NETWORK = "network";
  public static String SELECTION = "selection";
  private String scope;
  private Long[] selectedNodes; 
  private int defaultRowHeight;
  private ArrayList<Protein> eprotein;
  
  public static String BC = "BC";
  public static String CC = "CC";
  public static String DC = "DC";
  public static String EC = "EC";
  public static String LAC = "LAC";
  public static String NC = "NC";
  public static String SC = "SC";
  public static String IC = "IC";
  
  public static int analyze = 1;
  public static int openeplist = 2;
  
  private HashMap<String, Boolean> algorithmSet;
  
  
  
  public ParameterSet()
  {
    setDefaultParams();
    this.defaultRowHeight = 40;
  }
  
  

  public ParameterSet(String scope, Long[] selectedNodes, HashMap<String, Boolean> algorithmSet, ArrayList<Protein> eprotein)
  {
    setAllAlgorithmParams(scope, selectedNodes, algorithmSet, eprotein);
    this.defaultRowHeight = 40;
  //  eproteins = new HashMap<Long, ArrayList<String>>();
  }

  public void setDefaultParams()
  {
	  HashMap algorithmSet = new HashMap<String, Boolean>();
    algorithmSet.put(ParameterSet.BC, false);
    algorithmSet.put(ParameterSet.CC, false);
    algorithmSet.put(ParameterSet.DC, false);
    algorithmSet.put(ParameterSet.EC, false);
    algorithmSet.put(ParameterSet.LAC, false);
    algorithmSet.put(ParameterSet.NC, false);
    algorithmSet.put(ParameterSet.SC, false);
    algorithmSet.put(ParameterSet.IC, false);
	  
	  
	  setAllAlgorithmParams(NETWORK,  new Long[0], algorithmSet, new ArrayList<Protein>()); 
  }

  public void setAllAlgorithmParams(String scope, Long[] selectedNodes, HashMap<String, Boolean> algorithmSet, ArrayList<Protein> eprotein)
  {
    this.scope = scope;
    this.selectedNodes = selectedNodes;
    this.algorithmSet=algorithmSet;
    this.eprotein = eprotein;

  }

  public ParameterSet copy()
  {
	ParameterSet newParam = new ParameterSet();
    newParam.setScope(this.scope);
    newParam.setSelectedNodes(this.selectedNodes);
    newParam.setAlgorithmSet(this.algorithmSet);
    newParam.eprotein = this.eprotein;
 
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


  public int getDefaultRowHeight() {
    return this.defaultRowHeight;
  }

  public void setDefaultRowHeight(int defaultRowHeight) {
    this.defaultRowHeight = defaultRowHeight;
  }

  




public ArrayList<Protein> getEprotein() {
	return eprotein;
}



public void setEprotein(ArrayList<Protein> eprotein) {
	this.eprotein = eprotein;
}



public HashMap<String, Boolean> getAlgorithmSet() {
	return algorithmSet;
}



public void setAlgorithmSet(HashMap<String, Boolean> algorithmSet) {
	this.algorithmSet = algorithmSet;
}











}

