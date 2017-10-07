package com.briup.test;

import java.util.Collection;

import com.briup.util.BIDR;
import com.briup.util.ConfigurationImpl;
import com.briup.woss.client.Gather;

/** 
* @author Wang Yanyang
* @date   CreateTime:	2017年9月19日 下午4:30:24
* @since 1.8
*/
public class gather_test {
	public static void main(String[] args) throws Exception {
		Gather gi = new ConfigurationImpl().getGather();
		Collection<BIDR> gather = null;
		try {
			gather = gi.gather();
			System.out.println(gather.size());
//			for (BIDR bidr : gather) {
//				System.out.println(bidr.getAAA_login_name()+","+
//						bidr.getLogin_ip()+","+bidr.getNAS_ip()+","
//						+bidr.getTime_deration()+","
//						+bidr.getLogin_date()+","+bidr.getLogout_date());
//				System.out.println("---------------");
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
