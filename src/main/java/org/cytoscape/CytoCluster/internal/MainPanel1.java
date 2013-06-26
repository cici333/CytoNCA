package org.cytoscape.CytoCluster.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;

import org.cytoscape.CytoCluster.internal.algorithmPanels.EAGLEpanel;
import org.cytoscape.CytoCluster.internal.algorithmPanels.FAGECPanel;
import org.cytoscape.CytoCluster.internal.algorithmPanels.HCPINpanel;
import org.cytoscape.CytoCluster.internal.algorithmPanels.IPCApanel;
import org.cytoscape.CytoCluster.internal.algorithmPanels.MCODEpanel;
import org.cytoscape.CytoCluster.internal.algorithmPanels.OHPINpanel;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;


import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;



public class MainPanel1 extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;
	private CySwingApplication desktopApp;
	private CyApplicationManager cyApplicationManagerServiceRef;

	public ParameterSet currentParamsCopy; // store panel fields
	
	
	
	  private final ClusterUtil mcodeUtil;
	  private final List<CyAction> actions;
	  private JPanel bottomPanel;
	  private JPanel scopePanel;
	  private JPanel advancedOptionsPanel;
	  
	  
	  
 //   ClusterPlugin.MainPanelAction trigger;
//    ClusterVisualStyle vistyle;
    DecimalFormat decimal; // used in the formatted text fields
    JScrollPane algorithmPanel;
    CollapsiblePanel clusteringPanel;
    CollapsiblePanel customizePanel;
    JPanel clusteringContent;
    JPanel customizeClusteringContent;
    MCODEpanel MCODE;
	EAGLEpanel EAGLE;
	FAGECPanel FAGEC;
	HCPINpanel HCPIN;
	OHPINpanel OHPIN;
	IPCApanel IPCA;
    JPanel weakPanel;
    JPanel cliqueSizePanel;

    //resetable UI elements
    //Scoring
    JCheckBox includeLoops;
    JFormattedTextField degreeThreshold;
    //clustering
    JFormattedTextField kCore;
    JFormattedTextField nodeScoreThreshold;
    JRadioButton optimize;
    JRadioButton customize;

    JCheckBox haircut;
    JCheckBox fluff;
    JFormattedTextField nodeDensityThreshold;
    JFormattedTextField maxDepth;
    DecimalFormat decFormat;
    
    
    
    
