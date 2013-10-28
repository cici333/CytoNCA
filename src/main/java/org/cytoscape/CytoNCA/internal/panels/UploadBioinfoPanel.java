package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.CytoNCA.internal.panels.UploadBioinfoPanel.NodesAttributesPanel.ChoosePanel;
import org.cytoscape.CytoNCA.internal.panels.UploadBioinfoPanel.NodesAttributesPanel.SelectNodeAttributePanel;
import org.cytoscape.CytoNCA.internal.panels.UploadBioinfoPanel.NodesAttributesPanel.UploadFromfilePanel;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class UploadBioinfoPanel extends JFrame{
	
	SelectNodeAttributePanel selectnodeattributepanel; 
	UploadFromfilePanel uploadfromfilepanel;
	ChoosePanel choosePanel;
	ProteinUtil pUtil;
	CyNetwork network;
	
	
	
	UploadBioinfoPanel(ProteinUtil pUtil ){
		
		
		this.pUtil = pUtil;
		this.network = pUtil.getApplicationMgr().getCurrentNetwork();
		
		
		JPanel p0 = new NodesAttributesPanel();
		JPanel p1 = new EdgesAttributesPanel();
	
		JTabbedPane upTP = new JTabbedPane();
		upTP.add("Nodes attributes", p0);
		upTP.add("Edges attributes", p1);		
		this.getContentPane().add(upTP);
		
		
		
		
		//this.add(p0);
		this.setSize(new Dimension(500, 300));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	class NodesAttributesPanel extends JPanel{
		CyColumn bioinfoC;
		String NodeattributeName = null;
		
		NodesAttributesPanel(){
			
			this.setLayout(new BorderLayout());
			
			selectnodeattributepanel = new SelectNodeAttributePanel();
			uploadfromfilepanel = new UploadFromfilePanel();
			choosePanel = new ChoosePanel();
			
			JPanel leftp = new JPanel();
			leftp.setLayout(new BoxLayout(leftp, BoxLayout.Y_AXIS));
			JPanel rightp = new JPanel();

			JPanel bottomp = new JPanel(new BorderLayout());
			JPanel centerp = new JPanel(new BorderLayout());
			
			leftp.add(selectnodeattributepanel);
		//	leftp.add(uploadfromfilepanel);
			leftp.setBorder(BorderFactory.createEtchedBorder());
			
			rightp.add(choosePanel,BorderLayout.CENTER);
			rightp.setBorder(BorderFactory.createEtchedBorder());
			
			JButton okB = new JButton("OK");
			okB.addActionListener(new OkAction());
		
			okB.setPreferredSize(new Dimension(100, 20));
			bottomp.setPreferredSize(new Dimension(450, 70));
			bottomp.add(okB,BorderLayout.CENTER);
			
			JLabel asl = new JLabel(" as ");
		//	centerp.add(asl,BorderLayout.CENTER);
			centerp.add(asl);
			asl.setAlignmentX(CENTER_ALIGNMENT);
			asl.setAlignmentY(CENTER_ALIGNMENT);
			
			this.add(leftp, BorderLayout.WEST);
			this.add(centerp, BorderLayout.CENTER);
			this.add(rightp, BorderLayout.EAST);
			this.add(bottomp, BorderLayout.SOUTH);
						
		}
		
		
		class SelectNodeAttributePanel extends JPanel{
			JComboBox<String> selectattris;
			ArrayList<String> attributes;
			
			SelectNodeAttributePanel(){
				selectattris = new JComboBox<String>();
				getAttributes();
				selectattris.addActionListener(new SelectAttributeAction());
				//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				JLabel l1 = new JLabel("<html>  Upload from <br>existing attributes</html>",JLabel.CENTER);
				this.add(l1);
				this.add(selectattris);
			//	l1.setAlignmentY(CENTER_ALIGNMENT);
			//	selectattris.setAlignmentY(CENTER_ALIGNMENT);
				
			
				this.setBorder(BorderFactory.createTitledBorder(""));
				this.setPreferredSize(new Dimension(140,105));
			}
			
			void getAttributes(){
				for(CyColumn cm : network.getDefaultNodeTable().getColumns()){			
			//		if(!cm.equals("name") && !cm.equals("SUID") && !cm.equals("shared name") && !cm.equals("selected"))
						selectattris.addItem(cm.getName());
				}
			}
			
			void updateItems(String newname){
				selectattris.removeItem(selectattris.getSelectedItem());
				selectattris.addItem(newname);
				selectattris.updateUI();
			}
			
			
			class SelectAttributeAction extends AbstractAction{

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JComboBox<String> jcb = (JComboBox<String>) e.getSource();
					bioinfoC = network.getDefaultNodeTable().getColumn((String) jcb.getSelectedItem());			
				}
				
			}
		}
		
		class UploadFromfilePanel extends JPanel{
			JTextField filename;
			UploadFromfilePanel(){
				
				filename = new JTextField();
				filename.setEditable(false);
				filename.setPreferredSize(new Dimension(90,30));
				
				JButton uploadB = new JButton("Upload from file");
				uploadB.setPreferredSize(new Dimension(90,30));
				uploadB.addActionListener(new UploadFromfileAction());
				//this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				this.setLayout(new BorderLayout());
				this.add(uploadB,BorderLayout.NORTH);
				this.add(filename, BorderLayout.SOUTH);
				//this.add(uploadB);
				//this.add(filename);
				this.setBorder(BorderFactory.createTitledBorder("Upload from file"));
				this.setPreferredSize(new Dimension(140,105));
				
			}
			
			class UploadFromfileAction extends AbstractAction{

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
									
									System.out.println(text);
									
									
									
									
								}		
								JOptionPane.showMessageDialog(null,
										"Upload attributes success!", "", JOptionPane.WARNING_MESSAGE);
							
								filename.setText(f.getName());
								//		pUtil.setEvaluation(true);
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
		}

		class ChoosePanel extends JPanel{
			JRadioButton domainB, slengthB, NweightB, othersB;  
			JTextField aname;
			ChoosePanel(){
				domainB = new JRadioButton(ParameterSet.domainNum);
				slengthB = new JRadioButton(ParameterSet.sequencelength);
				NweightB = new JRadioButton(ParameterSet.weight);
				othersB = new JRadioButton("Others");
				aname = new JTextField("attribute name...");
				aname.setEditable(false);
				aname.setPreferredSize(new Dimension(90,30));
				
				ButtonGroup bg = new ButtonGroup();
				bg.add(domainB);
				bg.add(slengthB);
				bg.add(NweightB);
				bg.add(othersB);
				
				domainB.addItemListener(new ChoiseAction());
				slengthB.addItemListener(new ChoiseAction());
				NweightB.addItemListener(new ChoiseAction());
				othersB.addItemListener(new ChoiseAction());
				
				this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				this.add(domainB);
				this.add(slengthB);
				this.add(NweightB);
				this.add(othersB);
				this.add(aname);
				
				
				
			}
			class ChoiseAction implements ItemListener{

				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					JRadioButton jr = (JRadioButton) e.getSource();
					if(jr.isSelected()){
						if(jr.getText().equals("Others")){
							aname.setEditable(true);
							aname.setText("");
						}
						else{
							NodeattributeName = jr.getText();
							aname.setEditable(false);
						}
					}
				}
				
			}
		}

		class OkAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) throws IllegalArgumentException {
				// TODO Auto-generated method stub
	
				if(!bioinfoC.getType().equals(Double.class) && !bioinfoC.getType().equals(Integer.class)){
						
					JOptionPane.showMessageDialog(null,
    	            		"Data type must be double or integer!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
					
					
				
				}					
				else{
					try{
						bioinfoC.setName(NodeattributeName);
					}
					catch(IllegalArgumentException ie){
						JOptionPane.showMessageDialog(null,
	    	            		"Another column with a matching name already exists!", "Interrupted", JOptionPane.WARNING_MESSAGE);
						return;
					}
					pUtil.getBioinfoColumnNames().add(NodeattributeName);
					selectnodeattributepanel.updateItems(NodeattributeName);
					JOptionPane.showMessageDialog(null,
    	            		"Upload Success!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				
					
				
				
			}
			
		}

	}
	
	class EdgesAttributesPanel extends JPanel{
		CyColumn EdgeWeight;
		
		EdgesAttributesPanel(){
			this.setLayout(new BorderLayout());
			
			SelectEdgeAttributePanel sp = new SelectEdgeAttributePanel();
			
			this.add(sp, BorderLayout.CENTER);
			
			JButton okB = new JButton("OK");
			okB.addActionListener(new OkAction());
		
			okB.setPreferredSize(new Dimension(100, 20));
			JPanel bottomp = new JPanel(new BorderLayout());
			bottomp.setPreferredSize(new Dimension(450, 70));
			bottomp.add(okB,BorderLayout.CENTER);

			this.add(bottomp, BorderLayout.SOUTH);
			
		}
		
		class SelectEdgeAttributePanel extends JPanel{
			JComboBox<String> selectattris;
			ArrayList<String> attributes;
			
			SelectEdgeAttributePanel(){
				selectattris = new JComboBox<String>();
				getAttributes();
				selectattris.addActionListener(new SelectAttributeAction());
				this.add( new JLabel("Select an existing attribute as weight"));
				this.add(selectattris);
				//this.setPreferredSize(new Dimension(200, 200));
				this.setBorder(BorderFactory.createTitledBorder(""));
				this.setPreferredSize(new Dimension(140,105));
			}
			
			void getAttributes(){
				for(CyColumn cm : network.getDefaultEdgeTable().getColumns()){			
					if(!cm.equals("name") && !cm.equals("SUID") && !cm.equals("shared name") && !cm.equals("selected"))
						selectattris.addItem(cm.getName());
				}
			}
		
			
			
			class SelectAttributeAction extends AbstractAction{

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JComboBox<String> jcb = (JComboBox<String>) e.getSource();
					EdgeWeight = network.getDefaultEdgeTable().getColumn((String) jcb.getSelectedItem());	
					System.out.println(EdgeWeight.getName()+"$$$$$$");
				}
				
			}
		}
		class OkAction extends AbstractAction{

			@Override
			public void actionPerformed(ActionEvent e) throws IllegalArgumentException{
				// TODO Auto-generated method stub
	
				if(!EdgeWeight.getType().equals(Double.class) && !EdgeWeight.getType().equals(Integer.class)){
						
					JOptionPane.showMessageDialog(null,
    	            		"Data type must be double or integer!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;		
				}					
				else{					
					
					try{
						
					/*	if(EdgeWeight.getType().equals(Integer.class)){
							
							Integer i;
							network.getDefaultEdgeTable().createColumn("weight", Double.class, false);
							for(CyEdge edge : network.getEdgeList()){
								i = network.getRow(edge).get(EdgeWeight.getName(), Integer.class);
								network.getRow(edge).set("weight", i.doubleValue());
							}	
							network.getDefaultEdgeTable().deleteColumn(EdgeWeight.getName());
						}else{*/
							EdgeWeight.setName("weight");
					//	}
					}
					catch(IllegalArgumentException ie){
						
						
						JOptionPane.showMessageDialog(null,
	    	            		"There already exist an attribute named weight!", "Interrupted", JOptionPane.WARNING_MESSAGE);
						return;
					}
						
					
					
					JOptionPane.showMessageDialog(null,
    	            		"Upload Success!", "Interrupted", JOptionPane.WARNING_MESSAGE);
					return;
				}
	
			}
			
		}
		
	}
	


}
