package com.bgi.rule.impl;

import org.apache.commons.lang3.StringUtils;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class KlmoRule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result= "报+重建库+提示";
	
	private int order=7;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=!(model.getTest_chr21().contains("未检出") && model.getTest_chr18().contains("未检出")
				&& model.getTest_chr13().contains("未检出"));
		boolean flag3=StringUtils.isNotEmpty(model.getNote2());
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
