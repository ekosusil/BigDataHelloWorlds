package com.forex.services;

import java.util.List;

import com.forex.model.ForexData;

public interface ForexService {
	
	public double getBuy(String instrument, long timeStamp);
	public double getSell(String instrument, long timeStamp);
	public void store(ForexData forexData);
	public List<ForexData> scan(String instrument);
	public List<ForexData> scanWithinTimeRange(String instrument,long startTime, long endTime);
	
}
