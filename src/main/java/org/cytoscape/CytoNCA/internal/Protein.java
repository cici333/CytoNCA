package org.cytoscape.CytoNCA.internal;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;

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
	private double CCW = 0;
	private double BCW = 0;
	private double DCW = 0;
	private double ECW = 0;
	private double LACW = 0;
	private double NCW = 0;
	private double SCW = 0;
	private double ICW = 0;
	private ArrayList<String> selectGroups = null;
	private Paint originColor = null;
	private HashMap<String, Double> BioparasMap;
	
	public Protein(CyNode n, CyNetwork network){
		this.network = network;
		this.n = n;
		name = network.getRow(n).get("name", String.class);
		BioparasMap = new HashMap<String, Double>();
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

	public double getCCW() {
		return CCW;
	}
	public void setCCW(double cCW) {
		CCW = cCW;
	}
	public double getBCW() {
		return BCW;
	}
	public void setBCW(double bCW) {
		BCW = bCW;
	}
	public double getDCW() {
		return DCW;
	}
	public void setDCW(double dCW) {
		DCW = dCW;
	}
	public double getECW() {
		return ECW;
	}
	public void setECW(double eCW) {
		ECW = eCW;
	}
	public double getLACW() {
		return LACW;
	}
	public void setLACW(double lACW) {
		LACW = lACW;
	}
	public double getNCW() {
		return NCW;
	}
	public void setNCW(double nCW) {
		NCW = nCW;
	}
	public double getSCW() {
		return SCW;
	}
	public void setSCW(double sCW) {
		SCW = sCW;
	}
	public double getICW() {
		return ICW;
	}
	public void setICW(double iCW) {
		ICW = iCW;
	}
	
	public void setBioPara(String pname, double biopara){
		BioparasMap.put(pname, biopara);
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
		else if(alg.equals(ParameterSet.BCW))
			para = BCW; 
		else if(alg.equals(ParameterSet.CCW))
			para = CCW; 
		else if(alg.equals(ParameterSet.DCW))
			para = DCW;
		else if(alg.equals(ParameterSet.ECW))
			para = ECW;
		else if(alg.equals(ParameterSet.NCW))
			para = NCW; 
		else if(alg.equals(ParameterSet.SCW))
			para = SCW; 
		else if(alg.equals(ParameterSet.LACW))
			para = LACW; 
		else if(alg.equals(ParameterSet.ICW))
			para = ICW; 
		else{
			para = BioparasMap.get(alg);
		}
		return para;

	}
	
	public double getBioPara(String alg){
		return BioparasMap.get(alg);
	}
	
	public ArrayList<String> getSelectGroups() {
		return selectGroups;
	}
	public void setSelectGroups(ArrayList<String> selectGroups) {
		this.selectGroups = selectGroups;
	}
	public Paint getOriginColor() {
		return originColor;
	}
	public void setOriginColor(Paint originColor) {
		this.originColor = originColor;
	}
	
	
	
}
