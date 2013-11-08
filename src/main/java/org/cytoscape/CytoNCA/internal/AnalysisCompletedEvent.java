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

  public AnalysisCompletedEvent(boolean successful, ArrayList<Protein> proteins, String results)
  {
    this.successful = successful;
    this.proteins = proteins;
    this.results = results;
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
}