package com.litb.netclus.global;

public class Parameters {
	public static double PRIOR_COEFFCIENTS=0.80;
	public static double SMOOTHING_COEFFCIENTS=0.40;
	public static int MAX_EM_ITER=20;
	public static int MAX_CLUSTER_ITER=20;
	
	/**
	 * model parameters
	 */
	public static double FREQUENCY_WEIGHT=0.437;
	public static double MONETORY_WEIGHT=0.563;
	
	public static String[] relationSet={"order-item","order-customer","order-category","order-merchant"};
	public static String BASEPATH="D:/Cluster/";
}
