package com.briup.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.woss.server.DBStore;

public class DBStoreImpl implements DBStore{
	private Connection conn;
	private PreparedStatement prep;
	
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String url = "jdbc:oracle:thin:@localhost:1521:XE";
	static String user = "briup";
	static String password = "briup";

	@Override
	public void init(Properties arg0) {
		
	}

	@Override
	public void saveToDB(Collection<BIDR> arg0) throws Exception {
		//定义flag用于批处理
		int flag = 1;
		//注册驱动
		Class.forName(driver);
		//获得连接
		conn = 
			DriverManager.getConnection(url,user,password);
		//关闭自动提交
		//conn.setAutoCommit(false);
		
		//构建sql语句
		String sql = "insert into t_detail_" 
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					+ " values(?,?,?,?,?,?)";
		//获得prepareStatement接口
		prep = conn.prepareStatement(sql);
		
		//处理数据
		for (BIDR bidr : arg0) {
			prep.setString(1,bidr.getAAA_login_name());
			prep.setString(2, bidr.getLogin_ip());
			prep.setTimestamp(3, bidr.getLogin_date());
			prep.setTimestamp(4, bidr.getLogout_date());
			prep.setString(5, bidr.getNAS_ip());
			prep.setInt(6, bidr.getTime_deration()/1000/3600);
//			prep.execute();
			prep.addBatch();
			if(flag % 100 == 0){
				prep.executeBatch();
				prep.clearBatch();
			}
			flag++;
		}
		prep.executeBatch();
		prep.clearBatch();
		
		//关闭资源
		prep.close();
		conn.close();
	}
}
