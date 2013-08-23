package org.cytoscape.CytoNCA.internal.task;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;




//import org.cytoscape.CytoEPfinder.internal.algorithm.EC;
import org.cytoscape.CytoNCA.internal.AnalysisCompletedEvent;
import org.cytoscape.CytoNCA.internal.AnalysisCompletedListener;
import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.algorithm.Algorithm;
import org.cytoscape.CytoNCA.internal.algorithm.BC;
import org.cytoscape.CytoNCA.internal.algorithm.CC;
import org.cytoscape.CytoNCA.internal.algorithm.DC;
import org.cytoscape.CytoNCA.internal.algorithm.EC;
import org.cytoscape.CytoNCA.internal.algorithm.IC;
import org.cytoscape.CytoNCA.internal.algorithm.LAC;
import org.cytoscape.CytoNCA.internal.algorithm.NC;
import org.cytoscape.CytoNCA.internal.algorithm.SC;
//import org.cytoscape.CytoEPfinder.internal.algorithm.SC;


import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;



public class AnalyzeTask implements Task {
	private final ArrayList<Algorithm> algSet;
	private final ProteinUtil pUtil;
	private final int analyze;
	private final int resultId;
	private final AnalysisCompletedListener listener;
	private boolean interrupted;
	private CyNetwork network;
	private static final Logger logger = LoggerFactory
			.getLogger(AnalyzeTask.class);
	private HashMap<String, ArrayList<Protein>> proteinSet;

	public AnalyzeTask(CyNetwork network, int analyze, int resultId,
			ArrayList<Algorithm> algSet, ProteinUtil pUtil,
			AnalysisCompletedListener listener) {
		this.network = network;
		this.analyze = analyze;
		this.resultId = resultId;
		this.algSet = algSet;
		this.pUtil = pUtil;
		this.listener = listener;
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}

		boolean success = false;
	//	ArrayList<Protein> resultL;
		ArrayList<Protein> resultAll = new ArrayList<Protein>();
		pUtil.getPlist(network, resultAll);
		
		this.pUtil.resetLoading();
		try {
			for(Algorithm alg : algSet){
				if (alg instanceof CC)//
				{

				    CC algoCC = (CC)alg;
				    algoCC.setTaskMonitor(taskMonitor, network.getSUID());
					
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("CC Ranking...");
						algoCC.run(network, resultAll);
						
					
						if (interrupted)
							return;					


						success = true;
						if (interrupted)
							return;
					} 
			else if (alg instanceof DC)
			{
			    DC algoDC = (DC)alg;
			    algoDC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("DC Ranking...");
					algoDC.run(network, resultAll);	
					
				
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
			}
			else if (alg instanceof EC)
			{
			    EC algoEC = (EC)alg;
			    algoEC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("EC Ranking...");
					algoEC.run(network, resultAll);
					
				
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
			else if (alg instanceof LAC)
			{
			    LAC algoLAC = (LAC)alg;
			    algoLAC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("LAC Ranking...");
					algoLAC.run(network, resultAll);	
				
				
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
			else if (alg instanceof NC)
			{
			    NC algoNC = (NC)alg;
			    algoNC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("NC Ranking...");
					algoNC.run(network, resultAll);
				
					
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
			else if (alg instanceof SC)
			{
			    SC algoSC = (SC)alg;
			    algoSC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("SC Ranking...");
					algoSC.run(network, resultAll);	
				
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
			else if (alg instanceof BC)
			{
			    BC algoBC = (BC)alg;
			    algoBC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("BC Ranking...");
					algoBC.run(network, resultAll);	
				
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
				
			else if (alg instanceof IC)
			{
			    IC algoIC = (IC)alg;
			    algoIC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("IC Ranking...");
					algoIC.run(network, resultAll);	
				
					
					if (interrupted)
						return;								
					success = true;
					if (interrupted)
						return;
				} 
				
			
	
				
				
				
				
			}
			
			

		} catch (Exception e) {
			throw new Exception("Error while executing the analysis", e);
		} finally {

			if (this.listener != null)
				this.listener.handleEvent(new AnalysisCompletedEvent(success,
						resultAll));
		}
	}
	
	
	

	public void cancel() {
		this.interrupted = true;
		for(Algorithm alg : algSet){
			alg.setCancelled(true);
		}
		this.pUtil.removeNetworkResult(this.resultId);
		this.pUtil.removeNetworkAlgorithm(this.network.getSUID()
				.longValue());
	}

	public String getTitle() {
		return "Network Cluster Detection";
	}
}