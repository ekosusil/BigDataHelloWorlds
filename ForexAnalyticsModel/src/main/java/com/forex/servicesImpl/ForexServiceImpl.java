package com.forex.servicesImpl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import com.forex.dao.AbstractForexDao;
import com.forex.daoFactory.AbstractDaoFactory;
import com.forex.model.ForexData;
import com.forex.services.ForexService;

public class ForexServiceImpl implements ForexService, Serializable {

	private static AbstractForexDao dao;
	private static ForexServiceImpl instance;
	private static final Object lock=new Object();

	private ForexServiceImpl(AbstractDaoFactory adf) {
		dao=adf.getDao();
		dao.init();		
	}

	public static ForexServiceImpl getInstance(AbstractDaoFactory adf){
		
		if (instance==null){
			synchronized(lock){
				instance=new ForexServiceImpl(adf);
			}
		}
		return instance;
		
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
		return dao.scanByInstrumentWithinTimeRange(instrument, startTime,
				endTime);
	}

	public List<ForexData> scan(String instrument) {

		return dao.scanByInstrument(instrument);
	}

	public ForexData getData(String instrument, long timeStamp) {
		return dao.getData(instrument, timeStamp);
	}

	@Override
	public void finalize() {
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
