package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.border.Border;



import org.cytoscape.CytoNCA.internal.ParameterSet;
import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;


import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;




public class MainPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;
	private CySwingApplication desktopApp;
	ParameterSet currentParamsCopy; // store panel fields
	private final ProteinUtil pUtil;
	private final List<CyAction> actions;
	private JPanel bottomPanel;
	private OpenfilePanel openfilePanel;
	private JPanel scopePanel;
	//private JPanel advancedOptionsPanel;
	
	private JCheckBox BCButton;
	private JCheckBox CCButton;
	private JCheckBox DCButton;
	private JCheckBox ECButton;
	private JCheckBox LACButton;
	private JCheckBox NCButton;
	private JCheckBox SCButton; 
	private JCheckBox ICButton;
	private JCheckBox SelectAll;
	private JCheckBox BCButtonW;
	private JCheckBox CCButtonW;
	private JCheckBox DCButtonW;
	private JCheckBox ECButtonW;
	private JCheckBox LACButtonW;
	private JCheckBox NCButtonW;
	private JCheckBox SCButtonW; 
	private JCheckBox ICButtonW;
	private JCheckBox SelectAllW;
	private DecimalFormat decimal; // used in the formatted text fields
	private JScrollPane algorithmPanel;
	private UploadBioinfoPanel uploadbioinfopanel;
	private JButton uploadBioinfButton;
 

    


	 public MainPanel(CySwingApplication swingApplication, ProteinUtil pUtil)
	  {
	    this.desktopApp = swingApplication;
	    this.pUtil = pUtil;
	    this.actions = new ArrayList();
	    setLayout(new BorderLayout());
	    

	    this.currentParamsCopy = this.pUtil.getCurrentParameters().getParamsCopy(null);
	    this.currentParamsCopy.setDefaultParams();

		JScrollPane algorithmPanel = createAlgorithmPanel();
		this.openfilePanel = new OpenfilePanel();
		this.uploadbioinfopanel = null;
		
		uploadBioinfButton = new JButton("Upload Bioinformation");
		uploadBioinfButton.addActionListener(new UploadBioinfoAciton());
		
	
		this.add(algorithmPanel, BorderLayout.NORTH);
		add(getBottomPanel(), BorderLayout.CENTER);
		this.add(openfilePanel, BorderLayout.SOUTH);
		
	//	getBottomPanel().add(uploadBioinfButton);
		
		this.setVisible(true);
		this.setPreferredSize(new Dimension(300, 700));

	//	clusteringPanel.getContentPane().remove(clusteringContent);
     //   clusteringPanel.getContentPane().add(customizeClusteringContent, BorderLayout.NORTH);
        
	  }

	 public Component getComponent() {
			return this;
		}


	 public CytoPanelName getCytoPanelName() {
			return CytoPanelName.WEST;
		}


	 public String getTitle() {
			return "CytoNCA";
		}


	 public Icon getIcon() {
			ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo.jpg"));
			return icon;
		}

		
	 public ParameterSet getCurrentParamsCopy() {
			    return this.currentParamsCopy;
			  }


	 
	 private JPanel getBottomPanel()
	  {
	    if (this.bottomPanel == null) {
	      this.bottomPanel = new JPanel();
	      setPreferredSize(new Dimension(270, 100));
	      this.bottomPanel.setLayout(new FlowLayout());
	    //  this.bottomPanel.setLayout(new BoxLayout(this.bottomPanel, BoxLayout.X_AXIS));
	    }

	    return this.bottomPanel;
	  }
	
	
	private JScrollPane createAlgorithmPanel() {
		JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setPreferredSize(new Dimension(270, 420));
	
        
        
        CCButton = new JCheckBox("without weight");
        DCButton = new JCheckBox("without weight");
        ECButton = new JCheckBox("without weight");
        LACButton = new JCheckBox("without weight");
        NCButton = new JCheckBox("without weight");
        SCButton = new JCheckBox("without weight");
        BCButton = new JCheckBox("without weight");
        ICButton = new JCheckBox("without weight");
        SelectAll= new JCheckBox("without weight");
    /*    
        CCButton.setToolTipText("Closeness Centrality (CC)");
        DCButton.setToolTipText("Degree Centrality (DC)");
        ECButton.setToolTipText("Eigenvector Centrality (EC)");
        LACButton.setToolTipText("Local Average Connectivity-based method (LAC)");
        NCButton.setToolTipText("Network Centrality (NC)");
        SCButton.setToolTipText("Subgraph Centrality (SC)");
        BCButton.setToolTipText("Betweeness Centrality (BC)");
        ICButton.setToolTipText("Information Centrality (IC)");
        SelectAll.setToolTipText("Select All Algorithms");
*/
     
        CCButton.addItemListener(new AlgorithmAction());
        DCButton.addItemListener(new AlgorithmAction());
        ECButton.addItemListener(new AlgorithmAction());
        LACButton.addItemListener(new AlgorithmAction());
        NCButton.addItemListener(new AlgorithmAction());
        SCButton.addItemListener(new AlgorithmAction());
        BCButton.addItemListener(new AlgorithmAction());
        ICButton.addItemListener(new AlgorithmAction());
        SelectAll.addItemListener(new AlgorithmAction());
       
        
        CCButtonW = new JCheckBox("with weight");
        DCButtonW = new JCheckBox("with weight");
        ECButtonW = new JCheckBox("with weight");
        LACButtonW = new JCheckBox("with weight");
        NCButtonW = new JCheckBox("with weight");
        SCButtonW = new JCheckBox("with weight");
        BCButtonW = new JCheckBox("with weight");
        ICButtonW = new JCheckBox("with weight");
        SelectAllW= new JCheckBox("with weight");


        
        JPanel bcP = new JPanel(new GridLayout(1,2));
        bcP.add(BCButton);
        bcP.add(BCButtonW);
        JPanel ccP = new JPanel(new GridLayout(1,2));
        ccP.add(CCButton);
        ccP.add(CCButtonW);
        JPanel dcP = new JPanel(new GridLayout(1,2));
        dcP.add(DCButton);
        dcP.add(DCButtonW);
        JPanel ecP = new JPanel(new GridLayout(1,2));
        ecP.add(ECButton);
        ecP.add(ECButtonW);
        JPanel lacP = new JPanel(new GridLayout(1,2));
        lacP.add(LACButton);
        lacP.add(LACButtonW);
        JPanel ncP = new JPanel(new GridLayout(1,2));
        ncP.add(NCButton);
        ncP.add(NCButtonW);
        JPanel scP = new JPanel(new GridLayout(1,2));
        scP.add(SCButton);
        scP.add(SCButtonW);
        JPanel icP = new JPanel(new GridLayout(1,2));
        icP.add(ICButton);
        icP.add(ICButtonW);
        JPanel selectP = new JPanel(new GridLayout(1,2));
        selectP.add(SelectAll);
        selectP.add(SelectAllW);
        
          
        CCButtonW.addItemListener(new AlgorithmWithWeightAction());
        DCButtonW.addItemListener(new AlgorithmWithWeightAction());
        ECButtonW.addItemListener(new AlgorithmWithWeightAction());
        LACButtonW.addItemListener(new AlgorithmWithWeightAction());
        NCButtonW.addItemListener(new AlgorithmWithWeightAction());
        SCButtonW.addItemListener(new AlgorithmWithWeightAction());
        BCButtonW.addItemListener(new AlgorithmWithWeightAction());
        ICButtonW.addItemListener(new AlgorithmWithWeightAction());
        SelectAllW.addItemListener(new AlgorithmWithWeightAction());
       
     
       
        ccP.setToolTipText("Closeness Centrality (CC)");
        dcP.setToolTipText("Degree Centrality (DC)");
        ecP.setToolTipText("Eigenvector Centrality (EC)");
        lacP.setToolTipText("Local Average Connectivity-based method (LAC)");
        ncP.setToolTipText("Network Centrality (NC)");
        scP.setToolTipText("Subgraph Centrality (SC)");
        bcP.setToolTipText("Betweenness Centrality (BC)");
        icP.setToolTipText("Information Centrality (IC)");
        selectP.setToolTipText("Select All Algorithms");
       
        bcP.setBorder(BorderFactory.createTitledBorder("Betweenness(BC)"));
        choicePanel.add(bcP);
        ccP.setBorder(BorderFactory.createTitledBorder("Closeness(CC)"));
        choicePanel.add(ccP);
        dcP.setBorder(BorderFactory.createTitledBorder("Degree(DC)"));
        choicePanel.add(dcP);
        ecP.setBorder(BorderFactory.createTitledBorder("Eigenvector(EC)"));
        choicePanel.add(ecP);
        lacP.setBorder(BorderFactory.createTitledBorder("Local Average Connectivity-based method (LAC)"));
        choicePanel.add(lacP);
        ncP.setBorder(BorderFactory.createTitledBorder("Network(NC)"));
        choicePanel.add(ncP);
        scP.setBorder(BorderFactory.createTitledBorder("Subgraph(SC)"));
        choicePanel.add(scP);
        icP.setBorder(BorderFactory.createTitledBorder("Information(IC)"));
        choicePanel.add(icP);
        selectP.setBorder(BorderFactory.createTitledBorder("Select All Centralities"));
        choicePanel.add(selectP);
        
      //  choicePanel.setToolTipText("Please select one or more centralities.");
        
       
  
        JPanel p=new JPanel();
        p.setLayout(new BorderLayout());
        
        p.add(choicePanel);
       
        JScrollPane scrollPanel = new JScrollPane(p);
        scrollPanel.setBorder(BorderFactory.createTitledBorder("Centralities"));
        return scrollPanel;
    }
	
	
  

    /**
     * Creates a collapsible panel that holds a benchmark file input, placed within the cluster finding collapsible panel
     *
     * @param component the radio button that appears in the titled border of the panel
     * @return A collapsible panel holding a file selection input
     * @see CollapsiblePanel
     */
	private class OpenfilePanel extends JPanel{
		JTextField filenamelabel = new JTextField();
		
		OpenfilePanel() {
			setPreferredSize(new Dimension(270, 120));
			setBorder(BorderFactory.createTitledBorder("Evaluation"));
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	       // panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
	        filenamelabel.setEditable(false);
	        filenamelabel.setPreferredSize(new Dimension(270, 15));
	        JLabel openfilelabel = new JLabel("Import essential protein information file");
	        JButton openfileButton = new JButton("Choose file");
	        openfileButton.addActionListener(new OpenfileAction());
	    //    JButton openEplistButton = new JButton("Open Essential Protein List");
	        this.add(openfilelabel);
	        this.add(openfileButton);
	        this.add(filenamelabel);
	   //     openEplistButton.addActionListener(new OpenEplistAction());
	    //    this.add(openEplistButton);
	    }
		
		
	    
	    private class OpenfileAction extends AbstractAction {
	    	public void actionPerformed(ActionEvent e) {
	    					
	    			JFileChooser fc = new JFileChooser();
	    			ArrayList eplist = new ArrayList();
	    			File f;
	    			int flag = 0;;
	    			fc.setDialogTitle("Open File");   
	    			try{    
	    				flag=fc.showOpenDialog(null);    
	    			}catch(HeadlessException head){    
	    				JOptionPane.showMessageDialog(desktopApp.getJFrame(),
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
	    							eplist.add(text);
	    							System.out.println(text);
	    						}		
	    						JOptionPane.showMessageDialog(desktopApp.getJFrame(),
	    								"Upload essential protrin list success!", "", JOptionPane.WARNING_MESSAGE);
	    						pUtil.setAlleprotein(eplist); 
	    						filenamelabel.setText(f.getName());
	    				//		pUtil.setEvaluation(true);
	    					}catch (IOException e1) {
	    						// TODO Auto-generated catch block
	    						e1.printStackTrace();
	    						JOptionPane.showMessageDialog(desktopApp.getJFrame(),
	    	    	            		"Read data ERROR!", "Interrupted", JOptionPane.WARNING_MESSAGE);
	    					}
	    				}
	    				else {
	    					JOptionPane.showMessageDialog(desktopApp.getJFrame(),
	        	            		"Please Upload Text file!", "Interrupted", JOptionPane.WARNING_MESSAGE);
	    					return;
	    				}
	    		 }  
	    		
	    			 
	    		 
	    		 
	    	 }
	    	
	    	
	    }
	}
	

	/* private class OpenEplistAction extends AbstractAction {
	    	public void actionPerformed(ActionEvent e) {
	    		
	    	}
	   }
	
	*/
    
    public void addAction(CyAction action, int name)
    {
    	if(name == ParameterSet.analyze){
    		JButton bt = new JButton(action);
    	    getBottomPanel().add(bt);
    	    getBottomPanel().add(uploadBioinfButton);
    	}
       	if(name == ParameterSet.openeplist){
       		JButton bt = new JButton(action);
    	    openfilePanel.add(bt);
       	}
   
      this.actions.add(action);
    }
   
    
  
    
   
    /**
     * Handles the press of a algorithm option. Makes sure that appropriate options
     * inputs are added and removed depending on which algorithm is selected
     */
    private class AlgorithmAction implements ItemListener {
    	
    	public void itemStateChanged(ItemEvent e) {
            JCheckBox alg = (JCheckBox) e.getSource();
            if(alg.isSelected()){
            	if(alg.equals(CCButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.CC, true);
            	else if(alg.equals(DCButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.DC, true);
            	else if(alg.equals(ECButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.EC, true);
            	else if(alg.equals(LACButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.LAC, true);
            	else if(alg.equals(NCButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.NC, true);
            	else if(alg.equals(SCButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.SC, true);   
            	else if(alg.equals(BCButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.BC, true);
            	else if(alg.equals(ICButton))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.IC, true);
            	else if(alg.equals(SelectAll)){
            		CCButton.setSelected(true);
            		DCButton.setSelected(true);
            		ECButton.setSelected(true);
            		LACButton.setSelected(true);
            		NCButton.setSelected(true);
            		SCButton.setSelected(true);
            		BCButton.setSelected(true);
            		ICButton.setSelected(true);
            	}
            		
            }
            else{
            	if(alg.equals(SelectAll)){
            		CCButton.setSelected(false);
            		DCButton.setSelected(false);
            		ECButton.setSelected(false);
            		LACButton.setSelected(false);
            		NCButton.setSelected(false);
            		SCButton.setSelected(false);
            		BCButton.setSelected(false);
            		ICButton.setSelected(false);
            		
            	}else{
            	//	SelectAll.setSelected(false);
            		if(alg.equals(CCButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.CC, false);
                	else if(alg.equals(DCButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.DC, false);
                	else if(alg.equals(ECButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.EC, false);
                	else if(alg.equals(LACButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.LAC, false);
                	else if(alg.equals(NCButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.NC, false);
                	else if(alg.equals(SCButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.SC, false);   
                	else if(alg.equals(BCButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.BC, false);
                	else if(alg.equals(ICButton))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.IC, false);
            	}
            	
            	
            }
        }
    }

    private class AlgorithmWithWeightAction implements ItemListener {
    	
    	public void itemStateChanged(ItemEvent e) {
            JCheckBox alg = (JCheckBox) e.getSource();
            if(alg.isSelected()){
            	if(alg.equals(CCButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.CCW, true);
            	else if(alg.equals(DCButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.DCW, true);
            	else if(alg.equals(ECButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.ECW, true);
            	else if(alg.equals(LACButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.LACW, true);
            	else if(alg.equals(NCButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.NCW, true);
            	else if(alg.equals(SCButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.SCW, true);   
            	else if(alg.equals(BCButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.BCW, true);
            	else if(alg.equals(ICButtonW))
            		currentParamsCopy.getAlgorithmSet().put(ParameterSet.ICW, true);
            	else if(alg.equals(SelectAllW)){
            		CCButtonW.setSelected(true);
            		DCButtonW.setSelected(true);
            		ECButtonW.setSelected(true);
            		LACButtonW.setSelected(true);
            		NCButtonW.setSelected(true);
            		SCButtonW.setSelected(true);
            		BCButtonW.setSelected(true);
            		ICButtonW.setSelected(true);
            	}
            		
            }
            else{
            	if(alg.equals(SelectAllW)){
            		CCButtonW.setSelected(false);
            		DCButtonW.setSelected(false);
            		ECButtonW.setSelected(false);
            		LACButtonW.setSelected(false);
            		NCButtonW.setSelected(false);
            		SCButtonW.setSelected(false);
            		BCButtonW.setSelected(false);
            		ICButtonW.setSelected(false);
            	}else{
            	//	SelectAllW.setSelected(false);
            		if(alg.equals(CCButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.CCW, false);
                	else if(alg.equals(DCButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.DCW, false);
                	else if(alg.equals(ECButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.ECW, false);
                	else if(alg.equals(LACButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.LACW, false);
                	else if(alg.equals(NCButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.NCW, false);
                	else if(alg.equals(SCButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.SCW, false);   
                	else if(alg.equals(BCButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.BCW, false);
                	else if(alg.equals(ICButtonW))
                		currentParamsCopy.getAlgorithmSet().put(ParameterSet.ICW, false);
            	}
            	
            	
           
            }
        }
    }

    private class UploadBioinfoAciton extends AbstractAction{

	@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(uploadbioinfopanel == null || !uploadbioinfopanel.isDisplayable()){
				if(pUtil.getApplicationMgr().getCurrentNetwork() != null){
					uploadbioinfopanel = new UploadBioinfoPanel(pUtil);
				}
				else{
					JOptionPane.showMessageDialog(null/*Cytoscape.getDesktop()*/,
		                    "Network has not been loaded!", "Error", JOptionPane.WARNING_MESSAGE);
		            return;
				}
					
			}else if(uploadbioinfopanel.getState() == Frame.ICONIFIED){
				uploadbioinfopanel.setState(Frame.NORMAL);			
			}
		}
	   
   }


	public UploadBioinfoPanel getUploadbioinfopanel() {
		return uploadbioinfopanel;
	}

	public void setUploadbioinfopanel(UploadBioinfoPanel uploadbioinfopanel) {
		this.uploadbioinfopanel = uploadbioinfopanel;
	}

   

	
}
