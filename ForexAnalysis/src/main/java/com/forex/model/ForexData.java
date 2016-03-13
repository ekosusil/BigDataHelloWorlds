package com.forex.model;



import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ForexData {
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

	public String getTimeAsDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy");
		return formatter.format(getDate());

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

	@Override
	public String toString() {
		return "ForexData [instrument=" + instrument + ", buyPrice=" + buyPrice
				+ ", sellPrice=" + sellPrice + ", timeStamp=" + timeStamp + "]";
	}
	
	public static long parseStringTimeToLong(String strFormat, String strTime) {
		long _result = 0;
		if (strFormat != null && strTime != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
				Date date = sdf.parse(strTime);
				_result = date.getTime();
			} catch (ParseException e) {
				logger.error("Error Parsing the date");
			}
		}
		return _result;
	}
	
	public static String parseLongAsDate(long timeStamp){
		Date date=new Date(timeStamp);
		String strFormat="dd:MM:yyyy";
		SimpleDateFormat sdf=new SimpleDateFormat(strFormat);
		return sdf.format(date);
	}

	public static void main(String args[]){
		long lTime=1395450441154l;
		System.out.println(parseLongAsDate(lTime));
	}
	
	


}
