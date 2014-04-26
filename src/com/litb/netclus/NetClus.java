package com.litb.netclus;

import java.io.IOException;

import com.litb.netclus.entity.Parser;
import com.litb.netclus.entity.Store;
import com.litb.netclus.output.ClusterWriter;

public class NetClus {

	private static int k = 0, t = 10;
	private static Store s=null;
	private static Cluster c=null;
	private static ClusterWriter w;

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: NetClus <k> <t>");
			System.exit(1);
		}

		k = Integer.parseInt("5");
		t = Integer.parseInt(args[1]);

		s = new Store();
		System.out.println("Initialized data store!");

		new Parser("data/input.xml", s);
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
		
		System.out.println("Initialized Ranking");
		w=new ClusterWriter(s, c);
		System.out.println("Writing Result!");
		w.WriteCluster();
	}
}

