package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.Resources;
import org.cytoscape.CytoNCA.internal.Resources.ImageName;
import org.cytoscape.CytoNCA.internal.actions.DiscardEpListAction;
import org.cytoscape.CytoNCA.internal.algorithm.Algorithm;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.AbstractVisualPropertyValue;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EpListPanel extends JPanel
implements CytoPanelComponent,SetCurrentNetworkListener, AddedNodesListener, RemovedNodesListener
{
private List<Protein> CurrentEproteins;
private CyNetwork network;
private final int eplistId;
private JButton closeButton;
private ProteinUtil pUtil;
private final DiscardEpListAction discardEpListAction;
private static final Logger logger = LoggerFactory.getLogger(ResultPanel.class);
public BrowserPanel browserPanel;
private CyNetworkView networkView;
private ArrayList<String> AllEplist;
private ArrayList<String> NoncurrentEplist;
private CyApplicationManager applicationManager;
private HashMap<Long, ArrayList<CyNode>> SelectNodesMap;
private HashMap<Long, ArrayList<Paint>> NvpMap;
private HashMap<Long, ArrayList<NodeShape>> NsvMap;
private HashMap<Long, ArrayList<Double>> NodeSizeMap;

public EpListPanel(ArrayList<Protein> eprotein, ProteinUtil pUtil, CyNetwork network, CyNetworkView networkView, int eplistId, DiscardEpListAction discardEpListAction, CyApplicationManager applicationManager)
{
	System.out.println("shhhhhhhhhhhhhh");
  setLayout(new BorderLayout());


  
  this.pUtil = pUtil;
  this.eplistId = eplistId;
  this.AllEplist = pUtil.getAlleprotein();
  this.applicationManager = applicationManager;

 // this.CurrentEproteins = Collections.synchronizedList(eprotein);
 // this.network = network; 
 // this.networkView = networkView;
  CalCurrentEproteins();
  this.discardEpListAction = discardEpListAction;
  this.browserPanel = new BrowserPanel();
  
  
  
  
  this.setPreferredSize(new Dimension(400, 500));
  this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  add(this.browserPanel);
  add(createBottomPanel());
  //setSize(getMinimumSize());
  

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
	ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.jpg"));
	return icon;
}

public String getTitle()
{
  return "Essential Protein" + getEplistId();
}

public int getEplistId() {
  return this.eplistId;
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
      boolean oldRequestUserConfirmation = Boolean.valueOf(EpListPanel.this.discardEpListAction
        .getValue("requestUserConfirmation").toString()).booleanValue();

      EpListPanel.this.discardEpListAction.putValue("requestUserConfirmation", 
        Boolean.valueOf(requestUserConfirmation));
      EpListPanel.this.closeButton.doClick();
      EpListPanel.this.discardEpListAction.putValue("requestUserConfirmation", 
        Boolean.valueOf(oldRequestUserConfirmation));
    }
  });
}

	private JPanel createBottomPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		
		this.closeButton = new JButton(this.discardEpListAction);
		this.discardEpListAction.putValue("requestUserConfirmation",
				Boolean.valueOf(true));
		JButton resetnetview  = new JButton("Reset NetworkView");
		resetnetview.addActionListener(new resetnetviewAction());
		buttonPanel.add(resetnetview);
		buttonPanel.add(this.closeButton);
		panel.add(buttonPanel,"Center");

		return panel;
	}
	
	
	public class BrowserPanel extends JPanel {
		private final BrowserTableModel browserModel;
		private final JTable table;

		public BrowserPanel() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createTitledBorder("Essential Protein Browser"));
			this.browserModel = new BrowserTableModel();
			this.table = new JTable(this.browserModel);
			this.table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ListSelectionModel rowSM = this.table.getSelectionModel();
			 rowSM.addListSelectionListener(new TableRowSelectionHandler());
			 updateTableColor();
			 
			 
		 //    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane tableScrollPane = new JScrollPane(this.table);
			tableScrollPane.getViewport().setBackground(Color.WHITE);

			add(tableScrollPane, "Center");
		}


		public JTable getTable() {
			return this.table;
		}
		
		BrowserTableModel getBrowserTableModel() {
			return this.browserModel;
		}
		
		public void updateTableColor(){
			final int el = CurrentEproteins.size();
			 DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {
				 
		            public Component getTableCellRendererComponent(JTable table, 
		                          Object value, boolean isSelected, boolean hasFocus, 
		                                                     int row, int column) {
  	
		                if(row < el)
		                    setBackground(Color.white); 
		                if(row >= el){
		                    setBackground(Color.LIGHT_GRAY); 
		                    setEnabled(false);
		                   
		                }
		                    
		                return super.getTableCellRendererComponent(table, value, 
		                                          isSelected, hasFocus, row, column);
		            }
		        };
		        //设置列表现器------------------------//
		        for(int i = 0; i < browserModel.columnNames.length; i++) {
		            table.getColumn(browserModel.columnNames[i]).setCellRenderer(tcr);
		        }
		}
		
		
		
		}
	
	
	private class BrowserTableModel extends AbstractTableModel {
		private final String[] columnNames = { "No.", "Name" };
		private Object[][] data;

		public BrowserTableModel() {
			listIt();
		}

		public void listIt() {
		//	EpListPanel.this.exploreContent = new JPanel[EpListPanel.this.eprotein
		//			.size()];
			this.data = new Object[AllEplist.size()][this.columnNames.length];

			for (int i = 0; i < AllEplist.size(); i++) {
				int el = CurrentEproteins.size();
				if(i < el){
					Protein p = CurrentEproteins.get(i);
					this.data[i][0] = (new Integer(i + 1)).toString();
					this.data[i][1] = p.getName();
				}
				else{
					this.data[i][0] = (new Integer(i + 1)).toString();
					this.data[i][1] = NoncurrentEplist.get(i - el);
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
	
	private class TableRowSelectionHandler implements ListSelectionListener
	{
		
		private TableRowSelectionHandler()
		{
			//nvp = networkView.getNodeView(network.getNodeList().get(1)).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR);
			//nsv = networkView.getNodeView(network.getNodeList().get(1)).getVisualProperty(BasicVisualLexicon.NODE_SHAPE);
			//nodesize = networkView.getNodeView(network.getNodeList().get(1)).getVisualProperty(BasicVisualLexicon.NODE_SIZE);
			NvpMap = new HashMap<Long, ArrayList<Paint>>();
			NsvMap = new HashMap<Long, ArrayList<NodeShape>>();
			NodeSizeMap = new HashMap<Long, ArrayList<Double>>();
			SelectNodesMap = new HashMap<Long, ArrayList<CyNode>>();
		}

		public void valueChanged(ListSelectionEvent e)
		{
			
			if (e.getValueIsAdjusting()) 
				return;
			
			
			Long netid = network.getSUID();
			System.out.println(netid +"$$$$$$$$$$$$$$$$");
			
			if(SelectNodesMap.containsKey(netid)){
				resetnetview();
			}
		
				ArrayList<CyNode> selectednodes = new ArrayList<CyNode>();
				ArrayList<NodeShape> nsv = new ArrayList<NodeShape>();
				ArrayList<Paint> nvp = new ArrayList<Paint>();
				ArrayList<Double> nodesize = new ArrayList<Double>();
			
				
			
			
			
			int[] sr = browserPanel.getTable().getSelectedRows();
			if(sr.length != 0)
			for(int i=0 ; i<sr.length; i++){
				int selectedRow = sr[i];
				if(selectedRow < CurrentEproteins.size()){
					
					Protein p = CurrentEproteins.get(selectedRow);
					CyNode n = p.getN();
					selectednodes.add(n);
					
					double ns = networkView.getNodeView(n).getVisualProperty(BasicVisualLexicon.NODE_SIZE);
					nvp.add(networkView.getNodeView(n).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR));
					nsv.add(networkView.getNodeView(n).getVisualProperty(BasicVisualLexicon.NODE_SHAPE));
					nodesize.add(ns);
					
					if(!networkView.getNodeView(n).isValueLocked(BasicVisualLexicon.NODE_FILL_COLOR))
						networkView.getNodeView(n).setLockedValue(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.RED);
					networkView.getNodeView(n).setLockedValue(BasicVisualLexicon.NODE_SHAPE,NodeShapeVisualProperty.HEXAGON);
					networkView.getNodeView(n).setLockedValue(BasicVisualLexicon.NODE_SIZE, ns*1.7);
					
				//	System.out.println(nodesize);
					
					List<CyNode> neibors = network.getNeighborList(n, Type.ANY);
					if(neibors !=null){
						for(CyNode neibor : neibors){
							double nbs = networkView.getNodeView(neibor).getVisualProperty(BasicVisualLexicon.NODE_SIZE);
							nvp.add(networkView.getNodeView(neibor).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR));
							nsv.add(networkView.getNodeView(neibor).getVisualProperty(BasicVisualLexicon.NODE_SHAPE));
							nodesize.add(nbs);
							selectednodes.add(neibor);
							
							networkView.getNodeView(neibor).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.pink);
							networkView.getNodeView(neibor).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.TRIANGLE);
							networkView.getNodeView(neibor).setVisualProperty(BasicVisualLexicon.NODE_SIZE, nbs);
							
						}
						
					}
	    		 
					networkView.fitContent();
					networkView.updateView();
				}
				
			
				
			}
			
			NvpMap.put(netid, nvp);
			NsvMap.put(netid, nsv);
			NodeSizeMap.put(netid, nodesize);
			SelectNodesMap.put(netid, selectednodes);
			
		/*	
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if (!lsm.isSelectionEmpty()) {		 
				int selectedRow = lsm.getMinSelectionIndex();
				Protein p = eprotein.get(selectedRow);
				CyNode n = p.getN();
				selectednodes.add(n);
				networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.RED);
				
				List<CyNode> neibors = network.getNeighborList(n, Type.ANY);
				if(neibors !=null){
					for(CyNode neibor : neibors){
						networkView.getNodeView(neibor).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, java.awt.Color.YELLOW);
						selectednodes.add(neibor);
					}
					
				}
    		 
				networkView.fitContent();
				networkView.updateView();
        
			}
		
			*/
		}	
	}
	
	private void resetnetview(){
		ArrayList<Paint> nvp;
		ArrayList<CyNode> selectednodes;
		ArrayList<NodeShape> nsv;
		ArrayList<Double> nodesize;
		Long netid = network.getSUID();
		
		selectednodes = SelectNodesMap.get(netid);
		nsv = NsvMap.get(netid);
		nvp = NvpMap.get(netid);
		nodesize = NodeSizeMap.get(netid);
		
		if(!selectednodes.isEmpty()){	
			int i = 0;
			for(CyNode sn : selectednodes){
				networkView.getNodeView(sn).clearValueLock(BasicVisualLexicon.NODE_FILL_COLOR);
				networkView.getNodeView(sn).clearValueLock(BasicVisualLexicon.NODE_SHAPE);
				networkView.getNodeView(sn).clearValueLock(BasicVisualLexicon.NODE_SIZE);
				networkView.getNodeView(sn).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, nvp.get(i));
				networkView.getNodeView(sn).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, nsv.get(i));
				networkView.getNodeView(sn).setVisualProperty(BasicVisualLexicon.NODE_SIZE, nodesize.get(i));
			}
				
		}
	}
	
	private class resetnetviewAction extends AbstractAction {
		resetnetviewAction(){
			
		}
		public void actionPerformed(ActionEvent evt) {
			if(browserPanel.getTable().getSelectedRows().length != 0)
				browserPanel.getTable().clearSelection();
			else 
				resetnetview();
			networkView.updateView();
		}
	}
	@Override
	public void handleEvent(RemovedNodesEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void handleEvent(AddedNodesEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void handleEvent(SetCurrentNetworkEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("ReSet!!!!!!!!1");
		CalCurrentEproteins();
		browserPanel.getTable().removeAll();
		browserPanel.getTable();
		browserPanel.getBrowserTableModel().listIt();
		browserPanel.updateTableColor();
		browserPanel.getBrowserTableModel().fireTableDataChanged();						
		this.browserPanel.updateUI();
		((JPanel)browserPanel.getParent()).updateUI();
	}
	
	
	private void CalCurrentEproteins(){
		NoncurrentEplist = new ArrayList<String>();
		CurrentEproteins = new ArrayList<Protein>();
		for(String s : AllEplist){
			NoncurrentEplist.add(s);
		}
		this.network = this.applicationManager.getCurrentNetwork();
        this.networkView = this.applicationManager.getCurrentNetworkView();
		
        Iterator i = network.getNodeList().iterator();
		while (i.hasNext()){
			CyNode n = (CyNode)i.next();
			String name = network.getRow(n).get("name", String.class);
			if(AllEplist.contains(name)){
				Protein p = new Protein(n, network);
				CurrentEproteins.add(p);
				NoncurrentEplist.remove(name);
			}
		}

	}


	
	

}