package com.forex.model;



import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ForexData {
	private static final String INSTRUMENT_DATE_SEPARATOR_ROW_KEY_HBASE=";";
	private static Logger logger = LoggerFactory.getLogger(ForexData.class);
	public ForexData() {
	}

	public ForexData(String instrument, double buyPrice, double sellPrice,
			long timeStamp) {
		this.instrument = instrument;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.timeStamp = timeStamp;
	}

	

	private String instrument;
	private double buyPrice;
	private double sellPrice;
	private long timeStamp;
	
	public Date getDate() {
		Date _result = null;
		_result = new Date(timeStamp);
		return _result;
	}

	public String getTimeAsString() {
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
		return formatter.format(getDate());
	}

	public String getDateAsString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy");
		return formatter.format(getDate());

	}
	
	public void parseRowKey(String str){
		if (str!=null && str.contains(INSTRUMENT_DATE_SEPARATOR_ROW_KEY_HBASE)){
			String tokens[]=str.split(INSTRUMENT_DATE_SEPARATOR_ROW_KEY_HBASE);
			this.setInstrument(tokens[0]);
		}
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String createRowKey(){
		return this.instrument+INSTRUMENT_DATE_SEPARATOR_ROW_KEY_HBASE+this.getDateAsString();
	}
	
	@Override
	public String toString() {
		return "ForexData [instrument=" + instrument + ", buyPrice=" + buyPrice
				+ ", sellPrice=" + sellPrice + ", timeStamp=" + timeStamp + "]";
	}


	
	


}
