package BiNGO.BiNGO ;

// redistributed Cytoscape code

/* * Redistributed Date: Mar.25.2005
 * * by : Steven Maere
 * * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * */

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
/*import giny.model.GraphPerspective;*/
import org.cytoscape.model.CyNode;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.cytoscape.model.*;
import java.util.*;

/**
 * Calculates the all-pairs-shortest-paths (APSP) of a set of <code>giny.model.Node</code>
 * objects that reside in a <code>giny.model.GraphPerspective</code>.
 * Note: this was copied from giny.util because it is being phased out.  Eventually
 * the layout API will be available to use (TODO: remove when layout API is available)
 *
 * @see giny.util.IntNodeDistances
 */
public class NodeDistances implements MonitorableTask{

    public static final int INFINITY = Integer.MAX_VALUE;

    protected List nodesList;
    /*protected GraphPerspective perspective;*/
    protected CyNetwork network;
    protected int[][] distances;
    protected boolean directed;

    // Keep track of progress for monitoring:
    private int maxValue;
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;
    
	protected int currentProgress;
	protected int lengthOfTask;
	protected String statusMessage;
	protected boolean done;
	protected boolean canceled;

    protected Map nodeIndexToMatrixIndexMap; //a root node index to matrix index map

    
    class NodeDistancesTask
	{

		final NodeDistances this$0;

		NodeDistancesTask()
		{
			this$0 = NodeDistances.this;
			//super();
			calculate();
		}
	}

    /**
     * Constructor, defaults to undirected graph.
     *
     * @param nodes_list  a <code>List</code> of <code>giny.model.Node</code> objects for
     *                    which distances will be calculated
     * @param distances   the 2 dimensional array that will hold the calculated distances
     *                    (possibly null)
     * @param perspective the <code>giny.model.GraphPerspective</code> in which the nodes
     *                    reside
     * @deprecated Can't use this now that GraphPerspective uses root graph indices.
     */
    
