package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import javax.naming.NameParser;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;

import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.Protein;
import org.cytoscape.CytoNCA.internal.ProteinGraph;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.SpringEmbeddedLayouter;


import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.jfree.chart.ChartFrame;

public class AnalysisPanel extends JPanel implements CytoPanelComponent{
	private int id;
	private HashMap<String, List<Protein>> sortResults;
	private ArrayList<ChartFrame> chartfs;
	private List<Protein> sproteins;
	private CyNetwork network;
	private CyNetworkView networkView;
	private ProteinUtil pUtil;
	private SelectPanel selectPanel;
	private PaintPanel paintPanel;
	private OperationPanel operationPanel;
	private ProteinGraph pg;
	private ArrayList<String> eplist;
	private String curalg;
	private String curSetName;
	private static int Ylocation;
	private static boolean ismixcolor;
	private static int SETINDEX;
	private static boolean NETWOKRMODIFIED;
	
	
	public AnalysisPanel(int resultid, CyNetwork network, CyNetworkView networkView, HashMap<String, List<Protein>> sortResults, ProteinUtil pUtil, ArrayList<String> eplist, String curalg, List<Protein> sproteins, String curSetName){
		this.id = resultid;
		this.pUtil = pUtil;	 
		this.network = network; 
		this.networkView = networkView;
		this.sortResults = sortResults;
		//this.setLayout(new GridLayout(1,3));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.selectPanel = new SelectPanel();
		this.paintPanel = new PaintPanel();
		this.operationPanel = new OperationPanel();
		this.curalg = curalg;
		this.sproteins = sproteins;
		this.eplist = eplist;
		this.curSetName = curSetName;
		this.ismixcolor = true;
		this.SETINDEX = 0;
		NETWOKRMODIFIED = false;
		
		this.add(selectPanel);
		this.add(paintPanel);
		this.add(operationPanel);
		
		
		
	}
	
	
	
	
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this; 
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	@Override
	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.jpg"));
		return icon;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "AnalysisPanel "+id;
	}
	public CyNetworkView getNetworkView() {
		return this.networkView;
	}
	
	public int getAnalysisId() {
		return id;
	}
	
	
	
	
	public class SelectPanel extends JPanel{
		
		public JPanel buttonpanel;
		public JTextArea namesT;
		public JScrollPane namesP;
		public JButton selectB,uploadB,selectEdgesB;
		
		
		public SelectPanel(){
			
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
			buttonpanel = new JPanel();
			buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.X_AXIS));
			
			
			namesT = new JTextArea();
			//namesT.setRows(5);
			//namesT.setColumns(5);
			namesP = new JScrollPane(namesT);
		//	namesP.setPreferredSize(new Dimension(200,200));
			
			selectB = new JButton("Select Nodes");
			selectB.addActionListener(new SelectByNamesAction());
			selectEdgesB = new JButton("Select Edges");
			selectEdgesB.addActionListener(new SelectEdgesAction());
			
			
			uploadB = new JButton("Upload from file");
			uploadB.addActionListener(new UploadfileAction());
			
			buttonpanel.add(selectB);
			buttonpanel.add(uploadB);
		//	buttonpanel.add(selectEdgesB);
			
			this.add(namesP);
			this.add(buttonpanel);
			
			this.setBorder(BorderFactory.createTitledBorder("Select by name"));
		}
		
		private class UploadfileAction extends AbstractAction {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				JFileChooser fc = new JFileChooser();
				File f;
				int flag = 0;;
				fc.setDialogTitle("Open File");   
				try{    
					flag=fc.showOpenDialog(null);    
				}catch(HeadlessException head){    
					JOptionPane.showMessageDialog(null,
							"Open File Dialog ERROR!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}            
				if(flag==JFileChooser.APPROVE_OPTION){   
					f=fc.getSelectedFile();   
					if(f.getName().substring(f.getName().lastIndexOf(".")+1).equals("txt")){
						try {
							BufferedReader br=new BufferedReader(new FileReader(f));
							String text;   
							
							while((text=br.readLine())!=null){
								namesT.append(text+"\n");
							}		
							JOptionPane.showMessageDialog(null,
									"Upload success!", "", JOptionPane.WARNING_MESSAGE);
							
						}catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null,
									"Read data ERROR!", "Interrupted", JOptionPane.WARNING_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(null,
								"Please Upload Text file!", "Interrupted", JOptionPane.WARNING_MESSAGE);
						return;
					}	
				}  
			}

		}
		
		private class SelectEdgesAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				ArrayList<HashSet<String>> a = input("J:/学习/cytoscape/9.15处理数据/新建文件夹/tag_up_3_ppi-0.txt");
				ArrayList<HashSet<String>> b = input("J:/学习/cytoscape/9.15处理数据/新建文件夹/wt_up_3_ppi-0.txt");
				ArrayList<HashSet<String>> c = input("J:/学习/cytoscape/9.15处理数据/新建文件夹/common edges.txt");
				
				//paintview(a, Color.red);
				//paintview(b, Color.blue);
				paintview(a, Color.red, false);
				paintview(b, Color.blue, false);
				paintview(c, Color.yellow, false);
				
				delEdges(Color.blue);
				
				networkView.updateView();
				
			}
			
			public void delEdges(Color c){
				for(CyEdge e : network.getEdgeList()){
					ArrayList<CyEdge> temp = new ArrayList<CyEdge>();
					if(!networkView.getEdgeView(e).getVisualProperty(BasicVisualLexicon.EDGE_PAINT).equals(c)){
						temp.add(e);
						network.removeEdges(temp);
					}
				}
			}
			
			public void paintview(ArrayList<HashSet<String>> t,Color c, boolean flag){
				
				for(CyEdge e : network.getEdgeList()){
					HashSet<String> th = new HashSet<String>();
					th.add(network.getRow(e.getSource()).get("name", String.class));
					th.add(network.getRow(e.getTarget()).get("name", String.class));
					
					if(t.contains(th)){
						
						networkView.getEdgeView(e).setLockedValue(BasicVisualLexicon.EDGE_PAINT, c);
						if(flag){
							networkView.getNodeView(e.getSource()).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, c);
							networkView.getNodeView(e.getTarget()).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, c);
						}
						
						
					}
				}
			}
			
			public ArrayList<HashSet<String>> input(String fn){

				ArrayList<HashSet<String>> a = new ArrayList<HashSet<String>>();
				
				
		        File file = new File(fn);
		        BufferedReader reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String tempString = null;
		            int line = 1;
		            // 一次读入一行，直到读入null为文件结束
		            while ((tempString = reader.readLine()) != null) {
		          
		               
		                int idx1 = tempString.indexOf("	");
		                String s0 = tempString.substring(0, idx1);
		                String s1 = tempString.substring(idx1 + 1, tempString.length());
		               
		            	
		                HashSet<String> temp = new HashSet<String>();
						temp.add(s0);
						temp.add(s1);
						
						a.add(temp);
		            }
		            reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            if (reader != null) {
		                try {
		                    reader.close();
		                } catch (IOException e1) {
		                }
		            }
		        }
		        
		        return a;
		        
			}
			
		}
		
		/**
		 * @version 2.1.2
		 * @author TangYu
		 * @date: 2014年9月5日 下午4:57:09
		 *
		 */
		private class SelectByNamesAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				List<Protein> temp = new ArrayList<Protein>();
				int lastLineIndex = namesT.getLineCount()-1;
				int nameNum = lastLineIndex + 1;	
				int duplicateNum = 0;
				try {
					for(int i = 0; i < lastLineIndex; i++){
						if(namesT.getLineEndOffset(i) - namesT.getLineStartOffset(i)  == 1){
							nameNum--;
							continue;
						}
						String name = namesT.getText(namesT.getLineStartOffset(i), 
										namesT.getLineEndOffset(i) - namesT.getLineStartOffset(i) -1);						
						for(Protein p : sortResults.get(curalg)){
							if(p.getName().equals(name)){
								if(temp.contains(p)){
									duplicateNum++;
								}else{
									temp.add(p);									
								}	
								break;
							}
						}
					}
					
					if(namesT.getLineStartOffset(lastLineIndex) == namesT.getLineEndOffset(lastLineIndex)){
						nameNum--;					
					}else{
						String name = namesT.getText(namesT.getLineStartOffset(lastLineIndex), 
										namesT.getLineEndOffset(lastLineIndex) - namesT.getLineStartOffset(lastLineIndex));					
						for(Protein p : sortResults.get(curalg)){
							if(p.getName().equals(name)){
								temp.add(p);							
								break;
							}
						}
					}			
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(!temp.isEmpty()){			
					sproteins.clear();
					sproteins.addAll(temp);
					pUtil.sortVertex(sproteins, curalg);
					pUtil.getResultPanel(id).browserPanel.updateTable(null);
					pUtil.getResultPanel(id).browserPanel.changeSortingRange(false);
					curSetName = "Self-select proteins (No. "+ ++SETINDEX +")" ;
				}				
				JOptionPane.showMessageDialog(null,
						nameNum + " node names have been uploaded, "+ duplicateNum + " duplicated values, "+temp.size()+" nodes have been selected.", "Result", JOptionPane.WARNING_MESSAGE);				
			}			
		}
	
	
	}
	
	public class PaintPanel extends JPanel{
		JButton paintB, clearB, selectOverlapB;
		JCheckBox ismixcolorC;
		JPanel upPanel, labelPanel;
		JScrollPane labelSPanel;
		HashMap<String, Color> SetColorMap = new HashMap<String, Color>(); 
		ArrayList<String> selectedSets = new ArrayList<String>();
		
		PaintPanel(){
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setBorder(BorderFactory.createTitledBorder("Paint Selected Nodes"));
			upPanel = new JPanel();
			upPanel.setLayout(new BoxLayout(upPanel, BoxLayout.X_AXIS));
			
			labelPanel = new JPanel();
			//labelPanel.setLayout(null);
			labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
			//labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			labelPanel.setLayout(new GridLayout(0, 1));
			JPanel p1 = new JPanel();
			p1.add(labelPanel);
			p1.setLayout(new FlowLayout());
			
			//labelSPanel = new JScrollPane(labelPanel);
			labelSPanel = new JScrollPane(p1);
			
			paintB = new JButton("Paint");
			Ylocation = 10;
			paintB.addActionListener(new PaintAction());
			clearB = new JButton("Clear");
			clearB.addActionListener(new clearViewColorsAction());
			selectOverlapB = new JButton("Select Overlap");
			selectOverlapB.addActionListener(new SelectOverlapAction());
			ismixcolorC = new JCheckBox("Paint overlaps", true);
			ismixcolorC.setAction(new IsmixcolorAction());
			ismixcolorC.setText("Paint overlaps with mixed colors");
			

			upPanel.add(paintB);
			upPanel.add(clearB);
			upPanel.add(selectOverlapB);
			upPanel.add(ismixcolorC);
			this.add(upPanel);
			//this.add(selectOverlapB);
			this.add(labelSPanel);
			
			upPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//	selectOverlapB.setAlignmentX(Component.LEFT_ALIGNMENT);
		//	labelSPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			

			
		}
		
		
		private class PaintAction extends AbstractAction{
			
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Color mixc = null ,currentc = null;
				HashMap<String, Color> mixColors = new HashMap<String, Color>();
				
				JColorChooser jc = new JColorChooser();  
				currentc = jc.showDialog(null,
			            "Please choose one color", Color.red);				
			
				
				if(currentc != null){
					pUtil.setSelected(null, network);
					for(Protein p: sproteins){
						
							
						CyNode n = p.getN();
						SetColorMap.put(curSetName, currentc);
						if(p.getSelectGroups() == null || p.getSelectGroups().isEmpty()){
							
							p.setOriginColor(networkView.getNodeView(n).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR));
									
							p.setSelectGroups(new ArrayList<String>());	
							p.getSelectGroups().add(curSetName);
							networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, currentc);
							networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_TOOLTIP, curSetName);
							
						}else{
							
							if(ismixcolor){
								p.getSelectGroups().add(curSetName);
								String mixname = "";
								double blue = 0, red = 0, green = 0, s = p.getSelectGroups().size();
								int r,g,b;
								int i = 0;
								for(String set : p.getSelectGroups()){
									blue += SetColorMap.get(set).getBlue();
									red += SetColorMap.get(set).getRed();
									green += SetColorMap.get(set).getGreen();
									if(i==0)
										mixname += set;
									else
										mixname += " + "+set;
									i++;
								}
								r = (int) Math.round(red/s);
								g = (int) Math.round(green/s);
								b = (int) Math.round(blue/s);					
								mixc = new Color(r, g, b);
								
								networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, mixc);
								networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_TOOLTIP , mixname);
								mixColors.put(mixname, mixc);
							}else{
								
							//	p.setOriginColor(networkView.getNodeView(n).getVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR));
								
								
								p.getSelectGroups().add(curSetName);
								networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, currentc);
								networkView.getNodeView(n).setVisualProperty(BasicVisualLexicon.NODE_TOOLTIP, curSetName);
							}
							
							
						}
						
												
												
					}
					
					JPanel jp = new JPanel();
					jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
					
					jp.add(new JLabel(curSetName+"  ",createColorIcon(currentc), SwingConstants.LEFT));
					
					
					JCheckBox temp = new JCheckBox();
					temp.addActionListener(new ChooseSetsAction());
					
					jp.add(temp);
					jp.setBorder(BorderFactory.createTitledBorder(""));
					jp.setBounds(10, Ylocation,300,25);
					Ylocation += 30;
					labelPanel.add(jp);
					
					JPanel jj = new JPanel();
					jj.setLayout(new BoxLayout(jj, BoxLayout.Y_AXIS));
					jj.setAlignmentX(Component.LEFT_ALIGNMENT);
					if(!mixColors.isEmpty()){
						
						for(Entry<String, Color>  mix : mixColors.entrySet()){						
							jj.add(new JLabel(mix.getKey(), createColorIcon(mix.getValue()), SwingConstants.LEFT));
							jj.setBounds(10, Ylocation,300,25);
							Ylocation += 30;
							labelPanel.add(jj);
						}
						
					}
					
					labelPanel.repaint();
				
			
					

				
				}
				
				
				
				
				
			}
			
		private Icon createColorIcon(Color cc){
			final Color fc;
			fc = cc;
			Icon ic = new Icon() {				
				@Override
				public void paintIcon(Component c, Graphics g, int x, int y) {
					// TODO Auto-generated method stub
					 g.setColor(fc); 
				     g.fillOval(x, y, 15, 15);  
					
				}
				
				@Override
				public int getIconWidth() {
					// TODO Auto-generated method stub
					return 15;
				}
				
				@Override
				public int getIconHeight() {
					// TODO Auto-generated method stub
					return 15;
				}
			};
			
			return ic;
		}	
			
		}
		
		private class clearViewColorsAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				for(Protein p : sortResults.get(curalg)){
					if(p.getSelectGroups() != null && !p.getSelectGroups().isEmpty() ){
						networkView.getNodeView(p.getN()).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, p.getOriginColor());
						networkView.getNodeView(p.getN()).setVisualProperty(BasicVisualLexicon.NODE_TOOLTIP, "");
						p.getSelectGroups().clear();
					}
					
				}
				SetColorMap.clear();
				selectedSets.clear();
				labelPanel.removeAll();
				labelPanel.repaint();
				networkView.updateView();
				SETINDEX = 0;
			}
			
		}
		
		private class ChooseSetsAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JCheckBox set = (JCheckBox) e.getSource();
				if(set.isSelected()){
					selectedSets.add(((JLabel)set.getParent().getComponent(0)).getText().trim());
				}else
					selectedSets.remove(((JLabel)set.getParent().getComponent(0)).getText().trim());
	
				
			}
			
		}
		
		private class SelectOverlapAction extends AbstractAction{
			
			

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				
				int l = selectedSets.size();
				
				if(l != 0){
					
					ArrayList<Protein> temp = new ArrayList<Protein>();
					
					for(Protein p : sortResults.get(curalg)){
						
						
						if(p.getSelectGroups() != null && p.getSelectGroups().size() >= l){
							if(p.getSelectGroups().containsAll(selectedSets)){						
								temp.add(p);
							}					
						}					
					}
				
					if(!temp.isEmpty()){
					
						clearB.doClick();
						sproteins.clear();
						sproteins.addAll(temp);
						pUtil.sortVertex(sproteins, curalg);
						pUtil.getResultPanel(id).browserPanel.updateTable(null);
						curSetName = "Overlapping Nodes";					
					}else{
						
					}
					
				}else{
					
				}
				
				
			}
			
		}
		
		private class IsmixcolorAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JCheckBox set = (JCheckBox) e.getSource();
				if(set.isSelected()){
					ismixcolor = true;
				}else
					ismixcolor = false;
	
				
			}
			
		}
		
		
	}

	public class OperationPanel extends JPanel{
		JButton createChildButton, detailInformationButton, evaluationButton;
		ArrayList<JFrame> inforframes;
		
		
		
		OperationPanel(){
			 
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setBorder(BorderFactory.createTitledBorder(""));
			inforframes = new ArrayList<JFrame>();
			JPanel p1 = new JPanel(),p2 = new JPanel(),p3 = new JPanel();
			
			
			createChildButton = new JButton("Create Sub-Network");
			createChildButton.addActionListener(new CreateSubNetworkAction());
			createChildButton.setToolTipText("Generating the new sub network of selected nodes.");
			
			detailInformationButton = new JButton("<html> Detail Information <br>for selected nodes</html>");
			detailInformationButton.addActionListener(new ShowDetailInformationAction());
			
			evaluationButton = new JButton("Evaluating Result");
			evaluationButton.addActionListener(new EvaluationAction());
			
			p1.add(createChildButton);
			p2.add(detailInformationButton);
			p3.add(evaluationButton);
			
			this.add(p1);
			this.add(p2);
			this.add(p3);
		}
		
		private class CreateSubNetworkAction extends AbstractAction {
			

			public void actionPerformed(ActionEvent evt) {
				// NumberFormat nf = NumberFormat.getInstance();
				// nf.setMaximumFractionDigits(3);
				// final Cluster cluster =
				// (Cluster)ResultPanel.this.clusters.get(this.selectedRow);
				if( pg !=null){
					final CyNetwork pNetwork = pg.getSubNetwork();
					final String title = id
							+"("+ curSetName + ")";

					SwingWorker worker = new SwingWorker() {
						protected CyNetworkView doInBackground() throws Exception {
							CySubNetwork newNetwork = pUtil
									.createSubNetwork(pNetwork, pNetwork.getNodeList(),
											SavePolicy.SESSION_FILE);
							newNetwork.getRow(newNetwork).set("name", title);

							VisualStyle vs = pUtil
									.getNetworkViewStyle(networkView);
							CyNetworkView newNetworkView = pUtil
									.createNetworkView(newNetwork, vs);

							newNetworkView.setVisualProperty(
									BasicVisualLexicon.NETWORK_CENTER_X_LOCATION,
									Double.valueOf(0.0D));
							newNetworkView.setVisualProperty(
									BasicVisualLexicon.NETWORK_CENTER_Y_LOCATION,
									Double.valueOf(0.0D));

							pUtil.displayNetworkView(newNetworkView);

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

									
									if (eplist.contains(name)) {
										nv.setVisualProperty(
												BasicVisualLexicon.NODE_FILL_COLOR,
												java.awt.Color.RED);										
									} else {									
										nv.setVisualProperty(
												BasicVisualLexicon.NODE_FILL_COLOR,
												java.awt.Color.BLUE);
									}
								} 
									

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
		
		private class ShowDetailInformationAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				DetailInforFrame detailinforframe = new DetailInforFrame();
				inforframes.add(detailinforframe);
				
			}
			
		}
		
		private class EvaluationAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				pUtil.getResultPanel(id).ceateEvaluationPanel();
			}
			

			
			
		}
		
		private class DetailInforFrame extends JFrame{
			private JTable infortable;
			private InforTableModel infortablemodel;
			private ArrayList<Integer> n = new ArrayList<Integer>();
			DetailInforFrame(){
				
				
				infortablemodel = new InforTableModel();
				infortable = new JTable(infortablemodel);
				infortable.setSelectionMode(0);
				infortable.setDefaultRenderer(Object.class, new StringCellRenderer());
				infortable.setDefaultRenderer(Number.class, new NumberCellRenderer());
				infortable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				
				setColumnColor(n);
				fitTableColumns(infortable);
				JScrollPane tableSPanel = new JScrollPane(infortable);
				tableSPanel.getViewport().setBackground(Color.WHITE);
				
			//	JPanel tablePanel = new JPanel();
			//	tablePanel.add(tableSPanel);
				
			//	this.add(tablePanel);
				this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				this.add(tableSPanel);	
				this.setSize(900, 400);
				this.setVisible(true);
				
				
			}
			
			
			private class InforTableModel extends AbstractTableModel {
				
				private final String[] columnNames = new String[2+sortResults.keySet().size() * 5];
				private Object[][] data;
				
				InforTableModel(){
					columnNames[0] = "No.";
					columnNames[1] = "Name";
					
					int i = 0;
					for(String s : sortResults.keySet()){
						columnNames[2+i*5] = s;
						n.add(2+i*5);
						columnNames[3+i*5] = "Rank("+s+")";
						columnNames[4+i*5] = "Ave("+s+")";
						columnNames[5+i*5] = "Max("+s+")";
						columnNames[6+i*5] = "Min("+s+")";
						i++;			
						listIt(sproteins);
						
					}
					
				}
				
				public void listIt(List<Protein> sproteins) {
					
					this.data = new Object[sproteins.size()][this.columnNames.length];
					
					int i = 0,j;
					for(Protein p : sproteins){
						
						data[i][0] = i+1;
						data[i][1] = p.getName();
						j = 0;
						for(String s : sortResults.keySet()){
							data[i][2+j*5] = p.getPara(s);
							j++;
						}
						i++;
					}
					j=0;
					for(String s : sortResults.keySet()){
						
						List<Protein> rankedResult = sortResults.get(s);
						int length = rankedResult.size();
						DecimalFormat df = new DecimalFormat("#.####");
						
						
						double max = rankedResult.get(0).getPara(s);
						double min = rankedResult.get(length-1).getPara(s);
						double avg = 0;
						for(Protein p : rankedResult){
							avg += p.getPara(s);
						}
						avg = avg / (double)length; 
						i=0;
						for(Protein p : sproteins){
							data[i][3+j*5] = rankedResult.indexOf(p)+1;
							if(s == ParameterSet.IC)
								df = new DecimalFormat("#.####E0");
								
								data[i][4+j*5] = df.format(avg);
								data[i][5+j*5] = df.format(max);
								data[i][6+j*5] = df.format(min);
							
							
							i++;
						}
						
						j++;
					}
					
				}

				
				public String getColumnName(int col) {
					return columnNames[col];
				}

				@Override
				public int getRowCount() {
					// TODO Auto-generated method stub
					return data.length;
				}

				@Override
				public int getColumnCount() {
					// TODO Auto-generated method stub
					return columnNames.length;
				}

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					// TODO Auto-generated method stub
					return data[rowIndex][columnIndex];
				}
				
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
					column.setWidth(width + table.getIntercellSpacing().width);
					
				}
			}
			
			
			public void setColumnColor(ArrayList<Integer> n){
				
				CellRenderer tcr = new CellRenderer(n);			
		        //设置列表现器------------------------//
		        for(int i = 0; i < infortablemodel.columnNames.length; i++) {
		            infortable.getColumn(infortablemodel.columnNames[i]).setCellRenderer(tcr);
		             	
		        }
		       
			}
			
			
			private class CellRenderer extends DefaultTableCellRenderer{
				ArrayList<Integer> n;
				public CellRenderer(ArrayList<Integer> n){
					super();
					this.n = n;			
				}
				 public Component getTableCellRendererComponent(JTable table, 
		                 Object value, boolean isSelected, boolean hasFocus, 
		                                            int row, int column) {
					 if(n.contains(column)){
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
			
		}
	
	}
	

	
	
	public void updateNet(String curalg, String curSetName) {
		List<CyNode> nodes = new ArrayList();
		Iterator i = sproteins.iterator();
		while (i.hasNext()) {
			Protein p = (Protein) i.next();
			nodes.add(p.getN());
		}
		pg = pUtil.createGraph(network, nodes);
		selectProteins(pg.getSubNetwork());
		this.curalg = curalg;
		this.curSetName = curSetName;

	}
	public void selectProteins(CyNetwork custerNetwork) {
		if (custerNetwork != null) {
			this.pUtil.setSelected(custerNetwork.getNodeList(), this.network);
		} else {
			this.pUtil.setSelected(new ArrayList(), this.network);
		}
	}
	
	
	public void discard(){
		
		if(chartfs !=null && !chartfs.isEmpty())
			for(ChartFrame cf : chartfs)
				if(cf != null)
					cf.dispose();
		if(operationPanel.inforframes !=null && !operationPanel.inforframes.isEmpty())
			for(JFrame inf : operationPanel.inforframes)
				if(inf != null)
					inf.dispose();	
		paintPanel.clearB.doClick();
	}





}
