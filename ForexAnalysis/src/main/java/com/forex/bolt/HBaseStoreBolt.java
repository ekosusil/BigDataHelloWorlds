package com.forex.bolt;



import java.util.Map;

import com.forex.model.ForexData;
import com.forex.services.ForexService;
import com.forex.servicesImpl.ForexServiceImpl;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class HBaseStoreBolt extends BaseRichBolt {

	private OutputCollector _collector;
	private ForexService service;
	public void execute(Tuple arg0) {
	
		Object o=arg0.getValue(0);
		if(o!=null){
			
			service.store((ForexData)o);
		}
		System.out.println(o);
	}

	public void prepare(Map arg0, TopologyContext arg1, OutputCollector collector) {
		_collector=collector;
		service=new ForexServiceImpl();
		
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}

}
