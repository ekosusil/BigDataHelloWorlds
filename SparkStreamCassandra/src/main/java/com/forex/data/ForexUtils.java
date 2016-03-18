package com.forex.data;

import javax.annotation.Resource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Component;


import org.springframework.data.hadoop.hbase.HbaseTemplate;
import com.forex.repository.ForexRepository;

@Component
public class ForexUtils implements InitializingBean{
	@Resource(name = "hbaseConfiguration")
	private Configuration config;

	@Autowired
	private HbaseTemplate hbaseTemplate;
	
	
	@Autowired
	private ForexRepository forexRepository;
	
	private HBaseAdmin admin;
	
	public void afterPropertiesSet() throws Exception {
		this.admin=new HBaseAdmin(config);
	}

}
