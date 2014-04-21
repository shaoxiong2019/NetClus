package com.litb.netclus.entity;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.litb.netclus.Cluster;

public class Rank {
	public List RankList=null;
	public Store s=null;
	public Cluster c=null;
	
	public Rank(Store s,Cluster c){
		this.s=s;
		this.c=c;
	}
	
	public ArrayList<RankedObject> rank(int k){
		ArrayList<RankedObject> rol=new ArrayList<RankedObject>();
		
		for (int i = 0; i <s.c ; i++) {
			rol.add(new RankedObject(c.c.get(k).rc.get(i, 0), i));
		}
		
		Collections.sort(rol,new RankComparetor());
		
		return rol;
	}
	
	
	class RankComparetor implements Comparator<RankedObject>{
		@Override
		public int compare(RankedObject o1, RankedObject o2) {
			if (o1.r>o2.r){
				return 1;
			}
			if (o1.r<o2.r) {
				return -1;
			}
			return 0;
		}
	}
}
