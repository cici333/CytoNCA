package org.cytoscape.CytoNCA.internal.task;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;




//import org.cytoscape.CytoEPfinder.internal.algorithm.EC;
import org.cytoscape.CytoNCA.internal.AnalysisCompletedEvent;
import org.cytoscape.CytoNCA.internal.AnalysisCompletedListener;
import org.cytoscape.CytoNCA.internal.ParameterSet;
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
	private final ArrayList<String> algnames;
	private final ArrayList<String> Successfelalgnames;
	private String results;
	
	public AnalyzeTask(CyNetwork network, int analyze, int resultId,
			ArrayList<Algorithm> algSet, ProteinUtil pUtil,
			AnalysisCompletedListener listener, ArrayList<String> algnames) {
		this.network = network;
		this.analyze = analyze;
		this.resultId = resultId;
		this.algSet = algSet;
		this.pUtil = pUtil;
		this.listener = listener;
		this.Successfelalgnames = algnames;
		this.algnames = (ArrayList<String>) algnames.clone(); 
		results = "<html>";
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}

		boolean success = true, outofmemory = false;
	//	ArrayList<Protein> resultL;
		ArrayList<Protein> resultAll = new ArrayList<Protein>();
		pUtil.getPlist(network, resultAll);
		
		this.pUtil.resetLoading();
		try {
			for(Algorithm alg : algSet){
				if (alg instanceof CC)
				{

					if(algnames.contains(ParameterSet.CC)){
				    	algnames.remove(ParameterSet.CC);
				    	CC algoCC = (CC)alg;
					    algoCC.setTaskMonitor(taskMonitor, network.getSUID());
						
							taskMonitor.setProgress(0);
							taskMonitor
									.setStatusMessage("CC Ranking...");
							algoCC.run(network, resultAll, false);
							
						
							if (interrupted){								
								success = false;
								return;	
							}
							
							if(algoCC.isCancelled()){								
								Successfelalgnames.remove(ParameterSet.CC);
								results += "Can't calculate" + ParameterSet.CC + ". </br>";    
							}
				    	
					}
				 else if(algnames.contains(ParameterSet.CCW)){
				    	algnames.remove(ParameterSet.CCW);
				    	CC algoCC = (CC)alg;
					    algoCC.setTaskMonitor(taskMonitor, network.getSUID());
						
							taskMonitor.setProgress(0);
							taskMonitor
									.setStatusMessage("CC(weight) Ranking...");
							algoCC.run(network, resultAll, true);
							
						
							if (interrupted){
								success = false;
								return;	
							}
							
							if(algoCC.isCancelled()){
								Successfelalgnames.remove(ParameterSet.CCW);
								results += "Can't calculate" + ParameterSet.CCW + ": it can be calculated only if edge weights are greater then zero. </br>";  
							}
				    	
				 }
				} 
			else if (alg instanceof DC)
			{
			    if(algnames.contains(ParameterSet.DC)){
			    	algnames.remove(ParameterSet.DC);
			    	DC algoDC = (DC)alg;
				    algoDC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("DC Ranking...");
						algoDC.run(network, resultAll, false);	
						
					
						
						if (interrupted){						
							success = false;
							return;	
						}
						if(algoDC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.DC);
							results += "Can't calculate" + ParameterSet.DC + ". </br>";  
						}
			    }
			    else if(algnames.contains(ParameterSet.DCW)){
			    	algnames.remove(ParameterSet.DCW);
			    	DC algoDC = (DC)alg;
				    algoDC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("DC Ranking...");
						algoDC.run(network, resultAll, true);
						
						if (interrupted){
							success = false;
							return;	
						}
						if(algoDC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.DCW);
							results += "Can't calculate" + ParameterSet.DCW + ". </br>";  
						}
						
			    }
			    
				
			}
			else if (alg instanceof EC)
			{
				if(algnames.contains(ParameterSet.EC)){
			    	algnames.remove(ParameterSet.EC);
			    	EC algoEC = (EC)alg;
				    algoEC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 1...");
						taskMonitor.setTitle("EC [6 Steps] ");
						algoEC.run(network, resultAll, false);
					
					
						if (interrupted){							
							success = false;
							return;	
						}
						
						if(algoEC.isCancelled()){							
							Successfelalgnames.remove(ParameterSet.EC);
							results += "Can't calculate" + ParameterSet.EC + ": fail to compute eigenvector. </br>";  
						}
			    	
			 }
			 else if(algnames.contains(ParameterSet.ECW)){
			    	algnames.remove(ParameterSet.ECW);
			    	EC algoEC = (EC)alg;
				    algoEC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 1...");
						taskMonitor.setTitle("EC(weight) [6 Steps] ");
						algoEC.run(network, resultAll, true);
						
					
						if (interrupted){
							success = false;
							return;	
						}
						
						if(algoEC.isCancelled()){							
							Successfelalgnames.remove(ParameterSet.ECW);
							results += "Can't calculate" + ParameterSet.ECW + ": fail to compute eigenvector. </br>";  
						}
						
			    	
			 }
				
				
				
				} 
			else if (alg instanceof LAC)
			{
				if(algnames.contains(ParameterSet.LAC)){
			    	algnames.remove(ParameterSet.LAC);
			    	LAC algoLAC = (LAC)alg;
				    algoLAC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("LAC Ranking...");
						algoLAC.run(network, resultAll, false);	
						
						if (interrupted){							
							success = false;
							return;	
						}
						if(algoLAC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.LAC);
							results += "Can't calculate" + ParameterSet.LAC + ". </br>";  
						}
			    	
			 }
			 else if(algnames.contains(ParameterSet.LACW)){
			    	algnames.remove(ParameterSet.LACW);
			    	LAC algoLAC = (LAC)alg;
				    algoLAC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("LAC(weight) Ranking...");
						algoLAC.run(network, resultAll, true);	
											
						if (interrupted){
							success = false;
							return;	
						}
						
						if(algoLAC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.LACW);
							results += "Can't calculate" + ParameterSet.LACW + ". </br>";  
						}
			    	
			 }
				
				
				
				} 
			else if (alg instanceof NC)
			{
				if(algnames.contains(ParameterSet.NC)){
			    	algnames.remove(ParameterSet.NC);
			    	NC algoNC = (NC)alg;
				    algoNC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("NC Ranking...");
						algoNC.run(network, resultAll, false);
					
						
						if (interrupted){							
							success = false;
							return;	
						}
						if(algoNC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.NC);
							results += "Can't calculate" + ParameterSet.NC + ". </br>";  
						}
			    	
				}
			 else if(algnames.contains(ParameterSet.NCW)){
			    	algnames.remove(ParameterSet.NCW);
			    	NC algoNC = (NC)alg;
				    algoNC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("NC(weight) Ranking...");
						algoNC.run(network, resultAll, true);										
						if (interrupted){
							success = false;
							return;	
						}
						if(algoNC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.NCW);
							results += "Can't calculate" + ParameterSet.NCW + ". </br>";  
						}
			    	
			 	}
				
				
				
			} 
				/**
				 * @author TangYu
				 * @date: 2014年8月16日 下午5:03
				 * 
				 **/
			else if (alg instanceof SC)
			{
				if(algnames.contains(ParameterSet.SC)){
			    	algnames.remove(ParameterSet.SC);
			    	SC algoSC = (SC)alg;
				    algoSC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
						.setStatusMessage("Step 1...");
					taskMonitor.setTitle("SC [6 Steps] ");
					algoSC.run(network, resultAll, false);	
			
					if (interrupted){					
						success = false;
						return;	
					}
					
					if(algoSC.isCancelled()){					
						Successfelalgnames.remove(ParameterSet.SC);
						results += "Can't calculate" + ParameterSet.SC + ": fail to compute eigenvector. </br>";  
					}
			    	
				}
			 else if(algnames.contains(ParameterSet.SCW)){
			    	algnames.remove(ParameterSet.SCW);
			    	SC algoSC = (SC)alg;
				    algoSC.setTaskMonitor(taskMonitor, network.getSUID());			
					taskMonitor.setProgress(0);
					taskMonitor
						.setStatusMessage("Step 1...");
					taskMonitor.setTitle("EC [6 Steps] ");
					algoSC.run(network, resultAll, true);	
			
					if (interrupted){
						success = false;
						return;	
					}
					if(algoSC.isCancelled()){					
						Successfelalgnames.remove(ParameterSet.SCW);
						results += "Can't calculate" + ParameterSet.SCW + ": fail to compute eigenvector. </br>";  
					}
					
			 	}
				
				
				
			} 
			else if (alg instanceof BC)
			{
				 if(algnames.contains(ParameterSet.BC)){
				    	algnames.remove(ParameterSet.BC);
				    	BC algoBC = (BC)alg;
					    algoBC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor.setStatusMessage("BC Ranking...");
						algoBC.run(network, resultAll, false);	

						if (interrupted){
							
							success = false;
							return;	
						}
						if(algoBC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.BC);
							results += "Can't calculate" + ParameterSet.BC + ". </br>";  
						}
				    	
				 }
				 else if(algnames.contains(ParameterSet.BCW)){
				    	algnames.remove(ParameterSet.BCW);
				    	BC algoBC = (BC)alg;
					    algoBC.setTaskMonitor(taskMonitor, network.getSUID());			
						taskMonitor.setProgress(0);
						taskMonitor.setStatusMessage("BC(with weight) Ranking...");
						algoBC.run(network, resultAll, true);	

						if (interrupted){
							success = false;
							return;	
						}
						if(algoBC.isCancelled()){
							Successfelalgnames.remove(ParameterSet.BCW);
							results += "Can't calculate" + ParameterSet.BCW + ": it can be calculated only if edge weights are greater then zero. </br>";  
						}
				    	
				 }
				 
				
			} 
				
			else if (alg instanceof IC)
			{
			    
				if(algnames.contains(ParameterSet.IC)){
			    	algnames.remove(ParameterSet.IC);
			    	IC algoIC = (IC)alg;
				    algoIC.setTaskMonitor(taskMonitor, network.getSUID());			
				    taskMonitor.setProgress(0);
					taskMonitor
						.setStatusMessage("Step 1...");
					taskMonitor.setTitle("IC [6 Steps] ");
					algoIC.run(network, resultAll, false);	
					
					if (interrupted){				
						success = false;
						return;	
					}
					if(algoIC.isCancelled()){
						Successfelalgnames.remove(ParameterSet.IC);
						results += "Can't calculate" + ParameterSet.IC + ". </br>";  
					}
			    	
				}
			 else if(algnames.contains(ParameterSet.ICW)){
			    	algnames.remove(ParameterSet.ICW);
			    	IC algoIC = (IC)alg;
				    algoIC.setTaskMonitor(taskMonitor, network.getSUID());			
				    taskMonitor.setProgress(0);
					taskMonitor
						.setStatusMessage("Step 1...");
					taskMonitor.setTitle("ICW [6 Steps] ");
					algoIC.run(network, resultAll, true);	
					
					if (interrupted){
						success = false;
						return;	
					}
					if(algoIC.isCancelled()){
						Successfelalgnames.remove(ParameterSet.ICW);
						results += "Can't calculate" + ParameterSet.ICW + ". </br>";  
					}			    	
			 	}				
			} 			
		}
			

		} catch (Exception e) {
			throw new Exception("Error while executing the analysis", e);
		} catch(OutOfMemoryError oe){
			success = false;
			outofmemory = true;
			System.out.println("out of mem...");
		}finally {
			results += "</html>";
			if (this.listener != null)
				this.listener.handleEvent(new AnalysisCompletedEvent(success, 
						resultAll, results, outofmemory, Successfelalgnames));
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
		return "Analysis";
	}
	
	
}