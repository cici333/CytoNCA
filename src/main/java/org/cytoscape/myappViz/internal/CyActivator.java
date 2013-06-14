package org.cytoscape.myappViz.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.event.CyEventHelper;

import org.osgi.framework.BundleContext;

import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.util.swing.OpenBrowser;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
/*
		CySwingApplication cytoscapeDesktopService = getService(bc,CySwingApplication.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		

*/
		
		CyApplicationManager appMgr = (CyApplicationManager)getService(bc, org.cytoscape.application.CyApplicationManager.class);
		CyNetworkViewManager netViewMgr = (CyNetworkViewManager)getService(bc, org.cytoscape.view.model.CyNetworkViewManager.class);
		CyNetworkManager netMgr = (CyNetworkManager)getService(bc, org.cytoscape.model.CyNetworkManager.class);
		TaskManager taskMgr = (TaskManager)getService(bc, org.cytoscape.work.TaskManager.class);
		CyNetworkViewFactory netViewFactory = (CyNetworkViewFactory)getService(bc, org.cytoscape.view.model.CyNetworkViewFactory.class);
		CyRootNetworkManager rootNetworkMgr = (CyRootNetworkManager)getService(bc, org.cytoscape.model.subnetwork.CyRootNetworkManager.class);
		CySwingApplication swingApp = (CySwingApplication)getService(bc, org.cytoscape.application.swing.CySwingApplication.class);
		RenderingEngineFactory dingRenderingEngineFactory = (RenderingEngineFactory)getService(bc, org.cytoscape.view.presentation.RenderingEngineFactory.class, "(id=ding)");
		CyServiceRegistrar serviceRegistrar = (CyServiceRegistrar)getService(bc, org.cytoscape.service.util.CyServiceRegistrar.class);
		VisualStyleFactory visualStyleFactory = (VisualStyleFactory)getService(bc, org.cytoscape.view.vizmap.VisualStyleFactory.class);
		VisualMappingManager visualMappingMgr = (VisualMappingManager)getService(bc, org.cytoscape.view.vizmap.VisualMappingManager.class);
		VisualMappingFunctionFactory discreteMappingFactory = (VisualMappingFunctionFactory)getService(bc, org.cytoscape.view.vizmap.VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
		VisualMappingFunctionFactory continuousMappingFactory = (VisualMappingFunctionFactory)getService(bc, org.cytoscape.view.vizmap.VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
		
		CyTableFactory cyDataTableFactoryServiceRef = getService(bc,CyTableFactory.class);
		MapTableToNetworkTablesTaskFactory mapNetworkAttrTFServiceRef = getService(bc,MapTableToNetworkTablesTaskFactory.class);
	
		
//		Sample11TaskFactory sample11TaskFactory = new Sample11TaskFactory(cyDataTableFactoryServiceRef,mapNetworkAttrTFServiceRef);
		
		
		
		FileUtil fileUtil = (FileUtil)getService(bc, org.cytoscape.util.swing.FileUtil.class);
		OpenBrowser openBrowser = (OpenBrowser)getService(bc, org.cytoscape.util.swing.OpenBrowser.class);
		CyEventHelper eventHelper = (CyEventHelper)getService(bc, org.cytoscape.event.CyEventHelper.class);
		ClusterUtil clusterUtil = new ClusterUtil(dingRenderingEngineFactory, netViewFactory, rootNetworkMgr, appMgr, netMgr, netViewMgr, visualStyleFactory,
				visualMappingMgr, swingApp, eventHelper, discreteMappingFactory, continuousMappingFactory, fileUtil,mapNetworkAttrTFServiceRef);

		
/*		MainPanel mainPanel = new MainPanel(cytoscapeDesktopService, clusterUtil);
		StartAction m = new StartAction(cytoscapeDesktopService,cyApplicationManagerServiceRef,mainPanel);
	*/	
	/*	registerService(bc,mainPanel,CytoPanelComponent.class, new Properties());
		registerService(bc,m,CyAction.class, new Properties());
		
	*/	
		
		AnalyzeAction analyzeAction = new AnalyzeAction("Analyze current network", appMgr, swingApp, netViewMgr, serviceRegistrar, taskMgr, null, clusterUtil);
		//MCODEHelpAction helpAction = new MCODEHelpAction("Help", appMgr, swingApp, netViewMgr, openBrowser);
		MCODEVisualStyleAction visualStyleAction = new MCODEVisualStyleAction("Apply Viz style", appMgr, swingApp, netViewMgr, visualMappingMgr, clusterUtil);
	//	MCODEAboutAction aboutAction = new MCODEAboutAction("About", appMgr, swingApp, netViewMgr, openBrowser, clusterUtil);
		//registerService(bc, helpAction, org.cytoscape.application.swing.CyAction.class, new Properties());
	//	registerService(bc, aboutAction, org.cytoscape.application.swing.CyAction.class, new Properties());
		registerAllServices(bc, analyzeAction, new Properties());
		registerService(bc, visualStyleAction, org.cytoscape.application.swing.CyAction.class, new Properties());
		registerService(bc, visualStyleAction, org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener.class, new Properties());
		MCODEOpenTaskFactory openTaskFactory = new MCODEOpenTaskFactory(swingApp, serviceRegistrar, clusterUtil, analyzeAction);
		Properties openTaskFactoryProps = new Properties();
		openTaskFactoryProps.setProperty("preferredMenu", "Apps.CytoCluster");
		openTaskFactoryProps.setProperty("title", "Open CytoCluster");
		openTaskFactoryProps.setProperty("menuGravity", "1.0");
		registerService(bc, openTaskFactory, org.cytoscape.work.TaskFactory.class, openTaskFactoryProps);
		MCODECloseTaskFactory closeTaskFactory = new MCODECloseTaskFactory(swingApp, serviceRegistrar, clusterUtil);
		Properties closeTaskFactoryProps = new Properties();
		closeTaskFactoryProps.setProperty("preferredMenu", "Apps.CytoCluster");
		closeTaskFactoryProps.setProperty("title", "Close CytoCluster");
		closeTaskFactoryProps.setProperty("menuGravity", "2.0");
		
		
		registerService(bc, closeTaskFactory, org.cytoscape.work.TaskFactory.class, closeTaskFactoryProps);
		registerService(bc, closeTaskFactory, org.cytoscape.model.events.NetworkAboutToBeDestroyedListener.class, new Properties());
		
		
		
		
		

	}
}