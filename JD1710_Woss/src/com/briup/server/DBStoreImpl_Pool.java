package com.briup.server;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.woss.ConfigurationAWare;
import com.briup.woss.server.DBStore;

public class DBStoreImpl_Pool implements DBStore,ConfigurationAWare{
	private Connection conn;
	private PreparedStatement prep;
	private Configuration conf = null;
	
	//备份文件路径
	private String backupFile = null;
	//批次大小
	private int batchSize;
	@Override
	public void init(Properties arg0) {
		backupFile = arg0.getProperty("backup-file");
		batchSize = Integer.parseInt(arg0.getProperty("batch-size"));
	}
	
	@Override
	public void setConfiguration(Configuration arg0) {
		// TODO Auto-generated method stub
		conf = arg0;
	}

	@Override
	public void saveToDB(Collection<BIDR> arg0) throws Exception {
		//定义flag用于批处理
		int flag = 1;
		//定义存储备份文件的路径,得到文件
		String path = backupFile;
		File file = new File(path);
		//得到备份文件对象
		BackUP backup = conf.getBackup();
		//得到之前未入库的BIDR对象
		Object load = null;
		if(!file.exists()){
			file.createNewFile();
		} else if(file.length() != 0 && file.canRead()){
			load = backup.load(path, false);
		}
		
		//获得连接
		conn = new ConnPool().getConnection();
		
		//构建sql语句
		String sql = "insert into t_detail_" 
					+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
					+ " values(?,?,?,?,?,?)";
		//获得prepareStatement接口
		prep = conn.prepareStatement(sql);

		//处理新的数据
		try {
			if(load != null){
//				List<BIDR> load_1 = (List<BIDR>) load;
//				load_1.addAll(arg0);
				arg0.addAll((Collection<? extends BIDR>) load);
				//System.out.println("arg0-->"+arg0.size());
			}
			for (BIDR bidr : arg0) {
				prep.setString(1,bidr.getAAA_login_name());
				prep.setString(2, bidr.getLogin_ip());
				prep.setTimestamp(3, bidr.getLogin_date());
				prep.setTimestamp(4, bidr.getLogout_date());
				prep.setString(5, bidr.getNAS_ip());
				prep.setInt(6, bidr.getTime_deration()/1000);
//				prep.execute();
				//这个是为了模拟如果出现异常情况时,调用备份文件
				if(flag == 11223){
					conf.getLogger().error("模拟数据库传输数据存在异常");
					throw new RuntimeException("出错啦");
				}
				prep.addBatch();
				if(flag % batchSize == 0){
					prep.executeBatch();
					prep.clearBatch();
				}
				flag++;
			}
			prep.executeBatch();
			prep.clearBatch();
		} catch (Exception e) {
			ArrayList<BIDR> list = (ArrayList<BIDR>) arg0;
			//LinkedList<BIDR> list2 = new LinkedList<BIDR>();
			//list2.addAll(list);
			
			//如果在11223条数据出现异常,找到已经执行的37*300=11100条数据,
			//将其移除,然后把剩余数据存储
			int num = (flag - 1) / batchSize;
			for(int i = 0; i < num * batchSize; i++){
				list.remove(0);
			}
			//System.out.println("-->"+list.size());
			backup.store(path, list, false);
		} finally{
			//关闭资源
			prep.close();
			conn.close();
		}
	}
}
