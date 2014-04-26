package com.litb.netclus.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.litb.netclus.Cluster;
import com.litb.netclus.entity.Rank;
import com.litb.netclus.entity.RankedObject;
import com.litb.netclus.entity.Store;
import com.litb.netclus.entity.Vertex;
import com.litb.netclus.global.Parameters;

public class ClusterWriter {
	private Store s=null;
	private Cluster c=null;
	private Map<Integer,String> cm=null; 
	private Map<Integer, String> im=null; 
	private Map<Integer, String> gm=null; 
	private Map<Integer, String> mm=null;
	private Rank r=null;
	
	public ClusterWriter(Store s,Cluster c){
		this.s=s;
		this.c=c;
	}
	
	public void WriteCluster() throws IOException{
		init();
		BufferedWriter writer=new BufferedWriter(new FileWriter(new File(Parameters.BASEPATH+"customer_category.txt")));
		for (int k = 0; k < c.clusterNum; k++) {
			ArrayList<RankedObject> l=r.rank(c.c.get(k).cl);
			if(l.size()!=0){
				writer.write("Cluster "+k+":\r\n");
				for (int i = 0; i < l.size(); i++) {
					writer.write(l.get(i).id+"\t"+l.get(i).r+"\t"+cm.get(l.get(i).id)+"\r\n");
				}
				writer.write("\r\n");
			}
		}
		writer.flush();
		writer.close();
		
		writer=new BufferedWriter(new FileWriter(new File(Parameters.BASEPATH+"category.txt")));
		for (int k = 0; k < c.clusterNum; k++) {
			ArrayList<RankedObject> l=r.rank(c.c.get(k).gl);
			if(l.size()!=0){
				writer.write("Cluster "+k+":\r\n");
				
				for (int i = 0; i < l.size(); i++) {
					writer.write(l.get(i).id+"\t"+l.get(i).r+"\t"+gm.get(l.get(i).id)+"\r\n");
				}
				writer.write("\r\n");
			}
		}
		writer.flush();
		writer.close();
		
		writer=new BufferedWriter(new FileWriter(new File(Parameters.BASEPATH+"item.txt")));
		for (int k = 0; k < c.clusterNum; k++) {
			ArrayList<RankedObject> l=r.rank(c.c.get(k).il);
			if(l.size()!=0){
				writer.write("Cluster "+k+":\r\n");
				
				for (int i = 0; i < l.size(); i++) {
					writer.write(l.get(i).id+"\t"+l.get(i).r+"\t"+im.get(l.get(i).id)+"\r\n");
				}
				writer.write("\r\n");
			}
		}
		writer.flush();
		writer.close();
		
		writer=new BufferedWriter(new FileWriter(new File(Parameters.BASEPATH+"merchant.txt")));
		for (int k = 0; k < c.clusterNum; k++) {
			writer.write("Cluster "+k+":\r\n");
			ArrayList<RankedObject> l=r.rank(c.c.get(k).ml);
			for (int i = 0; i < l.size(); i++) {
				writer.write(l.get(i).id+"\t"+l.get(i).r+"\t"+mm.get(l.get(i).id)+"\r\n");
			}
			writer.write("\r\n");
		}
		writer.flush();
		writer.close();
	}
	
	
	private void init() {
		Iterator<String> it = this.s.v.keySet().iterator();
		cm = new HashMap<Integer, String>();
		im = new HashMap<Integer, String>();
		gm = new HashMap<Integer, String>();
		mm = new HashMap<Integer, String>();

		while (it.hasNext()) {
			String key = it.next();
			Vertex val = this.s.v.get(key);
			if (val.type == Vertex.CUSTOMER) {
				cm.put(val.id, val.name);
			}
			if (val.type == Vertex.ITEM) {
				im.put(val.id, val.name);
			}
			if (val.type == Vertex.MERCHANT) {
				mm.put(val.id, val.name);
			}
			if (val.type == Vertex.CATEGORY) {
				gm.put(val.id, val.name);
			}
		}
		r=new Rank(c);
	}
	
}
