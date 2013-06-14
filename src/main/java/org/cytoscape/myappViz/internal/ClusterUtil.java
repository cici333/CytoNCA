package org.cytoscape.myappViz.internal;



import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.myappViz.internal.CyActivator;
import org.cytoscape.myappViz.internal.Cluster;
import org.cytoscape.myappViz.internal.ParameterSet;
import org.cytoscape.myappViz.internal.MainPanel;
import org.cytoscape.myappViz.internal.ResultPanel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.edit.MapTableToNetworkTablesTaskFactory;
import org.cytoscape.util.swing.FileChooserFilter;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualPropertyDependency;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cytoscape.myappViz.internal.algorithm.*;

import BiNGO.BiNGO.SpringEmbeddedLayouter;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Gary Bader
 * * Date: Jun 25, 2004
 * * Time: 7:00:13 PM
 * * Description: Utilities for MCODE
 */

/**
 * Utilities for Clustering
 */
public class ClusterUtil {

    private static boolean INTERRUPTED = false;
    private static Image placeHolderImage = null;

    
    
    
	private final RenderingEngineFactory renderingEngineFactory;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyRootNetworkManager rootNetworkMgr;
	private final CyApplicationManager applicationMgr;
	private final CyNetworkViewManager networkViewMgr;
	private final CyNetworkManager networkMgr;
	private final VisualStyleFactory visualStyleFactory;
	private final VisualMappingManager visualMappingMgr;
	private final CySwingApplication swingApplication;
	private final CyEventHelper eventHelper;
	private final VisualMappingFunctionFactory discreteMappingFactory;
	private final VisualMappingFunctionFactory continuousMappingFactory;
	private final FileUtil fileUtil;
	private final Properties props = loadProperties("/mcode.properties");
	
	private final MapTableToNetworkTablesTaskFactory mapNetworkAttrTFServiceRef;
	private boolean interrupted;
	private VisualStyle clusterStyle;
	private VisualStyle appStyle;
	 private CurrentParameters currentParameters;
	  private Map<Long, Algorithm> networkAlgorithms;
	  private Map<Long, Set<Integer>> networkResults;
	private int currentResultId;
	private Map createdSubNetworks;
	
	private static final Logger logger = LoggerFactory.getLogger(org.cytoscape.myappViz.internal.ClusterUtil.class);

	public ClusterUtil(RenderingEngineFactory renderingEngineFactory, CyNetworkViewFactory networkViewFactory, CyRootNetworkManager rootNetworkMgr, CyApplicationManager applicationMgr, CyNetworkManager networkMgr, CyNetworkViewManager networkViewMgr, VisualStyleFactory visualStyleFactory, 
			VisualMappingManager visualMappingMgr, CySwingApplication swingApplication, CyEventHelper eventHelper,
			VisualMappingFunctionFactory discreteMappingFactory, VisualMappingFunctionFactory continuousMappingFactory,
			FileUtil fileUtil,MapTableToNetworkTablesTaskFactory mapNetworkAttrTFServiceRef)
	{
		this.renderingEngineFactory = renderingEngineFactory;
		this.networkViewFactory = networkViewFactory;
		this.rootNetworkMgr = rootNetworkMgr;
		this.applicationMgr = applicationMgr;
		this.networkMgr = networkMgr;
		this.networkViewMgr = networkViewMgr;
		this.visualStyleFactory = visualStyleFactory;
		this.visualMappingMgr = visualMappingMgr;
		this.swingApplication = swingApplication;
		this.eventHelper = eventHelper;
		this.discreteMappingFactory = discreteMappingFactory;
		this.continuousMappingFactory = continuousMappingFactory;
		this.fileUtil = fileUtil;
		this.mapNetworkAttrTFServiceRef=mapNetworkAttrTFServiceRef;
		reset();
	}