    public NodeDistances(List nodesList, CyNetwork network, Map nodeIndexToMatrixIndexMap)
	{
		this.nodesList = nodesList;
		this.nodeIndexToMatrixIndexMap =  nodeIndexToMatrixIndexMap;
		this.network = network;
		distances = new int[nodesList.size()][];
		directed = false;
	}
    
/*    public NodeDistances(List nodes_list, int[][] distances, GraphPerspective perspective) {
        this(nodes_list, distances, perspective, false);

    }//NodeDistances
*/
    /**
     * Constructor, specifies whether a the graph should be treated as a directed graph or not.
     *
     * @param nodes_list  a <code>List</code> of <code>giny.model.Node</code> objects for
     *                    which distances will be calculated
     * @param distances   the 2 dimensional array that will hold the calculated distances
     *                    (possibly null)
     * @param perspective the <code>giny.model.GraphPerspective</code> in which the nodes
     *                    reside
     * @param directed    if <code>true</code>, then APSP is calculated assuming a directed graph,
     *                    if <code>false</code> an undirected graph is assumed
     * @deprecated Can't use this now that GraphPerspective uses root graph indices.
     */
/*    public NodeDistances(List nodes_list,
                         int[][] distances,
                         GraphPerspective perspective,
                         boolean directed) {
        this.perspective = perspective;
        nodesList = nodes_list;
        if (distances == null) {
            this.distances = new int[nodesList.size()][];
        } else {
            this.distances = distances;
        }
        this.directed = directed;
    }//NodeDistances
*/
    /**
     * The main constructor
     *
     * @param nodesList                 List of nodes ordered by the index map
     * @param perspective               The <code>giny.model.GraphPerspective</code> in which the nodes reside
     * @param nodeIndexToMatrixIndexMap An index map that maps your root graph indices to the returned matrix indices
     */
/*    public NodeDistances(List nodesList, GraphPerspective perspective, HashMap nodeIndexToMatrixIndexMap) {
        this.nodesList = nodesList;
        this.nodeIndexToMatrixIndexMap = nodeIndexToMatrixIndexMap;
        this.perspective = perspective;
        this.distances = new int[nodesList.size()][];
        this.directed = false;
    }
*/
    /**
     * Calculates the node distances.
     *
     * @return the <code>int[][]</code> array of calculated distances or null if the
     *         task was canceled or there was an error
     */
    public int[][] calculate() {
        int currentProgress = 0;
        this.maxValue = distances.length;

        CyNode[] nodes = new CyNode[nodesList.size()];

        // TODO: REMOVE
        // System.err.println( "Calculating all node distances.. for: "
        //+nodesList.size()+" and "+nodes.length );

        // We don't have to make new Integers all the time, so we store the index
        // Objects in this array for reuse.
        Integer[] integers = new Integer[nodes.length];

        // Fill the nodes array with the nodes in their proper index locations.
        //int index;
        CyNode from_node;

        for (int i = 0; i < nodes.length; i++) {

            from_node = (CyNode) nodesList.get(i);
            if (from_node == null) {
                continue;
            }
            int index = ((Integer)nodeIndexToMatrixIndexMap.get(from_node.getSUID())).intValue();
            //index = ((Integer) nodeIndexToMatrixIndexMap.get(new Integer(from_node.getRootGraphIndex()))).intValue();

            if ((index < 0) || (index >= nodes.length)) {
                System.err.println("WARNING: GraphNode \"" + from_node +
                        "\" has an index value that is out of range: " +
                        index +
                        ".  Graph indices should be maintained such " +
                        "that no index is unused.");
                return null;
            }
            if (nodes[index] != null) {
                System.err.println("WARNING: GraphNode \"" + from_node +
                        "\" has an index value ( " + index + " ) that is the same as " +
                        "that of another GraphNode ( \"" + nodes[index] +
                        "\" ).  Graph indices should be maintained such " +
                        "that indices are unique.");
                return null;
            }
            nodes[index] = from_node;
            Integer in = new Integer(index);
            integers[index] = in;
        }

        LinkedList queue = new LinkedList();
        boolean[] completed_nodes = new boolean[nodes.length];
        Iterator neighbors;
        CyNode to_node;
        CyNode neighbor;
        int neighbor_index;
        int to_node_distance;
        int neighbor_distance;
        for (int from_node_index = 0;
             from_node_index < nodes.length;
             from_node_index++) {

            if (this.interrupted) {
                // The task was canceled
                this.distances = null;
                return this.distances;
            }

            from_node = nodes[from_node_index];

            if (from_node == null) {
                // Make the distances in this row all Integer.MAX_VALUE.
                if (distances[from_node_index] == null) {
                    distances[from_node_index] = new int[nodes.length];
                }
                Arrays.fill(distances[from_node_index], Integer.MAX_VALUE);
                continue;
            }

            // TODO: REMOVE
            //  System.err.print( "Calculating node distances from graph node " +
            //                  from_node );
            //System.err.flush();

            // Make the distances row and initialize it.
            if (distances[from_node_index] == null) {
                distances[from_node_index] = new int[nodes.length];
            }
            Arrays.fill(distances[from_node_index], Integer.MAX_VALUE);
            distances[from_node_index][from_node_index] = 0;

            // Reset the completed nodes array.
            Arrays.fill(completed_nodes, false);

            // Add the start node to the queue.
            queue.add(integers[from_node_index]);

            while (!(queue.isEmpty())) {

                if (this.interrupted) {
                    // The task was canceled
                    this.distances = null;
                    return this.distances;
                }

                int index = ((Integer) queue.removeFirst()).intValue();
                if (completed_nodes[index]) {
                    continue;
                }
                completed_nodes[index] = true;

                to_node = nodes[index];
                to_node_distance = distances[from_node_index][index];

                if (index < from_node_index) {
                    // Oh boy.  We've already got every distance from/to this node.
                    int distance_through_to_node;
                    for (int i = 0; i < nodes.length; i++) {
                        if (distances[index][i] == Integer.MAX_VALUE) {
                            continue;
                        }
                        distance_through_to_node =
                                to_node_distance + distances[index][i];
                        if (distance_through_to_node <=
                                distances[from_node_index][i]) {
                            // Any immediate neighbor of a node that's already been
                            // calculated for that does not already have a shorter path
                            // calculated from from_node never will, and is thus complete.
                            if (distances[index][i] == 1) {
                                completed_nodes[i] = true;
                            }
                            distances[from_node_index][i] =
                                    distance_through_to_node;
                        }
                    } // End for every node, update the distance using the distance from
                    // to_node.
                    // So now we don't need to put any neighbors on the queue or
                    // anything, since they've already been taken care of by the previous
                    // calculation.
                    continue;
                } // End if to_node has already had all of its distances calculated.

                neighbors = getNeighbors(to_node).iterator();
                
                //neighbors = perspective.neighborsList(to_node).iterator();

                while (neighbors.hasNext()) {

                    if (this.interrupted) {
                        this.distances = null;
                        return this.distances;
                    }

                    neighbor = (CyNode) neighbors.next();

                    
                    neighbor_index = ((Integer)nodeIndexToMatrixIndexMap.get(neighbor.getSUID())).intValue();
                    
                    //neighbor_index = ((Integer) nodeIndexToMatrixIndexMap.get(new Integer(neighbor.getRootGraphIndex()))).intValue();

                    // If this neighbor was not in the incoming List, we cannot include
                    // it in any paths.
                    if (nodes[neighbor_index] == null) {
                        distances[from_node_index][neighbor_index] =
                                Integer.MAX_VALUE;
                        continue;
                    }

                    if (completed_nodes[neighbor_index]) {
                        // We've already done everything we can here.
                        continue;
                    }

                    neighbor_distance = distances[from_node_index][neighbor_index];

                    if ((to_node_distance != Integer.MAX_VALUE) &&
                            (neighbor_distance > (to_node_distance + 1))) {
                        distances[from_node_index][neighbor_index] =
                                (to_node_distance + 1);
                        queue.addLast(integers[neighbor_index]);
                    }

                    // TODO: REMOVE
                    //System.out.print( "." );
                    //System.out.flush();


                } // For each of the next nodes' neighbors
                // TODO: REMOVE
                //System.out.print( "|" );
                //System.out.flush();
            } // For each to_node, in order of their (present) distances

            // TODO: REMOVE
            /*
            System.err.println( "done." );
            */
            // Calculate Percentage.  This must be a value between 0..100.
            int percentComplete = (int) (((double) currentProgress / maxValue) * 100);

            //  Estimate Time Remaining
            long timeRemaining = maxValue - currentProgress;

            //  Update the Task Monitor.
            //  This automatically updates the UI Component w/ progress bar.
            if (taskMonitor != null) {
                taskMonitor.setProgress(percentComplete);
                taskMonitor.setStatusMessage("Calculating Node Distances: " + currentProgress + "of " + maxValue);
               // taskMonitor.setEstimatedTimeRemaining(timeRemaining);
            }

            currentProgress++;

        } // For each from_node

        // TODO: REMOVE
        //System.err.println( "..Done calculating all node distances." );

        return distances;
    }//calculate

