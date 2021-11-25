package com.bgi.rule.impl;

import org.apache.commons.lang3.StringUtils;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class PdRule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result = "报+说明";
	
	private int order=10;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=model.getNote3().contains("del") || model.getNote3().contains("dup");
		boolean flag3=StringUtils.isNotEmpty(model.getDisease())&&model.isRef();
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
