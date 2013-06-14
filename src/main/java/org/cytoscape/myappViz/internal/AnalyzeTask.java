package org.cytoscape.myappViz.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


import org.cytoscape.myappViz.internal.AnalysisCompletedEvent;
import org.cytoscape.myappViz.internal.AnalysisCompletedListener;
import org.cytoscape.myappViz.internal.Cluster;
import org.cytoscape.myappViz.internal.ClusterUtil;
import org.cytoscape.myappViz.internal.algorithm.Algorithm;
import org.cytoscape.myappViz.internal.algorithm.EAGLE;
import org.cytoscape.myappViz.internal.algorithm.FAGEC;
import org.cytoscape.myappViz.internal.algorithm.HCPIN;
import org.cytoscape.myappViz.internal.algorithm.IPCA;
import org.cytoscape.myappViz.internal.algorithm.OHPIN;
import org.cytoscape.myappViz.internal.algorithm.MCODE;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class AnalyzeTask implements Task {
	private final Algorithm alg;
	private final ClusterUtil clusterUtil;
	private final int analyze;
	private final int resultId;
	private final AnalysisCompletedListener listener;
	private boolean interrupted;
	private CyNetwork network;
	private static final Logger logger = LoggerFactory
			.getLogger(AnalyzeTask.class);

	final static int FIRST_TIME = 0;
	final static int RESCORE = 1;
	final static int REFIND = 2;
	final static int FIND = 3;
	final static int INTERRUPTED = 4;
	final static int FINDCLIQUE = 5;
	final static int CLIQUEBASED = 6;
	final static int EXISTS = 7;

	public AnalyzeTask(CyNetwork network, int analyze, int resultId,
			Algorithm alg, ClusterUtil clusterUtil,
			AnalysisCompletedListener listener) {
		this.network = network;
		this.analyze = analyze;
		this.resultId = resultId;
		this.alg = alg;
		this.clusterUtil = clusterUtil;
		this.listener = listener;
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}

		boolean success = false;
		Cluster[] clusters = null;
		List<Cluster> clusterL = new ArrayList<Cluster>();

		this.clusterUtil.resetLoading();
		try {
			if (alg instanceof MCODE)

			{

				this.alg.setTaskMonitor(taskMonitor, this.network.getSUID());

				if (this.analyze == 1) {
					taskMonitor.setProgress(0.001D);
					taskMonitor.setTitle("MCODE Analysis");
					taskMonitor
							.setStatusMessage("Scoring Network (Step 1 of 3)");
					this.alg.scoreGraph(this.network, this.resultId);

					if (this.interrupted)
						return;
				}
				do {

					logger.info("Network was scored in "
							+ this.alg.getLastScoreTime() + " ms.");

					taskMonitor.setProgress(0.001D);
					taskMonitor
							.setStatusMessage("Finding Clusters (Step 2 of 3)");

					clusters = this.alg.K_CoreFinder(this.network,
							this.resultId);
					for (int i = 0; i < clusters.length; i++)
						clusterL.add(clusters[i]);
				} while (this.interrupted);

				taskMonitor.setProgress(0.001D);
				taskMonitor.setStatusMessage("Drawing Results (Step 3 of 3)");

				this.clusterUtil.sortClusters(clusters);
				int imageSize = this.clusterUtil.getCurrentParameters()
						.getResultParams(this.resultId).getDefaultRowHeight();
				int count = 0;

				for (Cluster c : clusters) {
					if (this.interrupted)
						return;

					Image img = this.clusterUtil.convertClusterToImage(null, c,
							imageSize, imageSize, null, true);
					c.setImage(img);
					taskMonitor.setProgress(++count / clusters.length);
				}

				success = true;

			} else if (alg instanceof EAGLE)//
			{
				EAGLE objEagle = (EAGLE) alg;
				objEagle.setTaskMonitor(taskMonitor, network.getSUID());
				taskMonitor.setProgress(0);
				taskMonitor
						.setStatusMessage("Step 1 of 3:Calculate all the maximal Clique...");
				clusters = objEagle.run(network, this.resultId);
				objEagle.getMaximalCliques(network, this.resultId);
				System.err.println("Finding clique: Time spent "
						+ objEagle.getFindCliquesTIme() + " ms.");
				if (interrupted)
					return;
				taskMonitor.setProgress(0);
				taskMonitor
						.setStatusMessage("Step 2 of 3:Generating Complexes...");
				clusters = objEagle.EAGLEFinder(network, this.resultId);
				if (interrupted)
					return;
				taskMonitor.setProgress(0);
				taskMonitor
						.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
				// create all the images here for the clusters, it can be a time
				// consuming operation
				clusters = ClusterUtil.sortClusters2(clusters);
				
				
				for (int i = 0; i < clusters.length; i++)
					clusterL.add(clusters[i]);

				int imageSize = this.clusterUtil.getCurrentParameters()
						.getResultParams(this.resultId).getDefaultRowHeight();
				int count = 0;

				for (Cluster c : clusters) {
					if (this.interrupted)
						return;

					Image img = this.clusterUtil.convertClusterToImage(null, c,
							imageSize, imageSize, null, true);
					c.setImage(img);
					taskMonitor.setProgress(++count / clusters.length);
				}

				success = true;

				// imageList = new Image[clusters.length];
				// for (int i = 0; i < clusters.length; i++) {
				// if (interrupted) {
				// return;
				// }
				// imageList[i] = clusterUtil.convertClusterToImage(null,
				// clusters[i], imageSize, imageSize, null, true);
				// taskMonitor.setProgress((i * 100) / clusters.length);
				// }
				// success = true;

			} else if (alg instanceof FAGEC)//
			{

				FAGEC algFagec = (FAGEC) alg;

				algFagec.setTaskMonitor(taskMonitor, network.getSUID());
				System.out.println(analyze);
				
				if (analyze == FINDCLIQUE) {
					
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("Step 1 of 3:Calculate all the maximal Clique...");
					algFagec.getMaximalCliques(network, this.resultId);
					if (interrupted)
						return;
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("Step 2 of 3:Generating Complexes...");
					clusters = algFagec.FAG_ECFinder(network, this.resultId);
					if (interrupted)
						return;
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
					// create all the images here for the clusters, it can be a
					// time consuming operation
					if (this.clusterUtil.getCurrentParameters()
							.getResultParams(this.resultId).isWeakFAGEC())
						clusters = ClusterUtil.sortClusters3(clusters);
					else
						clusters = ClusterUtil.sortClusters2(clusters);

					int imageSize = this.clusterUtil.getCurrentParameters()
							.getResultParams(this.resultId)
							.getDefaultRowHeight();
					
					
					for (int i = 0; i < clusters.length; i++)
						clusterL.add(clusters[i]);
					
					
					int count = 0;

					for (Cluster c : clusters) {
						if (this.interrupted)
							return;

						Image img = this.clusterUtil.convertClusterToImage(null,
								c, imageSize, imageSize, null, true);
						c.setImage(img);
						taskMonitor.setProgress(++count / clusters.length);
					}

					success = true;

					// imageList = new Image[clusters.length];
					// for (int i = 0; i < clusters.length; i++) {
					// if (interrupted) {
					// return;
					// }
					// imageList[i] = ClusterUtil.convertClusterToImage(null,
					// clusters[i], imageSize, imageSize, null, true);
					// taskMonitor.setProgress((i * 100) / clusters.length);
					// }
					// completedSuccessfully = true;
				} else {
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("Step 2 of 3:Generating Complexes...");
					clusters = algFagec.run(network, this.resultId);
					System.err.println("After FAG-EC.Time used:"
							+ algFagec.getLastFindTime());
					if (interrupted)
						return;
					taskMonitor.setProgress(0);
					taskMonitor
							.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
					// create all the images here for the clusters, it can be a
					// time consuming operation
					if (this.clusterUtil.getCurrentParameters()
							.getResultParams(this.resultId).isWeakFAGEC())
						clusters = ClusterUtil.sortClusters3(clusters);
					else
						clusters = ClusterUtil.sortClusters2(clusters);

					int imageSize = this.clusterUtil.getCurrentParameters()
							.getResultParams(this.resultId)
							.getDefaultRowHeight();
					
					
					for (int i = 0; i < clusters.length; i++)
						clusterL.add(clusters[i]);
					
					
					
					int count = 0;

					for (Cluster c : clusters) {
						if (this.interrupted)
							return;

						Image img = this.clusterUtil.convertClusterToImage(null,
								c, imageSize, imageSize, null, true);
						c.setImage(img);
						taskMonitor.setProgress(++count / clusters.length);
					}

					success = true;

					/*
					 * imageList = new Image[clusters.length]; for (int i = 0; i
					 * < clusters.length; i++) { if (interrupted) { return; }
					 * imageList[i] = clusterUtil.convertClusterToImage(null,
					 * clusters[i], imageSize, imageSize, null, true);
					 * taskMonitor.setProgress((i * 100) / clusters.length); }
					 * success = true;
					 */
				 }
					if (interrupted)
						return;
				} else if (alg instanceof HCPIN)//
				{

				/*	Iterator edges=network.getEdgeList().iterator();
					while(edges.hasNext()){
						CyEdge e = (CyEdge)edges.next();
						System.out.println(network.getRow(e).get("weight",Double.class));
					}
				 */					
						HCPIN algHcpin = (HCPIN) alg;

						algHcpin.setTaskMonitor(taskMonitor, network.getSUID());
					
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 2 of 3:Generating Complexes...");
						clusters = algHcpin.run(network, this.resultId);
						System.err.println("After FAG-EC.Time used:"
								+ algHcpin.getLastFindTime());
						if (interrupted)
							return;
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
						// create all the images here for the clusters, it can be a
						// time consuming operation
						if (this.clusterUtil.getCurrentParameters()
								.getResultParams(this.resultId).isWeakHCPIN())
							clusters = ClusterUtil.sortClusters3(clusters);
						else
							clusters = ClusterUtil.sortClusters2(clusters);

						int imageSize = this.clusterUtil.getCurrentParameters()
								.getResultParams(this.resultId)
								.getDefaultRowHeight();
						
						
						for (int i = 0; i < clusters.length; i++)
							clusterL.add(clusters[i]);
						
						
						
						int count = 0;

						for (Cluster c : clusters) {
							if (this.interrupted)
								return;

							Image img = this.clusterUtil.convertClusterToImage(null,
									c, imageSize, imageSize, null, true);
							c.setImage(img);
							taskMonitor.setProgress(++count / clusters.length);
						}

						success = true;

						/*
						 * imageList = new Image[clusters.length]; for (int i = 0; i
						 * < clusters.length; i++) { if (interrupted) { return; }
						 * imageList[i] = clusterUtil.convertClusterToImage(null,
						 * clusters[i], imageSize, imageSize, null, true);
						 * taskMonitor.setProgress((i * 100) / clusters.length); }
						 * success = true;
						 */
						// }
						if (interrupted)
							return;
					} else if (alg instanceof OHPIN)//
					{

						OHPIN algOhpin = (OHPIN) alg;

						algOhpin.setTaskMonitor(taskMonitor, network.getSUID());
					
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 2 of 3:Generating Complexes...");
						clusters = algOhpin.run(network, this.resultId);
						System.err.println("After OH-PIN.Time used:"
								+ algOhpin.getLastFindTime());
						if (interrupted)
							return;
						taskMonitor.setProgress(0);
						taskMonitor
								.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
						// create all the images here for the clusters, it can be a
						// time consuming operation
					//	if (this.mcodeUtil.getCurrentParameters()
					//			.getResultParams(this.resultId).isWeakFAGEC())
					//		clusters = ClusterUtil.sortClusters3(clusters);
					//	else
							clusters = ClusterUtil.sortClusters2(clusters);

						int imageSize = this.clusterUtil.getCurrentParameters()
								.getResultParams(this.resultId)
								.getDefaultRowHeight();
						
						
						for (int i = 0; i < clusters.length; i++)
							clusterL.add(clusters[i]);
						
						
						
						int count = 0;

						for (Cluster c : clusters) {
							if (this.interrupted)
								return;

							Image img = this.clusterUtil.convertClusterToImage(null,
									c, imageSize, imageSize, null, true);
							c.setImage(img);
							taskMonitor.setProgress(++count / clusters.length);
						}

						success = true;

						/*
						 * imageList = new Image[clusters.length]; for (int i = 0; i
						 * < clusters.length; i++) { if (interrupted) { return; }
						 * imageList[i] = clusterUtil.convertClusterToImage(null,
						 * clusters[i], imageSize, imageSize, null, true);
						 * taskMonitor.setProgress((i * 100) / clusters.length); }
						 * success = true;
						 */
						// }
						if (interrupted)
							return;
						} else if (alg instanceof IPCA)//
						{

							/*	Iterator edges=network.getEdgeList().iterator();
								while(edges.hasNext()){
									CyEdge e = (CyEdge)edges.next();
									System.out.println(network.getRow(e).get("weight",Double.class));
								}
							 */					
									IPCA algIpca = (IPCA) alg;

									algIpca.setTaskMonitor(taskMonitor, network.getSUID());
								
									taskMonitor.setProgress(0);
									taskMonitor
											.setStatusMessage("Step 2 of 3:Generating Complexes...");
									clusters = algIpca.run(network, this.resultId);
									System.err.println("After IPCA.Time used:"
											+ algIpca.getLastFindTime());
									if (interrupted)
										return;
									taskMonitor.setProgress(0);
									taskMonitor
											.setStatusMessage("Step 3 of 3: Drawing the Result Network...");
									// create all the images here for the clusters, it can be a
									// time consuming operation
								//	if (this.mcodeUtil.getCurrentParameters()
								//			.getResultParams(this.resultId).isWeakFAGEC())
								//		clusters = ClusterUtil.sortClusters3(clusters);
							//		else
										clusters = ClusterUtil.sortClusters2(clusters);

									int imageSize = this.clusterUtil.getCurrentParameters()
											.getResultParams(this.resultId)
											.getDefaultRowHeight();
									
									
									for (int i = 0; i < clusters.length; i++)
										clusterL.add(clusters[i]);
									
									
									
									int count = 0;

									for (Cluster c : clusters) {
										if (this.interrupted)
											return;

										Image img = this.clusterUtil.convertClusterToImage(null,
												c, imageSize, imageSize, null, true);
										c.setImage(img);
										taskMonitor.setProgress(++count / clusters.length);
									}

									success = true;

									/*
									 * imageList = new Image[clusters.length]; for (int i = 0; i
									 * < clusters.length; i++) { if (interrupted) { return; }
									 * imageList[i] = clusterUtil.convertClusterToImage(null,
									 * clusters[i], imageSize, imageSize, null, true);
									 * taskMonitor.setProgress((i * 100) / clusters.length); }
									 * success = true;
									 */
									// }
									if (interrupted)
										return;
								} 
//			}
		} catch (Exception e) {
			throw new Exception("Error while executing the analysis", e);
		} finally {
			this.clusterUtil.destroyUnusedNetworks(this.network, clusterL);

			if (this.listener != null)
				this.listener.handleEvent(new AnalysisCompletedEvent(success,
						clusterL));
		}
	}
	
	
	

	public void cancel() {
		this.interrupted = true;
		this.alg.setCancelled(true);
		this.clusterUtil.removeNetworkResult(this.resultId);
		this.clusterUtil.removeNetworkAlgorithm(this.network.getSUID()
				.longValue());
	}

	public String getTitle() {
		return "MCODE Network Cluster Detection";
	}
}