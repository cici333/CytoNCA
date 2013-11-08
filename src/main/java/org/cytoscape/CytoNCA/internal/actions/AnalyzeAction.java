package org.cytoscape.CytoNCA.internal.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cytoscape.CytoNCA.internal.AnalysisCompletedEvent;
import org.cytoscape.CytoNCA.internal.AnalysisCompletedListener;
import org.cytoscape.CytoNCA.internal.CurrentParameters;
import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinGraph;
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
import org.cytoscape.CytoNCA.internal.panels.EvaluationPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.CytoNCA.internal.task.AnalyzeTaskFactory;



import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.event.CyPayloadEvent;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;

/**
 * Classe to handle the action of clicking Button Analyze.
 */
public class AnalyzeAction extends AbstractPAction
		implements SetCurrentNetworkListener, AddedNodesListener, AddedEdgesListener, RemovedNodesListener, RemovedEdgesListener, CytoPanelComponentSelectedListener {
    final static int FIRST_TIME = 0;
   
    final static int FIND=1;
    final static int INTERRUPTED = 2;
    
    //final static int EXISTS=3;
    
    private HashMap networkManager;//Keeps track of netowrks (id is key) and their algorithms 
    private boolean resultFound ;
    private ResultPanel resultPanel;
    int analyze = FIRST_TIME;
    int resultCounter = 0;
    ParameterSet  curParams;
//    ClusterVisualStyle vistyle;
    private String interruptedMessage="";
    private int resultIndex;
    
    private final ProteinUtil pUtil;
    
    private static final long serialVersionUID = 0x1385f3897d8b2b0L;
	public static final int INTERRUPTION = 3;
	private final CyServiceRegistrar registrar;
	private final TaskManager taskManager;
	private Map<Long, Boolean>  dirtyNetworks;
	private ResultPanel resultsPanel;
	
	private ProteinGraph pg;
	
    
    public AnalyzeAction(String title, CyApplicationManager applicationManager, CySwingApplication swingApplication, 
    		CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, 
    		TaskManager taskManager,ParameterSet  curParams,
    		ProteinUtil util)
	{
		super(title, applicationManager, swingApplication, netViewManager, "network");
		analyze = 0;
		this.registrar = registrar;
		this.taskManager = taskManager;
		this.curParams = curParams;
		this.pUtil=util;
		this.dirtyNetworks = new HashMap();
	}
    

    public void actionPerformed(ActionEvent event) {
        resultFound = false;
        CurrentParameters resultParaSet=null;
        //get the network object, this contains the graph
        final CyNetwork network = applicationManager.getCurrentNetwork();
        final CyNetworkView networkView = this.applicationManager.getCurrentNetworkView();
        
        ParameterSet currentParamsCopy = pUtil.getMainPanel().getCurrentParamsCopy();
        if (network == null) {
            System.err.println("Can't get a network.");
            return;
        }
        if (network.getNodeCount() < 1) {
            JOptionPane.showMessageDialog(null/*Cytoscape.getDesktop()*/,
                    "Network has not been loaded!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        
        //get selected node
        List<CyNode> nodes = network.getNodeList();
        Set<Long> selectedNodes = new HashSet<Long>();
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();)
		{
			CyNode n = (CyNode)iterator.next();
			if (((Boolean)network.getRow(n).get("selected", java.lang.Boolean.class)).booleanValue())
				selectedNodes.add(n.getSUID());
		}
		
		
//        Set selectedNodes = network.getSelectedNodes();
        Long[] selectedNodesRGI = new Long[selectedNodes.size()];
        int c = 0;
        for(Iterator i = selectedNodes.iterator(); i.hasNext();)
        {
        	Long nodeID = (Long) i.next();
        	selectedNodesRGI[c++]=nodeID;
        }
        

        currentParamsCopy.setSelectedNodes(selectedNodesRGI);

       
        boolean newNet=isDirty(network);//judge new network
        

 /**********************************************************************************/     
        analyze=checkParams(currentParamsCopy, newNet); 
 /**********************************************************************************/ 

        
        

        
        

        
        if (analyze == INTERRUPTED )
        	JOptionPane.showMessageDialog(AnalyzeAction.this.swingApplication.getJFrame(),
            		interruptedMessage, "Interrupted", JOptionPane.WARNING_MESSAGE);
        else{  
        	
        	ArrayList de = this.pUtil.detectparalleledges(network);
        	System.out.println(de.size()+"^^^");
        	if(de.size()!=0){
				if(JOptionPane.showConfirmDialog(this.swingApplication.getJFrame(),
	            		"There are parallel edges or loops in the network! \n Do you remove these edge and contine?", "WARNING", JOptionPane.YES_NO_OPTION) == 1)
				{	
					this.pUtil.setSelected(de, network);
					return ;
				}
				else{
					this.pUtil.deleteparalleledges(network, de);
				}
					
				 
			 }
        	
        	final int resultId = this.pUtil.getCurrentResultId();
        	
        	 ArrayList<Algorithm> alg = new ArrayList<Algorithm>();
        	 final ArrayList<String> alg2 = new ArrayList<String>();
        	// boolean isweight = network.getRow(network.getEdgeList().get(0)).get("weight", Double.class)!=null;
        	 int flag = 0;
        	 
        	
        		 if(network.getRow(network.getEdgeList().get(0)).get("weight", Number.class)!=null && 
        				 (network.getDefaultEdgeTable().getColumn("weight").getType().equals(Double.class) || 
        				 network.getDefaultEdgeTable().getColumn("weight").getType().equals(Integer.class))){
        			 
        			 if(network.getDefaultEdgeTable().getColumn("weight").getType().equals(Integer.class)){   				 
        				 Integer i;
							network.getDefaultEdgeTable().createColumn("&^&", Double.class, false);
							for(CyEdge edge : network.getEdgeList()){
								
								if(network.getRow(edge).get("weight", Integer.class) != null){
									i = network.getRow(edge).get("weight", Integer.class);
									network.getRow(edge).set("&^&", i.doubleValue());
								}
								else 
									network.getRow(edge).set("&^&", 0.0);								
							}	
							network.getDefaultEdgeTable().deleteColumn("weight");
							network.getDefaultEdgeTable().getColumn("&^&").setName("weight");							
        			 }else{
        				 for(CyEdge edge : network.getEdgeList()){
        					 
        					 if(network.getRow(edge).get("weight", Double.class) == null){
        						 network.getRow(edge).set("weight", 0.0);
        					 }
        				 }
        			 }
        		 
        			for(Entry<String, Boolean> e :currentParamsCopy.getAlgorithmSet().entrySet()){		
                		if(e.getValue().equals(true)){        
                			if(e.getKey().equals(ParameterSet.BCW )){               				
                				alg.add(new BC(null, this.pUtil));
                				alg2.add(ParameterSet.BCW);							
                				
                					
                			}
                			if(e.getKey().equals(ParameterSet.CCW )){
                				alg.add(new CC(null, this.pUtil));
                				alg2.add(ParameterSet.CCW);						
                				
                			}
                			if(e.getKey().equals(ParameterSet.DCW )){
                				
                				alg.add(new DC(null, this.pUtil));
                				alg2.add(ParameterSet.DCW);				
                				
                			}
                			if(e.getKey().equals(ParameterSet.ECW )){
                				
                				alg.add(new EC(null, this.pUtil));
                				alg2.add(ParameterSet.ECW);						
                				
                			}
                			if(e.getKey().equals(ParameterSet.LACW )){
                				
                				alg.add(new LAC(null, this.pUtil));
                				alg2.add(ParameterSet.LACW);						
                				
                			}
                			if(e.getKey().equals(ParameterSet.SCW )){
                				
                				alg.add(new SC(null, this.pUtil));
                				alg2.add(ParameterSet.SCW);						
                				
                			}
                			if(e.getKey().equals(ParameterSet.NCW )){
                				alg.add(new NC(null, this.pUtil));
                				alg2.add(ParameterSet.NCW);				
                				
                			}
                			if(e.getKey().equals(ParameterSet.ICW )){
                				alg.add(new IC(null, this.pUtil));
                				alg2.add(ParameterSet.ICW);					
                				
                			}
                			
                			if(e.getKey().equals(ParameterSet.BC )){
                        		alg.add(new BC(null, this.pUtil));
                				alg2.add(ParameterSet.BC);		
                			}
                			if(e.getKey().equals(ParameterSet.CC )){
                				alg.add(new CC(null, this.pUtil));
                				alg2.add(ParameterSet.CC);
                			}
                        		
                        	if(e.getKey().equals(ParameterSet.DC )){
                        		alg.add(new DC(null, this.pUtil));
                        		alg2.add(ParameterSet.DC);
                        	}
                        		
                        	if(e.getKey().equals(ParameterSet.EC )){
                        		alg.add(new EC(null, this.pUtil));
                        		alg2.add(ParameterSet.EC);
                        	}
                        		
                        	if(e.getKey().equals(ParameterSet.LAC )){
                        		alg.add(new LAC(null, this.pUtil));
                        		alg2.add(ParameterSet.LAC);
                        	}
                        		
                        	if(e.getKey().equals(ParameterSet.NC )){
                        		alg.add(new NC(null, this.pUtil));
                        		alg2.add(ParameterSet.NC);
                        	}
                        		
                        	if(e.getKey().equals(ParameterSet.SC )){
                        		alg.add(new SC(null, this.pUtil));
                        		alg2.add(ParameterSet.SC);
                        	}
                        		
                        	if(e.getKey().equals(ParameterSet.IC )){
                        		alg.add(new IC(null, this.pUtil));
                        	alg2.add(ParameterSet.IC);
                        	}
                		}
                		
                	}
        	 }else{
        		 for(Entry<String, Boolean> e :currentParamsCopy.getAlgorithmSet().entrySet()){		
             		if(e.getValue().equals(true)){        
             			if(e.getKey().equals(ParameterSet.BCW ) ||
             					e.getKey().equals(ParameterSet.CCW ) ||
             					e.getKey().equals(ParameterSet.DCW ) ||
             					e.getKey().equals(ParameterSet.ECW ) ||
             					e.getKey().equals(ParameterSet.LACW ) ||
             					e.getKey().equals(ParameterSet.SCW ) ||
             					e.getKey().equals(ParameterSet.NCW ) ||
             					e.getKey().equals(ParameterSet.ICW )){          				
             					flag = 1;      				           					
             			}
             			
             			
             			if(e.getKey().equals(ParameterSet.BC )){
                     		alg.add(new BC(null, this.pUtil));
             				alg2.add(ParameterSet.BC);		
             			}
             			if(e.getKey().equals(ParameterSet.CC )){
             				alg.add(new CC(null, this.pUtil));
             				alg2.add(ParameterSet.CC);
             			}
                     		
                     	if(e.getKey().equals(ParameterSet.DC )){
                     		alg.add(new DC(null, this.pUtil));
                     		alg2.add(ParameterSet.DC);
                     	}
                     		
                     	if(e.getKey().equals(ParameterSet.EC )){
                     		alg.add(new EC(null, this.pUtil));
                     		alg2.add(ParameterSet.EC);
                     	}
                     		
                     	if(e.getKey().equals(ParameterSet.LAC )){
                     		alg.add(new LAC(null, this.pUtil));
                     		alg2.add(ParameterSet.LAC);
                     	}
                     		
                     	if(e.getKey().equals(ParameterSet.NC )){
                     		alg.add(new NC(null, this.pUtil));
                     		alg2.add(ParameterSet.NC);
                     	}
                     		
                     	if(e.getKey().equals(ParameterSet.SC )){
                     		alg.add(new SC(null, this.pUtil));
                     		alg2.add(ParameterSet.SC);
                     	}
                     		
                     	if(e.getKey().equals(ParameterSet.IC )){
                     		alg.add(new IC(null, this.pUtil));
                     		alg2.add(ParameterSet.IC);
                     	}
             		}
             		
             	} 
        		 if(flag == 1){
             		if(JOptionPane.showConfirmDialog(this.swingApplication.getJFrame(),
     	            		"The weight of edges haven't been uploaded, or the data type of the attibute named weight is not double or integer,  centralities with weight can't be analyzed! \n Do you want to contine?", "WARNING", JOptionPane.YES_NO_OPTION) == 1)
     				{	
     					return ;
     				}
     				
             	}
        	 }
        
        	
        	this.pUtil.getCurrentParameters().setParams(currentParamsCopy, resultId, network.getSUID());
        	
        	this.pUtil.addNetworkAlgorithm(network.getSUID().longValue());
        	AnalysisCompletedListener listener = new AnalysisCompletedListener(){
        		public void handleEvent(AnalysisCompletedEvent e)
        			{
        				resultsPanel = null;
        				boolean resultFound = false;
        				AnalyzeAction.this.setDirty(network, false);
        				
        				
        				
        				if (e.isSuccessful()) {
        					
        						
        					
        					if ((e.getProteins() != null) && (!e.getProteins().isEmpty())) {  
        						resultFound = true;
        						AnalyzeAction.this.pUtil.addNetworkResult(network.getSUID().longValue());
        						
        						if(!pUtil.getBioinfoColumnNames().isEmpty()){
        	
        								for(String s : pUtil.getBioinfoColumnNames()){
        									
        									for(Protein p : e.getProteins()){
                								Double para; 				
                								if(network.getRow(p.getN()).get(s, Number.class) != null){
                									Number num = network.getRow(p.getN()).get(s, Number.class);
             		
                    								if(network.getDefaultNodeTable().getColumn(s).getType().equals(Integer.class)){
                    									
                    										para = num.doubleValue();
                    								}else
                    									para = (Double) num;
                    								
                								}else{
                									para = 0.0;
                								}
                								p.setBioPara(s, para);
        									}
        									alg2.add(s);
        								
        								
        								}
        								pUtil.getBioinfoColumnNames().clear();
        						}
        						
        						
        						
        						
        						
        						DiscardResultAction discardResultAction = new DiscardResultAction(
        								"Discard Result", 
        								resultId, 
        								AnalyzeAction.this.applicationManager, 
        								AnalyzeAction.this.swingApplication, 
        								AnalyzeAction.this.netViewManager, 
        								AnalyzeAction.this.registrar, 
        								AnalyzeAction.this.pUtil);

        						resultsPanel = new ResultPanel(e.getProteins(), alg2, AnalyzeAction.this.pUtil, network, networkView, 
        								resultId, discardResultAction);
        						AnalyzeAction.this.registrar.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
        						
        						JOptionPane.showMessageDialog(null,
        	    	            		"<html>" + e.getResults() + "</html>", "Interrupted", JOptionPane.WARNING_MESSAGE);
        						
        						
        						
        					} 
        					else {
        						JOptionPane.showMessageDialog(AnalyzeAction.this.swingApplication.getJFrame(), 
        								"Ranking ERROR!", 
        								"No Results", 
        								2);
        					}
        				}

        				CytoPanel cytoPanel = AnalyzeAction.this.swingApplication.getCytoPanel(CytoPanelName.EAST);

        				if ((resultFound) || ((AnalyzeAction.this.analyze == INTERRUPTED) && (cytoPanel.indexOfComponent(resultsPanel) >= 0)))
        				{
        					int index = cytoPanel.indexOfComponent(resultsPanel);
        					cytoPanel.setSelectedIndex(index);

        				if (cytoPanel.getState() == CytoPanelState.HIDE) 
        					cytoPanel.setState(CytoPanelState.DOCK);
        				}
        			}
        		};
                  
        		AnalyzeTaskFactory analyzeTaskFactory = new AnalyzeTaskFactory(network, this.analyze, resultId, alg, 
                    this.pUtil, listener, alg2);
            
        		this.taskManager.execute(analyzeTaskFactory.createTaskIterator());
        	
        	//	resultsPanel.selectProteins(pg.getSubNetwork());
            
    //    }
       }

    }
    

	/**
	 * check the values of the input parameters so as to take corresponding action
	 * @param params The set of input parameters
	 * @return the code of action to be taken
	 */
	private int checkParams(ParameterSet curParams, boolean newNet){
		int analyze=-1;
		if (curParams.getScope().equals(ParameterSet.SELECTION) && curParams.getSelectedNodes().length < 1) {
            analyze = INTERRUPTED;
            interruptedMessage= "At least one nodes should be selected";
        }else{
        	Collection<Boolean> which=curParams.getAlgorithmSet().values(); 
        	if(!which.contains(true)){	//if no algorithm is selected
        		analyze=INTERRUPTED;
        		interruptedMessage="An algorithm need to be selected for analyzing";
        	}else{
        		
    			if(newNet ){
        	    	
    	        		analyze = FIND;
    	       // 	pUtil.setEvaluation(true);
        	    	
        	    	
    			}
        	}
        }
		return analyze;
	}
	
	private void setDirty(CyNetwork net, boolean dirty) {
		    if (this.pUtil.containsNetworkAlgorithm(net.getSUID().longValue()))
		      if (dirty)
		        this.dirtyNetworks.put(net.getSUID(), Boolean.valueOf(dirty));
		      else
		        this.dirtyNetworks.remove(net.getSUID());
	}
	
	//check if two sets of Parameter are equal or not 
	

	  public void handleEvent(SetCurrentNetworkEvent e)
	  {
	    updateEnableState();
	  }

	  public void handleEvent(RemovedEdgesEvent e)
	  {
	    setDirty((CyNetwork)e.getSource(), true);
	  }

	  public void handleEvent(RemovedNodesEvent e)
	  {
	    setDirty((CyNetwork)e.getSource(), true);
	  }

	  public void handleEvent(AddedEdgesEvent e)
	  {
	    setDirty((CyNetwork)e.getSource(), true);
	  }

	  public void handleEvent(AddedNodesEvent e)
	  {
	    setDirty((CyNetwork)e.getSource(), true);
	  }
	  
	  
	  private boolean isDirty(CyNetwork net)
	  {
	    return Boolean.TRUE.equals(this.dirtyNetworks.get(net.getSUID()));
	  }

	
	  public void handleEvent(CytoPanelComponentSelectedEvent e)
		 {
		  	CytoPanel c = e.getCytoPanel();
		  	if(c == this.pUtil.getEastCytoPanel()){
		  		int num = e.getSelectedIndex();
		  		if(c.getComponentAt(num) instanceof ResultPanel){
		  			ResultPanel rp = (ResultPanel) c.getComponentAt(num);
		  			CytoPanel cytopanel	= pUtil.getSouthCytoPanel();
		  			EvaluationPanel ep = rp.getEvaluationPanel();
					if (cytopanel.indexOfComponent(ep) >= 0)
					{
						int index = cytopanel.indexOfComponent(ep);
						cytopanel.setSelectedIndex(index);
					}
		  			
		  		}
		  	}
				

			
		 }
	  
	  
	  

}
