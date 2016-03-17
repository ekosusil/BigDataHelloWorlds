package com.forex.utils;

import java.util.List;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.forex.model.ForexData;
import com.forex.repository.ForexRepository;

public class ForexApp {
	public static void main(String[] args) throws Exception {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(
				"/META-INF/spring/application-context.xml", ForexApp.class);
	
		context.registerShutdownHook();
		
		ForexUtils forexUtils=context.getBean(ForexUtils.class);

		ForexRepository forexRepository=context.getBean(ForexRepository.class);
		
		
		ForexData fxData=new ForexData();
		fxData.setInstrument("GOLD_AUR");
		fxData.setTimeStamp(145771857515l);
		fxData.setBuyPrice(15236);
		fxData.setSellPrice(16223);
		forexRepository.store(fxData);
		System.out.println(forexRepository.getData(fxData.getInstrument(), fxData.getTimeStamp()));
	
	}
}
