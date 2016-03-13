package com.forex.stormSpout;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

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

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ForexDataSpout extends BaseRichSpout{
	private static final String AUTHENTICATE_KEY = "";
	private static final String USER_ID = "";
	
	private static final String INSTRUMENT = "EUR_USD";
	private static final String DOMAIN = "https://stream-fxpractice.oanda.com";;
	private static final String TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
	
	private SpoutOutputCollector _collector;
	private static Logger logger = LoggerFactory.getLogger(ForexDataSpout.class);
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
					
					long time=ForexData.parseStringTimeToLong(TIME_FORMAT,strTime);
					double bid = Double.parseDouble(tick.get("bid")
							.toString());
					double ask = Double.parseDouble(tick.get("ask")
							.toString());
					_result=new ForexData(instrument,bid,ask,time);
					//service.store(_result);
					
					System.out.println(_result);
				}
			} else {
				logger.error("Error during parsing the response "
						+ responseLine);
			}

		}

		return _result;
	}
	public void nextTuple() {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpUriRequest httpGet = new HttpGet(DOMAIN + "/v1/prices?accountId="
				+ USER_ID + "&instruments=" + INSTRUMENT);
		httpGet.setHeader(new BasicHeader("Authorization", "Bearer "
				+ AUTHENTICATE_KEY));

		System.out.println("Executing request: " + httpGet.getRequestLine());

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
					_collector.emit(new Values(data));
				}
			} else {
				
				String responseString = EntityUtils.toString(entity, "UTF-8");
				logger.error("ERROR RESPONSE " + responseString);
			}

		} catch (IOException e) {
			logger.error("EXCEPTION when reading service");
		}
		
	}

	public void open(Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		_collector=collector;
		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 declarer.declare(new Fields("forex-data"));
		
	}

}
