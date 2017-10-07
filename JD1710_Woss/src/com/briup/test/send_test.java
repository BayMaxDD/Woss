package com.briup.test;

import java.util.Collection;

import com.briup.util.BIDR;
import com.briup.util.ConfigurationImpl;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;

/** 
* @author Wang Yanyang
* @date   CreateTime:	2017年9月20日 上午9:58:38
* @since 1.8
*/
public class send_test {
	public static void main(String[] args) {
		try {
			ConfigurationImpl conf =
					new ConfigurationImpl();
			//获得采集对象
			Gather gather = conf.getGather();
			//数据采集,得到list
			Collection<BIDR> list = gather.gather();
			//获得客户端对象
			Client client = conf.getClient();
			//传输数据
			client.send(list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
