package com.forex.dao;



import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.forex.model.ForexData;
public class ForexDao {
	
	private static final byte[] TABLE_NAME = Bytes.toBytes("forex");
	private static final byte[] COLUMN_FAMILY = Bytes.toBytes("price");
	private static final byte[] COLUMN_NAME_BUY = Bytes.toBytes("buy");
	private static final byte[] COLUMN_NAME_SELL = Bytes.toBytes("sell");
	private static Logger logger = LoggerFactory.getLogger(ForexDao.class);
	private static final String INSTRUMENT_DATE_SEPARATOR = ";";

	private HTable table;

	private Put mkPut(ForexData data) {
		Put put = new Put(Bytes.toBytes(data.getInstrument()
				+ INSTRUMENT_DATE_SEPARATOR + data.getTimeAsDate()));
		put.add(COLUMN_FAMILY, COLUMN_NAME_BUY, data.getTimeStamp(),
				Bytes.toBytes(data.getBuyPrice()));
		put.add(COLUMN_FAMILY, COLUMN_NAME_SELL, data.getTimeStamp(),
				Bytes.toBytes(data.getSellPrice()));
		return put;
	}

	private Get mkGet(String instrument, long timeStamp) {
		String strDate = ForexData.parseLongAsDate(timeStamp);
		Get get = new Get(Bytes.toBytes(instrument + INSTRUMENT_DATE_SEPARATOR
				+ strDate));
		try {
			get.addFamily(COLUMN_FAMILY);
			get.setTimeStamp(timeStamp);
			get.setMaxVersions(Integer.MAX_VALUE);
		} catch (IOException e) {
			logger.error("Exception on querying data " + e.getMessage());
			e.printStackTrace();
		}
		return get;
	}

	private Delete mkDelete(String instrument, long timeStamp) {
		String strDate = ForexData.parseLongAsDate(timeStamp);
		Delete delete = new Delete(Bytes.toBytes(instrument
				+ ForexDao.INSTRUMENT_DATE_SEPARATOR + strDate));
		return delete;
	}

	private Scan mkScanWithTimestampFilter(String instrument,long timeStampBegin,
			long timeStampEnd) {
		Scan scan = new Scan();
		List<Long> timeStamps = new LinkedList<Long>();
		for (long id = timeStampBegin; id <= timeStampEnd; id++) {
			timeStamps.add(id);
		}
		
		FilterList filterList=new FilterList();
		filterList.addFilter(new TimestampsFilter(timeStamps));
		filterList.addFilter(new PrefixFilter(Bytes.toBytes(instrument)));
		scan.setFilter(filterList);
		scan.setMaxVersions(Integer.MAX_VALUE);
		return scan;
	}

	private List<ForexData> readFromResultScanner(ResultScanner rs) {
		Map<Long, ForexData> mapData = new HashMap<Long, ForexData>();
		for (Result result : rs) {
			List<Cell> cells = result.listCells();

			for (Cell curCell : cells) {
				ForexData data = null;
				if (mapData.containsKey(curCell.getTimestamp())) {
					data = mapData.get(curCell.getTimestamp());
				} else {
					data = new ForexData();
					String strKey[] = Bytes.toString(result.getRow()).split(
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

		}

		List<ForexData> dataList = new LinkedList<ForexData>();
		dataList.addAll(mapData.values());
		System.out.println(dataList.size());
		return dataList;
	}

	public void deleteInstrument(String instrument, Date date) {
		if (date != null && instrument != null) {
			deleteInstrument(instrument, date.getTime());
		}
	}

	public void deleteInstrument(String instrument, long timeStamp) {
		Delete del = this.mkDelete(instrument, timeStamp);
		try {
			table.delete(del);
		} catch (IOException e) {
			logger.error("Exception when deleting " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void deleteInstrument(String instrument, String strDate) {
		if (instrument != null && strDate != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
			try {
				deleteInstrument(instrument, sdf.parse(strDate));
			} catch (ParseException e) {
				logger.error("ERROR When parsing date " + strDate);
				e.printStackTrace();
			}
		}
	}

	public List<ForexData> scanByInstrumentWithinTimeRange(String instrument,
			long timeStampBegin, long timeStampEnd) {
		List<ForexData> forexData = null;
		if (instrument != null && (!instrument.isEmpty())) {
			Scan scan = this.mkScanWithTimestampFilter(instrument, timeStampBegin, timeStampEnd);
			try {
				ResultScanner rs = table.getScanner(scan);
				forexData = this.readFromResultScanner(rs);
			} catch (IOException e) {
				logger.error("ERROR When scanning data " + e.getMessage());
			}
			
		}
		return forexData;
	}

	public List<ForexData> scanByInstrument(String instrument) {
		List<ForexData> forexData = null;

		if (instrument != null && (!instrument.isEmpty())) {
			Scan scan = new Scan();
			PrefixFilter filter = new PrefixFilter(Bytes.toBytes(instrument));
			scan = scan.setMaxVersions(Integer.MAX_VALUE);
			scan.setFilter(filter);
			try {
				ResultScanner rs = table.getScanner(scan);
				forexData = this.readFromResultScanner(rs);
			} catch (IOException e) {
				logger.error("ERROR When scanning data " + e.getMessage());
			}
		}
		return forexData;
	}

	public void storeData(ForexData data) {
		Put put = mkPut(data);
		try {
			table.put(put);
		} catch (RetriesExhaustedWithDetailsException e) {
			logger.error("Exception on storing data " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedIOException e) {
			logger.error("Exception on storing data " + e.getMessage());
			e.printStackTrace();
		}

	}

	public ForexData getData(String instrument, long timeStamp) {
		ForexData _result = null;
		Get get = mkGet(instrument, timeStamp);
		if (get != null) {
			try {
				Result res = table.get(get);
				_result = new ForexData();
				String strKey[] = Bytes.toString(res.getRow()).split(
						INSTRUMENT_DATE_SEPARATOR);
				_result.setInstrument(strKey[0]);
				_result.setTimeStamp(ForexData.parseStringTimeToLong(
						"dd:MM:yyyy", strKey[1]));
				_result.setBuyPrice(Bytes.toDouble(res.getValue(COLUMN_FAMILY,
						COLUMN_NAME_BUY)));
				_result.setSellPrice(Bytes.toDouble(res.getValue(COLUMN_FAMILY,
						COLUMN_NAME_SELL)));
			} catch (IOException e) {
				logger.error("Exception when executing get " + e.getMessage());
				e.printStackTrace();
			}
		}
		return _result;
	}

	public void init() {
		Configuration conf = HBaseConfiguration.create();

		try {
			table = new HTable(conf, TABLE_NAME);
		} catch (IOException e) {
			logger.error("Error connection to table = " + e.getMessage());
		}

	}

	public void close() {
		if (table != null) {
			try {
				table.close();
			} catch (IOException e) {
				logger.error("Error closing the connection " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	

}
