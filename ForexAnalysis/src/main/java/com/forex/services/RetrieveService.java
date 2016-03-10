package com.forex.services;

public interface RetrieveService {
	//
	public double getBuy(String instrument, long timeStamp);
	public double getSell(String instrument, long timeStamp);
}
