package com.forex.dao;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
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
	
	private Put mkPut(ForexData data){
		Put put=new Put(Bytes.toBytes(data.getInstrument()+"_"+data.getTimeAsDate()));
		put.add(COLUMN_FAMILY, COLUMN_NAME_BUY, data.getTimeStamp(), Bytes.toBytes(data.getBuyPrice()));
		put.add(COLUMN_FAMILY,COLUMN_NAME_SELL,data.getTimeStamp(),Bytes.toBytes(data.getSellPrice()));
		return put;
	}
	
	
	public List<ForexData> scanAll(String instrument){
		List<ForexData> data=new LinkedList<ForexData>();
		Scan scan=new Scan(Bytes.toBytes(instrument));
		scan=scan.setMaxVersions(Integer.MAX_VALUE);
		try {
			ResultScanner rs=table.getScanner(scan);
			//TODO convert from Result to ForexData
			for(Result result:rs){
				String strRes="";
				strRes+=Bytes.toString(result.getRow());
				for (Cell kv : result.rawCells()) {
					strRes+=" Family - "
							+ Bytes.toString(CellUtil.cloneFamily(kv));
					strRes+=" : Qualifier - "
							+ Bytes.toString(CellUtil.cloneQualifier(kv));
					strRes+=" : Value: "
							+ Bytes.toDouble(CellUtil.cloneValue(kv)) + " ";
					System.out.println("=="+strRes);

				}
			}
		} catch (IOException e) {
			logger.error("Error during scanning "+e.getMessage());
			e.printStackTrace();
		}
		return data;
		
	}
	
	public void storeData(ForexData data){
		Put put=mkPut(data);
		try {
			table.put(put);
		} catch (RetriesExhaustedWithDetailsException e) {			
			logger.error("Exception on storing data "+e.getMessage());
			e.printStackTrace();
		} catch (InterruptedIOException e) {
			logger.error("Exception on storing data "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private Get mkGet(ForexData data){
		Get get=new Get(Bytes.toBytes(data.getInstrument()+"_"+data.getTimeAsDate()));
		try {
			get.addFamily(COLUMN_FAMILY);
			get.setMaxVersions(Integer.MAX_VALUE);
		} catch (IOException e) {
			logger.error("Exception on querying data "+e.getMessage());
			e.printStackTrace();
		}
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
