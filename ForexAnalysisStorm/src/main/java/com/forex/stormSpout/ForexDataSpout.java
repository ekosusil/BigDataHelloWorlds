package com.forex.stormSpout;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.forex.model.ForexData;
import com.forex.util.FunctionHelpers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ForexDataSpout extends BaseRichSpout{
	private static final String AUTHENTICATE_KEY = "d00e2ea50267d6f940a523b6917840eb-09fa1a7a9e47c0435a41b113c744c8bd";
	private static final String USER_ID = "6277689";
	
	private static final String INSTRUMENT = "EUR_USD";
	private static final String DOMAIN = "https://stream-fxpractice.oanda.com";;
	private static final String TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
	
	private SpoutOutputCollector _collector;
	private static Logger logger = LoggerFactory.getLogger(ForexDataSpout.class);
	private static Queue<ForexData> receivedData= new LinkedBlockingQueue<ForexData>();
	
	private static ForexData parseLine(String responseLine) {
		ForexData _result = null;

		if (responseLine != null) {
			Object obj = JSONValue.parse(responseLine);
			if (obj != null) {
				JSONObject tick = (JSONObject) obj;

				if (tick.containsKey("tick")) {
					tick = (JSONObject) tick.get("tick");
					String instrument = tick.get("instrument").toString();
					String strTime = tick.get("time").toString();
					
					long time=FunctionHelpers.parseStringTimeToLong(TIME_FORMAT,strTime);
					double bid = Double.parseDouble(tick.get("bid")
							.toString());
					double ask = Double.parseDouble(tick.get("ask")
							.toString());
					_result=new ForexData(instrument,bid,ask,time);
					
					
				}
			} else {
				logger.error("Error during parsing the response "
						+ responseLine);
			}

		}

		return _result;
	}
	public void nextTuple() {
		if (!receivedData.isEmpty()){
			ForexData data=receivedData.remove();
			System.out.println("EMITTING "+data);
			_collector.emit(new Values(data));
		}
	}

	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		_collector=collector;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 declarer.declare(new Fields("forex-data"));
		
	}
	@Override
	public void ack(Object msgId) {
	
		super.ack(msgId);
	}
	@Override
	public void activate() {
	
		super.activate();
		final HttpClient httpClient = HttpClientBuilder.create().build();
		final HttpUriRequest httpGet = new HttpGet(DOMAIN + "/v1/prices?accountId="
				+ USER_ID + "&instruments=" + INSTRUMENT);
		httpGet.setHeader(new BasicHeader("Authorization", "Bearer "
				+ AUTHENTICATE_KEY));
		
		ExecutorService ex=Executors.newSingleThreadExecutor();
		ex.submit(new Runnable(){
			public void run(){
				try {
					HttpResponse resp = httpClient.execute(httpGet);
					HttpEntity entity = resp.getEntity();

					if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
						InputStream stream = entity.getContent();
						String line;
						BufferedReader br = new BufferedReader(new InputStreamReader(
								stream));

						while ((line = br.readLine()) != null) {

							ForexData data=parseLine(line);
							System.out.println("OFFERING "+data);
							receivedData.offer(data);
							
						}
					} else {
						
						String responseString = EntityUtils.toString(entity, "UTF-8");
						logger.error("ERROR RESPONSE " + responseString);
					}

				} catch (IOException e) {
					logger.error("EXCEPTION when reading service");
				}
			}
		});
		

		System.out.println("Executing request: " + httpGet.getRequestLine());

			
	}
	@Override
	public void fail(Object msgId) {
		// TODO Auto-generated method stub
		super.fail(msgId);
	}

}
