package com.briup.client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Client;

public class ClientImpl implements Client,ConfigurationAWare{
	private String ip = null;
	private int port;
	private Configuration conf = null;
	
	@Override
	public void init(Properties arg0) {
		// TODO Auto-generated method stub
		ip = arg0.getProperty("ip");
		port = Integer.parseInt(arg0.getProperty("port"));
	}
	@Override
	public void setConfiguration(Configuration arg0) {
		// TODO Auto-generated method stub
		conf = arg0;
	}
	
	//发送数据,list<BIDR>
	@Override
	public void send(Collection<BIDR> arg0) throws Exception {
		// TODO Auto-generated method stub
		//1.new Socket
		Socket soc = new Socket(ip, port);
		conf.getLogger().info("与服务器连接成功");
		//2.通过socket获取流
		ObjectOutputStream oos =
				new ObjectOutputStream(soc.getOutputStream());
		//3.通过流去序列化arg0
		oos.writeObject(arg0);
		oos.flush();
		
		//4.关闭资源
		if(oos != null)
			oos.close();
		if(soc != null)
			soc.close();
	}
}
