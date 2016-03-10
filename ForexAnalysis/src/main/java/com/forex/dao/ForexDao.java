package com.forex.dao;

import java.io.IOException;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.forex.model.ForexData;
public class ForexDao {
	
	private static final byte[] TABLE_NAME=Bytes.toBytes("forex");
	private static final byte[] COLUMN_FAMILY=Bytes.toBytes("prive");
	private static final byte[] COLUMN_NAME_BUY=Bytes.toBytes("buy");
	private static final byte[] COLUMN_NAME_SELL=Bytes.toBytes("sell");
	
	private static Logger logger=LoggerFactory.getLogger(ForexDao.class);
	private HTable table;
	public Put mkPut(ForexData data){
		
		
		Put put=new Put(Bytes.toBytes(data.getInstrument()+"_"+data.getTimeAsDate()));
		put.add(COLUMN_FAMILY, COLUMN_NAME_BUY, data.getTimeStamp(), Bytes.toBytes(data.getBuyPrice()));
		put.add(COLUMN_FAMILY,COLUMN_NAME_SELL,data.getTimeStamp(),Bytes.toBytes(data.getSellPrice()));
		return put;
	}
	
	public Get mkGet(ForexData data){
		Get get=null;
		
		return get;
	}
	
	public void init(){
		Configuration conf=HBaseConfiguration.create();
		
		try {
			table=new HTable(conf,TABLE_NAME);
					
		
		} catch (IOException e) {
			logger.error("Error connection to table = "+e.getMessage());
		}
		
	}
	
	public void close(){
		if(table!=null){
			try {
				table.close();
			} catch (IOException e) {
				logger.error("Error closing the connection "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	

}
