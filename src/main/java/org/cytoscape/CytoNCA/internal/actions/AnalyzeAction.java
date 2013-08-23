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
        	this.pUtil.getCurrentParameters().setParams(currentParamsCopy, resultId, network.getSUID());
        	 ArrayList<Algorithm> alg = new ArrayList<Algorithm>();
        	 final ArrayList<String> alg2 = new ArrayList<String>();
        	
        	for(Entry<String, Boolean> e :currentParamsCopy.getAlgorithmSet().entrySet()){
        		if(e.getValue().equals(true)){
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
        						
        						DiscardResultAction discardResultAction = new DiscardResultAction(
        								"Discard Result", 
        								resultId, 
        								AnalyzeAction.this.applicationManager, 
        								AnalyzeAction.this.swingApplication, 
        								AnalyzeAction.this.netViewManager, 
        								AnalyzeAction.this.registrar, 
        								AnalyzeAction.this.pUtil);

        						resultsPanel = new ResultPanel(e.getProteins(), alg2, AnalyzeAction.this.pUtil, network, networkView, 
        								resultId, discardResultAction, AnalyzeAction.this.registrar);
        						AnalyzeAction.this.registrar.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
        						
        					/*	if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty()){
        							
        							System.out.println("@@@@@@@@@");
        							int eplistId = pUtil.getCurrentEplistId();
        							AnalyzeAction.this.pUtil.addNetworkEplist(network.getSUID().longValue());
        							ArrayList<String> Alleprotein = pUtil.getAlleprotein();
        							ArrayList<Protein> eprotein = pUtil.getCurrentParameters().getParamsCopy(network.getSUID()).getEprotein();
        							Iterator i = network.getNodeList().iterator();
        							while (i.hasNext()){
        								CyNode n = (CyNode)i.next();
        								String name = network.getRow(n).get("name", String.class);
        								if(Alleprotein.contains(name)){
        									Protein p = new Protein(n, network);
        									eprotein.add(p);
        								}
        							}
        							if(!eprotein.isEmpty()){        							
        								DiscardEpListAction discardEpListAction = new DiscardEpListAction(
        										"Discard Panel",
        										eplistId,
        										AnalyzeAction.this.applicationManager,
        										AnalyzeAction.this.swingApplication,
        										AnalyzeAction.this.netViewManager,
        										AnalyzeAction.this.registrar,
        										AnalyzeAction.this.pUtil);

        								eplistPanel = new EpListPanel(
        										eprotein,
        										AnalyzeAction.this.pUtil, network,
        										networkView, eplistId,
        										discardEpListAction);
        								AnalyzeAction.this.registrar.registerService(eplistPanel,
        											CytoPanelComponent.class,
													new Properties());
        							}
        						}
        						
        					*/	
        						
        					//	ArrayList nodes = new ArrayList();
        					//    for(Protein p: e.getProteins()){
        					//    	System.out.println(p.getName());
        					//    	nodes.add(p.n);
        					//    	}
        					//    pg = AnalyzeAction.this.pUtil.createGraph(network, nodes);
        						
        						
        						
        						
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
                    this.pUtil, listener);
            
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
        		interruptedMessage="An algorithm need to be selected for clustering";
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
		  			EvaluationPanel ep = rp.geteEvaluationPanel();
					if (cytopanel.indexOfComponent(ep) >= 0)
					{
						int index = cytopanel.indexOfComponent(ep);
						cytopanel.setSelectedIndex(index);
					}
		  			
		  		}
		  	}
				

			
		 }
	  
	  
	  

}