/*    
	public MainPanel() {
		
		
		currentParameters = ParameterSet.getInstance().getParamsCopy(null);
		JPanel scopePanel = createScopePanel();
		JScrollPane algorithmPanel = createAlgorithmPanel();
		JPanel bottomPanel = createBottomPanel();
		
		
		System.out.println(currentParameters.networkID+"%%%%%");	
		//System.out.println(currentParameters.algorithm+"$$$");
		System.out.println(currentParameters.getAlgorithm()+"^^^^");
		System.out.println(currentParameters.getScope()+"^^^^");
	
		this.add(scopePanel,BorderLayout.NORTH);
		this.add(algorithmPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(300, 800));
		
		clusteringPanel.getContentPane().remove(clusteringContent);
        clusteringPanel.getContentPane().add(customizeClusteringContent, BorderLayout.NORTH);
        
	}*/


	 public MainPanel1(CySwingApplication swingApplication, ClusterUtil mcodeUtil)
	  {
	    this.desktopApp = swingApplication;
	    this.mcodeUtil = mcodeUtil;
	    this.actions = new ArrayList();

	    setLayout(new BorderLayout());
	    

	    this.currentParamsCopy = this.mcodeUtil.getCurrentParameters().getParamsCopy(null);
	    this.currentParamsCopy.setDefaultParams();
	    
	    if(currentParamsCopy.isIncludeLoops())
	    	System.out.println("true tt");
	    else
	    	System.out.println("false tt");
	    
	    
		JScrollPane algorithmPanel = createAlgorithmPanel();
		
		

	    this.decFormat = new DecimalFormat();
	    this.decFormat.setParseIntegerOnly(true);

		this.add(getScopePanel(),BorderLayout.NORTH);
		this.add(algorithmPanel, BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(300, 800));

		//clusteringPanel.getContentPane().remove(clusteringContent);
        //clusteringPanel.getContentPane().add(customizeClusteringContent, BorderLayout.NORTH);
        
	  }

	 
	  private JPanel getScopePanel()
	  {
	    if (this.scopePanel == null) {
	      this.scopePanel = new JPanel();
	      this.scopePanel.setLayout(new BoxLayout(this.scopePanel, 1));
	      this.scopePanel.setBorder(BorderFactory.createTitledBorder("Find Cluster(s)"));

	      JRadioButton scopeNetwork = new JRadioButton("in Whole Network", this.currentParamsCopy.getScope()
	        .equals(ParameterSet.NETWORK));
	      JRadioButton scopeSelection = new JRadioButton("from Selection", this.currentParamsCopy.getScope()
	        .equals(ParameterSet.SELECTION));

	      scopeNetwork.setActionCommand(ParameterSet.NETWORK);
	      scopeSelection.setActionCommand(ParameterSet.SELECTION);

	      scopeNetwork.addActionListener(new ScopeAction());
	      scopeSelection.addActionListener(new ScopeAction());

	      ButtonGroup scopeOptions = new ButtonGroup();
	      scopeOptions.add(scopeNetwork);
	      scopeOptions.add(scopeSelection);

	      this.scopePanel.add(scopeNetwork);
	      this.scopePanel.add(scopeSelection);
	    }

	    return this.scopePanel;
	  }
	 
	 private JPanel getBottomPanel()
	  {
	    if (this.bottomPanel == null) {
	      this.bottomPanel = new JPanel();
	      this.bottomPanel.setLayout(new FlowLayout());
	    }

	    return this.bottomPanel;
	  }
	private JPanel createScopePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Scope"));
        panel.setPreferredSize(new Dimension(300, 100));
        
        JRadioButton scopeNetwork = new JRadioButton("Whole Network", currentParamsCopy.getScope().equals("NETWORK"));
        JRadioButton scopeSelection = new JRadioButton("Selected", currentParamsCopy.getScope().equals("SELECTION"));

        scopeNetwork.setActionCommand(ParameterSet.NETWORK);
        scopeSelection.setActionCommand(ParameterSet.SELECTION);
        scopeNetwork.addActionListener(new ScopeAction());
        scopeSelection.addActionListener(new ScopeAction());
        ButtonGroup scopeOptions = new ButtonGroup();
        scopeOptions.add(scopeNetwork);
        scopeOptions.add(scopeSelection);
        panel.add(scopeNetwork);
        panel.add(scopeSelection);
        //panel.setPreferredSize(new java.awt.Dimension(185, 164));
        panel.setToolTipText("Please select scope for clustring");
        return panel;
    }
	
	
	private JScrollPane createAlgorithmPanel() {
    	JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setPreferredSize(new Dimension(290, 200));
     
        JRadioButton MCODEButton = new JRadioButton("MCODE", currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE));
		JRadioButton EAGLEButton = new JRadioButton("EAGLE", currentParamsCopy.getAlgorithm().equals(ParameterSet.EAGLE));
        JRadioButton FAGECButton = new JRadioButton("FAG-EC", currentParamsCopy.getAlgorithm().equals(ParameterSet.FAGEC));
        JRadioButton HCPINButton = new JRadioButton("HC-PIN", currentParamsCopy.getAlgorithm().equals(ParameterSet.HCPIN));
        JRadioButton OHPINButton = new JRadioButton("OH-PIN", currentParamsCopy.getAlgorithm().equals(ParameterSet.HCPIN));
        JRadioButton IPCAButton = new JRadioButton("IPCA", currentParamsCopy.getAlgorithm().equals(ParameterSet.IPCA));

      /*      
        JRadioButton algorithm1 = new JRadioButton("MCODE");
		JRadioButton EAGLEButton = new JRadioButton("EAGLE");
        JRadioButton FAGECButton = new JRadioButton("FAG-EC");
     */
        MCODEButton.setToolTipText("Use K-Core-based MCODE algorithm.\nA K-Core is a subgraph with minimum degree of k");
        EAGLEButton.setToolTipText("Use maximal clique-based EAGLE algorithm.\n Overlapped clusters can be identified");
        FAGECButton.setToolTipText("Use fast hierarchical agglomerative FAG-EC algorithm");
        HCPINButton.setToolTipText("Use A fast hierarchical clustering algorithm .\n for functional modules discovery in protein .\n interaction networks");
        OHPINButton.setToolTipText("Use A fast hierarchical clustering algorithm .\n for functional modules discovery in protein .\n interaction networks");
        IPCAButton.setToolTipText("Use A clustering algorithm based on the new topological structure makes it possible to \n for identifing dense subgraphs in protein interaction networks .\n ");

        
        MCODEButton.setActionCommand(ParameterSet.MCODE);
        EAGLEButton.setActionCommand(ParameterSet.EAGLE);
        FAGECButton.setActionCommand(ParameterSet.FAGEC);
        HCPINButton.setActionCommand(ParameterSet.HCPIN);
        OHPINButton.setActionCommand(ParameterSet.OHPIN);
        IPCAButton.setActionCommand(ParameterSet.IPCA);
        MCODEButton.addActionListener(new AlgorithmAction());
        EAGLEButton.addActionListener(new AlgorithmAction());
        FAGECButton.addActionListener(new AlgorithmAction());
        HCPINButton.addActionListener(new AlgorithmAction());
        OHPINButton.addActionListener(new AlgorithmAction());
        IPCAButton.addActionListener(new AlgorithmAction());

        ButtonGroup algorithmOptions = new ButtonGroup();
        algorithmOptions.add(MCODEButton);
        algorithmOptions.add(EAGLEButton);
        algorithmOptions.add(FAGECButton);
        algorithmOptions.add(HCPINButton);
        algorithmOptions.add(OHPINButton);
        algorithmOptions.add(IPCAButton);
        choicePanel.add(FAGECButton);
        choicePanel.add(MCODEButton);
        choicePanel.add(EAGLEButton);
        choicePanel.add(HCPINButton);
        choicePanel.add(OHPINButton);
        choicePanel.add(IPCAButton);
        choicePanel.setToolTipText("Please select an algorithm");
        
      JPanel options=new JPanel();        
        options.setLayout(new BoxLayout(options,BoxLayout.Y_AXIS));
       
        
        EAGLE = new EAGLEpanel(this.desktopApp,this.mcodeUtil);
        EAGLE.setVisible(currentParamsCopy.getAlgorithm().equals("EAGLE"));
   

        FAGEC = new FAGECPanel(this.desktopApp,this.mcodeUtil);
        FAGEC.setVisible(currentParamsCopy.getAlgorithm().equals("FAG-EC"));
        
        
	
        
        HCPIN = new HCPINpanel(this.desktopApp,this.mcodeUtil);
        HCPIN.setVisible(currentParamsCopy.getAlgorithm().equals("HC-PIN"));
        
        OHPIN = new OHPINpanel(this.desktopApp,this.mcodeUtil);
        OHPIN.setVisible(currentParamsCopy.getAlgorithm().equals("OH-PIN"));
        
        IPCA = new IPCApanel(this.desktopApp,this.mcodeUtil);
        IPCA.setVisible(currentParamsCopy.getAlgorithm().equals("OH-PIN"));
        
        MCODE = new MCODEpanel(this.desktopApp,this.mcodeUtil);
        MCODE.setVisible(currentParamsCopy.getAlgorithm().equals("MCODE")); 
  
        options.add(MCODE);
        options.add(EAGLE);
        options.add(FAGEC);
        options.add(HCPIN);
        options.add(OHPIN);
        options.add(IPCA);
        
        JPanel p=new JPanel();
        p.setLayout(new BorderLayout());
        
        p.add(choicePanel,BorderLayout.NORTH);
        p.add(options,BorderLayout.CENTER);
        JScrollPane scrollPanel = new JScrollPane(p);
        scrollPanel.setBorder(BorderFactory.createTitledBorder("Algorithm"));
        return scrollPanel;
    }
	
	/**
     * Creates a collapsible panel that holds 2 other collapsible panels 
     * for inputing scoring and clustering parameter
     *
     * @return collapsiblePanel
     */
  /*  private JPanel createOptionsPanel1() {
    	JPanel retPanel=new JPanel();
        retPanel.setLayout(new BorderLayout());
        retPanel.setBorder(BorderFactory.createTitledBorder(""));
    	
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("MCODE Options");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        CollapsiblePanel createScoringPanel = createScoringPanel();
        clusteringPanel = createClusteringPanel();
        panel.add(createScoringPanel);
        panel.add(clusteringPanel);
        collapsiblePanel.getContentPane().add(panel,BorderLayout.NORTH);
        collapsiblePanel.setToolTipText("Customize clustering parameters (Optional)");
        retPanel.add(collapsiblePanel);
        return retPanel;
    }    
    
    
    /**
     * Create a collapsible panel that holds network scoring parameter inputs
     *
     * @return panel containing the network scoring parameter inputs
     */
