package com.bgi.rule.impl;

import org.apache.commons.lang3.StringUtils;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class PRule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result = "报+不说明";
	
	private int order=9;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=model.getNote3().contains("del") || model.getNote3().contains("dup");
		boolean flag3=StringUtils.isEmpty(model.getDisease());
		if(flag1&&flag2&&flag3) {
			model.setResult(normal_result);
		}

	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
