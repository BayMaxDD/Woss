package com.briup.util;

import java.util.List;
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

public class ConfigurationImpl_mine implements Configuration{
	//根节点
	private Element root = null;
	//键值对,存放标签以及对应的值
	private Properties prop = null;
	private WossModule woss = null;
	private Map<String, WossModule> map = null;
	
	public ConfigurationImpl_mine() {
		try {
			prop = new Properties();
			//拿到解析器
			SAXReader reader = 
					new SAXReader();
			//读取xml文件,得到Document对象
			Document doc = reader.read("src/conf.xml");
			//获得根节点
			root = doc.getRootElement();
			reEelement(root.elements());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void reEelement(List<Element> elements) throws Exception{
		for (Element el : elements) {
			//获得属性
			if(el.attributes() != null){
				Attribute attr = el.attribute("class");
				String className = attr.getValue();
				//通过权限定名,反射获得对象
				woss = (WossModule) Class.forName(className).
						newInstance();
			}
			//获得标签名,区分对象
			String keyName = el.getName();
			//得到标签的值
			List<Object> es = el.elements();
			if(es != null){
				for (Object obj : es) {
					Element ele= (Element)obj;
					String key = ele.getName();
					String value = ele.getText();
					//往集合中添加数据
					prop.setProperty(key, value);
				}
				//依赖注入
				woss.init(prop);
				if(woss instanceof ConfigurationAWare){
					((ConfigurationAWare) woss).setConfiguration(this);
				}
				map.put(keyName, woss);
			}
			//判断是否有子节点
			if(el.hasContent()){
				reEelement(el.elements());
			}
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
