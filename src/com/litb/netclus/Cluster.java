package com.litb.netclus;

import java.util.ArrayList;
import java.util.Iterator;

import com.litb.jama.Matrix;
import com.litb.netclus.entity.SingleCluster;
import com.litb.netclus.entity.Store;
import com.litb.netclus.entity.Vertex;
import com.litb.netclus.rankFunc.RankFunc;

@SuppressWarnings("unused")
public class Cluster {
	private Store s=null;
	private int clusterNum;
	public ArrayList<SingleCluster> c=null;
	private int ITER=7;
	
	private double PRIOR_COEFFCIENTS=0.8;
	private static double SMOOTHING_COEFFCIENTS=0.4;
	
	private double[][] pko=null;
	private RankFunc rf=null;
	private SingleCluster background=null;
	private int iterationTimes=0;
	private int times=0;

	
	public Cluster(Store s,int k){
		this.s=s;
		this.clusterNum=k;
	}
	
	
	public void iteration(int t) {
		iterationTimes=t;
		for (int i = 0; i < t; i++) {
			step1();
			step2();
			step3();
		}
	}
	
	
	/**
	 * build ranking-based probability generative model for each net-cluster
	 */
	public void step1(){
		for (int k = 0; k < clusterNum; k++) {
			/*Map<String, Matrix> arl=rf.AuthorityRank(this.c.get(k).l,k);
			this.c.get(k).ri=arl.get("item");
			this.c.get(k).rc=arl.get("customer");*/
			this.c.get(k).rc=rf.SimpleRank(this.s.woc, this.c.get(k).l);
			this.c.get(k).ri=rf.SimpleRank(this.s.woi, this.c.get(k).l);
			this.c.get(k).rm=rf.SimpleRank(this.s.wom, this.c.get(k).l);
			System.gc();
		}
	}
	
	
	public void generateBackgroundModel() {
		background.rc=rf.SimpleRank(this.s.woc, background.l);
		background.ri=rf.SimpleRank(this.s.woi, background.l);
		background.rm=rf.SimpleRank(this.s.wom, background.l);
	}
	
	
	/**
	 * calculate the posterior probability of target objects
	 */
	public void step2(){
		double[] pk=new double[clusterNum];
		double[][] pok=new double[clusterNum][this.s.o];
		pko=new double[this.s.o][clusterNum];
		
		for (int i = 0; i < pk.length; i++) {
			pk[i]=1.0/(clusterNum+1);
		}
		
		for (int k = 0; k < clusterNum; k++) {
			pok[k]=getPxk(k).getColumnPackedCopy();
			
			for (int i = 0; i < this.ITER; i++) {
				for (int m = 0; m < this.s.o; m++) {
					pk[k]+=pk[k]*pok[k][m]/this.s.o;	
				}
			}
		}
		
		for (int i = 0; i < this.s.o; i++) {
			for (int j = 0; j < clusterNum; j++) {
				pko[i][j]=pok[j][i]*pk[j];
			}
		}
		
		//center vector
		for (int k = 0; k < clusterNum; k++) {
			int cs=this.c.get(k).l.size();
			
			for (int i = 0; i < cs; i++) {
				for (int j = 0; j < clusterNum; j++) {
					this.c.get(k).s[j]+=pko[this.c.get(k).l.get(i)][j];
				}
			}
			
			for (int y=0; y<clusterNum; y++) {
				this.c.get(k).s[y] /= cs;
			}
		}
	}
	
	public void step3(){
		int[] o=new int[this.s.o];
		
		Iterator<String> it=this.s.v.keySet().iterator();
		while (it.hasNext()) {
			String key =it.next();
			Vertex val=this.s.v.get(key);
			if (val.type==Vertex.ORDER) {
				double minDis=Double.MAX_VALUE;
				
				for (int k = 0; k < clusterNum; k++) {
					double num=0.0,denp1=0.0,denp2=0.0;
					
					for (int i = 0; i < clusterNum; i++) {
						num+=pko[val.id][i]*this.c.get(k).s[i];
						denp1+=Math.pow(pko[val.id][i], 2.0);
						denp2+=Math.pow(this.c.get(k).s[i], 2.0);
					}
					
					double dis=1-(num/Math.pow(denp1*denp2,0.5));
					
					if(dis<=minDis){
						minDis=dis;
						o[val.id]=k;
						val.cluster=k;
					}
				}
			}
		}
		
		pko=null;
		clearClusterList();
		
		beginCluster();
		
		for (int i = 0; i < o.length; i++) {
			this.c.get(o[i]).l.add(i);
		}
		
		System.gc();
	}
	
	public void clearClusterList() {
		for (int i = 0; i < clusterNum; i++) {
			this.c.get(i).l.clear();
		}
	}


	public Matrix getPxk(int k){
		double[] pok=new double[this.s.o];
		double[] rates=getPTxG(this.c.get(k).l);
		
		for (int i = 0; i <this.s.o; i++) {
			pok[i]=1.0;
			for (int c = 0; c < this.s.c; c++) {
				double w=this.s.oc(i, c);
				double r=background.rc.get(c, 0)*SMOOTHING_COEFFCIENTS+this.c.get(k).rc.get(c, 0)*SMOOTHING_COEFFCIENTS;
				pok[i]*=Math.pow(r*rates[0], w);
			}
			for (int n = 0; n < this.s.i; n++) {
				double w=this.s.oi(i,n);
				double r=background.ri.get(n, 0)*SMOOTHING_COEFFCIENTS+this.c.get(k).ri.get(n, 0)*SMOOTHING_COEFFCIENTS;
				pok[i]*=Math.pow(r*rates[1], w);
			}
			for (int m = 0; m < this.s.m; m++) {
				double w=this.s.om(i,m);
				double r=background.rm.get(m, 0)*SMOOTHING_COEFFCIENTS+this.c.get(k).rm.get(m, 0)*SMOOTHING_COEFFCIENTS;
				pok[i]*=Math.pow(r*rates[2], w);
			}
			
		}
		return new Matrix(pok,1).transpose();
	}
	
	
	
	public double[] getPTxG(ArrayList<Integer> tl){
		double[] rates=new double[3];
		
		int cc=0,ci=0,cm=7;
		for (int i = 0; i < tl.size(); i++) {
			for (int j = 0; j < this.s.c; j++) {
				if (this.s.oc(tl.get(i),j)!=0.0){
					cc++;
				}
			}
			
			for (int n = 0; n < this.s.i; n++) {
				if (this.s.oi(tl.get(i),n)!=0.0){
					ci++;
				}
			}
		}
		rates[0]=(double)cc/(cc+cm+ci+tl.size());
		rates[1]=(double)ci/(cc+cm+ci+tl.size());
		rates[2]=(double)cm/(cc+cm+ci+tl.size());
		return rates;
	}
	
	public void beginCluster(){
		this.c=new ArrayList<SingleCluster>();
		for (int i = 0; i < clusterNum; i++) {
			c.add(new SingleCluster(clusterNum));
		}
	}
	
	public void initCluster(){
		beginCluster();
		
		background=new SingleCluster(clusterNum);
		
		Iterator<String> it=s.v.keySet().iterator();
		
		int count_order=0;
		while (it.hasNext()) {
			String key = it.next();
			Vertex val=s.v.get(key);
			
			if (val.type==Vertex.ORDER){
				int Num=count_order++%clusterNum;
				this.s.v.get(key).cluster=Num;
				
				this.c.get(Num).l.add(this.s.v.get(key).id);
				background.l.add(this.s.v.get(key).id);
			}
		}
		
		this.rf=new RankFunc(this.s);
		generateBackgroundModel();
	}
}	
