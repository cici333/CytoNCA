package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;


import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.Resources;
import org.cytoscape.CytoNCA.internal.Resources.ImageName;
import org.cytoscape.CytoNCA.internal.actions.DiscardEpListAction;
import org.cytoscape.CytoNCA.internal.actions.DiscardEvaluationAction;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EvaluationPanel extends JPanel implements CytoPanelComponent
{
private	List<Protein> proteins; 
private List<Protein> sproteins;
private CyNetwork network;
private final int evaluationId;
private JButton closeButton;
private ProteinUtil pUtil;
//private final DiscardEvaluationAction discardEvaluationAction;
private static final Logger logger = LoggerFactory.getLogger(ResultPanel.class);
public BrowserPanel browserPanel;
private CyNetworkView networkView;
private double TP, FP, TN, FN, SN, SP, PPV, NPV, F, ACC;
private ArrayList<String> eplist;
private ArrayList<String> alg;
private HashMap<String, ArrayList<Double>> paraSets;
private int selectnum = 0;
private HashMap<String, List<Protein>> sortResults;
private int allsize = 0;

private HashMap<Integer, HashMap<String, ArrayList<Double>>> ChartMap; 
private ArrayList<Integer> percentagesNum; 
private ArrayList<Integer> percentages;
private int epsize = 0;
private Double maxY;
public ArrayList<ChartFrame> chartfs;
public ArrayList<String> selectedAlgs;
public int  pIndex = -1;
private XYSeries idealxys;
private HashMap<String, Integer> AUCmap;
private HashMap<String, ArrayList<Integer>> eproteinNumMap;
private DiscardEvaluationAction discardevaluationaction;
private JButton discard;
public EvaluationPanel(int evaluationId){
	this.setVisible(false);
	this.pUtil = null;
	  this.evaluationId = evaluationId;
	  this.network =null;
	  this.networkView = null;
	  this.alg = null;
	  this.eplist = null;
	  this.proteins = null;
	  this.sproteins = null;
	 
}

public EvaluationPanel(List<Protein> proteins, int selectnum,  ProteinUtil pUtil, CyNetwork network, CyNetworkView networkView, int evaluationId, ArrayList<String> alg, HashMap<String, List<Protein>> sortResults)
{
  //setLayout(new BorderLayout());


  this.pUtil = pUtil;
  this.evaluationId = evaluationId;
  this.network = network; 
  this.networkView = networkView;
  this.alg = alg;
  this.eplist = pUtil.getAlleprotein();
  this.proteins = proteins;
  this.selectnum = selectnum;
  this.sortResults = sortResults;
  AUCmap = new HashMap<String, Integer>(); 
  eproteinNumMap = new HashMap<String, ArrayList<Integer>>();
  allsize = proteins.size();
  setPercentageList();
  initialChartMap();
  chartfs = new ArrayList<ChartFrame>();
  
  this.browserPanel = new BrowserPanel();
  
  JPanel buttonpanel = CreateButtonPanel();
  
  this.setPreferredSize(new Dimension(400, 600));
  this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
  add(buttonpanel);
  add(this.browserPanel);


  

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
 
	ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.jpg"));
	return icon;
}

public String getTitle()
{
  return "Evaluation Panel " + getEvaluationId();
}

public int getEvaluationId() {
  return this.evaluationId;
}



public CyNetwork getNetwork() {
  return this.network;
}



public BrowserPanel getBrowserPanel() {
	return this.browserPanel;
}

public void discard(final boolean requestUserConfirmation) {
	  SwingUtilities.invokeLater(new Runnable()
	  {
	    public void run()
	    {
	      boolean oldRequestUserConfirmation = Boolean.valueOf(EvaluationPanel.this.discardevaluationaction
	        .getValue("requestUserConfirmation").toString()).booleanValue();

	      EvaluationPanel.this.discardevaluationaction.putValue("requestUserConfirmation", 
	        Boolean.valueOf(requestUserConfirmation));
	      EvaluationPanel.this.discard.doClick();
	      EvaluationPanel.this.discardevaluationaction.putValue("requestUserConfirmation", 
	        Boolean.valueOf(oldRequestUserConfirmation));
	    }
	  });
	}

private void setPercentageList(){
	  
	  percentagesNum = new ArrayList<Integer>();
	  percentages = new ArrayList<Integer>();
	  
	  percentages.add(1);
	  percentages.add(5);
	  percentages.add(10);
	  percentages.add(15);
	  percentages.add(20);
	  percentages.add(25);
	  
	  percentagesNum.add( (int) Math.round(allsize * 0.01));
	  percentagesNum.add( (int) Math.round(allsize * 0.05));
	  percentagesNum.add( (int) Math.round(allsize * 0.10));
	  percentagesNum.add( (int) Math.round(allsize * 0.15));
	  percentagesNum.add( (int) Math.round(allsize * 0.20));
	  percentagesNum.add( (int) Math.round(allsize * 0.25));
	  
}



private void initialChartMap(){
	ChartMap = new HashMap<Integer, HashMap<String, ArrayList<Double>>>();
	  for(int i : percentagesNum){
		  HashMap<String, ArrayList<Double>> p = new HashMap<String, ArrayList<Double>>();
		  for(String s : alg){
			  p.put(s, new ArrayList<Double>());
		  }
		  ChartMap.put(i, p);
	  }
}


private JPanel CreateButtonPanel(){
	  JPanel buttonpanel = new JPanel();
	  buttonpanel.setLayout(new GridLayout(3,1));
	  
	  JPanel p1 = new JPanel();
	  JPanel p2 = new JPanel();
	  JPanel p3 = new JPanel();
 

	  
	  p1.setBorder(BorderFactory.createEtchedBorder());
	  p2.setBorder(BorderFactory.createEtchedBorder());
	  p3.setBorder(BorderFactory.createEtchedBorder());
	 
	  
	  JButton createChar = new JButton();
	  JButton createlineChart = new JButton();
	  discard = new JButton();
	  
	  createChar.setPreferredSize(new Dimension(110,25));
	  createlineChart.setPreferredSize(new Dimension(110,25));
	  discard.setPreferredSize(new Dimension(110,25));
	 
	  

	  
	  
	  createChar.setAction(new CreateChart());
	  createlineChart.setAction(new CreateLineChart());
	  //discard.setAction(new DiscardAction());
	  
	  discardevaluationaction = new DiscardEvaluationAction(EvaluationPanel.this, pUtil);
	  discardevaluationaction.putValue("requestUserConfirmation",
				Boolean.valueOf(true));
	  discard.setAction(discardevaluationaction);
	  
	  createChar.setMargin(new Insets(0, 0, 0, 0));
	  createlineChart.setMargin(new Insets(0, 0, 0, 0));
	  discard.setMargin(new Insets(0, 0, 0, 0));
	  
	  createChar.setText("Create Column Chart");
	  createChar.setToolTipText("To show the statistical measure values of selected centralities with column chart.");
	  createlineChart.setText("Create Line Chart");
	  createlineChart.setToolTipText("To show essential proteins number of selected centralities with column chart.");
	  discard.setText("Discard");
	  
	
	  p1.add(createChar);
	  p2.add(createlineChart);
	  p3.add(discard);
	  
	  buttonpanel.setPreferredSize(new Dimension(130,600));
	  buttonpanel.add(p1);
	  buttonpanel.add(p2);
	  buttonpanel.add(p3);
	  return buttonpanel;
}


/*
	private JPanel createBottomPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		
	//	this.closeButton = new JButton(this.discardEvaluationAction);
	//	this.discardEvaluationAction.putValue("requestUserConfirmation",
				Boolean.valueOf(true));
		JButton resetnetview  = new JButton("Reset NetworkView");
		resetnetview.addActionListener(new resetnetviewAction());
		buttonPanel.add(resetnetview);
		buttonPanel.add(this.closeButton);
		panel.add(buttonPanel,"Center");

		return panel;
	}
	
*/
	public class BrowserPanel extends JPanel {
		private final BrowserTableModel browserModel;
		private final JTable table;

		public BrowserPanel() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createTitledBorder("Essential Protein Browser"));
			this.browserModel = new BrowserTableModel();
			this.table = new JTable(this.browserModel){
				
				protected JTableHeader createDefaultTableHeader() {  
					
                    return new JTableHeader(columnModel) {   
                    	
                        public String getToolTipText(MouseEvent e) {   
                            String tip = null;   
                            java.awt.Point p = e.getPoint();   
                            int index = columnModel.getColumnIndexAtX(p.x);   
                            int realIndex =  columnModel.getColumn(index).getModelIndex();    
                            
                            if(index == 1)
		                		return "TP, True Positives: The number of essential protein which are correctly identified as essential protein.";
		                	if(index == 2)
		                		return "FP, False Positives: The number of non-essential protein which are incorrectly identified as essential protein.";
		                	if(index == 3)
		                		return "TN, True Negatives: The number of non-essential protein which are correctly identified as non-essential protein.";
		                	if(index == 4)
		                		return "FN, False Negatives: The number of essential protein which are incorrectly identified as non-essential protein.";
		                	if(index == 5)
		                		return "<html>SN, Sensitivity: The proportion of essential protein which are correctly identified." +
		                				"<br>SN = TP / (TP+FN)</html>";
		                	if(index == 6)
		                		return "<html>SP, Specificity: The proportion of non-essential protein which are correctly removed." +
		                				"<br> SP = TN / (TN+FP) </html>";
		                	if(index == 7)
		                		return "<html>PPV, Positive Predictive Value: The proportion of selected protein which are correctly identified as essential protein." +
		                				"<br>PPV = TP / (TP+FP)</html>";
		                	if(index == 8)
		                		return "<html>NPV, Negative Predictive Value: The proportion of removed protein which are correctly identified as non-essential protein. " +
		                				"<br>NPV = TN / (TN+FN)</html> ";
		                	if(index == 9)
		                		return "<html>F, F-measure: The harmonic mean of sensitivity and positive predictive value." +
		                				"<br>F = 2*SN*PPV / (SN+PPV)</html>  ";
		                	if(index == 10)
		                		return "<html>ACC, Accuracy: The proximity of measurement results to the true value." +
		                				"<br>ACC = (TP+TN) / (P+N)</html>";
		                	else 
		                		return "";
                            
                        }   
                    };   
                }   
				
				
			};
			this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel rowSM = this.table.getSelectionModel();
			 rowSM.addListSelectionListener(new SelectionHandler());
			 table.setRowSelectionAllowed(false);
			 table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			 
			JScrollPane tableScrollPane = new JScrollPane(this.table);
		//	tableScrollPane.setHorizontalScrollBarPolicy();
			tableScrollPane.getViewport().setBackground(Color.WHITE);

			add(tableScrollPane, "Center");
			
		//	DefaultTableCellRenderer backFontColor = new DefaultTableCellRenderer();  
	 //       backFontColor.setForeground(Color.red);      			
	//		table.getColumn("  ").setCellRenderer(backFontColor);
		//	table.setFocusable(false);	
			
			
			 DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
		            public Component getTableCellRendererComponent(JTable table, 
		                          Object value, boolean isSelected, boolean hasFocus, 
		                                                     int row, int column) {
		                	            			            	
		            	if(column == 0){
		                	setForeground(Color.red);
		                } 
		                else
		                	setForeground(Color.black); 
		                if(row%2 == 0)
		                    setBackground(Color.white); //设置奇数行底色
		                else if(row%2 == 1)
		                    setBackground(new Color(206,231,255));  //设置偶数行底色
		                    
		                return super.getTableCellRendererComponent(table, value, 
		                                          isSelected, hasFocus, row, column);
		            }
		        };
		        //设置列表现器------------------------//
		        for(int i = 0; i < browserModel.columnNames.length; i++) {
		            table.getColumn(browserModel.columnNames[i]).setCellRenderer(tcr);
		        }
			
		        fitTableColumns(table);
			
		}


		JTable getTable() {
			return this.table;
		}
		BrowserTableModel getBrowserTableModel() {
			return this.browserModel;
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
				header.setResizingColumn(column); // 此行很重要
				column.setWidth(width + table.getIntercellSpacing().width + 30);
				System.out.println("with : "+width +"    "+table.getIntercellSpacing().width);
			}
		}


	
	
	private class BrowserTableModel extends AbstractTableModel {
		private final String[] columnNames = {"  ", "TP", "FP", "TN", "FN", "SN", "SP", "PPV", "NPV", "F", "ACC"};
		private Object[][] data;

		public BrowserTableModel() {
			
			listIt(selectnum);
			
		}

		public void listIt(int selectnum) {
			calParaSets(selectnum);
			this.data = new Object[alg.size()][this.columnNames.length];
			ArrayList<Double> params;
			
			for(int i = 0; i<alg.size(); i++){
				this.data[i][0] = alg.get(i);
				params = paraSets.get(alg.get(i));
				for(int j = 0; j < params.size(); j++){
					this.data[i][j+1] = params.get(j);
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
		
		

	
	}
	
	private int calParaSets(int selectnum) {
		int flag = 1;
		paraSets = new HashMap<String, ArrayList<Double>>();
		if (eplist == null || eplist.isEmpty()) {
			flag = 0;
			return flag;
		}
		
		
		epsize = 0;
		Iterator i2 = proteins.iterator();
		while (i2.hasNext()) {
			Protein p = (Protein) i2.next();
			if (eplist.contains(p.getName()))
				epsize++;
		}

		
		
		for(String algname : alg){
			ArrayList<Double> params = calEvaluationparams(selectnum, sortResults.get(algname));
				paraSets.put(algname, params);			
		}
		return flag;
	}
	
	private  ArrayList<Double> calEvaluationparams(int selectnum, List<Protein> proteins) {

		double eplength = 0, splength = 0, alllength = 0, count = 0;
		ArrayList<Double> params = new ArrayList<Double>();
		sproteins = proteins.subList(0, selectnum);
		
		
		splength = selectnum;
		alllength = allsize;
		eplength = epsize;

		Iterator i1 = sproteins.iterator();
		while (i1.hasNext()) {
			Protein p = (Protein) i1.next();
			if (eplist.contains(p.getName()))
				count++;
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
		
		params.add(TP);
		params.add(FP);
		params.add(FN);
		params.add(TN);
		params.add(SN);
		params.add(SP);
		params.add(PPV);
		params.add(NPV);
		params.add(F);
		params.add(ACC);
		return params;

	}
	
	public void update(int selectnum){		
		browserPanel.getTable().removeAll();
		browserPanel.getBrowserTableModel().listIt(selectnum);
		fitTableColumns(browserPanel.getTable());
		browserPanel.getBrowserTableModel().fireTableDataChanged();						
		this.browserPanel.updateUI();
		((JPanel)browserPanel.getParent()).updateUI();
		System.out.println("haaaaaaaaaaaaaa");
	}
	
	private class CreateChart extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
		
			if(selectedAlgs != null && !selectedAlgs.isEmpty() && pIndex > -1){
				DefaultCategoryDataset dataset = calDataset(selectedAlgs, pIndex);
		        JFreeChart chart = ChartFactory.createBarChart3D("", "Number of Proteins",browserPanel.getTable().getColumnName(pIndex+1), dataset, PlotOrientation.VERTICAL, true, true, true);  
		        ChartFrame frame = new ChartFrame("", chart, true); 
		        chartfs.add(frame);
		        
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
		        numberaxis.setAutoTickUnitSelection(false); 
		        if(Math.abs(maxY) > 1)
		        	numberaxis.setTickUnit(new NumberTickUnit(Math.ceil(maxY/10)));  
		        else
		        	numberaxis.setTickUnit(new NumberTickUnit(maxY/10)); 
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
			else{
				JOptionPane.showMessageDialog(null/*Cytoscape.getDesktop()*/,
	                    "Please at least choose one algorithm and one parameter!", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			
			
			
		}
		
		
		
      
	}
	
	
	private class CellRenderer extends DefaultTableCellRenderer{
		
		public CellRenderer(ArrayList a, int j){
			super();		
		}
		
		public boolean iscontain(int[] a, int b){
			for(int x : a)
				if(x == b)
					return true;		
			return false;
		}
		

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			
			
			if(row%2 == 0)
				setBackground(Color.white); //设置奇数行底色
			else if(row%2 == 1)
				setBackground(new Color(206,231,255));  //设置偶数行底色
			 
			if(column == 0)
             	setForeground(Color.red);
			else
				setForeground(Color.black);
			
			if(column == browserPanel.table.getSelectedColumn() && iscontain(browserPanel.table.getSelectedRows(), row))
				setBackground(Color.lightGray); 
			else if(column == 0 && iscontain(browserPanel.table.getSelectedRows(), row))
				setBackground(Color.lightGray); 
			else
				setFont(new Font("宋体",Font.ITALIC,20));
			
          
			return super.getTableCellRendererComponent(table, value, 
                                isSelected, hasFocus, row, column);
		}
   }
	
	private class SelectionHandler implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {

			selectedAlgs = new ArrayList<String>();
			pIndex = 0;
			for(int i : browserPanel.getTable().getSelectedRows()){
				selectedAlgs.add(alg.get(i));
				
			}
			 pIndex = browserPanel.getTable().getSelectedColumn()-1;
			 
			 CellRenderer tcr = new CellRenderer(selectedAlgs, pIndex);
			 
			 for(int i = 0; i < browserPanel.browserModel.columnNames.length; i++) {
				 browserPanel.table.getColumn(browserPanel.browserModel.columnNames[i]).setCellRenderer(tcr);
		        }
	
			
		}
		
	}
	
	

	
	private void calChartParams(ArrayList<String> algs){
		for(Integer sublength : percentagesNum){
			for(String alg :algs){
				if(ChartMap.get(sublength).get(alg).isEmpty()){
					ArrayList<Double> params = calEvaluationparams(sublength, sortResults.get(alg));
					ChartMap.get(sublength).put(alg, params);
				}
				
			}
		}
		
	}
	
	private DefaultCategoryDataset calDataset(ArrayList<String> algs, int paramindex){
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		calChartParams(algs);
		maxY = 0.0;
		for(int i = 0; i<percentagesNum.size() ;i++){
			int sublength = percentagesNum.get(i);
			for(String alg :algs){
				//System.out.println(ChartMap.get(sublength).get(alg).get(paramindex) + "  "+ alg +"  "+ String.valueOf(sublength));
				Double temp = ChartMap.get(sublength).get(alg).get(paramindex);
				dataset.addValue(temp, alg, String.valueOf(sublength)+"("+ percentages.get(i)+"%)");
				if(temp > maxY)
					maxY = temp;
			}
		}
		
		
		return dataset;
	}
/*
	public void discard(){
		if(chartfs !=null && !chartfs.isEmpty())
			for(ChartFrame cf : chartfs)
				if(cf != null)
					cf.dispose();
	}
*/	
	
	public synchronized ArrayList<Integer> calEproteinNum(String algname){
		ArrayList<Integer> eproteinNum = new ArrayList<Integer>();
		ArrayList<String> epl = new ArrayList<String>();
		int num = 0;
		for(String s : eplist)
			epl.add(s);
		List<Protein> sortl = sortResults.get(algname);
		eproteinNum.add(num);
		for(Protein p : sortl){
			String pname = p.getName();
			if(epl.contains(pname)){
				num++;
				epl.remove(pname);
			}
			eproteinNum.add(num);
		}
		
		return eproteinNum;
	}
	
	
	public synchronized XYDataset CalLineChartData(ArrayList<String> algs){
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		ArrayList<Integer> epnum;
		for(String algname : algs){
			XYSeries xys = new XYSeries(algname);
			if(eproteinNumMap.containsKey(algname) && eproteinNumMap.get(algname) != null && !eproteinNumMap.get(algname).isEmpty()){
				epnum = eproteinNumMap.get(algname);
			}				
			else{
				epnum = calEproteinNum(algname);
				eproteinNumMap.put(algname, epnum);
			}							
			int l = epnum.size();
			for(int i = 0; i < l; i++){
				xys.add(i, epnum.get(i));
			}
			xyseriescollection.addSeries(xys);
		}
		
		if(idealxys != null && !idealxys.isEmpty())
			xyseriescollection.addSeries(idealxys);
		else{
			idealxys = new XYSeries("Ideal");
			int l = proteins.size();
			int el = eplist.size();
			for(int i = 0; i <= l; i++){
				if(i <= el)
					idealxys.add(i, i);
				else
					idealxys.add(i, el);
			}
			xyseriescollection.addSeries(idealxys);
		}
		
		
		return xyseriescollection;		  
	}
	
	private void CalAUC(){
		
		int sum = 0;
		int l = proteins.size();
		int el = eplist.size();
		for(int i = 0; i <= l; i++){
			if(i <= el)
				sum +=  i;
			else
				sum +=  l;
		}
		AUCmap.put("Ideal", sum);
		
		for(String algname : selectedAlgs){
			if(AUCmap.get(algname) == null && eproteinNumMap.containsKey(algname) && eproteinNumMap.get(algname) != null && !eproteinNumMap.get(algname).isEmpty())
			{
				sum = 0;
				ArrayList<Integer> temp = eproteinNumMap.get(algname);
				l = proteins.size();
				el = eplist.size();
				for(int i = 0; i <= l; i++){
					if(i <= el)
						sum += temp.get(i);
					else
						sum += temp.get(i);
				}
				AUCmap.put(algname, sum);
			}
			
		}
	}
	
/*	
	private class DiscardAction extends AbstractAction{

		DiscardAction(){
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			discard();
			
			
				String message = (new StringBuilder("You are about to dispose of Evaluation ")).append(evaluationId).append(".\nDo you wish to continue?").toString();
				Integer confirmed = Integer.valueOf(JOptionPane.showOptionDialog(null, ((Object) (new Object[] {
					message
				})), "Confirm", 0, 3, null, null, null));
			
			if (confirmed.intValue() == 0)
			{
				discard();
				pUtil.getRegistrar().unregisterService(EvaluationPanel.this, CytoPanelComponent.class);
				pUtil.getResultPanel(evaluationId).setEvaluationPanel(null);
			
			//	registrar.unregisterService(panel, CytoPanelComponent.class);
		//		pUtil.removeNetworkResult(resultId);
				
			
			}
		}
		
	}
	*/
	private class CreateLineChart extends AbstractAction{
		
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
	//		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columKeys, data);

			if(selectedAlgs != null && !selectedAlgs.isEmpty()){
				JFreeChart chart = ChartFactory.createXYLineChart("", "", "The cumulative count of essential protiens", CalLineChartData(selectedAlgs), PlotOrientation.VERTICAL, true, true, false);
				
				CalAUC();
		        JPanel aucpanel = new JPanel();
		        aucpanel.setLayout(new GridLayout(2, 7));
		        aucpanel.setBorder(BorderFactory.createTitledBorder("AUC Values"));
		        
		        for(String algname : selectedAlgs){
		        	aucpanel.add(new JLabel(algname + ": "+AUCmap.get(algname)));
		        }
		        aucpanel.add(new JLabel("Ideal" + ": "+AUCmap.get("Ideal")));
		        MyChartFram frame = new MyChartFram("Comparison", chart,aucpanel); 
		        chartfs.add(frame);
			
		        XYPlot plot = (XYPlot) chart.getPlot();
		        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)plot.getRenderer();
		        //设置网格背景颜色
		        plot.setBackgroundPaint(Color.white);
		        //设置网格竖线颜色
		        plot.setDomainGridlinePaint(Color.pink);
		        //设置网格横线颜色
		        plot.setRangeGridlinePaint(Color.pink);
		        //设置曲线图与xy轴的距离
		        plot.setAxisOffset(new RectangleInsets(0D, 0D, 0D, 10D));
		        //设置曲线是否显示数据点
		        xylineandshaperenderer.setBaseShapesVisible(false);
		        //设置曲线显示各数据点的值
		        
		        
		        NumberAxis numberaxis = (NumberAxis) plot.getRangeAxis();  
		       
		
		        numberaxis.setTickUnit(new NumberTickUnit(eplist.size()/10)); 
		        
		        
		        
		        
		        
		        frame.pack();  
		        frame.setVisible(true); 
			}
			else{
				JOptionPane.showMessageDialog(null/*Cytoscape.getDesktop()*/,
	                    "Please at least choose an algorithm!", "Error", JOptionPane.WARNING_MESSAGE);
				return;
			}
				
			
		}
	}
	
	private class MyChartFram extends ChartFrame{
		JPanel all = new JPanel();
		public MyChartFram(String title, JFreeChart chart, JPanel aucp) {
			super(title, chart, true);
			all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
			all.add(new JScrollPane(new ChartPanel(chart)));
			all.add(aucp);
			this.setContentPane(all);
			
			
		}
		
	}
	
}