package com.briup.test;

import java.util.Collection;

import com.briup.util.BIDR;
import com.briup.util.ConfigurationImpl;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

/** 
* @author Wang Yanyang
* @date   CreateTime:	2017年9月20日 上午9:58:23
* @since 1.8
*/
public class revicer_test {
	public static void main(String[] args) {
		try {
			ConfigurationImpl conf =
					new ConfigurationImpl();
			//获得服务端对象
			Server server = conf.getServer();
			//接受数据
			Collection<BIDR> list = server.revicer();
			System.out.println("服务端接收到的数据长度:"+list.size());
			server.shutdown();
			//得到DBStoreImpl对象
			DBStore db = conf.getDBStore();
			db.saveToDB(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
