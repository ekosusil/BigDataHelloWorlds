package com.forex.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Repository;

import com.forex.model.ForexData;
import com.forex.services.ForexService;

@Repository
public class ForexRepository {
	@Autowired
	private HbaseTemplate hbaseTemplate;
	private static final String STR_TABLE_NAME = "forex";
	private static final String STR_COLUMN_FAMILY = "price";
	private static final String STR_COLUMN_NAME_BUY = "buy";
	private static final String STR_COLUMN_NAME_SELL = "sell";

	private static final byte[] TABLE_NAME = Bytes.toBytes(STR_TABLE_NAME);
	private static final byte[] COLUMN_FAMILY = Bytes
			.toBytes(STR_COLUMN_FAMILY);
	private static final byte[] COLUMN_NAME_BUY = Bytes
			.toBytes(STR_COLUMN_NAME_BUY);
	private static final byte[] COLUMN_NAME_SELL = Bytes
			.toBytes(STR_COLUMN_NAME_SELL);
	private static final String INSTRUMENT_DATE_SEPARATOR = ";";
	private static final long RETRIEVE_ALL_VERSION = 0l;

	private Scan mkScanByInstrument(String instrument) {
		Scan scan = new Scan();
		List<Long> timeStamps = new LinkedList<Long>();
		scan.setFilter(new PrefixFilter(Bytes.toBytes(instrument)));
		scan.setMaxVersions(Integer.MAX_VALUE);
		return scan;
	}

	private Scan mkScanWithTimestampFilter(String instrument,
			long timeStampBegin, long timeStampEnd) {
		Scan scan = new Scan();
		List<Long> timeStamps = new LinkedList<Long>();
		for (long id = timeStampBegin; id <= timeStampEnd; id++) {
			timeStamps.add(id);
		}

		FilterList filterList = new FilterList();
		filterList.addFilter(new TimestampsFilter(timeStamps));
		filterList.addFilter(new PrefixFilter(Bytes.toBytes(instrument)));
		scan.setFilter(filterList);
		scan.setMaxVersions(Integer.MAX_VALUE);
		return scan;
	}

	private List<ForexData> doScan(String instrument, long... timestamps) {
		final Map<Long, ForexData> mapData = new HashMap<Long, ForexData>();
		Scan scan = null;
		if (timestamps.length == 2 && timestamps[1] > timestamps[0])

			scan = mkScanWithTimestampFilter(instrument, timestamps[0],
					timestamps[1]);
		else
			scan = mkScanByInstrument(instrument);

		hbaseTemplate.find(STR_TABLE_NAME, scan, new RowMapper<ForexData>() {

			public ForexData mapRow(Result res, int rowNum) throws Exception {
				List<Cell> cells = res.listCells();

				for (Cell curCell : cells) {
					ForexData data = null;
					if (mapData.containsKey(curCell.getTimestamp())) {
						data = mapData.get(curCell.getTimestamp());
					} else {
						data = new ForexData();
						String strKey[] = Bytes.toString(res.getRow()).split(
								INSTRUMENT_DATE_SEPARATOR);
						data.setInstrument(strKey[0]);
						mapData.put(curCell.getTimestamp(), data);

					}

					if (Arrays.equals(CellUtil.cloneQualifier(curCell),
							COLUMN_NAME_BUY)) {
						data.setBuyPrice(Bytes.toDouble(CellUtil
								.cloneValue(curCell)));
					} else if (Arrays.equals(CellUtil.cloneQualifier(curCell),
							COLUMN_NAME_SELL)) {
						data.setSellPrice(Bytes.toDouble(CellUtil
								.cloneValue(curCell)));
					}
					data.setTimeStamp(curCell.getTimestamp());

				}
				return null;

			}

		});
		List<ForexData> dataList = new LinkedList<ForexData>();
		dataList.addAll(mapData.values());
		return dataList;
	}

	public List<ForexData> scan(String instrument) {
		return doScan(instrument);
	}

	public double getBuy(String instrument, long timeStamp) {
		
		
		
		return 0;
	}

	public double getSell(String instrument, long timeStamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ForexData getData(String instrument, long timeStamp) {
		return hbaseTemplate.get(STR_TABLE_NAME, instrument+INSTRUMENT_DATE_SEPARATOR+ForexData.parseLongAsDate(timeStamp), STR_COLUMN_FAMILY, new RowMapper<ForexData>(){

			public ForexData mapRow(Result res, int arg1) throws Exception {
			
				ForexData _result=new ForexData();
				String strKey[] = Bytes.toString(res.getRow()).split(
						INSTRUMENT_DATE_SEPARATOR);
				_result.setInstrument(strKey[0]);
				_result.setTimeStamp(ForexData.parseStringTimeToLong(
						"dd:MM:yyyy", strKey[1]));
				_result.setBuyPrice(Bytes.toDouble(res.getValue(COLUMN_FAMILY,
						COLUMN_NAME_BUY)));
				_result.setSellPrice(Bytes.toDouble(res.getValue(COLUMN_FAMILY,
						COLUMN_NAME_SELL)));
				
				return _result;
			}
			
		});
		
	}

	public void store(ForexData forexData) {
		String rowName = forexData.getInstrument() + INSTRUMENT_DATE_SEPARATOR
				+ forexData.getTimeAsDate();
	
		hbaseTemplate.put(STR_TABLE_NAME, rowName, STR_COLUMN_FAMILY,
				STR_COLUMN_NAME_BUY, Bytes.toBytes(forexData.getBuyPrice()));
		hbaseTemplate.put(STR_TABLE_NAME, rowName, STR_COLUMN_FAMILY,
				STR_COLUMN_NAME_SELL, Bytes.toBytes(forexData.getSellPrice()));
	}

	public List<ForexData> scanWithinTimeRange(String instrument,
			long startTime, long endTime) {

		return doScan(instrument, startTime, endTime);

	}

	public void delete(ForexData forex) {
		// TODO Auto-generated method stub

	}

	public void delete(String instrument, long timeStamp) {
		// TODO Auto-generated method stub

	}

	public void delete(String instrument, String strDate) {
		// TODO Auto-generated method stub

	}

	public void delete(String instrument, Date date) {
		// TODO Auto-generated method stub

	}
}