    /**
     * Convert a network to an image that will be shown in the ResultsPanel.
     *
     * @param loader Graphic loader displaying progress and process
     * @param cluster Input network to convert to an image
     * @param height  Height that the resulting image should be
     * @param width   Width that the resulting image should be
     * @param layouter Reference to the layout algorithm
     * @param layoutNecessary Determinant of cluster size growth or shrinkage, the former requires layout
     * @return The resulting image
     */
    public Image convertClusterToImage(Loader loader, final Cluster cluster, 
    		final int height, final int width, SpringEmbeddedLayouter layouter, boolean layoutNecessary) {
    	
    	
    	
    	CyNetwork net = cluster.getNetwork();
		int weightSetupNodes = 20;
		int weightSetupEdges = 5;
		double weightLayout = 75D;
		double goalTotal = weightSetupNodes + weightSetupEdges;
		if (layoutNecessary)
			goalTotal += weightLayout;
		double progress = 0.0D;
		final VisualStyle vs = getClusterStyle();
		final CyNetworkView clusterView = createNetworkView(net, vs);
		clusterView.setVisualProperty(BasicVisualLexicon.NETWORK_WIDTH, new Double(width));
		clusterView.setVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT, new Double(height));
		for (Iterator iterator = clusterView.getNodeViews().iterator(); iterator.hasNext();)
		{
			View nv = (View)iterator.next();
			if (interrupted)
			{
				logger.debug("Interrupted: Node Setup");
				if (layouter != null)
					layouter.resetDoLayout();
				resetLoading();
				return null;
			}
			double x;
			double y;
			if (cluster.getView() != null && cluster.getView().getNodeView((CyNode)nv.getModel()) != null)
			{
				x = ((Double)cluster.getView().getNodeView((CyNode)nv.getModel()).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)).doubleValue();
				y = ((Double)cluster.getView().getNodeView((CyNode)nv.getModel()).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)).doubleValue();
			} else
			{
				x = (((Double)clusterView.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH)).doubleValue() + 100D) * Math.random();
				y = (((Double)clusterView.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT)).doubleValue() + 100D) * Math.random();
				if (!layoutNecessary)
				{
					goalTotal += weightLayout;
					progress /= goalTotal / (goalTotal - weightLayout);
					layoutNecessary = true;
				}
			}
			nv.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, Double.valueOf(x));
			nv.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, Double.valueOf(y));
			if (cluster.getSeedNode() == ((CyNode)nv.getModel()).getSUID())
				nv.setLockedValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.RECTANGLE);
			else
				nv.setLockedValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
			if (loader != null)
			{
				progress += 100D * (1.0D / (double)clusterView.getNodeViews().size()) * ((double)weightSetupNodes / goalTotal);
				loader.setProgress((int)progress, "Setup: nodes");
			}
		}

		if (clusterView.getEdgeViews() != null)
		{
			for (int i = 0; i < clusterView.getEdgeViews().size(); i++)
			{
				if (interrupted)
				{
					logger.error("Interrupted: Edge Setup");
					if (layouter != null)
						layouter.resetDoLayout();
					resetLoading();
					return null;
				}
				if (loader != null)
				{
					progress += 100D * (1.0D / (double)clusterView.getEdgeViews().size()) * ((double)weightSetupEdges / goalTotal);
					loader.setProgress((int)progress, "Setup: edges");
				}
			}

		}
		if (layoutNecessary)
		{
			if (layouter == null)
				layouter = new SpringEmbeddedLayouter();
			layouter.setGraphView(clusterView);
			if (!layouter.doLayout(weightLayout, goalTotal, progress, loader))
			{
				resetLoading();
				return null;
			}
		}
		java.awt.Image image = new java.awt.image.BufferedImage(width, height, 2);
		final java.awt.Graphics2D g = (java.awt.Graphics2D)image.getGraphics();
		SwingUtilities.invokeLater(new Runnable() {

		

			public void run()
			{
				try
				{
					java.awt.Dimension size = new java.awt.Dimension(width, height);
					JPanel panel = new JPanel();
					panel.setPreferredSize(size);
					panel.setSize(size);
					panel.setMinimumSize(size);
					panel.setMaximumSize(size);
					panel.setBackground((java.awt.Color)vs.getDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT));
					JWindow window = new JWindow();
					window.getContentPane().add(panel, "Center");
					RenderingEngine re = renderingEngineFactory.createRenderingEngine(panel, clusterView);
					vs.apply(clusterView);
					clusterView.fitContent();
					clusterView.updateView();
					window.pack();
					window.repaint();
					re.createImage(width, height);
					re.printCanvas(g);
					g.dispose();
					if (clusterView.getNodeViews().size() > 0)
						cluster.setView(clusterView);
				}
				catch (Exception ex)
				{
					throw new RuntimeException(ex);
				}
			}

		
		});
		layouter.resetDoLayout();
		resetLoading();
		return image;
    	
    	
    	
    
    }


