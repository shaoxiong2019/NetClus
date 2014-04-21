package com.litb.netclus.rankFunc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.litb.jama.Matrix;
import com.litb.netclus.entity.Store;
import com.litb.netclus.entity.Vertex;

public class RankFunc {
	public Store s=null;
	
	/**
	 * key 
	 * value 
	 */
	Map<Integer, Vertex> civ=null;
	Map<Integer, Vertex> ccv=null;
	Map<Integer, Vertex> cmv=null;
 	
	public RankFunc(Store s){
		this.s=s;
		init();
	}
	
	/**
	 * 
	 * @param type the type to adapt simple rank
	 * @param tl target type list
	 */
	public Matrix SimpleRank(double[][] ox,ArrayList<Integer> tl){
		double totalwm=0;
		double[] wm=new double[ox[0].length];
		
		for (int m=0;m<wm.length;m++){
			for (int o = 0; o<tl.size(); o++){
				double w=0.0;
				w=ox[tl.get(o)][m];
				totalwm+=w;
				wm[m]+=w;
			}
		}
		
		for (int j = 0; j < wm.length; j++) {
			wm[j]=wm[j]/totalwm;
		}
		return new Matrix(wm,1).transpose();
	}
	
	
	/**
	 * 
	 * @param type type to adapt authority rank 
 	 * @param tl target type list
	 */
	public Map<String, Matrix> AuthorityRank(ArrayList<Integer> tl,int k){
		int cs=tl.size();
		Map<String, Matrix> arl=new HashMap<String, Matrix>();
		
		double[][] woi=new double[cs][this.s.i];

		for (int o = 0; o < cs; o++) {
			for (int j = 0; j < this.s.i; j++) {
				woi[o][j]=this.s.oi(tl.get(o), j);
				if (woi[o][j]!=0){
					getVertexById(Vertex.ITEM, j).cluster=k;
				}
			}
		}
		
		double[][] woc=new double[cs][this.s.c];
		for (int o = 0; o < cs; o++) {
			for (int j = 0; j < this.s.c; j++) {
				woc[o][j]=this.s.oc(tl.get(o), j);
				if (woc[o][j]!=0.0){
					getVertexById(Vertex.CUSTOMER, j).cluster=k;
				}
			}
		}
		
		Matrix moi=new Matrix(woi);
		Matrix moc=new Matrix(woc);
		
		Matrix doi=Matrix.identity(cs, cs);
		Matrix doc=Matrix.identity(cs, cs);
		
		for (int m=0;m<moi.getRowDimension();m++){
			double rowsum=0;
			for (int n = 0; n < moi.getColumnDimension(); n++) {
				rowsum+=moi.get(m, n);
			}
			if (rowsum==0.0){
				rowsum=1.0;
			}
			doi.set(m, m, rowsum);
		}
		
		for (int m=0;m<moc.getRowDimension();m++){
			double rowsum=0;
			for (int n = 0; n < moc.getColumnDimension(); n++) {
				rowsum+=moc.get(m, n);
			}
			doc.set(m, m, rowsum);
		}
		
		Matrix rim=moi.transpose().times(doc.inverse()).times(moc).times(moc.transpose()).times(doi.inverse()).times(moi);
		Matrix rcm=moc.transpose().times(doi.inverse()).times(moi).times(moi.transpose()).times(doc.inverse()).times(moc);
		
		Matrix eim=rim.eig().getV();
		Matrix ecm=rcm.eig().getV();
		
		double[] pig=new double[this.s.i];
		double[] pcg=new double[this.s.c];
		
		for (int m = 0; m < this.s.i; m++) {
			if (getVertexById(Vertex.ITEM, m).cluster==k){
				pig[m]=(double)this.s.i/(this.s.i+this.s.m+this.s.c+this.s.o);
			}
		}
		
		for (int m = 0; m < pcg.length; m++) {
			if (getVertexById(Vertex.CUSTOMER, m).cluster==k){
				pcg[m]=(double)this.s.c/(this.s.i+this.s.m+this.s.c+this.s.o);
			}
		}
		
		Matrix ri=eim.getMatrix(0, eim.getRowDimension()-1, 0, 0);
		Matrix rc=ecm.getMatrix(0, ecm.getRowDimension()-1, 0, 0);
		
		
		arl.put("item", ri.times(1.0/ri.norm1()));
		arl.put("customer",rc.times(1.0/rc.norm1()));
		return arl;
	}
	
	
	public void init(){
		Iterator<String> it=this.s.v.keySet().iterator();
		ccv=new HashMap<Integer, Vertex>();
		civ=new HashMap<Integer, Vertex>();
		cmv=new HashMap<Integer, Vertex>();
		
		while(it.hasNext()){
			String key=it.next();
			Vertex val=this.s.v.get(key);
			if (val.type==Vertex.CUSTOMER){
				ccv.put(val.id, val);
			}
			if (val.type==Vertex.ITEM){
				civ.put(val.id, val);
			}
			if (val.type==Vertex.MERCHANT){
				cmv.put(val.id, val);
			}
		}
	}
	
	public Vertex getVertexById(int type,int id){
		if (type==Vertex.CUSTOMER){
			return ccv.get(id);
		}
		if (type==Vertex.ITEM){
			return civ.get(id);
		}
		if (type==Vertex.MERCHANT){
			return civ.get(id);
		}
		return null;
	}
}