    /**
     * @return the <code>int[][]</code> 2D array of calculated distances or null
     *         if not yet calculated
     */
    public int[][] getDistances() {
        return this.distances;
    }//getDistances

    /**
     * Run the Task.
     */
    public void run() {
        calculate();
    }

    /**
     * Non-blocking call to interrupt the task.
     */
    public void halt() {
        this.interrupted = true;
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("Calculating Node Distances");
    }

	public boolean wasCanceled()
	{
		return canceled;
	}

	public int getCurrentProgress()
	{
		return currentProgress;
	}

	public int getLengthOfTask()
	{
		return lengthOfTask;
	}

	public String getTaskDescription()
	{
		return "Calculating Node Distances";
	}

	public String getCurrentStatusMessage()
	{
		return statusMessage;
	}

	public boolean isDone()
	{
		return done;
	}

	public void stop()
	{
		canceled = true;
		statusMessage = null;
	}

	
	public void start(boolean return_when_done) {
		// TODO Auto-generated method stub
		SwingWorker worker = new SwingWorker() {

			final NodeDistances this$0;

			protected Object doInBackground()
				throws Exception
			{
				return new NodeDistancesTask();
			}

			
			{
				this$0 = NodeDistances.this;
				//super();
			}
		};
		worker.execute();
		if (return_when_done)
			try
			{
				worker.get();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
	}


	
	
	private Collection getNeighbors(CyNode node)
	{
		Set result = new HashSet();
		Collection edges = network.getAdjacentEdgeList(node, org.cytoscape.model.CyEdge.Type.ANY);
		if (edges == null || edges.size() == 0)
			return result;
		Long targetID = node.getSUID();
		for (Iterator iterator = edges.iterator(); iterator.hasNext();)
		{
			CyEdge curEdge = (CyEdge)iterator.next();
			if (curEdge.getSource().getSUID() != targetID)
				result.add(curEdge.getSource());
			else
			if (curEdge.getTarget().getSUID() != targetID)
				result.add(curEdge.getTarget());
		}

		return result;
	}

} // class NodeDistances
