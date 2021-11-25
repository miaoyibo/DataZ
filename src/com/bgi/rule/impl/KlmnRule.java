package com.bgi.rule.impl;

import org.apache.commons.lang3.StringUtils;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class KlmnRule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result = "报+不说明";
	
	private int order=12;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=model.getTest_chr21().contains("未检出") && model.getTest_chr18().contains("未检出")
				&& model.getTest_chr13().contains("未检出") && model.getTest_sex().contains("未检出")
				&& (StringUtils.isNotEmpty(model.getNote2()) || StringUtils.isNotEmpty(model.getNote1())
				|| StringUtils.isNotEmpty(model.getTest_common()) || StringUtils.isNotEmpty(model.getTest_area()))
				&& StringUtils.isEmpty(model.getNote3());
		if(flag1&&flag2) {
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
