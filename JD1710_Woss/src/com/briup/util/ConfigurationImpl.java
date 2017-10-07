package com.briup.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.woss.ConfigurationAWare;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;

public class ConfigurationImpl implements Configuration{
	//根节点
	private Element root = null;
	//键值对,存放标签以及对应的值
	private Properties prop = null;
	//conf.xml文件
	private File file = null;
	//存放所有的信息
	private Map<String,WossModule> map = null;
	
	public ConfigurationImpl(){
		file = new File("src/conf.xml");
		init();
	}
	private void init() {
		try {
			//初始化
			prop = new Properties();
			map = new HashMap<String,WossModule>();
			//拿到解析器
			SAXReader reader = 
					new SAXReader();
			//读取xml文件,得到Document对象,得到整个树
			Document doc = reader.read(file);
			//获得根节点
			root = doc.getRootElement();
			//遍历根元素
			for(Object obj:root.elements()){
				Element e2 = (Element)obj;
				/*
				 *	二级标签  class = com.briup.Woss.xxx
				 *	三级标签 配置的属性 
				 */
				Attribute attr = e2.attribute("class");
				String className = attr.getValue();
				WossModule woss = (WossModule)Class.forName(className).newInstance();
				//区分对象
				String keyName = e2.getName();
				//解析三级标签
				for(Object obj1:e2.elements()){
					Element e3 = (Element)obj1;
					String initName = e3.getName();
					String initValue = e3.getText();
					prop.setProperty(initName, initValue);
				}
				//依赖注入
				woss.init(prop);
				if(woss instanceof ConfigurationAWare){
					((ConfigurationAWare) woss).setConfiguration(this);
				}
				map.put(keyName, woss);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public BackUP getBackup() throws Exception {
		// TODO Auto-generated method stub
		//return new BackUPImpl();
		return (BackUP) map.get("backup");
	}

	@Override
	public Client getClient() throws Exception {
		// TODO Auto-generated method stub		
		return (Client) map.get("client");
	}
	
	@Override
	public DBStore getDBStore() throws Exception {
		// TODO Auto-generated method stub
		return (DBStore) map.get("dbstore");	
	}

	@Override
	public Gather getGather() throws Exception {
		// TODO Auto-generated method stub
		return (Gather) map.get("gather");
	}

	@Override
	public Logger getLogger() throws Exception {
		// TODO Auto-generated method stub
		return (Logger) map.get("logger");
	}

	@Override
	public Server getServer() throws Exception {
		// TODO Auto-generated method stub
		return (Server) map.get("server");
	}
}
