package com.briup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;


public class BackUPImpl implements BackUP{	
	@Override
	public void init(Properties arg0) {
	}
	
	//读取备份文件
	/**
	 * 1.param1:文件路径
	 * 2.param2:是否删除已加载的文件-->true:删除,false:保存
	 */
	@Override
	public Object load(String arg0, boolean arg1) throws Exception {
		//获得ObjectInputStream流
		File file = new File(arg0);
		if(!file.exists()){
			file.createNewFile();
		}
		ObjectInputStream ois =
				new ObjectInputStream(new FileInputStream(file));
		//得到读取的内容
		Object o = ois.readObject();
		ois.close();
		//判断备份文件是否删除
		if(arg1){
			file.delete();
		}
		return o;
	}

	/**
	 * 备份文件:处理产生异常的数据
	 * param1:文件路径
	 * param2:要备份的数据对象
	 * param3:追加还是覆盖     设定true为追加,false为覆盖
	 */
	@Override
	public void store(String arg0, Object arg1, boolean arg2) throws Exception {
		//创建存取的文件
		File file = new File(arg0);
		if(!file.exists()){
			file.createNewFile();
		}
		
		ObjectOutputStream oos = null;
		//arg2-->true,代表追加。false代表覆盖
		if(arg2){
			//获得ObjectOutputStream流
			oos = new ObjectOutputStream(new FileOutputStream(file,true));
		} else {
			oos = new ObjectOutputStream(new FileOutputStream(file));
		}
		//序列化
		oos.writeObject(arg1);
		oos.flush();
		oos.close();
	}
}
