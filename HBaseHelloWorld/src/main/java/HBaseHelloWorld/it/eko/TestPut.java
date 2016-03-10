package HBaseHelloWorld.it.eko;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class TestPut {

//test adding command from host machine. 
  public static void main(String args[]) {
    Configuration conf = HBaseConfiguration.create();
    conf.addResource(new Path("/etc/hbase/conf/core-site.xml"));
    conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
    try {
      HTableInterface table = new HTable(conf, Bytes.toBytes("customer"));
      tryPut(table,"daba","address","city","jakarta");
      tryPut(table,"daba","address","zip","14350");
      System.out.println("Try Read ");
      tryRead(table,"jsmith","address","city");
      System.out.println("Try scan");
      tryScan(table,"address","city");
      System.out.println("Try delete");
      tryDelete(table,"daba");
      System.out.println("Try rescan");
      tryScan(table,"address","city");
      table.close();
    } catch (IOException e) {
      e.printStackTrace();
      String message = e.getMessage();
      System.out.println(message);
    }
  }
  
  public static void tryPut(HTableInterface table,String rowKey,String colFamily, String colName, String value) throws IOException{
	  Put put = null;
      put= new Put(Bytes.toBytes(rowKey));
      put.add(Bytes.toBytes(colFamily), Bytes.toBytes(colName),
          Bytes.toBytes(value));
      table.put(put);
      table.flushCommits();
      
  }
  
  public static void tryRead(HTableInterface table,String key, String family, String colName) throws IOException{
	  Get get=new Get(Bytes.toBytes(key));
	  get.addColumn(Bytes.toBytes(family), Bytes.toBytes(colName));
	  Result result=table.get(get);
	  table.flushCommits();
	  String strZip=Bytes.toString(result.getValue(
			  Bytes.toBytes(family), Bytes.toBytes(colName)));
	  
	  System.out.println(Bytes.toString(result.getRow())+" : "+colName+" = "+strZip);
	  
  }
  
  public static void tryScan(HTableInterface table,String colFamily,String colName) throws IOException{
	  Scan scan=new Scan();
	  byte colFamByte[]=Bytes.toBytes(colFamily);
	  byte colNameByte[]=Bytes.toBytes(colName);
	  scan.addColumn(colFamByte, colNameByte);
	  ResultScanner scanner=table.getScanner(scan);
	  table.flushCommits();
	  
	  for(Result result:scanner){
		 System.out.println(Bytes.toString(result.getRow())+" " +Bytes.toString(result.getValue(colFamByte, colNameByte)));
	  }
  }
  
  public static void tryDelete(HTableInterface table, String rowKey) throws IOException{
	 Delete toDelete = new Delete(Bytes.toBytes(rowKey));
	 table.delete(toDelete);
	 table.flushCommits();
  }
}