/*   private CollapsiblePanel createScoringPanel() {
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Scoring");        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        
        //Include Loop input
        JLabel includeLoopsLabel = new JLabel("Include Loop");
        includeLoops = new JCheckBox() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
       includeLoops.addItemListener(new MainPanel.IncludeLoopsCheckBoxAction());
        String includeLoopsTip = "Regard loops when clustering.\n" +
                "This will influence the score of cliques";
        includeLoops.setToolTipText(includeLoopsTip);
        includeLoops.setSelected(currentParamsCopy.isIncludeLoops());
        JPanel includeLoopsPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        includeLoopsPanel.setLayout(new BorderLayout());
        includeLoopsPanel.setToolTipText(includeLoopsTip);
        includeLoopsPanel.add(includeLoopsLabel, BorderLayout.WEST);
        includeLoopsPanel.add(includeLoops, BorderLayout.EAST);

        //Degree Threshold input
        JLabel degreeThresholdLabel = new JLabel("Degree Threshold");
        degreeThreshold = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        degreeThreshold.setColumns(3);
        degreeThreshold.addPropertyChangeListener("value", new MainPanel.FormattedTextFieldAction());
        String degreeThresholdTip = "degree cutoff of the nodes";
        degreeThreshold.setToolTipText(degreeThresholdTip);
        degreeThreshold.setText((new Integer(currentParamsCopy.getDegreeCutoff()).toString()));
        JPanel degreeThresholdPanel = new JPanel() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        degreeThresholdPanel.setLayout(new BorderLayout());
        degreeThresholdPanel.setToolTipText(degreeThresholdTip);
        degreeThresholdPanel.add(degreeThresholdLabel, BorderLayout.WEST);
        degreeThresholdPanel.add(degreeThreshold, BorderLayout.EAST);
        
        //add the components to the panel
        panel.add(includeLoopsPanel);
        panel.add(degreeThresholdPanel);
        collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsiblePanel;
    }
    
    
    
    /**
     * Creates a collapsible panel that holds 2 other collapsible panels for 
     * either customizing or optimized clustering parameters
     *
     */
	/*    private CollapsiblePanel createClusteringPanel() {
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Clustering");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        customize = new JRadioButton("Customize", !currentParamsCopy.isOptimize());
        optimize = new JRadioButton("Optimize", currentParamsCopy.isOptimize());
        ButtonGroup clusteringChoice = new ButtonGroup();
        clusteringChoice.add(customize);
        clusteringChoice.add(optimize);
        customize.addActionListener(new CustomizeAction());
        optimize.addActionListener(new CustomizeAction());
        
        customizePanel = createClusterParaPanel(customize);
        CollapsiblePanel optimalPanel = createOptimizePanel(optimize);
        panel.add(customizePanel);
        panel.add(optimalPanel);        
        this.clusteringContent = panel;        
        collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsiblePanel;
    }

    /**
     * Creates a collapsible panel that holds clustering parameters
     * placed within the cluster finding collapsible panel
     *
     * @param component Any JComponent that may appear in the titled border of the panel
     * @return collapsablePanel
     */
	/*     private CollapsiblePanel createClusterParaPanel(JRadioButton component) {
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel(component);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Node Score Threshold
        String nodeScoreThresholdTip = "Sets the acceptable score deviance from\n" +
        		"the seed node's score for expanding a cluster\n" +
        		"(most influental parameter for cluster size).";
        JLabel nodeScoreThresholdLabel = new JLabel("NodeScoreThreshold");
        nodeScoreThreshold = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        nodeScoreThreshold.setColumns(3);
        nodeScoreThreshold.addPropertyChangeListener("value", new MainPanel.FormattedTextFieldAction());
        nodeScoreThreshold.setToolTipText(nodeScoreThresholdTip);
        nodeScoreThreshold.setText((new Double(currentParamsCopy.getNodeScoreCutoff()).toString()));
        JPanel nodeScoreThresholdPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        nodeScoreThresholdPanel.setToolTipText(nodeScoreThresholdTip);
        nodeScoreThresholdPanel.add(nodeScoreThresholdLabel, BorderLayout.WEST);
        nodeScoreThresholdPanel.add(nodeScoreThreshold,BorderLayout.EAST);

        //K-Core input
        JLabel kCoreLabel = new JLabel("K-CoreThreshold");
        kCore = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        kCore.setColumns(3);
        kCore.addPropertyChangeListener("value", new MainPanel.FormattedTextFieldAction());
        String kCoreTip = "Filters out clusters lacking a\n" +
        		"maximally inter-connected core\n" +
        		"of at least k edges per node.";
        kCore.setToolTipText(kCoreTip);
        kCore.setText((new Integer(currentParamsCopy.getKCore()).toString()));
        JPanel kCorePanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        kCorePanel.setToolTipText(kCoreTip);
        kCorePanel.add(kCoreLabel, BorderLayout.WEST);
        kCorePanel.add(kCore, BorderLayout.EAST);

        //Haircut Input
        String haircutTip = "Remove singly connected\n" +
        		"nodes from clusters.";
        JLabel haircutLabel = new JLabel("Haircut");
        haircut = new JCheckBox() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        haircut.addItemListener(new MainPanel.HaircutCheckBoxAction());
        haircut.setToolTipText(haircutTip);
        haircut.setSelected(currentParamsCopy.isHaircut());
        JPanel haircutPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        }; 
        haircutPanel.setToolTipText(haircutTip);
        haircutPanel.add(haircutLabel, BorderLayout.WEST);
        haircutPanel.add(haircut, BorderLayout.EAST);

        //Fluffy Input
        JLabel fluffLabel = new JLabel("Fluff");
        fluff = new JCheckBox() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        fluff.addItemListener(new MainPanel.FluffCheckBoxAction());
        String fluffTip = "Expand core cluster by one\n" +
        		"neighbour shell (applied\n"+
        		"after the optional haircut).";
        fluff.setToolTipText(fluffTip);
        fluff.setSelected(currentParamsCopy.isFluff());
        JPanel fluffPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        fluffPanel.setToolTipText(fluffTip);
        fluffPanel.add(fluffLabel, BorderLayout.WEST);
        fluffPanel.add(fluff, BorderLayout.EAST);

        //Fluff node density cutoff input
        JLabel nodeDensityThresholdLabel = new JLabel("threshold");
        nodeDensityThreshold = new JFormattedTextField(new DecimalFormat("0.000")) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        nodeDensityThreshold.setColumns(3);
        nodeDensityThreshold.addPropertyChangeListener("value", new MainPanel.FormattedTextFieldAction());
        String fluffNodeDensityCutoffTip = "Limits fluffing by setting the acceptable\n" +
        		"node density deviance from the core cluster\n" +
        		"density (allows clusters' edges to overlap).";
        nodeDensityThreshold.setToolTipText(fluffNodeDensityCutoffTip);
        nodeDensityThreshold.setText((new Double(currentParamsCopy.getFluffNodeDensityCutoff()).toString()));
        JPanel nodeDensityThresholdPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        nodeDensityThresholdPanel.setToolTipText(fluffNodeDensityCutoffTip);
        nodeDensityThresholdPanel.add(nodeDensityThresholdLabel, BorderLayout.WEST);
        nodeDensityThresholdPanel.add(nodeDensityThreshold, BorderLayout.EAST);
        nodeDensityThresholdPanel.setVisible(currentParamsCopy.isFluff());

        //Max depth input
        JLabel maxDepthLabel = new JLabel("MaxDepth");
        maxDepth = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        maxDepth.setColumns(3);
        maxDepth.addPropertyChangeListener("value", new MainPanel.FormattedTextFieldAction());
        String maxDepthTip = "Limits the cluster size by setting the\n" +
        		"maximum search distance from a seed\n" +
        		"node (100 virtually means no limit).";
        maxDepth.setToolTipText(maxDepthTip);
        maxDepth.setText((new Integer(currentParamsCopy.getMaxDepthFromStart()).toString()));
        JPanel maxDepthPanel = new JPanel(new BorderLayout()) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        maxDepthPanel.setToolTipText(maxDepthTip);
        maxDepthPanel.add(maxDepthLabel, BorderLayout.WEST);
        maxDepthPanel.add(maxDepth, BorderLayout.EAST);
        
        panel.add(haircutPanel);
        panel.add(fluffPanel);
        panel.add(nodeDensityThresholdPanel);
        panel.add(nodeScoreThresholdPanel);
        panel.add(kCorePanel);
        panel.add(maxDepthPanel);
        this.customizeClusteringContent = panel;

        collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsiblePanel;
    }

    /**
     * Creates a collapsible panel that holds a benchmark file input, placed within the cluster finding collapsible panel
     *
     * @param component the radio button that appears in the titled border of the panel
     * @return A collapsible panel holding a file selection input
     * @see CollapsiblePanel
     */
	 /* private CollapsiblePanel createOptimizePanel(JRadioButton component) {
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel(component);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel benchmarkStarter = new JLabel("Benchmark file location");
        JPanel benchmarkStarterPanel = new JPanel(new BorderLayout());
        benchmarkStarterPanel.add(benchmarkStarter, BorderLayout.WEST);
        JFormattedTextField benchmarkFileLocation = new JFormattedTextField();
        JButton browseButton = new JButton("Browse...");
        JPanel fileChooserPanel = new JPanel(new BorderLayout());
        fileChooserPanel.add(benchmarkFileLocation, BorderLayout.SOUTH);
        fileChooserPanel.add(browseButton, BorderLayout.EAST);
        panel.add(benchmarkStarterPanel);
        panel.add(fileChooserPanel);
        collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
        return collapsiblePanel;
    }
*/
    

 	public void addAction(CyAction action)
    {
      JButton bt = new JButton(action);
      getBottomPanel().add(bt);

      this.actions.add(action);
    }

    
    
    /*
     * Utility method that creates a panel for buttons at the bottom of the <code>MainPanel</code>
     *
     * @return a flow layout panel containing the analyze and quite buttons
     */
   private JPanel createBottomPanel() {
    	JPanel bottomPanel=new JPanel();
    	bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.setToolTipText("start the process of analyze");
        
        
 
        
     /*   analyzeButton.addActionListener(new AnalyzeAction( title,  cyApplicationManagerServiceRef,  desktopApp, 
        		CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, 
        		TaskManager taskManager, currentParamsCopy,
        		ClusterUtil util));       */ 
        panel.add(analyzeButton);
        
        JButton closeButton = new JButton("Close");
        closeButton.setToolTipText("terminate the plugin");
       	panel.add(closeButton);
      //addActionListener(new MainPanel.CloseAction(this));
        bottomPanel.add(panel);
        
        JPanel vacantContainer = new JPanel(new BorderLayout());
        bottomPanel.add(vacantContainer);
        vacantContainer.setPreferredSize(new java.awt.Dimension(127, 90));
        return bottomPanel;
    }

    
    
    /**
     * Handles the press of a scope option. 
     */
    private class ScopeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String scope = e.getActionCommand();
            currentParamsCopy.setScope(scope);
        }
    }
    /**
     * Handles the press of a algorithm option. Makes sure that appropriate options
     * inputs are added and removed depending on which algorithm is selected
     */
    private class AlgorithmAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String algorithm = e.getActionCommand();
            currentParamsCopy.setAlgorithm(algorithm);
            
            MCODE.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.MCODE));
            EAGLE.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.EAGLE));
            FAGEC.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.FAGEC));
            HCPIN.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.HCPIN));
            OHPIN.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.OHPIN));
            IPCA.setVisible(currentParamsCopy.getAlgorithm().equals(ParameterSet.IPCA));
        }
    }

    /**
    /**
     * Sets the optimization parameter depending on which radio button is selected (cusomize/optimize)
     */
   private class CustomizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (optimize.isSelected()) {
                currentParamsCopy.setOptimize(true);
            } else {
                currentParamsCopy.setOptimize(false);
            }
        }
    }
    /**
     * Handles the press of the Close button
     */
 /*   private class CloseAction extends AbstractAction {
        MainPanel mainPanel;
        ResultPanel component;
        CloseAction (MainPanel mainPanel) {
            this.mainPanel = mainPanel;
        }
        public void actionPerformed(ActionEvent e) {
            //close all open panels
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            boolean resultsClosed = true;
            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
            for (int c = cytoPanel.getCytoPanelComponentCount() - 1; c >= 0; c--) {
                cytoPanel.setSelectedIndex(c);
                Component component = cytoPanel.getSelectedComponent();
                String componentTitle;
                if (component instanceof ResultPanel) {
                    this.component = (ResultPanel) component;
                    componentTitle = this.component.getResultTitle();
                    String message = "Close" + componentTitle + ".\nContiune?";
                    int result = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new Object[] {message}, "Comfirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                    if (result == JOptionPane.YES_OPTION){
                        cytoPanel.remove(component);
                        ParameterSet.removeResultParams(componentTitle);
                    } else {
                        resultsClosed = false;
                    }
                }
            }
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
                cytoPanel.setState(CytoPanelState.HIDE);
            }
            if (resultsClosed) {
                cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);
                cytoPanel.remove(mainPanel);
                trigger.setOpened(false);
            }
        }
    }

    /**
     * Handles setting of the include loops parameter
     */
  /*  private class IncludeLoopsCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setIncludeLoops(false);
            } else {
                currentParamsCopy.setIncludeLoops(true);
            }
        }
    }

    /**
     * Handles setting for the text field parameters that are numbers.
     * Makes sure that the numbers make sense.
     */
    /*    private class FormattedTextFieldAction implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField source = (JFormattedTextField) e.getSource();
            String message = "Invaluled input\n";
            boolean invalid = false;
            try {
            
            	
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            if (source == degreeThreshold) {
            	
            	Number value = format.parse((String) degreeThreshold.getValue());
				
            	
                //Number value = (Number) degreeThreshold.getValue();
                if ((value != null) && (value.intValue() > 1)) {
                    currentParamsCopy.setDegreeCutoff(value.intValue());
                } else {
                    source.setValue(new Integer (2));
                    message += "the node degree cutoff should no less than 2.";
                    invalid = true;
                }
            } else if (source == nodeScoreThreshold) {
                Number value =  format.parse((String) nodeScoreThreshold.getValue());
                if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1.0)) {
                    currentParamsCopy.setNodeScoreCutoff(value.doubleValue());
                } else {
                    source.setValue(new Double (currentParamsCopy.getNodeScoreCutoff()));
                    message += "the node score cutoff should set between 0 and 1.";
                    invalid = true;
                }
            } else if (source == kCore) {
                Number value =  format.parse((String) kCore.getValue());
                if ((value != null) && (value.intValue() > 1)) {
                    currentParamsCopy.setKCore(value.intValue());
                } else {
                    source.setValue(new Integer (2));
                    message += "the k value of K-Core should be greater than 1.";
                    invalid = true;
                }
            } else if (source == maxDepth) {
                Number value =  format.parse((String)maxDepth.getValue());
                if ((value != null) && (value.intValue() > 0)) {
                    currentParamsCopy.setMaxDepthFromStart(value.intValue());
                } else {
                    source.setValue(new Integer (1));
                    message += "max depth should be no less than 1.";
                    invalid = true;
                }
            } else if (source == nodeDensityThreshold) {
                Number value =  format.parse((String)nodeDensityThreshold.getValue());
                if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1.0)) {
                    currentParamsCopy.setFluffNodeDensityCutoff(value.doubleValue());
                } else {
                    source.setValue(new Double (currentParamsCopy.getFluffNodeDensityCutoff()));
                    message += "fluff node density cutoff should\n" +
                    		"be set between 0 and 1.";
                    invalid = true;
                }
            }
			if (invalid) {
				JOptionPane.showMessageDialog(MainPanel.this.desktopApp.getJFrame(), 
				          message, 
				          "Parameter out of bounds", 
				          2);
            }
            } catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }

    /**
     * Handles setting of the haircut parameter
     */
    /*    private class HaircutCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setHaircut(false);
            } else {
                currentParamsCopy.setHaircut(true);
            }
        }
    }

    /**
     * Handles setting of the fluff parameter and showing or hiding of the fluff node density cutoff input
     */
    /*   private class FluffCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                currentParamsCopy.setFluff(false);
            } else {
                currentParamsCopy.setFluff(true);
            }
            nodeDensityThreshold.getParent().setVisible(currentParamsCopy.isFluff());
        }
    }
    
/*	public ClusterPlugin.MainPanelAction getTrigger() {
		return trigger;
	}

	public void setTrigger(ClusterPlugin.MainPanelAction trigger) {
		this.trigger = trigger;
	}
*/

	public Component getComponent() {
		return this;
	}


	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}


	public String getTitle() {
		return "CytoCluster";
	}


	public Icon getIcon() {
		return null;
	}




	public CyApplicationManager getCyApplicationManagerServiceRef() {
		return cyApplicationManagerServiceRef;
	}




	public void setCyApplicationManagerServiceRef(
			CyApplicationManager cyApplicationManagerServiceRef) {
		this.cyApplicationManagerServiceRef = cyApplicationManagerServiceRef;
	}
	
	 public ParameterSet getCurrentParamsCopy() {
		    return this.currentParamsCopy;
		  }
}
