package com.litb.netclus.util;

import com.litb.jama.Matrix;

public class MatrixTools {
	
	public static void print(String name,Matrix m){
		System.out.printf("Matrix %s:\r\n",name);
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				System.out.print(m.get(i, j)+" ");
			}
			System.out.print("\r\n");
		}
	}
	
	public static void VectorNorm(Matrix m){
		m=m.times(1.0/m.norm1());
	}
}
