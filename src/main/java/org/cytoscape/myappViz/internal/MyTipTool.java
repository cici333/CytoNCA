package org.cytoscape.myappViz.internal;

//Copied from http://www.codeguru.com/java/articles/122.shtml

// JMultiLineToolTip.java

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import java.awt.*;


/**
 * A multiple line tiptool for the main panel
 * Copied from http://www.codeguru.com/java/articles/122.shtml
 */
public class MyTipTool extends JToolTip {
    String tipText;
    JComponent component;
    protected int columns = 0;
    protected int fixedwidth = 0;

    public MyTipTool() {
        updateUI();
    }
    public void updateUI() {
        setUI(MultiLineToolTipUI.createUI(this));
    }
    public void setColumns(int columns) {
        this.columns = columns;
        this.fixedwidth = 0;
    }
    public int getColumns() {
        return columns;
    }
    public void setFixedWidth(int width) {
        this.fixedwidth = width;
        this.columns = 0;
    }
    public int getFixedWidth() {
        return fixedwidth;
    }
    public static void main(String args[]){
    	System.out.println("Complete!");
    }
}

/**
 * The UI for this multiple line tool tip
 */
class MultiLineToolTipUI extends BasicToolTipUI {
    static MultiLineToolTipUI instance = new MultiLineToolTipUI();
    Font smallFont;
    static JToolTip tip;
    protected CellRendererPane rendererPane;

    private static JTextArea text;

    public static ComponentUI createUI(JComponent c) {
        return instance;
    }

    public MultiLineToolTipUI() {
        super();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        tip = (JToolTip) c;
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        c.remove(rendererPane);
        rendererPane = null;
    }

    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        text.setBackground(c.getBackground());
        rendererPane.paintComponent(g, text, c, 1, 1,
                size.width - 1, size.height - 1, true);
    }

    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        if (tipText == null)
            return new Dimension(0, 0);
        text = new JTextArea(tipText);
        rendererPane.removeAll();
        rendererPane.add(text);
        text.setWrapStyleWord(true);
        int width = ((MyTipTool) c).getFixedWidth();
        int columns = ((MyTipTool) c).getColumns();

        if (columns > 0) {
            text.setColumns(columns);
            text.setSize(0, 0);
            text.setLineWrap(true);
            text.setSize(text.getPreferredSize());
        } 
        else if (width > 0) {
            text.setLineWrap(true);
            Dimension d = text.getPreferredSize();
            d.width = width;
            d.height++;
            text.setSize(d);
        } 
        else
            text.setLineWrap(false);
        Dimension dim = text.getPreferredSize();
        dim.height += 1;
        dim.width += 1;
        return dim;
    }

    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}
