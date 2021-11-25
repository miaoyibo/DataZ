package com.bgi.log;

import javax.swing.JTextArea;

public class MyLog {
	
	private JTextArea jta;
	
	public void info(String info) {
		jta.append(info);
		jta.append("\r\n");
	}
	public void error(String error) {
		jta.append("error:"+error);
		jta.append("\r\n");
	}
	public JTextArea getJta() {
		return jta;
	}
	public void setJta(JTextArea jta) {
		this.jta = jta;
	}
	
}
