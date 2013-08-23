package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinGraph;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.Resources;
import org.cytoscape.CytoNCA.internal.SpringEmbeddedLayouter;
import org.cytoscape.CytoNCA.internal.Resources.ImageName;
import org.cytoscape.CytoNCA.internal.actions.DiscardResultAction;
import org.cytoscape.CytoNCA.internal.algorithm.Algorithm;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultPanel extends JPanel implements CytoPanelComponent{
	// private static final String SCORE_ATTR = "MCODE_Score";
	// private static final String NODE_STATUS_ATTR = "MCODE_Node_Status";
	// private static final String CLUSTER_ATTR = "MCODE_Cluster";
	private static final int graphPicSize = 80;
	private static final int defaultRowHeight = 88;
	private final int resultId;
	private ArrayList<String> alg;
	private String curalg;
	private final List<Protein> proteins;
	private List<Protein> sproteins;
	private List<CyNode> nodes;
	private ProteinGraph pg;
	private final CyNetwork network;
	private CyNetworkView networkView;
	private CollapsiblePanel explorePanel;
	private JPanel[] exploreContent;
	private JButton closeButton;
	private ParameterSet currentParamsCopy;
	private int enumerationSelection = 0;
	private BrowserPanel browserPanel;
	//private EvaluationPanel evaluationPanel;
	private EvaluationPanel evaluationPanel;
	private ProteinUtil pUtil;
	private final DiscardResultAction discardResultAction;
	private static final Logger logger = LoggerFactory
			.getLogger(ResultPanel.class);
	private ArrayList<String> eplist;
	private int selectNum = 1;
	private CyServiceRegistrar registrar;
	public ArrayList<ChartFrame> chartfs;
	private int maxY;
	public ResultPanel(ArrayList<Protein> resultL, ArrayList<String> alg,
			ProteinUtil pUtil, CyNetwork network, CyNetworkView networkView,
			int resultId, DiscardResultAction discardResultAction, CyServiceRegistrar registrar) {
		setLayout(new BorderLayout());

		this.registrar = registrar;
		this.pUtil = pUtil;
		this.alg = alg;
		this.curalg = alg.get(0);
		System.out.println(curalg+"   $$$$$$$$$$$");
		
		this.resultId = resultId;
		this.proteins = Collections.synchronizedList(resultL);
		this.network = network;
		pUtil.sortVertex(proteins, curalg);
		this.sproteins = this.proteins;
		this.selectNum = sproteins.size();
		chartfs = new ArrayList<ChartFrame>();
		
		this.networkView = networkView;
		this.discardResultAction = discardResultAction;
		this.currentParamsCopy = pUtil.getCurrentParameters().getResultParams(
				resultId);
		this.eplist = pUtil.getAlleprotein();
		

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.browserPanel = new BrowserPanel();
		StringBuffer sb = new StringBuffer("Result List( ");
		sb.append(proteins.size());
		sb.append(" in total )");
		this.browserPanel.setBorder(BorderFactory.createTitledBorder(sb
				.toString()));

	
	//	this.evaluationPanel = new EvaluationPanel();
		this.setPreferredSize(new Dimension(400, 500));
		//add(createSelectPanel());
		add(this.browserPanel);
		add(createBottomPanel());
	//	add(evaluationPanel);
		setColumnColor(2);
		
		ceateEvaluationPanel();
		

	}

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	public Icon getIcon() {
		URL iconURL = Resources.getUrl(Resources.ImageName.LOGO_SMALL);
		return new ImageIcon(iconURL);
	}

	public String getTitle() {
		return "Result " + getResultId();
	}

	public int getResultId() {
		return this.resultId;
	}

	public CyNetworkView getNetworkView() {
		return this.networkView;
	}

	public CyNetwork getNetwork() {
		return this.network;
	}

	public int getSelectedClusterRow() {
		return this.browserPanel.getSelectedRow();
	}
	
	
	

	public void discard(final boolean requestUserConfirmation) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean oldRequestUserConfirmation = Boolean.valueOf(
						ResultPanel.this.discardResultAction.getValue(
								"requestUserConfirmation").toString())
						.booleanValue();

				ResultPanel.this.discardResultAction.putValue(
						"requestUserConfirmation",
						Boolean.valueOf(requestUserConfirmation));
				ResultPanel.this.closeButton.doClick();
				ResultPanel.this.discardResultAction.putValue(
						"requestUserConfirmation",
						Boolean.valueOf(oldRequestUserConfirmation));
			}
		});
	}
	
	//private JPanel createSelectPanel() {
