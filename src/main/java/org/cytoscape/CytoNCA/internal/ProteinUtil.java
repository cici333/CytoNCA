package org.cytoscape.CytoNCA.internal;



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

import org.cytoscape.CytoNCA.internal.CyActivator;
import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.algorithm.*;
import org.cytoscape.CytoNCA.internal.panels.EpListPanel;
import org.cytoscape.CytoNCA.internal.panels.EvaluationPanel;
import org.cytoscape.CytoNCA.internal.panels.MainPanel;
import org.cytoscape.CytoNCA.internal.panels.ResultPanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.event.CyEventHelper;
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
public class ProteinUtil {

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
	private VisualStyle nodeStyle;
	private VisualStyle appStyle;
	 private CurrentParameters currentParameters;
  private  ArrayList<Long> networkAlgorithms;
	  private Map<Long, Set<Integer>> networkResults;
	  private Map<Long, Set<Integer>> networkEplists;
	private int currentResultId;
	private int currentEplistId;
//	private int currentEvaluationId;
	//private boolean isEvaluation;
	private Map createdSubNetworks;
	private ArrayList<String> Alleprotein;
	
	private static final Logger logger = LoggerFactory.getLogger(org.cytoscape.CytoNCA.internal.ProteinUtil.class);

	public ProteinUtil(RenderingEngineFactory renderingEngineFactory, CyNetworkViewFactory networkViewFactory, CyRootNetworkManager rootNetworkMgr, CyApplicationManager applicationMgr, CyNetworkManager networkMgr, CyNetworkViewManager networkViewMgr, VisualStyleFactory visualStyleFactory, 
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
     * Save results to a file
     *
     * @param alg       The algorithm instance containing parameters, etc.
     * @param complexes  The list of clusters
     * @param network   The network source of the clusters
     * @param fileName  The file name to write to
     * @return True if the file was written, false otherwise
     */
    public boolean exportResults0(Algorithm alg, Protein[] eproteins, CyNetwork network, String fileName) {
        if (alg == null || eproteins == null || network == null || fileName == null) {
            return false;
        }
        String lineSep = System.getProperty("line.separator");
        try {
            File file = new File(fileName);
            FileWriter fout = new FileWriter(file);
            //write header
            fout.write("Results" + lineSep);
            fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
           // fout.write("Parameters:" + lineSep + alg.getParams().toString() + lineSep);
          //  fout.write("Complex	Score (Density*#Nodes)\tNodes\tEdges\tNode IDs" + lineSep);
            //get GraphPerspectives for all clusters, score and rank them
            //convert the ArrayList to an array of GraphPerspectives and sort it by cluster score
            //GraphPerspective[] gpClusterArray = ClusterUtil.convertClusterListToSortedNetworkList(clusters, network, alg);
            for (int i = 0; i < eproteins.length; i++) {
            	
            	Protein p = (Protein)eproteins[i];
                //GraphPerspective gpCluster = complexes[i].getGPCluster();
            	CyNetwork clusterNetwork = p.getNetwork();
                fout.write((i + 1) + "\t"); //rank
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);
                fout.write(nf.format(p.getName()) + "\t");
              
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
   public boolean exportResults(ArrayList<String > allalg, String curalg, List<Protein> eproteins, CyNetwork network) {
        if (curalg == null || eproteins == null || network == null ) {
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
            fout.write("Results ranked by "+ curalg + lineSep);
            fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
            for (int i = 0; i < eproteins.size(); i++) {         	
            	Protein p = (Protein)eproteins.get(i);
                fout.write((i + 1) + "\t"); //rank
          //      NumberFormat nf = NumberFormat.getInstance();
        //        nf.setMaximumFractionDigits(3);
            //    fout.write(nf.format(p.getName()) + "\t");
                fout.write(p.getName() + "\t");
                fout.write(curalg +": "+ (float)p.getPara(curalg) + "\t\t");
                for(String alg : allalg){
                	if(alg != curalg)
                		fout.write(alg +": "+ (float)p.getPara(alg) + "\t\t");
                }
               fout.write(lineSep);
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
 /*   public static boolean exportSimpleClusters(Algorithm alg, Cluster[] complexes, CyNetwork network, String fileName) {
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
    
 /*   public int getcurrentEvaluationId()
	{
		return currentEvaluationId;
	}
	*/
	public int getCurrentEplistId()
	{
		return currentEplistId;
	}
	
	/*public int setCurrentEplistId(int currentEplistId)
	{
		return this.currentEplistId = currentEplistId;
	}*/

	public String getProperty(String key)
	{
		return props.getProperty(key);
	}

	public void reset()
	{
		currentResultId = 1;
		currentEplistId = 1;
	//	currentEvaluationId = 1;
		currentParameters = new CurrentParameters();
		networkAlgorithms = new ArrayList<Long>();
		networkResults = new HashMap<Long, Set<Integer>> ();
		networkEplists = new HashMap<Long, Set<Integer>> ();
		createdSubNetworks = new HashMap();
		Alleprotein = new ArrayList<String>();
	//	setEvaluation(false);
	}
	
	
	public void resetResults()
	{
		currentResultId = 1;	
//		currentEvaluationId = 1;
	//	setEvaluation(false);
		currentParameters = new CurrentParameters();
		networkAlgorithms = new ArrayList<Long>();
		networkResults = new HashMap<Long, Set<Integer>> ();
		createdSubNetworks = new HashMap();
	}
	
	public void resetEplists()
	{
		
		currentEplistId = 1;	
		networkEplists = new HashMap<Long, Set<Integer>> ();
		
	}

	public synchronized void destroyUnusedNetworks(CyNetwork network, List proteins)
	{
		Map proteinNetworks = new HashMap();
		if (proteins != null)
		{
			Protein p;
			for (Iterator iterator = proteins.iterator(); iterator.hasNext(); proteinNetworks.put(p.getNetwork(), Boolean.TRUE))
				p = (Protein)iterator.next();

		}
		CyRootNetwork rootNet = rootNetworkMgr.getRootNetwork(network);
		Set snSet = (Set)createdSubNetworks.get(rootNet);
		if (snSet != null)
		{
			Set disposedSet = new HashSet();
			for (Iterator iterator1 = snSet.iterator(); iterator1.hasNext();)
			{
				CySubNetwork sn = (CySubNetwork)iterator1.next();
				if (!proteinNetworks.containsKey(sn) && !networkMgr.networkExists(sn.getSUID().longValue()))
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



	  public void addNetworkAlgorithm(long suid) {
	    this.networkAlgorithms.add(Long.valueOf(suid));
	  }
	
	
	
	public boolean containsNetworkAlgorithm(long suid)
	{
		return networkAlgorithms.contains(Long.valueOf(suid));
	}



	public void removeNetworkAlgorithm(long suid)
	{
		networkAlgorithms.remove(Long.valueOf(suid));
	}

/*	public boolean containsNetworkResult(long suid)
	{
		return networkResults.containsKey(Long.valueOf(suid));
	}
*/
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
	//	currentEvaluationId++;
	}
	
	public Set getNetworkEplists(long suid)
	{
		Set ids = (Set)networkEplists.get(Long.valueOf(suid));
		return ((Set) (ids == null ? new HashSet() : ids));
	}

	public void addNetworkEplist(long suid)
	{
		Set ids = (Set)networkEplists.get(Long.valueOf(suid));
		if (ids == null)
		{
			ids = new HashSet();
			networkEplists.put(Long.valueOf(suid), ids);
		}
		ids.add(Integer.valueOf(currentEplistId++));
	}


	public  ProteinGraph createGraph(CyNetwork net, List<CyNode> nodes)
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

		ProteinGraph graph = new ProteinGraph(root, nodes, edges, this);
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
		if (nodeStyle == null)
		{
			nodeStyle = visualStyleFactory.createVisualStyle("MCODE Cluster");
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_SIZE, Double.valueOf(40D));
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_WIDTH, Double.valueOf(40D));
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_HEIGHT, Double.valueOf(40D));
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_PAINT, java.awt.Color.RED);
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.RED);
			nodeStyle.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, Double.valueOf(0.0D));
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(5D));
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_PAINT, java.awt.Color.BLUE);
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_UNSELECTED_PAINT, java.awt.Color.BLUE);
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, java.awt.Color.BLUE);
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_SELECTED_PAINT, java.awt.Color.BLUE);
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, java.awt.Color.BLUE);
			nodeStyle.setDefaultValue(BasicVisualLexicon.EDGE_STROKE_SELECTED_PAINT, java.awt.Color.BLUE);
			VisualLexicon lexicon = applicationMgr.getCurrentRenderingEngine().getVisualLexicon();
			VisualProperty vp = lexicon.lookup(CyEdge.class, "edgeTargetArrowShape");
			if (vp != null)
			{
				Object arrowValue = vp.parseSerializableString("ARROW");
				if (arrowValue != null)
					nodeStyle.setDefaultValue(vp, arrowValue);
			}
		}
		return nodeStyle;
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
	
	public boolean removeNetworkEplist(int EplistId)
	{
		boolean removed = false;
		Long networkId = null;
		for (Iterator iterator = networkEplists.entrySet().iterator(); iterator.hasNext();)
		{
			java.util.Map.Entry entries = (java.util.Map.Entry)iterator.next();
			Set ids = (Set)entries.getValue();
			if (ids.remove(Integer.valueOf(EplistId)))
			{
				if (ids.isEmpty())
					networkId = (Long)entries.getKey();
				removed = true;
				break;
			}
		}

		if (networkId != null)
			networkEplists.remove(networkId);
		getCurrentParameters().removeResultParams(EplistId);
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
		if(elements != null){
			for (Iterator iterator = allElements.iterator(); iterator.hasNext(); network.getRow(nodeOrEdge).set("selected", Boolean.valueOf(select)))
			{
				nodeOrEdge = (CyIdentifiable)iterator.next();
				select = elements.contains(nodeOrEdge);
			}
		}
		else{
			for (Iterator iterator = allElements.iterator(); iterator.hasNext(); )
			{
				nodeOrEdge = (CyIdentifiable)iterator.next();
					network.getRow(nodeOrEdge).set("selected", false);
			}
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

    
    
    
	 // public VisualStyle getAppStyle(double maxScore) {
	  public VisualStyle getAppStyle() {
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

		  //  nodeColorCm.addPoint(Double.valueOf(maxScore), new BoundaryRangeValues(MAX_COLOR, MAX_COLOR, MAX_COLOR));

		    this.appStyle.addVisualMappingFunction(nodeColorCm);

		    return this.appStyle;
		  }

	
	  public CytoPanel getEastCytoPanel()
	  {
	    return this.swingApplication.getCytoPanel(CytoPanelName.EAST);
	  }
	  
	
	  public CytoPanel getSouthCytoPanel()
	  {
			return swingApplication.getCytoPanel(CytoPanelName.SOUTH);
	  }
	  
	  public Collection<ResultPanel> getResultPanels()
	  {
	    Collection panels = new ArrayList();
	    CytoPanel cytoPanel = getEastCytoPanel();
	    int count = cytoPanel.getCytoPanelComponentCount();

	    for (int i = 0; i < count; i++) {
	      if ((cytoPanel.getComponentAt(i) instanceof ResultPanel)) {
	        panels.add((ResultPanel)cytoPanel.getComponentAt(i));
	      }
	    }
	    return panels;
	  }
	  
	  protected Collection getEvaluationPanels()
		{
			Collection panels = new ArrayList();
			CytoPanel cytoPanel = getSouthCytoPanel();
			int count = cytoPanel.getCytoPanelComponentCount();
			for (int i = 0; i < count; i++)
				if (cytoPanel.getComponentAt(i) instanceof EvaluationPanel)
					panels.add((EvaluationPanel)cytoPanel.getComponentAt(i));

			return panels;
		}
		
	  
	  public Collection<EpListPanel> getEpListPanels()
		{
			Collection panels = new ArrayList();
			CytoPanel cytoPanel = getEastCytoPanel();
			int count = cytoPanel.getCytoPanelComponentCount();
			for (int i = 0; i < count; i++)
				if (cytoPanel.getComponentAt(i) instanceof EpListPanel)
					panels.add((EpListPanel)cytoPanel.getComponentAt(i));

			return panels;
		}

	  public ResultPanel getResultPanel(int resultId) {
	    for (ResultPanel panel : getResultPanels()) {
	      if (panel.getResultId() == resultId) return panel;
	    }

	    return null;
	  }
	  
	  public EpListPanel getEpListPanel(int epListId) {
		    for (EpListPanel panel : getEpListPanels()) {
		      if (panel.getEplistId() == epListId) 
		    	  return panel;
		    }

		    return null;
		  }
	  public EvaluationPanel getEvaluationPanel(int evaluationId)
		{
			for (Iterator iterator = getEvaluationPanels().iterator(); iterator.hasNext();)
			{
				EvaluationPanel panel = (EvaluationPanel)iterator.next();
				if (panel.getEvaluationId() == evaluationId)
					return panel;
			}

			return null;
		}
	  
	  public ArrayList detectparalleledges(CyNetwork n){
		 List<CyEdge> edges = n.getEdgeList();
		 Map<Long, Long> emap = new HashMap<Long, Long>(); 
		 ArrayList e = new ArrayList();
		 
		 Iterator i = edges.iterator();
		 while(i.hasNext()){
			 CyEdge temp = (CyEdge) i.next();
			 Long l = temp.getSource().getSUID() > temp.getTarget().getSUID() ? temp.getSource().getSUID() : temp.getTarget().getSUID();
			 Long s = temp.getSource().getSUID() > temp.getTarget().getSUID() ? temp.getTarget().getSUID() : temp.getSource().getSUID();
		//	 System.out.println(l+"    "+s+"  rrrrrrrrrRRRR");
			 
			 
			 if((emap.containsKey(l) && emap.get(l).longValue() == s) || (l == s)){
				 e.add(temp);
				 if(l == s){
					 e.add(temp);
					 e.add(n.getNode(l));
				 }				
				 else{
					 e.add(temp);
					 e.add(n.getNode(l));
					 e.add(n.getNode(s));
				 }
				 return e;
			 }
			 else
				 emap.put(l, s); 	
		 }
		 return e;
	  }
	  
	  
	  public void deleteparalleledges(CyNetwork n,  ArrayList a){
		  Collection allElements = new ArrayList(n.getNodeList());
		
			CyIdentifiable nodeOrEdge;
			boolean select;
			if(a != null){
				for (Iterator iterator = allElements.iterator(); iterator.hasNext();)
				{
					nodeOrEdge = (CyIdentifiable)iterator.next();
					if(a.contains(nodeOrEdge)){
						a.remove(nodeOrEdge);
					}
				}
				n.removeEdges(a);
			}
	  
			
			eventHelper.flushPayloadEvents();
			Collection netViews = networkViewMgr.getNetworkViews(n);
			CyNetworkView view;
			for (Iterator iterator1 = netViews.iterator(); iterator1.hasNext(); view.updateView())
				view = (CyNetworkView)iterator1.next();

			swingApplication.getJFrame().repaint();
		  
	  }
	  
	  public void getPlist(CyNetwork inputNetwork, ArrayList<Protein> plist){
		
		  List<CyNode> nodes = inputNetwork.getNodeList();
		  Iterator iterator = nodes.iterator();
		  while(iterator.hasNext()){
			  CyNode n = (CyNode) iterator.next();
			  Protein p = new Protein(n, inputNetwork);
			  plist.add(p);
			}
	  }
	  
	  public int isweight(CyNetwork inputNetwork){
		  int param = 0;
		  List<CyNode> nodes = inputNetwork.getNodeList();
		  if(inputNetwork.getRow(nodes.get(0)).get("weight", String.class) != null)
				param = 1;
		  return param;
	  }
    
	  public void sortVertex(List<Protein> vertex, String alg) {
		  
		  
		  if(alg.equals(ParameterSet.BC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getBC() - o2.getBC() > 0) {
							return -1;
						} else if (o1.getBC() - o2.getBC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  
		  else if(alg.equals(ParameterSet.CC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getCC() - o2.getCC() > 0) {
							return -1;
						} else if (o1.getCC() - o2.getCC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.DC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getDC() - o2.getDC() > 0) {
							return -1;
						} else if (o1.getDC() - o2.getDC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.EC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getEC() - o2.getEC() > 0) {
							return -1;
						} else if (o1.getEC() - o2.getEC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.LAC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getLAC() - o2.getLAC() > 0) {
							return -1;
						} else if (o1.getLAC() - o2.getLAC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.NC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getNC() - o2.getNC() > 0) {
							return -1;
						} else if (o1.getNC() - o2.getNC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.SC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getSC() - o2.getSC() > 0) {
							return -1;
						} else if (o1.getSC() - o2.getSC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  else if(alg.equals(ParameterSet.IC)){
			  Collections.sort(vertex, new Comparator<Protein>() {				
					@Override
					public int compare(Protein o1, Protein o2) {
						// TODO Auto-generated method stub
						if (o1.getIC() - o2.getIC() > 0) {
							return -1;
						} else if (o1.getIC() - o2.getIC() == 0) {
							return 0;
						} else {
							return 1;
						}
					}
				});
		  }
		  
			
		}



	/*public boolean isEvaluation() {
		return isEvaluation;
	}



	public void setEvaluation(boolean isEvaluation) {
		this.isEvaluation = isEvaluation;
	}
*/
	  public synchronized List<Protein> copyList(List<Protein> ps){
			List<Protein> t = new ArrayList<Protein>();
			for(Protein p : ps)
				t.add(p);
			return t;
		}
	 

	public ArrayList<String> getAlleprotein() {
		return Alleprotein;
	}



	public void setAlleprotein(ArrayList<String> alleprotein) {
		Alleprotein = alleprotein;
	}
   
	
}
