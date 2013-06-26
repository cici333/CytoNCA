package org.cytoscape.CytoCluster.internal;
/*
import org.cytoscape.*;
import org.cytoscape.view.*;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.cytopanels.*;
import org.cytoscape.actions.GinyUtils;
import org.cytoscape.data.CyAttributes;
import org.cytoscape.util.CyFileFilter;
import org.cytoscape.util.FileUtil;
import ding.view.DGraphView;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.NodeView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.myappViz.internal.CollapsiblePanel;
import org.cytoscape.myappViz.internal.MyTipTool;
import org.cytoscape.myappViz.internal.ParameterSet;
import BiNGO.BiNGO.SettingsPanel;
import org.cytoscape.myappViz.internal.algorithm.*;

*//**
 * Show a Table Browser for the results of clustering. This class sets up the GUI.
 *//*
public class ResultPanel extends JPanel {
	protected String resultTitle;
	protected Algorithm alg;
	protected Cluster[] complexes;
	protected JTable table;
	protected ResultPanel.ClusterBrowserTableModel complexBrowser;
	//table size parameters
	protected final int picSize = 80;
	protected final int defaultRowHeight = picSize + 8;
	protected int preferredTableWidth = 0; // incremented below
	protected int selectedCluster = 0; //track selected Cluster (that is Row in the table)
	protected CySwingApplication desktopApp;
	private CyApplicationManager cyApplicationManagerServiceRef;

	private final int resultId;
	
	CyNetwork network;  //original input, used in the table row selection listener
	CyNetworkView networkView;	 //Keep a record of this view, if it exists
	CollapsiblePanel explorePanel;
	JPanel browserPanel;
	JPanel[] exploreContents;
	ParameterSet currentParamsCopy;
	Image[] imageList;
	//Keep track of selected attribute for enumeration 
	//so it stays selected for all cluster explorations
	int enumerationSelection = 0; 

	//GraphDrawer drawer;
	Loader loader;

	*//**
	 * Constructor for the Results Panel which displays the complexes in a browser table and
	 * an explore panels for each cluster.
	 *
	 * @param complexes complexes found by the AnalyzeTask
	 * @param alg A reference to the algorithm for this network
	 * @param network Network were these complexes were found
	 * @param imageList A list of images of the found complex
	 * @param resultTitle Title of this result as determined by AnalyzeAction
	 *//*
	public ResultPanel(Cluster[] complexes, Algorithm alg, CyNetwork network, Image[] imageList, String resultTitle,
			int resultId
			) {
		setLayout(new BorderLayout());
		this.setEnabled(false);
		this.alg = alg;
		this.resultTitle = resultTitle;
		this.complexes = complexes;
		this.network = network;		
		this.imageList=imageList;
		networkView = cyApplicationManagerServiceRef.getCurrentNetworkView();//the view may not exist
		currentParamsCopy = ParameterSet.getInstance().getResultParams(resultTitle).copy();
		
		browserPanel = createBrowserPanel(this.imageList);
		JPanel bottomPanel = createBottomPanel();
		add(browserPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		//drawer = new GraphDrawer();
		loader = new Loader(table, picSize, picSize);
		this.setSize(this.getMinimumSize());
		this.resultId=resultId;
		
	}

	*//**
	 * Creates a panel that contains the browser table with a scroll bar.
	 *
	 * @param imageList images of cluster graphs
	 * @return panel
	 *//*
	private JPanel createBrowserPanel(Image imageList[]) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		StringBuffer sb=new StringBuffer("Complex Browser( ");
		sb.append(imageList.length);
		sb.append(" in total )");
		panel.setBorder(BorderFactory.createTitledBorder(sb.toString())); 
		
		complexBrowser=new ResultPanel.ClusterBrowserTableModel(imageList);
		table = new JTable(complexBrowser);
		table.addMouseListener(new PopClickListener());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(StringBuffer.class, new ResultPanel.JTextAreaRenderer(defaultRowHeight));
		table.setIntercellSpacing(new Dimension(10, 8));   //gives a little vertical room between complexes
		table.setFocusable(false);  //removes an outline that appears when the user clicks on the images

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ResultPanel.TableRowSelectionAction());
		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.getViewport().setBackground(Color.GRAY); 
		
		//the sortPanel
		JPanel sortPanel=new JPanel();
		sortPanel.setLayout(new FlowLayout());
		//the radio buttons
		boolean set3=currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE);
		boolean set2=(currentParamsCopy.getAlgorithm().equals(ParameterSet.FAGEC)&& 
				currentParamsCopy.isWeak());
		boolean set1=((!set2)&&(!set3));
		JRadioButton way1=new JRadioButton("Size",set1);
		JRadioButton way2 = new JRadioButton("Modularity", set2);
		JRadioButton way3 = new JRadioButton("Score", set3);
		way1.setActionCommand("size");
		way2.setActionCommand("modu");
		way3.setActionCommand("score");
		way1.addActionListener(new SortWayAction(table, complexBrowser));
		way2.addActionListener(new SortWayAction(table, complexBrowser));
		way3.addActionListener(new SortWayAction(table, complexBrowser));
		ButtonGroup ways = new ButtonGroup();
		ways.add(way1);
		ways.add(way2);
		if(currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE))
			ways.add(way3);
		//the label
		JLabel label=new JLabel("Sort Complexes by (descend):");
		//add components to the sortPanel
		sortPanel.add(label);
		sortPanel.add(way1);
		sortPanel.add(way2);
		if(currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE))
			sortPanel.add(way3);
		sortPanel.setToolTipText("Select a way to sort the complexes");
		
		panel.add(sortPanel,BorderLayout.NORTH);
		panel.add(tableScrollPane,BorderLayout.CENTER);
		panel.setToolTipText("information of the identified complexes");
		return panel;
	}
	*//**
	 * Creates a panel containing the explore collapsable panel and result set specific buttons
	 *//*
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		explorePanel = new CollapsiblePanel("Current");
		explorePanel.setCollapsed(false);
		explorePanel.setVisible(false);

		JPanel buttonPanel = new JPanel();
		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ResultPanel.ExportAction());
		exportButton.setToolTipText("Export the resulting complexes into a file");
		JButton closeButton = new JButton("Discard");
		closeButton.setToolTipText("Close this result panel");
		closeButton.addActionListener(new ResultPanel.DiscardAction(this));
		JButton unclusterButton = new JButton("All Clustered Nodes");
		unclusterButton.setToolTipText("Select all clustered nodes");
		unclusterButton.addActionListener(new ResultPanel.UnclusterAction(this));
		
		buttonPanel.add(exportButton);
		buttonPanel.add(closeButton);
		buttonPanel.add(unclusterButton);

		panel.add(explorePanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		return panel;
	}

	private class SortWayAction extends AbstractAction {
		JTable browserTable;
		ResultPanel.ClusterBrowserTableModel modelBrowser;
		
		SortWayAction(JTable browserTable,ResultPanel.ClusterBrowserTableModel modelBrowser) {
			this.browserTable = browserTable;
			this.modelBrowser = modelBrowser;
		}
		public void actionPerformed(ActionEvent e) {
			String way = e.getActionCommand();
			switchPlace(way);
			if(imageList!=null){
				//browserPanel=createBrowserPanel(imageList);
				modelBrowser.listIt();
				modelBrowser.fireTableDataChanged();
				browserPanel.updateUI();
				((JPanel)browserPanel.getParent()).updateUI();
			}else System.err.println("list null");
		}
	}
	private void switchPlace(String field){
		int max;
		Image image;
		Cluster cluster;
		for(int i=0;i<imageList.length-1;i++){
			max=i;
			for(int j=i+1;j<imageList.length;j++){
				if(field.equals("size")){
					if(complexes[j].getALNodes().size()>complexes[max].getALNodes().size())
						max=j;
				}else if(field.equals("modu")){
					if(complexes[j].getModularity()>complexes[max].getModularity())
						max=j;
				}else if(field.equals("score")){
					if(complexes[j].getClusterScore()>complexes[max].getClusterScore())
						max=j;
				}else{
					System.err.println("In switchPlace:Erro Parameter");
					return;
				}
			}
			//switch
			image=imageList[i];
			imageList[i]=imageList[max];
			imageList[max]=image;
			cluster=complexes[i];
			complexes[i]=complexes[max];
			complexes[max]=cluster;
		}
	}
	*//**
	 * This method creates a JPanel containing a node score cutoff slider 
	 * and a node attribute enumeration viewer
	 *
	 * @param selectedRow The cluster that is selected in the cluster browser
	 * @return panel A JPanel with the contents of the explore panel, get's added to the explore collapsable panel's content pane
	 *//*
	private JPanel createExploreContent(int selectedRow) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		//Node attributes Panel
		JPanel nodeAttributesPanel = new JPanel(new BorderLayout());
		nodeAttributesPanel.setBorder(BorderFactory.createTitledBorder("Node Attribute"));	
		
//		network.getRow(network).get("name",String.class));
		
		String[] availableAttributes = Cytoscape.getNodeAttributes().getAttributeNames();
		Arrays.sort(availableAttributes, String.CASE_INSENSITIVE_ORDER);
		
		
		String[] attributesList = new String[availableAttributes.length];
		System.arraycopy(availableAttributes, 0, attributesList, 0, availableAttributes.length);
		JComboBox nodeAttributes = new JComboBox(attributesList);
		nodeAttributes.setToolTipText("Choose the attribute name you want to look into");
		//Create a table listing the node attributes and their enumerations
		ResultPanel.EnumeratorTableModel modelEnumerator;
		modelEnumerator = new ResultPanel.EnumeratorTableModel(new HashMap());		
		JTable enumerationsTable = new JTable(modelEnumerator);
		JScrollPane tableScrollPane = new JScrollPane(enumerationsTable);
		tableScrollPane.getViewport().setBackground(Color.WHITE);
		enumerationsTable.setPreferredScrollableViewportSize(new Dimension(100, picSize));
		enumerationsTable.setGridColor(Color.LIGHT_GRAY);
		enumerationsTable.setFont(new Font(enumerationsTable.getFont().getFontName(), Font.PLAIN, 11));
		enumerationsTable.setDefaultRenderer(StringBuffer.class, new ResultPanel.JTextAreaRenderer(0));
		enumerationsTable.setFocusable(false);
		//Create a combo box that lists all the available node attributes for enumeration
		nodeAttributes.addActionListener(new ResultPanel.enumerateAction(enumerationsTable, modelEnumerator, selectedRow));
		nodeAttributesPanel.add(nodeAttributes, BorderLayout.NORTH);
		nodeAttributesPanel.add(tableScrollPane, BorderLayout.SOUTH);
		
		//The sizeSliderPanle
		JPanel sizePanel = new JPanel(new BorderLayout());
		sizePanel.setBorder(BorderFactory.createTitledBorder("Size Slider"));
		//A slider to manipulate node score cutoff 
		//TODO: note here,we need to modify the codes, or we can just remove this panel simpley
		JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 
				(int) (currentParamsCopy.getNodeScoreCutoff() * 1000)) {
			public JToolTip createToolTip() {
				return new MyTipTool();
			}
		};
		sizeSlider.addChangeListener(new ResultPanel.SizeAction(selectedRow, nodeAttributes));
		sizeSlider.setMajorTickSpacing(200);
		sizeSlider.setMinorTickSpacing(50);
		sizeSlider.setPaintTicks(true);
		sizeSlider.setPaintLabels(true);
		Hashtable labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("Min"));
		labelTable.put(new Integer(1000), new JLabel("Max"));
		//TODO: note here,we need to modify the codes, or we can just remove this panel simpley
		//TODO: there may be some other situation like this ,we need to find out them
		//Make a special label for the initial position
		labelTable.put(new Integer((int) (currentParamsCopy.getNodeScoreCutoff() * 1000)),
				new JLabel("^") );
		sizeSlider.setLabelTable(labelTable);
		sizeSlider.setFont(new Font("Arial", Font.PLAIN, 8));
		String sizeTip = "Move the slider to change the size of the complex";
		sizeSlider.setToolTipText(sizeTip);
		sizePanel.add(sizeSlider, BorderLayout.NORTH);

		//JPanel bottomExplorePanel = createBottomExplorePanel(selectedRow);
		JButton createChildButton = new JButton("Create SubNetwork");
		createChildButton.addActionListener(new ResultPanel.CreateChildAction(this, selectedRow));
		createChildButton.setToolTipText("create a network for this complex");JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(createChildButton,BorderLayout.SOUTH);
		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());
		if(alg.getParams().getAlgorithm().equals(ParameterSet.MCODE))
			bottom.add(sizePanel,BorderLayout.CENTER);
			bottom.add(buttonPanel,BorderLayout.EAST);
		}
		else
			bottom.add(buttonPanel);
		panel.add(nodeAttributesPanel);
		panel.add(bottom);
		return panel;
	}

	*//**
	 * Handles the create child network press in the cluster exploration panel
	 *//*
	private class CreateChildAction extends AbstractAction {
		int selectedRow;
		ResultPanel trigger;
		CreateChildAction (ResultPanel trigger, int selectedRow) {
			this.selectedRow = selectedRow;
			this.trigger = trigger;
		}
		public void actionPerformed(ActionEvent actionEvent) {
			final Cluster cluster = complexes[selectedRow];
			final GraphPerspective gpCluster = cluster.getGPCluster();
			final String title = cluster.getClusterName() + "_" + trigger.getResultTitle()+".sif";
			//create the child network and view
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					CyNetwork newNetwork = Cytoscape.createNetwork(gpCluster.getNodeIndicesArray(), gpCluster.getEdgeIndicesArray(), title, network);
					DCyNetworkView view = (DCyNetworkView) Cytoscape.createNetworkView(newNetwork);
					//layout new cluster and fit it to window
					//randomize node positions before layout so that they don't all layout in a line
					//(so they don't fall into a local minimum for the SpringEmbedder)
					//If the SpringEmbedder implementation changes, this code may need to be removed
					NodeView nv;
					boolean layoutNecessary = false;
					for (Iterator in = view.getNodeViewsIterator(); in.hasNext();) {
						nv = (NodeView) in.next();
						if (cluster.getDGView() != null && cluster.getDGView().getNodeView(nv.getNode().getRootGraphIndex()) != null) {
							//If it does, then we take the layout position that was already generated for it
							nv.setXPosition(cluster.getDGView().getNodeView(nv.getNode().getRootGraphIndex()).getXPosition());
							nv.setYPosition(cluster.getDGView().getNodeView(nv.getNode().getRootGraphIndex()).getYPosition());
						} else {
							//this will likely never occur
							nv.setXPosition(view.getCanvas().getWidth() * Math.random());
							//height is small for many default drawn graphs, thus +100
							nv.setYPosition((view.getCanvas().getHeight() + 100) * Math.random());
							layoutNecessary = true;
						}
					}
					if (layoutNecessary) {
						ClusterLayout layout = new ClusterLayout(view);
						layout.doLayout(0, 0, 0, null);
					}
					view.fitContent();
					return null;
				}
			};
			worker.start();
		}
	}

	*//**
	 * Generates a string buffer with the cluster's details
	 * 
	 * @param cluster The cluster
	 * @return details String buffer containing the details
	 *//*
	private StringBuffer getClusterDetails(Cluster cluster) {
		//TODO: codes here need to be modified
		StringBuffer details = new StringBuffer();
		details.append("NO.: ");
		details.append((new Integer(cluster.getRank() + 1)).toString());
		details.append("\n");
		details.append("Nodes: ");
		details.append(cluster.getGPCluster().getNodeCount());
		details.append("\n");
		details.append("Edges: ");
		details.append(cluster.getGPCluster().getEdgeCount());
		details.append("\n");
		if(currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE)){
			details.append("Score: ");
			NumberFormat nf1 = NumberFormat.getInstance();
			nf1.setMaximumFractionDigits(3);
			details.append(nf1.format(cluster.getClusterScore()));
			details.append("\n");
		}
		details.append("Modularity: ");
		NumberFormat nf2 = NumberFormat.getInstance();
		nf2.setMaximumFractionDigits(3);
		details.append(nf2.format(cluster.getModularity()));
		details.append("\n");
		details.append("InDeg: ");
		details.append(cluster.getInDegree());
		details.append(" OutDeg: ");
		int outDegree=cluster.getTotalDegree()-2*cluster.getInDegree();
		details.append(outDegree);
		return details;
	}

	*//**
	 * Handles the data to be displayed in the cluster browser table
	 * methods need to be implements
	 *   public int getRowCount();
	 *   public int getColumnCount();
	 *   public Object getValueAt(int row, int column);
	 *//*
	private class ClusterBrowserTableModel extends AbstractTableModel {
		//Create column headings
		String[] columnNames = {"Snapshot", "Details"};
		Object[][] data;	//the actual table data
		public ClusterBrowserTableModel(Image imageList[]) {
			listIt();
		}
		public void listIt(){
			exploreContents = new JPanel[complexes.length];
			data = new Object[complexes.length][columnNames.length];
			//create an image for each cluster - make it a nice layout of the cluster
			for (int i = 0; i < complexes.length; i++) {
				complexes[i].setRank(i);
				Image image;
				if (imageList != null) {
					image = imageList[i];
				} else {
					image = ClusterUtil.convertClusterToImage(null, complexes[i], picSize, picSize, null, true);
				}
				data[i][0] = new ImageIcon(image);
				StringBuffer details = new StringBuffer(getClusterDetails(complexes[i]));
				data[i][1] = new StringBuffer(details);
			}			
		}
		public String getColumnName(int col) {
			return columnNames[col];
		}
		public int getColumnCount() {
			return columnNames.length;
		}
		public int getRowCount() {
			return data.length;
		}
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
		public void setValueAt(Object object, int row, int col) {
			data[row][col] = object;
			fireTableCellUpdated(row, col);
		}
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}


	}
	*//**
	 * Handles the data to be displayed in the node attribute enumeration table
	 *//*
	private class EnumeratorTableModel extends AbstractTableModel {
		String[] columnNames = {"Attribute Name", "Occurrence Times"};
		Object[][] data = new Object[0][columnNames.length]; 
		public EnumeratorTableModel(HashMap enumerations) {
			listIt(enumerations);
		}
		public void listIt(HashMap enumerations){
			ArrayList enumerationsSorted = sortMap(enumerations);
			Object[][] newData = new Object[enumerationsSorted.size()][columnNames.length];
			int c = enumerationsSorted.size()-1;
			for (Iterator i = enumerationsSorted.iterator(); i.hasNext();) {
				Map.Entry mp = (Map.Entry) i.next();
				newData[c][0] = new StringBuffer(mp.getKey().toString());
				newData[c][1] = new String(mp.getValue().toString());
				c--;
			}
			if (getRowCount() == newData.length) {
				data = new Object[newData.length][columnNames.length];
				System.arraycopy(newData, 0, data, 0, data.length);
				fireTableRowsUpdated(0, getRowCount());
			} else {
				data = new Object[newData.length][columnNames.length];
				System.arraycopy(newData, 0, data, 0, data.length);
				fireTableDataChanged();
			}
		}
		public String getColumnName(int col) {
			return columnNames[col];
		}
		public int getRowCount() {
			return data.length;
		}
		public int getColumnCount() {
			return columnNames.length;
		}
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
		public void setValueAt(Object object, int row, int col) {
			data[row][col] = object;
			fireTableCellUpdated(row, col);
		}		
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

	*//**
	* This method uses Arrays.sort for sorting a Map by the entries' values
	*
	* @param map Has values mapped to keys
	* @return outputList of Map.Entries
	*//*
	private ArrayList sortMap(Map map) {
		ArrayList outputList = null;
		int count = 0;
		Set set = null;
		Map.Entry[] entries = null;

		set = (Set) map.entrySet();
		Iterator iterator = set.iterator();
		entries = new Map.Entry[set.size()];
		while(iterator.hasNext()) {
			entries[count++] = (Map.Entry) iterator.next();
		}

		// Sort the entries with own comparator for the values:
		Arrays.sort(entries, new Comparator() {
			public int compareTo(Object o1, Object o2) {
				Map.Entry le = (Map.Entry)o1;
				Map.Entry re = (Map.Entry)o2;
				return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
			}

			public int compare(Object o1, Object o2) {
				Map.Entry le = (Map.Entry)o1;
				Map.Entry re = (Map.Entry)o2;
				return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
			}
		});
		outputList = new ArrayList();
		for(int i = 0; i < entries.length; i++) {
			outputList.add(entries[i]);
		}
		return outputList;
	}

	*//**
	 * Handles the selection of all available node attributes for the enumeration within the cluster
	 *//*
	private class enumerateAction extends AbstractAction {
		JTable enumerationsTable;
		int selectedRow;
		ResultPanel.EnumeratorTableModel modelEnumerator;

		enumerateAction(JTable enumerationsTable,ResultPanel.EnumeratorTableModel modelEnumerator, int selectedRow) {
			this.selectedRow = selectedRow;
			this.enumerationsTable = enumerationsTable;
			this.modelEnumerator = modelEnumerator;
		}

		public void actionPerformed(ActionEvent e) {
			HashMap attributeEnumerations = new HashMap(); //the key is the attribute value and the value is the number of times that value appears in the cluster
			//First we want to see which attribute was selected in the combo box
			String attributeName = (String) ((JComboBox) e.getSource()).getSelectedItem();
			int selectionIndex = (int) ((JComboBox) e.getSource()).getSelectedIndex();
			//If its the generic 'please select' option then we don't do any enumeration
			if (!attributeName.equals("Please Select")) {
				//otherwise, we want to get the selected attribute's value for each node in the selected cluster
				for (Iterator i = complexes[selectedRow].getGPCluster().nodesIterator(); i.hasNext();) {
					Node node = (Node) i.next();
					//The attribute value will be stored as a string no matter what it is but we need an array list
					//because some attributes are maps or lists of any size
					ArrayList attributeValue = new ArrayList();
					//Every type of attribute has its own get method so we have to see which one to use
					//When we find the type, we get its value(s)
					if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_STRING) {
						attributeValue.add(Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), attributeName));
					} else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_FLOATING) {
						attributeValue.add(Cytoscape.getNodeAttributes().getDoubleAttribute(node.getIdentifier(), attributeName));
					} else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_INTEGER) {
						attributeValue.add(Cytoscape.getNodeAttributes().getIntegerAttribute(node.getIdentifier(), attributeName));
					} else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_BOOLEAN) {
						attributeValue.add(Cytoscape.getNodeAttributes().getBooleanAttribute(node.getIdentifier(), attributeName));
					} else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
						List valueList = Cytoscape.getNodeAttributes().getListAttribute(node.getIdentifier(), attributeName);
						for (Iterator vli = valueList.iterator(); vli.hasNext();) {
							attributeValue.add(vli.next());
						}
					} else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_SIMPLE_MAP) {
						Map valueMap = Cytoscape.getNodeAttributes().getMapAttribute(node.getIdentifier(), attributeName);
						for (Iterator vmki = valueMap.keySet().iterator(); vmki.hasNext();) {
							String key = (String) vmki.next();
							Object value = valueMap.get(key);
							attributeValue.add(new String(key + " -> " + value));
						}
					}
					//Next we must make a non-repeating list with the attribute values and enumerate the repetitions
					for (Iterator avi = attributeValue.iterator(); avi.hasNext();) {
						Object aviElement = avi.next();
						if (aviElement != null) {
							String value = aviElement.toString();

							if (!attributeEnumerations.containsKey(value)) {
								//If the attribute value appears for the first time, we give it an enumeration of 1 and add it to the enumerations
								attributeEnumerations.put(value, new Integer(1));
							} else {
								//If it already appeared before, we want to add to the enumeration of the value
								Integer enumeration = (Integer) attributeEnumerations.get(value);
								enumeration = new Integer(enumeration.intValue()+1);
								attributeEnumerations.put(value, enumeration);
							}
						}
					}
				}
			}
			modelEnumerator.listIt(attributeEnumerations);
			//Finally we make sure that the selection is stored so that all the cluster explorations are looking at the already selected attribute
			enumerationSelection = selectionIndex;
		}
	}


	*//**
	 * Handles the select unclustered nodes process for this results panel
	 *//*
	private class UnclusterAction extends AbstractAction {
		ResultPanel trigger;
		UnclusterAction(ResultPanel trigger) {
			this.trigger = trigger;
		}
		public void actionPerformed(ActionEvent e) {
			int length=0;
			for(int i=0; i < complexes.length; i++){
				length = length + complexes[i].getGPCluster().getNodeIndicesArray().length;
			}
			int[] clusteredNodes;
			clusteredNodes = new int[length];
			int index=0;
			for(int i=0; i < complexes.length; i++){
				for(int j=0;j <= complexes[i].getGPCluster().getNodeIndicesArray().length -1; j++){
					clusteredNodes[index++] = complexes[i].getGPCluster().getNodeIndicesArray()[j];
				}
			}

			selectCluster(network.createGraphPerspective(clusteredNodes));
		}
	}
	*//**
	 * Handles the close press for this results panel
	 *//*
	private class DiscardAction extends AbstractAction {
		ResultPanel trigger;
		DiscardAction(ResultPanel trigger) {
			this.trigger = trigger;
		}
		public void actionPerformed(ActionEvent e) {
			CytoscapeDesktop desktop = Cytoscape.getDesktop();
			CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
			String message = "Confirm to close the" + resultTitle + "?";
			int result = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new Object[]{message}, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (result == JOptionPane.YES_OPTION) {
				cytoPanel.remove(trigger);
				ParameterSet.removeResultParams(trigger.getResultTitle());
			}
			if (cytoPanel.getCytoPanelComponentCount() == 0) {
				cytoPanel.setState(CytoPanelState.HIDE);
			}
		}
	}

	*//**
	 * Handles the Export press for this panel (export results to a text file)
	 *//*
	private class ExportAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			File file = FileUtil.getFile("Save", FileUtil.SAVE, new CyFileFilter[]{});
			if (file != null) {
				String fileName = file.getAbsolutePath();
				String message = "Save the complete information \n" +
						"of the resulting complexes?"+
						"(Y/Complete锟斤拷N/Basic)";
				int result = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new Object[]{message}, "Export Mode", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (result == JOptionPane.YES_OPTION) {
					ClusterUtil.exportResults(alg, complexes, network, fileName);
					JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "Completed!"); 
				}
				else if(result==JOptionPane.NO_OPTION){
					ClusterUtil.exportSimpleClusters(alg, complexes, network, fileName);
					JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "Succeed!"); 
				}
			}
		}
	}

	*//**
	 * Handler to select nodes in graph when a row is selected
	 *//*
	private class TableRowSelectionAction implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			final GraphPerspective gpCluster;
			if (!lsm.isSelectionEmpty()) {
				final int selectedRow = lsm.getMinSelectionIndex();
				selectedCluster = selectedRow;
				gpCluster = complexes[selectedRow].getGPCluster();
				selectCluster(gpCluster);
				if (exploreContents[selectedRow] == null) {
					exploreContents[selectedRow] = createExploreContent(selectedRow);
				}
				if (explorePanel.isVisible()) {
					explorePanel.getContentPane().remove(0);
				}
				explorePanel.getContentPane().add(exploreContents[selectedRow], BorderLayout.SOUTH);
				//and set the explore panel to visible so that it can be seen (this only happens once
				//after the first time the user selects a cluster
				if (!explorePanel.isVisible()){
					explorePanel.setVisible(true);
				}
				//Finally the explore panel must be redrawn upon the selection event to display the
				//new content with the name of the cluster, if it exists
				String title = "Current: ";
				if (complexes[selectedRow].getClusterName() != null) {
					title = title + complexes[selectedRow].getClusterName();
				} else {
					title = title + "Complex " + (selectedRow + 1);
				}
				explorePanel.setTitleComponentText(title);
				explorePanel.updateUI();

				//In order for the enumeration to be conducted for this cluster on the same 
				//attribute that might already have been selected
				//we get a reference to the combo box within the explore content
				JComboBox nodeAttributesComboBox = (JComboBox) ((JPanel) exploreContents[selectedRow].getComponent(0)).getComponent(0);
				//and fire the enumeration action
				nodeAttributesComboBox.setSelectedIndex(enumerationSelection);
				table.scrollRectToVisible(table.getCellRect(selectedRow, 0, true));
			}
		}
	}

	*//**
	 * Selects a cluster in the view that is selected by the user in the browser table
	 *
	 * @param gpCluster Cluster to be selected
	 *//*
	public void selectCluster(GraphPerspective gpCluster) {
		if (gpCluster != null) {
			if (networkView != null) {
				GinyUtils.deselectAllNodes(networkView);
				network.setSelectedNodeState(gpCluster.nodesList(), true);
				//We want the focus to switch to the appropriate network view but only if the cytopanel is docked
				//If it is not docked then it is best if the focus stays on the panel
				if(Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).getState() == CytoPanelState.DOCK) {
					Cytoscape.getDesktop().setFocus(networkView.getIdentifier());
				}
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
						"There is no network to select nodes.", "", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (networkView != null) {
			GinyUtils.deselectAllNodes(networkView);
		}
	}

	*//**
	 * A text area renderer that creates a line wrapped, non-editable text area
	 *//*
	private class JTextAreaRenderer extends JTextArea implements TableCellRenderer {
		int minHeight;
		public JTextAreaRenderer(int minHeight) {
			this.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 11));
			this.minHeight = minHeight;
		}

		*//**
		 * Used to render a table cell.  Handles selection color and cell heigh and width.
		 * Note: Be careful changing this code as there could easily be infinite loops created
		 * when calculating preferred cell size as the user changes the dialog box size.
		 *
		 * @param table	  Parent table of cell
		 * @param value	  Value of cell
		 * @param isSelected True if cell is selected
		 * @param hasFocus   True if cell has focus
		 * @param row		The row of this cell
		 * @param column	 The column of this cell
		 * @return The cell to render by the calling code
		 *//*
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
													   boolean hasFocus, int row, int column) {
			StringBuffer sb = (StringBuffer) value;
			this.setText(sb.toString());
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
				this.setForeground(table.getSelectionForeground());
			} else {
				this.setBackground(table.getBackground());
				this.setForeground(table.getForeground());
			}
			//row height calculations
			int currentRowHeight = table.getRowHeight(row);
			int rowMargin = table.getRowMargin();
			this.setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight - (2 * rowMargin));
			int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();
			//JTextArea can grow and shrink here
			if (currentRowHeight != Math.max(textAreaPreferredHeight + (2 * rowMargin) , minHeight + (2 * rowMargin))) {
				table.setRowHeight(row, Math.max(textAreaPreferredHeight + (2 * rowMargin), minHeight + (2 * rowMargin)));
			}
			return this;
		}
	}
	public String getResultTitle() {
		return resultTitle;
	}
	public void setResultTitle(String title) {
		resultTitle = title;
	}
	public JTable getClusterBrowserTable() {
		return table;
	}

	*//**
	 * Handles the dynamic cluster size manipulation via the JSlider
	 *//*
	private class SizeAction implements ChangeListener {
		private int selectedRow;
		public boolean loaderSet = false;
		private JComboBox nodeAttributesComboBox;
		private GraphDrawer drawer;
		private boolean drawPlaceHolder;
		*//**
		 * @param selectedRow The selected complex
		 * @param nodeAttributesComboBox Reference to the attribute enumeration picker
		 *//*
		SizeAction(int selectedRow, JComboBox nodeAttributesComboBox){
			this.selectedRow = selectedRow;
			this.nodeAttributesComboBox = nodeAttributesComboBox;
			drawer = new GraphDrawer();
			loaderSet = false;
		}
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			//TODO:modify code here
			double nodeScoreCutoff = (((double)source.getValue())/1000);
			ArrayList oldNodes = complexes[selectedRow].getALNodes();
			Cluster cluster = alg.exploreCluster(complexes[selectedRow], nodeScoreCutoff, network, resultTitle);
			ArrayList newNodes = cluster.getALNodes();
			drawPlaceHolder = newNodes.size() > 300;
			//compare the old and new
			if (!newNodes.equals(oldNodes)) {
				drawer.interruptDrawing();
				complexes[selectedRow] = cluster;
				//Update the details
				StringBuffer details = getClusterDetails(cluster);
				table.setValueAt(details, selectedRow, 1);
				//Fire the enumeration action  ????
				nodeAttributesComboBox.setSelectedIndex(nodeAttributesComboBox.getSelectedIndex());
				if (!loaderSet && !drawPlaceHolder) {
					loader.setLoader(selectedRow, table);
					loaderSet = true;
				}
				//When expanding, new nodes need random position and thus must go through the layout
				boolean layoutNecessary = newNodes.size() > oldNodes.size();
				drawer.drawGraph(cluster, layoutNecessary, this, drawPlaceHolder);
			}
		}
	}

	*//**
	 * Threaded method for drawing exploration graphs which allows 
	 * the slider to move uninterruptedly despite drawing efforts
	 *//*
	private class GraphDrawer implements Runnable {
		private Thread thread;
		private boolean drawGraph; //run switch
		private boolean placeHolderDrawn;
		private boolean drawPlaceHolder;
		Cluster cluster;
		ClusterLayout layout;
		ResultPanel.SizeAction trigger;
		boolean layoutNecessary;
		boolean clusterSelected;
		GraphDrawer () {
			drawGraph = false;	//if there is need to draw graph for a complex
			drawPlaceHolder = false;	//if a place holder shouldbe draw in the case of big complexes
			layout = new ClusterLayout();
			thread = new Thread(this);
			thread.start();
		}
		*//**
		 * method for drawing graphs during exploration
		 *
		 * @param cluster Cluster to be drawn
		 * @param layoutNecessary True only if the cluster is expanding in size or lacks a DGView
		 * @param trigger Reference to the slider size action
		 * @param drawPlaceHolder Determines if the cluster should be drawn 
		 * or a place holder in the case of big complexes
		 *//*
		public void drawGraph(Cluster cluster, boolean layoutNecessary, 
				ResultPanel.SizeAction trigger, boolean drawPlaceHolder) {
			this.cluster = cluster;
			this.trigger = trigger;
			this.layoutNecessary = layoutNecessary;
			drawGraph = !drawPlaceHolder;
			this.drawPlaceHolder = drawPlaceHolder;
			clusterSelected = false;
		}
		public void run () {
			try {
				while (true) {
					if (drawGraph && !drawPlaceHolder) {
						Image image = ClusterUtil.convertClusterToImage(loader, cluster, picSize, picSize, layout, layoutNecessary);
						if (image != null && drawGraph) {
							loader.setProgress(100, "Selecting Nodes");
							//Select the new cluster
							selectCluster(cluster.getGPCluster());
							clusterSelected = true;
							//Update the table
							table.setValueAt(new ImageIcon(image), cluster.getRank(), 0);
							//Loader is no longer showing, let the SizeAction know that
							trigger.loaderSet = false;
							//stop loader from animating and taking up computer processing power
							loader.loaded();
							drawGraph = false;
						}
						placeHolderDrawn = false;
					} else if (drawPlaceHolder && !placeHolderDrawn) {
						//draw place holder, only once
						Image image = ClusterUtil.getPlaceHolderImage(picSize, picSize);
						table.setValueAt(new ImageIcon(image), cluster.getRank(), 0);
						selectCluster(cluster.getGPCluster());
						trigger.loaderSet = false;
						loader.loaded();
						drawGraph = false;
						//Make sure this block is not run again unless if we need to reload the image
						placeHolderDrawn = true;						
					} else if (!drawGraph && drawPlaceHolder && !clusterSelected) {
						selectCluster(cluster.getGPCluster());
						clusterSelected = true;
					}
					//This sleep time produces the drawing response time of 1 20th of a second
					Thread.sleep(100);
				}
			} catch (Exception e) {}
		}
		public void interruptDrawing() {
			drawGraph = false;
			layout.interruptDoLayout();
			ClusterUtil.interruptLoading();
		}
	}
	*//**
	 * Sets the network node attributes to the current result set's degrees and clusters.
	 * This method is accessed from VisualStyleAction only when a results panel is selected in the east cytopanel.
	 *
	 * @return the maximal degree in the network given the parameters that were used for scoring at the time
	 *//*
	public int setNodeAttributesAndGetMaxDegree() {
		Cytoscape.getNodeAttributes().deleteAttribute("Cluster");
		for (Iterator nodes = network.nodesIterator(); nodes.hasNext();) {
			Node n = (Node) nodes.next();
			int rgi = n.getRootGraphIndex();
			Cytoscape.getNodeAttributes().setAttribute(n.getIdentifier(), "Node_Status", "Unclustered");
			for (int c = 0; c < complexes.length; c++) {
				Cluster complex = complexes[c];
				if (complex.getALNodes().contains(new Integer(rgi))) {
					ArrayList clusterArrayList = new ArrayList();
					if (Cytoscape.getNodeAttributes().getListAttribute(n.getIdentifier(), "Cluster") != null) {
						clusterArrayList = (ArrayList) Cytoscape.getNodeAttributes().getListAttribute(n.getIdentifier(), "MCODE_Cluster");
						clusterArrayList.add(complex.getClusterName());
					} else {
						clusterArrayList.add(complex.getClusterName());
					}
					Cytoscape.getNodeAttributes().setListAttribute(n.getIdentifier(), "Cluster", clusterArrayList);

					if (complex.getSeedNode().intValue() == rgi) {
						Cytoscape.getNodeAttributes().setAttribute(n.getIdentifier(), "Node_Status", "Seed");
					} else {
						Cytoscape.getNodeAttributes().setAttribute(n.getIdentifier(), "Node_Status", "Clustered");
					}
				}
			}
			Cytoscape.getNodeAttributes().setAttribute(n.getIdentifier(), "Cluster_Size", alg.getNodeScore(n.getRootGraphIndex(), resultTitle));
		}
		if(network==null)
			return 0;
		int max=0,degree;
		for(Iterator it=network.nodesIterator(); it.hasNext();){
			Node node=(Node)it.next();
			degree=network.getDegree(node.getRootGraphIndex());
			if(degree>max)
				max=degree;
		}
		return max;
	}

	public int getResultId() {
		return resultId;
	}



	class PopUpDemo extends JPopupMenu {
		JMenuItem goEnrichmentItem;
		public PopUpDemo(int rowNumber){
			goEnrichmentItem = new JMenuItem("GO Enrichment Analysis on Cluster"+(rowNumber+1));
			goEnrichmentItem.addActionListener(new GOenrichmentAction());
			add(goEnrichmentItem);
		}
	}

	class PopClickListener extends MouseAdapter {
		public void mousePressed(MouseEvent e){
			Point p = e.getPoint();
			int rowNumber = table.rowAtPoint(p);
			ListSelectionModel model = table.getSelectionModel();
			model.setSelectionInterval(rowNumber, rowNumber);

			if (e.isPopupTrigger())
				doPop(e, rowNumber);
		}

		public void mouseReleased(MouseEvent e){
			Point p = e.getPoint();
			int rowNumber = table.rowAtPoint(p);
			ListSelectionModel model = table.getSelectionModel();
			model.setSelectionInterval(rowNumber, rowNumber);

			if (e.isPopupTrigger())
				doPop(e, rowNumber);
		}

		private void doPop(MouseEvent e, int rowNumber){
			PopUpDemo menu = new PopUpDemo(rowNumber);
			menu.show(e.getComponent(), e.getX(), e.getY());
   		 }

	}
	class GOenrichmentAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String bingoDir;
			String tmp = System.getProperty("user.dir");
			bingoDir = new File(tmp, "plugins").toString();
			JFrame window = new JFrame("BiNGO Settings");
			SettingsPanel settingsPanel = new SettingsPanel(bingoDir, selectedCluster);
			//window.setJMenuBar(new HelpMenuBar(settingsPanel).getHelpMenuBar());
			window.getContentPane().add(settingsPanel);
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.pack();
			Dimension screenSize =
					Toolkit.getDefaultToolkit().getScreenSize();
			// for central position of the settingspanel.
			window.setLocation(screenSize.width / 2 - (window.getWidth() / 2),
					screenSize.height / 2 - (window.getHeight() / 2));
			window.setVisible(true);
			window.setResizable(true);
		}
	}


}
*/


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.CytoCluster.internal.Cluster;
import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.DiscardResultAction;
import org.cytoscape.CytoCluster.internal.ParameterSet;
import org.cytoscape.CytoCluster.internal.Resources;
import org.cytoscape.CytoCluster.internal.Resources.ImageName;
import org.cytoscape.CytoCluster.internal.algorithm.Algorithm;
import org.cytoscape.CytoCluster.internal.algorithm.MCODE;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultPanel extends JPanel
  implements CytoPanelComponent
{
 // private static final String SCORE_ATTR = "MCODE_Score";
  //private static final String NODE_STATUS_ATTR = "MCODE_Node_Status";
  //private static final String CLUSTER_ATTR = "MCODE_Cluster";
  private static final int graphPicSize = 80;
  private static final int defaultRowHeight = 88;
  private final int resultId;
  private Algorithm alg;
  private static String algname;
  private final List<Cluster> clusters;
  private final CyNetwork network;
  private CyNetworkView networkView;
  private CollapsiblePanel explorePanel;
  private JPanel[] exploreContent;
  private JButton closeButton;
  private ParameterSet currentParamsCopy;
  private int enumerationSelection = 0;
  private MCODEClusterBrowserPanel clusterBrowserPanel;
  private final ClusterUtil mcodeUtil;
  private final DiscardResultAction discardResultAction;
  private static final Logger logger = LoggerFactory.getLogger(ResultPanel.class);

  public ResultPanel(List<Cluster> clusters, Algorithm alg, ClusterUtil mcodeUtil, CyNetwork network, CyNetworkView networkView, int resultId, DiscardResultAction discardResultAction)
  {
    setLayout(new BorderLayout());

    this.alg = alg;
    this.mcodeUtil = mcodeUtil;
    this.resultId = resultId;
    this.clusters = Collections.synchronizedList(clusters);
    this.network = network;

    this.networkView = networkView;
    this.discardResultAction = discardResultAction;
    this.currentParamsCopy = mcodeUtil.getCurrentParameters().getResultParams(resultId);
    algname = currentParamsCopy.getAlgorithm();
    System.out.println(algname+"NAME!!!!!!!!");
    
    this.clusterBrowserPanel = new MCODEClusterBrowserPanel();
    add(this.clusterBrowserPanel, "Center");
    add(createBottomPanel(), "South");

    setSize(getMinimumSize());
    
  
  }

  public Component getComponent()
  {
    return this;
  }

  public CytoPanelName getCytoPanelName()
  {
    return CytoPanelName.EAST;
  }

  public Icon getIcon()
  {
    URL iconURL = Resources.getUrl(Resources.ImageName.LOGO_SMALL);
    return new ImageIcon(iconURL);
  }

  public String getTitle()
  {
    return "Result " + getResultId();
  }

  public int getResultId() {
    return this.resultId;
  }

  public CyNetworkView getNetworkView() {
    return this.networkView;
  }

  public List<Cluster> getClusters() {
    return this.clusters;
  }

  public CyNetwork getNetwork() {
    return this.network;
  }

  public int getSelectedClusterRow() {
    return this.clusterBrowserPanel.getSelectedRow();
  }

  public void discard(final boolean requestUserConfirmation) {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        boolean oldRequestUserConfirmation = Boolean.valueOf(ResultPanel.this.discardResultAction
          .getValue("requestUserConfirmation").toString()).booleanValue();

        ResultPanel.this.discardResultAction.putValue("requestUserConfirmation", 
          Boolean.valueOf(requestUserConfirmation));
        ResultPanel.this.closeButton.doClick();
        ResultPanel.this.discardResultAction.putValue("requestUserConfirmation", 
          Boolean.valueOf(oldRequestUserConfirmation));
      }
    });
  }

  private JPanel createBottomPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    this.explorePanel = new CollapsiblePanel("Explore");
    this.explorePanel.setCollapsed(false);
    this.explorePanel.setVisible(false);

    JPanel buttonPanel = new JPanel();

    JButton exportButton = new JButton("Export");
    exportButton.addActionListener(new ExportAction());
    exportButton.setToolTipText("Export result set to a text file");

    this.closeButton = new JButton(this.discardResultAction);
    this.discardResultAction.putValue("requestUserConfirmation", Boolean.valueOf(true));

    buttonPanel.add(exportButton);
    buttonPanel.add(this.closeButton);

    panel.add(this.explorePanel, "North");
    panel.add(buttonPanel, "South");

    return panel;
  }

  private JPanel createExploreContent(int selectedRow)
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));

   /* JPanel sizePanel = new JPanel(new BorderLayout());
    sizePanel.setBorder(BorderFactory.createTitledBorder("Size Threshold"));

    JSlider sizeSlider = new JSlider(0, 0, 1000, 
      (int)(this.currentParamsCopy.getNodeScoreCutoff() * 1000.0D))
    {
      public JToolTip createToolTip() {
        return new JMultiLineToolTip();
      }
    };
    sizeSlider.setMajorTickSpacing(200);
    sizeSlider.setMinorTickSpacing(50);
    sizeSlider.setPaintTicks(true);
    sizeSlider.setPaintLabels(true);

    Dictionary labelTable = new Hashtable();
    labelTable.put(Integer.valueOf(0), new JLabel("Min"));
    labelTable.put(Integer.valueOf(1000), new JLabel("Max"));

    labelTable.put(Integer.valueOf((int)(this.currentParamsCopy.getNodeScoreCutoff() * 1000.0D)), new JLabel("^"));

    sizeSlider.setLabelTable(labelTable);
    sizeSlider.setFont(new Font("Arial", 0, 8));

    String sizeTip = "Move the slider to include or\nexclude nodes from the cluster";
    sizeSlider.setToolTipText(sizeTip);

    sizePanel.add(sizeSlider, "North");
*/
    JPanel nodeAttributesPanel = new JPanel(new BorderLayout());
    nodeAttributesPanel.setBorder(BorderFactory.createTitledBorder("Node Attribute Enumerator"));

    Collection<CyColumn> nodeColumns = this.network.getDefaultNodeTable().getColumns();
    String[] availableAttributes = new String[nodeColumns.size()];

    int i = 0;
    for (CyColumn column : nodeColumns) {
      availableAttributes[(i++)] = column.getName();
    }
    Arrays.sort(availableAttributes, String.CASE_INSENSITIVE_ORDER);

    String[] attributesList = new String[availableAttributes.length + 1];
    System.arraycopy(availableAttributes, 0, attributesList, 1, availableAttributes.length);
    attributesList[0] = "Please Select";

    JComboBox nodeAttributesComboBox = new JComboBox(attributesList);

