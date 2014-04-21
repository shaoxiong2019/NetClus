package com.litb.netclus.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Store {
	public Map<String, Edge> e=null;
	public Map<String, Vertex> v=null;
	
	HashMap<String, String> ii=null;
	
	HashMap<String, Set<String>> oi=null;
	HashMap<String, String> oc=null;
	HashMap<String, String> om=null;
	
	public int o,c,i,m=0;
	
	public double[][] woi=null;
	public double[][] woc=null;
	public double[][] wom=null;
	
	public Store(){
		e=new HashMap<String, Edge>();
		v=new HashMap<String, Vertex>();
		
		oi=new HashMap<String, Set<String>>();
		oc=new HashMap<String, String>();
		om=new HashMap<String, String>();
	}
	
	public void build(){
		this.prepare();
		System.gc();
		this.formwoi();
		this.formwoc();
		this.formwom();
		clean();
		System.gc();
	}
	
	
	public void formwoi(){
		woi=new double[this.o][this.i];
		
		Iterator<String> oit=oi.keySet().iterator();
		
		while (oit.hasNext()) {
			String key = oit.next();
			Set<String> items=oi.get(key);
			Iterator<String> iit=items.iterator();
			while (iit.hasNext()) {
				String itemId = iit.next();
				this.woi[v.get(key).id][v.get(itemId).id]++;
			}
		}
	}
	
	public void formwoc(){
		this.woc=new double[this.o][this.c];
		
		Iterator<String> it=oc.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			String val=oc.get(key);
			this.woc[this.v.get(key).id][this.v.get(val).id]++;
		}
	}
	
	public void formwom(){
		this.wom=new double[this.o][this.m];
		
		Iterator<String> it=om.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			String val=om.get(key);
			this.wom[this.v.get(key).id][this.v.get(val).id]++;
		}
	}
	
	
	public void prepare(){
		Iterator<String> it=this.e.keySet().iterator();
		while (it.hasNext()){
			String key=it.next();
			Edge val=this.e.get(key);
			
			if(val.des.startsWith("i")){
				if(!oi.get(val.src).contains(val.des)){
					oi.get(val.src).add(val.des);
				}
			}else if(val.des.startsWith("c")){
				oc.put(val.src, val.des);
			}else if(val.des.startsWith("m")){
				om.put(val.src, val.des);
			}
		}
		
		this.e=null;
	}
	
	
	public double oi(int o,int i){
		return woi[o][i];
	}
	
	public double om(int o,int m){
		return wom[o][m];
	}
	
	public double oc(int o,int c){
		return woc[o][c];
	}
	
	public void clean(){
		this.oi=null;
		this.oc=null;
		this.om=null;
	}
	
	public void order(String itemId){
		if (!v.containsKey(itemId)) {
			v.put(itemId, new Vertex(o, Vertex.ORDER, ""));
			o++;
			if(!oi.containsKey(itemId)){
				oi.put(itemId, new HashSet<String>());
			}
		}
	}
	
	public void item(String itemId,String itemName){
		if (!v.containsKey(itemId)){
			v.put(itemId, new Vertex(i, Vertex.ITEM, itemName));
			i++;
		}
	}
	
	public void merchant(String merchantId,String merchantName){
		if (!v.containsKey(merchantId)) {
			v.put(merchantId, new Vertex(m, Vertex.MERCHANT, merchantName));
			m++;
		}
	}
	
	public void customer(String customerId,String customerName){
		if (!v.containsKey(customerId)) {
			v.put(customerId, new Vertex(c, Vertex.CUSTOMER, customerName));
			c++;
		}
	}
}
