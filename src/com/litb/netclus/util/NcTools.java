package com.litb.netclus.util;

import java.awt.List;

import com.litb.jama.Matrix;

public class NcTools {
	
	public static void print(String name,Matrix m){
		System.out.printf("Matrix %s:\r\n",name);
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				System.out.print(m.get(i, j)+" ");
			}
			System.out.print("\r\n");
		}
	}
	
	public static Matrix Norm(Matrix m){
		m=m.times(1.0/m.norm1());
		return m;
	}
	
	public static double[] VecterNorm(double[] dl){
		double total=0.0;
		for (int i = 0; i < dl.length; i++) {
			total += dl[i];
		}
		for (int i = 0; i < dl.length; i++) {
			dl[i]=dl[i]/total;
		}
		return dl;
	}
	

	public static double[] addArray(double[] x,double[] y){
		for (int i = 0; i < y.length; i++) {
			x[i]=x[i]+y[i];
		}
		return x;
	}
	
	public static double getEMinterval(double[] pk,double[] newpk){
		double sumInterval=0.0;
		double sum=0.0;
		for (int i = 0; i < newpk.length; i++) {
			sumInterval+=Math.abs(newpk[i]-pk[i]);
			sum+=pk[i];
		}
		return (double)(sumInterval/sum);
	}
	
	public static double getClusterInterval(List oldClusters,List newClusters){
		
		return 0;
	}
	
}
