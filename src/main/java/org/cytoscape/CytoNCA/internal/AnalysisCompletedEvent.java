package org.cytoscape.CytoNCA.internal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.cytoscape.model.CyNode;

public class AnalysisCompletedEvent
{
  private final boolean successful;
  private final ArrayList<Protein> proteins;

  public AnalysisCompletedEvent(boolean successful, ArrayList<Protein> proteins)
  {
    this.successful = successful;
    this.proteins = proteins;
  }

  public boolean isSuccessful()
  {
    return this.successful;
  }

  public ArrayList<Protein> getProteins()
  {
    return this.proteins;
  }
}