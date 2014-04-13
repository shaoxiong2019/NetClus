package com.litb.netclus;

import java.util.ArrayList;
import java.util.HashMap;

import com.litb.jama.Matrix;

public class SingleCluster {
	
	public ArrayList<Integer> l;
	
	
	public Matrix ro=null;
	public Matrix rc=null;
	public Matrix ri=null;
	public Matrix rm=null;
	
	//cluster center
	public double[] s;
	
	public SingleCluster(int k){
		this.l=new ArrayList<Integer>();
		this.s=new double[k];
	}
}
