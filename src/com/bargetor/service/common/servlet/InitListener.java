package com.bargetor.service.common.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bargetor.service.common.config.AllCommonConfigsLoader;
import com.bargetor.service.common.jdbc.connection.DBConnectionManager;

/**
 * <p>description: ��ʼ�������������ڹ���ģ��ĳ�ʼ��</p>
 * <p>Date: 2013-9-23 ����07:11:20</p>
 * <p>modify��</p>
 * @author: Madgin
 * @version: 1.0
 */
public class InitListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		AllCommonConfigsLoader.getInstance();
		ServletProcessorManager.getInstance();
		DBConnectionManager.getInstance();
	}

}
