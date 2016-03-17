package com.forex.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionHelpers {
	
	private static Logger logger=LoggerFactory.getLogger(FunctionHelpers.class);
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
}
