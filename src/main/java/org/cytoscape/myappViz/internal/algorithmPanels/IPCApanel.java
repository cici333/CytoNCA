package org.cytoscape.myappViz.internal.algorithmPanels;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.myappViz.internal.ClusterUtil;
import org.cytoscape.myappViz.internal.CollapsiblePanel;
import org.cytoscape.myappViz.internal.MyTipTool;
import org.cytoscape.myappViz.internal.ParameterSet;

public class IPCApanel extends JPanel{
	private final ClusterUtil mcodeUtil;
	private CySwingApplication desktopApp;
	ParameterSet currentParameters;

    private DecimalFormat decimal; // used in the formatted text fields
	private JFormattedTextField tinThreshold;
    private JFormattedTextField complexSize;
    private JFormattedTextField ShortestPathLength;
    
	public IPCApanel(CySwingApplication swingApplication, ClusterUtil mcodeUtil){

		
		desktopApp=swingApplication;
		  this.mcodeUtil = mcodeUtil;

		

		    this.currentParameters =this.mcodeUtil.getCurrentParameters().getParamsCopy(null);
//        currentParameters = ParameterSet.getInstance().getParamsCopy(null);

		decimal = new DecimalFormat();
        decimal.setParseIntegerOnly(true);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(""));
        //the collapsible panel
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("IPCA Options ");
        JPanel TinThresholdPanel = createTinThresholdPanel();
        JPanel complexSizePanel = createComplexSizePanel();
        JPanel ShortestPathLengthPanel = createShortestPathLengthPanel();
        collapsiblePanel.getContentPane().add(TinThresholdPanel, BorderLayout.NORTH);
        collapsiblePanel.getContentPane().add(complexSizePanel, BorderLayout.CENTER);
        collapsiblePanel.getContentPane().add(ShortestPathLengthPanel, BorderLayout.SOUTH);
        collapsiblePanel.setToolTipText("Customize parameters for IPCA (Optional)");
        this.add(collapsiblePanel);
	}

    private JPanel createTinThresholdPanel(){
    	JPanel panel=new JPanel();
    	panel.setLayout(new BorderLayout()); 
        //the label
        JLabel TinThresholdLabel = new JLabel(" TinThreshold ");  
        tinThreshold = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        //the input text field
        tinThreshold.setColumns(3);
        tinThreshold.addPropertyChangeListener("value", new IPCApanel.FormattedTextFieldAction());
        String tip = "threshold to define how strongly a vertex v \n" +
        				"is connected to a subgraph K."
                         ;
        tinThreshold.setToolTipText(tip);
        tinThreshold.setText((new Double(currentParameters.getTinThreshold()).toString()));
      
        panel.add(TinThresholdLabel, BorderLayout.WEST);
        panel.add(tinThreshold, BorderLayout.EAST);
    	return panel;
    }

    private JPanel createComplexSizePanel(){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());        
        //the label
        JLabel ComplexSizeLabel=new JLabel(" ComplexSize Threshold");  //Clique Size Threshold input
        //the input text field
        complexSize= new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        complexSize.setColumns(3);
        complexSize.addPropertyChangeListener("value", new IPCApanel.FormattedTextFieldAction());
        String tip3 = "size cutoff of modules to be outputed\n" +
                "modules smaller than this will be filtered";
        complexSize.setToolTipText(tip3);
        complexSize.setText((new Integer(currentParameters.getComplexSizeThresholdIPCA()).toString()));
        panel.add(ComplexSizeLabel,BorderLayout.WEST);
        panel.add(complexSize,BorderLayout.EAST);
        return panel;
    }

    private JPanel createShortestPathLengthPanel(){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());        
        //the label
        JLabel ShortestPathLengthLabel=new JLabel(" Shortest Path Length");  //Clique Size Threshold input
        //the input text field
        ShortestPathLength = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        ShortestPathLength.setColumns(3);
        ShortestPathLength.addPropertyChangeListener("value", new IPCApanel.FormattedTextFieldAction());
        String tip3 = "largest length of a shortest path \n" +
                "between a pair of vertices in cluster";
        ShortestPathLength.setToolTipText(tip3);
        ShortestPathLength.setText((new Integer(currentParameters.getShortestPathLength()).toString()));
        panel.add(ShortestPathLengthLabel,BorderLayout.WEST);
        panel.add(ShortestPathLength,BorderLayout.EAST);
        return panel;
    }
    
    
    private class FormattedTextFieldAction implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField source = (JFormattedTextField) e.getSource();
            String message = "Invaluled input\n";
            boolean invalid = false;
            
            
            currentParameters=mcodeUtil.getMainPanel().getCurrentParamsCopy();// 
            
            
            if (source == tinThreshold) {
                Number value = (Number) tinThreshold.getValue();
                if ((value != null) && (value.doubleValue() > 0.0) && (value.doubleValue() <= 1.0)) {
                    currentParameters.setTinThreshold(value.doubleValue());
                } else {
                    source.setValue(new Double (currentParameters.getTinThreshold()));
                    message += "Tin threshold should\n" +
    						"be set between 0 and 1.";
                    invalid = true;
                }
            }else if (source == complexSize) {
                Number value = (Number) complexSize.getValue();
                if ((value != null) && (value.intValue() >= 0)) {
                    currentParameters.setComplexSizeThresholdIPCA(value.intValue());
                } else {
                    source.setValue(new Double (currentParameters.getComplexSizeThresholdIPCA()));
                    message += "size of output module cutoff should\n" +
    						"be greater than 0.";
                    invalid = true;
                }
			}else if (source == ShortestPathLength) {
                Number value = (Number) ShortestPathLength.getValue();
                if ((value != null) && (value.intValue() >= 0)) {
                    currentParameters.setShortestPathLength(value.intValue());
                } else {
                    source.setValue(new Double (currentParameters.getShortestPathLength()));
                    message += "the Shortest Path Length of clusters should\n" +
    						"be greater than 0.";
                    invalid = true;
                }
			}
            
            if (invalid) {
                JOptionPane.showMessageDialog(desktopApp.getJFrame(), message, "paramter out of boundary", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}

