package com.forex.servicesImpl;

import java.util.Date;
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
		return dao.getData(instrument, timeStamp).getBuyPrice();
	}

	public double getSell(String instrument, long timeStamp) {
	
		return dao.getData(instrument, timeStamp).getSellPrice();
	}

	

	public void store(ForexData forexData) {
		dao.storeData(forexData);
	}	

	public List<ForexData> scanWithinTimeRange(String instrument,
			long startTime, long endTime) {
		return null;
	}

	
	public List<ForexData> scan(String instrument) {
	
		return dao.scanAll(instrument);
	}


	public ForexData getData(String instrument, long timeStamp) {
		return dao.getData(instrument, timeStamp);
	}
	
	@Override
	public void finalize(){
		dao.close();
	}


	public void delete(ForexData forex) {
		dao.deleteInstrument(forex.getInstrument(), forex.getTimeStamp());
		
	}


	public void delete(String instrument, long timeStamp) {
		dao.deleteInstrument(instrument, timeStamp);
		
	}


	public void delete(String instrument, String strDate) {
		dao.deleteInstrument(instrument, strDate);
	}


	public void delete(String instrument, Date date) {
		dao.deleteInstrument(instrument, date);
		
	}
}
