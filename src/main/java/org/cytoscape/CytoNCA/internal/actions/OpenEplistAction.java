package org.cytoscape.CytoNCA.internal.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.EpListPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;

public class OpenEplistAction extends AbstractPAction{
	private final CyServiceRegistrar registrar;
	private EpListPanel eplistPanel =null;
	private final ProteinUtil pUtil;
	
	public OpenEplistAction(String title, CyApplicationManager applicationManager, CySwingApplication swingApplication, 
    		CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, ProteinUtil util){
		super(title, applicationManager, swingApplication, netViewManager, "network");
		this.registrar = registrar;	
		this.pUtil = util;
	}
	 public void actionPerformed(ActionEvent event) {
		 final CyNetwork network = applicationManager.getCurrentNetwork();
	        final CyNetworkView networkView = this.applicationManager.getCurrentNetworkView();
		 if(network != null){
			 if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty()){
					
					System.out.println("@@@@@@@@@");
					int eplistId = pUtil.getCurrentEplistId();
					pUtil.addNetworkEplist(network.getSUID().longValue());
					ArrayList<String> Alleprotein = pUtil.getAlleprotein();
				//	ArrayList<Protein> eprotein = pUtil.getCurrentParameters().getParamsCopy(network.getSUID()).getEprotein();
					ArrayList<Protein> eprotein = new ArrayList<Protein>();
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
								applicationManager,
								swingApplication,
								netViewManager,
								registrar,
								pUtil);
						
						eplistPanel = new EpListPanel(
								eprotein,
								pUtil, network,
								networkView, eplistId,
								discardEpListAction,
								applicationManager);
						registrar.registerService(eplistPanel, CytoPanelComponent.class, new Properties());
						registrar.registerService(eplistPanel,SetCurrentNetworkListener.class, new Properties());
						CytoPanel cytopanel	= pUtil.getEastCytoPanel();
							if (cytopanel.indexOfComponent(eplistPanel) >= 0)
							{
								int index = cytopanel.indexOfComponent(eplistPanel);
								cytopanel.setSelectedIndex(index);
								if (cytopanel.getState() == CytoPanelState.HIDE) 
									cytopanel.setState(CytoPanelState.DOCK);
							}
					}
				}
				
				
		 }
		 else{
			 JOptionPane.showMessageDialog(null,
	                    "Please load a network first!", "Error", JOptionPane.WARNING_MESSAGE);
	            return;
		 }
	 }

}
