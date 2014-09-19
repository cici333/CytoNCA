package org.cytoscape.CytoNCA.internal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import org.cytoscape.model.CyNode;

public class AnalysisCompletedEvent
{
  private final boolean successful;
  private final ArrayList<Protein> proteins;
  private final String results;
  private final boolean outofmemory;
  private final ArrayList<String> Successfelalgnames;

  public AnalysisCompletedEvent(boolean successful, ArrayList<Protein> proteins, String results, boolean outofmemory, ArrayList<String> Successfelalgnames)
  {
    this.successful = successful;
    this.proteins = proteins;
    this.results = results;
    this.outofmemory = outofmemory;
    this.Successfelalgnames = Successfelalgnames;
  }

  public boolean isSuccessful()
  {
    return this.successful;
  }

  public ArrayList<Protein> getProteins()
  {
    return this.proteins;
  }
  public String getResults()
  {
    return this.results;
  }

public boolean isOutofmemory() {
	return outofmemory;
}

public ArrayList<String> getSuccessfelalgnames() {
	return Successfelalgnames;
}
  
}