package com.forex.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.Receiver;
import org.apache.spark.api.java.function.Function2;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.apache.spark.streaming.Time;

import com.forex.model.ForexData;
import com.forex.services.ForexService;
import com.forex.servicesImpl.ForexServiceImpl;
import com.forex.util.FunctionHelpers;

public class JavaCustomReceiver extends Receiver<String> {

	private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

	public static void main(String args[]) {
	
	
		SparkConf conf = new SparkConf().setAppName("Test Custom Receiver")
				.setMaster("local[2]");
		JavaStreamingContext ssc = new JavaStreamingContext(conf, new Duration(
				1000));

		JavaReceiverInputDStream<String> lines = ssc
				.receiverStream(new JavaCustomReceiver(" ",
						" "));

		JavaDStream<ForexData> fxData = lines
				.map(new Function<String, ForexData>() {

					public ForexData call(String responseLine) throws Exception {
						ForexData _result = null;

						if (responseLine != null) {
							Object obj = JSONValue.parse(responseLine);
							if (obj != null) {
								
								JSONObject tick = (JSONObject) obj;

								if (tick.containsKey("tick")) {
									tick = (JSONObject) tick.get("tick");
									String instrument = tick.get("instrument")
											.toString();
									String strTime = tick.get("time")
											.toString();

									long time = FunctionHelpers
											.parseStringTimeToLong(TIME_FORMAT,
													strTime);
									double bid = Double.parseDouble(tick.get(
											"bid").toString());
									double ask = Double.parseDouble(tick.get(
											"ask").toString());
									_result = new ForexData(instrument, bid,
											ask, time);

									System.out.println(_result);
								}
							} else {
								System.out
										.println("Error during parsing the response "
												+ responseLine);
							}

						}

						return _result;
					}
				});
		fxData.foreachRDD(new VoidFunction<JavaRDD<ForexData>>() {

			public void call(JavaRDD<ForexData> arg0) throws Exception {
				
				arg0.foreach(new VoidFunction<ForexData>(){
					 
					
					
					public void call(ForexData fxData) throws Exception {
						if(fxData!=null){
							ForexService fxService=ForexServiceImpl.getInstance();
							System.out.println(fxData);
							fxService.store(fxData);
						//	forexRepository.store(fxData);
						}
						
					}
					
				});
			}

		});

		ssc.start();
		ssc.awaitTermination();
	}

	private String userId;
	private String key;
	private String instrument;

	public JavaCustomReceiver(String userId, String key) {
		super(StorageLevel.MEMORY_AND_DISK_2());

		this.userId = userId;
		this.key = key;
		this.instrument = "EUR_USD";
	}

	@Override
	public void onStart() {
		new Thread() {
			@Override
			public void run() {
				receive();
			}
		}.start();

	}

	private void receive() {
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpUriRequest httpGet = new HttpGet(
				"https://stream-fxpractice.oanda.com/v1/prices?accountId="
						+ userId + "&instruments=" + instrument);
		httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + key));

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
					this.store(line);
				}
			} else {

				String responseString = EntityUtils.toString(entity, "UTF-8");
				System.out.println("ERROR RESPONSE " + responseString);
			}

		} catch (IOException e) {
			System.out.println("EXCEPTION when reading service");
		}

	}

	@Override
	public void onStop() {
	}

}
