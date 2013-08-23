package org.cytoscape.CytoNCA.internal;

import java.util.*;

import org.cytoscape.CytoNCA.internal.ProteinUtil;
import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;

public class ProteinGraph
{

	private final CyRootNetwork rootNetwork;
	  private final Set<CyNode> nodes;
	  private final Set<CyEdge> edges;
	  private final Map<Long, CyNode> nodeMap;
	  private final Map<Long, CyEdge> edgeMap;
	  private CySubNetwork subNetwork;
	private ProteinUtil clusterUtil;
	private boolean disposed;

	public ProteinGraph(CyRootNetwork rootNetwork, Collection nodes, Collection edges, ProteinUtil mcodeUtil)
	{
		if (rootNetwork == null)
			throw new NullPointerException("rootNetwork is null!");
		if (nodes == null)
			throw new NullPointerException("nodes is null!");
		if (edges == null)
			throw new NullPointerException("edges is null!");
		this.clusterUtil = mcodeUtil;
		this.rootNetwork = rootNetwork;
		this.nodes = Collections.synchronizedSet(new HashSet(nodes.size()));
		this.edges = Collections.synchronizedSet(new HashSet(edges.size()));
		nodeMap = Collections.synchronizedMap(new HashMap(nodes.size()));
		edgeMap = Collections.synchronizedMap(new HashMap(edges.size()));
		CyNode n;
		for (Iterator iterator = nodes.iterator(); iterator.hasNext(); addNode(n))
			n = (CyNode)iterator.next();

		CyEdge e;
		for (Iterator iterator1 = edges.iterator(); iterator1.hasNext(); addEdge(e))
			e = (CyEdge)iterator1.next();

	}

	public boolean addNode(CyNode node)
	{
		if (nodes.contains(node))
			return false;
		node = rootNetwork.getNode(node.getSUID().longValue());
		if (nodes.add(node))
		{
			nodeMap.put(node.getSUID(), node);
			return true;
		} else
		{
			return false;
		}
	}

	public boolean addEdge(CyEdge edge)
	{
		if (edges.contains(edge))
			return false;
		if (nodes.contains(edge.getSource()) && nodes.contains(edge.getTarget()))
		{
			edge = rootNetwork.getEdge(edge.getSUID().longValue());
			if (edges.add(edge))
			{
				edgeMap.put(edge.getSUID(), edge);
				return true;
			}
		}
		return false;
	}

	public int getNodeCount()
	{
		return nodes.size();
	}

	public int getEdgeCount()
	{
		return edges.size();
	}

	 public List<CyNode> getNodeList() {
		    return new ArrayList(this.nodes);
		  }

		  public List<CyEdge> getEdgeList() {
		    return new ArrayList(this.edges);
		  }

	public boolean containsNode(CyNode node)
	{
		return nodes.contains(node);
	}

	public boolean containsEdge(CyEdge edge)
	{
		return edges.contains(edge);
	}

	public CyNode getNode(long index)
	{
		return (CyNode)nodeMap.get(Long.valueOf(index));
	}

	public CyEdge getEdge(long index)
	{
		return (CyEdge)edgeMap.get(Long.valueOf(index));
	}

	public List getAdjacentEdgeList(CyNode node, org.cytoscape.model.CyEdge.Type edgeType)
	{
		List rootList = rootNetwork.getAdjacentEdgeList(node, edgeType);
		List list = new ArrayList(rootList.size());
		for (Iterator iterator = rootList.iterator(); iterator.hasNext();)
		{
			CyEdge e = (CyEdge)iterator.next();
			if (containsEdge(e))
				list.add(e);
		}

		return list;
	}

	public List getConnectingEdgeList(CyNode source, CyNode target, org.cytoscape.model.CyEdge.Type edgeType)
	{
		List rootList = rootNetwork.getConnectingEdgeList(source, target, edgeType);
		List list = new ArrayList(rootList.size());
		for (Iterator iterator = rootList.iterator(); iterator.hasNext();)
		{
			CyEdge e = (CyEdge)iterator.next();
			if (containsEdge(e))
				list.add(e);
		}

		return list;
	}

	public CyRootNetwork getRootNetwork()
	{
		return rootNetwork;
	}

	public synchronized CySubNetwork getSubNetwork()
	{
		if (!disposed && subNetwork == null)
			subNetwork = clusterUtil.createSubNetwork(rootNetwork, nodes, SavePolicy.DO_NOT_SAVE);
		return subNetwork;
	}

	public synchronized boolean isDisposed()
	{
		return disposed;
	}

	public synchronized void dispose()
	{
		if (disposed)
			return;
		if (subNetwork != null)
		{
			clusterUtil.destroy(subNetwork);
			subNetwork = null;
		}
		nodes.clear();
		edges.clear();
		nodeMap.clear();
		edgeMap.clear();
		disposed = true;
	}
}