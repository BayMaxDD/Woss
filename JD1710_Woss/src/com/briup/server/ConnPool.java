package com.briup.server;

import java.io.FileInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/** 
* @author Wang Yanyang
* @date   CreateTime:	2017年9月20日 下午4:56:55
* @since 1.8
*/
public class ConnPool{
	private static String driver = null;
	private static String url = null;
	private static String user = null;
	private static String password = null;
	private static int init_size;
	private static int max_size;
	private static int curr_size;
	//存放Connection连接
	List<Connection> conns =new LinkedList<Connection>();
	private static Properties prop = null;
	
	//初始化配置信息
	static{
		prop = new Properties();
		try {
			prop.load(new FileInputStream("src/info.properties"));
			url = prop.getProperty("url");
			driver = prop.getProperty("driver");
			user = prop.getProperty("userName");
			password = prop.getProperty("passWord");
			init_size = Integer.parseInt(prop.getProperty("init_size"));
			max_size = Integer.parseInt(prop.getProperty("max_size"));
			curr_size = Integer.parseInt(prop.getProperty("curr_size"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//构造器,将Connection连接对象放到集合中
	public ConnPool() throws Exception{
		for(int i = 0; i < init_size; i++){
			Connection conn = createConnection();
			curr_size++;
			conns.add(conn);
		}
	}
	
	//创建连接
	private Connection createConnection() throws Exception{
		//注册驱动
		Class.forName(driver);
		//获得连接
		Connection conn =
				DriverManager.getConnection(url, user, password);
		//使用动态代理技术构建连接池中的connection
		//对conn.close()方法进行代理--->判断是否往连接池中添加还是直接关闭conn资源
		Connection proxy = (Connection)Proxy.newProxyInstance(
					conn.getClass().getClassLoader(),
					new Class[] {Connection.class}, 
					new InvocationHandler() {
			
						@Override
						public Object invoke(Object proxy,
									Method method, 
									Object[] args) throws Throwable {
							
							if("close".equals(method.getName())){
								conns.add(conn);
								return null;
							}  
							return method.invoke(conn, args);
						
						}
					});
		return proxy;
	}
	
	//得到连接
	public Connection getConnection() throws Exception{
		//线程池中有已经创建的conn连接对象
		if(conns.size() > 0){
			return conns.remove(0);
		}
		//池中没有了连接对象,但是没有达到创建Connection对象的上限
		if(curr_size < max_size){
			return createConnection();
		}
		//达到上限
		throw new RuntimeException("达到上限");
	}
}
