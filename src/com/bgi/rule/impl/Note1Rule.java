package com.bgi.rule.impl;
import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class Note1Rule implements Rule {
	public final static String abnormal = "异常";
	public final static String abnormal_result = "退费";
	
	private int order=1;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=abnormal.equalsIgnoreCase(model.getDataType());
		boolean flag2="退费".equals(model.getNote1());
		if (flag1&&flag2) {
			model.setResult(abnormal_result);
		}

	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
