package com.forex.dao;

import com.forex.daoFactory.AbstractDaoFactory;

public class HbaseDaoFactory extends AbstractDaoFactory{

	private static HbaseDaoFactory instance;
	
	public static HbaseDaoFactory getInstance(){
		if (instance==null)
				instance=new HbaseDaoFactory();
		return instance;
	}
	
	@Override
	public AbstractForexDao getDao() {
		return new HBaseForexDao();
	}
	
	

}
