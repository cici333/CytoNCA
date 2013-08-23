	package org.cytoscape.CytoNCA.internal;

	import javax.swing.JToolTip;

	public class JMultiLineToolTip extends JToolTip
	{
	  private static final long serialVersionUID = 7813662474312183098L;
	  protected int columns = 0;
	  protected int fixedwidth = 0;

	  public JMultiLineToolTip()
	  {
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
	    return this.columns;
	  }

	  public void setFixedWidth(int width) {
	    this.fixedwidth = width;
	    this.columns = 0;
	  }

	  public int getFixedWidth() {
	    return this.fixedwidth;
	  }
	}