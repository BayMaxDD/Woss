package com.briup.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.Server;

public class ServerImpl implements Server,ConfigurationAWare{
	//端口号
	private int port;
	private ServerSocket ss = null;
	private Socket soc = null;
	private Configuration conf = null;
	
	@Override
	public void init(Properties arg0) {
		port = Integer.parseInt(arg0.getProperty("port"));
	}
	
	@Override
	public void setConfiguration(Configuration arg0) {
		conf = arg0;
	}
	
	//接受封装好的对象
	@Override
	public Collection<BIDR> revicer() throws Exception {
		//1.new ServerSocket
		ss = new ServerSocket(port);
		//2.获得连接
		conf.getLogger().info("等待连接");
		soc = ss.accept();
		conf.getLogger().info("与客户端连接成功");
		//3.通过连接,获取流
		ObjectInputStream ois =
				new ObjectInputStream(soc.getInputStream());
		
		//4.反序列化-->list<BIDR>
		Object o = ois.readObject();
		List<BIDR> list = (List<BIDR>)o;
		
		//5.关闭资源
		if(ois != null)
			ois.close();
		//6.返回结果
		return list;
	}

	//关闭服务器
	@Override
	public void shutdown() {
		try {
			if(soc != null)
				soc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(ss != null)
					ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