/*	private static CyNetworkView generateGraphView(GraphPerspective gp) {
		CyNetworkView view = new CyNetworkView(gp);
        final int[] nodes = gp.getNodeIndicesArray();
        for (int i = 0; i < nodes.length; i++) {
            view.addNodeView(nodes[i]);
        }
        final int[] edges = gp.getEdgeIndicesArray();
        for (int i = 0; i < edges.length; i++) {
            view.addEdgeView(edges[i]);
        }
		return view;
	}*/
	
/*    public static void interruptLoading() {
        INTERRUPTED = true;
    }
    public static void resetLoading() {
        INTERRUPTED = false;
    }*/

    /**
     * Converts a list of clusters to a list of networks that is sorted by the score of the cluster
     *
     * @param clusters   List of generated clusters
     * @return A sorted array of cluster objects based on cluster score.
     */
    public static Cluster[] sortClusters(Cluster[] clusters) {
        Arrays.sort(clusters, new Comparator() {
            //sorting clusters by decreasing score
            public int compare(Object o1, Object o2) {
                double d1 = ((Cluster) o1).getClusterScore();
                double d2 = ((Cluster) o2).getClusterScore();
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return clusters;
    }

    /**
     * Converts a list of clusters to a list of networks that is sorted by size
     *
     * @param clusters   List of generated clusters
     * @return A sorted array of cluster objects based on cluster score.
     */
    public static Cluster[] sortClusters2(Cluster[] clusters) {
        Arrays.sort(clusters, new Comparator() {
            //sorting clusters by decreasing score
            public int compare(Object o1, Object o2) {
                double d1 = ((Cluster) o1).getALNodes().size();
                double d2 = ((Cluster) o2).getALNodes().size();
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return clusters;
    }
    /**
     * Converts a list of clusters to a list of networks that is sorted by modularity
     *
     * @param clusters   List of generated clusters
     * @return A sorted array of cluster objects based on cluster score.
     */
    public static Cluster[] sortClusters3(Cluster[] clusters) {
        Arrays.sort(clusters, new Comparator() {
            //sorting clusters by decreasing score
            public int compare(Object o1, Object o2) {
                double d1 = ((Cluster) o1).getModularity();
                double d2 = ((Cluster) o2).getModularity();
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return clusters;
    }

    /**
     * A utility method to convert ArrayList to int[]
     *
     * @param alInput ArrayList input
     * @return int array
     */
    public static Long[] convertIntArrayList2array(ArrayList alInput) {
        Long[] outputNodeIndices = new Long[alInput.size()];
        int j = 0;
        for (Iterator i = alInput.iterator(); i.hasNext(); j++) {
            outputNodeIndices[j] = ((Long) i.next()).longValue();
        }
        return (outputNodeIndices);
    }

    /**
     * ClusterUtility method to get the names of all the nodes in a GraphPerspective
     *
     * @param gpInput The input graph perspective to get the names from
     * @return A concatenated set of all node names (separated by a comma)
     */
/*    public static StringBuffer getNodeNameList(GraphPerspective gpInput) {
        Iterator i = gpInput.nodesIterator();
        StringBuffer sb = new StringBuffer();
        while (i.hasNext()) {
            Node node = (Node) i.next();
            sb.append(node.getIdentifier());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        return (sb);
    }*/
    /**
     * Save results to a file
     *
     * @param alg       The algorithm instance containing parameters, etc.
     * @param complexes  The list of clusters
     * @param network   The network source of the clusters
     * @param fileName  The file name to write to
     * @return True if the file was written, false otherwise
     */
    public boolean exportResults0(Algorithm alg, Cluster[] complexes, CyNetwork network, String fileName) {
        if (alg == null || complexes == null || network == null || fileName == null) {
            return false;
        }
        String lineSep = System.getProperty("line.separator");
        try {
            File file = new File(fileName);
            FileWriter fout = new FileWriter(file);
            //write header
            fout.write("Clustering Results" + lineSep);
            fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
            fout.write("Parameters:" + lineSep + alg.getParams().toString() + lineSep);
            fout.write("Complex	Score (Density*#Nodes)\tNodes\tEdges\tNode IDs" + lineSep);
            //get GraphPerspectives for all clusters, score and rank them
            //convert the ArrayList to an array of GraphPerspectives and sort it by cluster score
            //GraphPerspective[] gpClusterArray = ClusterUtil.convertClusterListToSortedNetworkList(clusters, network, alg);
            for (int i = 0; i < complexes.length; i++) {
            	
            	Cluster c = (Cluster)complexes[i];
                //GraphPerspective gpCluster = complexes[i].getGPCluster();
            	CyNetwork clusterNetwork = c.getNetwork();
                fout.write((i + 1) + "\t"); //rank
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);
                fout.write(nf.format(complexes[i].getClusterScore()) + "\t");
                //cluster size - format: (# prot, # intx)
    			fout.write((new StringBuilder(String.valueOf(clusterNetwork.getNodeCount()))).append("\t").toString());
    			fout.write((new StringBuilder(String.valueOf(clusterNetwork.getEdgeCount()))).append("\t").toString());
    			fout.write((new StringBuilder(String.valueOf(getNodeNameList(clusterNetwork)))).append(lineSep).toString());               
                
/*                fout.write(gpCluster.getNodeCount() + "\t");
                fout.write(gpCluster.getEdgeCount() + "\t");
                //create a string of node names - this can be long
                fout.write(getNodeNameList(gpCluster).toString() + lineSep);*/
            }
            fout.close();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "Error while exporting Write file " + fileName + " exceptioin! \"",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    public boolean exportResults(Algorithm alg, List<Cluster> clusters, CyNetwork network) {
        if (alg == null || clusters == null || network == null ) {
            return false;
        }
        String lineSep = System.getProperty("line.separator");
        
        String fileName = null;
        FileWriter fout = null;
        try
        {
          Collection filters = new ArrayList();
          filters.add(new FileChooserFilter("Text format", "txt"));
          File file = this.fileUtil.getFile(this.swingApplication.getJFrame(), 
            "Export Graph as Interactions", 
            1, 
            filters);

          if (file != null) {
            fileName = file.getAbsolutePath();
            fout = new FileWriter(file);
        
        
        
   
            
            //write header
            fout.write("Clustering Results\t"+clusters.size() +" complexes in all"+ lineSep);
            fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
            fout.write("Parameters:" + lineSep + alg.getParams().toString() + lineSep);
            //get GraphPerspectives for all clusters, score and rank them
            //convert the ArrayList to an array of GraphPerspectives and sort it by cluster score
            //GraphPerspective[] gpClusterArray = ClusterUtil.convertClusterListToSortedNetworkList(clusters, network, alg);
            for (int i = 0; i < clusters.size(); i++) {
            	
            	Cluster c = (Cluster)clusters.get(i);
                //GraphPerspective gpCluster = complexes[i].getGPCluster();
            	 fout.write("Complex "+(i + 1)+"  "); //rank
            	 
            	CyNetwork  clusterNetwork= c.getNetwork();
            	fout.write(clusterNetwork.getNodeCount()+" "+lineSep);
               
            	  Iterator it = clusterNetwork.getNodeList().iterator();
                  while (it.hasNext()) {
                      CyNode node = (CyNode) it.next();
                      CyRow row = network.getRow(node);
	          			String name = (new StringBuilder()).append(node.getSUID()).toString();
	          			if (row.isSet("name"))
	          				name = (String)row.get("name", String.class);
                      
                      fout.write(name+lineSep);
                  }
            	
            	
            
            /*	
            	
                GraphPerspective gpCluster = complexes[i].getGPCluster();
                fout.write("Complex "+(i + 1)+"  "); //rank
                fout.write(gpCluster.getNodeCount()+" "+lineSep);
                //fout.write(gpCluster.getEdgeCount()+lineSep);
                Iterator it = gpCluster.nodesIterator();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    String name=node.getIdentifier();
                    fout.write(name+lineSep);
                }*/
            }
            fout.close();
            return true;
          }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
            		"Error while exporting Write file \"" + fileName + " exceptioin! \"",
                    JOptionPane.ERROR_MESSAGE);
            
        }
        return false;
    }
    /**
     * Save results to a file
     *
     * @param alg       The algorithm instance containing parameters, etc.
     * @param complexes  The list of clusters
     * @param network   The network source of the clusters
     * @param fileName  The file name to write to
     * @return True if the file was written, false otherwise
     */
    public static boolean exportSimpleClusters(Algorithm alg, Cluster[] complexes, CyNetwork network, String fileName) {
        if (alg == null || complexes == null || network == null || fileName == null) {
            return false;
        }
        String lineSep = System.getProperty("line.separator");
        try {
            File file = new File(fileName);
            FileWriter fout = new FileWriter(file);
            for (int i = 0; i < complexes.length; i++) {
            	
            	Cluster c = (Cluster)complexes[i];
            	 fout.write("Complex "+(i + 1)+"  "); //rank
            	 
             	CyNetwork  clusterNetwork= c.getNetwork();
             	fout.write(clusterNetwork.getNodeCount()+" "+lineSep);
             	  Iterator it = clusterNetwork.getNodeList().iterator();
                  while (it.hasNext()) {
                      CyNode node = (CyNode) it.next();
                      CyRow row = network.getRow(node);
	          			String name = (new StringBuilder()).append(node.getSUID()).toString();
	          			if (row.isSet("name"))
	          				name = (String)row.get("name", String.class);
                      
                      fout.write(name+lineSep);
                  }
            	
            	
            	
            	
               /* GraphPerspective gpCluster = complexes[i].getGPCluster();
            	
            	
                fout.write("Complex "+(i + 1)+"  "); //rank
                fout.write(gpCluster.getNodeCount()+" "+lineSep);
                Iterator it = gpCluster.nodesIterator();
                while (it.hasNext()) {
                    Node node = (Node) it.next();
                    String name=node.getIdentifier();
                    fout.write(name+lineSep);
                }*/
            }
            fout.close();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
            		"Error while exporting Write file \"" + fileName + " exceptioin! \"",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }   
    /**
     * Generates an image of a place holder showing message.
     *
     * @param width width of the image
     * @param height height of the image
     * @return place holder
     */
    public static Image getPlaceHolderImage(int width, int height) {
        //We only want to generate a place holder image once so that memory is not eaten up
        if (placeHolderImage == null) {
            Image image;
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            int fontSize = 10;
            g2.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font f = g2.getFont();
            FontMetrics fm = g2.getFontMetrics(f);

            //Place Holder text
            String placeHolderText = "The complex is too large to visualize";
            //We want to center the text vertically in the top 20 pixels
            height = 20;
            //White outline
            g2.setColor(Color.BLACK);
            g2.drawString(placeHolderText, (width / 2) - (fm.stringWidth(placeHolderText) / 2) - 1, (height / 2) + (fontSize / 2) - 1);
            g2.drawString(placeHolderText, (width / 2) - (fm.stringWidth(placeHolderText) / 2) - 1, (height / 2) + (fontSize / 2) + 1);
            g2.drawString(placeHolderText, (width / 2) - (fm.stringWidth(placeHolderText) / 2) + 1, (height / 2) + (fontSize / 2) - 1);
            g2.drawString(placeHolderText, (width / 2) - (fm.stringWidth(placeHolderText) / 2) + 1, (height / 2) + (fontSize / 2) + 1);
            //Red text
            g2.setColor(Color.BLUE);
            g2.drawString(placeHolderText, (width / 2) - (fm.stringWidth(placeHolderText) / 2), (height / 2) + (fontSize / 2));

            placeHolderImage = image;
        }
        return placeHolderImage;
    }
    
    
    
    
    
    
    
    
    
	public int getCurrentResultId()
	{
		return currentResultId;
	}

	public String getProperty(String key)
	{
		return props.getProperty(key);
	}

	public void reset()
	{
		currentResultId = 1;
		currentParameters = new CurrentParameters();
		networkAlgorithms = new HashMap<Long, Algorithm>();
		networkResults = new HashMap<Long, Set<Integer>> ();
		createdSubNetworks = new HashMap();
	}

	public synchronized void destroyUnusedNetworks(CyNetwork network, List clusters)
	{
		Map clusterNetworks = new HashMap();
		if (clusters != null)
		{
			Cluster c;
			for (Iterator iterator = clusters.iterator(); iterator.hasNext(); clusterNetworks.put(c.getNetwork(), Boolean.TRUE))
				c = (Cluster)iterator.next();

		}
		CyRootNetwork rootNet = rootNetworkMgr.getRootNetwork(network);
		Set snSet = (Set)createdSubNetworks.get(rootNet);
		if (snSet != null)
		{
			Set disposedSet = new HashSet();
			for (Iterator iterator1 = snSet.iterator(); iterator1.hasNext();)
			{
				CySubNetwork sn = (CySubNetwork)iterator1.next();
				if (!clusterNetworks.containsKey(sn) && !networkMgr.networkExists(sn.getSUID().longValue()))
					try
					{
						destroy(sn);
						disposedSet.add(sn);
					}
					catch (Exception e)
					{
						logger.error((new StringBuilder("Error disposing: ")).append(sn).toString(), e);
					}
			}

			snSet.removeAll(disposedSet);
		}
	}

	public void destroy(CySubNetwork net)
	{
		if (net != null)
		{
			CyRootNetwork rootNet = rootNetworkMgr.getRootNetwork(net);
			if (rootNet.containsNetwork(net))
			{
				rootNet.removeSubNetwork(net);
				net.dispose();
			}
		}
	}



	  public Algorithm getNetworkAlgorithm(long suid) {
	    return (Algorithm)this.networkAlgorithms.get(Long.valueOf(suid));
	  }

	  public void addNetworkAlgorithm(long suid, Algorithm alg) {
	    this.networkAlgorithms.put(Long.valueOf(suid), alg);
	  }
	
	
	
	public boolean containsNetworkAlgorithm(long suid)
	{
		return networkAlgorithms.containsKey(Long.valueOf(suid));
	}



	public void removeNetworkAlgorithm(long suid)
	{
		networkAlgorithms.remove(Long.valueOf(suid));
	}

	public boolean containsNetworkResult(long suid)
	{
		return networkResults.containsKey(Long.valueOf(suid));
	}

	public Set getNetworkResults(long suid)
	{
		Set ids = (Set)networkResults.get(Long.valueOf(suid));
		return ((Set) (ids == null ? new HashSet() : ids));
	}

	public void addNetworkResult(long suid)
	{
		Set ids = (Set)networkResults.get(Long.valueOf(suid));
		if (ids == null)
		{
			ids = new HashSet();
			networkResults.put(Long.valueOf(suid), ids);
		}
		ids.add(Integer.valueOf(currentResultId++));
	}


	public  ClusterGraph createGraph(CyNetwork net, Collection<CyNode> nodes)
	{
		CyRootNetwork root = rootNetworkMgr.getRootNetwork(net);
		Set edges = new HashSet();
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();)
		{
			CyNode n = (CyNode)iterator.next();
			Set adjacentEdges = new HashSet(net.getAdjacentEdgeList(n, org.cytoscape.model.CyEdge.Type.ANY));
			for (Iterator iterator1 = adjacentEdges.iterator(); iterator1.hasNext();)
			{
				CyEdge e = (CyEdge)iterator1.next();
				if (nodes.contains(e.getSource()) && nodes.contains(e.getTarget()))
					edges.add(e);
			}

		}

		ClusterGraph graph = new ClusterGraph(root, nodes, edges, this);
		return graph;
	}

	public CySubNetwork createSubNetwork(CyNetwork net, Collection nodes, SavePolicy policy)
	{
		CyRootNetwork root = rootNetworkMgr.getRootNetwork(net);
		Set edges = new HashSet();
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();)
		{
			CyNode n = (CyNode)iterator.next();
			Set adjacentEdges = new HashSet(net.getAdjacentEdgeList(n, org.cytoscape.model.CyEdge.Type.ANY));
			for (Iterator iterator1 = adjacentEdges.iterator(); iterator1.hasNext();)
			{
				CyEdge e = (CyEdge)iterator1.next();
				if (nodes.contains(e.getSource()) && nodes.contains(e.getTarget()))
					edges.add(e);
			}

		}

		CySubNetwork subNet = root.addSubNetwork(nodes, edges, policy);
		Set snSet = (Set)createdSubNetworks.get(root);
		if (snSet == null)
		{
			snSet = new HashSet();
			createdSubNetworks.put(root, snSet);
		}
		snSet.add(subNet);
		return subNet;
	}

	public CyNetworkView createNetworkView(CyNetwork net, VisualStyle vs)
	{
		CyNetworkView view = networkViewFactory.createNetworkView(net);
		if (vs == null)
			vs = visualMappingMgr.getDefaultVisualStyle();
		visualMappingMgr.setVisualStyle(vs, view);
		vs.apply(view);
		view.updateView();
		return view;
	}

	public void displayNetworkView(CyNetworkView view)
	{
		networkMgr.addNetwork((CyNetwork)view.getModel());
		networkViewMgr.addNetworkView(view);
		view.fitContent();
		view.updateView();
	}

	public VisualStyle getClusterStyle()
	{
		if (clusterStyle == null)
		{
			clusterStyle = visualStyleFactory.createVisualStyle("MCODE Cluster");
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_SIZE, Double.valueOf(40D));
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, Double.valueOf(40D));
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, Double.valueOf(40D));
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_PAINT, java.awt.Color.RED);
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.RED);
			clusterStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, Double.valueOf(0.0D));
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(5D));
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_PAINT, java.awt.Color.BLUE);
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_UNSELECTED_PAINT, java.awt.Color.BLUE);
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, java.awt.Color.BLUE);
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_SELECTED_PAINT, java.awt.Color.BLUE);
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, java.awt.Color.BLUE);
			clusterStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, java.awt.Color.BLUE);
			VisualLexicon lexicon = applicationMgr.getCurrentRenderingEngine().getVisualLexicon();
			VisualProperty vp = lexicon.lookup(CyEdge.class, "edgeTargetArrowShape");
			if (vp != null)
			{
				Object arrowValue = vp.parseSerializableString("ARROW");
				if (arrowValue != null)
					clusterStyle.setDefaultValue(vp, arrowValue);
			}
		}
		return clusterStyle;
	}


	public VisualStyle getNetworkViewStyle(CyNetworkView view)
	{
		return view == null ? null : visualMappingMgr.getVisualStyle(view);
	}

	public void registerVisualStyle(VisualStyle style)
	{
		if (!visualMappingMgr.getAllVisualStyles().contains(style))
			visualMappingMgr.addVisualStyle(style);
	}
	
	public boolean removeNetworkResult(int resultId)
	{
		boolean removed = false;
		Long networkId = null;
		for (Iterator iterator = networkResults.entrySet().iterator(); iterator.hasNext();)
		{
			java.util.Map.Entry entries = (java.util.Map.Entry)iterator.next();
			Set ids = (Set)entries.getValue();
			if (ids.remove(Integer.valueOf(resultId)))
			{
				if (ids.isEmpty())
					networkId = (Long)entries.getKey();
				removed = true;
				break;
			}
		}

		if (networkId != null)
			networkResults.remove(networkId);
		getCurrentParameters().removeResultParams(resultId);
		return removed;
	}

	
	public CurrentParameters getCurrentParameters()
	{
		return currentParameters;
	}
	
	
	public void setSelected(Collection elements, CyNetwork network)
	{
		Collection allElements = new ArrayList(network.getNodeList());
		allElements.addAll(network.getEdgeList());
		CyIdentifiable nodeOrEdge;
		boolean select;
		for (Iterator iterator = allElements.iterator(); iterator.hasNext(); network.getRow(nodeOrEdge).set("selected", Boolean.valueOf(select)))
		{
			nodeOrEdge = (CyIdentifiable)iterator.next();
			select = elements.contains(nodeOrEdge);
		}

		eventHelper.flushPayloadEvents();
		Collection netViews = networkViewMgr.getNetworkViews(network);
		CyNetworkView view;
		for (Iterator iterator1 = netViews.iterator(); iterator1.hasNext(); view.updateView())
			view = (CyNetworkView)iterator1.next();

		swingApplication.getJFrame().repaint();
	}

	public void interruptLoading()
	{
		interrupted = true;
	}

	public void resetLoading()
	{
		interrupted = false;
	}




	public String getNodeNameList(CyNetwork network)
	{
		StringBuffer sb = new StringBuffer();
		for (Iterator iterator = network.getNodeList().iterator(); iterator.hasNext(); sb.append(", "))
		{
			CyNode node = (CyNode)iterator.next();
			CyRow row = network.getRow(node);
			String id = (new StringBuilder()).append(node.getSUID()).toString();
			if (row.isSet("name"))
				id = (String)row.get("name", String.class);
			sb.append(id);
		}

		if (sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}




	private static Properties loadProperties(String name)
	{
		Properties props = new Properties();
		try
		{
			InputStream in = CyActivator.class.getResourceAsStream(name);
			if (in != null)
			{
				props.load(in);
				in.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return props;
	}


    
    
	 public boolean isOpened()
	  {
	    return getMainPanel() != null;
	  }

    
	  public MainPanel getMainPanel()
	  {
	    CytoPanel cytoPanel = getControlCytoPanel();
	    int count = cytoPanel.getCytoPanelComponentCount();

	    for (int i = 0; i < count; i++) {
	      if ((cytoPanel.getComponentAt(i) instanceof MainPanel)) {
	        return (MainPanel)cytoPanel.getComponentAt(i);
	      }
	    }
	    return null;
	  }

	  
	  public CytoPanel getControlCytoPanel()
	  {
	    return this.swingApplication.getCytoPanel(CytoPanelName.WEST);
	  }

    
    
    
	  public VisualStyle getAppStyle(double maxScore) {
		    if (this.appStyle == null) {
		      this.appStyle = this.visualStyleFactory.createVisualStyle("MCODE");

		      DiscreteMapping nodeShapeDm = (DiscreteMapping)this.discreteMappingFactory
		        .createVisualMappingFunction("MCODE_Node_Status", String.class, BasicVisualLexicon.NODE_SHAPE);

		      nodeShapeDm.putMapValue("Clustered", NodeShapeVisualProperty.ELLIPSE);
		      nodeShapeDm.putMapValue("Seed", NodeShapeVisualProperty.RECTANGLE);
		      nodeShapeDm.putMapValue("Unclustered", NodeShapeVisualProperty.DIAMOND);

		      for (VisualPropertyDependency dep : this.appStyle.getAllVisualPropertyDependencies()) {
		        if ((dep.getParentVisualProperty() == BasicVisualLexicon.NODE_SIZE) && 
		          (dep.getVisualProperties().contains(BasicVisualLexicon.NODE_WIDTH)) && 
		          (dep.getVisualProperties().contains(BasicVisualLexicon.NODE_HEIGHT))) {
		          dep.setDependency(true);
		        }
		      }
		      this.appStyle.addVisualMappingFunction(nodeShapeDm);
		    }

		    this.appStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, Color.WHITE);

		    this.appStyle.removeVisualMappingFunction(BasicVisualLexicon.NODE_FILL_COLOR);

		    ContinuousMapping nodeColorCm = (ContinuousMapping)this.continuousMappingFactory
		      .createVisualMappingFunction("MCODE_Score", Double.class, BasicVisualLexicon.NODE_FILL_COLOR);

		    Color MIN_COLOR = Color.BLACK;
		    Color MAX_COLOR = Color.RED;

		    nodeColorCm.addPoint(Double.valueOf(0.0D), new BoundaryRangeValues(Color.WHITE, Color.WHITE, MIN_COLOR));

		    nodeColorCm.addPoint(Double.valueOf(maxScore), new BoundaryRangeValues(MAX_COLOR, MAX_COLOR, MAX_COLOR));

		    this.appStyle.addVisualMappingFunction(nodeColorCm);

		    return this.appStyle;
		  }

	
	  public CytoPanel getResultsCytoPanel()
	  {
	    return this.swingApplication.getCytoPanel(CytoPanelName.EAST);
	  }


	  public Collection<ResultPanel> getResultPanels()
	  {
	    Collection panels = new ArrayList();
	    CytoPanel cytoPanel = getResultsCytoPanel();
	    int count = cytoPanel.getCytoPanelComponentCount();

	    for (int i = 0; i < count; i++) {
	      if ((cytoPanel.getComponentAt(i) instanceof ResultPanel)) {
	        panels.add((ResultPanel)cytoPanel.getComponentAt(i));
	      }
	    }
	    return panels;
	  }

	  public ResultPanel getResultPanel(int resultId) {
	    for (ResultPanel panel : getResultPanels()) {
	      if (panel.getResultId() == resultId) return panel;
	    }

	    return null;
	  }
    
    
    
}
