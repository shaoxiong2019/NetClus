package com.litb.netclus.entity;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.litb.netclus.Cluster;

public class Rank {
	public List RankList=null;
	public Cluster c=null;
	
	public Rank(Cluster c){
		this.c=c;
	}
	
	public ArrayList<RankedObject> rank(ArrayList<RankedObject> l){
		ArrayList<RankedObject> rol=l;
		Collections.sort(rol,new RankComparetor());
		return rol;
	}
	
	class RankComparetor implements Comparator<RankedObject>{
		@Override
		public int compare(RankedObject o1, RankedObject o2) {
			if (o1.r>o2.r){
				return -1;
			}
			if (o1.r<o2.r) {
				return 1;
			}
			return 0;
		}
	}
}
