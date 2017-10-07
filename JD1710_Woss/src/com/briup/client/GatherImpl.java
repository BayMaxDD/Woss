package com.briup.client;

import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.client.Gather;


public class GatherImpl implements Gather,ConfigurationAWare{
	//日志文件
	private String dailyFile = null;
	//位置文件
	private String posFile = null;
	//备份文件
	private String backUpFile = null;
	private Configuration conf = null;
	
	@Override
	public void init(Properties arg0) {
		dailyFile = arg0.getProperty("src-file");
		posFile = arg0.getProperty("pos-file");
		backUpFile = arg0.getProperty("backup-file");
	}
	
	@Override
	public void setConfiguration(Configuration arg0) {
		conf = arg0;
	}
	
	//数据采集,解析文件,得到BIDR对象集合
	//读取日志文件
	@Override
	public Collection<BIDR> gather() throws Exception {
		//读取radwtmp文件
		RandomAccessFile raf =
				new RandomAccessFile(dailyFile, "r");
		
		//保存位置的文件
				File position = new File(posFile);
		//设置开始读取radwtmp的位置
		RandomAccessFile pos = 
				new RandomAccessFile(position, "rw");
		
		//备份文件
		File file = new File(backUpFile);
		//读取之前备份的用户信息,使用ObjectInputStream
		//ObjectInputStream ois = null;
		//存在未下线的用户,将所有信息存放到文件中,使用ObjectOutputStream
		//ObjectOutputStream oos = null;
		
		//临时保存BIDR信息,用于索引
		HashMap<String, BIDR> map = 
				new HashMap<String,BIDR>();
		//保存要返回的BIDR信息,终极信息
		List<BIDR> list = new ArrayList<BIDR>();
		
		//-----开始执行------
		//加载之前未匹配的内容,今日要跳过的内容
//		ois =new ObjectInputStream(
//						new FileInputStream(file));
		if(!file.exists()){
			file.createNewFile();
		} else if(file.length() != 0 && file.canRead()){
//			Object o = ois.readObject();
//			map = (HashMap<String, BIDR>)o;
//			ois.close();
			map = (HashMap<String, BIDR>) conf.getBackup().
					load(backUpFile, false);
		}
		
//		if(!position.exists()){
//			position.createNewFile();
//		} else if(position.length() != 0 && position.canRead()){
//			raf.seek(pos.readLong());
//		}
		
		String readline = null;
		while((readline = raf.readLine()) != null){
			//文件已utf-8形式存储,而该流以iso-8859-1读取,需要转为utf-8
			readline = 
					new String(
							readline.getBytes(
									"ISO-8859-1"),"UTF-8");
			//切分字符串
			String[] split =
					readline.split("[|]");
			
			//如果长度为5-->有效长度
			if(split.length == 5){
				if("7".equals(split[2])){
					//封装对象
					BIDR bidr = new BIDR();
					//设置登录名
					bidr.setAAA_login_name(
							split[0].substring(
									1, split[0].length()));
					//设置服务器ip
					bidr.setNAS_ip(split[1]);
					//设置登录日期
					bidr.setLogin_date(
							new Timestamp(
									Long.parseLong(split[3]+"000")));
					//设置登陆ip
					bidr.setLogin_ip(split[4]);
					
					//将对象临时放到Map中
					map.put(split[4], bidr);
				}
				if("8".equals(split[2])){
					if(map.containsKey(split[4])){
						//得到对象
						BIDR bidr = map.get(split[4]);
						//设置登出时间
						bidr.setLogout_date(
								new Timestamp(
										Long.parseLong(split[3]+"000")));
						//设置在线时长
						int allTime = 
								(int)(bidr.getLogout_date().
										getTime() - bidr.getLogin_date().
											getTime());
						bidr.setTime_deration(allTime);
						
						//将最终的BIDR对象放入list中
						list.add(bidr);
						//map中剔除已匹配的信息
						map.remove(split[4]);
					}
				}
			}
		}
		//文件读完,处理没有下线的7--->写到一个文件中
//		oos = new ObjectOutputStream(
//				new FileOutputStream(file));
//		oos.writeObject(map);
//		oos.flush();
//		oos.close();
		conf.getBackup().store(backUpFile, map, false);
//		
		//记录数据读取到哪里
		pos.writeLong(raf.getFilePointer());
		pos.close();
	
		if(raf != null)
			raf.close();
		return list;
	}
}

