package org.cytoscape.CytoNCA.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class Protein {
	private CyNode n ;
	private int id;
	private String name;
	private CyNetwork network;
	private double CC = 0;
	private double BC = 0;
	private double DC = 0;
	private double EC = 0;
	private double LAC = 0;
	private double NC = 0;
	private double SC = 0;
	private double IC = 0;
	
	public Protein(CyNode n, CyNetwork network){
		this.network = network;
		this.n = n;
		name = network.getRow(n).get("name", String.class);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CyNetwork getNetwork() {
		return network;
	}
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}
	public CyNode getN() {
		return n;
	}
	public void setN(CyNode n) {
		this.n = n;
	}
	public double getCC() {
		return CC;
	}
	public void setCC(double cC) {
		CC = cC;
	}
	public double getBC() {
		return BC;
	}
	public void setBC(double bC) {
		BC = bC;
	}
	public double getDC() {
		return DC;
	}
	public void setDC(double dC) {
		DC = dC;
	}
	public double getEC() {
		return EC;
	}
	public void setEC(double eC) {
		EC = eC;
	}
	public double getLAC() {
		return LAC;
	}
	public void setLAC(double lAC) {
		LAC = lAC;
	}
	public double getNC() {
		return NC;
	}
	public void setNC(double nC) {
		NC = nC;
	}
	public double getSC() {
		return SC;
	}
	public void setSC(double sC) {
		SC = sC;
	}
	public double getIC() {
		return IC;
	}
	public void setIC(double iC) {
		IC = iC;
	}

	public double getPara(String alg){
		double para = -1;
		if(alg.equals(ParameterSet.BC))
			para = BC;
		else if(alg.equals(ParameterSet.CC))
			para = CC; 
		else if(alg.equals(ParameterSet.DC))
			para = DC;
		else if(alg.equals(ParameterSet.EC))
			para = EC;
		else if(alg.equals(ParameterSet.NC))
			para = NC; 
		else if(alg.equals(ParameterSet.SC))
			para = SC; 
		else if(alg.equals(ParameterSet.LAC))
			para = LAC; 
		else if(alg.equals(ParameterSet.IC))
			para = IC; 
		return para;
		
		
	}
	
	
}
