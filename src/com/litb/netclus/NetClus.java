package com.litb.netclus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.litb.netclus.entity.Parser;
import com.litb.netclus.entity.Rank;
import com.litb.netclus.entity.RankedObject;
import com.litb.netclus.entity.Store;
import com.litb.netclus.entity.Vertex;

public class NetClus {

	private static int k = 0, t = 10;
	private static Store s=null;
	private static Cluster c=null;
	private static Rank r=null;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: NetClus <k> <t>");
			System.exit(1);
		}

		k = Integer.parseInt(args[0]);
		t = Integer.parseInt(args[1]);

		s = new Store();
		System.out.println("Initialized data store!");

		new Parser("data/data.xml", s);
		System.out.println("Parsing input finished!");

		s.build();
		System.out.println("Building data structure finished!");

		if (k > s.c) {
			System.out
					.println("\nERROR: Number of clusters cannot exceed target objects\n");
			System.exit(1);
		}

		c = new Cluster(s, k);
		c.initCluster();
		System.out.println("Initialized clustering!");
		c.iteration(t);
		
		r=new Rank(s, c);
		System.out.print("Initialized Ranking");

		System.out.println("Printing results!\n");
		print();
	}

	private static void print() {
		HashMap<Integer, String> cid=new HashMap<Integer, String>();
		
		Iterator<String> it=s.v.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Vertex val=s.v.get(key);
			
			if (val.type==Vertex.CUSTOMER){
				cid.put(val.id, val.name);
			}
		}
		
		
		for (int i = 0; i < c.c.size(); i++) {
			ArrayList<RankedObject> l=r.rank(i);
			System.out.printf("Cluster [%s]:\r\n",i);
			for (int j = 0; j < l.size(); j++) {
				System.out.print(cid.get(l.get(i).id)+" "+l.get(i).r);
			}
			System.out.println();
		}
	}
}
