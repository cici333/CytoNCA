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
  
  public static String BC = "Betweenness";
  public static String CC = "Closeness";
  public static String DC = "Degree";
  public static String EC = "Eigenvector";
  public static String LAC = "LAC";
  public static String NC = "Network";
  public static String SC = "Subgragh";
  public static String IC = "Information";
  
  public static String BCW = "Betweenness(Weight)";
  public static String CCW = "Closeness(Weight)";
  public static String DCW = "Degree(Weight)";
  public static String ECW = "Eigenvector(Weight)";
  public static String LACW = "LACW(Weight)";
  public static String NCW = "Network(Weight)";
  public static String SCW = "Subgragh(Weight)";
  public static String ICW = "Information(Weight)";
  
  public static String weight = "Weight of node";
  public static String domainNum = "Domain numbers";
  public static String sequencelength = "Sequence length";
  
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
    
    algorithmSet.put(ParameterSet.BCW, false);
    algorithmSet.put(ParameterSet.CCW, false);
    algorithmSet.put(ParameterSet.DCW, false);
    algorithmSet.put(ParameterSet.ECW, false);
    algorithmSet.put(ParameterSet.LACW, false);
    algorithmSet.put(ParameterSet.NCW, false);
    algorithmSet.put(ParameterSet.SCW, false);
    algorithmSet.put(ParameterSet.ICW, false);
	  
	  
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

