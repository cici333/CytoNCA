package org.cytoscape.CytoNCA.internal.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JToolTip;
import javax.swing.SwingConstants;



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
	private JPanel advancedOptionsPanel;
	JCheckBox BCButton;
	JCheckBox CCButton;
	JCheckBox DCButton;
	JCheckBox ECButton;
	JCheckBox LACButton;
	JCheckBox NCButton;
	JCheckBox SCButton; 
	JCheckBox ICButton;
	JCheckBox SelectAll;
	  
 //   ClusterPlugin.MainPanelAction trigger;
//    ClusterVisualStyle vistyle;
    DecimalFormat decimal; // used in the formatted text fields
    JScrollPane algorithmPanel;
 

    


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
	
		this.add(algorithmPanel, BorderLayout.NORTH);
		add(getBottomPanel(), BorderLayout.CENTER);
		this.add(openfilePanel, BorderLayout.SOUTH);
		
		
		
		this.setVisible(true);
		this.setPreferredSize(new Dimension(300, 700));

	//	clusteringPanel.getContentPane().remove(clusteringContent);
     //   clusteringPanel.getContentPane().add(customizeClusteringContent, BorderLayout.NORTH);
        
	  }


	 
	 private JPanel getBottomPanel()
	  {
	    if (this.bottomPanel == null) {
	      this.bottomPanel = new JPanel();
	      setPreferredSize(new Dimension(270, 50));
	      this.bottomPanel.setLayout(new FlowLayout());
	    }

	    return this.bottomPanel;
	  }
	
	
	private JScrollPane createAlgorithmPanel() {
		JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setPreferredSize(new Dimension(270, 220));
	
		CCButton = new JCheckBox("Closeness Centrality (CC)");
        DCButton = new JCheckBox("Degree Centrality (DC)");
        ECButton = new JCheckBox("Eigenvector Centrality (EC)");
        LACButton = new JCheckBox("Local Average Connectivity-based method (LAC)");
        NCButton = new JCheckBox("Network Centrality (NC)");
        SCButton = new JCheckBox("Subgraph Centrality (SC)");
        BCButton = new JCheckBox("Betweeness Centrality (BC)");
        ICButton = new JCheckBox("Information Centrality (IC)");
        SelectAll= new JCheckBox("SelectAll");
        
        CCButton.setToolTipText("Closeness Centrality (CC)");
        DCButton.setToolTipText("Degree Centrality (DC)");
        ECButton.setToolTipText("Eigenvector Centrality (EC)");
        LACButton.setToolTipText("Local Average Connectivity-based method (LAC)");
        NCButton.setToolTipText("Network Centrality (NC)");
        SCButton.setToolTipText("Subgraph Centrality (SC)");
        BCButton.setToolTipText("Betweeness Centrality (BC)");
        ICButton.setToolTipText("Information Centrality (IC)");
        SelectAll.setToolTipText("Select All Algorithms");

     
        CCButton.addItemListener(new AlgorithmAction());
        DCButton.addItemListener(new AlgorithmAction());
        ECButton.addItemListener(new AlgorithmAction());
        LACButton.addItemListener(new AlgorithmAction());
        NCButton.addItemListener(new AlgorithmAction());
        SCButton.addItemListener(new AlgorithmAction());
        BCButton.addItemListener(new AlgorithmAction());
        ICButton.addItemListener(new AlgorithmAction());
        SelectAll.addItemListener(new AlgorithmAction());
       

        choicePanel.add(BCButton);
        choicePanel.add(CCButton);
        choicePanel.add(DCButton);
        choicePanel.add(ECButton);
        choicePanel.add(LACButton);
        choicePanel.add(NCButton);
        choicePanel.add(SCButton);
        choicePanel.add(ICButton);
        choicePanel.add(SelectAll);
        
              
        choicePanel.setToolTipText("Please select an algorithm");
  
        JPanel p=new JPanel();
        p.setLayout(new BorderLayout());
        
        p.add(choicePanel,BorderLayout.NORTH);
        JScrollPane scrollPanel = new JScrollPane(p);
        scrollPanel.setBorder(BorderFactory.createTitledBorder("Algorithm"));
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
		JLabel filenamelabel = new JLabel();
		
		OpenfilePanel() {
			setPreferredSize(new Dimension(270, 300));
			setBorder(BorderFactory.createTitledBorder("Evaluation"));
	        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	       // panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
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
            	else if(alg.equals(SelectAll)){
            		CCButton.setSelected(false);
            		DCButton.setSelected(false);
            		ECButton.setSelected(false);
            		LACButton.setSelected(false);
            		NCButton.setSelected(false);
            		SCButton.setSelected(false);
            		BCButton.setSelected(false);
            		ICButton.setSelected(false);
            	}
            }
        }
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
}
