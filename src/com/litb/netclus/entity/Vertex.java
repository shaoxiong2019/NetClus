package com.litb.netclus.entity;

public class Vertex {
	
	/**
	 * order O
	 * customer C
	 * merchant M
	 * item I
	 */
	public int  id=-1;
	public int type=-1;
	public int cluster=-1;
	
	public String name="";
	
	public static int ORDER=1;
	public static int ITEM=2;
	public static int CUSTOMER=3;
	public static int MERCHANT=4;
	public static int CATEGORY=5;
	
	public Vertex(int id,int type,String name) {
		this.id=id;
		this.type=type;
		this.name=name;
	}
}
