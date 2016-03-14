package com.forex.tests;

import static org.junit.Assert.*;
import com.forex.model.ForexData;

import org.junit.Test;

public class ForexDataTest {
	@Test
	public void testConvertDate(){
		String strTime = "2014-03-21T17:56:13.668154Z";
		String strTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
		assertTrue(ForexData.parseStringTimeToLong(strTimeFormat, strTime)==1395450441154l);		
	}
	@Test
	public void testConvertDateNullParams(){
		assertTrue(ForexData.parseStringTimeToLong(null, null)==0);
	}
	
	@Test
	public void testConvertLongToDate(){
		long lTime=1395450441154l;
		assertTrue(ForexData.parseLongAsDate(lTime).equals("21:03:2014"));
	}
	
	
	
	
	
	
	
	
	
}
