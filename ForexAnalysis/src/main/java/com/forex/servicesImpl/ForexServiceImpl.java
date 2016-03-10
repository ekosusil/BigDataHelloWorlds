package com.forex.servicesImpl;

import java.util.List;
import java.util.LinkedList;

import com.forex.dao.ForexDao;
import com.forex.model.ForexData;
import com.forex.services.ForexService;

public class ForexServiceImpl implements ForexService{

	private ForexDao dao;
	
	public ForexServiceImpl(){
		dao=new ForexDao();
		dao.init();
	}
	
	
	public double getBuy(String instrument, long timeStamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getSell(String instrument, long timeStamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	

	public void store(ForexData forexData) {
		dao.storeData(forexData);
	}	

	public List<ForexData> scanWithinTimeRange(String instrument,
			long startTime, long endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<ForexData> scan(String instrument) {
		dao.scanAll(instrument);
		return null;
	}

}
