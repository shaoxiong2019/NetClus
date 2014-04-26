package com.litb.netclus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.litb.jama.Matrix;
import com.litb.netclus.entity.RankedObject;
import com.litb.netclus.entity.SingleCluster;
import com.litb.netclus.entity.Store;
import com.litb.netclus.entity.Vertex;
import com.litb.netclus.global.Parameters;
import com.litb.netclus.rankFunc.RankFunc;
import com.litb.netclus.util.NcTools;

public class Cluster {
	private Store s=null;
	public int clusterNum;
	public ArrayList<SingleCluster> c=null;
	
	private double[][] pko=null;
	private RankFunc rf=null;
	private SingleCluster background=null;
	private double clusterInterval=1.0;

	
	public Cluster(Store s,int k){
		this.s=s;
		this.clusterNum=k;
	}
	
	public void iteration(int t) {
		for (int i = 0; i < t && clusterInterval>0.0001; i++) {
			step1();
			step2();
			step3();
		}
		step1();
		step2();
		step4();
	}
	
	/**
	 * build ranking-based probability generative model for each net-cluster
	 */
	private void step1(){
		for (int k = 0; k < clusterNum; k++) {
			/*Map<String, Matrix> arl=rf.AuthorityRank(this.c.get(k).l,k);
			this.c.get(k).ri=arl.get("item");
			this.c.get(k).rc=arl.get("customer");*/
			this.c.get(k).ri=rf.SimpleRank(this.s.relations.get("order-item"), this.c.get(k).l);
			this.c.get(k).rc=rf.SimpleRank(this.s.relations.get("order-customer"), this.c.get(k).l);
			this.c.get(k).rg=rf.SimpleRank(this.s.relations.get("order-category"), this.c.get(k).l);
			this.c.get(k).rm=rf.SimpleRank(this.s.relations.get("order-merchant"), this.c.get(k).l);
			System.gc();
		}
	}
	
	
	/**
	 * calculate the posterior probability of target objects and center vector
	 */
	private void step2(){
		double[] pk=new double[clusterNum];
		double[] newPk=new double[clusterNum];
		double[][] pok=new double[clusterNum][this.s.o];
		pko=new double[this.s.o][clusterNum];
		
		
		for (int i = 0; i < pk.length; i++) {
			pk[i]=1.0/(clusterNum+1);
		}
		
		for (int k = 0; k < clusterNum; k++) {
			pok[k] = getPxk(k).getColumnPackedCopy();
		}
		
		double espi_pk=1.0;
		for (int i = 0; i < Parameters.MAX_EM_ITER && espi_pk>0.00001; i++) {
			for (int j = 0; j < newPk.length; j++) {
				newPk[j]=0.0;
			}
			
			for (int j = 0; j < this.s.o; j++) {
				double sum=0.0;
				for (int k = 0; k < clusterNum; k++) {
					pko[j][k]=pok[k][j]*pk[k];
					sum+=pko[j][k];
				}
				
				for (int k = 0; k < clusterNum; k++) {
					if (sum==0.0){
						pko[j][k]=0.0;
					}else {
						pko[j][k]=pko[j][k]/sum;
					}
					newPk[k]+=pko[j][k];
				}
			}
			
			for (int k = 0; k < clusterNum; k++) {
				newPk[k]=newPk[k]/this.s.o;
			}
			
			espi_pk=NcTools.getEMinterval(pk, newPk);
			double[] tmp = pk;
            pk = newPk;
            newPk = tmp;
		}
		System.gc();
		
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
	
	private void step3(){
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
		clusterInterval=getCluseterInterval(o);
		
		pko=null;
		clearClusterList();
		
		beginCluster();
		
		for (int i = 0; i < o.length; i++) {
			this.c.get(o[i]).l.add(i);
		}
		System.gc();
	}
	
	/**
	 * the posterior probabilities for attribute objects
	 */
	private void step4(){
		int[] i_indicator=atrributeObjectCluster(this.s.woi);
		int[] c_indicator=atrributeObjectCluster(this.s.woc);
		int[] g_indicator=atrributeObjectCluster(this.s.wog);
		
		for (int i = 0; i < c_indicator.length; i++) {
			this.c.get(c_indicator[i]).cl.add(new RankedObject(i, 0.0));
		}
		for (int i = 0; i < g_indicator.length; i++) {
			this.c.get(g_indicator[i]).gl.add(new RankedObject(i, 0.0));
		}
		for (int i = 0; i < i_indicator.length; i++) {
			this.c.get(i_indicator[i]).il.add(new RankedObject(i, 0.0));
		}
		for (int i = 0; i < clusterNum; i++) {
			for (int j = 0; j < this.s.m; j++) {
				this.c.get(i).ml.add(new RankedObject(j, 0.0));
			}
		}
		
		calculateDistribution();
	}
	
	private void calculateDistribution() {
		for (int k = 0; k < clusterNum; k++) {
			this.c.get(k).cl=RankAttributeInCluster(this.s.woc, this.c.get(k).cl, this.c.get(k).l);
			this.c.get(k).il=RankAttributeInCluster(this.s.woi, this.c.get(k).il, this.c.get(k).l);
			this.c.get(k).gl=RankAttributeInCluster(this.s.wog, this.c.get(k).gl, this.c.get(k).l);
			this.c.get(k).ml=RankAttributeInCluster(this.s.wom, this.c.get(k).ml, this.c.get(k).l);
		}
	}
	
	private ArrayList<RankedObject> RankAttributeInCluster(double[][] ox,ArrayList<RankedObject> attrObjs,ArrayList<Integer> tl){
		ArrayList<RankedObject> rol=new ArrayList<RankedObject>();
		boolean[] flag=new boolean[ox[0].length];
		for (int i = 0; i < flag.length; i++) {
			flag[i]=false;
		}
		
		double totalw=0.0;
		for (int i = 0; i < attrObjs.size(); i++) {
			for (int j = 0; j < tl.size(); j++) {
				double w=ox[tl.get(j)][attrObjs.get(i).id];
				if (w!=0.0){
					if(flag[attrObjs.get(i).id]==false){
						rol.add(new RankedObject(attrObjs.get(i).id, w));
						flag[attrObjs.get(i).id]=true;
					}else {
						for (int k = 0; k < rol.size(); k++) {
							if (rol.get(k).id==attrObjs.get(i).id) {
								rol.get(k).r+=w;
							}
							
						}
					}
					totalw+=w;
				}
			}
		}
		
		for (int i = 0; i < rol.size(); i++) {
			rol.get(i).r=rol.get(i).r/totalw;
		}
		return rol;
	}
	
	private double getCluseterInterval(int[] indicator){
		int[] cur=new int[this.s.o];
		for (int i = 0; i < clusterNum; i++) {
			for (int j = 0; j < this.c.get(i).l.size(); j++) {
				cur[this.c.get(i).l.get(j)]=i;
			}
		}
		
		int count=0;
		for (int i = 0; i < cur.length; i++) {
			if (cur[i]!=indicator[i]) {
				count++;
			}
		}
		return (double)count/(double)this.s.o;
	}


	private int[] atrributeObjectCluster(double[][] ox){
		double[][] pkx=getAvgPkx(ox);
		int[] indicator=new int[ox[0].length];
		
		
		double minDis=Double.MAX_VALUE;
		for (int n = 0; n < ox[0].length; n++) {
			for (int k = 0; k < clusterNum; k++) {
				double num=0.0,denp1=0.0,denp2=0.0;
				for (int j = 0; j < clusterNum; j++) {
					num+=pkx[n][j]*this.c.get(k).s[j];
					denp1+=Math.pow(pkx[n][j], 2.0);
					denp2+=Math.pow(this.c.get(k).s[j], 2.0);
				}
				
				double dis=1-num/(Math.pow(denp1*denp2, 0.5));
				
				if (dis<=minDis){
					minDis=dis;
					indicator[n]=k;
				}
			}
		}
		return indicator;
	}
	
	private double[] getAttributeNgx(double[][] ox){
		double[] ngx=new double[ox[0].length];
		int count;
		for (int j = 0; j < ox[0].length; j++) {
			count=0;
			for (int i = 0; i < ox.length; i++) {
				if (ox[i][j]!=0.0){
					count++;
				}
			}
			ngx[j]=count;
		}
		return ngx;
	}
	
	private double[][] getAvgPkx(double[][] ox){
		double[][] pkx=new double[ox[0].length][clusterNum];
		double[] ngx=getAttributeNgx(ox);
		
		for (int j = 0; j < ox[0].length; j++) {
			for (int i = 0; i < ox.length; i++) {
				if (ox[i][j]!=0.0){
						pkx[j]=NcTools.addArray(pkx[j], pko[i]);
				}
			}
			
			for (int m = 0; m < pkx[j].length; m++) {
				if(ngx[j]!=0.0){
					pkx[j][m]=pkx[j][m]/ngx[j];
				}else {
					pkx[j][m]=0.0;
				}
			}
		
		}
		return pkx;
	}

	private Matrix getPxk(int k){
		double[] pok=new double[this.s.o];
		double[] PtxG=getPTxG(this.c.get(k).l);
		
		for (int o = 0; o <this.s.o; o++) {
			pok[o]=1.0;
			for (int i = 0; i < this.s.i; i++) {
				double w=this.s.oi(o,i);
				double r=background.ri.get(i, 0)*Parameters.SMOOTHING_COEFFCIENTS+
						this.c.get(k).ri.get(i, 0)*(1.0-Parameters.SMOOTHING_COEFFCIENTS);
				pok[o]+=r*PtxG[0]*w;
			}
			for (int c = 0; c < this.s.c; c++) {
				double w=this.s.oc(o, c);
				double r=background.rc.get(c, 0)*Parameters.SMOOTHING_COEFFCIENTS+
						this.c.get(k).rc.get(c, 0)*(1.0-Parameters.SMOOTHING_COEFFCIENTS);
				pok[o]+=r*PtxG[1]* w;
			}
			for (int g = 0; g < this.s.g; g++) {
				double w=this.s.og(o, g);
				double r=background.rg.get(g, 0)*Parameters.SMOOTHING_COEFFCIENTS+
						this.c.get(k).rg.get(g, 0)*(1.0-Parameters.SMOOTHING_COEFFCIENTS);
				pok[o]+=r*PtxG[2]*w;
			}
			for (int m = 0; m < this.s.m; m++) {
				double w=this.s.om(o,m);
				double r=background.rm.get(m, 0)*Parameters.SMOOTHING_COEFFCIENTS+
						this.c.get(k).rm.get(m, 0)*(1.0-Parameters.SMOOTHING_COEFFCIENTS);
				pok[o]+=r*PtxG[3]*w;
			}
		}
		return NcTools.Norm(new Matrix(pok,1).transpose());
	}
	
	private double[] getPTxG(ArrayList<Integer> tl){
		double[] TxG=new double[Parameters.relationSet.length];
		Set<Integer> temp=new HashSet<Integer>();//avoid duplicate
		
		for (int j = 0; j < Parameters.relationSet.length; j++) {
			double[][] ox = this.s.relations.get(Parameters.relationSet[j]);
			for (int i = 0; i < tl.size(); i++) {
				for (int n = 0; n < ox[0].length; n++) {
					if (ox[i][n] != 0.0)
						if (!temp.contains(n)) {
							TxG[j]++;
							temp.add(n);
						}
				}
			}
			temp.clear();
		}	
		
		double sum=0.0;
		for (int i = 0; i < TxG.length; i++) {
			sum += TxG[i];
		}
		for (int i = 0; i < TxG.length; i++) {
			if (sum!=0.0) {
				TxG[i]=TxG[i]/(sum+tl.size());
			}else {
				TxG[i]=0.0;
			}
			
		}
		return TxG;
	}
	
	private void beginCluster(){
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
	
	
	public void generateBackgroundModel() {
		background.rc=rf.SimpleRank(this.s.woc, background.l);
		background.ri=rf.SimpleRank(this.s.woi, background.l);
		background.rm=rf.SimpleRank(this.s.wom, background.l);
		background.rg=rf.SimpleRank(this.s.wog, background.l);
	}
	
	
	public void clearClusterList() {
		for (int i = 0; i < clusterNum; i++) {
			this.c.get(i).l.clear();
		}
	}
}	
