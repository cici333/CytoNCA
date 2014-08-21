package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import org.cytoscape.work.TaskMonitor;

public abstract class Matrix {
	int width;
	int height;
	
	
	public int getHeight(){
		return height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public abstract float getElement(int x, int y);
	public abstract boolean setElement(int x, int y, float value);
	public abstract boolean makeSymTri(float[] dblB, float[] dblC, TaskMonitor ts);
	public abstract boolean computeEvSymTri(float[] dblB, float[] dblC, int nMaxIt, float eps, TaskMonitor ts);
	public abstract boolean invertGaussJordan(TaskMonitor taskMonitor);

}
