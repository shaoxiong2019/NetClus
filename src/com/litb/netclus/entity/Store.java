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
	
	HashMap<String, Set<WeightObject>> oi=null;
	HashMap<String, WeightObject> oc=null;
	HashMap<String, WeightObject> om=null;
	HashMap<String, Set<WeightObject>> og=null;
	
	
	public int o,c,i,m,g=0;
	
	public Map<String, double[][]> relations=null;
	
	public double[][] woi=null;
	public double[][] woc=null;
	public double[][] wom=null;
	public double[][] wog=null;
	public Map<String, double[][]> s=new HashMap<String, double[][]>();
	
	/*public static void main(String[] args) {
		Store s=new Store();
		Parser p=new Parser("data/input.xml", s);
		s.build();
	}*/
	
	public Store(){
		e=new HashMap<String, Edge>();
		v=new HashMap<String, Vertex>();
		
		oi=new HashMap<String, Set<WeightObject>>();
		oc=new HashMap<String, WeightObject>();
		om=new HashMap<String, WeightObject>();
		og=new HashMap<String, Set<WeightObject>>();
		relations=new HashMap<String, double[][]>();
	}
	
	public void build(){
		this.prepare();
		System.gc();
		this.formwoi();
		this.formwoc();
		this.formwom();
		this.formwog();
		relations.put("order-item", woi);
		relations.put("order-customer", woc);
		relations.put("order-category", wog);
		relations.put("order-merchant", wom);
		clean();
		System.gc();
	}
	
	
	public void formwoi(){
		woi=new double[this.o][this.i];
		Iterator<String> oit=oi.keySet().iterator();
		while (oit.hasNext()) {
			String key = oit.next();
			Iterator<WeightObject> iit=oi.get(key).iterator();
			while (iit.hasNext()) {
				WeightObject obj = iit.next();
				this.woi[v.get(key).id][v.get(obj.itemId).id]+=obj.weight;
			}
		}
	}
	
	public void formwoc(){
		this.woc=new double[this.o][this.c];
		
		Iterator<String> it=oc.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			WeightObject obj=oc.get(key);
			this.woc[this.v.get(key).id][this.v.get(obj.itemId).id]+=obj.weight;
		}
	}
	
	public void formwom(){
		this.wom=new double[this.o][this.m];
		
		Iterator<String> it=om.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			WeightObject obj=om.get(key);
			this.wom[this.v.get(key).id][this.v.get(obj.itemId).id]+=obj.weight;
		}
	}
	
	public void formwog(){
		this.wog=new double[this.o][this.g];
		
		Iterator<String> it=og.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			Iterator<WeightObject> iit=og.get(key).iterator();
			while (iit.hasNext()) {
				WeightObject obj = iit.next();
				this.wog[this.v.get(key).id][this.v.get(obj.itemId).id]+=obj.weight;
			}
		}
	}
	
	
	public void prepare(){
		Iterator<String> it=this.e.keySet().iterator();
		while (it.hasNext()){
			String key=it.next();
			Edge val=this.e.get(key);
			
			WeightObject obj=new WeightObject(val.des, val.weight);
			
			if(val.des.startsWith("I")){
				if(!oi.get(val.src).contains(obj)){
					oi.get(val.src).add(obj);
				}
			}else if(val.des.startsWith("C")){
				oc.put(val.src, obj);
			}else if(val.des.startsWith("M")){
				om.put(val.src, obj);
			}else if(val.des.startsWith("G")){
				if(!og.get(val.src).contains(obj)){
					og.get(val.src).add(obj);
				}
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
	
	public double og(int o,int g){
		return wog[o][g];
	}
	
	public void clean(){
		this.oi=null;
		this.oc=null;
		this.om=null;
		this.og=null;
	}
	
	public void order(String itemId){
		if (!v.containsKey(itemId)) {
			v.put(itemId, new Vertex(o, Vertex.ORDER, ""));
			o++;
			if(!oi.containsKey(itemId)){
				oi.put(itemId, new HashSet<WeightObject>());
			}
			if(!og.containsKey(itemId)){
				og.put(itemId,new HashSet<WeightObject>());
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
	
	public void category(String categoryId,String categoryName){
		if(!v.containsKey(categoryId)){
			v.put(categoryId, new Vertex(g, Vertex.CATEGORY, categoryName));
			g++;
		}
	}
}

class WeightObject{
	public String itemId;
	public double weight;
	
	public WeightObject(String itemId,double weight){
		this.itemId=itemId;
		this.weight=weight;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WeightObject)) 
			return false;
		if (obj==this)
			return true;
		return this.itemId.equals(((WeightObject)obj).itemId);
	}

	@Override
	public int hashCode() {
		return itemId.length();
	}
}
