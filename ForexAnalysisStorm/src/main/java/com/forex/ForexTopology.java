package com.forex;


import com.forex.bolt.ForexAnalyticBolt;
import com.forex.bolt.HBaseStoreBolt;
import com.forex.stormSpout.ForexDataSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

public class ForexTopology {

	public static void main(String args[]){
		TopologyBuilder builder = new TopologyBuilder();

		ForexDataSpout dataSpout=new ForexDataSpout();
		
		builder.setSpout("data-spout", dataSpout,1);
		builder.setBolt("hbase-bolt", new HBaseStoreBolt(),10).shuffleGrouping("data-spout");
		builder.setBolt("analytics-bolt", new ForexAnalyticBolt(),10).shuffleGrouping("data-spout");
		// create the default config object
		Config conf = new Config();

		// set the config in debugging mode
		conf.setDebug(false);

		// run it in a simulated local cluster

		// set the number of threads to run - similar to setting number of
		// workers in live cluster
		conf.setMaxTaskParallelism(3);

		// create the local cluster instance
		LocalCluster cluster = new LocalCluster();

		// submit the topology to the local cluster
		cluster.submitTopology("forex-analytics", conf,
				builder.createTopology());

	}
}