//    sizeSlider.addChangeListener(new SizeAction(selectedRow, nodeAttributesComboBox));

    MCODEResultsEnumeratorTableModel modelEnumerator = new MCODEResultsEnumeratorTableModel(new HashMap());

    JTable enumerationsTable = new JTable(modelEnumerator);

    JScrollPane tableScrollPane = new JScrollPane(enumerationsTable);
    tableScrollPane.getViewport().setBackground(Color.WHITE);
    enumerationsTable.setPreferredScrollableViewportSize(new Dimension(100, 80));
    enumerationsTable.setGridColor(Color.LIGHT_GRAY);
    enumerationsTable.setFont(new Font(enumerationsTable.getFont().getFontName(), 0, 11));
    enumerationsTable.setDefaultRenderer(StringBuffer.class, new JTextAreaRenderer(0));
    enumerationsTable.setFocusable(false);

    nodeAttributesComboBox.addActionListener(new enumerateAction(modelEnumerator, selectedRow));

    nodeAttributesPanel.add(nodeAttributesComboBox, "North");
    nodeAttributesPanel.add(tableScrollPane, "South");

    JPanel bottomExplorePanel = createBottomExplorePanel(selectedRow);

  //  panel.add(sizePanel);
    panel.add(nodeAttributesPanel);
    panel.add(bottomExplorePanel);

    return panel;
  }

  private JPanel createBottomExplorePanel(int selectedRow)
  {
    JPanel panel = new JPanel();
    JButton createChildButton = new JButton("Create Sub-Network");
    createChildButton.addActionListener(new CreateSubNetworkAction(this, selectedRow));
    panel.add(createChildButton);

    return panel;
  }

  public double setNodeAttributesAndGetMaxScore()
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.network.getNodeList().iterator(); localIterator1.hasNext(); 
      localIterator2.hasNext())
    {
      CyNode n = (CyNode)localIterator1.next();
      Long rgi = n.getSUID();
      CyTable netNodeTbl = this.network.getDefaultNodeTable();

   /*   if (netNodeTbl.getColumn("MCODE_Cluster") == null)
        netNodeTbl.createListColumn("MCODE_Cluster", String.class, false);
      if (netNodeTbl.getColumn("MCODE_Node_Status") == null)
        netNodeTbl.createColumn("MCODE_Node_Status", String.class, false);
      if (netNodeTbl.getColumn("MCODE_Score") == null) {
        netNodeTbl.createColumn("MCODE_Score", Double.class, false);
      }*/
  //    CyRow nodeRow = this.network.getRow(n);
   //   nodeRow.set("MCODE_Node_Status", "Unclustered");
  //    nodeRow.set("MCODE_Score", Double.valueOf(this.alg.getNodeScore(n.getSUID(), this.resultId)));

      localIterator2 = this.clusters.iterator(); 
      Cluster cluster = (Cluster)localIterator2.next();
     // if (cluster.getAlCluster().contains(rgi)) {
      if (cluster.getALNodes().contains(rgi)) {
      
        Set clusterNameSet = new LinkedHashSet();

   /*     if (nodeRow.isSet("MCODE_Cluster")) {
          clusterNameSet.addAll(nodeRow.getList("MCODE_Cluster", String.class));
        }
        clusterNameSet.add(cluster.getClusterName());
        nodeRow.set("MCODE_Cluster", new ArrayList(clusterNameSet));

        if (cluster.getSeedNode() == rgi)
          nodeRow.set("MCODE_Node_Status", "Seed");
        else {
          nodeRow.set("MCODE_Node_Status", "Clustered");
        }*/
      }
    }
    
    return this.alg.getMaxScore(this.resultId);
  }

  private static StringBuffer getClusterDetails(Cluster cluster) {
    StringBuffer details = new StringBuffer();
    
    

    details.append("Rank: ");
    details.append(String.valueOf(cluster.getRank() + 1));

    if(algname.compareTo("MCODE")==0){
    	details.append("\n");
    	details.append("Score: ");
    	NumberFormat nf = NumberFormat.getInstance();
    	nf.setMaximumFractionDigits(3);
    	details.append(nf.format(cluster.getClusterScore()));
    }
    details.append("\n");
    details.append("Nodes: ");
    details.append(cluster.getNetwork().getNodeCount());

    details.append("\n");
    details.append("Edges: ");
    details.append(cluster.getNetwork().getEdgeCount());

    return details;
  }

	private ArrayList sortMap(Map map) {
		ArrayList outputList = null;
		int count = 0;
		Set set = null;
		Map.Entry[] entries = null;

		set = (Set) map.entrySet();
		Iterator iterator = set.iterator();
		entries = new Map.Entry[set.size()];
		while(iterator.hasNext()) {
			entries[count++] = (Map.Entry) iterator.next();
		}

		// Sort the entries with own comparator for the values:
		Arrays.sort(entries, new Comparator() {
			public int compareTo(Object o1, Object o2) {
				Map.Entry le = (Map.Entry)o1;
				Map.Entry re = (Map.Entry)o2;
				return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
			}

			public int compare(Object o1, Object o2) {
				Map.Entry le = (Map.Entry)o1;
				Map.Entry re = (Map.Entry)o2;
				return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
			}
		});
		outputList = new ArrayList();
		for(int i = 0; i < entries.length; i++) {
			outputList.add(entries[i]);
		}
		return outputList;
	}

  public void selectCluster(CyNetwork custerNetwork)
  {
    if (custerNetwork != null)
    {
      this.mcodeUtil.setSelected(custerNetwork.getNodeList(), this.network);
    }
    else
    {
      this.mcodeUtil.setSelected(new ArrayList(), this.network);
    }
  }

  private class CreateSubNetworkAction extends AbstractAction
  {
    int selectedRow;
    ResultPanel trigger;

    CreateSubNetworkAction(ResultPanel trigger, int selectedRow)
    {
      this.selectedRow = selectedRow;
      this.trigger = trigger;
    }

    public void actionPerformed(ActionEvent evt)
    {
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(3);
      final Cluster cluster = (Cluster)ResultPanel.this.clusters.get(this.selectedRow);
      final CyNetwork clusterNetwork = cluster.getNetwork();
      final String title = this.trigger.getResultId() + ": " + cluster.getClusterName() + " (Score: " + 
        nf.format(cluster.getClusterScore()) + ")";

      SwingWorker worker = new SwingWorker()
      {
        protected CyNetworkView doInBackground() throws Exception
        {
          CySubNetwork newNetwork = ResultPanel.this.mcodeUtil.createSubNetwork(clusterNetwork, clusterNetwork.getNodeList(), 
            SavePolicy.SESSION_FILE);
          newNetwork.getRow(newNetwork).set("name", title);

          VisualStyle vs = ResultPanel.this.mcodeUtil.getNetworkViewStyle(ResultPanel.this.networkView);
          CyNetworkView newNetworkView = ResultPanel.this.mcodeUtil.createNetworkView(newNetwork, vs);

          newNetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_X_LOCATION, Double.valueOf(0.0D));
          newNetworkView.setVisualProperty(BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION, Double.valueOf(0.0D));

          ResultPanel.this.mcodeUtil.displayNetworkView(newNetworkView);

          boolean layoutNecessary = false;
          CyNetworkView clusterView = cluster.getView();

          for (View nv : newNetworkView.getNodeViews()) {
            CyNode node = (CyNode)nv.getModel();
            View cnv = clusterView != null ? clusterView.getNodeView(node) : null;

            if (cnv != null)
            {
              double x = ((Double)cnv.getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION)).doubleValue();
              double y = ((Double)cnv.getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION)).doubleValue();
              nv.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, Double.valueOf(x));
              nv.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, Double.valueOf(y));
            }
            else
            {
              double w = ((Double)newNetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH)).doubleValue();
              double h = ((Double)newNetworkView.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT)).doubleValue();

              nv.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, Double.valueOf(w * Math.random()));

              nv.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, Double.valueOf((h + 100.0D) * Math.random()));

              layoutNecessary = true;
            }
          }

          if (layoutNecessary) {
            SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(newNetworkView);
            lay.doLayout(0.0D, 0.0D, 0.0D, null);
          }

          newNetworkView.fitContent();
          newNetworkView.updateView();

          return newNetworkView;
        }
      };
      worker.execute();
    }
  }

  private class ExportAction extends AbstractAction
  {
    private ExportAction()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
      mcodeUtil.exportResults(alg, clusters, network);
    }
  }

  private class GraphDrawer
    implements Runnable
  {
    private boolean drawGraph;
    private boolean placeHolderDrawn;
    private boolean drawPlaceHolder;
    Cluster cluster;
    SpringEmbeddedLayouter layouter;
    boolean layoutNecessary;
    boolean clusterSelected;
    private Thread t;
    private final Loader loader;

    GraphDrawer(Loader loader)
    {
      this.loader = loader;
      this.layouter = new SpringEmbeddedLayouter();
    }

    public void drawGraph(Cluster cluster, boolean layoutNecessary, boolean drawPlaceHolder)
    {
      this.cluster = cluster;
      this.layoutNecessary = layoutNecessary;

      this.drawGraph = (!drawPlaceHolder);
      this.drawPlaceHolder = drawPlaceHolder;
      this.clusterSelected = false;
      this.t = new Thread(this);
      this.t.start();
    }

    
    
    
    public void run()
    {
      try
      {
        if (!this.drawPlaceHolder)
        {
          this.loader.start();
        }

        Thread currentThread = Thread.currentThread();

        while (this.t == currentThread)
        {
          if ((this.drawGraph) && (!this.drawPlaceHolder)) {
            Image image = ResultPanel.this.mcodeUtil.convertClusterToImage(this.loader,this.cluster, 
              80, 
              80, 
              this.layouter, 
              this.layoutNecessary
              );

            if ((image != null) && (this.drawGraph))
            {
              this.loader.setProgress(100, "Selecting Nodes");
              ResultPanel.this.selectCluster(this.cluster.getNetwork());
              this.clusterSelected = true;
              this.cluster.setImage(image);

              ResultPanel.this.clusterBrowserPanel.update(new ImageIcon(image), this.cluster.getRank());
              this.drawGraph = false;
            }

            this.placeHolderDrawn = false;
          } else if ((this.drawPlaceHolder) && (!this.placeHolderDrawn))
          {
            Image image = ResultPanel.this.mcodeUtil.getPlaceHolderImage(80, 80);
            this.cluster.setImage(image);

            ResultPanel.this.clusterBrowserPanel.update(new ImageIcon(image), this.cluster.getRank());

            ResultPanel.this.selectCluster(this.cluster.getNetwork());
            this.drawGraph = false;

            this.placeHolderDrawn = true;
          } else if ((!this.drawGraph) && (this.drawPlaceHolder) && (!this.clusterSelected)) {
            ResultPanel.this.selectCluster(this.cluster.getNetwork());
            this.clusterSelected = true;
          }

          if (((!this.drawGraph) && (!this.drawPlaceHolder)) || (this.placeHolderDrawn)) {
            stop();
          }

          Thread.sleep(100L);
        }
      } catch (Exception e) {
        ResultPanel.logger.error("Error while drawing cluster image", e);
      }
    }

    void stop()
    {
      this.loader.stop();
      this.layouter.interruptDoLayout();
      ResultPanel.this.mcodeUtil.interruptLoading();
      this.drawGraph = false;
      this.t = null;
    }
  }

  private static class JTextAreaRenderer extends JTextArea
    implements TableCellRenderer
  {
    int minHeight;

    public JTextAreaRenderer(int minHeight)
    {
      setLineWrap(true);
      setWrapStyleWord(true);
      setEditable(false);
      setFont(new Font(getFont().getFontName(), 0, 11));
      this.minHeight = minHeight;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      StringBuffer sb = (StringBuffer)value;
      setText(sb.toString());

      if (isSelected) {
        setBackground(table.getSelectionBackground());
        setForeground(table.getSelectionForeground());
      } else {
        setBackground(table.getBackground());
        setForeground(table.getForeground());
      }

      int currentRowHeight = table.getRowHeight(row);
      int rowMargin = table.getRowMargin();
      setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight - 2 * rowMargin);
      int textAreaPreferredHeight = (int)getPreferredSize().getHeight();

      if (currentRowHeight != Math.max(textAreaPreferredHeight + 2 * rowMargin, this.minHeight + 2 * rowMargin)) {
        table.setRowHeight(row, 
          Math.max(textAreaPreferredHeight + 2 * rowMargin, this.minHeight + 2 * rowMargin));
      }

      return this;
    }
  }

  private class MCODEClusterBrowserPanel extends JPanel
  {
	    private final ResultPanel.MCODEClusterBrowserTableModel browserModel;
	    private final JTable table;

    public MCODEClusterBrowserPanel()
    {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createTitledBorder("Cluster Browser"));

      this.browserModel = new ResultPanel.MCODEClusterBrowserTableModel();

      this.table = new JTable(this.browserModel);
      this.table.setSelectionMode(0);
      this.table.setDefaultRenderer(StringBuffer.class, new ResultPanel.JTextAreaRenderer(88));
      this.table.setIntercellSpacing(new Dimension(0, 4));
      this.table.setFocusable(false);

      ListSelectionModel rowSM = this.table.getSelectionModel();
      rowSM.addListSelectionListener(new ResultPanel.TableRowSelectionHandler());

      JScrollPane tableScrollPane = new JScrollPane(this.table);
      tableScrollPane.getViewport().setBackground(Color.WHITE);

      add(tableScrollPane, "Center");
    }

    public int getSelectedRow() {
      return this.table.getSelectedRow();
    }

    public void update(ImageIcon image, int row) {
      this.table.setValueAt(image, row, 0);
    }

    public void update(Cluster cluster, int row) {
      StringBuffer details = ResultPanel.getClusterDetails(cluster);
      this.table.setValueAt(details, row, 1);
    }

    JTable getTable() {
      return this.table;
    }
  }

  private class MCODEClusterBrowserTableModel extends AbstractTableModel
  {
    private final String[] columnNames = { "Network", "Details" };
    private final Object[][] data;

    public MCODEClusterBrowserTableModel()
    {
      ResultPanel.this.exploreContent = new JPanel[ResultPanel.this.clusters.size()];
      this.data = new Object[ResultPanel.this.clusters.size()][this.columnNames.length];

      for (int i = 0; i < ResultPanel.this.clusters.size(); i++) {
        Cluster c = (Cluster)ResultPanel.this.clusters.get(i);
        c.setRank(i);
        StringBuffer details = ResultPanel.getClusterDetails(c);
        this.data[i][1] = new StringBuffer(details);

        Image image = c.getImage();
        this.data[i][0] = (image != null ? new ImageIcon(image) : new ImageIcon());
      }
    }

    public String getColumnName(int col)
    {
      return this.columnNames[col];
    }

    public int getColumnCount()
    {
      return this.columnNames.length;
    }

    public int getRowCount()
    {
      return this.data.length;
    }

    public Object getValueAt(int row, int col)
    {
      return this.data[row][col];
    }

    public void setValueAt(Object object, int row, int col)
    {
      this.data[row][col] = object;
      fireTableCellUpdated(row, col);
    }

    public Class<?> getColumnClass(int c)
    {
      return getValueAt(0, c).getClass();
    }
  }

  private class MCODEResultsEnumeratorTableModel extends AbstractTableModel
  {
    String[] columnNames = { "Value", "Occurrence" };
    Object[][] data = new Object[0][this.columnNames.length];

    public MCODEResultsEnumeratorTableModel(HashMap enumerations)
    {
      listIt(enumerations);
    }

    public void listIt(HashMap enumerations)
    {
      ArrayList enumerationsSorted = ResultPanel.this.sortMap(enumerations);

      Object[][] newData = new Object[enumerationsSorted.size()][this.columnNames.length];
      int c = enumerationsSorted.size() - 1;

      for (Iterator i = enumerationsSorted.iterator(); i.hasNext(); ) {
        Map.Entry mp = (Map.Entry)i.next();
        newData[c][0] = new StringBuffer(mp.getKey().toString());
        newData[c][1] = mp.getValue().toString();
        c--;
      }

      if (getRowCount() == newData.length) {
        this.data = new Object[newData.length][this.columnNames.length];
        System.arraycopy(newData, 0, this.data, 0, this.data.length);
        fireTableRowsUpdated(0, getRowCount());
      } else {
        this.data = new Object[newData.length][this.columnNames.length];
        System.arraycopy(newData, 0, this.data, 0, this.data.length);
        fireTableDataChanged();
      }
    }

    public String getColumnName(int col) {
      return this.columnNames[col];
    }

    public int getRowCount() {
      return this.data.length;
    }

    public int getColumnCount() {
      return this.columnNames.length;
    }

    public Object getValueAt(int row, int col) {
      return this.data[row][col];
    }

    public void setValueAt(Object object, int row, int col) {
      this.data[row][col] = object;
      fireTableCellUpdated(row, col);
    }

    public Class<?> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
      return false;
    }
  }

  private class SizeAction
    implements ChangeListener
  {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private ScheduledFuture<?> futureLoader;
    private int selectedRow;
    private JComboBox nodeAttributesComboBox;
    private boolean drawPlaceHolder;
    private final ResultPanel.GraphDrawer drawer;
    private final Loader loader;

    SizeAction(int selectedRow, JComboBox nodeAttributesComboBox)
    {
      this.selectedRow = selectedRow;
      this.nodeAttributesComboBox = nodeAttributesComboBox;
      this.loader = new Loader(selectedRow, ResultPanel.this.clusterBrowserPanel.getTable(), 80, 80);
      this.drawer = new ResultPanel.GraphDrawer(this.loader);
    }

    public void stateChanged(ChangeEvent e)
    {
      
    	System.out.println("change!!!!!!!!");
    	JSlider source = (JSlider)e.getSource();
      final double nodeScoreCutoff = source.getValue() / 1000.0D;
      final int clusterRow = this.selectedRow;

      final Cluster oldCluster = (Cluster)ResultPanel.this.clusters.get(clusterRow);

      if ((this.futureLoader != null) && (!this.futureLoader.isDone())) {
        this.drawer.stop();
        this.futureLoader.cancel(false);

        if (!oldCluster.equals(ResultPanel.this.clusters.get(clusterRow))) {
          oldCluster.dispose();
        }
      }
      Runnable command = new Runnable()
      {
        public void run() {
        	System.out.println("run!!!!!!!!");
          List<Long> oldALCluster = oldCluster.getAlCluster();
          
          System.out.println(nodeScoreCutoff);
          Cluster newCluster = ResultPanel.this.alg.exploreCluster(oldCluster, nodeScoreCutoff, ResultPanel.this.network, ResultPanel.this.resultId);

          List<Long> newALCluster = newCluster.getAlCluster();

          ResultPanel.SizeAction.this.drawPlaceHolder = (newALCluster.size() > 300);

          if (!newALCluster.equals(oldALCluster))
          {
            ResultPanel.this.clusters.set(clusterRow, newCluster);

            System.out.println("update!!!!!!!!");
            ResultPanel.this.clusterBrowserPanel.update(newCluster, clusterRow);

            ResultPanel.SizeAction.this.nodeAttributesComboBox.setSelectedIndex(ResultPanel.SizeAction.this.nodeAttributesComboBox.getSelectedIndex());

            boolean layoutNecessary = newALCluster.size() > oldALCluster.size();

            if (!newCluster.isDisposed()) {
              ResultPanel.SizeAction.this.drawer.drawGraph(newCluster, layoutNecessary, ResultPanel.SizeAction.this.drawPlaceHolder);
              oldCluster.dispose();
            }
          }
          ResultPanel.this.mcodeUtil.destroyUnusedNetworks(ResultPanel.this.network, ResultPanel.this.clusters);
        }
      };
      this.futureLoader = this.scheduler.schedule(command, 100L, TimeUnit.MILLISECONDS);
    }
  }

  private class TableRowSelectionHandler
    implements ListSelectionListener
  {
    private TableRowSelectionHandler()
    {
    }

    public void valueChanged(ListSelectionEvent e)
    {
      if (e.getValueIsAdjusting()) return;

      ListSelectionModel lsm = (ListSelectionModel)e.getSource();

      if (!lsm.isSelectionEmpty()) {
        int selectedRow = lsm.getMinSelectionIndex();
        Cluster c = (Cluster)ResultPanel.this.clusters.get(selectedRow);
        CyNetwork gpCluster = c.getNetwork();//最新的结果可以获得值，而非最新结果不能获得值
        
        System.out.println(gpCluster.getNodeList().size()+"size!!!!!!!!!");
        
        
        ResultPanel.this.selectCluster(gpCluster);

        if (ResultPanel.this.exploreContent[selectedRow] == null) {
          ResultPanel.this.exploreContent[selectedRow] = ResultPanel.this.createExploreContent(selectedRow);
        }

        if (ResultPanel.this.explorePanel.isVisible()) {
          ResultPanel.this.explorePanel.getContentPane().remove(0);
        }

        ResultPanel.this.explorePanel.getContentPane().add(ResultPanel.this.exploreContent[selectedRow], "Center");

        if (!ResultPanel.this.explorePanel.isVisible()) {
          ResultPanel.this.explorePanel.setVisible(true);
        }

        String title = "Explore: ";

        if (c.getClusterName() != null)
          title = title + c.getClusterName();
        else {
          title = title + "Cluster " + (selectedRow + 1);
        }

        ResultPanel.this.explorePanel.setTitleComponentText(title);
        ResultPanel.this.explorePanel.updateUI();

        JComboBox nodeAttributesComboBox = (JComboBox)((JPanel)ResultPanel.this.exploreContent[selectedRow].getComponent(1))
          .getComponent(0);

        nodeAttributesComboBox.setSelectedIndex(ResultPanel.this.enumerationSelection);
      }
    }
  }

  private class enumerateAction extends AbstractAction
  {
    int selectedRow;
    ResultPanel.MCODEResultsEnumeratorTableModel modelEnumerator;

    enumerateAction(ResultPanel.MCODEResultsEnumeratorTableModel modelEnumerator, int selectedRow)
    {
      this.selectedRow = selectedRow;
      this.modelEnumerator = modelEnumerator;
    }

    public void actionPerformed(ActionEvent e)
    {
      HashMap attributeEnumerations = new HashMap();

      String attributeName = (String)((JComboBox)e.getSource()).getSelectedItem();
      int selectionIndex = ((JComboBox)e.getSource()).getSelectedIndex();

      if (!attributeName.equals("Please Select")) {
        CyNetwork net = ((Cluster)ResultPanel.this.clusters.get(this.selectedRow)).getNetwork();
        Object value;
        for (Iterator localIterator1 = net.getNodeList().iterator(); localIterator1.hasNext(); 
          )
        {
          CyNode node = (CyNode)localIterator1.next();

          ArrayList attributeValues = new ArrayList();
          CyRow row = net.getRow(node);
          Class type = row.getTable().getColumn(attributeName).getType();

          if (Collection.class.isAssignableFrom(type)) {
            Collection valueList = (Collection)row.get(attributeName, type);

            if (valueList != null)
              for (Iterator localIterator2 = valueList.iterator(); localIterator2.hasNext(); ) { value = localIterator2.next();
                attributeValues.add(value); }
          }
          else
          {
            attributeValues.add(row.get(attributeName, type));
          }

          value = attributeValues.iterator(); 
          Object aviElement = ((Iterator) value).next();
          if (aviElement != null) {
            value = aviElement.toString();

            if (!attributeEnumerations.containsKey(value))
            {
              attributeEnumerations.put(value, Integer.valueOf(1));
            }
            else {
              Integer enumeration = (Integer)attributeEnumerations.get(value);
              enumeration = Integer.valueOf(enumeration.intValue() + 1);
              attributeEnumerations.put(value, enumeration);
            }
          }
        }

      }

      this.modelEnumerator.listIt(attributeEnumerations);

      ResultPanel.this.enumerationSelection = selectionIndex;
    }
  }
}
