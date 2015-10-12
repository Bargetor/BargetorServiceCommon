package com.bargetor.service.common.jdbc.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bargetor.service.common.jdbc.beans.JDBCConfigBean;

public class DBConnectionPool {
	
	/**
	 * config:���ݿ�����
	 */
	private JDBCConfigBean config;
	
	private List<Connection> freeConnections;
	
	/**
	 * inUsed:����ʹ��������
	 */
	private int inUsed;
	
	public DBConnectionPool(JDBCConfigBean config){
		this.config = config;
		init();
	}
	

	/**
	 *<p>Title: getConnection</p>
	 *<p>Description: ��ȡ����</p>
	 * @param @param timeout
	 * @param @return �趨�ļ�
	 * @return  Connection ��������
	 * @throws
	*/
	public synchronized Connection getConnection(long timeout) {
		Connection con = findOneConnection();
		if (con == null && timeout > 0) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			con = findOneConnection(); // �����������
		}
		if (con != null) {
			this.freeConnections.remove(0);
			this.inUsed++;
		}
		return con;
	}

	/**
	 *<p>Title: getConnection</p>
	 *<p>Description:��ȡ����</p>
	 * @param @return �趨�ļ�
	 * @return  Connection ��������
	 * @throws
	*/
	public synchronized Connection getConnection() {
		return getConnection(0);
	}
	
	/**
	 *<p>Title: freeConnection</p>
	 *<p>Description:�黹����</p>
	 * @param @param con �趨�ļ�
	 * @return  void ��������
	 * @throws
	*/
	public synchronized void freeConnection(Connection con) {
		this.freeConnections.add(con);// ��ӵ��������ӵ�ĩβ
		this.inUsed--;
	}
	
	/**
	 *<p>Title: release</p>
	 *<p>Description:ע�����ͷ���������</p>
	 * @param  �趨�ļ�
	 * @return  void ��������
	 * @throws
	*/
	public synchronized void release() {
		Iterator<Connection> allConns = this.freeConnections.iterator();
		while (allConns.hasNext()) {
			Connection con = (Connection) allConns.next();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.freeConnections.clear();

	}
	
	
	
	/******************************************** private methods *********************************************/

	private synchronized Connection findOneConnection(){
		Connection con = null;
		if (this.freeConnections.size() > 0) {
			con = (Connection) this.freeConnections.get(0);
		}else{
			if (this.config.getMaxConn() == 0 || this.config.getMaxConn() < this.inUsed) {
				con = null;// �ﵽ�������������ʱ���ܻ�������ˡ�
			}else{
				con = addNewConnection();
			}
		}
		return con;
	}
	
	
	/**
	 *<p>Title: init</p>
	 *<p>Description:��ʼ��</p>
	 * @param  �趨�ļ�
	 * @return  void ��������
	 * @throws
	*/
	private void init(){
		this.freeConnections = new ArrayList<Connection>();
		initPoolConnection();
	}
	
	/**
	 *<p>Title: initConnection</p>
	 *<p>Description:��ʼ�����ӳ�����</p>
	 * @param  �趨�ļ�
	 * @return  void ��������
	 * @throws
	*/
	private void initPoolConnection(){
		int count = this.config.getMinConn() - this.freeConnections.size();
		while(count > 0){
			addNewConnection();
			count--;
		}
	}
	
	/**
	 *<p>Title: addNewConnection</p>
	 *<p>Description:����һ��������</p>
	 * @param @return �趨�ļ�
	 * @return  Connection ��������
	 * @throws
	*/
	private Connection addNewConnection(){
		Connection newConnection = buildNewConnection();
		if(newConnection != null){
			this.freeConnections.add(newConnection);
		}
		return newConnection;
	}
	
	/**
	 *<p>Title: buildConnection</p>
	 *<p>Description:��������</p>
	 * @param  �趨�ļ�
	 * @return  void ��������
	 * @throws
	*/
	private Connection buildNewConnection(){
		try {
			Class.forName(config.getDriverName());
			return DriverManager.getConnection(this.config.getUrl(),this.config.getUser(),this.config.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
