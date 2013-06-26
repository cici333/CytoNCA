package org.cytoscape.CytoCluster.internal.algorithmPanels;


import javax.swing.*;

import org.cytoscape.CytoCluster.internal.ClusterUtil;
import org.cytoscape.CytoCluster.internal.CollapsiblePanel;
import org.cytoscape.CytoCluster.internal.MyTipTool;
import org.cytoscape.CytoCluster.internal.ParameterSet;
import org.cytoscape.application.swing.CySwingApplication;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;



public class HCPINpanel extends JPanel{
	private final ClusterUtil mcodeUtil;
	private CySwingApplication desktopApp;
	ParameterSet currentParameters;

    private DecimalFormat decimal; // used in the formatted text fields
	private JFormattedTextField fThreshold;
    private JFormattedTextField complexSizeThreshold;
    JRadioButton weak; // use weak module definition
    JRadioButton strong; // use strong module definition
    JPanel weakPanel;
	public HCPINpanel(CySwingApplication swingApplication, ClusterUtil mcodeUtil){

		
		desktopApp=swingApplication;
		  this.mcodeUtil = mcodeUtil;

		

		    this.currentParameters =this.mcodeUtil.getCurrentParameters().getParamsCopy(null);
//        currentParameters = ParameterSet.getInstance().getParamsCopy(null);

		decimal = new DecimalFormat();
        decimal.setParseIntegerOnly(true);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createTitledBorder(""));
        //the collapsible panel
        CollapsiblePanel collapsiblePanel = new CollapsiblePanel("HC-PIN Options");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("")); 
        
        //the radio botton panel
        JPanel functionPanel=new JPanel();
        functionPanel.setLayout(new BorderLayout());
        //the radio bottons
        weak = new JRadioButton("Weak",true);
        weak.setToolTipText("use weak module definition");
        strong = new JRadioButton("Strong", false);
        strong.setToolTipText("use strong module definition");
        weak.addItemListener(new FunctionAction());
        //strong.addItemListener(new FunctionAction());
        ButtonGroup choices = new ButtonGroup();
        choices.add(weak);
        choices.add(strong);
        functionPanel.add(weak,BorderLayout.WEST);
        functionPanel.add(strong,BorderLayout.CENTER);        
        
        //the weak module definition parameter input Panel
        weakPanel = new JPanel();
        weakPanel.setLayout(new BorderLayout());
        //the label
        JLabel label=new JLabel("Threshold");  //Clique Size Threshold input
        fThreshold = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        //the input text field
        fThreshold.setColumns(3);
        fThreshold.addPropertyChangeListener("value", new HCPINpanel.FormattedTextFieldAction());
        String tip2 = "threshold to define a module\n" +
                "It stands for the proportion of\n"+
                "the indegree to the outdegree of a clique";
        fThreshold.setToolTipText(tip2);
        fThreshold.setText((new Double(currentParameters.getfThresholdHCPIN()).toString()));
        weakPanel.add(fThreshold,BorderLayout.EAST); 
        weakPanel.add(label,BorderLayout.WEST);  
        weakPanel.setVisible(true);
        
        //the ComplexSize Panel
        JPanel complexSizePanel=createComplexSizePanel();
 
        panel.add(functionPanel);
        panel.add(weakPanel);  
        panel.add(complexSizePanel);
        collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
        collapsiblePanel.setToolTipText("Customize parameters for FAG-EC (Optional)");
        this.add(collapsiblePanel);
	}

    private JPanel createCliqueSizePanel(){
    	JPanel panel;
        //the label
        JLabel sizeThresholdLabel=new JLabel("Threshold");  
        fThreshold = new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        //the input text field
        fThreshold.setColumns(3);
        fThreshold.addPropertyChangeListener("value", new HCPINpanel.FormattedTextFieldAction());
        String tip = "threshold to define a module\n" +
                "It stands for the proportion of\n"+
                "the indegree to the outdegree of a clique";
        fThreshold.setToolTipText(tip);
        fThreshold.setText((new Double(currentParameters.getfThresholdHCPIN()).toString()));
        //the panel 
        panel = new JPanel() {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        //add the components to the panel
        panel.setLayout(new BorderLayout());
        panel.setToolTipText(tip);
        panel.add(sizeThresholdLabel, BorderLayout.WEST);
        panel.add(fThreshold, BorderLayout.EAST);
    	return panel;
    }

    private JPanel createComplexSizePanel(){
        JPanel panel=new JPanel();
        panel.setLayout(new BorderLayout());        
        //the label
        JLabel sizeThresholdLabel2=new JLabel(" ComplexSize Threshold");  //Clique Size Threshold input
        //the input text field
        complexSizeThreshold= new JFormattedTextField(decimal) {
            public JToolTip createToolTip() {
                return new MyTipTool();
            }
        };
        complexSizeThreshold.setColumns(3);
        complexSizeThreshold.addPropertyChangeListener("value", new HCPINpanel.FormattedTextFieldAction());
        String tip3 = "size cutoff of modules to be outputed\n" +
                "modules smaller than this will be filtered";
        complexSizeThreshold.setToolTipText(tip3);
        complexSizeThreshold.setText((new Integer(currentParameters.getComplexSizeThresholdHCPIN()).toString()));
        panel.add(sizeThresholdLabel2,BorderLayout.WEST);
        panel.add(complexSizeThreshold,BorderLayout.EAST);
        return panel;
    }

 
    private class FunctionAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
        	currentParameters=mcodeUtil.getMainPanel().getCurrentParamsCopy();
            if (e.getStateChange() == ItemEvent.SELECTED) {
                currentParameters.setWeakHCPIN(true);
                weakPanel.setVisible(true);
     
            } else {
                currentParameters.setWeakHCPIN(false);
                weakPanel.setVisible(false);
               
            }
        }
    }
    
    private class FormattedTextFieldAction implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField source = (JFormattedTextField) e.getSource();
            String message = "Invaluled input\n";
            boolean invalid = false;
            
            
            currentParameters=mcodeUtil.getMainPanel().getCurrentParamsCopy();// 
            
            
            if (source == fThreshold) {
                String value = fThreshold.getText();
                if ((value != null) && (Double.parseDouble(value) >= 1.0) && (Double.parseDouble(value) <= 10.0)) {
                    currentParameters.setfThresholdHCPIN(Double.parseDouble(value));
                } else {
                    //source.setValue(new Double (currentParameters.getfThresholdHCPIN()));
                    message += "module threshold should\n" +
    						"be set between 1 and 10.";
                    invalid = true;
                }
                source.setValue(new Double (currentParameters.getfThresholdHCPIN()));
            }else if (source == complexSizeThreshold) {
                Number value = (Number) complexSizeThreshold.getValue();
                if ((value != null) && (value.intValue() >= 0)) {
                    currentParameters.setComplexSizeThresholdHCPIN(value.intValue());
                } else {
                    //source.setValue(new Double (currentParameters.getComplexSizeThresholdHCPIN()));
                    message += "size of output module cutoff should\n" +
    						"be greater than 0.";
                    invalid = true;
                }
                source.setValue(new Double (currentParameters.getComplexSizeThresholdHCPIN()));
			}
            
            if (invalid) {
                JOptionPane.showMessageDialog(desktopApp.getJFrame(), message, "paramter out of boundary", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    

}
