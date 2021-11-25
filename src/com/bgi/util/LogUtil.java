package com.bgi.util;

import javax.swing.JTextArea;

import com.bgi.log.MyLog;

public class LogUtil {

	private static MyLog log=new MyLog();


	public static MyLog getLog() {

		return log;
	}
	
	public static void setLog(JTextArea jta) {
		log.setJta(jta);
	}
}
