package org.cytoscape.CytoNCA.internal.actions;


import java.util.*;

import org.cytoscape.CytoNCA.internal.panels.MainPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.*;
import org.cytoscape.view.model.CyNetworkViewManager;

public abstract class AbstractPAction extends AbstractCyAction
{

	private static final long serialVersionUID = 0xbb92c5989a7b4b9L;
	protected final CySwingApplication swingApplication;
	protected final CyApplicationManager applicationManager;
	protected final CyNetworkViewManager netViewManager;

	public AbstractPAction(String name, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, String enableFor)
	{
		super(name, applicationManager, enableFor, netViewManager);
		this.applicationManager = applicationManager;
		this.swingApplication = swingApplication;
		this.netViewManager = netViewManager;
	}
/*
	protected CytoPanel getControlCytoPanel()
	{
		return swingApplication.getCytoPanel(CytoPanelName.WEST);
	}

	protected CytoPanel getResultsCytoPanel()
	{
		return swingApplication.getCytoPanel(CytoPanelName.EAST);
	}
	
	protected CytoPanel getEpListsCytoPanel()
	{
		return swingApplication.getCytoPanel(CytoPanelName.EAST);
	}
	
	protected CytoPanel getEvaluationsCytoPanel()
	{
		return swingApplication.getCytoPanel(CytoPanelName.EAST);
	}
	
	

	protected MainPanel getMainPanel()
	{
		CytoPanel cytoPanel = getControlCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();
		for (int i = 0; i < count; i++)
			if (cytoPanel.getComponentAt(i) instanceof MainPanel)
				return (MainPanel)cytoPanel.getComponentAt(i);

		return null;
	}
/*
	protected Collection getResultPanels()
	{
		Collection panels = new ArrayList();
		CytoPanel cytoPanel = getResultsCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();
		for (int i = 0; i < count; i++)
			if (cytoPanel.getComponentAt(i) instanceof ResultPanel)
				panels.add((ResultPanel)cytoPanel.getComponentAt(i));

		return panels;
	}
	
	protected Collection getEpListPanels()
	{
		Collection panels = new ArrayList();
		CytoPanel cytoPanel = getEpListsCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();
		for (int i = 0; i < count; i++)
			if (cytoPanel.getComponentAt(i) instanceof EpListPanel)
				panels.add((EpListPanel)cytoPanel.getComponentAt(i));

		return panels;
	}
	
	protected Collection getEvaluationPanels()
	{
		Collection panels = new ArrayList();
		CytoPanel cytoPanel = getEvaluationsCytoPanel();
		int count = cytoPanel.getCytoPanelComponentCount();
		for (int i = 0; i < count; i++)
			if (cytoPanel.getComponentAt(i) instanceof EvaluationPanel)
				panels.add((EvaluationPanel)cytoPanel.getComponentAt(i));

		return panels;
	}
	

	protected ResultPanel getResultPanel(int resultId)
	{
		for (Iterator iterator = getResultPanels().iterator(); iterator.hasNext();)
		{
			ResultPanel panel = (ResultPanel)iterator.next();
			if (panel.getResultId() == resultId)
				return panel;
		}

		return null;
	}
	
	protected EpListPanel getEpListPanel(int eplistId)
	{
		for (Iterator iterator = getEpListPanels().iterator(); iterator.hasNext();)
		{
			EpListPanel panel = (EpListPanel)iterator.next();
			if (panel.getEplistId() == eplistId)
				return panel;
		}

		return null;
	}
	
	protected EvaluationPanel getEvaluationPanel(int evaluationId)
	{
		for (Iterator iterator = getEvaluationPanels().iterator(); iterator.hasNext();)
		{
			EvaluationPanel panel = (EvaluationPanel)iterator.next();
			if (panel.getEvaluationId() == evaluationId)
				return panel;
		}

		return null;
	}

	protected boolean isOpened()
	{
		return getMainPanel() != null;
	}
*/
}
