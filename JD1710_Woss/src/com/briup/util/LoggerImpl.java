package com.briup.util;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class LoggerImpl implements Logger{
	private static org.apache.log4j.Logger logger;
	//日志文件位置
	private String LogFile = null;
	@Override
	public void init(Properties arg0) {
		// TODO Auto-generated method stub
		LogFile = arg0.getProperty("log-properties");
		PropertyConfigurator.configure(LogFile);
		logger = org.apache.log4j.Logger.getRootLogger();
	}
	
	@Override
	public void debug(String arg0) {
		// TODO Auto-generated method stub
		logger.debug("briup-->"+arg0);
	}

	@Override
	public void error(String arg0) {
		// TODO Auto-generated method stub
		logger.error("briup-->"+arg0);
	}

	@Override
	public void fatal(String arg0) {
		// TODO Auto-generated method stub
		logger.fatal("briup-->"+arg0);
	}

	@Override
	public void info(String arg0) {
		// TODO Auto-generated method stub
		logger.info("briup-->"+arg0);
	}

	@Override
	public void warn(String arg0) {
		// TODO Auto-generated method stub
		logger.warn("briup-->"+arg0);
	}
}
