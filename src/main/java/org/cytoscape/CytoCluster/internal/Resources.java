package org.cytoscape.CytoCluster.internal;

import java.net.URL;


public class Resources
{
	  public static URL getUrl(ImageName img)
	  {
	    return Resources.class.getResource(img.toString());
	  }

	  public static enum ImageName
	  {
	    ARROW_EXPANDED("/arrow_expanded.gif"), 
	    ARROW_COLLAPSED("/arrow_collapsed.gif"), 
	    LOGO_SMALL("/arrow_collapsed.gif");

	    private final String name;

	    private ImageName(String name) {
	      this.name = name;
	    }

	    public String toString()
	    {
	      return this.name;
	    }
	  }
}

