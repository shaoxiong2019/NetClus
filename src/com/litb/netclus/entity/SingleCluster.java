package com.litb.netclus.entity;

import java.util.ArrayList;

import com.litb.jama.Matrix;

public class SingleCluster {
	
	public ArrayList<Integer> l;//target object list
	
	//rank of attribute objects
	public Matrix ro=null; 
	public Matrix rc=null;
	public Matrix ri=null;
	public Matrix rm=null;
	public Matrix rg=null;
	
	//final rank distribution
	public ArrayList<RankedObject> cl=null;
	public ArrayList<RankedObject> il=null;
	public ArrayList<RankedObject> ml=null;
	public ArrayList<RankedObject> gl=null;
	
	//cluster center
	public double[] s;
	
	public SingleCluster(int k){
		this.l=new ArrayList<Integer>();
		this.s=new double[k];
		this.cl=new ArrayList<RankedObject>();
		this.il=new ArrayList<RankedObject>();
		this.ml=new ArrayList<RankedObject>();
		this.gl=new ArrayList<RankedObject>();
	}
}
