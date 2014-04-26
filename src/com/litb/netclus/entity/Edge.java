package com.litb.netclus.entity;

public class Edge {
	
	public String id;
	public String src;
	public String des;
	public double weight;
	
	public Edge(String id,String src,String des,double weight) {
		this.src=src;
		this.des=des;
		this.weight=weight;
	}
}
