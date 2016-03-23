package com.forex.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.forex.model.ForexData;

public abstract class AbstractForexDao implements Serializable{

	
	
	
	public abstract void init();
	public abstract void close();
	public abstract ForexData getData(String instrument, long timeStamp);
	public abstract void storeData(ForexData data);
	public abstract List<ForexData> scanByInstrument(String instrument); 
	public abstract List<ForexData> scanByInstrumentWithinTimeRange(String instrument,long timeStampBegin, long timeStampEnd);
	public abstract void deleteInstrument(String instrument, long timeStamp);
	public abstract void deleteInstrument(String instrument, String strDate);
	public abstract void deleteInstrument(String instrument, Date date);
}
