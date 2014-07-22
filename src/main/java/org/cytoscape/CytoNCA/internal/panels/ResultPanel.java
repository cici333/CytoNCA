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
import java.text.Format;
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


	private final int resultId;
	private ArrayList<String> alg;
	private String curalg;
	private final List<Protein> proteins;
	private List<Protein> sproteins;
	//private List<CyNode> nodes;
	//private ProteinGraph pg;
	private final CyNetwork network;
	private CyNetworkView networkView;
	private CollapsiblePanel explorePanel;
	private JPanel[] exploreContent;
	private JButton closeButton;
	private ParameterSet currentParamsCopy;

	public BrowserPanel browserPanel;
	private EvaluationPanel evaluationPanel;
	private AnalysisPanel analysisPanel;
	private ProteinUtil pUtil;
	private final DiscardResultAction discardResultAction;

	private ArrayList<String> eplist;
	//private List<Integer> indexs;
	private int selectNum = 0;

	public ArrayList<ChartFrame> chartfs;
	private int maxY;
	private HashMap<String, List<Protein>> sortResults;
	public static JComboBox<String> algselect;
	public boolean issortwholenet = true;
	private String curSetName;
	
	public ResultPanel(ArrayList<Protein> resultL, ArrayList<String> alg,
			ProteinUtil pUtil, CyNetwork network, CyNetworkView networkView,
			int resultId, DiscardResultAction discardResultAction) {
		setLayout(new BorderLayout());

		this.pUtil = pUtil;
		this.alg = alg;
		this.curalg = alg.get(0);
		
	
		
		this.resultId = resultId;
		this.proteins = Collections.synchronizedList(resultL);
		this.network = network;
		SortResults();		
		this.sproteins = pUtil.copyList(sortResults.get(curalg));
		selectNum = this.sproteins.size();
		this.curSetName = this.curalg +"(Top"+ sproteins.size() +")";
		System.out.println(curSetName);
		pUtil.SaveInTable(alg, proteins, network);
		
		chartfs = new ArrayList<ChartFrame>();
		
		this.networkView = networkView;
		this.discardResultAction = discardResultAction;
		this.currentParamsCopy = pUtil.getCurrentParameters().getResultParams(
				resultId);
		this.eplist = pUtil.getAlleprotein();
		this.evaluationPanel = null;

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.browserPanel = new BrowserPanel();
		StringBuffer sb = new StringBuffer("Result List( ");
		sb.append(proteins.size());
		sb.append(" in total )");
		this.browserPanel.setBorder(BorderFactory.createTitledBorder(sb
				.toString()));

	

		this.setPreferredSize(new Dimension(400, 700));

		add(this.browserPanel);
		add(createBottomPanel());

		setColumnColor(2);
		ceateAnalysisPanel();

	}

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.jpg"));
		return icon;
	}

	public String getTitle() {
		return "Result " + getResultId();
	}

	public int getResultId() {
		return this.resultId;
	}

	

	public CyNetwork getNetwork() {
		return this.network;
	}

	public int getSelectedClusterRow() {
		return this.browserPanel.getSelectedRow();
	}
	
	

	private synchronized void SortResults(){
		sortResults = new HashMap<String, List<Protein>>();
		  for(String s : alg){
			  List<Protein> temp = pUtil.copyList(proteins);
			  pUtil.sortVertex(temp, s);
			  sortResults.put(s, temp);
		  }
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
			//	pUtil.deleteDiskFiles();
			}
		});
	}
	

	
	

	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ExportAction());
		exportButton.setToolTipText("Export result set to a text file");

		this.closeButton = new JButton(this.discardResultAction);
		this.discardResultAction.putValue("requestUserConfirmation",
				Boolean.valueOf(true));

		JButton columnchartButton = new JButton("Value Distribution");
		columnchartButton.addActionListener(new createChartAction());
		columnchartButton.setToolTipText("Visualizing the distribution of the selected centrality’ value with histogram.");
		
		buttonPanel.add(exportButton);	
		buttonPanel.add(columnchartButton);
		buttonPanel.add(this.closeButton);
		

		
		panel.add(createSelectByRankPanel(), "North");
		panel.add(createSelectByValuePanel(), "Center");
		panel.add(buttonPanel, "South");
		
       
		return panel;
	}

	private JPanel createSelectPanel(){
		JPanel panel = new JPanel();
		panel.add(createSelectByRankPanel());
		panel.add(createSelectByValuePanel());
		return panel;
	}
	private JPanel createSelectByRankPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			
		JPanel panel2 = new JPanel();		
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
 
		JTextField selectT = new JTextField(6);
		selectT.setText(proteins.size()+"");
		
		JTextField selectTP = new JTextField(4);
		selectTP.setText("100");
		
		panel2.add(new JLabel("Top"));
		panel2.add(selectT);
		panel2.add(new JLabel("Proteins"));
		
		JButton select  = new JButton("Select");
		select.addActionListener(new SelectByRankAction(selectT, selectTP, 0));
		panel2.add(select);
		panel2.setBorder(BorderFactory.createTitledBorder(""));
	
		panel3.add(new JLabel("Top"));
		panel3.add(selectTP);
		panel3.add(new JLabel("% Proteins"));
		
		JButton selectP  = new JButton("Select");
		selectP.addActionListener(new SelectByRankAction(selectT, selectTP, 1));
		panel3.add(selectP);
		panel3.setBorder(BorderFactory.createTitledBorder(""));
		
		panel.setBorder(BorderFactory.createTitledBorder("Select by rank"));
		panel.add(panel2);
		panel.add(panel3);
	
		return panel;
	}

	private JPanel createSelectByValuePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		algselect = new JComboBox<String>();
		JTextField maxv = new JTextField(4);
		JTextField minv = new JTextField(4);
		
		
		for(String alg : sortResults.keySet())
			algselect.addItem(alg);
		algselect.addActionListener(new SelectAlgAction(maxv, minv));
		
		JButton selectB = new JButton("Select");
		selectB.addActionListener(new SelectByValueAction(maxv,minv));
		
		panel.add(algselect);
		panel.add(new JLabel(" Max "));
		panel.add(maxv);
		panel.add(new JLabel(" ~ Min "));
		panel.add(minv);
		panel.add(selectB);
		
		panel.setBorder(BorderFactory.createTitledBorder("Select by value"));
		algselect.setSelectedIndex(0);
		return panel;
	}
	
	private class SelectByValueAction extends AbstractAction {
		JTextField maxv,minv;
		
		
		SelectByValueAction(JTextField maxv, JTextField minv){
			this.maxv = maxv;
			this.minv = minv;
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			double max=0, min=0;
			String ca = (String) ResultPanel.algselect.getSelectedItem();
			List<Protein> l =  sortResults.get(ca);
		
			if(maxv.getText() != null && minv.getText() != null &&!maxv.getText().trim().isEmpty() && !minv.getText().trim().isEmpty()){
				try{
					max = Double.parseDouble(maxv.getText().trim());
					min = Double.parseDouble(minv.getText().trim());
				}
				catch(NumberFormatException er){
					JOptionPane.showMessageDialog(null,
		            		"Please input integer or decimal!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if(max < min ){
					JOptionPane.showMessageDialog(null,
		            		"Please input legal value!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				curalg = ca;
				curSetName = curalg+"["+max+"~"+min+"]";
				System.out.println(curSetName);
				sproteins.clear();
				for(Protein p :l){
					if(p.getPara(ca) <= max)
						sproteins.add(p);
					if(p.getPara(ca) < min)
						break;				
				}
				
				
				if (ResultPanel.this.sproteins != null) {
					browserPanel.updateTable(browserPanel.browserModel.getColumnIndex(ca));
					browserPanel.changeSortingRange(false);
					

				} else {
					System.err.println("list null");
				}
				
			}else{
				JOptionPane.showMessageDialog(null,
	            		"Please input integer or decimal first!", "Interrupted", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			
			
			
		}
		
	}
	private class SelectAlgAction extends AbstractAction{
		JTextField maxv,minv;
		
		SelectAlgAction(JTextField maxv, JTextField minv){
			this.maxv = maxv;
			this.minv = minv;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JComboBox<String> jcb = (JComboBox<String>) e.getSource();
			String ca = (String) jcb.getSelectedItem();
			List<Protein> l =  sortResults.get(ca);
			maxv.setText(l.get(0).getPara(ca)+"");
			minv.setText(l.get(l.size()-1).getPara(ca)+"");
						
		}
		
	}
	
	private class ExportAction extends AbstractAction {
		private ExportAction() {
		}

		public void actionPerformed(ActionEvent e) {
			pUtil.exportResults(alg, curalg, sortResults.get(curalg), network);
		}
	}

	
	public class BrowserPanel extends JPanel {
		private final BrowserTableModel browserModel;
		private final JTable table;
		private JRadioButton Sortselectnodes, Sortwholenodes;
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
			
			Sortselectnodes = new JRadioButton("Sorting in select nodes");
			Sortselectnodes.addItemListener(new SortingrangeAction());
			
			Sortwholenodes = new JRadioButton("Sorting in whole network");
			Sortwholenodes.addItemListener(new SortingrangeAction());
			Sortwholenodes.setSelected(true);
			
			ButtonGroup sbg = new ButtonGroup();
			sbg.add(Sortwholenodes);
			sbg.add(Sortselectnodes);
			
			JPanel spanel = new JPanel();
			spanel.setLayout(new BoxLayout(spanel, BoxLayout.X_AXIS));
			spanel.add(Sortselectnodes);
			spanel.add(Sortwholenodes);
			
			add(spanel,"North");
			add(tableScrollPane, "Center");
			
			fitTableColumns(table);
			
			
			
			
		}
		
		public void updateTable(Integer tableColumn ){
			browserPanel.getTable().removeAll();
			browserPanel.getBrowserTableModel().listIt(sproteins);
			fitTableColumns(browserPanel.getTable());
			browserPanel.getBrowserTableModel().fireTableDataChanged();
			if(tableColumn != null)
				setColumnColor(tableColumn);
			ResultPanel.this.browserPanel.updateUI();
			((JPanel) ResultPanel.this.browserPanel.getParent()).updateUI();
			analysisPanel.updateNet(curalg, curSetName);
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
				header.setResizingColumn(column); 
				column.setWidth(width + table.getIntercellSpacing().width + 30);
				
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
				listIt(sproteins);
				
			}

			public void listIt(List<Protein> sproteins) {
				ResultPanel.this.exploreContent = new JPanel[ResultPanel.this.sproteins
						.size()];
				this.data = new Object[sproteins.size()][this.columnNames.length];

				for (int i = 0; i < sproteins.size(); i++) {
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
			
			public Integer getColumnIndex(String name) {
				
				for(int i=2; i < columnNames.length; i++){
					if(this.columnNames[i].equals(name)){
						return i;
					}
				}
				return null;
			}
			
			
		}
	
	
		private class SortingrangeAction implements ItemListener{

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				JRadioButton jr = (JRadioButton) e.getSource();
				if(jr.isSelected()){
					System.out.println(jr.getText());
					if(jr.getText().equals("Sorting in select nodes"))
						issortwholenet = false;
					if(jr.getText().equals("Sorting in whole network"))
						issortwholenet = true;
				}
			}

					
		}
	
		public void changeSortingRange(boolean issortwhole){
			if(issortwhole){
				Sortwholenodes.setSelected(true);
			}else
				Sortselectnodes.setSelected(true);
		}
	}



	private class SelectByRankAction extends AbstractAction {
		//JTable browserTable;
	//	ResultPanel.BrowserTableModel modelBrowser;
		JTextField selectT;
		JTextField selectTP;
		int num = 0;
		int flag = 0;// 0 by rank ;1 by percent
		
		SelectByRankAction(JTextField selectT,JTextField selectTP,int flag){
			this.selectT = selectT;
			this.selectTP = selectTP;
			this.flag = flag;
			
			
		}
		@Override
		public void actionPerformed(ActionEvent e) throws NumberFormatException {
			// TODO Auto-generated method stub
			String s = null;
			if(flag == 0)
				 s = selectT.getText();
			if(flag == 1)
				s = selectTP.getText();
			
			int size = proteins.size();
			if(s != null && !s.trim().isEmpty()){
				s = s.trim();
				try{
					if(flag == 0)
						num = Integer.parseInt(s);
					if(flag ==1)
						num = (int) Math.ceil(Double.parseDouble(s) * size * 0.01);
				}
				catch(NumberFormatException er){
					if(flag == 0)
						JOptionPane.showMessageDialog(null,
    	            		"Please input integer!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					if(flag ==1)
						JOptionPane.showMessageDialog(null,
	    	            		"Please input integer or decimal!", "Interrupted", JOptionPane.WARNING_MESSAGE);
    				return;
				}
				
				if(num > size ){
					if(flag == 0)
					JOptionPane.showMessageDialog(null,
    	            		"Please input integer less than "+ size +"!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					if(flag ==1)
						JOptionPane.showMessageDialog(null,
	    	            		"Please input integer or decimal less than 100 as percentage!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
					
				if(flag == 0)
					selectTP.setText(String.format( "%.1f", (double)num/(double)size *100 ));
				if(flag ==1)
					selectT.setText(num+"");
				
				
				sproteins.clear();			
				selectNum = num;
				curSetName = curalg+"(Top"+num+")";
				System.out.println(curSetName+"^^^");
				List<Protein> temp = sortResults.get(curalg).subList(0, num);
				for(Protein p : temp){
					sproteins.add(p);
					
				}
				
			
			
				if (ResultPanel.this.sproteins != null) {
					browserPanel.updateTable(null);
					if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty() && evaluationPanel != null){
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
	
	/*public void updateNet() {
		nodes = new ArrayList();
		Iterator i = sproteins.iterator();
		while (i.hasNext()) {
			Protein p = (Protein) i.next();
			nodes.add(p.getN());
		}
		pg = ResultPanel.this.pUtil.createGraph(network, nodes);
		selectProteins(pg.getSubNetwork());

	}
	*/
	private class SortAction implements MouseListener{
	
		@Override
		public void mouseClicked(MouseEvent e) 
        {  
			int tableColumn=browserPanel.getTable().columnAtPoint(e.getPoint());
			if(tableColumn > 1){
				String salg = alg.get(tableColumn-2);
				curalg = salg;
				
				
			//	pUtil.sortVertex(proteins, salg);
				if(issortwholenet){
					sproteins.clear();
					List<Protein> temp = sortResults.get(curalg).subList(0, selectNum);
					for(Protein p : temp){
						sproteins.add(p);
						curSetName = curalg+"(Top"+selectNum+")";
					}
				}
				else{
					pUtil.sortVertex(sproteins, curalg);
				}
				
				
					
				
				if (ResultPanel.this.sproteins != null) {
					algselect.setSelectedItem(curalg);
					browserPanel.updateTable(tableColumn);
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
		if(evaluationPanel == null){
			if(pUtil.getAlleprotein() != null && !pUtil.getAlleprotein().isEmpty()){
				
				
				evaluationPanel = new EvaluationPanel(proteins, selectNum, pUtil, network, networkView, resultId, alg, sortResults);
				pUtil.getRegistrar().registerService(evaluationPanel,
						CytoPanelComponent.class,
						new Properties());
				CytoPanel cytopanel	= pUtil.getSouthCytoPanel();
		
				if (cytopanel.indexOfComponent(evaluationPanel) >= 0)
				{
					int index = cytopanel.indexOfComponent(evaluationPanel);
					cytopanel.setSelectedIndex(index);
		
					if (cytopanel.getState() == CytoPanelState.HIDE) 
						cytopanel.setState(CytoPanelState.DOCK);
				}

				
			}
			else{
				
				JOptionPane.showMessageDialog(null,
	            		"Please upload essential protein list first!", "Interrupted", JOptionPane.WARNING_MESSAGE);
				return;
				
			}
		}
		else{
			CytoPanel cytopanel	= pUtil.getSouthCytoPanel();
			int index = cytopanel.indexOfComponent(evaluationPanel);
			cytopanel.setSelectedIndex(index);
		}
		
			
	}
	
	
	public synchronized void ceateAnalysisPanel(){
		
			
			analysisPanel = new AnalysisPanel(resultId, network, networkView, sortResults, pUtil, eplist, curalg, sproteins, curSetName);
			pUtil.getRegistrar().registerService(analysisPanel,
					CytoPanelComponent.class,
					new Properties());
			CytoPanel cytopanel	= pUtil.getSouthCytoPanel();
		
			if (cytopanel.indexOfComponent(analysisPanel) >= 0)
			{
				int index = cytopanel.indexOfComponent(analysisPanel);
				cytopanel.setSelectedIndex(index);
		
				if (cytopanel.getState() == CytoPanelState.HIDE) 
					cytopanel.setState(CytoPanelState.DOCK);
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
	
	
	public EvaluationPanel getEvaluationPanel(){
		return this.evaluationPanel;
	}
	public void setEvaluationPanel(EvaluationPanel e){
		this.evaluationPanel = e;
	}
	public AnalysisPanel geteAnalysisPanel(){
		return this.analysisPanel;
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
		
	
		public DefaultCategoryDataset calChartData(){
			//HashMap<Float, Integer> chartdataMap = new HashMap<Float, Integer>();
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			
			ArrayList<Float> a = new ArrayList<Float>();
			ArrayList<Float> b = new ArrayList<Float>();
			
			DecimalFormat df;
		//	List<Protein> temp = sortResults.get(curalg);
			List<Protein> temp = sproteins;
			float max = (float) temp.get(0).getPara(curalg);
			float min = (float) temp.get(temp.size()-1).getPara(curalg);
			int avg = temp.size()/20;
			
			float interval = (max - min)/20;
			
			
			
			float[]  chartdata = calDateList(temp,20);
			
			float tempin = 0;
			float tempsum = 0;
			
			for(int j=0; j<20; j++){
					
				/*
					if(chartdata[j] < avg && j !=0 && a.get(a.size()-1) < 2*avg){
						tempin += interval;
						tempsum += chartdata[j];
						
						a.set(a.size()-1, a.get(a.size()-1) + chartdata[j]);
						b.set(b.size()-1, b.get(b.size()-1) + interval);
					} 
					
					else if(chartdata[j] > 5*avg){
						//float t = chartdata[j]/avg;
						float t = 20;
						
						float[]  subchartdata = calDateList( temp.subList((int)tempsum , (int)(tempsum + chartdata[j])), t);
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
					*/
						tempin += interval;
						tempsum += chartdata[j];
						
						//System.out.println(tempsum - chartdata[j]+"    $$     "+tempsum);
					//	System.out.println(tempin - interval+"    $$     "+tempin);
						a.add(chartdata[j]);
						b.add(interval);
				//	}
		
					
		
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
	

	
	

	
}
