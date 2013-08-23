package org.cytoscape.CytoNCA.internal.panels;
import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.cytoscape.CytoNCA.internal.Resources;
import org.cytoscape.CytoNCA.internal.Resources.ImageName;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.jfree.chart.*;

public class ParamsChartPanel extends ChartPanel implements CytoPanelComponent{
	ParamsChartPanel(JFreeChart jfc){
		super(jfc);
		
	}
	
	
	public Component getComponent()
	{
	  return this;
	}

	public CytoPanelName getCytoPanelName()
	{
	  return CytoPanelName.SOUTH;
	}

	public Icon getIcon()
	{
	  URL iconURL = Resources.getUrl(Resources.ImageName.LOGO_SMALL);
	  return new ImageIcon(iconURL);
	}

	public String getTitle()
	{
	  return "hahaha";
	}

}