//		JPanel selectPanel = new JPanel();
	//	JComboBox<Integer> select = new JComboBox<Integer>();
	//	for(int i = 1; i<= proteins.size(); i++)
	//		select.addItem(i);
	//	select.setSelectedIndex(selectNum - 1);
	//	select.addItemListener(new SelectAction());	
	//	selectPanel.add(select);
///		JTextField select = new JTextField(6);
//		select.setText(proteins.size()+"");
	//	selectPanel.add(select);
//		return selectPanel;
		
//	}
	
	
	

	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		// this.explorePanel = new CollapsiblePanel("Explore");
		// this.explorePanel.setCollapsed(false);
		// this.explorePanel.setVisible(false);

		JPanel buttonPanel = new JPanel();

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ExportAction());
		exportButton.setToolTipText("Export result set to a text file");

		this.closeButton = new JButton(this.discardResultAction);
		this.discardResultAction.putValue("requestUserConfirmation",
				Boolean.valueOf(true));

		JButton columnchartButton = new JButton("Centralitiy distribution");
		columnchartButton.addActionListener(new createChartAction());
		columnchartButton.setToolTipText("Visualizing the distribution of the selected centrality’ value with histogram.");
		
		buttonPanel.add(exportButton);
		
		buttonPanel.add(columnchartButton);
		buttonPanel.add(this.closeButton);
		
		
	//	JPanel closepanel = new JPanel();
	//	closepanel.add(this.closeButton);
		// panel.add(this.explorePanel, "North");
		panel.add(createBottomExplorePanel(), "North");
		panel.add(buttonPanel, "Center");
	//	panel.add(closepanel,"South");
		
       
		return panel;
	}

	private JPanel createBottomExplorePanel() {
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		JButton createChildButton = new JButton("Create Sub-Network");
		createChildButton.addActionListener(new CreateSubNetworkAction(this));
		createChildButton.setToolTipText("Generating the new sub network of selected nodes.");
		
		JPanel selectPanel = new JPanel();
		JTextField selectT = new JTextField(6);
		selectT.setText(proteins.size()+"");
		selectPanel.add(selectT);
		
		panel2.add(new JLabel("Top", SwingConstants.RIGHT));
		panel2.add(selectPanel);
		panel2.add(new JLabel("Proteins", SwingConstants.LEFT));
		JButton select  = new JButton("Select");
		select.addActionListener(new SelectAction(selectT));
		panel2.add(select);
		panel2.setBorder(BorderFactory.createTitledBorder(""));
		
		panel.add(panel2);
		panel.add(createChildButton);
		
		return panel;
	}

	public void selectProteins(CyNetwork custerNetwork) {
		if (custerNetwork != null) {
			this.pUtil.setSelected(custerNetwork.getNodeList(), this.network);
		} else {
			this.pUtil.setSelected(new ArrayList(), this.network);
		}
	}

	private class CreateSubNetworkAction extends AbstractAction {
		// int selectedRow;
		ResultPanel trigger;

		CreateSubNetworkAction(ResultPanel trigger) {
			// this.selectedRow = selectedRow;
			this.trigger = trigger;
		}

		public void actionPerformed(ActionEvent evt) {
			// NumberFormat nf = NumberFormat.getInstance();
			// nf.setMaximumFractionDigits(3);
			// final Cluster cluster =
			// (Cluster)ResultPanel.this.clusters.get(this.selectedRow);
			if( pg !=null){
				final CyNetwork pNetwork = pg.getSubNetwork();
				final String title = this.trigger.getResultId()
						+"("+ curalg + ")";

				SwingWorker worker = new SwingWorker() {
					protected CyNetworkView doInBackground() throws Exception {
						CySubNetwork newNetwork = ResultPanel.this.pUtil
								.createSubNetwork(pNetwork, pNetwork.getNodeList(),
										SavePolicy.SESSION_FILE);
						newNetwork.getRow(newNetwork).set("name", title);

						VisualStyle vs = ResultPanel.this.pUtil
								.getNetworkViewStyle(ResultPanel.this.networkView);
						CyNetworkView newNetworkView = ResultPanel.this.pUtil
								.createNetworkView(newNetwork, vs);

						newNetworkView.setVisualProperty(
								BasicVisualLexicon.NETWORK_CENTER_X_LOCATION,
								Double.valueOf(0.0D));
						newNetworkView.setVisualProperty(
								BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION,
								Double.valueOf(0.0D));

						ResultPanel.this.pUtil.displayNetworkView(newNetworkView);

						boolean layoutNecessary = false;
						// CyNetworkView clusterView = cluster.getView();

						for (View nv : newNetworkView.getNodeViews()) {
							CyNode node = (CyNode) nv.getModel();

							double w = ((Double) newNetworkView
									.getVisualProperty(BasicVisualLexicon.NETWORK_WIDTH))
									.doubleValue();
							double h = ((Double) newNetworkView
									.getVisualProperty(BasicVisualLexicon.NETWORK_HEIGHT))
									.doubleValue();

							nv.setVisualProperty(
									BasicVisualLexicon.NODE_X_LOCATION,
									Double.valueOf(w * Math.random()));
							nv.setVisualProperty(
									BasicVisualLexicon.NODE_Y_LOCATION,
									Double.valueOf((h + 100.0D) * Math.random()));

							layoutNecessary = true;
							String name = network.getRow(node).get("name",
									String.class);

							if (eplist != null && !eplist.isEmpty()) {

								System.out.println("SSSSSSSSSS");
								if (eplist.contains(name)) {
									nv.setVisualProperty(
											BasicVisualLexicon.NODE_FILL_COLOR,
											java.awt.Color.RED);
									System.out.println(name + "        RED");
								} else {
									System.out.println(name + "        BLUE");
									nv.setVisualProperty(
											BasicVisualLexicon.NODE_FILL_COLOR,
											java.awt.Color.BLUE);
								}
							} else
								System.out.println("RRRRRRRRR");

						}

						if (layoutNecessary) {
							SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(
									newNetworkView);
							lay.doLayout(0.0D, 0.0D, 0.0D, null);
						}

						newNetworkView.fitContent();
						newNetworkView.updateView();

						return newNetworkView;
					}
				};
				worker.execute();
			}
			else{
				JOptionPane.showMessageDialog(null,
	            		"Please push select button to select several protiens first!", "Interrupted", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
		}
	}

	private class ExportAction extends AbstractAction {
		private ExportAction() {
		}

		public void actionPerformed(ActionEvent e) {
			pUtil.exportResults(alg, curalg, proteins, network);
		}
	}

	
	private class BrowserPanel extends JPanel {
		private final BrowserTableModel browserModel;
		private final JTable table;
		public BrowserPanel() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createTitledBorder("Proteins Browser"));

			this.browserModel = new BrowserTableModel();

			this.table = new JTable(this.browserModel);
			this.table.setSelectionMode(0);
			this.table.setDefaultRenderer(Object.class, new StringCellRenderer());
			this.table.setDefaultRenderer(Number.class, new NumberCellRenderer());
			this.table.setFocusable(false);
			this.table.getTableHeader().addMouseListener(new SortAction());
			this.table.getTableHeader().setToolTipText("Clicking the table header to choose one of the centralities to sort result protein table.");
			this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel rowSM = this.table.getSelectionModel();
			 rowSM.addListSelectionListener(new TableRowSelectionHandler());
			 table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane tableScrollPane = new JScrollPane(this.table);	
			
			tableScrollPane.getViewport().setBackground(Color.WHITE);
			add(tableScrollPane, "Center");
			
			fitTableColumns(table);
			
			
			
			
		}

		public int getSelectedRow() {
			return this.table.getSelectedRow();
		}

		public void update(ImageIcon image, int row) {
			this.table.setValueAt(image, row, 0);
		}

		
		JTable getTable() {
			return this.table;
		}
		
		BrowserTableModel getBrowserTableModel() {
			return this.browserModel;
		}
		
		
		public class StringCellRenderer extends DefaultTableCellRenderer{  
		    public StringCellRenderer(){  
		        setHorizontalAlignment(LEFT);  
		    }  	
		}
		public class NumberCellRenderer extends DefaultTableCellRenderer{
			  public NumberCellRenderer(){
			          setHorizontalAlignment(SwingConstants.LEFT);
			  }
			}
	}

	private class BrowserTableModel extends AbstractTableModel {
		private final String[] columnNames = new String[2+alg.size()];
		private Object[][] data;

		public BrowserTableModel() {
			columnNames[0] = "No.";
			columnNames[1] = "Name";
			int i = 2;
			for(String s : alg){
				columnNames[i] = s;
				i++;
			}
			listIt(proteins);
			
		}

		public void listIt(List<Protein> sproteins) {
			ResultPanel.this.exploreContent = new JPanel[ResultPanel.this.sproteins
					.size()];
			this.data = new Object[ResultPanel.this.sproteins.size()][this.columnNames.length];

			for (int i = 0; i < ResultPanel.this.sproteins.size(); i++) {
				Protein p = sproteins.get(i);
				this.data[i][0] = i + 1;
				this.data[i][1] = p.getName();
				int j = 2;
				for(String s : alg){
					this.data[i][j] = String.valueOf(p.getPara(s));
					j++;
				}
			}
		}

		public String getColumnName(int col) {
			return this.columnNames[col];
		}

		public int getColumnCount() {
			return this.columnNames.length;
		}

		public int getRowCount() {
			return this.data.length;
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
	}
/*
	private class EvaluationPanel extends JPanel {

		JLabel TPlabel = new JLabel("TP:", SwingConstants.CENTER);

		JLabel FPlabel = new JLabel("FP:", SwingConstants.CENTER);
		JLabel TNlabel = new JLabel("TN:", SwingConstants.CENTER);
		JLabel FNlabel = new JLabel("FN:", SwingConstants.CENTER);

		JLabel SNlabel = new JLabel("SN:", SwingConstants.CENTER);
		JLabel SPlabel = new JLabel("SP:", SwingConstants.CENTER);
		JLabel PPVlabel = new JLabel("PPV:", SwingConstants.CENTER);
		JLabel NPVlabel = new JLabel("NPV:", SwingConstants.CENTER);
		JLabel Flabel = new JLabel("F:", SwingConstants.CENTER);
		JLabel ACClabel = new JLabel("ACC:", SwingConstants.CENTER);

		JTextField TPtext = new JTextField("0.0 ");
		JTextField FPtext = new JTextField("0.0");
		JTextField TNtext = new JTextField("0.0");
		JTextField FNtext = new JTextField("0.0");

		JTextField SNtext = new JTextField("0.0");
		JTextField SPtext = new JTextField("0.0");
		JTextField PPVtext = new JTextField("0.0");
		JTextField NPVtext = new JTextField("0.0");
		JTextField Ftext = new JTextField("0.0");
		JTextField ACCtext = new JTextField("0.0");

		JPanel p1 = new JPanel(new GridLayout(5, 2));
		JPanel p2 = new JPanel(new GridLayout(5, 2));

		EvaluationPanel()

		{
			this.setLayout(new GridLayout(1, 2, 100, 0));
			p1.add(TPlabel);
			p1.add(TPtext);
			p1.add(TNlabel);
			p1.add(TNtext);
			p1.add(SNlabel);
			p1.add(SNtext);
			p1.add(PPVlabel);
			p1.add(PPVtext);
			p1.add(Flabel);
			p1.add(Ftext);

			p2.add(FPlabel);
			p2.add(FPtext);
			p2.add(FNlabel);
			p2.add(FNtext);
			p2.add(SPlabel);
			p2.add(SPtext);
			p2.add(NPVlabel);
			p2.add(NPVtext);
			p2.add(ACClabel);
			p2.add(ACCtext);

			this.add(p1);
			this.add(p2);
			update();
		}

		private int calEvaluationparams() {
			int flag = 1;
			double eplength = 0, splength = 0, alllength = 0, count = 0;
			if (eplist == null || eplist.isEmpty()) {
				flag = 0;
				return flag;
			}

			splength = sproteins.size();
			alllength = proteins.size();

			Iterator i1 = sproteins.iterator();
			while (i1.hasNext()) {
				Protein p = (Protein) i1.next();
				if (eplist.contains(p.getName()))
					count++;
			}

			Iterator i2 = proteins.iterator();
			while (i2.hasNext()) {
				Protein p = (Protein) i2.next();
				if (eplist.contains(p.getName()))
					eplength++;
			}

			TP = count;
			FP = splength - count;
			FN = eplength - count;
			TN = alllength - FN - TP - FP;

			SN = TP / (TP + FN);
			SP = TN / (TN + FP);
			PPV = TP / (TP + TN);
			NPV = TN / (TP + FN);
			F = (2 * SN * PPV) / (SN + PPV);
			ACC = (TP + TN) / alllength;
			return flag;
		}

		private void update() {
			if (calEvaluationparams() == 1) {
				this.TPtext.setText(String.valueOf(TP));
				this.FPtext.setText(String.valueOf(FP));
				this.FNtext.setText(String.valueOf(FN));
				this.TNtext.setText(String.valueOf(TN));
				this.SNtext.setText(String.valueOf(SN));
				this.SPtext.setText(String.valueOf(SP));
				this.PPVtext.setText(String.valueOf(PPV));
				this.NPVtext.setText(String.valueOf(NPV));
				this.Ftext.setText(String.valueOf(F));
				this.ACCtext.setText(String.valueOf(ACC));
				this.updateUI();
			}
		}
	}

	*/
	private class SelectAction extends AbstractAction {
		//JTable browserTable;
	//	ResultPanel.BrowserTableModel modelBrowser;
		JTextField selectT;
		int num = 0;

		
		SelectAction(JTextField selectT){
			this.selectT = selectT;
		}
		@Override
		public void actionPerformed(ActionEvent e) throws NumberFormatException {
			// TODO Auto-generated method stub
			String s = selectT.getText();
			int size = proteins.size();
			if(s != null && !s.trim().isEmpty()){
				s = s.trim();
				try{
					num = Integer.parseInt(s);
				}
				catch(NumberFormatException er){
					JOptionPane.showMessageDialog(null,
    	            		"Please input integer!", "Interrupted", JOptionPane.WARNING_MESSAGE);
    				return;
				}
				
				if(num > size){
					JOptionPane.showMessageDialog(null,
    	            		"Please input integer less than "+ size +"!", "Interrupted", JOptionPane.WARNING_MESSAGE);
    				return;
				}
					
				
				selectNum = num;
				sproteins = proteins.subList(0, num);
			
				if (ResultPanel.this.sproteins != null) {
					browserPanel.getTable().removeAll();
					browserPanel.getBrowserTableModel().listIt(sproteins);
					fitTableColumns(browserPanel.getTable());
					browserPanel.getBrowserTableModel().fireTableDataChanged();
					ResultPanel.this.browserPanel.updateUI();
					((JPanel) ResultPanel.this.browserPanel.getParent()).updateUI();
					updateNet();
					if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty()){
						evaluationPanel.update(selectNum);
						System.out.println("not null!");
					}
					System.out.println("null!");
						
				} else {
					System.err.println("list null");
				}
				
				
			}else{
				JOptionPane.showMessageDialog(null,
	            		"Please input integer less than "+ size +" first !", "Interrupted", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
		}
	}
	
	public void updateNet() {
		nodes = new ArrayList();
		Iterator i = sproteins.iterator();
		while (i.hasNext()) {
			Protein p = (Protein) i.next();
			nodes.add(p.getN());
		}
		pg = ResultPanel.this.pUtil.createGraph(network, nodes);
		selectProteins(pg.getSubNetwork());

	}
	
	private class SortAction implements MouseListener{
	
		@Override
		public void mouseClicked(MouseEvent e) 
        {  
			int tableColumn=browserPanel.getTable().columnAtPoint(e.getPoint());
			if(tableColumn > 1){
				String salg = alg.get(tableColumn-2);
				curalg = salg;
				pUtil.sortVertex(proteins, salg);
				sproteins = proteins.subList(0, selectNum);
				
				if (ResultPanel.this.sproteins != null) {
					browserPanel.getTable().removeAll();
					browserPanel.getBrowserTableModel().listIt(sproteins);
					fitTableColumns(browserPanel.getTable());
					browserPanel.getBrowserTableModel().fireTableDataChanged();
					setColumnColor(tableColumn);							
					ResultPanel.this.browserPanel.updateUI();
					((JPanel) ResultPanel.this.browserPanel.getParent()).updateUI();
					updateNet();
				} else {
					System.err.println("list null");
				}
			}
			
			
        }

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	public void setColumnColor(int n){
		
		JTable table;
		table = browserPanel.getTable();
	/*	TableColumn sc = browserPanel.getTable().getColumn(s);
		DefaultTableCellRenderer backFontColor = new DefaultTableCellRenderer();  
        backFontColor.setForeground(Color.red);   
        sc.setCellRenderer(backFontColor);  
*/
	/*	
		
		DefaultTableCellRenderer backFontColor1 = new DefaultTableCellRenderer();  
        backFontColor1.setForeground(Color.red); 
        DefaultTableCellRenderer backFontColor2 = new DefaultTableCellRenderer();  
        backFontColor2.setForeground(Color.black);
        
		
		for(int i = 2; i < table.getColumnCount(); i++){
			TableColumn sc = table.getColumn(alg.get(i-2));	
			if(i == n)
				sc.setCellRenderer(backFontColor1); 
			else
				sc.setCellRenderer(backFontColor2);
		}
		
		*/
		
		CellRenderer tcr = new CellRenderer(n);			
        //设置列表现器------------------------//
        for(int i = 0; i < browserPanel.browserModel.columnNames.length; i++) {
            table.getColumn(browserPanel.browserModel.columnNames[i]).setCellRenderer(tcr);
             	
        }
       
	}
	
	
	private class CellRenderer extends DefaultTableCellRenderer{
		int n;
		public CellRenderer( int n){
			super();
			this.n = n;			
		}
		 public Component getTableCellRendererComponent(JTable table, 
                 Object value, boolean isSelected, boolean hasFocus, 
                                            int row, int column) {
       if(column == n){
    	   setForeground(Color.red); 
       }else
    	   setForeground(Color.black);
    	        	
       if(row%2 == 0)
           setBackground(Color.white); //设置奇数行底色
       else if(row%2 == 1)
           setBackground(new Color(206,231,255));  //设置偶数行底色
           
       return super.getTableCellRendererComponent(table, value, 
                                 isSelected, hasFocus, row, column);
   }
		
   }
	
	
	
	public synchronized void ceateEvaluationPanel(){
		if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty()){
			
			evaluationPanel = new EvaluationPanel(proteins, selectNum, pUtil, network, networkView, resultId, alg);
			registrar.registerService(evaluationPanel,
					CytoPanelComponent.class,
					new Properties());
			CytoPanel cytopanel	= pUtil.getSouthCytoPanel();
		//	CytoPanel cytopanel2 = pUtil.getControlCytoPanel();
			if (cytopanel.indexOfComponent(evaluationPanel) >= 0)
			{
				int index = cytopanel.indexOfComponent(evaluationPanel);
				cytopanel.setSelectedIndex(index);
		//		cytopanel2.setState(CytoPanelState.FLOAT);
				if (cytopanel.getState() == CytoPanelState.HIDE) 
					cytopanel.setState(CytoPanelState.DOCK);
			}

			
		}
		else{
			evaluationPanel = new EvaluationPanel(resultId);
			registrar.registerService(evaluationPanel,
					CytoPanelComponent.class,
					new Properties());
		}
			
	}
	
	
	private class TableRowSelectionHandler implements ListSelectionListener
	{
		ArrayList<CyNode> selectednodes;
		private TableRowSelectionHandler()
		{
			selectednodes = new ArrayList<CyNode>();
		}

		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting()) 
				return;
			selectednodes.clear();
			
			int[] sr = browserPanel.getTable().getSelectedRows();
			if(sr.length != 0)
			for(int i=0 ; i<sr.length; i++){
				int selectedRow = sr[i];
				
				Protein p = sproteins.get(selectedRow);
				CyNode n = p.getN();
				selectednodes.add(n);
				pUtil.setSelected(selectednodes, network);
    		 
				networkView.fitContent();
				networkView.updateView();
				
			}
			
		}	
	}
	
	
	public EvaluationPanel geteEvaluationPanel(){
		return this.evaluationPanel;
	}
	
	public void fitTableColumns(JTable table) {
		JTableHeader header = table.getTableHeader();
		int rowCount = table.getRowCount();
		Enumeration columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) table.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(table,column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) table.getCellRenderer(row, col)
						.getTableCellRendererComponent(table,
								table.getValueAt(row, col), false, false, row,col).getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(width + table.getIntercellSpacing().width + 30);
			System.out.println("with : "+width +"    "+table.getIntercellSpacing().width);
		}
	}
	
	
	
	public class createChartAction extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			maxY = 0;
			 JFreeChart chart = ChartFactory.createBarChart3D("", "Value interval", "Number of proteins" ,calChartData(), PlotOrientation.VERTICAL, true, true, true);  
		        ChartFrame frame = new ChartFrame("", chart, true); 
		        chartfs.add(frame);
		        System.out.println(maxY+"^^^^^^^^");
		     // chart.setBackgroundPaint(Color.getHSBColor(23,192,223));  
		        chart.setBackgroundPaint(Color.WHITE);  
		        // 获得 plot：3dBar为CategoryPlot  
		        CategoryPlot categoryPlot = chart.getCategoryPlot();  
		        // 设定图表数据显示部分背景色  
		        categoryPlot.setBackgroundPaint(Color.lightGray);  
		        // 横坐标网格线  
		        categoryPlot.setDomainGridlinePaint(Color.RED);  
		        // 设置网格线可见  
		        categoryPlot.setDomainGridlinesVisible(true);  
		        // 纵坐标网格线  
		        categoryPlot.setRangeGridlinePaint(Color.RED);  
		        // 重要的类，负责生成各种效果  
		        // BarRenderer3D renderer=(BarRenderer3D) categoryPlot.getRenderer();  
		        // 获取纵坐标  
		        NumberAxis numberaxis = (NumberAxis) categoryPlot.getRangeAxis();  
		        // 设置纵坐标的标题字体和大小  
		        //numberaxis.setLabelFont(new Font("黑体", Font.CENTER_BASELINE, 24));  
		        // 设置丛坐标的坐标值的字体颜色  
		        numberaxis.setLabelPaint(Color.BLACK);  
		        // 设置丛坐标的坐标轴标尺颜色  
		        numberaxis.setTickLabelPaint(Color.RED);  
		        // 坐标轴标尺颜色  
		        numberaxis.setTickMarkPaint(Color.BLUE);  
		        // 丛坐标的默认间距值  
		        // numberaxis.setAutoTickUnitSelection(true);  
		        // 设置丛坐标间距值  
		     //   numberaxis.setAutoTickUnitSelection(false); 
		       
		        numberaxis.setTickUnit(new NumberTickUnit(Math.ceil(maxY/10)));  
		       
		        // 获取横坐标  
		        CategoryAxis domainAxis = categoryPlot.getDomainAxis();  
		        // 设置横坐标的标题字体和大小  
		      //  domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 13));  
		        // 设置横坐标的坐标值的字体颜色  
		        domainAxis.setTickLabelPaint(Color.RED);  
		        // 设置横坐标的坐标值的字体  
		       // domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 30));  
		        // 设置横坐标的显示  
		        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.4));  
		        // 这句代码解决了底部汉字乱码的问题  
		        chart.getLegend().setItemFont(new Font("黑体", 0, 16));  
		        // 设置图例标题  
		        Font font = new java.awt.Font("黑体", java.awt.Font.CENTER_BASELINE, 50);  
		        TextTitle title = new TextTitle("");  
		        title.getBackgroundPaint();  
		        title.setFont(font);  
		        // 设置标题的字体颜色  
		        title.setPaint(Color.RED);  
		        chart.setTitle(title);  
		        
		        
		        
		        
		        frame.pack();  
		        frame.setVisible(true); 
			
			
		}
		
	}
	

	
	
	public DefaultCategoryDataset calChartData(){
		//HashMap<Float, Integer> chartdataMap = new HashMap<Float, Integer>();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		ArrayList<Float> a = new ArrayList<Float>();
		ArrayList<Float> b = new ArrayList<Float>();
		
		DecimalFormat df;
		float max = (float) proteins.get(0).getPara(curalg);
		float min = (float) proteins.get(proteins.size()-1).getPara(curalg);
		int avg = proteins.size()/20;
		
		float interval = (max - min)/20;
		
		
		
		float[]  chartdata = calDateList(proteins,20);
		
		float tempin = 0;
		float tempsum = 0;
		
		for(int j=0; j<20; j++){
				

				if(chartdata[j] < avg && j !=0 && a.get(a.size()-1) < 2*avg){
					tempin += interval;
					tempsum += chartdata[j];
					
					a.set(a.size()-1, a.get(a.size()-1) + chartdata[j]);
					b.set(b.size()-1, b.get(b.size()-1) + interval);
				} 
				
				else if(chartdata[j] > 5*avg){
					//float t = chartdata[j]/avg;
					float t = 20;
					
					float[]  subchartdata = calDateList( proteins.subList((int)tempsum , (int)(tempsum + chartdata[j])), t);
					for(int i=0; i<20; i++){
						
						tempin += interval/t;
						tempsum += subchartdata[i];
						
						if(subchartdata[i] < avg && i !=0){
							a.set(a.size()-1, a.get(a.size()-1) + subchartdata[i]);
							b.set(b.size()-1, b.get(b.size()-1) + (interval/t));
						}
						else{
							a.add(subchartdata[i]);
							b.add(interval/t);
						}
						
					}
				
				
				}
				
				else{
					tempin += interval;
					tempsum += chartdata[j];
					
					//System.out.println(tempsum - chartdata[j]+"    $$     "+tempsum);
				//	System.out.println(tempin - interval+"    $$     "+tempin);
					a.add(chartdata[j]);
					b.add(interval);
				}
	
				
	
		}
		
		
		tempin = 0;
		ArrayList<String> temparr = new ArrayList<String>();
		for(int j=0; j<a.size(); j++){
		
				if(a.get(j) > maxY)
					maxY =  a.get(j).intValue();
				
				tempin += b.get(j);	
				
				
				interval = b.get(j);	
				if(curalg != ParameterSet.IC){
					if(interval < 0.01)
						df = new DecimalFormat("#.####");
					else if(interval < 0.1)
						df = new DecimalFormat("#.##");
					else 
						df = new DecimalFormat("#.#");
					
					temparr.add( df.format(max-tempin) +"- "+df.format(max-(tempin-b.get(j))));
				//	dataset.addValue(a.get(j).intValue(), curalg, df.format(max-(tempin-b.get(j))) +"- "+ df.format(max-tempin));
				}
				else{
					//df = new DecimalFormat("0.00000E");  
					//dataset.addValue(a.get(j).intValue(), curalg, df.format(max-tempin+b.get(j)) +"- "+ df.format(max-tempin));
				//	dataset.addValue(a.get(j).intValue(), curalg, (max-tempin+b.get(j)) +"- "+ (max-tempin));
					temparr.add((max-tempin) +"- "+(max-tempin+b.get(j)) );
				}
		}
		
		for(int j=a.size()-1; j>=0; j--){
			dataset.addValue(a.get(j).intValue(), curalg, temparr.get(j));
		}
		
		return dataset;
	}
	
	
	
	
	public float[] calDateList( List<Protein> proteins, float t){
		float[] chartdata = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		float max = (float) proteins.get(0).getPara(curalg);
		float min = (float) proteins.get(proteins.size()-1).getPara(curalg);
		
		
		float interval = (max - min)/t;
		int i = 1;
		
		
		
		for(Protein p : proteins){
			if(p.getPara(curalg) >= (max-interval*i)){
				chartdata[i-1]++;	
			}
			else{
				while(true){
					i++;
					float fi = max-interval*i;
					if(i == 20)
						 fi = min;
						
					if(p.getPara(curalg) >= fi){
						chartdata[i-1]++;
						break;
					}
				}
			}
		}
		
		
		
		return chartdata;
	}
	
}
