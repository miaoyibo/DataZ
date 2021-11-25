package com.bgi.rule.impl;

import com.bgi.model.ReportModel;
import com.bgi.rule.Rule;

public class KlmRule implements Rule {
	public final static String normal = "正常";
	public final static String normal_result = "报+重建库";
	
	private int order=6;
	@Override
	public void filter(ReportModel model) {
		boolean flag1=normal.equalsIgnoreCase(model.getDataType());
		boolean flag2=!(model.getTest_chr21().contains("未检出") && model.getTest_chr18().contains("未检出")
				&& model.getTest_chr13().contains("未检出"));
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
